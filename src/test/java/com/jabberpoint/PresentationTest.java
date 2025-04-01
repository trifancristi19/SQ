package com.jabberpoint;

import org.junit.Test;

import static org.junit.Assert.*;

public class PresentationTest
{

    @Test
    public void testPresentationCreation()
    {
        Presentation presentation = new Presentation();
        assertNotNull("Presentation should be created", presentation);
        assertEquals("New presentation should have 0 slides", 0, presentation.getSize());
    }

    @Test
    public void testSetTitle()
    {
        Presentation presentation = new Presentation();
        String testTitle = "Test Presentation";
        presentation.setTitle(testTitle);
        assertEquals("Title should be set correctly", testTitle, presentation.getTitle());
    }

    @Test
    public void testAppendSlide()
    {
        Presentation presentation = new Presentation();
        Slide slide = new Slide();
        presentation.append(slide);
        assertEquals("Presentation should have 1 slide", 1, presentation.getSize());
        assertSame("getSlide should return the appended slide", slide, presentation.getSlide(0));
    }

    @Test
    public void testSlideNavigation()
    {
        Presentation presentation = new Presentation();
        Slide slide1 = new Slide();
        Slide slide2 = new Slide();
        Slide slide3 = new Slide();

        presentation.append(slide1);
        presentation.append(slide2);
        presentation.append(slide3);

        presentation.setSlideNumber(0);
        assertEquals("Current slide should be first slide", 0, presentation.getSlideNumber());
        assertSame("getCurrentSlide should return first slide", slide1, presentation.getCurrentSlide());

        presentation.nextSlide();
        assertEquals("Current slide should be second slide", 1, presentation.getSlideNumber());
        assertSame("getCurrentSlide should return second slide", slide2, presentation.getCurrentSlide());

        presentation.prevSlide();
        assertEquals("Current slide should be first slide again", 0, presentation.getSlideNumber());
        assertSame("getCurrentSlide should return first slide again", slide1, presentation.getCurrentSlide());
    }

    @Test
    public void testNavigationBoundary()
    {
        Presentation presentation = new Presentation();
        Slide slide1 = new Slide();
        presentation.append(slide1);

        presentation.setSlideNumber(0);
        presentation.prevSlide(); // Try to go before first slide
        assertEquals("Should not go before first slide", 0, presentation.getSlideNumber());

        presentation.nextSlide(); // Try to go past last slide
        assertEquals("Should not go past last slide", 0, presentation.getSlideNumber());
    }

    @Test
    public void testClear()
    {
        Presentation presentation = new Presentation();
        Slide slide1 = new Slide();
        Slide slide2 = new Slide();

        presentation.append(slide1);
        presentation.append(slide2);
        presentation.setSlideNumber(1);

        presentation.clear();
        assertEquals("Presentation should have 0 slides after clear", 0, presentation.getSize());
        assertEquals("Slide number should be reset after clear", -1, presentation.getSlideNumber());
    }

    @Test
    public void testGetInvalidSlide()
    {
        Presentation presentation = new Presentation();
        assertNull("Getting negative slide number should return null", presentation.getSlide(-1));
        assertNull("Getting out of bounds slide should return null", presentation.getSlide(0));

        presentation.append(new Slide());
        assertNull("Getting out of bounds slide should return null", presentation.getSlide(1));
    }

    @Test
    public void testSetInvalidSlideNumber() {
        Presentation presentation = new Presentation();
        presentation.append(new Slide());
        presentation.append(new Slide());
        
        // Try to set invalid slide numbers
        presentation.setSlideNumber(-1);
        assertEquals("Should not set negative slide number", 0, presentation.getSlideNumber());
        
        presentation.setSlideNumber(2);
        assertEquals("Should not set slide number beyond bounds", 1, presentation.getSlideNumber());
    }

    @Test
    public void testEmptyPresentationNavigation() {
        Presentation presentation = new Presentation();
        
        // Test navigation on empty presentation
        presentation.nextSlide();
        assertEquals("Should not navigate on empty presentation", -1, presentation.getSlideNumber());
        
        presentation.prevSlide();
        assertEquals("Should not navigate on empty presentation", -1, presentation.getSlideNumber());
        
        assertNull("Should return null for current slide on empty presentation", 
                  presentation.getCurrentSlide());
    }

    @Test
    public void testMultipleSlidesNavigation() {
        Presentation presentation = new Presentation();
        Slide slide1 = new Slide();
        Slide slide2 = new Slide();
        Slide slide3 = new Slide();
        
        presentation.append(slide1);
        presentation.append(slide2);
        presentation.append(slide3);
        
        // Test forward navigation
        presentation.setSlideNumber(0);
        presentation.nextSlide();
        presentation.nextSlide();
        assertEquals("Should reach last slide", 2, presentation.getSlideNumber());
        
        // Test backward navigation
        presentation.prevSlide();
        presentation.prevSlide();
        assertEquals("Should reach first slide", 0, presentation.getSlideNumber());
    }

    @Test
    public void testSlideNumberAfterClear() {
        Presentation presentation = new Presentation();
        presentation.append(new Slide());
        presentation.append(new Slide());
        presentation.setSlideNumber(1);
        
        presentation.clear();
        assertEquals("Slide number should be -1 after clear", -1, presentation.getSlideNumber());
        assertNull("Current slide should be null after clear", presentation.getCurrentSlide());
    }

    @Test
    public void testSlideNumberAfterAppend() {
        Presentation presentation = new Presentation();
        presentation.append(new Slide());
        presentation.setSlideNumber(0);
        
        presentation.append(new Slide());
        assertEquals("Slide number should remain unchanged after append", 0, presentation.getSlideNumber());
    }
} 