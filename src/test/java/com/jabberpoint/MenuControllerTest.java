package com.jabberpoint;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import java.awt.MenuBar;
import java.awt.Frame;
import java.awt.Menu;
import java.awt.MenuItem;
import java.lang.reflect.Field;

import com.jabberpoint.error.ErrorHandler;

/**
 * Test class for MenuController that tests only static constants and does not require
 * GUI components, avoiding headless environment issues.
 */
public class MenuControllerTest
{
    private Frame frame;
    private Presentation presentation;
    private MenuController menuController;

    @Before
    public void setUp()
    {
        frame = new Frame("Test Frame");
        presentation = new Presentation();
        menuController = new MenuController(frame, presentation);
    }

    /**
     * Test constructor
     */
    @Test
    public void testConstructor()
    {
        assertNotNull("MenuController should be created", menuController);
        
        // Verify frame was stored
        try {
            Field parentField = MenuController.class.getDeclaredField("parent");
            parentField.setAccessible(true);
            Object storedFrame = parentField.get(menuController);
            assertSame("Frame should be stored", frame, storedFrame);
        } catch (Exception e) {
            fail("Exception accessing field: " + e.getMessage());
        }
        
        // Verify presentation was stored
        try {
            Field presentationField = MenuController.class.getDeclaredField("presentation");
            presentationField.setAccessible(true);
            Object storedPresentation = presentationField.get(menuController);
            assertSame("Presentation should be stored", presentation, storedPresentation);
        } catch (Exception e) {
            fail("Exception accessing field: " + e.getMessage());
        }
        
        // Verify error handler was created
        try {
            Field errorHandlerField = MenuController.class.getDeclaredField("errorHandler");
            errorHandlerField.setAccessible(true);
            Object errorHandler = errorHandlerField.get(menuController);
            assertNotNull("ErrorHandler should be created", errorHandler);
            assertTrue("ErrorHandler should be of correct type", errorHandler instanceof ErrorHandler);
        } catch (Exception e) {
            fail("Exception accessing field: " + e.getMessage());
        }
    }

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