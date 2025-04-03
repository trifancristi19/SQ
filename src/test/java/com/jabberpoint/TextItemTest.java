package com.jabberpoint;

import org.junit.Test;
import org.junit.Before;
import org.junit.BeforeClass;

import static org.junit.Assert.*;

import java.awt.Rectangle;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.Font;
import java.awt.Color;

public class TextItemTest {

    @BeforeClass
    public static void setUpClass() {
        // Initialize styles
        Style.createStyles();
    }
    
    @Before
    public void setUp() {
        // No specific setup needed for each test
    }

    @Test
    public void testTextItemCreation() {
        String testText = "Test Text";
        TextItem textItem = new TextItem(1, testText);
        assertNotNull("TextItem should be created", textItem);
        assertEquals("Level should be set correctly", 1, textItem.getLevel());
        assertEquals("Text should be set correctly", testText, textItem.getText());
    }

    @Test
    public void testEmptyTextItemCreation() {
        TextItem textItem = new TextItem();
        assertNotNull("Empty TextItem should be created", textItem);
        assertEquals("Default level should be 0", 0, textItem.getLevel());
        // The default text is "No Text Given" but getText() returns "" for null text
        assertNotNull("Text should not be null", textItem.getText());
    }

    @Test
    public void testGetText() {
        String testText = "Test Text";
        TextItem textItem = new TextItem(1, testText);
        assertEquals("getText should return the correct text", testText, textItem.getText());
    }

    @Test
    public void testGetAttributedString() {
        String testText = "Test Text";
        TextItem textItem = new TextItem(1, testText);

        // Create a style for testing
        Style style = new Style(10, Color.BLACK, 12, 5);

        // Test with a real style
        assertNotNull("AttributedString should be created", textItem.getAttributedString(style, 1.0f));
    }


    @Test
    public void testToString() {
        String testText = "Test Text";
        int level = 2;
        TextItem textItem = new TextItem(level, testText);

        String result = textItem.toString();
        assertNotNull("toString should return a non-null string", result);
        assertTrue("toString should contain the level", result.contains(String.valueOf(level)));
        assertTrue("toString should contain the text", result.contains(testText));
    }
    
    @Test
    public void testDrawMethod() {
        // Create a TextItem
        TextItem textItem = new TextItem(1, "Test Drawing");
        
        // Create a mock graphics context
        BufferedImage bufferedImage = new BufferedImage(300, 200, BufferedImage.TYPE_INT_ARGB);
        Graphics g = bufferedImage.getGraphics();
        
        // Call draw method - should not throw exceptions
        textItem.draw(100, 100, 1.0f, g, Style.getStyle(textItem.getLevel()), null);
        
        // No specific assertions since we're just checking it doesn't throw exceptions
        // and the actual rendering is hard to verify in a unit test
    }
    
    @Test
    public void testDrawWithNullText() {
        // Create a TextItem with null text (implementation should handle this)
        TextItem textItem = new TextItem(1, null);
        
        // Create a mock graphics context
        BufferedImage bufferedImage = new BufferedImage(300, 200, BufferedImage.TYPE_INT_ARGB);
        Graphics g = bufferedImage.getGraphics();
        
        // Call draw method - should not throw exceptions even with null text
        textItem.draw(100, 100, 1.0f, g, Style.getStyle(textItem.getLevel()), null);
        
        // Test passes if no exception is thrown
    }
    
    @Test
    public void testDrawWithEmptyText() {
        // Create a TextItem with empty text
        TextItem textItem = new TextItem(1, "");
        
        // Create a mock graphics context
        BufferedImage bufferedImage = new BufferedImage(300, 200, BufferedImage.TYPE_INT_ARGB);
        Graphics g = bufferedImage.getGraphics();
        
        // Call draw method - should not throw exceptions with empty text
        textItem.draw(100, 100, 1.0f, g, Style.getStyle(textItem.getLevel()), null);
        
        // Test passes if no exception is thrown
    }
    
    @Test
    public void testDrawWithDifferentScaleFactor() {
        // Create a TextItem
        TextItem textItem = new TextItem(1, "Test Drawing with Scale");
        
        // Create a mock graphics context
        BufferedImage bufferedImage = new BufferedImage(300, 200, BufferedImage.TYPE_INT_ARGB);
        Graphics g = bufferedImage.getGraphics();
        
        // Test with different scale factors
        textItem.draw(100, 100, 0.5f, g, Style.getStyle(textItem.getLevel()), null);
        textItem.draw(100, 100, 2.0f, g, Style.getStyle(textItem.getLevel()), null);
        
        // Test passes if no exception is thrown
    }
    
    @Test
    public void testDrawWithMultilineText() {
        // Create a TextItem with multiline text
        TextItem textItem = new TextItem(1, "Line 1\nLine 2\nLine 3");
        
        // Create a mock graphics context
        BufferedImage bufferedImage = new BufferedImage(300, 200, BufferedImage.TYPE_INT_ARGB);
        Graphics g = bufferedImage.getGraphics();
        
        // Call draw method - should handle multiline text
        textItem.draw(100, 100, 1.0f, g, Style.getStyle(textItem.getLevel()), null);
        
        // Test passes if no exception is thrown
    }
    
    @Test
    public void testBoundingBoxCalculation() {
        // Create a TextItem
        TextItem textItem = new TextItem(1, "Test Bounding Box");
        
        // Create a mock graphics context
        BufferedImage bufferedImage = new BufferedImage(300, 200, BufferedImage.TYPE_INT_ARGB);
        Graphics g = bufferedImage.getGraphics();
        
        // Get the bounding box
        Rectangle result = textItem.getBoundingBox(g, null, 1.0f, Style.getStyle(textItem.getLevel()));
        
        // Verify the bounding box
        assertNotNull("Bounding box should not be null", result);
        assertTrue("Bounding box width should be positive", result.width > 0);
        assertTrue("Bounding box height should be positive", result.height > 0);
    }
} 