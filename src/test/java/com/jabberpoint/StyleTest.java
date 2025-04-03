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
        // Initialize styles before all tests
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
} 