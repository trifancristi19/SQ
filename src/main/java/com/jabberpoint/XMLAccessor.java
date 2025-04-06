package com.jabberpoint;

import java.util.Vector;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import com.jabberpoint.io.XMLParsingStrategy;
import com.jabberpoint.io.DOMXMLParsingStrategy;
import com.jabberpoint.io.XMLParsingStrategyFactory;

/**
 * XMLAccessor, reads and writes XML files
 *
 * @author Ian F. Darwin, ian@darwinsys.com, Gert Florijn, Sylvia Stuurman
 * @version 1.6 2014/05/16 Sylvia Stuurman
 */

public class XMLAccessor extends Accessor
{
    private final XMLParsingStrategy parsingStrategy;

    /**
     * Default API to use.
     */
    protected static final String DEFAULT_API_TO_USE = "dom";

    /**
     * namen van xml tags of attributen
     */
    protected static final String SHOWTITLE = "showtitle";
    protected static final String SLIDETITLE = "title";
    protected static final String SLIDE = "slide";
    protected static final String ITEM = "item";
    protected static final String LEVEL = "level";
    protected static final String KIND = "kind";
    protected static final String TEXT = "text";
    protected static final String IMAGE = "image";

    /**
     * tekst van messages
     */
    protected static final String PCE = "Parser Configuration Exception";
    protected static final String UNKNOWNTYPE = "Unknown Element type";
    protected static final String NFE = "Number Format Exception";

    /**
     * Creates an XMLAccessor with the default XMLParsingStrategy (DOM)
     */
    public XMLAccessor() {
        this(XMLParsingStrategyFactory.getDefaultStrategy());
    }
    
    /**
     * Creates an XMLAccessor with a specific XMLParsingStrategy implementation
     * @param parsingStrategy The XMLParsingStrategy to use
     */
    public XMLAccessor(XMLParsingStrategy parsingStrategy) {
        this.parsingStrategy = parsingStrategy != null ? parsingStrategy : XMLParsingStrategyFactory.getDefaultStrategy();
    }

    private String getTitle(Element element, String tagName)
    {
        NodeList titles = element.getElementsByTagName(tagName);
        if (titles.getLength() == 0)
        {
            return ""; // Return empty string if no title element found
        }
        return titles.item(0).getTextContent();
    }

    public void loadFile(Presentation presentation, String filename) throws IOException
    {
        int slideNumber, itemNumber, max = 0, maxItems = 0;
        try
        {
            // Use the parser from the strategy pattern
            Document document = parsingStrategy.parseFile(new File(filename));
            Element doc = parsingStrategy.getRootElement(document);

            // First, get the title so it doesn't get cleared
            String title = getTitle(doc, SHOWTITLE);

            // Clear the presentation
            presentation.clear();

            // Set the title after clearing
            presentation.setTitle(title);

            NodeList slides = doc.getElementsByTagName(SLIDE);
            max = slides.getLength();
            for (slideNumber = 0; slideNumber < max; slideNumber++)
            {
                Element xmlSlide = (Element) slides.item(slideNumber);
                Slide slide = new Slide();
                slide.setTitle(getTitle(xmlSlide, SLIDETITLE));
                presentation.append(slide);

                NodeList slideItems = xmlSlide.getElementsByTagName(ITEM);
                maxItems = slideItems.getLength();
                for (itemNumber = 0; itemNumber < maxItems; itemNumber++)
                {
                    Element item = (Element) slideItems.item(itemNumber);
                    loadSlideItem(slide, item);
                }
            }
        } catch (IOException iox)
        {
            System.err.println(iox.toString());
            throw iox; // Rethrow to ensure caller knows about the error
        } catch (Exception ex)
        {
            System.err.println(ex.getMessage());
            throw new IOException("Error parsing XML: " + ex.getMessage(), ex);
        }
    }

    protected void loadSlideItem(Slide slide, Element item)
    {
        int level = 1; // default
        NamedNodeMap attributes = item.getAttributes();

        // Check if required attributes exist
        if (attributes.getNamedItem(LEVEL) == null || attributes.getNamedItem(KIND) == null)
        {
            System.err.println("Missing required attributes (level or kind)");
            return; // Skip this item if required attributes are missing
        }

        String leveltext = attributes.getNamedItem(LEVEL).getTextContent();
        if (leveltext != null)
        {
            try
            {
                level = Integer.parseInt(leveltext);
            } catch (NumberFormatException x)
            {
                System.err.println(NFE);
            }
        }
        
        String type = attributes.getNamedItem(KIND).getTextContent();
        String content = item.getTextContent();
        
        try {
            // Use the factory to create the appropriate slide item
            SlideItem slideItem = SlideItemFactory.createSlideItem(type, level, content);
            slide.append(slideItem);
        } catch (IllegalArgumentException e) {
            System.err.println(UNKNOWNTYPE + ": " + type);
        }
    }

    private void createDTDFile(String path) throws IOException
    {
        String dtdContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
                           "<!ELEMENT presentation (showtitle, slide*)>\n" + 
                           "<!ELEMENT showtitle (#PCDATA)>\n" + 
                           "<!ELEMENT slide (title, item*)>\n" + 
                           "<!ELEMENT title (#PCDATA)>\n" + 
                           "<!ELEMENT item (#PCDATA)>\n" + 
                           "<!ATTLIST item kind CDATA #REQUIRED>\n" + 
                           "<!ATTLIST item level CDATA #REQUIRED>";

        PrintWriter out = new PrintWriter(new FileWriter(path));
        out.print(dtdContent);
        out.close();
        System.out.println("Created DTD file at: " + path);
    }

    public void saveFile(Presentation presentation, String filename) throws IOException
    {
        // Create the parent directory first
        File file = new File(filename);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists())
        {
            boolean dirCreated = parentDir.mkdirs();
            if (!dirCreated)
            {
                System.err.println("Failed to create directory: " + parentDir.getAbsolutePath());
                // Check if parent directory exists now, despite failed mkdirs() call
                if (!parentDir.exists())
                {
                    throw new IOException("Failed to create directory: " + parentDir.getAbsolutePath());
                }
                else
                {
                    System.out.println("Directory exists despite mkdirs() returning false: " + parentDir.getAbsolutePath());
                }
            }
            else
            {
                System.out.println("Successfully created directory: " + parentDir.getAbsolutePath());
            }
        }

        try
        {
            // Note: For XML saving, we bypass the parsingStrategy and write directly to the file
            // This is because the XMLParsingStrategy is primarily for parsing, not for creating
            // XML structures. We could extend XMLParsingStrategy to include serialization methods in the future.
            PrintWriter out = new PrintWriter(new FileWriter(filename));
            out.println("<?xml version=\"1.0\"?>");

            // Get the directory path from the filename
            String directory = file.getParent();

            // Check if jabberpoint.dtd exists in the target directory, if not, copy it
            if (directory != null)
            {
                File dtdFile = new File(directory, "jabberpoint.dtd");
                if (!dtdFile.exists())
                {
                    // Create the DTD file in the same directory
                    createDTDFile(dtdFile.getPath());
                }
            }

            out.println("<!DOCTYPE presentation SYSTEM \"jabberpoint.dtd\">");
            out.println("<presentation>");
            out.print("<showtitle>");
            out.print(presentation.getTitle() == null ? "" : presentation.getTitle());
            out.println("</showtitle>");
            for (int slideNumber = 0; slideNumber < presentation.getSize(); slideNumber++)
            {
                Slide slide = presentation.getSlide(slideNumber);
                if (slide == null) continue;

                out.println("<slide>");
                out.println("<title>" + (slide.getTitle() == null ? "" : slide.getTitle()) + "</title>");
                Vector<SlideItem> slideItems = slide.getSlideItems();
                for (int itemNumber = 0; itemNumber < slideItems.size(); itemNumber++)
                {
                    SlideItem slideItem = slideItems.elementAt(itemNumber);
                    if (slideItem == null) continue;

                    out.print("<item kind=");
                    if (slideItem instanceof TextItem)
                    {
                        out.print("\"text\" level=\"" + slideItem.getLevel() + "\">");
                        out.print(((TextItem) slideItem).getText() == null ? "" : ((TextItem) slideItem).getText());
                    }
                    else
                    {
                        if (slideItem instanceof BitmapItem)
                        {
                            out.print("\"image\" level=\"" + slideItem.getLevel() + "\">");
                            out.print(((BitmapItem) slideItem).getName() == null ? "" : ((BitmapItem) slideItem).getName());
                        }
                        else
                        {
                            System.out.println("Ignoring " + slideItem);
                            continue;
                        }
                    }
                    out.println("</item>");
                }
                out.println("</slide>");
            }
            out.println("</presentation>");
            out.close();
        } catch (IOException e)
        {
            System.err.println(e.toString());
            throw e;
        }
    }
}
