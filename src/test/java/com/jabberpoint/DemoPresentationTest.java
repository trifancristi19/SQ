package com.jabberpoint;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.io.IOException;

public class DemoPresentationTest {
    
    private DemoPresentation demoPresentation;
    private Presentation presentation;
    
    @Before
    public void setUp() {
        demoPresentation = new DemoPresentation();
        presentation = new Presentation();
    }
    
    @Test
    public void testLoadFile() {
        // Load the demo presentation
        demoPresentation.loadFile(presentation, "");
        
        // Verify the presentation was populated
        assertEquals("Demo Presentation", presentation.getTitle());
        assertEquals(3, presentation.getSize());
        
        // Check the first slide
        Slide firstSlide = presentation.getSlide(0);
        assertEquals("JabberPoint", firstSlide.getTitle());
        assertEquals(10, firstSlide.getSlideItems().size());
        
        // Verify specific content in the first slide
        assertTrue(firstSlide.getSlideItems().get(0).toString().contains("The Java Presentation Tool"));
        assertTrue(firstSlide.getSlideItems().get(6).toString().contains("Navigate:"));
        assertTrue(firstSlide.getSlideItems().get(7).toString().contains("Next slide: PgDn or Enter"));
        assertTrue(firstSlide.getSlideItems().get(8).toString().contains("Previous slide: PgUp or up-arrow"));
        assertTrue(firstSlide.getSlideItems().get(9).toString().contains("Quit: q or Q"));
        
        // Check the second slide
        Slide secondSlide = presentation.getSlide(1);
        assertEquals("Demonstration of levels and stijlen", secondSlide.getTitle());
        assertEquals(7, secondSlide.getSlideItems().size());
        
        // Verify specific content and levels in the second slide
        SlideItem item = secondSlide.getSlideItems().get(0);
        assertEquals(1, item.getLevel());
        assertTrue(item.toString().contains("Level 1"));
        
        item = secondSlide.getSlideItems().get(1);
        assertEquals(2, item.getLevel());
        assertTrue(item.toString().contains("Level 2"));
        
        item = secondSlide.getSlideItems().get(5);
        assertEquals(3, item.getLevel());
        assertTrue(item.toString().contains("level 3"));
        
        item = secondSlide.getSlideItems().get(6);
        assertEquals(4, item.getLevel());
        assertTrue(item.toString().contains("level 4"));
        
        // Check the third slide
        Slide thirdSlide = presentation.getSlide(2);
        assertEquals("The third slide", thirdSlide.getTitle());
        assertEquals(5, thirdSlide.getSlideItems().size());
        
        // Verify bitmap item in the third slide
        SlideItem bitmapItem = thirdSlide.getSlideItems().get(4);
        assertTrue(bitmapItem instanceof BitmapItem);
        assertTrue(bitmapItem.toString().contains("JabberPoint.gif"));
        assertEquals(1, bitmapItem.getLevel());
    }
    
    @Test(expected = IllegalStateException.class)
    public void testSaveFile() {
        // Saving the demo presentation should throw an IllegalStateException
        demoPresentation.saveFile(presentation, "demo.xml");
        // Test should pass if the expected exception is thrown
    }
    
    @Test
    public void testDemoPresentationAccessorPattern() {
        try {
            // Test the accessor pattern by getting the demo presenter through the factory method
            Accessor accessor = Accessor.getDemoAccessor();
            assertNotNull("Demo accessor should not be null", accessor);
            assertTrue("Should get a DemoPresentation instance", accessor instanceof DemoPresentation);
            
            // Load using the accessor
            Presentation testPresentation = new Presentation();
            accessor.loadFile(testPresentation, "");
            
            // Verify it loads correctly
            assertEquals("Demo Presentation", testPresentation.getTitle());
            assertEquals(3, testPresentation.getSize());
        } catch (IOException e) {
            fail("Should not throw IOException: " + e.getMessage());
        }
    }
    
    @Test
    public void testSlideContentDetails() {
        // Load the demo presentation
        demoPresentation.loadFile(presentation, "");
        
        // Get the first slide and check detailed content
        Slide slide = presentation.getSlide(0);
        
        // Check specific items from the first slide
        SlideItem item = slide.getSlideItems().get(0);
        assertEquals(1, item.getLevel());
        assertTrue(item.toString().contains("The Java Presentation Tool"));
        
        item = slide.getSlideItems().get(1);
        assertEquals(2, item.getLevel());
        assertTrue(item.toString().contains("Copyright (c) 1996-2000: Ian Darwin"));
        
        item = slide.getSlideItems().get(2);
        assertEquals(2, item.getLevel());
        assertTrue(item.toString().contains("Copyright (c) 2000-now:"));
        
        // Check the third slide's file reference
        Slide thirdSlide = presentation.getSlide(2);
        SlideItem lastItem = thirdSlide.getSlideItems().get(4);
        assertTrue(lastItem instanceof BitmapItem);
        BitmapItem bitmapItem = (BitmapItem) lastItem;
        assertEquals("JabberPoint.gif", bitmapItem.getName());
    }
    
    @Test
    public void testLoadFileComprehensively() {
        // Create a fresh presentation
        Presentation presentation = new Presentation();
        
        // Load the demo content
        demoPresentation.loadFile(presentation, "any-filename-is-ignored");
        
        // Verify the overall presentation properties
        assertEquals("Demo Presentation", presentation.getTitle());
        assertEquals(3, presentation.getSize());
        
        // Verify each slide in detail
        // First slide
        Slide slide1 = presentation.getSlide(0);
        assertEquals("JabberPoint", slide1.getTitle());
        assertEquals(10, slide1.getSlideItems().size());
        
        // Verify every item in the first slide
        TextItem item1 = (TextItem) slide1.getSlideItems().get(0);
        assertEquals(1, item1.getLevel());
        assertEquals("The Java Presentation Tool", item1.getText());
        
        TextItem item2 = (TextItem) slide1.getSlideItems().get(1);
        assertEquals(2, item2.getLevel());
        assertEquals("Copyright (c) 1996-2000: Ian Darwin", item2.getText());
        
        TextItem item3 = (TextItem) slide1.getSlideItems().get(2);
        assertEquals(2, item3.getLevel());
        assertEquals("Copyright (c) 2000-now:", item3.getText());
        
        TextItem item4 = (TextItem) slide1.getSlideItems().get(3);
        assertEquals(2, item4.getLevel());
        assertEquals("Gert Florijn andn Sylvia Stuurman", item4.getText());
        
        TextItem item5 = (TextItem) slide1.getSlideItems().get(4);
        assertEquals(4, item5.getLevel());
        assertEquals("Starting JabberPoint without a filename", item5.getText());
        
        TextItem item6 = (TextItem) slide1.getSlideItems().get(5);
        assertEquals(4, item6.getLevel());
        assertEquals("shows this presentation", item6.getText());
        
        TextItem item7 = (TextItem) slide1.getSlideItems().get(6);
        assertEquals(1, item7.getLevel());
        assertEquals("Navigate:", item7.getText());
        
        TextItem item8 = (TextItem) slide1.getSlideItems().get(7);
        assertEquals(3, item8.getLevel());
        assertEquals("Next slide: PgDn or Enter", item8.getText());
        
        TextItem item9 = (TextItem) slide1.getSlideItems().get(8);
        assertEquals(3, item9.getLevel());
        assertEquals("Previous slide: PgUp or up-arrow", item9.getText());
        
        TextItem item10 = (TextItem) slide1.getSlideItems().get(9);
        assertEquals(3, item10.getLevel());
        assertEquals("Quit: q or Q", item10.getText());
        
        // Second slide
        Slide slide2 = presentation.getSlide(1);
        assertEquals("Demonstration of levels and stijlen", slide2.getTitle());
        assertEquals(7, slide2.getSlideItems().size());
        
        // Third slide
        Slide slide3 = presentation.getSlide(2);
        assertEquals("The third slide", slide3.getTitle());
        assertEquals(5, slide3.getSlideItems().size());
        
        // Check the bitmap item in the third slide
        BitmapItem bitmapItem = (BitmapItem) slide3.getSlideItems().get(4);
        assertEquals(1, bitmapItem.getLevel());
        assertEquals("JabberPoint.gif", bitmapItem.getName());
    }
} 