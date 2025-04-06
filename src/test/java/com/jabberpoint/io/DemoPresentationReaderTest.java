package com.jabberpoint.io;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import com.jabberpoint.Presentation;
import com.jabberpoint.Slide;
import com.jabberpoint.SlideItem;
import com.jabberpoint.BitmapItem;

/**
 * Tests for the DemoPresentationReader class in the io package
 */
public class DemoPresentationReaderTest
{

    private DemoPresentationReader reader;
    private Presentation presentation;

    @Before
    public void setUp()
    {
        reader = new DemoPresentationReader();
        presentation = new Presentation();
    }

    @Test
    public void testLoadPresentation() throws Exception
    {
        // Load the demo presentation
        reader.loadPresentation(presentation, "");

        // Verify the presentation was populated
        assertEquals("Demo Presentation", presentation.getTitle());
        assertEquals(3, presentation.getSize());

        // Check the first slide
        Slide firstSlide = presentation.getSlide(0);
        assertEquals("JabberPoint", firstSlide.getTitle());
        assertTrue("First slide should have multiple items", firstSlide.getSlideItems().size() > 5);

        // Check the second slide
        Slide secondSlide = presentation.getSlide(1);
        assertEquals("Demonstration of levels and styles", secondSlide.getTitle());
        assertTrue("Second slide should have multiple items", secondSlide.getSlideItems().size() > 3);

        // Check the third slide
        Slide thirdSlide = presentation.getSlide(2);
        assertEquals("The third slide", thirdSlide.getTitle());
        assertTrue("Third slide should have multiple items", thirdSlide.getSlideItems().size() > 3);

        // Verify that the third slide has a bitmap item
        boolean hasBitmapItem = false;
        for (SlideItem item : thirdSlide.getSlideItems())
        {
            if (item instanceof BitmapItem)
            {
                hasBitmapItem = true;
                assertEquals("JabberPoint.gif", ((BitmapItem) item).getName());
                break;
            }
        }
        assertTrue("Third slide should have a bitmap item", hasBitmapItem);
    }

    @Test
    public void testLoadPresentationIgnoresFilename() throws Exception
    {
        // The demo reader ignores the filename, so we should be able to pass any string
        reader.loadPresentation(presentation, "ignored-filename.xyz");

        // Verify the presentation was populated
        assertEquals("Demo Presentation", presentation.getTitle());
        assertEquals(3, presentation.getSize());
    }

    @Test
    public void testLoadMultipleTimes() throws Exception
    {
        // Load once
        reader.loadPresentation(presentation, "");
        int initialSlideCount = presentation.getSize();

        // Load again - should clear the presentation first
        reader.loadPresentation(presentation, "");

        // Should still have the same number of slides (not doubled)
        assertEquals("Should have same slide count after loading twice", initialSlideCount, presentation.getSize());
    }
} 