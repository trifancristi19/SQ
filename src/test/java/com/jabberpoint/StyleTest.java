package com.jabberpoint;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.BeforeClass;
import static org.junit.Assert.*;

import java.awt.Color;
import java.awt.Font;
import java.lang.reflect.Field;

/**
 * Tests for the Style class
 */
public class StyleTest {

    @BeforeClass
    public static void setUpClass() {
        // Create styles for testing
        Style.createStyles();
    }
    
    @Before
    public void setUp() {
        // No specific setup needed for each test
    }
    
    @After
    public void tearDown() {
        // No specific teardown needed for each test
    }
    
    /**
     * Test creating styles
     */
    @Test
    public void testCreateStyles() {
        // Call createStyles again to ensure it works multiple times
        Style.createStyles();
        
        // Verify the styles were created properly
        assertNotNull("Default style should exist", Style.getStyle(0));
        assertNotNull("Style for level 1 should exist", Style.getStyle(1));
        assertNotNull("Style for level 2 should exist", Style.getStyle(2));
        assertNotNull("Style for level 3 should exist", Style.getStyle(3));
        assertNotNull("Style for level 4 should exist", Style.getStyle(4));
    }
    
    /**
     * Test getting styles by level
     */
    @Test
    public void testGetStyle() {
        // Test each style level
        for (int level = 0; level <= 4; level++) {
            Style style = Style.getStyle(level);
            assertNotNull("Style for level " + level + " should exist", style);
            
            // Test properties of style
            assertNotNull("Font should not be null", style.font);
            assertNotNull("Color should not be null", style.color);
        }
        
        // Test getting style with negative level (should return default style)
        assertEquals("Negative level should return default style", 
                Style.getStyle(0), Style.getStyle(-1));
        
        // Test getting style with too high level (should return highest defined style)
        assertEquals("Too high level should return highest defined style",
                Style.getStyle(4), Style.getStyle(10));
    }
    
    /**
     * Test style constructors and property access
     */
    @Test
    public void testStyleProperties() {
        int indent = 15;
        Color color = Color.RED;
        int fontSize = 20;
        int leading = 10;
        
        // Create style with all properties
        Style style = new Style(indent, color, fontSize, leading);
        
        // Test all properties
        assertEquals("Indent should match", indent, style.indent);
        assertEquals("Font size should match", fontSize, style.fontSize);
        assertEquals("Leading should match", leading, style.leading);
        assertEquals("Color should match", color, style.color);
    }
    
    /**
     * Test getFont method with scaling
     */
    @Test
    public void testGetFontWithScale() {
        Style style = new Style(10, Color.RED, 20, 5);
        float scale = 2.0f;
        Font scaledFont = style.getFont(scale);
        
        assertNotNull("Scaled font should not be null", scaledFont);
        assertEquals("Font size should be scaled correctly", 
                20 * scale, scaledFont.getSize2D(), 0.001f);
    }
    
    /**
     * Test toString method
     */
    @Test
    public void testToString() {
        Style style = new Style(10, Color.RED, 20, 5);
        String styleString = style.toString();
        
        assertNotNull("String representation should not be null", styleString);
        assertTrue("String should contain indent", styleString.contains("10"));
        assertTrue("String should contain font size", styleString.contains("20"));
        assertTrue("String should contain leading", styleString.contains("5"));
    }
    
    /**
     * Test Style.styles array for internal state
     */
    @Test
    public void testStylesArray() throws Exception {
        // Access the styles array using reflection
        Field stylesField = Style.class.getDeclaredField("styles");
        stylesField.setAccessible(true);
        Object stylesObj = stylesField.get(null);
        
        // Verify it's a Style array
        assertTrue("styles should be a Style array", stylesObj instanceof Style[]);
        
        // Check the length of the array
        Style[] styles = (Style[]) stylesObj;
        assertEquals("Should have 5 styles defined", 5, styles.length);
        
        // Test that styles in the array match what getStyle returns
        for (int i = 0; i < styles.length; i++) {
            assertEquals("Style in array should match getStyle", 
                    styles[i], Style.getStyle(i));
        }
    }
} 