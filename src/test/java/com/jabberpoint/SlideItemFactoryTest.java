package com.jabberpoint;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

public class SlideItemFactoryTest {

    @Before
    public void setUp() {
        // Initialize any styles needed for tests
        Style.createStyles();
    }
    
    @Test
    public void testCreateTextItem() {
        SlideItem item = SlideItemFactory.createSlideItem("text", 1, "Test Content");
        assertTrue(item instanceof TextItem);
        assertEquals(1, item.getLevel());
        assertEquals("Test Content", ((TextItem)item).getText());
    }
    
    @Test
    public void testCreateBitmapItem() {
        SlideItem item = SlideItemFactory.createSlideItem("bitmap", 2, "test.png");
        assertTrue(item instanceof BitmapItem);
        assertEquals(2, item.getLevel());
        assertEquals("test.png", ((BitmapItem)item).getName());
    }
    
    @Test
    public void testCreateImageItem() {
        // "image" should be treated the same as "bitmap"
        SlideItem item = SlideItemFactory.createSlideItem("image", 2, "demo.jpg");
        assertNotNull("Should create a bitmap item for 'image' type", item);
        assertTrue("Item should be a BitmapItem", item instanceof BitmapItem);
        assertEquals("Level should be correct", 2, item.getLevel());
    }
    
    @Test
    public void testCreateTextItemCaseInsensitive() {
        SlideItem item = SlideItemFactory.createSlideItem("TEXT", 1, "Test Content");
        assertTrue(item instanceof TextItem);
        assertEquals(1, item.getLevel());
        assertEquals("Test Content", ((TextItem)item).getText());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testCreateUnknownItemType() {
        SlideItemFactory.createSlideItem("unknown", 1, "Test Content");
    }
    
    @Test
    public void testHandleUnknownElementType() {
        try {
            SlideItemFactory.createSlideItem("unknown", 1, "test");
            fail("Should throw IllegalArgumentException for unknown element type");
        } catch (IllegalArgumentException e) {
            // This is expected
            assertTrue("Exception message should mention unknown type", 
                e.getMessage().contains("unknown") || e.getMessage().contains("Unknown"));
        }
    }
    
    @Test
    public void testCreateTextItemWithDifferentLevels() {
        // Test with valid levels
        for (int level = 0; level <= 4; level++) {
            SlideItem item = SlideItemFactory.createSlideItem("text", level, "Content");
            assertEquals("Level should be set correctly", level, item.getLevel());
        }
        
        // Test with negative level (implementation should handle this)
        SlideItem item = SlideItemFactory.createSlideItem("text", -1, "Content");
        assertEquals("Negative level should be handled", -1, item.getLevel());
    }
    
    @Test
    public void testCreateItemWithEmptyContent() {
        // Test with empty content
        SlideItem textItem = SlideItemFactory.createSlideItem("text", 1, "");
        assertNotNull("Should create text item with empty content", textItem);
        assertEquals("Empty content should be preserved", "", ((TextItem)textItem).getText());
        
        // Test with null content - implementation should handle this
        SlideItem textItemNull = SlideItemFactory.createSlideItem("text", 1, null);
        assertNotNull("Should create text item with null content", textItemNull);
        
        // Test bitmap with empty path
        SlideItem bitmapItem = SlideItemFactory.createSlideItem("bitmap", 1, "");
        assertNotNull("Should create bitmap item with empty path", bitmapItem);
        assertEquals("Empty path should be preserved", "", ((BitmapItem)bitmapItem).getName());
    }
} 