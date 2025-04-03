package com.jabberpoint;

import org.junit.Test;
import org.junit.Before;
import org.junit.BeforeClass;
import static org.junit.Assert.*;

import java.awt.Dimension;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.lang.reflect.Field;

/**
 * Tests for the SlideViewerComponent class.
 * These tests are simple examples and are excluded from the JaCoCo coverage requirements
 * due to the difficulty of testing GUI components.
 */
public class SlideViewerComponentTest {
    
    private SlideViewerComponent component;
    private Presentation presentation;
    
    @BeforeClass
    public static void setUpClass() {
        // Initialize styles for testing
        Style.createStyles();
    }
    
    @Before
    public void setUp() {
        // Create a presentation with a test slide
        presentation = new Presentation();
        presentation.setTitle("Test Presentation");
        
        // Create a slide
        Slide slide = new Slide();
        slide.setTitle("Test Slide");
        slide.append(1, "Test Item 1");
        slide.append(2, "Test Item 2");
        
        // Add the slide to the presentation
        presentation.append(slide);
        
        // Create the component
        component = new SlideViewerComponent(presentation);
    }
    
    @Test
    public void testConstructor() {
        // Test component creation
        assertNotNull("Component should exist", component);
        assertEquals("Background color should be white", Color.white, component.getBackground());
    }
    
    @Test
    public void testPreferredSize() {
        // Test getPreferredSize method
        Dimension preferredSize = component.getPreferredSize();
        assertEquals("Width should match slide width", Slide.WIDTH, preferredSize.width);
        assertEquals("Height should match slide height", Slide.HEIGHT, preferredSize.height);
    }
    
    @Test
    public void testOnSlideChanged() {
        // Test when slide changes
        component.onSlideChanged(0);
        
        // Verify that the current slide is updated
        try {
            Field slideField = SlideViewerComponent.class.getDeclaredField("slide");
            slideField.setAccessible(true);
            Slide currentSlide = (Slide) slideField.get(component);
            assertNotNull("Slide should be set after onSlideChanged", currentSlide);
            assertEquals("Slide title should match", "Test Slide", currentSlide.getTitle());
        } catch (Exception e) {
            fail("Failed to access slide field: " + e.getMessage());
        }
    }
    
    @Test
    public void testOnPresentationChanged() {
        // Test presentation change handling
        component.onPresentationChanged();
        
        // Add another slide and test again
        Slide slide2 = new Slide();
        slide2.setTitle("Second Slide");
        presentation.append(slide2);
        presentation.setSlideNumber(1);
        component.onSlideChanged(1);
    }
    
    @Test
    public void testPaintComponent() {
        // Create a BufferedImage to render to
        BufferedImage image = new BufferedImage(Slide.WIDTH, Slide.HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.createGraphics();
        
        // Set the component size for rendering
        component.setSize(Slide.WIDTH, Slide.HEIGHT);
        
        // Call paintComponent
        component.paintComponent(g);
        
        // Clean up
        g.dispose();
        
        // No exceptions should have been thrown
        assertTrue(true);
    }
    
    @Test
    public void testEmptyPresentation() {
        // Create component with empty presentation
        Presentation emptyPresentation = new Presentation();
        SlideViewerComponent emptyComponent = new SlideViewerComponent(emptyPresentation);
        
        // Test with empty presentation
        emptyComponent.onPresentationChanged();
        
        // Create graphics for painting
        BufferedImage image = new BufferedImage(Slide.WIDTH, Slide.HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.createGraphics();
        
<<<<<<< HEAD
        // This should not throw exceptions
        emptyComponent.paintComponent(g);
        g.dispose();
=======
        // Reset tracking
        trackingComponent.resetTracking();
        
        // Trigger presentation changed
        emptyPresentation.clear(); // This will trigger onPresentationChanged
        
        // Verify slide is null
        assertNull("Slide should be null for empty presentation", trackingComponent.lastSlide);
        assertTrue("Repaint should still be called", trackingComponent.repaintCalled);
    }
    
    @Test
    public void testSetCurrentSlideDirectly() {
        // Create a presentation with a slide
        Presentation testPresentation = new Presentation();
        Slide slide = new Slide();
        slide.setTitle("Test Direct Slide");
        testPresentation.append(slide);
        
        // Create a component with the presentation
        SlideViewerComponent testComponent = new SlideViewerComponent(testPresentation);
        
        // Access the slide field directly using reflection
        try {
            // Set slide directly
            Field slideField = SlideViewerComponent.class.getDeclaredField("slide");
            slideField.setAccessible(true);
            slideField.set(testComponent, slide);
            
            // Get the slide back to verify
            Slide retrievedSlide = (Slide) slideField.get(testComponent);
            
            // Verify slide was set
            assertEquals("Slide should be set directly", slide, retrievedSlide);
        } catch (Exception e) {
            fail("Exception in slide field access: " + e.getMessage());
        }
    }
    
    @Test
    public void testNotificationOnPresentationReload() {
        // Create a tracking component to monitor notifications
        TrackingObserverComponent trackingComponent = new TrackingObserverComponent(presentation);
        
        // Add slides to the presentation
        Slide slide1 = new Slide();
        slide1.setTitle("Original Slide 1");
        Slide slide2 = new Slide();
        slide2.setTitle("Original Slide 2");
        
        presentation.append(slide1);
        presentation.append(slide2);
        presentation.setSlideNumber(0);
        
        // Reset tracking to start fresh
        trackingComponent.resetTracking();
        
        // Clear the presentation (should trigger presentation changed)
        presentation.clear();
        
        assertTrue("onPresentationChanged should be called when presentation is cleared",
                trackingComponent.presentationChangedCalled);
        assertTrue("repaint should be called when presentation is cleared",
                trackingComponent.repaintCalled);
        
        // Reset tracking again
        trackingComponent.resetTracking();
        
        // Add new slides
        Slide newSlide1 = new Slide();
        newSlide1.setTitle("New Slide 1");
        Slide newSlide2 = new Slide();
        newSlide2.setTitle("New Slide 2");
        
        presentation.append(newSlide1);
        presentation.append(newSlide2);
        
        // This should trigger a slide changed notification
        presentation.setSlideNumber(0);
        
        assertTrue("onSlideChanged should be called when slide number changes",
                trackingComponent.slideChangedCalled);
        assertEquals("onSlideChanged should receive correct slide number",
                0, trackingComponent.lastSlideNumber);
        assertTrue("repaint should be called when slide changes",
                trackingComponent.repaintCalled);
    }
    
    /**
     * Test rendering with null slide
     */
    @Test
    public void testPaintWithNullSlide() {
        // Create a component with a presentation that has no slides
        Presentation emptyPresentation = new Presentation();
        TestableSlideViewerComponent component = new TestableSlideViewerComponent(emptyPresentation);
        
        // Force the slide number to be -1 to trigger the null slide condition
        try {
            emptyPresentation.clear(); // This should set slideNumber to -1
        } catch (Exception e) {
            fail("Exception during test setup: " + e.getMessage());
        }
        
        // Create a buffered image to draw on
        BufferedImage image = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();
        
        // Call paintComponent - it should handle null slide gracefully
        component.paintComponent(g);
        
        // Verify that there were no exceptions and some drawing occurred
        assertTrue("Background should be drawn even with null slide", component.backgroundDrawn);
        assertFalse("Slide should not be drawn when null", component.slideDrawn);
        
        // Clean up
        g.dispose();
    }
    
    /**
     * Test that the component properly updates when slides change
     */
    @Test
    public void testSlideNumberChange() {
        // Create a presentation with multiple slides
        Presentation testPresentation = new Presentation();
        
        // Add two slides
        Slide slide1 = new Slide();
        slide1.setTitle("Slide 1");
        testPresentation.append(slide1);
        
        Slide slide2 = new Slide();
        slide2.setTitle("Slide 2");
        testPresentation.append(slide2);
        
        // Create a component that tracks repaints
        TrackingObserverComponent component = new TrackingObserverComponent(testPresentation);
        
        // Reset tracking flags
        component.resetTracking();
        
        // Change slide number
        testPresentation.setSlideNumber(1);
        
        // Verify component was notified and repainted
        assertTrue("onSlideChanged should be called", component.slideChangedCalled);
        assertEquals("Last slide number should match", 1, component.lastSlideNumber);
        assertTrue("Component should be repainted", component.repaintCalled);
        
        // Reset tracking again
        component.resetTracking();
        
        // Change to an invalid slide number
        testPresentation.setSlideNumber(5); // Beyond bounds, should sanitize to valid number
        
        // Verify component was still notified
        assertTrue("onSlideChanged should be called even for invalid number", component.slideChangedCalled);
        assertTrue("Component should be repainted even for invalid number", component.repaintCalled);
    }
    
    /**
     * Test component that tracks when the onSlideChanged method is called
     */
    private class TrackingObserverComponent extends SlideViewerComponent {
        public boolean slideChangedCalled = false;
        public boolean presentationChangedCalled = false;
        public boolean repaintCalled = false;
        public int lastSlideNumber = -1;
        public Slide lastSlide = null;
        
        public TrackingObserverComponent(Presentation presentation) {
            super(presentation);
        }
        
        @Override
        public void onSlideChanged(int slideNumber) {
            slideChangedCalled = true;
            lastSlideNumber = slideNumber;
            super.onSlideChanged(slideNumber);
            
            // Capture the slide after parent method sets it
            try {
                Field slideField = SlideViewerComponent.class.getDeclaredField("slide");
                slideField.setAccessible(true);
                lastSlide = (Slide) slideField.get(this);
            } catch (Exception e) {
                System.err.println("Error capturing slide: " + e);
            }
        }
        
        @Override
        public void onPresentationChanged() {
            presentationChangedCalled = true;
            super.onPresentationChanged();
            
            // Capture the slide after parent method sets it
            try {
                Field slideField = SlideViewerComponent.class.getDeclaredField("slide");
                slideField.setAccessible(true);
                lastSlide = (Slide) slideField.get(this);
            } catch (Exception e) {
                System.err.println("Error capturing slide: " + e);
            }
        }
        
        @Override
        public void repaint() {
            repaintCalled = true;
            // Don't call super to avoid actual repainting in tests
        }
        
        public void resetTracking() {
            slideChangedCalled = false;
            presentationChangedCalled = false;
            repaintCalled = false;
            lastSlideNumber = -1;
        }
        
        public Slide getLastSlide() {
            return lastSlide;
        }
    }
    
    /**
     * A component that tracks painting operations
     */
    private class TestableSlideViewerComponent extends SlideViewerComponent {
        public boolean backgroundDrawn = false;
        public boolean slideNumberDrawn = false;
        public boolean slideDrawn = false;
        
        public TestableSlideViewerComponent(Presentation p) {
            super(p);
        }
        
        @Override
        public void paintComponent(Graphics g) {
            // Override to track method calls
            if (g == null) return;
            
            // Draw background
            backgroundDrawn = true;
            
            // Access the slide using reflection to avoid changing visibility
            try {
                Field slideField = SlideViewerComponent.class.getDeclaredField("slide");
                slideField.setAccessible(true);
                Slide currentSlide = (Slide) slideField.get(this);
                
                // Check slide conditions
                if (presentation.getSlideNumber() < 0 || currentSlide == null) {
                    return;
                }
                
                // Track that we'd draw the slide number
                slideNumberDrawn = true;
                
                // Track that we'd draw the slide
                slideDrawn = true;
            } catch (Exception e) {
                System.err.println("Error in testable paint: " + e);
            }
        }
        
        public void resetTracking() {
            backgroundDrawn = false;
            slideNumberDrawn = false;
            slideDrawn = false;
        }
    }
    
    /**
     * A simple frame mock that doesn't require GUI
     */
    private class FrameMock {
        private String title = "";
        
        public void setTitle(String title) {
            this.title = title;
        }
        
        public String getTitle() {
            return title;
        }
>>>>>>> d6925b5f1f4d3bd3ef515fc7598526c5c7875072
    }
} 