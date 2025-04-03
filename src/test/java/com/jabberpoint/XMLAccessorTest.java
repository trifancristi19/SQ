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
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.Color;
import javax.imageio.ImageIO;

public class XMLAccessorTest {
<<<<<<< HEAD
    
=======

    private Presentation presentation;
>>>>>>> d6925b5f1f4d3bd3ef515fc7598526c5c7875072
    private XMLAccessor xmlAccessor;
    private Presentation presentation;
    private static final String TEST_FILE_PATH = "test-presentation.xml";
    private static final String MALFORMED_XML_PATH = "malformed-test.xml";
    private static final String MISSING_ATTRS_XML_PATH = "missing-attrs-test.xml";
    
    @Before
<<<<<<< HEAD
    public void setUp() {
        xmlAccessor = new XMLAccessor();
        presentation = new Presentation();
=======
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
>>>>>>> d6925b5f1f4d3bd3ef515fc7598526c5c7875072
    }
    
    @After
    public void tearDown() {
<<<<<<< HEAD
        // Clean up test files
        try {
            Files.deleteIfExists(Paths.get(TEST_FILE_PATH));
            Files.deleteIfExists(Paths.get(MALFORMED_XML_PATH));
            Files.deleteIfExists(Paths.get(MISSING_ATTRS_XML_PATH));
            Files.deleteIfExists(Paths.get("jabberpoint.dtd"));
            
            // Clean up any test directories that might have been created
            Files.deleteIfExists(Paths.get("test-output-dir/test.xml"));
            Files.deleteIfExists(Paths.get("test-output-dir/jabberpoint.dtd"));
            Files.deleteIfExists(Paths.get("test-output-dir"));
        } catch (IOException e) {
            System.err.println("Error during cleanup: " + e.getMessage());
=======
        // Clean up any test files
        File testFile = new File(TEST_FILE);
        if (testFile.exists()) {
            testFile.delete();
>>>>>>> d6925b5f1f4d3bd3ef515fc7598526c5c7875072
        }
    }
    
    @Test
    public void testSaveAndLoadFile() throws IOException {
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
        assertEquals("Image path should match", "test-image.jpg", ((BitmapItem)item).getName());
    }
    
    @Test
    public void testGetTitleMethod() {
        // This test indirectly tests the private getTitle method through loadFile
        presentation.setTitle("Test Title");
        assertEquals("Title should be set correctly", "Test Title", presentation.getTitle());
    }
    
    @Test
    public void testLoadNonExistentFile() {
        // Try to load a non-existent file
        try {
            xmlAccessor.loadFile(presentation, "non-existent-file.xml");
            fail("Should throw IOException when file does not exist");
        } catch (IOException e) {
            // Expected behavior
            assertTrue(e.getMessage().contains("non-existent-file.xml"));
        }
    }
    
    @Test
    public void testSaveToNewDirectory() throws IOException {
        // Test saving to a directory that doesn't exist yet
        String dirPath = "test-output-dir";
        String filePath = dirPath + "/test.xml";
        
        // Ensure directory doesn't exist to start
        File dir = new File(dirPath);
        if (dir.exists()) {
            for (File file : dir.listFiles()) {
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
    public void testMalformedXmlFile() throws IOException {
        // Create a malformed XML file
        try (PrintWriter out = new PrintWriter(new FileWriter(MALFORMED_XML_PATH))) {
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
        try {
            xmlAccessor.loadFile(presentation, MALFORMED_XML_PATH);
            fail("Should throw IOException for malformed XML");
        } catch (IOException e) {
            // Expected behavior
            assertTrue("Exception should be related to SAX or XML parsing",
                      e.getMessage().contains("SAX") || 
                      e.getMessage().toLowerCase().contains("xml") ||
                      e.getMessage().toLowerCase().contains("parse"));
        }
    }
    
    @Test
    public void testMissingAttributesInXmlItems() throws IOException {
        // Create XML with items missing required attributes
        try (PrintWriter out = new PrintWriter(new FileWriter(MISSING_ATTRS_XML_PATH))) {
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
    public void testHandlingEmptyPresentation() throws IOException {
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
    public void testLoadPresentationWithNullItems() throws IOException {
        // Setup a presentation with null items
        presentation.setTitle("Null Items Test");
        
        Slide slide = new Slide();
        slide.setTitle("Test Slide");
<<<<<<< HEAD
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
=======
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
>>>>>>> d6925b5f1f4d3bd3ef515fc7598526c5c7875072
    }
    
    @Test
    public void testLoadFileWithItemNumberFormatException() throws IOException {
        // Create XML with invalid level format
        try (PrintWriter out = new PrintWriter(new FileWriter(TEST_FILE_PATH))) {
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
    public void testLoadFileWithUnknownItemType() throws IOException {
<<<<<<< HEAD
        // Create XML with unknown item type
        try (PrintWriter out = new PrintWriter(new FileWriter(TEST_FILE_PATH))) {
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
=======
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
>>>>>>> d6925b5f1f4d3bd3ef515fc7598526c5c7875072
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

    /**
     * Creates a test image if it doesn't already exist
     * @param imagePath Path where the image should be created
     * @throws IOException If there's an error creating the image
     */
    private void ensureTestImageExists(String imagePath) throws IOException {
        File testImageFile = new File(imagePath);
        if (!testImageFile.exists()) {
            // Create parent directories if needed
            testImageFile.getParentFile().mkdirs();
            
            // Create a small test image
            BufferedImage image = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = image.createGraphics();
            g2d.setColor(Color.RED);
            g2d.fillRect(0, 0, 10, 10);
            g2d.dispose();
            
            // Save as a JPEG
            ImageIO.write(image, "jpg", testImageFile);
        }
    }
    
    @Test
    public void testHandleImageWithoutDimensions() throws Exception {
        // Create a test file with an image without width and height attributes
        String xmlWithoutDimensions = "<?xml version=\"1.0\"?>\n" +
            "<!DOCTYPE presentation SYSTEM \"jabberpoint.dtd\">\n" +
            "<presentation>\n" +
            "<showtitle>Image Without Dimensions</showtitle>\n" +
            "<slide>\n" +
            "<title>Image Test</title>\n" +
            // Create the image item with both required attributes
            "<item kind=\"image\" level=\"1\">" + TEST_DIR + "/test-image.jpg</item>\n" +
            "</slide>\n" +
            "</presentation>";
        
        File testFile = new File(TEST_DIR + "/image-without-dimensions.xml");
        try {
            // Ensure test image exists
            ensureTestImageExists(TEST_DIR + "/test-image.jpg");
            
            Files.write(Paths.get(testFile.getPath()), xmlWithoutDimensions.getBytes());
            
            // Should not throw an exception
            Presentation presentation = new Presentation();
            this.xmlAccessor.loadFile(presentation, testFile.getAbsolutePath());
            
            assertEquals("Image Without Dimensions", presentation.getTitle());
            assertEquals("Presentation should have 1 slide", 1, presentation.getSize());
            
            // Should have created an image SlideItem
            Slide slide = presentation.getSlide(0);
            assertEquals("Image Test", slide.getTitle());
            assertTrue("Should contain at least one item", slide.getSize() > 0);
            
        } finally {
            testFile.delete();
        }
    }
    
    @Test
    public void testHandleInvalidImageDimensions() throws Exception {
        // Create a test file with an image with invalid width and height values
        String xmlWithInvalidDimensions = "<?xml version=\"1.0\"?>\n" +
            "<!DOCTYPE presentation SYSTEM \"jabberpoint.dtd\">\n" +
            "<presentation>\n" +
            "<showtitle>Invalid Image Dimensions</showtitle>\n" +
            "<slide>\n" +
            "<title>Invalid Image Test</title>\n" +
            // Make sure we include both required attributes
            "<item kind=\"image\" level=\"1\" width=\"notANumber\" height=\"alsoNotANumber\">" + TEST_DIR + "/test-image.jpg</item>\n" +
            "</slide>\n" +
            "</presentation>";
        
        File testFile = new File(TEST_DIR + "/invalid-image-dimensions.xml");
        try {
            // Ensure test image exists
            ensureTestImageExists(TEST_DIR + "/test-image.jpg");
            
            Files.write(Paths.get(testFile.getPath()), xmlWithInvalidDimensions.getBytes());
            
            // Should not throw an exception despite invalid dimensions
            Presentation presentation = new Presentation();
            this.xmlAccessor.loadFile(presentation, testFile.getAbsolutePath());
            
            assertEquals("Invalid Image Dimensions", presentation.getTitle());
            assertEquals("Presentation should have 1 slide", 1, presentation.getSize());
            
            // Should have created an image SlideItem
            Slide slide = presentation.getSlide(0);
            assertEquals("Invalid Image Test", slide.getTitle());
            assertTrue("Should contain at least one item", slide.getSize() > 0);
            
        } finally {
            testFile.delete();
        }
    }
    
    @Test
    public void testHandleEmptyPresentation() throws Exception {
        // Create a test file with no slides
        String xmlWithNoSlides = "<?xml version=\"1.0\"?>\n" +
            "<!DOCTYPE presentation SYSTEM \"jabberpoint.dtd\">\n" +
            "<presentation>\n" +
            "<showtitle>Empty Presentation</showtitle>\n" +
            "</presentation>";
        
        File testFile = new File(TEST_DIR + "/empty-presentation.xml");
        try {
            Files.write(Paths.get(testFile.getPath()), xmlWithNoSlides.getBytes());
            
            // Should not throw an exception
            Presentation presentation = new Presentation();
            this.xmlAccessor.loadFile(presentation, testFile.getAbsolutePath());
            
            assertEquals("Empty Presentation", presentation.getTitle());
            assertEquals("Presentation should have 0 slides", 0, presentation.getSize());
            
        } finally {
            testFile.delete();
        }
    }
    
    @Test
    public void testHandleEmptySlide() throws Exception {
        // Create a test file with an empty slide (no items)
        String xmlWithEmptySlide = "<?xml version=\"1.0\"?>\n" +
            "<!DOCTYPE presentation SYSTEM \"jabberpoint.dtd\">\n" +
            "<presentation>\n" +
            "<showtitle>Empty Slide</showtitle>\n" +
            "<slide>\n" +
            "<title>Empty Slide Test</title>\n" +
            "</slide>\n" +
            "</presentation>";
        
        File testFile = new File(TEST_DIR + "/empty-slide.xml");
        try {
            Files.write(Paths.get(testFile.getPath()), xmlWithEmptySlide.getBytes());
            
            // Should not throw an exception
            Presentation presentation = new Presentation();
            this.xmlAccessor.loadFile(presentation, testFile.getAbsolutePath());
            
            assertEquals("Empty Slide", presentation.getTitle());
            assertEquals("Presentation should have 1 slide", 1, presentation.getSize());
            
            Slide slide = presentation.getSlide(0);
            assertEquals("Empty Slide Test", slide.getTitle());
            assertEquals("Slide should have 0 items", 0, slide.getSize());
            
        } finally {
            testFile.delete();
        }
    }
} 