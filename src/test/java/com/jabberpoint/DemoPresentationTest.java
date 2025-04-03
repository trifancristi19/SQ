package com.jabberpoint;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.io.IOException;

public class DemoPresentationTest
{

    private DemoPresentation demoPresentation;
    private Presentation presentation;

    @Before
    public void setUp()
    {
        demoPresentation = new DemoPresentation();
        presentation = new Presentation();
    }

    @Test
    public void testLoadFile()
    {
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
    public void testSaveFile()
    {
        // Saving the demo presentation should throw an IllegalStateException
        demoPresentation.saveFile(presentation, "demo.xml");
        // Test should pass if the expected exception is thrown
    }

    @Test
    public void testDemoPresentationAccessorPattern()
    {
        try
        {
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
        } catch (IOException e)
        {
            fail("Should not throw IOException: " + e.getMessage());
        }
    }

    @Test
    public void testSlideContentDetails()
    {
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
} 