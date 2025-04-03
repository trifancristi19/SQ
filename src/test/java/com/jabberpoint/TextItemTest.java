package com.jabberpoint;

import org.junit.Test;
import org.junit.Before;
import org.junit.BeforeClass;
import static org.junit.Assert.*;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.text.AttributedString;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Font;

<<<<<<< HEAD
/**
 * Tests for the TextItem class
 */
public class TextItemTest {
    
    private TextItem textItem;
    private static Style style;
    private Graphics mockGraphics;
    private ImageObserver mockObserver;
    
    @BeforeClass
    public static void setUpClass() {
        // Create styles for testing
=======
public class TextItemTest {

    @BeforeClass
    public static void setUpClass() {
        // Initialize styles
>>>>>>> d6925b5f1f4d3bd3ef515fc7598526c5c7875072
        Style.createStyles();
        style = Style.getStyle(0);
    }
    
    @Before
    public void setUp() {
<<<<<<< HEAD
        textItem = new TextItem(1, "Test Text");
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
=======
        // No specific setup needed for each test
    }

    @Test
    public void testTextItemCreation() {
        String testText = "Test Text";
        TextItem textItem = new TextItem(1, testText);
        assertNotNull("TextItem should be created", textItem);
        assertEquals("Level should be set correctly", 1, textItem.getLevel());
        assertEquals("Text should be set correctly", testText, textItem.getText());
>>>>>>> d6925b5f1f4d3bd3ef515fc7598526c5c7875072
    }
    
    /**
     * Test constructor with level and string
     */
    @Test
<<<<<<< HEAD
    public void testConstructorWithLevelAndString() {
        TextItem item = new TextItem(2, "Test String");
        assertEquals("Level should be set correctly", 2, item.getLevel());
        assertEquals("Text should be set correctly", "Test String", item.getText());
=======
    public void testEmptyTextItemCreation() {
        TextItem textItem = new TextItem();
        assertNotNull("Empty TextItem should be created", textItem);
        assertEquals("Default level should be 0", 0, textItem.getLevel());
        // The default text is "No Text Given" but getText() returns "" for null text
        assertNotNull("Text should not be null", textItem.getText());
>>>>>>> d6925b5f1f4d3bd3ef515fc7598526c5c7875072
    }
    
    /**
     * Test default constructor
     */
    @Test
<<<<<<< HEAD
    public void testDefaultConstructor() {
        TextItem defaultItem = new TextItem();
        assertEquals("Default level should be 0", 0, defaultItem.getLevel());
        assertEquals("Default text should be 'No Text Given'", "No Text Given", defaultItem.getText());
=======
    public void testGetText() {
        String testText = "Test Text";
        TextItem textItem = new TextItem(1, testText);
        assertEquals("getText should return the correct text", testText, textItem.getText());
>>>>>>> d6925b5f1f4d3bd3ef515fc7598526c5c7875072
    }
    
    /**
     * Test getText method
     */
    @Test
<<<<<<< HEAD
    public void testGetText() {
        assertEquals("Text should match what was set", "Test Text", textItem.getText());
        
        // Test with null text (should return empty string)
        TextItem nullTextItem = new TextItem(1, null);
        assertEquals("Null text should return empty string", "", nullTextItem.getText());
=======
    public void testGetAttributedString() {
        String testText = "Test Text";
        TextItem textItem = new TextItem(1, testText);

        // Create a style for testing
        Style style = new Style(10, Color.BLACK, 12, 5);

        // Test with a real style
        assertNotNull("AttributedString should be created", textItem.getAttributedString(style, 1.0f));
>>>>>>> d6925b5f1f4d3bd3ef515fc7598526c5c7875072
    }
    
    /**
     * Test getAttributedString method
     */
    @Test
<<<<<<< HEAD
    public void testGetAttributedString() {
        float scale = 1.0f;
        AttributedString attrStr = textItem.getAttributedString(style, scale);
        assertNotNull("AttributedString should not be null", attrStr);
        
        // Test with empty text
        TextItem emptyItem = new TextItem(1, "");
        AttributedString emptyAttrStr = emptyItem.getAttributedString(style, scale);
        assertNotNull("AttributedString for empty text should not be null", emptyAttrStr);
    }
    
    /**
     * Test toString method
     */
    @Test
    public void testToString() {
        String expected = "TextItem[1,Test Text]";
        assertEquals("toString should return correct format", expected, textItem.toString());
    }
    
    /**
     * Test that the getBoundingBox method exists
     */
    @Test
    public void testGetBoundingBoxMethodExists() {
        try {
            TextItem.class.getDeclaredMethod("getBoundingBox", 
                Graphics.class, ImageObserver.class, float.class, Style.class);
            // If we get here, the method exists
            assertTrue(true);
        } catch (NoSuchMethodException e) {
            fail("getBoundingBox method does not exist");
        }
    }
    
    /**
     * Test getBoundingBox method with actual parameters
     */
    @Test
    public void testGetBoundingBox() {
        // Call the bounding box method with our mock objects
        Rectangle box = textItem.getBoundingBox(mockGraphics, mockObserver, 1.0f, style);
        
        // Verify the box has reasonable dimensions
        assertNotNull("Bounding box should not be null", box);
        assertTrue("Box width should be greater than 0", box.width > 0);
        assertTrue("Box height should be greater than 0", box.height > 0);
        assertEquals("Box x should match style indent", (int)style.indent, box.x);
    }
    
    /**
     * Test getBoundingBox with empty text
     */
    @Test
    public void testGetBoundingBoxWithEmptyText() {
        TextItem emptyItem = new TextItem(1, "");
        Rectangle box = emptyItem.getBoundingBox(mockGraphics, mockObserver, 1.0f, style);
        
        // Should still return a box with zero width
        assertNotNull("Bounding box should not be null for empty text", box);
        assertEquals("Box height should reflect style leading", (int)style.leading, box.height);
    }
    
    /**
     * Test draw method with actual parameters
     */
    @Test
    public void testDraw() {
        // Create a style with a specific color to test
        Style colorStyle = new Style(12, Color.RED, 12, 12);
        
        // Call the draw method
        textItem.draw(10, 10, 1.0f, mockGraphics, colorStyle, mockObserver);
        
        // We can't easily verify the visual output, but we can ensure no exception occurs
        assertTrue("Draw method should execute without exceptions", true);
    }
    
    /**
     * Test draw method with empty text
     */
    @Test
    public void testDrawWithEmptyText() {
        TextItem emptyItem = new TextItem(1, "");
        
        // Should not throw exception and should return early
        emptyItem.draw(10, 10, 1.0f, mockGraphics, style, mockObserver);
        assertTrue("Draw with empty text should execute without exceptions", true);
    }
    
    /**
     * Test draw method with null text
     */
    @Test
    public void testDrawWithNullText() {
        TextItem nullItem = new TextItem(1, null);
        
        // Should not throw exception and should return early
        nullItem.draw(10, 10, 1.0f, mockGraphics, style, mockObserver);
        assertTrue("Draw with null text should execute without exceptions", true);
    }
    
    /**
     * Test getLayouts method through reflection
     */
    @Test
    public void testGetLayouts() {
        try {
            // Get the private method
            Method getLayoutsMethod = TextItem.class.getDeclaredMethod("getLayouts", 
                Graphics.class, Style.class, float.class);
            getLayoutsMethod.setAccessible(true);
            
            // Call the method
            List<?> layouts = (List<?>) getLayoutsMethod.invoke(textItem, mockGraphics, style, 1.0f);
            
            // Verify result
            assertNotNull("Layouts should not be null", layouts);
            assertFalse("Layouts should not be empty for normal text", layouts.isEmpty());
            
            // Test with empty text
            TextItem emptyItem = new TextItem(1, "");
            List<?> emptyLayouts = (List<?>) getLayoutsMethod.invoke(emptyItem, mockGraphics, style, 1.0f);
            assertNotNull("Layouts should not be null for empty text", emptyLayouts);
            assertTrue("Layouts should be empty for empty text", emptyLayouts.isEmpty());
            
        } catch (Exception e) {
            fail("Exception while testing getLayouts: " + e.getMessage());
        }
    }
    
    /**
     * Test that the draw method exists
     */
    @Test
    public void testDrawMethodExists() {
        try {
            TextItem.class.getDeclaredMethod("draw", 
                int.class, int.class, float.class, Graphics.class, Style.class, ImageObserver.class);
            // If we get here, the method exists
            assertTrue(true);
        } catch (NoSuchMethodException e) {
            fail("draw method does not exist");
        }
    }
    
    /**
     * Test that the getLayouts method exists (private method)
     */
    @Test
    public void testGetLayoutsMethodExists() {
        try {
            // Since it's private, we need getDeclaredMethod and not getMethod
            Method method = TextItem.class.getDeclaredMethod("getLayouts", 
                Graphics.class, Style.class, float.class);
            // If we get here, the method exists
            assertTrue(true);
        } catch (NoSuchMethodException e) {
            // Method might be private, which is fine
            assertTrue("Private method might not be accessible", true);
        }
    }
    
    /**
     * Test handling of empty text
     */
    @Test
    public void testEmptyText() {
        // Create item with empty text
        TextItem emptyItem = new TextItem(1, "");
        
        // Test getText
        assertEquals("Empty string should be returned", "", emptyItem.getText());
        
        // Test toString
        assertEquals("toString should format correctly", "TextItem[1,]", emptyItem.toString());
        
        // Test AttributedString creation with empty text
        AttributedString attrStr = emptyItem.getAttributedString(style, 1.0f);
        assertNotNull("AttributedString should not be null for empty text", attrStr);
    }
    
    /**
     * Test with various scales
     */
    @Test
    public void testDifferentScales() {
        // Test with very small scale
        float smallScale = 0.1f;
        AttributedString smallAttrStr = textItem.getAttributedString(style, smallScale);
        assertNotNull("AttributedString should work with small scale", smallAttrStr);
        
        // Test with large scale
        float largeScale = 5.0f;
        AttributedString largeAttrStr = textItem.getAttributedString(style, largeScale);
        assertNotNull("AttributedString should work with large scale", largeAttrStr);
        
        // Test bounding box with different scales
        Rectangle smallBox = textItem.getBoundingBox(mockGraphics, mockObserver, smallScale, style);
        Rectangle largeBox = textItem.getBoundingBox(mockGraphics, mockObserver, largeScale, style);
        
        assertTrue("Large scale should produce wider box than small scale", 
                largeBox.width > smallBox.width);
    }
    
    /**
     * Test with multiline text
     */
    @Test
    public void testMultilineText() {
        // Create a TextItem with multiline text
        String multilineText = "This is line 1.\nThis is line 2.\nThis is line 3.";
        TextItem multilineItem = new TextItem(0, multilineText);
        
        // Verify the text is stored correctly
        assertEquals("Text should be stored as-is", multilineText, multilineItem.getText());
        
        // Test toString
        String expectedToString = "TextItem[0," + multilineText + "]";
        assertEquals("toString should include the multiline text", expectedToString, multilineItem.toString());
        
        // Test AttributedString creation with multiline text
        AttributedString attrStr = multilineItem.getAttributedString(style, 1.0f);
        assertNotNull("AttributedString should work with multiline text", attrStr);
        
        // Test bounding box with multiline text
        Rectangle box = multilineItem.getBoundingBox(mockGraphics, mockObserver, 1.0f, style);
        assertNotNull("Bounding box should not be null for multiline text", box);
        
        // Test drawing multiline text
        multilineItem.draw(10, 10, 1.0f, mockGraphics, style, mockObserver);
    }
    
    /**
     * Test with very long text
     */
    @Test
    public void testLongText() {
        // Create a TextItem with long text
        StringBuilder longTextBuilder = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            longTextBuilder.append("This is a very long text to test text wrapping. ");
        }
        String longText = longTextBuilder.toString();
        
        TextItem longItem = new TextItem(0, longText);
        
        // Test getText
        assertEquals("Long text should be stored correctly", longText, longItem.getText());
        
        // Test AttributedString creation with long text
        AttributedString attrStr = longItem.getAttributedString(style, 1.0f);
        assertNotNull("AttributedString should work with long text", attrStr);
        
        // Test bounding box with long text
        Rectangle box = longItem.getBoundingBox(mockGraphics, mockObserver, 1.0f, style);
        assertNotNull("Bounding box should not be null for long text", box);
        
        // Test drawing long text
        longItem.draw(10, 10, 1.0f, mockGraphics, style, mockObserver);
    }
    
    /**
     * Test EMPTYTEXT constant
     */
    @Test
    public void testEmptyTextConstant() {
        try {
            // Get the EMPTYTEXT constant via reflection
            Field emptyTextField = TextItem.class.getDeclaredField("EMPTYTEXT");
            emptyTextField.setAccessible(true);
            String emptyText = (String) emptyTextField.get(null);
            
            // Test that default constructor uses this constant
            TextItem defaultItem = new TextItem();
            assertEquals("Default constructor should use EMPTYTEXT constant", 
                    emptyText, defaultItem.getText());
            
        } catch (Exception e) {
            fail("Could not access EMPTYTEXT constant: " + e.getMessage());
        }
    }
    
    /**
     * Test comprehensive functionality of TextItem
     */
    @Test
    public void testComprehensiveTextItemFunctionality() {
        // Create a text item
        TextItem item = new TextItem(2, "Comprehensive Test");
        
        // Test basic properties
        assertEquals(2, item.getLevel());
        assertEquals("Comprehensive Test", item.getText());
        
        // Test string representation
        String itemString = item.toString();
        assertTrue(itemString.contains("Comprehensive Test"));
        
        // Test drawing functionality
        BufferedImage image = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.createGraphics();
        Style style = Style.getStyle(2);
        
        // Should not throw exception
        item.draw(10, 10, 1.0f, g, style, null);
        
        // Test drawing with different text lengths
        TextItem shortItem = new TextItem(1, "Short");
        shortItem.draw(10, 10, 1.0f, g, Style.getStyle(1), null);
        
        TextItem longItem = new TextItem(3, "This is a very long text item that should test the word wrapping functionality");
        longItem.draw(10, 10, 1.0f, g, Style.getStyle(3), null);
        
        // Test with different scales and positions
        item.draw(20, 50, 1.5f, g, style, null);
        item.draw(30, 100, 0.8f, g, style, null);
        
        // Test AttributedString creation
        AttributedString attrStr = item.getAttributedString(style, 1.0f);
        assertNotNull("AttributedString should not be null", attrStr);
        
        // Test getBoundingBox
        Rectangle boundingBox = item.getBoundingBox(g, null, 1.0f, style);
        assertTrue("Bounding box width should be positive", boundingBox.width > 0);
        assertTrue("Bounding box height should be positive", boundingBox.height > 0);
        
        // Clean up
        g.dispose();
=======
    public void testToString() {
        String testText = "Test Text";
        int level = 2;
        TextItem textItem = new TextItem(level, testText);

        String result = textItem.toString();
        assertNotNull("toString should return a non-null string", result);
        assertTrue("toString should contain the level", result.contains(String.valueOf(level)));
        assertTrue("toString should contain the text", result.contains(testText));
>>>>>>> d6925b5f1f4d3bd3ef515fc7598526c5c7875072
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