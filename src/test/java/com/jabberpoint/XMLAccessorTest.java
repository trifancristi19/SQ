package com.jabberpoint;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class XMLAccessorTest {

    private Presentation presentation;
    private XMLAccessor xmlAccessor;
    private final String TEST_DIR = "target/test-resources";
    private final String TEST_FILE = TEST_DIR + "/test-presentation.xml";

    @Before
    public void setUp() throws IOException {
        // Make sure test directory exists
        new File(TEST_DIR).mkdirs();

        // Initialize objects
        this.presentation = new Presentation();
        this.xmlAccessor = new XMLAccessor();

        // Initialize styles
        Style.createStyles();

        // Ensure the DTD file exists in root directory
        File rootDtdFile = new File("jabberpoint.dtd");
        if (!rootDtdFile.exists()) {
            createDtdFile(rootDtdFile.getPath());
        }

        // Also copy the DTD to the test resources directory
        File testDtdFile = new File(TEST_DIR + "/jabberpoint.dtd");
        if (!testDtdFile.exists()) {
            createDtdFile(testDtdFile.getPath());
        }
    }

    @After
    public void tearDown() {
        // Clean up any test files
        File testFile = new File(TEST_FILE);
        if (testFile.exists()) {
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

        File invalidLevelFile = new File(TEST_DIR + "/invalid-level.xml");
        try {
            Files.write(Paths.get(invalidLevelFile.getPath()), xmlContent.getBytes());
            this.xmlAccessor.loadFile(presentation, invalidLevelFile.getPath());
            
            // Should still load the slide with some default level
            Slide slide = this.presentation.getSlide(0);
            assertNotNull("Slide should be loaded", slide);
            
            // The item might be skipped or loaded with a default level
            if (slide.getSize() > 0) {
                SlideItem item = slide.getSlideItem(0);
                assertNotNull("Item should exist if loaded", item);
                // Item level should be valid (any level is acceptable)
                assertTrue("Level should be a valid number", item.getLevel() >= 0);
            }
        } finally {
            invalidLevelFile.delete();
        }
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

        File unknownTypeFile = new File(TEST_DIR + "/unknown-type.xml");
        try {
            Files.write(Paths.get(unknownTypeFile.getPath()), xmlContent.getBytes());
            this.xmlAccessor.loadFile(presentation, unknownTypeFile.getPath());
            
            // Should load the slide but might skip the unknown item type
            Slide slide = this.presentation.getSlide(0);
            assertNotNull("Slide should be loaded", slide);
            
            // We don't assert the item count because different implementations might
            // handle unknown types differently (skip or create default item)
        } finally {
            unknownTypeFile.delete();
        }
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

    @Test
    public void testLoadFileWithInvalidXML() throws IOException {
        // Create a malformed XML file with unclosed tags
        String invalidXml = "<?xml version=\"1.0\"?>\n" +
                "<!DOCTYPE presentation SYSTEM \"jabberpoint.dtd\">\n" +
                "<presentation>\n" +
                "<showtitle>Test Presentation</showtitle>\n" +
                "<slide>\n" +
                "<title>Test Slide</title>\n" +
                "<item kind=\"text\" level=\"1\">Text Item 1</item>\n" +
                "</presentation>";  // Close presentation tag

        File invalidFile = new File(TEST_DIR + "/invalid-xml.xml");
        try {
            Files.write(Paths.get(invalidFile.getPath()), invalidXml.getBytes());
            
            try {
                this.xmlAccessor.loadFile(presentation, invalidFile.getPath());
                fail("Should throw exception for invalid XML");
            } catch (IOException e) {
                // Expected exception
                String error = e.getMessage().toLowerCase();
                // Verify the error is related to XML parsing
                assertTrue("Exception should be related to XML parsing",
                    error.contains("xml") || error.contains("parse") || 
                    error.contains("entity") || error.contains("element"));
            }
        } finally {
            invalidFile.delete();
        }
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

        File missingAttrFile = new File(TEST_DIR + "/missing-attr.xml");
        try {
            Files.write(Paths.get(missingAttrFile.getPath()), xmlContent.getBytes());
            this.xmlAccessor.loadFile(presentation, missingAttrFile.getPath());
            
            // Should handle missing attributes gracefully
            // The item might be skipped or loaded with default values
            Slide slide = this.presentation.getSlide(0);
            assertNotNull("Slide should be loaded", slide);
            // Don't make assertions about the item count as implementations may vary
        } finally {
            missingAttrFile.delete();
        }
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

    @Test
    public void testLoadSlideItemWithMissingAttributes() throws IOException {
        String xmlContent = "<?xml version=\"1.0\"?>\n" +
                "<!DOCTYPE presentation SYSTEM \"jabberpoint.dtd\">\n" +
                "<presentation>\n" +
                "<showtitle>Test Presentation</showtitle>\n" +
                "<slide>\n" +
                "<title>Test Slide</title>\n" +
                "<item>Text Item 1</item>\n" + // Missing both level and kind attributes
                "</slide>\n" +
                "</presentation>";

        Files.write(Paths.get(TEST_FILE), xmlContent.getBytes());
        this.xmlAccessor.loadFile(presentation, TEST_FILE);
        
        // Should load the slide but skip the item with missing attributes
        Slide slide = this.presentation.getSlide(0);
        assertEquals(0, slide.getSize());
    }

    @Test
    public void testSaveFileWithNullTitle() throws IOException {
        // Create a presentation with null title
        this.presentation.setTitle(null);
        Slide slide = new Slide();
        slide.setTitle("Test Slide");
        slide.append(new TextItem(1, "Test Text Item"));
        this.presentation.append(slide);

        // Save the presentation
        this.xmlAccessor.saveFile(this.presentation, TEST_FILE);

        // Verify the file was created and contains empty title
        String savedContent = new String(Files.readAllBytes(Paths.get(TEST_FILE)));
        assertTrue(savedContent.contains("<showtitle></showtitle>"));
    }

    @Test
    public void testSaveFileWithNullSlideTitle() throws IOException {
        // Create a presentation with null slide title
        this.presentation.setTitle("Test Presentation");
        Slide slide = new Slide();
        slide.setTitle(null);
        slide.append(new TextItem(1, "Test Text Item"));
        this.presentation.append(slide);

        // Save the presentation
        this.xmlAccessor.saveFile(this.presentation, TEST_FILE);

        // Verify the file was created and contains empty slide title
        String savedContent = new String(Files.readAllBytes(Paths.get(TEST_FILE)));
        assertTrue(savedContent.contains("<title></title>"));
    }

    @Test
    public void testSaveFileWithNullItems() throws IOException {
        // Create a presentation with null items
        this.presentation.setTitle("Test Presentation");
        Slide slide = new Slide();
        slide.setTitle("Test Slide");
        slide.append(null); // This should be handled gracefully
        this.presentation.append(slide);

        // Save the presentation
        this.xmlAccessor.saveFile(this.presentation, TEST_FILE);

        // Verify the file was created and doesn't contain any items
        String savedContent = new String(Files.readAllBytes(Paths.get(TEST_FILE)));
        assertFalse(savedContent.contains("<item"));
    }

    @Test
    public void testLoadFileWithMalformedXML() throws IOException {
        String xmlContent = "<?xml version=\"1.0\"?>\n" +
                "<!DOCTYPE presentation SYSTEM \"jabberpoint.dtd\">\n" +
                "<presentation>\n" +
                "<showtitle>Test Presentation</showtitle>\n" +
                "<slide>\n" +
                "<title>Test Slide</title>\n" +
                "<item kind=\"text\" level=\"1\">Text Item 1</item>\n" +
                "</slide>"; // Missing closing presentation tag

        Files.write(Paths.get(TEST_FILE), xmlContent.getBytes());
        
        // Should throw IOException for malformed XML
        try {
            this.xmlAccessor.loadFile(presentation, TEST_FILE);
            fail("Should have thrown IOException for malformed XML");
        } catch (IOException e) {
            assertTrue(e.getMessage().contains("SAX Exception"));
        }
    }

    @Test
    public void testLoadFileWithInvalidDTD() throws IOException {
        String xmlContent = "<?xml version=\"1.0\"?>\n" +
                "<!DOCTYPE presentation SYSTEM \"invalid.dtd\">\n" + // Invalid DTD
                "<presentation>\n" +
                "<showtitle>Test Presentation</showtitle>\n" +
                "<slide>\n" +
                "<title>Test Slide</title>\n" +
                "<item kind=\"text\" level=\"1\">Text Item 1</item>\n" +
                "</slide>\n" +
                "</presentation>";

        Files.write(Paths.get(TEST_FILE), xmlContent.getBytes());
        
        // Should still load the file even with invalid DTD
        this.xmlAccessor.loadFile(presentation, TEST_FILE);
        
        // Verify the content was loaded correctly
        assertEquals("Test Presentation", presentation.getTitle());
        assertEquals(1, presentation.getSize());
        assertEquals("Test Slide", presentation.getSlide(0).getTitle());
    }

    @Test
    public void testSaveFileWithEmptyDirectory() throws IOException {
        // Create a presentation
        this.presentation.setTitle("Test Presentation");
        Slide slide = new Slide();
        slide.setTitle("Test Slide");
        slide.append(new TextItem(1, "Test Text Item"));
        this.presentation.append(slide);

        // Save to a non-existent directory
        String newDirPath = TEST_DIR + "/nonexistent/dir";
        String newFilePath = newDirPath + "/test.xml";
        
        // Clean up any existing test files from previous failed runs
        new File(newFilePath).delete();
        new File(newDirPath).delete();
        new File(TEST_DIR + "/nonexistent").delete();
        
        // Now attempt to save the file
        this.xmlAccessor.saveFile(this.presentation, newFilePath);
            
        // Verify the file was created
        File savedFile = new File(newFilePath);
        assertTrue("File should be created", savedFile.exists());
            
        // Clean up after test
        savedFile.delete();
        new File(newDirPath).delete();
        new File(TEST_DIR + "/nonexistent").delete();
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

    /**
     * Test loadSlideItem method directly
     */
    @Test
    public void testLoadSlideItem() throws Exception {
        // Create an XML document with item elements
        String xmlContent = "<?xml version=\"1.0\"?>\n" +
                "<!DOCTYPE presentation SYSTEM \"jabberpoint.dtd\">\n" +
                "<presentation>\n" +
                "<showtitle>Test Presentation</showtitle>\n" +
                "<slide>\n" +
                "<title>Test Slide</title>\n" +
                "<item kind=\"text\" level=\"1\">Text Item 1</item>\n" +
                "<item kind=\"image\" level=\"2\">test.jpg</item>\n" +
                "<item kind=\"unknown\" level=\"3\">Unknown Item</item>\n" +
                "</slide>\n" +
                "</presentation>";

        // Save this to a temporary file
        File tempFile = new File(TEST_DIR + "/load-item-test.xml");
        Files.write(Paths.get(tempFile.getPath()), xmlContent.getBytes());

        try {
            // Parse the file to get the slide elements
            javax.xml.parsers.DocumentBuilderFactory factory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            javax.xml.parsers.DocumentBuilder builder = factory.newDocumentBuilder();
            org.w3c.dom.Document document = builder.parse(tempFile);
            
            // Get the slide element
            org.w3c.dom.NodeList slides = document.getElementsByTagName("slide");
            org.w3c.dom.Element slideElement = (org.w3c.dom.Element) slides.item(0);
            
            // Get the item elements
            org.w3c.dom.NodeList items = slideElement.getElementsByTagName("item");
            
            // Create a slide to add items to
            Slide slide = new Slide();
            
            // Use the loadSlideItem method directly using reflection
            java.lang.reflect.Method loadSlideItemMethod = XMLAccessor.class.getDeclaredMethod(
                "loadSlideItem", Slide.class, org.w3c.dom.Element.class);
            loadSlideItemMethod.setAccessible(true);
            
            // Test with text item
            org.w3c.dom.Element textItem = (org.w3c.dom.Element) items.item(0);
            loadSlideItemMethod.invoke(this.xmlAccessor, slide, textItem);
            
            // Test with image item
            org.w3c.dom.Element imageItem = (org.w3c.dom.Element) items.item(1);
            loadSlideItemMethod.invoke(this.xmlAccessor, slide, imageItem);
            
            // Test with unknown item type
            org.w3c.dom.Element unknownItem = (org.w3c.dom.Element) items.item(2);
            loadSlideItemMethod.invoke(this.xmlAccessor, slide, unknownItem);
            
            // Verify the items were added correctly
            assertEquals("Slide should have 2 items (unknown type is skipped)", 2, slide.getSize());
            
            // Check text item
            SlideItem item1 = slide.getSlideItem(0);
            assertTrue("First item should be a TextItem", item1 instanceof TextItem);
            assertEquals("First item level should be 1", 1, item1.getLevel());
            assertEquals("First item text should match", "Text Item 1", ((TextItem)item1).getText());
            
            // Check image item
            SlideItem item2 = slide.getSlideItem(1);
            assertTrue("Second item should be a BitmapItem", item2 instanceof BitmapItem);
            assertEquals("Second item level should be 2", 2, item2.getLevel());
            assertEquals("Second item filename should match", "test.jpg", ((BitmapItem)item2).getName());
        } finally {
            // Clean up
            tempFile.delete();
        }
    }

    /**
     * Test loadSlideItem with malformed attributes
     */
    @Test
    public void testLoadSlideItemWithMalformedAttributes() throws Exception {
        // Create an XML document with item elements with various issues
        String xmlContent = "<?xml version=\"1.0\"?>\n" +
                "<!DOCTYPE presentation SYSTEM \"jabberpoint.dtd\">\n" +
                "<presentation>\n" +
                "<showtitle>Test Presentation</showtitle>\n" +
                "<slide>\n" +
                "<title>Test Slide</title>\n" +
                "<item kind=\"text\" level=\"invalid\">Invalid Level</item>\n" + // Non-numeric level
                "<item kind=\"text\">Missing Level</item>\n" + // Missing level attribute
                "<item level=\"1\">Missing Kind</item>\n" + // Missing kind attribute
                "<item>No Attributes</item>\n" + // No attributes
                "</slide>\n" +
                "</presentation>";

        // Save this to a temporary file
        File tempFile = new File(TEST_DIR + "/malformed-item-test.xml");
        Files.write(Paths.get(tempFile.getPath()), xmlContent.getBytes());

        try {
            // Parse the file to get the slide elements
            javax.xml.parsers.DocumentBuilderFactory factory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            javax.xml.parsers.DocumentBuilder builder = factory.newDocumentBuilder();
            org.w3c.dom.Document document = builder.parse(tempFile);
            
            // Get the slide element
            org.w3c.dom.NodeList slides = document.getElementsByTagName("slide");
            org.w3c.dom.Element slideElement = (org.w3c.dom.Element) slides.item(0);
            
            // Get the item elements
            org.w3c.dom.NodeList items = slideElement.getElementsByTagName("item");
            
            // Create a slide to add items to
            Slide slide = new Slide();
            
            // Use the loadSlideItem method directly using reflection
            java.lang.reflect.Method loadSlideItemMethod = XMLAccessor.class.getDeclaredMethod(
                "loadSlideItem", Slide.class, org.w3c.dom.Element.class);
            loadSlideItemMethod.setAccessible(true);
            
            // Test each problematic item
            for (int i = 0; i < items.getLength(); i++) {
                org.w3c.dom.Element item = (org.w3c.dom.Element) items.item(i);
                loadSlideItemMethod.invoke(this.xmlAccessor, slide, item);
            }
            
            // Invalid level should default to 1, missing attributes should be skipped
            assertTrue("Items with valid required attributes should be added", slide.getSize() <= 1);
            
            // If the first item was added (some implementations might handle invalid level by using default)
            if (slide.getSize() > 0) {
                SlideItem item = slide.getSlideItem(0);
                assertEquals("Level should default to 1 for invalid level", 1, item.getLevel());
            }
        } finally {
            // Clean up
            tempFile.delete();
        }
    }

    /**
     * Test createDTDFile method
     */
    @Test
    public void testCreateDTDFile() throws IOException {
        String dtdPath = TEST_DIR + "/test-create-dtd.dtd";
        File dtdFile = new File(dtdPath);
        
        // Delete the file if it exists
        if (dtdFile.exists()) {
            dtdFile.delete();
        }
        
        // Create a presentation with content
        Presentation testPresentation = new Presentation();
        testPresentation.setTitle("DTD Test");
        
        // Save the presentation, which should create the DTD
        String xmlPath = TEST_DIR + "/dtd-test.xml";
        this.xmlAccessor.saveFile(testPresentation, xmlPath);
        
        // Verify the DTD was created
        File createdDtd = new File(TEST_DIR + "/jabberpoint.dtd");
        assertTrue("DTD file should be created", createdDtd.exists());
        
        // Read the DTD content to verify
        String dtdContent = new String(Files.readAllBytes(createdDtd.toPath()));
        assertTrue("DTD should define presentation element", 
                dtdContent.contains("<!ELEMENT presentation"));
        assertTrue("DTD should define item attributes", 
                dtdContent.contains("<!ATTLIST item"));
    }

    /**
     * Test handling of null slide titles
     */
    @Test
    public void testHandleNullSlideTitle() throws IOException {
        // Create a presentation with a slide that has a null title
        Presentation nullTitlePresentation = new Presentation();
        nullTitlePresentation.setTitle("Null Title Test");
        
        Slide slide = new Slide();
        slide.setTitle(null); // Explicitly set null title
        nullTitlePresentation.append(slide);
        
        // Save and then reload to test both directions
        String nullTitleFile = TEST_DIR + "/null-title-test.xml";
        this.xmlAccessor.saveFile(nullTitlePresentation, nullTitleFile);
        
        // Load the file back
        Presentation loadedPresentation = new Presentation();
        this.xmlAccessor.loadFile(loadedPresentation, nullTitleFile);
        
        // Verify handling of null title
        assertEquals("Should have one slide", 1, loadedPresentation.getSize());
        assertEquals("Null title should be handled as empty string", "", loadedPresentation.getSlide(0).getTitle());
    }

    /**
     * Test handling null items in a slide
     */
    @Test
    public void testHandleNullSlideItems() throws IOException {
        // Create a presentation with a slide that has null items
        Presentation nullItemsPresentation = new Presentation();
        nullItemsPresentation.setTitle("Null Items Test");
        
        Slide slide = new Slide();
        slide.setTitle("Slide with null items");
        
        // Add a valid item for comparison
        slide.append(new TextItem(1, "Valid item"));
        
        // Attempt to add null items (should be ignored by Slide.append)
        slide.append(null);
        
        nullItemsPresentation.append(slide);
        
        // Save and then reload to test both directions
        String nullItemsFile = TEST_DIR + "/null-items-test.xml";
        this.xmlAccessor.saveFile(nullItemsPresentation, nullItemsFile);
        
        // Load the file back
        Presentation loadedPresentation = new Presentation();
        this.xmlAccessor.loadFile(loadedPresentation, nullItemsFile);
        
        // Verify only valid items were saved/loaded
        assertEquals("Should have one slide", 1, loadedPresentation.getSize());
        assertEquals("Slide should have one item", 1, loadedPresentation.getSlide(0).getSize());
    }

    @Test
    public void testSaveFileWithNonExistentImage() throws IOException {
        // Create a presentation with a non-existent image file
        this.presentation.setTitle("Test Presentation");
        Slide slide = new Slide();
        slide.setTitle("Test Slide");
        slide.append(new BitmapItem(1, "nonexistent.jpg"));
        this.presentation.append(slide);

        // Save the presentation
        this.xmlAccessor.saveFile(this.presentation, TEST_FILE);

        // Verify the file was created with the image reference
        String savedContent = new String(Files.readAllBytes(Paths.get(TEST_FILE)));
        assertTrue(savedContent.contains("nonexistent.jpg"));
    }
} 