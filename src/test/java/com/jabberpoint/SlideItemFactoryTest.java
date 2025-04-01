package com.jabberpoint;

import org.junit.Test;
import static org.junit.Assert.*;

public class SlideItemFactoryTest {
    
    @Test
    public void testCreateTextItem() {
        SlideItem item = SlideItemFactory.createSlideItem("text", 1, "Test Content");
        assertNotNull("Should create a text item", item);
        assertTrue("Item should be a TextItem", item instanceof TextItem);
        assertEquals("Level should be correct", 1, item.getLevel());
    }
    
    @Test
    public void testCreateBitmapItem() {
        SlideItem item = SlideItemFactory.createSlideItem("bitmap", 2, "demo.jpg");
        assertNotNull("Should create a bitmap item", item);
        assertTrue("Item should be a BitmapItem", item instanceof BitmapItem);
        assertEquals("Level should be correct", 2, item.getLevel());
    }
    
    @Test
    public void testCreateTextItemCaseInsensitive() {
        SlideItem item = SlideItemFactory.createSlideItem("TEXT", 1, "Test Content");
        assertNotNull("Should create a text item regardless of case", item);
        assertTrue("Item should be a TextItem", item instanceof TextItem);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testCreateInvalidType() {
        SlideItemFactory.createSlideItem("invalid", 1, "content");
    }
} 