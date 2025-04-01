package com.jabberpoint;

import org.junit.Test;
import org.junit.BeforeClass;

import static org.junit.Assert.*;

import java.awt.Color;
import java.awt.Font;

public class StyleTest
{

    @BeforeClass
    public static void setUpClass()
    {
        Style.createStyles();
    }

    @Test
    public void testStyleCreation()
    {
        Style style = new Style(10, Color.RED, 20, 5);
        assertNotNull("Style should be created", style);
        assertEquals("Indent should be set correctly", 10, style.indent);
        assertEquals("Color should be set correctly", Color.RED, style.color);
        assertEquals("Font size should be set correctly", 20, style.fontSize);
        assertEquals("Leading should be set correctly", 5, style.leading);
    }

    @Test
    public void testGetStyle()
    {
        // Test getting style for different levels
        Style style0 = Style.getStyle(0);
        assertNotNull("Style for level 0 should exist", style0);
        assertEquals("Style for level 0 should have correct indent", 0, style0.indent);
        assertEquals("Style for level 0 should have correct color", Color.red, style0.color);

        Style style1 = Style.getStyle(1);
        assertNotNull("Style for level 1 should exist", style1);
        assertEquals("Style for level 1 should have correct indent", 20, style1.indent);
        assertEquals("Style for level 1 should have correct color", Color.blue, style1.color);
    }

    @Test
    public void testGetStyleWithHighLevel()
    {
        // Test getting style for level above the available styles
        // Should return the highest defined style
        Style highestStyle = Style.getStyle(100);
        Style lastDefinedStyle = Style.getStyle(4);

        assertEquals("Should return the last defined style for high levels",
                lastDefinedStyle.indent, highestStyle.indent);
        assertEquals("Should return the last defined style for high levels",
                lastDefinedStyle.color, highestStyle.color);
    }

    @Test
    public void testGetFont()
    {
        Style style = new Style(10, Color.RED, 20, 5);
        float scale = 2.0f;
        Font font = style.getFont(scale);

        assertNotNull("Font should be created", font);
        assertEquals("Font should be scaled correctly", 40.0f, font.getSize2D(), 0.001f);
    }

    @Test
    public void testToString()
    {
        Style style = new Style(10, Color.RED, 20, 5);
        String styleString = style.toString();

        assertNotNull("String representation should be created", styleString);
        assertTrue("String should contain indent", styleString.contains("10"));
        assertTrue("String should contain color", styleString.contains("java.awt.Color"));
        assertTrue("String should contain font size", styleString.contains("20"));
        assertTrue("String should contain leading", styleString.contains("5"));
    }
} 