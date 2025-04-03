package com.jabberpoint;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    
    /**
     * Test that created slides have the correct content
     */
    @Test
    public void testDemoSlideContent() throws Exception {
        // Load the demo presentation
        loader.loadPresentation(presentation, "");
        
        // Verify we have the expected number of slides
        assertEquals("Should have 3 slides", 3, presentation.getSize());
        
        // Verify first slide content
        Slide slide1 = presentation.getSlide(0);
        assertEquals("First slide title should be correct", "JabberPoint", slide1.getTitle());
        assertFalse("First slide should have content", slide1.getSize() == 0);
        
        // Check that some slides have TextItems
        boolean hasTextItems = false;
        for (int i = 0; i < presentation.getSize(); i++) {
            Slide slide = presentation.getSlide(i);
            for (int j = 0; j < slide.getSize(); j++) {
                SlideItem item = slide.getSlideItem(j);
                if (item instanceof TextItem) {
                    hasTextItems = true;
                    break;
                }
            }
            if (hasTextItems) break;
        }
        assertTrue("Demo presentation should contain TextItems", hasTextItems);
    }
    
    /**
     * Test loading demo presentation multiple times
     */
    @Test
    public void testLoadMultipleTimes() throws Exception {
        // Clear the presentation first to avoid any existing slides
        presentation.clear();
        
        // Load the demo presentation
        loader.loadPresentation(presentation, "");
        
        // Store the original slide count
        int originalSlideCount = presentation.getSize();
        
        // Clear the presentation before loading again
        presentation.clear();
        
        // Load again
        loader.loadPresentation(presentation, "");
        
        // Verify slide count remains the same (no duplicates)
        assertEquals("Loading twice should not duplicate slides", 
                originalSlideCount, presentation.getSize());
    }
    
    /**
     * Test loading with a pre-populated presentation
     */
    @Test
    public void testLoadWithExistingContent() throws Exception {
        // Add a custom slide first
        Slide customSlide = new Slide();
        customSlide.setTitle("Custom Slide");
        customSlide.append(new TextItem(1, "Custom Text"));
        presentation.append(customSlide);
        
        // Set a custom title
        presentation.setTitle("Custom Title");
        
        // Clear the presentation before loading
        presentation.clear();
        
        // Load the demo presentation (should replace existing content)
        loader.loadPresentation(presentation, "");
        
        // Verify custom slide is gone and replaced with demo content
        assertEquals("Title should be demo title", "Demo Presentation", presentation.getTitle());
        assertEquals("Should have only demo slides", 3, presentation.getSize());
        
        // Ensure our custom slide is not there
        boolean foundCustomSlide = false;
        for (int i = 0; i < presentation.getSize(); i++) {
            if ("Custom Slide".equals(presentation.getSlide(i).getTitle())) {
                foundCustomSlide = true;
                break;
            }
        }
        assertFalse("Custom slide should be gone", foundCustomSlide);
    }
} 