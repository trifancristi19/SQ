package com.jabberpoint;

import org.junit.Test;

import static org.junit.Assert.*;

import java.awt.Rectangle;
import java.awt.Graphics;
import java.awt.image.ImageObserver;

public class SlideItemTest
{

    // Since SlideItem is abstract, we need a concrete implementation for testing
    private class TestSlideItem extends SlideItem
    {
        public TestSlideItem(int level)
        {
            super(level);
        }

        public TestSlideItem()
        {
            super();
        }

        @Override
        public Rectangle getBoundingBox(Graphics g, ImageObserver observer, float scale, Style style)
        {
            return new Rectangle(0, 0, 100, 100);
        }

        @Override
        public void draw(int x, int y, float scale, Graphics g, Style style, ImageObserver observer)
        {
            // Do nothing for the test
        }
    }

    @Test
    public void testSlideItemCreation()
    {
        SlideItem item = new TestSlideItem(2);
        assertNotNull("SlideItem should be created", item);
        assertEquals("Level should be set correctly", 2, item.getLevel());
    }

    @Test
    public void testDefaultConstructor()
    {
        SlideItem item = new TestSlideItem();
        assertEquals("Default level should be 0", 0, item.getLevel());
    }

    @Test
    public void testGetLevel()
    {
        int level = 3;
        SlideItem item = new TestSlideItem(level);
        assertEquals("getLevel should return the correct level", level, item.getLevel());
    }

    @Test
    public void testGetBoundingBox()
    {
        SlideItem item = new TestSlideItem(1);
        Rectangle boundingBox = item.getBoundingBox(null, null, 1.0f, null);

        assertNotNull("Bounding box should be created", boundingBox);
        assertEquals("Width should be as expected", 100, boundingBox.width);
        assertEquals("Height should be as expected", 100, boundingBox.height);
    }
} 