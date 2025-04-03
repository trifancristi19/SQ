package com.jabberpoint;

import org.junit.Test;
import org.junit.Before;
import org.junit.BeforeClass;
import static org.junit.Assert.*;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;
import java.util.Vector;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.awt.image.BufferedImage;
import java.awt.Color;

/**
 * Tests for the Slide class
 */
public class SlideTest {
    
    private Slide slide;
    private SlideItem mockItem;
    private Graphics mockGraphics;
    private ImageObserver mockObserver;
    
    @BeforeClass
    public static void setUpClass() {
        // Initialize styles for testing
        Style.createStyles();
    }
    
    @Before
    public void setUp() {
        slide = new Slide();
        slide.setTitle("Test Slide");
        
        // Create a mock item for testing
        mockItem = new TextItem(1, "Test Item");
        
        // Create a mock Graphics object using a BufferedImage
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        mockGraphics = image.createGraphics();
        
        // Create a mock ImageObserver
        mockObserver = new ImageObserver() {
            @Override
            public boolean imageUpdate(java.awt.Image img, int infoflags, int x, int y, int width, int height) {
                return false;
            }
        };
    }
    
    /**
     * Test slide basics
     */
    @Test
    public void testSlideBasics() {
        assertEquals("Title should be set correctly", "Test Slide", slide.getTitle());
        assertEquals("New slide should have 0 items", 0, slide.getSize());
    }
    
    /**
     * Test append functionality
     */
    @Test
    public void testAppend() {
        slide.append(mockItem);
        assertEquals("After append, slide should have 1 item", 1, slide.getSize());
        assertEquals("Item should be retrievable", mockItem, slide.getSlideItem(0));
        
        SlideItem newItem = new TextItem(2, "New Item");
        slide.append(newItem);
        assertEquals("After second append, slide should have 2 items", 2, slide.getSize());
        
        // Check that items are stored in correct order
        Vector<SlideItem> items = slide.getSlideItems();
        assertEquals("First item should match first appended", mockItem, items.get(0));
        assertEquals("Second item should match second appended", newItem, items.get(1));
    }
    
    /**
     * Test getTitle
     */
    @Test
    public void testGetTitle() {
        assertEquals("Title should match what was set", "Test Slide", slide.getTitle());
        
        // Test null title
        Slide nullTitleSlide = new Slide();
        assertNull("Title should be null for new slide", nullTitleSlide.getTitle());
    }
    
    /**
     * Test setTitle
     */
    @Test
    public void testSetTitle() {
        slide.setTitle("New Title");
        assertEquals("Title should be updated", "New Title", slide.getTitle());
        
        // Test with null title
        slide.setTitle(null);
        assertNull("Title should be null after setting to null", slide.getTitle());
    }
    
    /**
     * Test default constructor
     */
    @Test
    public void testDefaultConstructor() {
        Slide newSlide = new Slide();
        assertNull("New slide should have null title", newSlide.getTitle());
        assertEquals("New slide should have 0 items", 0, newSlide.getSize());
    }
    
    /**
     * Test drawing with null parameters
     */
    @Test
    public void testDrawWithNullParameters() {
        // This should not throw an exception when g is null
        try {
            slide.draw(null, new Rectangle(0, 0, 100, 100), mockObserver);
            // We would not normally reach here because a NullPointerException is expected
            fail("Drawing with null Graphics should throw NullPointerException");
        } catch (NullPointerException e) {
            // This is expected because Graphics cannot be null in real use
            assertTrue("Expected NullPointerException was thrown", true);
        }
    }
    
    /**
     * Test getSlideItem with various indices
     */
    @Test
    public void testGetSlideItem() {
        // Add multiple items to test retrieval
        SlideItem item1 = new TextItem(1, "Item 1");
        SlideItem item2 = new TextItem(2, "Item 2");
        SlideItem item3 = new TextItem(3, "Item 3");
        
        slide.append(item1);
        slide.append(item2);
        slide.append(item3);
        
        // Test getting item at valid index
        assertEquals("Should retrieve correct item at index 0", item1, slide.getSlideItem(0));
        assertEquals("Should retrieve correct item at index 1", item2, slide.getSlideItem(1));
        assertEquals("Should retrieve correct item at index 2", item3, slide.getSlideItem(2));
        
        // Test index out of bounds
        try {
            slide.getSlideItem(-1);
            fail("Negative index should throw ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException e) {
            // Expected exception
            assertTrue(true);
        }
        
        try {
            slide.getSlideItem(3);
            fail("Index >= size should throw ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException e) {
            // Expected exception
            assertTrue(true);
        }
    }
    
    /**
     * Test getSlideItems
     */
    @Test
    public void testGetSlideItems() {
        // Add items to test
        SlideItem item1 = new TextItem(1, "Item 1");
        SlideItem item2 = new TextItem(2, "Item 2");
        
        slide.append(item1);
        slide.append(item2);
        
        Vector<SlideItem> items = slide.getSlideItems();
        assertNotNull("Slide items vector should not be null", items);
        assertEquals("Vector should contain 2 items", 2, items.size());
        assertEquals("First item should match", item1, items.get(0));
        assertEquals("Second item should match", item2, items.get(1));
        
        // Verify the returned vector is a copy by modifying it and checking original
        items.remove(0);
        assertEquals("Original slides should be unchanged", 2, slide.getSize());
    }
    
    /**
     * Test getScale method via reflection
     */
    @Test
    public void testGetScale() {
        try {
            // Access the private getScale method via reflection
            Method getScaleMethod = Slide.class.getDeclaredMethod("getScale", Rectangle.class);
            getScaleMethod.setAccessible(true);
            
            // Test with different rectangle sizes
            Rectangle smallRect = new Rectangle(0, 0, 100, 100);
            Rectangle largeRect = new Rectangle(0, 0, 1000, 1000);
            Rectangle wideRect = new Rectangle(0, 0, 1000, 100);
            Rectangle tallRect = new Rectangle(0, 0, 100, 1000);
            
            float smallScale = (float) getScaleMethod.invoke(slide, smallRect);
            float largeScale = (float) getScaleMethod.invoke(slide, largeRect);
            float wideScale = (float) getScaleMethod.invoke(slide, wideRect);
            float tallScale = (float) getScaleMethod.invoke(slide, tallRect);
            
            // Small rectangle should have a small scale
            assertTrue("Small rectangle should have scale < 1.0", smallScale < 1.0);
            
            // Large rectangle should have a larger scale than small
            assertTrue("Large scale should be larger than small scale", largeScale > smallScale);
            
            // Test that width and height are considered correctly
            assertEquals("Scale should be minimum of width and height ratios", 
                    Math.min(wideRect.width / (float)Slide.WIDTH, wideRect.height / (float)Slide.HEIGHT), 
                    wideScale, 0.01);
            
        } catch (Exception e) {
            fail("Exception while testing getScale: " + e.getMessage());
        }
    }
    
    /**
     * Test draw method with actual objects
     */
    @Test
    public void testDraw() {
        // Set up a slide with items for drawing
        Slide drawSlide = new Slide();
        drawSlide.setTitle("Drawing Test");
        
        TextItem item1 = new TextItem(1, "Draw Item 1");
        TextItem item2 = new TextItem(2, "Draw Item 2");
        
        drawSlide.append(item1);
        drawSlide.append(item2);
        
        // Create a rectangle for the area
        Rectangle area = new Rectangle(0, 0, 500, 500);
        
        // Call draw method - should not throw exceptions
        drawSlide.draw(mockGraphics, area, mockObserver);
        assertTrue("Draw should complete without exceptions", true);
    }
    
    /**
     * Test draw method with null title
     */
    @Test
    public void testDrawWithNullTitle() {
        Slide nullTitleSlide = new Slide();
        
        // Add an item so we can test item drawing with null title
        TextItem item = new TextItem(1, "Item in slide with null title");
        nullTitleSlide.append(item);
        
        // Create a rectangle for the area
        Rectangle area = new Rectangle(0, 0, 500, 500);
        
        // Should not throw exception
        nullTitleSlide.draw(mockGraphics, area, mockObserver);
        assertTrue("Draw with null title should not throw exceptions", true);
    }
    
    /**
     * Test draw method with empty slide (no items)
     */
    @Test
    public void testDrawWithEmptySlide() {
        // Create an empty slide
        Slide emptySlide = new Slide();
        emptySlide.setTitle("Empty Slide");
        
        // Create a rectangle for the area
        Rectangle area = new Rectangle(0, 0, 500, 500);
        
        // Should not throw exception
        emptySlide.draw(mockGraphics, area, mockObserver);
        assertTrue("Draw with empty slide should not throw exceptions", true);
    }
    
    /**
     * Test append with null item
     */
    @Test
    public void testAppendNullItem() {
        int initialSize = slide.getSize();
        
        // Should not throw exception when null is appended
        slide.append(null);
        
        // Size should remain the same
        assertEquals("Size should not change when null item is appended", initialSize, slide.getSize());
    }
    
    /**
     * Test constants access via reflection
     */
    @Test
    public void testSlideConstants() {
        try {
            // Access WIDTH and HEIGHT constants
            Field widthField = Slide.class.getDeclaredField("WIDTH");
            Field heightField = Slide.class.getDeclaredField("HEIGHT");
            widthField.setAccessible(true);
            heightField.setAccessible(true);
            
            int width = widthField.getInt(null);
            int height = heightField.getInt(null);
            
            assertTrue("WIDTH should be positive", width > 0);
            assertTrue("HEIGHT should be positive", height > 0);
            
        } catch (Exception e) {
            fail("Exception while testing constants: " + e.getMessage());
        }
    }
    
    /**
     * Test append with level and string
     */
    @Test
    public void testAppendWithLevelAndString() {
        int initialSize = slide.getSize();
        
        // Append text with level
        slide.append(2, "Text with level");
        assertEquals("Size should increase by 1", initialSize + 1, slide.getSize());
        
        // Get the added item and verify its properties
        SlideItem item = slide.getSlideItem(initialSize);
        assertTrue("Added item should be a TextItem", item instanceof TextItem);
        assertEquals("Level should match", 2, item.getLevel());
        assertEquals("Text should match", "Text with level", ((TextItem)item).getText());
        
        // Test with null message
        slide.append(3, null);
        assertEquals("Size should not change with null message", initialSize + 1, slide.getSize());
    }
    
    /**
     * Test replacing items in the vector
     */
    @Test
    public void testReplaceItems() {
        // Add a few items
        slide.append(new TextItem(1, "Item 1"));
        slide.append(new TextItem(2, "Item 2"));
        
        // Get the vector of items
        Vector<SlideItem> original = slide.getSlideItems();
        int originalSize = original.size();
        
        // Create a new vector with different items
        Vector<SlideItem> newItems = new Vector<>();
        newItems.add(new TextItem(3, "New Item 3"));
        newItems.add(new TextItem(4, "New Item 4"));
        
        // Attempting to modify the items directly should not affect the slide
        // because getSlideItems returns a copy
        original.clear();
        assertEquals("Slide size should be unchanged", originalSize, slide.getSize());
        
        // The items in the slide should still be accessible
        assertTrue("Original items should still be in the slide", 
                slide.getSlideItem(0) instanceof TextItem);
    }
} 