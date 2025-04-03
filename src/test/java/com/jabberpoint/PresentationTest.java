package com.jabberpoint;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Field;

/**
 * Tests for the Presentation class
 */
public class PresentationTest {
    
    private Presentation presentation;
    
    @Before
    public void setUp() {
        presentation = new Presentation();
        presentation.setTitle("Test Presentation");
        
        // Create a slide
        Slide slide = new Slide();
        slide.setTitle("Test Slide 1");
        slide.append(1, "Test Item 1");
        
        // Add the slide to the presentation
        presentation.append(slide);
    }
    
    @Test
    public void testConstructor() {
        Presentation p = new Presentation();
        assertNotNull("Presentation should not be null", p);
        assertEquals("Presentation size should be 0", 0, p.getSize());
    }
    
    @Test
    public void testAppendSlide() {
        Slide slide = new Slide();
        slide.setTitle("Test Slide 2");
        
        int initialSize = presentation.getSize();
        presentation.append(slide);
        
        assertEquals("Presentation size should increase by 1", initialSize + 1, presentation.getSize());
    }
    
    @Test
    public void testGetCurrentSlide() {
        Slide currentSlide = presentation.getCurrentSlide();
        assertNotNull("Current slide should not be null", currentSlide);
        assertEquals("Current slide title should match", "Test Slide 1", currentSlide.getTitle());
    }
    
    @Test
    public void testGetCurrentSlideWithInvalidNumber() {
        // Set an invalid slide number
        presentation.setSlideNumber(999);
        
        // Note: In the actual implementation, if slide number is invalid, the current slide is still returned
        // So we just verify it's not null rather than expecting null
        Slide slide = presentation.getCurrentSlide();
        assertNotNull("Current slide should still be valid even with invalid slide number", slide);
    }
    
    @Test
    public void testGetSlide() {
        Slide slide = presentation.getSlide(0);
        assertNotNull("Slide at index 0 should not be null", slide);
        assertEquals("Slide title should match", "Test Slide 1", slide.getTitle());
    }
    
    @Test
    public void testGetSlideWithInvalidIndex() {
        Slide slide = presentation.getSlide(999);
        assertNull("Slide should be null for invalid index", slide);
    }
    
    @Test
    public void testClear() {
        presentation.clear();
        assertEquals("Presentation size should be 0 after clear", 0, presentation.getSize());
    }
    
    @Test
    public void testObserverManagement() {
        PresentationObserver mockObserver = new PresentationObserver() {
            @Override
            public void onSlideChanged(int slideNumber) {}
            
            @Override
            public void onPresentationChanged() {}
        };
        
        // Add observer
        presentation.addObserver(mockObserver);
        
        // Check if observer was added
        try {
            Field observersField = Presentation.class.getDeclaredField("observers");
            observersField.setAccessible(true);
            List<?> observers = (List<?>) observersField.get(presentation);
            assertTrue("Observer should be in the list", observers.contains(mockObserver));
            
            // Remove observer
            presentation.removeObserver(mockObserver);
            assertFalse("Observer should not be in the list after removal", observers.contains(mockObserver));
        } catch (Exception e) {
            fail("Failed to access observers field: " + e.getMessage());
        }
    }
    
    @Test
    public void testSetTitle() {
        String newTitle = "New Presentation Title";
        presentation.setTitle(newTitle);
        assertEquals("Title should be updated", newTitle, presentation.getTitle());
    }
    
    @Test
    public void testSetSlideNumber() {
        // Add another slide
        Slide slide = new Slide();
        slide.setTitle("Test Slide 2");
        presentation.append(slide);
        
        // Set slide number to 1
        presentation.setSlideNumber(1);
        assertEquals("Slide number should be updated", 1, presentation.getSlideNumber());
        
        // Test with invalid slide number (negative)
        presentation.setSlideNumber(-1);
        assertEquals("Slide number should be set to 0 with invalid negative number", 0, presentation.getSlideNumber());
        
        // Set slide number back to 1 for the next test
        presentation.setSlideNumber(1);
        
        // Test with invalid slide number (too large)
        presentation.setSlideNumber(999);
        assertEquals("Slide number should be set to last slide with invalid large number", 1, presentation.getSlideNumber());
    }
    
    @Test
    public void testNextAndPrevSlide() {
        // Add another slide
        Slide slide = new Slide();
        slide.setTitle("Test Slide 2");
        presentation.append(slide);
        
        // Start with slide 0
        presentation.setSlideNumber(0);
        assertEquals("Current slide number should be 0", 0, presentation.getSlideNumber());
        
        // Go to next slide
        presentation.nextSlide();
        assertEquals("Current slide number should be 1", 1, presentation.getSlideNumber());
        
        // Try to go past the end
        presentation.nextSlide();
        assertEquals("Current slide number should still be 1", 1, presentation.getSlideNumber());
        
        // Go back to previous slide
        presentation.prevSlide();
        assertEquals("Current slide number should be 0", 0, presentation.getSlideNumber());
        
        // Try to go before the beginning
        presentation.prevSlide();
        assertEquals("Current slide number should still be 0", 0, presentation.getSlideNumber());
    }
    
    @Test
    public void testSetSlides() {
        // Create new slides list
        List<Slide> newSlides = new ArrayList<>();
        Slide slide1 = new Slide();
        slide1.setTitle("New Slide 1");
        Slide slide2 = new Slide();
        slide2.setTitle("New Slide 2");
        
        newSlides.add(slide1);
        newSlides.add(slide2);
        
        // Set the new slides
        presentation.setSlides(newSlides);
        
        // Check if slides were updated
        assertEquals("Presentation size should match new slides list size", 2, presentation.getSize());
        assertEquals("First slide title should match", "New Slide 1", presentation.getSlide(0).getTitle());
        assertEquals("Second slide title should match", "New Slide 2", presentation.getSlide(1).getTitle());
    }
    
    @Test
    public void testGetSlides() {
        List<Slide> slides = presentation.getSlides();
        assertNotNull("Slides list should not be null", slides);
        assertEquals("Slides list size should match presentation size", presentation.getSize(), slides.size());
    }
    
    @Test
    public void testGetCurrentSlideNumber() {
        assertEquals("Current slide number should be 0", 0, presentation.getCurrentSlideNumber());
        
        // Change slide number
        presentation.setSlideNumber(0);
        assertEquals("Current slide number should be updated", 0, presentation.getCurrentSlideNumber());
    }
} 