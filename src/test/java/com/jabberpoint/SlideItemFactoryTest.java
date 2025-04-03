package com.jabberpoint;

import org.junit.Test;

import static org.junit.Assert.*;

public class SlideItemFactoryTest
{

    @Test
    public void testCreateTextItem()
    {
        SlideItem item = SlideItemFactory.createSlideItem("text", 1, "Test Content");
        assertTrue(item instanceof TextItem);
        assertEquals(1, item.getLevel());
        assertEquals("Test Content", ((TextItem) item).getText());
    }

    @Test
    public void testCreateBitmapItem()
    {
        SlideItem item = SlideItemFactory.createSlideItem("bitmap", 2, "test.png");
        assertTrue(item instanceof BitmapItem);
        assertEquals(2, item.getLevel());
        assertEquals("test.png", ((BitmapItem) item).getName());
    }

    @Test
    public void testCreateTextItemCaseInsensitive()
    {
        SlideItem item = SlideItemFactory.createSlideItem("TEXT", 1, "Test Content");
        assertTrue(item instanceof TextItem);
        assertEquals(1, item.getLevel());
        assertEquals("Test Content", ((TextItem) item).getText());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateUnknownItemType()
    {
        SlideItemFactory.createSlideItem("unknown", 1, "Test Content");
    }
} 