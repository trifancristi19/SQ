package com.jabberpoint.io;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.jabberpoint.Presentation;
import com.jabberpoint.Slide;
import com.jabberpoint.TextItem;
import com.jabberpoint.BitmapItem;
import com.jabberpoint.DOMXMLParser;

/**
 * Tests for the XMLPresentationLoader in the new io package
 */
public class XMLPresentationLoaderTest {

    private XMLPresentationLoader loader;
    private Presentation presentation;
    private static final String TEST_FILE_PATH = "test-presentation.xml";

    @Before
    public void setUp() {
        loader = new XMLPresentationLoader(new DOMXMLParser());
        presentation = new Presentation();
    }

    @After
    public void tearDown() {
        // Clean up test files
        try {
            Files.deleteIfExists(Paths.get(TEST_FILE_PATH));
            Files.deleteIfExists(Paths.get("jabberpoint.dtd"));
        } catch (Exception e) {
            System.err.println("Error during cleanup: " + e.getMessage());
        }
    }

    @Test
    public void testLoadAndSavePresentation() throws Exception {
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
    public void testLoadNonExistentFile() throws Exception {
        // Try to load a non-existent file
        loader.loadPresentation(presentation, "non-existent-file.xml");
    }

    @Test
    public void testGetParser() {
        // Create loader with explicit parser
        DOMXMLParser parser = new DOMXMLParser();
        XMLPresentationLoader loaderWithParser = new XMLPresentationLoader(parser);
        
        // Test passes if no exception
        assertNotNull("Loader should be created with explicit parser", loaderWithParser);
    }

    @Test
    public void testDefaultConstructor() {
        // Use default constructor
        XMLPresentationLoader defaultLoader = new XMLPresentationLoader();
        
        // Test passes if no exception
        assertNotNull("Loader should be created with default constructor", defaultLoader);
    }
} 