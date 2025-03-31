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
        presentation = new Presentation();
        xmlAccessor = new XMLAccessor();
        
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
    public void testLoadFile() throws IOException {
        // Create a test file or use an existing one
        createTestXmlFile();
        
        // Load the file
        xmlAccessor.loadFile(presentation, TEST_FILE);
        
        // Verify the presentation was loaded correctly
        assertEquals("Test Presentation", presentation.getTitle());
        assertTrue("Presentation should have at least one slide", presentation.getSize() > 0);
        
        Slide slide = presentation.getSlide(0);
        assertNotNull("Slide should be loaded", slide);
        assertEquals("Test Slide", slide.getTitle());
        assertTrue("Slide should have items", slide.getSize() > 0);
    }
    
    @Test
    public void testSaveFile() throws IOException {
        // Create a presentation
        presentation.setTitle("Test Presentation");
        Slide slide = new Slide();
        slide.setTitle("Test Slide");
        slide.append(new TextItem(1, "Test Text Item"));
        presentation.append(slide);
        
        // Save the presentation
        xmlAccessor.saveFile(presentation, TEST_FILE);
        
        // Verify the file was created
        File savedFile = new File(TEST_FILE);
        assertTrue("File should be created", savedFile.exists());
        
        // Load the saved file into a new presentation to verify
        Presentation loadedPresentation = new Presentation();
        xmlAccessor.loadFile(loadedPresentation, TEST_FILE);
        
        assertEquals("Title should match", presentation.getTitle(), loadedPresentation.getTitle());
        assertEquals("Size should match", presentation.getSize(), loadedPresentation.getSize());
    }
    
    // We don't test loading invalid files since it could behave differently based on environment
    
    // Helper method to create a test XML file
    private void createTestXmlFile() throws IOException {
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
    private void createDtdFile(String path) throws IOException {
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