package com.jabberpoint;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import static org.junit.Assert.*;

import java.util.function.BiFunction;

/**
 * Tests for the SlideItemFactory
 */
public class SlideItemFactoryTest
{
    // Store a custom item creator to restore after tests
    private BiFunction<Integer, String, SlideItem> originalCustomItemCreator = null;
    private boolean hadCustomItem = false;

    @Before
    public void setUp()
    {
        // Check if "custom" type already exists
        try
        {
            hadCustomItem = true;
            SlideItemFactory.createSlideItem("custom", 1, "test");
        } catch (IllegalArgumentException e)
        {
            hadCustomItem = false;
        }
    }

    @After
    public void tearDown()
    {
        // Remove test registration if we added it
        if (!hadCustomItem)
        {
            try
            {
                // Use reflection to remove our test registration
                java.lang.reflect.Field itemCreatorsField = SlideItemFactory.class.getDeclaredField("itemCreators");
                itemCreatorsField.setAccessible(true);
                @SuppressWarnings("unchecked")
                java.util.Map<String, BiFunction<Integer, String, SlideItem>> itemCreators =
                        (java.util.Map<String, BiFunction<Integer, String, SlideItem>>) itemCreatorsField.get(null);
                itemCreators.remove("custom");
            } catch (Exception e)
            {
                // Ignore
            }
        }
    }

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

    @Test
    public void testRegisterNewItemType()
    {
        // Create a custom item creator
        BiFunction<Integer, String, SlideItem> customCreator = (level, content) -> new TextItem(level, "Custom: " + content);

        // Register the custom creator
        SlideItemFactory.registerItemType("custom", customCreator);

        // Use the custom creator
        SlideItem item = SlideItemFactory.createSlideItem("custom", 3, "Test");

        // Verify the result
        assertTrue(item instanceof TextItem);
        assertEquals(3, item.getLevel());
        assertEquals("Custom: Test", ((TextItem) item).getText());

        // Also test with different case
        item = SlideItemFactory.createSlideItem("CUSTOM", 4, "Case Test");
        assertEquals(4, item.getLevel());
        assertEquals("Custom: Case Test", ((TextItem) item).getText());
    }

    @Test
    public void testImageAliasForBitmap()
    {
        // Test that "image" is an alias for "bitmap"
        SlideItem item = SlideItemFactory.createSlideItem("image", 2, "test.jpg");
        assertTrue(item instanceof BitmapItem);
        assertEquals("test.jpg", ((BitmapItem) item).getName());
    }
} 