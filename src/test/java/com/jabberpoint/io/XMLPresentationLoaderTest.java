package com.jabberpoint.io;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.jabberpoint.Presentation;
import com.jabberpoint.Slide;
import com.jabberpoint.TextItem;
import com.jabberpoint.BitmapItem;

/**
 * Tests for the XMLPresentationLoader in the new io package
 */
public class XMLPresentationLoaderTest
{

    private XMLPresentationLoader loader;
    private Presentation presentation;
    private static final String TEST_FILE_PATH = "test-presentation.xml";
    private static final String DTD_FILE_PATH = "jabberpoint.dtd";

    @Before
    public void setUp()
    {
        loader = new XMLPresentationLoader(new DOMXMLParsingStrategy());
        presentation = new Presentation();

        // Create the DTD file needed for testing to avoid FileNotFoundException
        createDTDFile();
    }

    @After
    public void tearDown()
    {
        // Clean up test files
        try
        {
            Files.deleteIfExists(Paths.get(TEST_FILE_PATH));
            Files.deleteIfExists(Paths.get(DTD_FILE_PATH));
        } catch (Exception e)
        {
            System.err.println("Error during cleanup: " + e.getMessage());
        }
    }

    /**
     * Creates a simple DTD file for testing to avoid FileNotFoundException
     */
    private void createDTDFile()
    {
        try (PrintWriter out = new PrintWriter(new FileWriter(DTD_FILE_PATH)))
        {
            out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            out.println("<!ELEMENT presentation (showtitle, slide*)>");
            out.println("<!ELEMENT showtitle (#PCDATA)>");
            out.println("<!ELEMENT slide (title, item*)>");
            out.println("<!ELEMENT title (#PCDATA)>");
            out.println("<!ELEMENT item (#PCDATA)>");
            out.println("<!ATTLIST item kind CDATA #REQUIRED>");
            out.println("<!ATTLIST item level CDATA #REQUIRED>");
        } catch (IOException e)
        {
            System.err.println("Error creating DTD file: " + e.getMessage());
        }
    }

    @Test
    public void testLoadAndSavePresentation() throws Exception
    {
        // Create a test presentation
        presentation.setTitle("Test Presentation");

        Slide slide1 = new Slide();
        slide1.setTitle("Test Slide 1");
        slide1.append(new TextItem(1, "Test Text Item"));
        presentation.append(slide1);

        Slide slide2 = new Slide();
        slide2.setTitle("Test Slide 2");
        slide2.append(new TextItem(2, "Another Test Item"));
        slide2.append(new BitmapItem(3, "test-image.jpg"));
        presentation.append(slide2);

        // Save the presentation
        loader.savePresentation(presentation, TEST_FILE_PATH);

        // Verify file was created
        File testFile = new File(TEST_FILE_PATH);
        assertTrue("Test file should exist after saving", testFile.exists());

        // Create a new presentation to load into
        Presentation loadedPresentation = new Presentation();

        // Load the saved presentation
        loader.loadPresentation(loadedPresentation, TEST_FILE_PATH);

        // Verify loaded content
        assertEquals("Presentation title should match",
                presentation.getTitle(), loadedPresentation.getTitle());
        assertEquals("Slide count should match",
                presentation.getSize(), loadedPresentation.getSize());

        // Verify first slide content
        assertEquals("Slide 1 title should match",
                presentation.getSlide(0).getTitle(),
                loadedPresentation.getSlide(0).getTitle());

        // Verify second slide content
        assertEquals("Slide 2 title should match",
                presentation.getSlide(1).getTitle(),
                loadedPresentation.getSlide(1).getTitle());
    }

    @Test(expected = Exception.class)
    public void testLoadNonExistentFile() throws Exception
    {
        // Try to load a non-existent file
        loader.loadPresentation(presentation, "non-existent-file.xml");
    }

    @Test
    public void testGetStrategy()
    {
        // Create loader with explicit strategy
        DOMXMLParsingStrategy strategy = new DOMXMLParsingStrategy();
        XMLPresentationLoader loaderWithStrategy = new XMLPresentationLoader(strategy);

        // Test passes if no exception
        assertNotNull("Loader should be created with explicit strategy", loaderWithStrategy);
    }

    @Test
    public void testDefaultConstructor()
    {
        // Use default constructor
        XMLPresentationLoader defaultLoader = new XMLPresentationLoader();

        // Test passes if no exception
        assertNotNull("Loader should be created with default constructor", defaultLoader);
    }
} 