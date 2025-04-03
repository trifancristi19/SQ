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
        
        // This should not throw exceptions
        emptyComponent.paintComponent(g);
        g.dispose();
    }
} 