package com.jabberpoint;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import static org.junit.Assert.*;

import java.lang.reflect.Method;

/**
 * Tests for the DemoPresentationLoader class
 */
@RunWith(JUnit4.class)
public class DemoPresentationLoaderTest {
    
    private DemoPresentationLoader loader;
    private Presentation presentation;
    
    @Before
    public void setUp() {
        loader = new DemoPresentationLoader();
        presentation = new Presentation();
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