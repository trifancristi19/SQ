package com.jabberpoint;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import java.awt.Frame;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;

public class MenuControllerTest {
    
    private Frame frame;
    private Presentation presentation;
    private MenuController menuController;
    
    @Before
    public void setUp() {
        frame = new Frame();
        presentation = new Presentation();
        menuController = new MenuController(frame, presentation);
    }
    
    @Test
    public void testMenuCreation() {
        assertNotNull("MenuController should be created", menuController);
        
        // Check that the menus are created
        int menuCount = menuController.getMenuCount();
        assertTrue("MenuController should have at least one menu", menuCount > 0);
    }
    
    @Test
    public void testFileMenuExists() {
        Menu fileMenu = null;
        
        // Find the File menu
        for (int i = 0; i < menuController.getMenuCount(); i++) {
            Menu menu = menuController.getMenu(i);
            if (menu.getLabel().equals(MenuController.FILE)) {
                fileMenu = menu;
                break;
            }
        }
        
        assertNotNull("File menu should exist", fileMenu);
        
        // Check that the file menu has items
        int itemCount = fileMenu.getItemCount();
        assertTrue("File menu should have items", itemCount > 0);
        
        // Check for specific menu items
        boolean hasOpen = false;
        boolean hasNew = false;
        boolean hasSave = false;
        boolean hasExit = false;
        
        for (int i = 0; i < itemCount; i++) {
            MenuItem item = fileMenu.getItem(i);
            if (item != null) {
                String label = item.getLabel();
                if (label.equals(MenuController.OPEN)) hasOpen = true;
                if (label.equals(MenuController.NEW)) hasNew = true;
                if (label.equals(MenuController.SAVE)) hasSave = true;
                if (label.equals(MenuController.EXIT)) hasExit = true;
            }
        }
        
        assertTrue("File menu should have Open item", hasOpen);
        assertTrue("File menu should have New item", hasNew);
        assertTrue("File menu should have Save item", hasSave);
        assertTrue("File menu should have Exit item", hasExit);
    }
    
    @Test
    public void testViewMenuExists() {
        Menu viewMenu = null;
        
        // Find the View menu
        for (int i = 0; i < menuController.getMenuCount(); i++) {
            Menu menu = menuController.getMenu(i);
            if (menu.getLabel().equals(MenuController.VIEW)) {
                viewMenu = menu;
                break;
            }
        }
        
        assertNotNull("View menu should exist", viewMenu);
        
        // Check that the view menu has items
        int itemCount = viewMenu.getItemCount();
        assertTrue("View menu should have items", itemCount > 0);
        
        // Check for specific menu items
        boolean hasNext = false;
        boolean hasPrev = false;
        
        for (int i = 0; i < itemCount; i++) {
            MenuItem item = viewMenu.getItem(i);
            if (item != null) {
                String label = item.getLabel();
                if (label.equals(MenuController.NEXT)) hasNext = true;
                if (label.equals(MenuController.PREV)) hasPrev = true;
            }
        }
        
        assertTrue("View menu should have Next item", hasNext);
        assertTrue("View menu should have Prev item", hasPrev);
    }
} 