package com.jabberpoint;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.io.IOException;

/**
 * Tests for the DemoPresentationLoader class
 */
public class DemoPresentationLoaderTest {
    
    private DemoPresentationLoader loader;
    private Presentation presentation;
    
    @Before
    public void setUp() {
        // Initialize objects for testing
        loader = new DemoPresentationLoader();
        presentation = new Presentation();
        
        // Initialize styles for tests
        Style.createStyles();
    }
    
    @After
    public void tearDown() {
        loader = null;
        presentation = null;
    }
    
    /**
     * Test the DemoPresentationLoader constructor
     */
    @Test
    public void testConstructor() {
        assertNotNull("DemoPresentationLoader should be created successfully", loader);
    }
    
    /**
     * Test loadPresentation method
     */
    @Test
    public void testLoadPresentationMethod() {
        try {
            // Verify method signature
            Method loadMethod = DemoPresentationLoader.class.getDeclaredMethod("loadPresentation", Presentation.class, String.class);
            assertNotNull("loadPresentation method should exist", loadMethod);
            
            // Test loading the demo presentation
            loader.loadPresentation(presentation, "");
            
            // Verify presentation was loaded
            assertFalse("Presentation should not be empty", presentation.getSize() == 0);
            assertNotNull("Presentation title should be set", presentation.getTitle());
            
        } catch (Exception e) {
            fail("Exception in loadPresentation: " + e.getMessage());
        }
    }
    
    /**
     * Test DemoPresentationLoader implements PresentationLoader
     */
    @Test
    public void testImplementsPresentationLoader() {
        assertTrue("DemoPresentationLoader should implement PresentationLoader", 
                 loader instanceof PresentationLoader);
    }
    
    /**
     * Test savePresentation method throws UnsupportedOperationException
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testSavePresentationThrowsException() throws Exception {
        // This should throw UnsupportedOperationException
        loader.savePresentation(presentation, "test.xml");
    }
} 