package com.jabberpoint;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test class for MenuController that tests only static constants and does not require
 * GUI components, avoiding headless environment issues.
 */
public class MenuControllerTest
{

    @Test
    public void testMenuItemConstants()
    {
        assertEquals("About", MenuController.ABOUT);
        assertEquals("Help", MenuController.HELP);
        assertEquals("New", MenuController.NEW);
        assertEquals("Next", MenuController.NEXT);
        assertEquals("Open", MenuController.OPEN);
        assertEquals("Prev", MenuController.PREV);
        assertEquals("Exit", MenuController.EXIT);
        assertEquals("Save", MenuController.SAVE);
        assertEquals("File", MenuController.FILE);
        assertEquals("View", MenuController.VIEW);
        assertEquals("Go to", MenuController.GOTO);
        assertEquals("Page number?", MenuController.PAGENR);
    }

    @Test
    public void testFileConstants()
    {
        assertEquals("test.xml", MenuController.TESTFILE);
        assertEquals("dump.xml", MenuController.SAVEFILE);
    }

    @Test
    public void testErrorConstants()
    {
        assertEquals("IO Exception: ", MenuController.IOEX);
        assertEquals("Load Error", MenuController.LOADERR);
        assertEquals("Save Error", MenuController.SAVEERR);
    }

    @Test
    public void testClassName()
    {
        // Simple test to verify the class name
        assertEquals("MenuController", MenuController.class.getSimpleName());
    }
} 