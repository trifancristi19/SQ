package com.jabberpoint.io;

import com.jabberpoint.BitmapItem;
import com.jabberpoint.Presentation;
import com.jabberpoint.Slide;
import com.jabberpoint.TextItem;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;

/**
 * XML implementation of PresentationReader that uses the XMLParsingStrategy
 * This reader can use different parsing strategies interchangeably
 */
public class StrategicXMLPresentationReader implements PresentationReader {

    private final XMLParsingStrategy parsingStrategy;
    
    /**
     * Constructor that uses the default parsing strategy
     */
    public StrategicXMLPresentationReader() {
        this.parsingStrategy = XMLParsingStrategyFactory.getDefaultStrategy();
    }
    
    /**
     * Constructor that uses a specific parsing strategy
     * @param parsingStrategy The parsing strategy to use
     */
    public StrategicXMLPresentationReader(XMLParsingStrategy parsingStrategy) {
        this.parsingStrategy = parsingStrategy;
    }
    
    /**
     * Constructor that uses a specific strategy type
     * @param strategyType The type of strategy to use
     */
    public StrategicXMLPresentationReader(XMLParsingStrategyFactory.StrategyType strategyType) {
        this.parsingStrategy = XMLParsingStrategyFactory.getStrategy(strategyType);
    }
    
    @Override
    public void loadPresentation(Presentation presentation, String fileName) throws Exception {
        File file = new File(fileName);
        
        if (!file.exists()) {
            throw new java.io.FileNotFoundException(fileName);
        }
        
        // Parse the XML file using the strategy
        Document document = parsingStrategy.parseFile(file);
        Element rootElement = parsingStrategy.getRootElement(document);
        
        // Process the presentation
        presentation.setTitle(getTitle(rootElement));
        
        // Process all slides
        NodeList slideNodes = rootElement.getElementsByTagName("slide");
        for (int slideIndex = 0; slideIndex < slideNodes.getLength(); slideIndex++) {
            Element slideElement = (Element) slideNodes.item(slideIndex);
            Slide slide = processSlide(slideElement);
            presentation.append(slide);
        }
    }
    
    private String getTitle(Element rootElement) {
        NodeList titleNodeList = rootElement.getElementsByTagName("title");
        if (titleNodeList.getLength() > 0) {
            // Get the first title element (directly under presentation)
            // Check if this is the presentation title (not a slide title)
            Element titleElement = (Element) titleNodeList.item(0);
            if (titleElement.getParentNode() == rootElement) {
                return titleElement.getTextContent();
            }
        }
        // Return default title if no presentation title found
        return "Unnamed Presentation";
    }
    
    private Slide processSlide(Element slideElement) {
        Slide slide = new Slide();
        
        // Set slide title
        NodeList slideTitleNodes = slideElement.getElementsByTagName("title");
        if (slideTitleNodes.getLength() > 0) {
            slide.setTitle(slideTitleNodes.item(0).getTextContent());
        }
        
        // Create a merged list of all slide items in the proper order
        NodeList childNodes = slideElement.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            if (childNodes.item(i) instanceof Element) {
                Element childElement = (Element) childNodes.item(i);
                String tagName = childElement.getTagName();
                
                // Skip the title element, as it's not a slide item
                if ("title".equals(tagName)) {
                    continue;
                }
                
                // Process based on tag name
                if ("text".equals(tagName)) {
                    int level = Integer.parseInt(childElement.getAttribute("level"));
                    String text = childElement.getTextContent();
                    slide.append(new TextItem(level, text));
                } else if ("image".equals(tagName)) {
                    int level = Integer.parseInt(childElement.getAttribute("level"));
                    String imageName = childElement.getAttribute("name");
                    slide.append(new BitmapItem(level, imageName));
                }
                // Ignore unknown elements
            }
        }
        
        return slide;
    }
    
    // These methods are no longer used since we process all elements in order in processSlide
    private void processTextItems(Element slideElement, Slide slide) {
        // This method is kept for backward compatibility but is no longer used
    }
    
    private void processImageItems(Element slideElement, Slide slide) {
        // This method is kept for backward compatibility but is no longer used
    }
} 