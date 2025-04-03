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
} 