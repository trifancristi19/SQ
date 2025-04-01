package com.jabberpoint;

import org.junit.Test;
import org.junit.BeforeClass;

import static org.junit.Assert.*;

import java.awt.Rectangle;
import java.awt.Graphics;
import java.awt.image.ImageObserver;
import java.awt.image.BufferedImage;
import java.util.Vector;

public class SlideTest
{

    @BeforeClass
    public static void setUpClass()
    {
        // Initialize styles for tests that need them
        Style.createStyles();
    }

    @Test
    public void testSlideCreation()
    {
        Slide slide = new Slide();
        assertNotNull("Slide should be created", slide);
        assertEquals("New slide should have 0 items", 0, slide.getSize());
    }

    @Test
    public void testAppendItem()
    {
        Slide slide = new Slide();
        TextItem item = new TextItem(1, "Test Text");
        slide.append(item);
        assertEquals("Slide should have 1 item", 1, slide.getSize());
    }

    @Test
    public void testSetTitle()
    {
        Slide slide = new Slide();
        String testTitle = "Test Title";
        slide.setTitle(testTitle);
        assertEquals("Title should be set correctly", testTitle, slide.getTitle());
    }

    @Test
    public void testGetSlideItem()
    {
        Slide slide = new Slide();
        TextItem item1 = new TextItem(1, "Item 1");
        TextItem item2 = new TextItem(2, "Item 2");

        slide.append(item1);
        slide.append(item2);

        assertSame("First item should be retrieved", item1, slide.getSlideItem(0));
        assertSame("Second item should be retrieved", item2, slide.getSlideItem(1));
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testGetInvalidSlideItem()
    {
        Slide slide = new Slide();
        // This should throw an ArrayIndexOutOfBoundsException
        slide.getSlideItem(0);
    }

    @Test
    public void testGetSlideItems()
    {
        Slide slide = new Slide();
        TextItem item1 = new TextItem(1, "Item 1");
        TextItem item2 = new TextItem(2, "Item 2");

        slide.append(item1);
        slide.append(item2);

        assertNotNull("Slide items should be returned", slide.getSlideItems());
        assertEquals("Slide items vector should have correct size", 2, slide.getSlideItems().size());
    }

    @Test
    public void testGetSize()
    {
        Slide slide = new Slide();
        assertEquals("Empty slide should have size 0", 0, slide.getSize());

        slide.append(new TextItem(1, "Item 1"));
        assertEquals("Slide should have size 1", 1, slide.getSize());

        slide.append(new TextItem(2, "Item 2"));
        assertEquals("Slide should have size 2", 2, slide.getSize());
    }

    @Test
    public void testDraw()
    {
        Slide slide = new Slide();
        slide.setTitle("Test Slide");
        slide.append(new TextItem(1, "Test Item"));

        // Create a dummy graphics context instead of passing null
        BufferedImage image = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();

        try
        {
            // Create a dummy rectangle
            Rectangle rect = new Rectangle(0, 0, 800, 600);
            slide.draw(g, rect, null);
            // If we get here, the test passes (no exceptions thrown)
            assertTrue(true);
        } catch (Exception e)
        {
            fail("draw() should not throw exceptions: " + e.getMessage());
        } finally
        {
            if (g != null)
            {
                g.dispose(); // Clean up resources
            }
        }
    }

    @Test
    public void testAppendWithLevelAndText()
    {
        Slide slide = new Slide();
        slide.append(1, "Test Text");

        assertEquals("Slide should have 1 item", 1, slide.getSize());
        SlideItem item = slide.getSlideItem(0);
        assertTrue("Item should be a TextItem", item instanceof TextItem);
        TextItem textItem = (TextItem) item;
        assertEquals("Text item should have correct text", "Test Text", textItem.getText());
        assertEquals("Text item should have correct level", 1, textItem.getLevel());
    }

    @Test
    public void testDrawWithDifferentScales() {
        Slide slide = new Slide();
        slide.setTitle("Test Slide");
        slide.append(new TextItem(1, "Test Item"));

        BufferedImage image = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();

        try {
            // Test with different rectangle sizes to test scaling
            Rectangle[] testRects = {
                new Rectangle(0, 0, 400, 300),  // Smaller than slide
                new Rectangle(0, 0, 1600, 1200), // Larger than slide
                new Rectangle(0, 0, 1200, 800),  // Same as slide
                new Rectangle(0, 0, 600, 400)    // Different aspect ratio
            };

            for (Rectangle rect : testRects) {
                try {
                    slide.draw(g, rect, null);
                } catch (Exception e) {
                    fail("draw() should not throw exceptions for rectangle " + rect + ": " + e.getMessage());
                }
            }
        } finally {
            if (g != null) {
                g.dispose();
            }
        }
    }

    @Test
    public void testDrawWithNullTitle() {
        Slide slide = new Slide();
        slide.setTitle(null);
        slide.append(new TextItem(1, "Test Item"));

        BufferedImage image = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();

        try {
            Rectangle rect = new Rectangle(0, 0, 800, 600);
            slide.draw(g, rect, null);
            // Should not throw exception for null title
        } finally {
            if (g != null) {
                g.dispose();
            }
        }
    }

    @Test
    public void testDrawWithEmptySlide() {
        Slide slide = new Slide();
        slide.setTitle("Test Slide");

        BufferedImage image = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();

        try {
            Rectangle rect = new Rectangle(0, 0, 800, 600);
            slide.draw(g, rect, null);
            // Should not throw exception for empty slide
        } finally {
            if (g != null) {
                g.dispose();
            }
        }
    }

    @Test
    public void testDrawWithBitmapItem() {
        Slide slide = new Slide();
        slide.setTitle("Test Slide");
        slide.append(new BitmapItem(1, "test.jpg"));

        BufferedImage image = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();

        try {
            Rectangle rect = new Rectangle(0, 0, 800, 600);
            slide.draw(g, rect, null);
            // Should not throw exception for bitmap item
        } finally {
            if (g != null) {
                g.dispose();
            }
        }
    }

    @Test
    public void testAppendNullItem() {
        Slide slide = new Slide();
        slide.append((SlideItem)null);
        assertEquals("Slide should not add null item", 0, slide.getSize());
    }

    @Test
    public void testAppendNullText() {
        Slide slide = new Slide();
        slide.append(1, null);
        assertEquals("Slide should not add null text", 0, slide.getSize());
    }

    @Test
    public void testGetSlideItemsModification() {
        Slide slide = new Slide();
        TextItem item = new TextItem(1, "Test Item");
        slide.append(item);

        Vector<SlideItem> items = slide.getSlideItems();
        items.add(new TextItem(2, "New Item")); // Try to modify the vector

        assertEquals("Original slide should not be modified", 1, slide.getSize());
        assertEquals("Original item should remain unchanged", item, slide.getSlideItem(0));
    }

    @Test
    public void testSetNullTitle() {
        Slide slide = new Slide();
        slide.setTitle(null);
        assertNull("Title should be null", slide.getTitle());
    }

    @Test
    public void testDrawWithZeroSizeArea() {
        Slide slide = new Slide();
        slide.setTitle("Test Slide");
        slide.append(new TextItem(1, "Test Item"));

        BufferedImage image = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();

        try {
            Rectangle rect = new Rectangle(0, 0, 0, 0);
            slide.draw(g, rect, null);
            // Should not throw exception for zero size area
        } finally {
            if (g != null) {
                g.dispose();
            }
        }
    }
}