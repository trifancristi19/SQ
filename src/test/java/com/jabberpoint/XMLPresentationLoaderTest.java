package com.jabberpoint;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.lang.reflect.Method;

/**
 * Tests for the XMLPresentationLoader class
 */
@RunWith(JUnit4.class)
public class XMLPresentationLoaderTest {
    
    private XMLPresentationLoader loader;
    private Presentation presentation;
    private final String TEST_DIR = "target/test-resources";
    private final String TEST_FILE = TEST_DIR + "/test-xmlloader.xml";
    private final String TEST_SAVE_FILE = TEST_DIR + "/test-xmlloader-save.xml";
    
    @Before
    public void setUp() throws IOException {
        // Make sure test directory exists
        new File(TEST_DIR).mkdirs();
        
        // Initialize styles for tests
        Style.createStyles();
        
        // Initialize objects
        loader = new XMLPresentationLoader();
        presentation = new Presentation();
        
        // Create a test XML file
        createTestXmlFile();
        
        // Ensure the DTD file exists in test directory
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
        
        File saveFile = new File(TEST_SAVE_FILE);
        if (saveFile.exists()) {
            saveFile.delete();
        }
    }
    
    /**
     * Test the XMLPresentationLoader constructor
     */
    @Test
    public void testConstructor() {
        XMLPresentationLoader newLoader = new XMLPresentationLoader();
        assertNotNull("XMLPresentationLoader should be created successfully", newLoader);
    }
    
    /**
     * Test loadPresentation method with a valid file
     */
    @Test
    public void testLoadPresentationWithValidFile() throws Exception {
        // Load the test file created in setUp
        loader.loadPresentation(presentation, TEST_FILE);
        
        // Verify the presentation was loaded correctly
        assertEquals("Test Presentation", presentation.getTitle());
        assertEquals("Should have one slide", 1, presentation.getSize());
        
        // Verify slide content
        Slide slide = presentation.getSlide(0);
        assertNotNull("Slide should be loaded", slide);
        assertEquals("Test Slide", slide.getTitle());
        assertTrue("Slide should have items", slide.getSize() > 0);
        
        // Verify slide items
        SlideItem item = slide.getSlideItem(0);
        assertTrue("First item should be a TextItem", item instanceof TextItem);
        assertEquals("First item level should be 1", 1, item.getLevel());
        assertEquals("First item text should match", "Test Text Item", ((TextItem)item).getText());
    }
    
    /**
     * Test loadPresentation method with a non-existent file
     */
    @Test(expected = IOException.class)
    public void testLoadPresentationWithNonExistentFile() throws Exception {
        // This should throw an IOException for a non-existent file
        loader.loadPresentation(presentation, "non-existent-file.xml");
    }
    
    /**
     * Test loadPresentation with invalid XML
     */
    @Test(expected = Exception.class)
    public void testLoadPresentationWithInvalidXML() throws Exception {
        // Create a malformed XML file
        String invalidXml = "<?xml version=\"1.0\"?>\n" +
                "<!DOCTYPE presentation SYSTEM \"jabberpoint.dtd\">\n" +
                "<presentation>\n" +
                "<showtitle>Invalid Presentation</showtitle>\n" +
                "<slide>\n" +
                "<title>Invalid Slide</title>\n" +
                "<item kind=\"text\" level=\"1\">Test Item</item>\n" +
                // Missing closing tags
                "</presentation>";
        
        File invalidFile = new File(TEST_DIR + "/invalid.xml");
        Files.write(Paths.get(invalidFile.getPath()), invalidXml.getBytes());
        
        try {
            // This should throw an exception for invalid XML
            loader.loadPresentation(presentation, invalidFile.getPath());
        } finally {
            // Clean up
            invalidFile.delete();
        }
    }
    
    /**
     * Test savePresentation method
     */
    @Test
    public void testSavePresentation() throws Exception {
        // Create a presentation with content
        presentation.setTitle("Save Test Presentation");
        Slide slide = new Slide();
        slide.setTitle("Save Test Slide");
        slide.append(new TextItem(1, "Save Test Text Item"));
        slide.append(new BitmapItem(2, "test.jpg"));
        presentation.append(slide);
        
        // Save the presentation
        loader.savePresentation(presentation, TEST_SAVE_FILE);
        
        // Verify the file was created
        File savedFile = new File(TEST_SAVE_FILE);
        assertTrue("File should be created", savedFile.exists());
        
        // Load the saved file into a new presentation to verify
        Presentation loadedPresentation = new Presentation();
        XMLPresentationLoader newLoader = new XMLPresentationLoader();
        newLoader.loadPresentation(loadedPresentation, TEST_SAVE_FILE);
        
        assertEquals("Title should match", presentation.getTitle(), loadedPresentation.getTitle());
        assertEquals("Size should match", presentation.getSize(), loadedPresentation.getSize());
        
        // Verify slide content
        Slide loadedSlide = loadedPresentation.getSlide(0);
        assertEquals("Slide title should match", slide.getTitle(), loadedSlide.getTitle());
        assertEquals("Slide item count should match", slide.getSize(), loadedSlide.getSize());
        
        // Verify text item
        SlideItem loadedTextItem = loadedSlide.getSlideItem(0);
        assertTrue("First item should be a TextItem", loadedTextItem instanceof TextItem);
        assertEquals("Text item level should match", 1, loadedTextItem.getLevel());
        assertEquals("Text item content should match", "Save Test Text Item", ((TextItem)loadedTextItem).getText());
        
        // Verify bitmap item
        SlideItem loadedBitmapItem = loadedSlide.getSlideItem(1);
        assertTrue("Second item should be a BitmapItem", loadedBitmapItem instanceof BitmapItem);
        assertEquals("Bitmap item level should match", 2, loadedBitmapItem.getLevel());
        assertEquals("Bitmap item filename should match", "test.jpg", ((BitmapItem)loadedBitmapItem).getName());
    }
    
    /**
     * Test savePresentation with an invalid directory
     */
    @Test(expected = IOException.class)
    public void testSavePresentationToInvalidLocation() throws Exception {
        // Create a simple presentation
        presentation.setTitle("Test");
        
        // Try to save to an invalid location that will definitely cause an IOException
        // On Windows, paths with special characters like "?" and "*" will cause an IOException
        // On Unix/Linux, using a path in a directory without write permissions will cause IOException
        String invalidPath;
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            // Windows invalid path
            invalidPath = "\\\\?\\invalid:*" + System.currentTimeMillis() + ".xml";
        } else {
            // Unix/Linux invalid path - trying to write to a root directory without permissions
            invalidPath = "/root/no_permission_" + System.currentTimeMillis() + ".xml";
        }
        
        // This should throw IOException
        loader.savePresentation(presentation, invalidPath);
    }
    
    /**
     * Test loading a file with multiple slides
     */
    @Test
    public void testLoadFileWithMultipleSlides() throws Exception {
        // Create a test file with multiple slides
        String multiSlideXml = "<?xml version=\"1.0\"?>\n" +
                "<!DOCTYPE presentation SYSTEM \"jabberpoint.dtd\">\n" +
                "<presentation>\n" +
                "<showtitle>Multiple Slides</showtitle>\n" +
                "<slide>\n" +
                "<title>Slide 1</title>\n" +
                "<item kind=\"text\" level=\"1\">Slide 1 Text</item>\n" +
                "</slide>\n" +
                "<slide>\n" +
                "<title>Slide 2</title>\n" +
                "<item kind=\"text\" level=\"1\">Slide 2 Text</item>\n" +
                "</slide>\n" +
                "</presentation>";
        
        File multiSlideFile = new File(TEST_DIR + "/multi-slide.xml");
        Files.write(Paths.get(multiSlideFile.getPath()), multiSlideXml.getBytes());
        
        try {
            // Load the multi-slide presentation
            loader.loadPresentation(presentation, multiSlideFile.getPath());
            
            // Verify multiple slides were loaded
            assertEquals("Should have two slides", 2, presentation.getSize());
            assertEquals("First slide title should match", "Slide 1", presentation.getSlide(0).getTitle());
            assertEquals("Second slide title should match", "Slide 2", presentation.getSlide(1).getTitle());
        } finally {
            // Clean up
            multiSlideFile.delete();
        }
    }
    
    /**
     * Test loading a file with missing attributes
     */
    @Test
    public void testLoadFileWithMissingAttributes() throws Exception {
        // Create a test file with missing attributes
        String missingAttrXml = "<?xml version=\"1.0\"?>\n" +
                "<!DOCTYPE presentation SYSTEM \"jabberpoint.dtd\">\n" +
                "<presentation>\n" +
                "<showtitle>Missing Attributes</showtitle>\n" +
                "<slide>\n" +
                "<title>Missing Attributes Slide</title>\n" +
                "<item>Missing attributes item</item>\n" + // Missing kind and level
                "</slide>\n" +
                "</presentation>";
        
        File missingAttrFile = new File(TEST_DIR + "/missing-attr.xml");
        Files.write(Paths.get(missingAttrFile.getPath()), missingAttrXml.getBytes());
        
        try {
            // Load the presentation (should not throw, but skip the invalid item)
            loader.loadPresentation(presentation, missingAttrFile.getPath());
            
            // Verify slide was loaded but item was skipped
            assertEquals("Should have one slide", 1, presentation.getSize());
            assertEquals("Slide title should match", "Missing Attributes Slide", presentation.getSlide(0).getTitle());
            assertEquals("Item should be skipped", 0, presentation.getSlide(0).getSize());
        } finally {
            // Clean up
            missingAttrFile.delete();
        }
    }
    
    /**
     * Test XMLPresentationLoader implements PresentationLoader interface
     */
    @Test
    public void testImplementsPresentationLoader() {
        assertTrue("XMLPresentationLoader should implement PresentationLoader", 
                 loader instanceof PresentationLoader);
    }
    
    /**
     * Test the XMLAccessor is being used internally
     */
    @Test
    public void testXMLAccessorUsage() throws Exception {
        // Verify that the loader uses XMLAccessor by ensuring the same behavior
        
        // Create a simple presentation for comparison
        Presentation loaderPresentation = new Presentation();
        Presentation accessorPresentation = new Presentation();
        
        // Load the same file using both loader and accessor directly
        loader.loadPresentation(loaderPresentation, TEST_FILE);
        XMLAccessor accessor = new XMLAccessor();
        accessor.loadFile(accessorPresentation, TEST_FILE);
        
        // Both should produce the same result
        assertEquals("Titles should match", loaderPresentation.getTitle(), accessorPresentation.getTitle());
        assertEquals("Slide counts should match", loaderPresentation.getSize(), accessorPresentation.getSize());
    }
    
    /**
     * Create a test XML file for loading tests
     */
    private void createTestXmlFile() throws IOException {
        String xmlContent = "<?xml version=\"1.0\"?>\n" +
                "<!DOCTYPE presentation SYSTEM \"jabberpoint.dtd\">\n" +
                "<presentation>\n" +
                "<showtitle>Test Presentation</showtitle>\n" +
                "<slide>\n" +
                "<title>Test Slide</title>\n" +
                "<item kind=\"text\" level=\"1\">Test Text Item</item>\n" +
                "</slide>\n" +
                "</presentation>";
        
        Files.write(Paths.get(TEST_FILE), xmlContent.getBytes());
    }
    
    /**
     * Create a DTD file at the specified path
     */
    private void createDtdFile(String path) throws IOException {
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
    }
} 