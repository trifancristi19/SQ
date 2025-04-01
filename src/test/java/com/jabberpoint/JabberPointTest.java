package com.jabberpoint;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import java.io.IOException;
import java.awt.GraphicsEnvironment;

public class JabberPointTest {
    
    private static boolean headless;
    
    @Before
    public void setUp() {
        // Check if running in headless mode
        headless = GraphicsEnvironment.isHeadless();
        
        // Always create styles
        Style.createStyles();
    }
    
    @Test
    public void testJabberPointConstants() {
        // Test the constants defined in JabberPoint
        assertEquals("IO Error: ", JabberPoint.IOERR);
        assertEquals("Jabberpoint Error ", JabberPoint.JABERR);
        assertEquals("Jabberpoint 1.6 - OU version", JabberPoint.JABVERSION);
    }
    
    // We can't easily test the main method directly as it creates windows,
    // but we can test parts of it by directly testing the components it uses
    @Test
    public void testDemoAccessorLoad() {
        try {
            Presentation presentation = new Presentation();
            Accessor.getDemoAccessor().loadFile(presentation, "");
            assertTrue("Demo should load successfully", presentation.getSize() > 0);
        } catch (IOException e) {
            fail("Demo loading threw unexpected exception: " + e.getMessage());
        }
    }
    
    @Test
    public void testXMLAccessorLoadNonExistent() {
        Presentation presentation = new Presentation();
        try {
            new XMLAccessor().loadFile(presentation, "nonexistent.xml");
            fail("Should throw IOException for nonexistent file");
        } catch (IOException e) {
            // Expected exception
            assertTrue("Exception should contain file info", e.getMessage().contains("nonexistent.xml"));
        }
    }
} 