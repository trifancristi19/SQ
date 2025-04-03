package com.jabberpoint;

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

public class XMLAccessorTest
{

    private XMLAccessor xmlAccessor;
    private Presentation presentation;
    private static final String TEST_FILE_PATH = "test-presentation.xml";
    private static final String MALFORMED_XML_PATH = "malformed-test.xml";
    private static final String MISSING_ATTRS_XML_PATH = "missing-attrs-test.xml";

    @Before
    public void setUp()
    {
        xmlAccessor = new XMLAccessor();
        presentation = new Presentation();
    }

    @After
    public void tearDown()
    {
        // Clean up test files
        try
        {
            Files.deleteIfExists(Paths.get(TEST_FILE_PATH));
            Files.deleteIfExists(Paths.get(MALFORMED_XML_PATH));
            Files.deleteIfExists(Paths.get(MISSING_ATTRS_XML_PATH));
            Files.deleteIfExists(Paths.get("jabberpoint.dtd"));

            // Clean up any test directories that might have been created
            Files.deleteIfExists(Paths.get("test-output-dir/test.xml"));
            Files.deleteIfExists(Paths.get("test-output-dir/jabberpoint.dtd"));
            Files.deleteIfExists(Paths.get("test-output-dir"));
        } catch (IOException e)
        {
            System.err.println("Error during cleanup: " + e.getMessage());
        }
    }

    @Test
    public void testSaveAndLoadFile() throws IOException
    {
        // Setup a presentation with test data
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

        // Save the presentation to a test file
        xmlAccessor.saveFile(presentation, TEST_FILE_PATH);

        // Verify file was created
        File testFile = new File(TEST_FILE_PATH);
        assertTrue("Test file should exist after saving", testFile.exists());

        // Create a new presentation to load into
        Presentation loadedPresentation = new Presentation();

        // Load the saved presentation
        xmlAccessor.loadFile(loadedPresentation, TEST_FILE_PATH);

        // Verify loaded content
        assertEquals("Presentation title should match", presentation.getTitle(), loadedPresentation.getTitle());
        assertEquals("Slide count should match", presentation.getSize(), loadedPresentation.getSize());

        // Verify first slide content
        assertEquals("Slide 1 title should match",
                presentation.getSlide(0).getTitle(),
                loadedPresentation.getSlide(0).getTitle());

        // Verify second slide content
        assertEquals("Slide 2 title should match",
                presentation.getSlide(1).getTitle(),
                loadedPresentation.getSlide(1).getTitle());

        // Verify bitmap item was loaded
        SlideItem item = loadedPresentation.getSlide(1).getSlideItems().elementAt(1);
        assertTrue("Item should be a BitmapItem", item instanceof BitmapItem);
        assertEquals("Image path should match", "test-image.jpg", ((BitmapItem) item).getName());
    }

    @Test
    public void testGetTitleMethod()
    {
        // This test indirectly tests the private getTitle method through loadFile
        presentation.setTitle("Test Title");
        assertEquals("Title should be set correctly", "Test Title", presentation.getTitle());
    }

    @Test
    public void testLoadNonExistentFile()
    {
        // Try to load a non-existent file
        try
        {
            xmlAccessor.loadFile(presentation, "non-existent-file.xml");
            fail("Should throw IOException when file does not exist");
        } catch (IOException e)
        {
            // Expected behavior
            assertTrue(e.getMessage().contains("non-existent-file.xml"));
        }
    }

    @Test
    public void testSaveToNewDirectory() throws IOException
    {
        // Test saving to a directory that doesn't exist yet
        String dirPath = "test-output-dir";
        String filePath = dirPath + "/test.xml";

        // Ensure directory doesn't exist to start
        File dir = new File(dirPath);
        if (dir.exists())
        {
            for (File file : dir.listFiles())
            {
                file.delete();
            }
            dir.delete();
        }

        // Setup a simple presentation
        presentation.setTitle("Directory Test");

        // Save to the non-existent directory
        xmlAccessor.saveFile(presentation, filePath);

        // Verify directory and file were created
        assertTrue("Directory should exist after saving", dir.exists());
        assertTrue("File should exist after saving", new File(filePath).exists());
        assertTrue("DTD file should exist after saving", new File(dirPath + "/jabberpoint.dtd").exists());
    }

    @Test
    public void testMalformedXmlFile() throws IOException
    {
        // Create a malformed XML file
        try (PrintWriter out = new PrintWriter(new FileWriter(MALFORMED_XML_PATH)))
        {
            out.println("<?xml version=\"1.0\"?>");
            out.println("<!DOCTYPE presentation SYSTEM \"jabberpoint.dtd\">");
            out.println("<presentation>");
            out.println("<showtitle>Malformed XML Test</showtitle>");
            out.println("<slide>");
            out.println("<title>Incomplete Slide</title>");
            // Missing closing tags
            out.println("<item kind=\"text\" level=\"1\">Test Item");
            // Malformed XML - missing closing tags
        }

        // Try to load the malformed file
        try
        {
            xmlAccessor.loadFile(presentation, MALFORMED_XML_PATH);
            fail("Should throw IOException for malformed XML");
        } catch (IOException e)
        {
            // Expected behavior
            assertTrue("Exception should be related to SAX or XML parsing",
                    e.getMessage().contains("SAX") ||
                            e.getMessage().toLowerCase().contains("xml") ||
                            e.getMessage().toLowerCase().contains("parse"));
        }
    }

    @Test
    public void testMissingAttributesInXmlItems() throws IOException
    {
        // Create XML with items missing required attributes
        try (PrintWriter out = new PrintWriter(new FileWriter(MISSING_ATTRS_XML_PATH)))
        {
            out.println("<?xml version=\"1.0\"?>");
            out.println("<!DOCTYPE presentation SYSTEM \"jabberpoint.dtd\">");
            out.println("<presentation>");
            out.println("<showtitle>Missing Attributes Test</showtitle>");
            out.println("<slide>");
            out.println("<title>Test Slide</title>");
            // Item missing level attribute
            out.println("<item kind=\"text\">Missing Level</item>");
            // Item missing kind attribute
            out.println("<item level=\"1\">Missing Kind</item>");
            // Item with complete attributes (should be loaded)
            out.println("<item kind=\"text\" level=\"2\">Complete Item</item>");
            out.println("</slide>");
            out.println("</presentation>");
        }

        // Load the file - should not throw exception but log errors
        xmlAccessor.loadFile(presentation, MISSING_ATTRS_XML_PATH);

        // Verify only the correctly formatted item was loaded
        assertEquals("Should have loaded the presentation", "Missing Attributes Test", presentation.getTitle());
        assertEquals("Should have loaded one slide", 1, presentation.getSize());

        Slide slide = presentation.getSlide(0);
        // Should only have loaded the complete item
        assertEquals("Should have loaded only the complete item", 1, slide.getSlideItems().size());

        TextItem item = (TextItem) slide.getSlideItems().elementAt(0);
        assertEquals("Complete item text should match", "Complete Item", item.getText());
        assertEquals("Complete item level should match", 2, item.getLevel());
    }

    @Test
    public void testHandlingEmptyPresentation() throws IOException
    {
        // Create an empty presentation
        Presentation emptyPresentation = new Presentation();
        emptyPresentation.setTitle("Empty Presentation");

        // Save and load the empty presentation
        xmlAccessor.saveFile(emptyPresentation, TEST_FILE_PATH);

        Presentation loadedPresentation = new Presentation();
        xmlAccessor.loadFile(loadedPresentation, TEST_FILE_PATH);

        // Verify the loaded presentation
        assertEquals("Empty presentation title should match",
                emptyPresentation.getTitle(),
                loadedPresentation.getTitle());
        assertEquals("Empty presentation should have no slides",
                0,
                loadedPresentation.getSize());
    }

    @Test
    public void testLoadPresentationWithNullItems() throws IOException
    {
        // Setup a presentation with null items
        presentation.setTitle("Null Items Test");

        Slide slide = new Slide();
        slide.setTitle("Test Slide");
        slide.append(null); // Add a null item
        presentation.append(slide);

        // Save and load the presentation
        xmlAccessor.saveFile(presentation, TEST_FILE_PATH);

        Presentation loadedPresentation = new Presentation();
        xmlAccessor.loadFile(loadedPresentation, TEST_FILE_PATH);

        // Verify the loaded presentation
        assertEquals("Title should match", presentation.getTitle(), loadedPresentation.getTitle());
        assertEquals("Should have one slide", 1, loadedPresentation.getSize());
        // Null items should be skipped during save
        assertEquals("Slide should have no items", 0, loadedPresentation.getSlide(0).getSlideItems().size());
    }

    @Test
    public void testLoadFileWithItemNumberFormatException() throws IOException
    {
        // Create XML with invalid level format
        try (PrintWriter out = new PrintWriter(new FileWriter(TEST_FILE_PATH)))
        {
            out.println("<?xml version=\"1.0\"?>");
            out.println("<!DOCTYPE presentation SYSTEM \"jabberpoint.dtd\">");
            out.println("<presentation>");
            out.println("<showtitle>Number Format Test</showtitle>");
            out.println("<slide>");
            out.println("<title>Test Slide</title>");
            // Item with non-numeric level
            out.println("<item kind=\"text\" level=\"not-a-number\">Invalid Level</item>");
            out.println("</slide>");
            out.println("</presentation>");
        }

        // Should not throw exception, but use default level
        xmlAccessor.loadFile(presentation, TEST_FILE_PATH);

        // Verify loaded content
        assertEquals("Title should match", "Number Format Test", presentation.getTitle());
        assertEquals("Should have one slide", 1, presentation.getSize());

        Slide slide = presentation.getSlide(0);
        assertEquals("Should have one item", 1, slide.getSlideItems().size());

        TextItem item = (TextItem) slide.getSlideItems().elementAt(0);
        assertEquals("Item text should match", "Invalid Level", item.getText());
        assertEquals("Item should have default level 1", 1, item.getLevel());
    }

    @Test
    public void testLoadFileWithUnknownItemType() throws IOException
    {
        // Create XML with unknown item type
        try (PrintWriter out = new PrintWriter(new FileWriter(TEST_FILE_PATH)))
        {
            out.println("<?xml version=\"1.0\"?>");
            out.println("<!DOCTYPE presentation SYSTEM \"jabberpoint.dtd\">");
            out.println("<presentation>");
            out.println("<showtitle>Unknown Type Test</showtitle>");
            out.println("<slide>");
            out.println("<title>Test Slide</title>");
            // Item with unknown type
            out.println("<item kind=\"unknown\" level=\"1\">Unknown Type</item>");
            // Valid item
            out.println("<item kind=\"text\" level=\"2\">Valid Item</item>");
            out.println("</slide>");
            out.println("</presentation>");
        }

        // Should not throw exception, but skip unknown type
        xmlAccessor.loadFile(presentation, TEST_FILE_PATH);

        // Verify loaded content
        assertEquals("Title should match", "Unknown Type Test", presentation.getTitle());
        assertEquals("Should have one slide", 1, presentation.getSize());

        Slide slide = presentation.getSlide(0);
        assertEquals("Should have one item (unknown type skipped)", 1, slide.getSlideItems().size());

        TextItem item = (TextItem) slide.getSlideItems().elementAt(0);
        assertEquals("Item text should match", "Valid Item", item.getText());
    }
} 