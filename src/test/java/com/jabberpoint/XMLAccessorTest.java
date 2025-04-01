package com.jabberpoint;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class XMLAccessorTest
{

    private Presentation presentation;
    private XMLAccessor xmlAccessor;
    private final String TEST_DIR = "target/test-resources";
    private final String TEST_FILE = TEST_DIR + "/test-presentation.xml";

    @Before
    public void setUp() throws IOException
    {
        // Make sure test directory exists
        new File(TEST_DIR).mkdirs();

        // Initialize objects
        this.presentation = new Presentation();
        this.xmlAccessor = new XMLAccessor();

        // Initialize styles
        Style.createStyles();

        // Ensure the DTD file exists in root directory
        File rootDtdFile = new File("jabberpoint.dtd");
        if (!rootDtdFile.exists())
        {
            createDtdFile(rootDtdFile.getPath());
        }

        // Also copy the DTD to the test resources directory
        File testDtdFile = new File(TEST_DIR + "/jabberpoint.dtd");
        if (!testDtdFile.exists())
        {
            createDtdFile(testDtdFile.getPath());
        }
    }

    @After
    public void tearDown()
    {
        // Clean up any test files
        File testFile = new File(TEST_FILE);
        if (testFile.exists())
        {
            testFile.delete();
        }
    }

    @Test
    public void testLoadFile() throws IOException
    {
        // Create a test file or use an existing one
        createTestXmlFile();

        // Load the file
        this.xmlAccessor.loadFile(presentation, TEST_FILE);

        // Verify the presentation was loaded correctly
        assertEquals("Test Presentation", presentation.getTitle());
        assertTrue("Presentation should have at least one slide", presentation.getSize() > 0);

        Slide slide = this.presentation.getSlide(0);
        assertNotNull("Slide should be loaded", slide);
        assertEquals("Test Slide", slide.getTitle());
        assertTrue("Slide should have items", slide.getSize() > 0);
    }

    @Test
    public void testSaveFile() throws IOException
    {
        // Create a presentation
        this.presentation.setTitle("Test Presentation");
        Slide slide = new Slide();
        slide.setTitle("Test Slide");
        slide.append(new TextItem(1, "Test Text Item"));
        this.presentation.append(slide);

        // Save the presentation
        this.xmlAccessor.saveFile(this.presentation, TEST_FILE);

        // Verify the file was created
        File savedFile = new File(TEST_FILE);
        assertTrue("File should be created", savedFile.exists());

        // Load the saved file into a new presentation to verify
        Presentation loadedPresentation = new Presentation();
        this.xmlAccessor.loadFile(loadedPresentation, TEST_FILE);

        assertEquals("Title should match", this.presentation.getTitle(), loadedPresentation.getTitle());
        assertEquals("Size should match", this.presentation.getSize(), loadedPresentation.getSize());
    }

    @Test
    public void testLoadFileWithInvalidLevel() throws IOException {
        String xmlContent = "<?xml version=\"1.0\"?>\n" +
                "<!DOCTYPE presentation SYSTEM \"jabberpoint.dtd\">\n" +
                "<presentation>\n" +
                "<showtitle>Test Presentation</showtitle>\n" +
                "<slide>\n" +
                "<title>Test Slide</title>\n" +
                "<item kind=\"text\" level=\"invalid\">Text Item 1</item>\n" +
                "</slide>\n" +
                "</presentation>";

        Files.write(Paths.get(TEST_FILE), xmlContent.getBytes());
        this.xmlAccessor.loadFile(presentation, TEST_FILE);
        
        // Should still load the slide but with default level 1
        Slide slide = this.presentation.getSlide(0);
        assertEquals(1, slide.getSlideItems().get(0).getLevel());
    }

    @Test
    public void testLoadFileWithUnknownItemType() throws IOException {
        String xmlContent = "<?xml version=\"1.0\"?>\n" +
                "<!DOCTYPE presentation SYSTEM \"jabberpoint.dtd\">\n" +
                "<presentation>\n" +
                "<showtitle>Test Presentation</showtitle>\n" +
                "<slide>\n" +
                "<title>Test Slide</title>\n" +
                "<item kind=\"unknown\" level=\"1\">Unknown Item</item>\n" +
                "</slide>\n" +
                "</presentation>";

        Files.write(Paths.get(TEST_FILE), xmlContent.getBytes());
        this.xmlAccessor.loadFile(presentation, TEST_FILE);
        
        // Should load the slide but skip the unknown item type
        Slide slide = this.presentation.getSlide(0);
        assertEquals(0, slide.getSize());
    }

    @Test
    public void testLoadFileWithMissingTitle() throws IOException {
        String xmlContent = "<?xml version=\"1.0\"?>\n" +
                "<!DOCTYPE presentation SYSTEM \"jabberpoint.dtd\">\n" +
                "<presentation>\n" +
                "<showtitle></showtitle>\n" +
                "<slide>\n" +
                "<title></title>\n" +
                "<item kind=\"text\" level=\"1\">Text Item 1</item>\n" +
                "</slide>\n" +
                "</presentation>";

        Files.write(Paths.get(TEST_FILE), xmlContent.getBytes());
        this.xmlAccessor.loadFile(presentation, TEST_FILE);
        
        assertEquals("", presentation.getTitle());
        assertEquals("", presentation.getSlide(0).getTitle());
    }

    @Test
    public void testSaveFileWithImageItem() throws IOException {
        // Create a presentation with an image item
        this.presentation.setTitle("Test Presentation");
        Slide slide = new Slide();
        slide.setTitle("Test Slide");
        // Use a non-existent image file - should still save but log warning
        slide.append(new BitmapItem(1, "nonexistent.jpg"));
        this.presentation.append(slide);

        // Save the presentation
        this.xmlAccessor.saveFile(this.presentation, TEST_FILE);

        // Verify the file was created and contains the image item
        String savedContent = new String(Files.readAllBytes(Paths.get(TEST_FILE)));
        assertTrue(savedContent.contains("kind=\"image\""));
        assertTrue(savedContent.contains("nonexistent.jpg"));
    }

    @Test
    public void testSaveFileWithMultipleItems() throws IOException {
        // Create a presentation with multiple items
        this.presentation.setTitle("Test Presentation");
        Slide slide = new Slide();
        slide.setTitle("Test Slide");
        slide.append(new TextItem(1, "First Item"));
        slide.append(new TextItem(2, "Second Item"));
        // Use a non-existent image file - should still save but log warning
        slide.append(new BitmapItem(3, "nonexistent.jpg"));
        this.presentation.append(slide);

        // Save the presentation
        this.xmlAccessor.saveFile(this.presentation, TEST_FILE);

        // Verify the file was created and contains all items
        String savedContent = new String(Files.readAllBytes(Paths.get(TEST_FILE)));
        assertTrue(savedContent.contains("First Item"));
        assertTrue(savedContent.contains("Second Item"));
        assertTrue(savedContent.contains("nonexistent.jpg"));
    }

    @Test(expected = IOException.class)
    public void testLoadFileWithInvalidXML() throws IOException {
        // Create a malformed XML file with unclosed tags
        String invalidXml = "<?xml version=\"1.0\"?>\n" +
                "<!DOCTYPE presentation SYSTEM \"jabberpoint.dtd\">\n" +
                "<presentation>\n" +
                "<showtitle>Test Presentation</showtitle>\n" +
                "<slide>\n" +
                "<title>Test Slide</title>\n" +
                "<item kind=\"text\" level=\"1\">Text Item 1</item>\n" +
                "</slide>\n" +
                "<slide>\n" +  // Unclosed slide tag
                "<title>Invalid Slide</title>\n" +
                "<item kind=\"text\" level=\"1\">Invalid Item</item>\n" +
                "</presentation>";  // Close presentation tag

        Files.write(Paths.get(TEST_FILE), invalidXml.getBytes());
        this.xmlAccessor.loadFile(presentation, TEST_FILE);
    }

    @Test
    public void testLoadFileWithInvalidLevelValue() throws IOException {
        String xmlContent = "<?xml version=\"1.0\"?>\n" +
                "<!DOCTYPE presentation SYSTEM \"jabberpoint.dtd\">\n" +
                "<presentation>\n" +
                "<showtitle>Test Presentation</showtitle>\n" +
                "<slide>\n" +
                "<title>Test Slide</title>\n" +
                "<item kind=\"text\" level=\"not_a_number\">Text Item 1</item>\n" +
                "</slide>\n" +
                "</presentation>";

        Files.write(Paths.get(TEST_FILE), xmlContent.getBytes());
        this.xmlAccessor.loadFile(presentation, TEST_FILE);
        
        // Should use default level 1 when parsing fails
        Slide slide = this.presentation.getSlide(0);
        assertEquals(1, slide.getSlideItems().get(0).getLevel());
    }

    @Test
    public void testSaveFileWithEmptyPresentation() throws IOException {
        // Create an empty presentation
        this.presentation.setTitle("");
        this.presentation.clear();

        // Save the presentation
        this.xmlAccessor.saveFile(this.presentation, TEST_FILE);

        // Verify the file was created with empty content
        String savedContent = new String(Files.readAllBytes(Paths.get(TEST_FILE)));
        assertTrue(savedContent.contains("<showtitle></showtitle>"));
        assertFalse(savedContent.contains("<slide>"));
    }

    @Test
    public void testSaveFileWithSpecialCharacters() throws IOException {
        // Create a presentation with special characters
        this.presentation.setTitle("Test & Presentation");
        Slide slide = new Slide();
        slide.setTitle("Slide with < > & characters");
        slide.append(new TextItem(1, "Text with < > & characters"));
        this.presentation.append(slide);

        // Save the presentation
        this.xmlAccessor.saveFile(this.presentation, TEST_FILE);

        // Verify the file was created and contains the special characters
        String savedContent = new String(Files.readAllBytes(Paths.get(TEST_FILE)));
        assertTrue(savedContent.contains("Test & Presentation"));
        assertTrue(savedContent.contains("Slide with < > & characters"));
        assertTrue(savedContent.contains("Text with < > & characters"));
    }

    @Test
    public void testLoadFileWithMissingAttributes() throws IOException {
        String xmlContent = "<?xml version=\"1.0\"?>\n" +
                "<!DOCTYPE presentation SYSTEM \"jabberpoint.dtd\">\n" +
                "<presentation>\n" +
                "<showtitle>Test Presentation</showtitle>\n" +
                "<slide>\n" +
                "<title>Test Slide</title>\n" +
                "<item>Missing attributes</item>\n" +
                "</slide>\n" +
                "</presentation>";

        Files.write(Paths.get(TEST_FILE), xmlContent.getBytes());
        this.xmlAccessor.loadFile(presentation, TEST_FILE);
        
        // Should handle missing attributes gracefully
        Slide slide = this.presentation.getSlide(0);
        assertEquals(0, slide.getSize());
    }

    @Test
    public void testSaveFileWithMultipleSlidesAndItems() throws IOException {
        // Create a presentation with multiple slides and items
        this.presentation.setTitle("Complex Presentation");
        
        // First slide
        Slide slide1 = new Slide();
        slide1.setTitle("First Slide");
        slide1.append(new TextItem(1, "First Item"));
        // Use a non-existent image file - should still save but log warning
        slide1.append(new BitmapItem(2, "nonexistent.jpg"));
        this.presentation.append(slide1);
        
        // Second slide
        Slide slide2 = new Slide();
        slide2.setTitle("Second Slide");
        slide2.append(new TextItem(1, "Second Item"));
        slide2.append(new TextItem(2, "Third Item"));
        this.presentation.append(slide2);

        // Save the presentation
        this.xmlAccessor.saveFile(this.presentation, TEST_FILE);

        // Verify the file was created with all content
        String savedContent = new String(Files.readAllBytes(Paths.get(TEST_FILE)));
        assertTrue(savedContent.contains("Complex Presentation"));
        assertTrue(savedContent.contains("First Slide"));
        assertTrue(savedContent.contains("Second Slide"));
        assertTrue(savedContent.contains("First Item"));
        assertTrue(savedContent.contains("nonexistent.jpg"));
        assertTrue(savedContent.contains("Second Item"));
        assertTrue(savedContent.contains("Third Item"));
    }

    @Test
    public void testLoadFileWithMixedItemTypes() throws IOException {
        String xmlContent = "<?xml version=\"1.0\"?>\n" +
                "<!DOCTYPE presentation SYSTEM \"jabberpoint.dtd\">\n" +
                "<presentation>\n" +
                "<showtitle>Test Presentation</showtitle>\n" +
                "<slide>\n" +
                "<title>Test Slide</title>\n" +
                "<item kind=\"text\" level=\"1\">Text Item 1</item>\n" +
                "<item kind=\"image\" level=\"2\">nonexistent.jpg</item>\n" +
                "<item kind=\"text\" level=\"3\">Text Item 2</item>\n" +
                "</slide>\n" +
                "</presentation>";

        Files.write(Paths.get(TEST_FILE), xmlContent.getBytes());
        this.xmlAccessor.loadFile(presentation, TEST_FILE);
        
        // Verify all items are loaded correctly
        Slide slide = this.presentation.getSlide(0);
        assertEquals(3, slide.getSize());
        
        // Verify item types and levels
        assertTrue(slide.getSlideItems().get(0) instanceof TextItem);
        assertTrue(slide.getSlideItems().get(1) instanceof BitmapItem);
        assertTrue(slide.getSlideItems().get(2) instanceof TextItem);
        
        assertEquals(1, slide.getSlideItems().get(0).getLevel());
        assertEquals(2, slide.getSlideItems().get(1).getLevel());
        assertEquals(3, slide.getSlideItems().get(2).getLevel());
    }

    // Helper method to create a test XML file
    private void createTestXmlFile() throws IOException
    {
        // Create a basic XML structure for testing with a local DTD reference
        String xmlContent = "<?xml version=\"1.0\"?>\n" +
                "<!DOCTYPE presentation SYSTEM \"jabberpoint.dtd\">\n" +
                "<presentation>\n" +
                "<showtitle>Test Presentation</showtitle>\n" +
                "<slide>\n" +
                "<title>Test Slide</title>\n" +
                "<item kind=\"text\" level=\"1\">Text Item 1</item>\n" +
                "<item kind=\"text\" level=\"2\">Text Item 2</item>\n" +
                "</slide>\n" +
                "</presentation>";

        // Ensure directory exists
        new File(TEST_FILE).getParentFile().mkdirs();

        // Write the file
        Files.write(Paths.get(TEST_FILE), xmlContent.getBytes());
    }

    // Helper method to create a simple DTD file for testing
    private void createDtdFile(String path) throws IOException
    {
        String dtdContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!ELEMENT presentation (showtitle, slide*)>\n" +
                "<!ELEMENT showtitle (#PCDATA)>\n" +
                "<!ELEMENT slide (title, item*)>\n" +
                "<!ELEMENT title (#PCDATA)>\n" +
                "<!ELEMENT item (#PCDATA)>\n" +
                "<!ATTLIST item kind CDATA #REQUIRED>\n" +
                "<!ATTLIST item level CDATA #REQUIRED>";

        Files.write(Paths.get("jabberpoint.dtd"), dtdContent.getBytes());
        System.out.println("Created jabberpoint.dtd file for testing");
    }
} 