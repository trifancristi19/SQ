package com.jabberpoint;

import org.junit.Test;
import org.junit.Before;
import org.junit.BeforeClass;
import static org.junit.Assert.*;

import java.awt.Color;
import java.awt.Font;

/**
 * Tests for the Style class
 */
public class StyleTest {
    
    @BeforeClass
    public static void setUpClass() {
<<<<<<< HEAD
        // Initialize styles before all tests
=======
        // Initialize styles for tests
>>>>>>> d6925b5f1f4d3bd3ef515fc7598526c5c7875072
        Style.createStyles();
    }
    
    /**
     * Test creating styles
     */
    @Test
    public void testCreateStyles() {
        // Create styles again (should reinitialize)
        Style.createStyles();
        
        // Verify we can get styles for each level
        for (int i = 0; i < 5; i++) {
            Style style = Style.getStyle(i);
            assertNotNull("Style should be created for level " + i, style);
            assertNotNull("Font should not be null", style.font);
            assertNotNull("Color should not be null", style.color);
        }
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
     * Test style properties and constructor
     */
    @Test
    public void testStyleProperties() {
        // Create a custom style
        int indent = 20;
        Color color = Color.RED;
        int points = 24;
        int leading = 10;
        
        Style style = new Style(indent, color, points, leading);
        
        // Verify properties
        assertEquals("Color should match", color, style.color);
        assertEquals("Indent should match", indent, style.indent);
        assertEquals("Leading should match", leading, style.leading);
        assertEquals("Font size should match", points, style.fontSize);
    }
    
    /**
     * Test font creation and properties
     */
    @Test
    public void testFontCreation() {
        Style style = new Style(10, Color.RED, 24, 5);
        Font font = style.font;
        
        // The expected font might be "Helvetica" based on FONTNAME constant,
        // but Java might map it to "Dialog" on some platforms
        // So we should either check for Dialog or skip the font family check
        assertEquals("Font style should be bold", Font.BOLD, font.getStyle());
        assertEquals("Font size should match", 24, font.getSize());
    }
    
    /**
     * Test different scaling factors with getFont
     */
    @Test
    public void testGetFontWithDifferentScales() {
        Style style = new Style(10, Color.RED, 20, 5);
        
        // Test with various scaling factors
        float[] scales = {0.5f, 1.0f, 1.5f, 2.0f};
        
        for (float scale : scales) {
            Font scaledFont = style.getFont(scale);
            assertEquals("Font size should scale correctly with " + scale, 
                    20 * scale, scaledFont.getSize2D(), 0.001f);
        }
    }
} 