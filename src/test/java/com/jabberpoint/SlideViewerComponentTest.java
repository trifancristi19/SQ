package com.jabberpoint;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import javax.swing.JFrame;
import java.awt.Dimension;

public class SlideViewerComponentTest {
    
    private Presentation presentation;
    private JFrame frame;
    private SlideViewerComponent component;
    
    @Before
    public void setUp() {
        presentation = new Presentation();
        frame = new JFrame();
        component = new SlideViewerComponent(presentation, frame);
    }
    
    @Test
    public void testComponentCreation() {
        assertNotNull("SlideViewerComponent should be created", component);
        assertEquals("Background color should be set", java.awt.Color.white, component.getBackground());
    }
    
    @Test
    public void testGetPreferredSize() {
        Dimension size = component.getPreferredSize();
        assertNotNull("Preferred size should be returned", size);
        assertEquals("Width should match Slide.WIDTH", Slide.WIDTH, size.width);
        assertEquals("Height should match Slide.HEIGHT", Slide.HEIGHT, size.height);
    }
    
    @Test
    public void testUpdateWithSlide() {
        // Create a slide
        Slide slide = new Slide();
        slide.setTitle("Test Slide");
        
        // Set a title on the presentation
        presentation.setTitle("Test Presentation");
        
        // Update component with the slide
        component.update(presentation, slide);
        
        // Test that the frame title reflects the presentation title
        assertEquals("Frame title should reflect presentation title", 
                    presentation.getTitle(), frame.getTitle());
    }
    
    @Test
    public void testUpdateWithNull() {
        // This just checks that no exception is thrown
        component.update(presentation, null);
        // If we get here, the test passes
        assertTrue(true);
    }
    
    @Test
    public void testFullPresentationFlow() {
        // Create a presentation with multiple slides
        Presentation testPresentation = new Presentation();
        testPresentation.setTitle("Test Presentation");
        
        Slide slide1 = new Slide();
        slide1.setTitle("Slide 1");
        testPresentation.append(slide1);
        
        Slide slide2 = new Slide();
        slide2.setTitle("Slide 2");
        testPresentation.append(slide2);
        
        // Set up the viewer
        SlideViewerComponent viewer = new SlideViewerComponent(testPresentation, frame);
        testPresentation.setShowView(viewer);
        
        // Navigate through slides
        testPresentation.setSlideNumber(0);
        assertEquals("Current slide should be slide 1", 0, testPresentation.getSlideNumber());
        
        testPresentation.nextSlide();
        assertEquals("Current slide should be slide 2", 1, testPresentation.getSlideNumber());
        
        testPresentation.prevSlide();
        assertEquals("Current slide should be slide 1 again", 0, testPresentation.getSlideNumber());
    }
} 