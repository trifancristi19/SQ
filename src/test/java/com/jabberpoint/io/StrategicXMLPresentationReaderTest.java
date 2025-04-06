package com.jabberpoint.io;

import com.jabberpoint.BitmapItem;
import com.jabberpoint.Presentation;
import com.jabberpoint.Slide;
import com.jabberpoint.TextItem;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests for the StrategicXMLPresentationReader class
 */
public class StrategicXMLPresentationReaderTest
{

    private static final String TEST_XML_CONTENT =
            "<?xml version=\"1.0\"?>\n" +
                    "<presentation>\n" +
                    "  <title>Test Presentation</title>\n" +
                    "  <slide>\n" +
                    "    <title>First Slide</title>\n" +
                    "    <text level=\"1\">This is text item 1</text>\n" +
                    "    <text level=\"2\">This is text item 2</text>\n" +
                    "  </slide>\n" +
                    "  <slide>\n" +
                    "    <title>Second Slide</title>\n" +
                    "    <image level=\"1\" name=\"test-image.jpg\"/>\n" +
                    "    <text level=\"3\">This is a text item</text>\n" +
                    "  </slide>\n" +
                    "</presentation>";

    private File testFile;
    private Presentation presentation;

    @Before
    public void setUp() throws IOException
    {
        // Create a temporary test file
        testFile = File.createTempFile("test-presentation", ".xml");
        testFile.deleteOnExit();

        // Write the test XML content to the file
        try (FileWriter writer = new FileWriter(testFile))
        {
            writer.write(TEST_XML_CONTENT);
        }

        // Create a fresh presentation for each test
        presentation = new Presentation();
    }

    @Test
    public void testConstructorWithDefaultStrategy()
    {
        // Test the default constructor
        StrategicXMLPresentationReader reader = new StrategicXMLPresentationReader();
        assertNotNull("Reader should be created", reader);
    }

    @Test
    public void testConstructorWithSpecificStrategy()
    {
        // Test constructor with specific strategy
        XMLParsingStrategy strategy = new DOMXMLParsingStrategy();
        StrategicXMLPresentationReader reader = new StrategicXMLPresentationReader(strategy);
        assertNotNull("Reader should be created with specific strategy", reader);
    }

    @Test
    public void testConstructorWithStrategyType()
    {
        // Test constructor with strategy type
        StrategicXMLPresentationReader reader = new StrategicXMLPresentationReader(
                XMLParsingStrategyFactory.StrategyType.DOM);
        assertNotNull("Reader should be created with strategy type", reader);
    }

    @Test
    public void testLoadPresentationWithDOMStrategy() throws Exception
    {
        // Create a reader with DOM strategy
        StrategicXMLPresentationReader reader = new StrategicXMLPresentationReader(
                XMLParsingStrategyFactory.StrategyType.DOM);

        // Load the presentation
        reader.loadPresentation(presentation, testFile.getAbsolutePath());

        // Verify the presentation content
        assertEquals("Presentation title should match", "Test Presentation", presentation.getTitle());
        assertEquals("Should have 2 slides", 2, presentation.getSize());

        // Check first slide
        Slide slide1 = presentation.getSlide(0);
        assertEquals("First slide title should match", "First Slide", slide1.getTitle());
        List<?> items1 = slide1.getSlideItems();
        assertEquals("First slide should have 2 items", 2, items1.size());
        assertTrue("First item should be TextItem", items1.get(0) instanceof TextItem);
        assertEquals("First item level should be 1", 1, ((TextItem) items1.get(0)).getLevel());

        // Check second slide
        Slide slide2 = presentation.getSlide(1);
        assertEquals("Second slide title should match", "Second Slide", slide2.getTitle());
        List<?> items2 = slide2.getSlideItems();
        assertEquals("Second slide should have 2 items", 2, items2.size());
        assertTrue("First item should be BitmapItem", items2.get(0) instanceof BitmapItem);
        assertEquals("Bitmap item name should match", "test-image.jpg", ((BitmapItem) items2.get(0)).getName());
    }

    @Test
    public void testLoadPresentationWithSAXStrategy() throws Exception
    {
        // Create a reader with SAX strategy
        StrategicXMLPresentationReader reader = new StrategicXMLPresentationReader(
                XMLParsingStrategyFactory.StrategyType.SAX);

        // Load the presentation
        reader.loadPresentation(presentation, testFile.getAbsolutePath());

        // Verify the presentation content (same checks as DOM test)
        assertEquals("Presentation title should match", "Test Presentation", presentation.getTitle());
        assertEquals("Should have 2 slides", 2, presentation.getSize());
    }

    @Test(expected = java.io.FileNotFoundException.class)
    public void testNonExistentFile() throws Exception
    {
        // Create a reader with default strategy
        StrategicXMLPresentationReader reader = new StrategicXMLPresentationReader();

        // Try to load a non-existent file
        reader.loadPresentation(presentation, "non-existent-file.xml");
    }

    @Test
    public void testEmptyTitle() throws Exception
    {
        // Create a file with an empty title
        File emptyTitleFile = File.createTempFile("empty-title", ".xml");
        emptyTitleFile.deleteOnExit();

        try (FileWriter writer = new FileWriter(emptyTitleFile))
        {
            writer.write("<?xml version=\"1.0\"?>\n" +
                    "<presentation>\n" +
                    "  <title></title>\n" +
                    "  <slide>\n" +
                    "    <title>Empty Title Test</title>\n" +
                    "  </slide>\n" +
                    "</presentation>");
        }

        // Create a reader with default strategy
        StrategicXMLPresentationReader reader = new StrategicXMLPresentationReader();

        // Load the presentation
        reader.loadPresentation(presentation, emptyTitleFile.getAbsolutePath());

        // Title should be empty but not null
        assertEquals("Title should be empty", "", presentation.getTitle());
    }

    @Test
    public void testNoTitle() throws Exception
    {
        // Create a file with no title element
        File noTitleFile = File.createTempFile("no-title", ".xml");
        noTitleFile.deleteOnExit();

        try (FileWriter writer = new FileWriter(noTitleFile))
        {
            writer.write("<?xml version=\"1.0\"?>\n" +
                    "<presentation>\n" +
                    "  <slide>\n" +
                    "    <title>No Title Test</title>\n" +
                    "  </slide>\n" +
                    "</presentation>");
        }

        // Create a reader with default strategy
        StrategicXMLPresentationReader reader = new StrategicXMLPresentationReader();

        // Load the presentation
        reader.loadPresentation(presentation, noTitleFile.getAbsolutePath());

        // Title should be the default
        assertEquals("Title should be default", "Unnamed Presentation", presentation.getTitle());
    }
} 