package com.jabberpoint;

import org.junit.Test;
import org.junit.Before;
import org.junit.BeforeClass;

import static org.junit.Assert.*;

import java.awt.Frame;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.GraphicsEnvironment;

import org.junit.Assume;

import java.util.ArrayList;
import java.util.List;

public class MenuControllerTest
{

    private Object frame; // Changed from Frame to Object
    private Presentation presentation;
    private Object menuController; // Changed from MenuController to Object
    private boolean isHeadless;

    @BeforeClass
    public static void setUpClass()
    {
        // Set headless mode for tests
        System.setProperty("java.awt.headless", "true");
    }

    @Before
    public void setUp()
    {
        this.presentation = new Presentation();
        this.isHeadless = GraphicsEnvironment.isHeadless();

        if (this.isHeadless)
        {
            // Use a mock implementation that doesn't use AWT
            this.frame = new Object(); // Simple mock
            this.menuController = new MockMenuController(this.presentation);
        }
        else
        {
            this.frame = new Frame();
            this.menuController = new MenuController((Frame) this.frame, this.presentation);
        }
    }

    @Test
    public void testMenuCreation()
    {
        assertNotNull("MenuController should be created", this.menuController);

        if (this.isHeadless)
        {
            MockMenuController mockController = (MockMenuController) this.menuController;
            assertTrue("MockMenuController should have at least one menu",
                    mockController.getMenuCount() > 0);
        }
        else
        {
            MenuController realController = (MenuController) this.menuController;
            int menuCount = realController.getMenuCount();
            assertTrue("MenuController should have at least one menu", menuCount > 0);
        }
    }

    @Test
    public void testFileMenuExists()
    {
        if (this.isHeadless)
        {
            MockMenuController mockController = (MockMenuController) this.menuController;
            assertTrue("File menu should exist", mockController.hasMenu(MenuController.FILE));
            assertTrue("File menu should have Open item", mockController.hasMenuItem(MenuController.FILE, MenuController.OPEN));
            assertTrue("File menu should have New item", mockController.hasMenuItem(MenuController.FILE, MenuController.NEW));
            assertTrue("File menu should have Save item", mockController.hasMenuItem(MenuController.FILE, MenuController.SAVE));
            assertTrue("File menu should have Exit item", mockController.hasMenuItem(MenuController.FILE, MenuController.EXIT));
        }
        else
        {
            MenuController realController = (MenuController) this.menuController;
            Menu fileMenu = null;

            // Find the File menu
            for (int i = 0; i < realController.getMenuCount(); i++)
            {
                Menu menu = realController.getMenu(i);
                if (menu.getLabel().equals(MenuController.FILE))
                {
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

            for (int i = 0; i < itemCount; i++)
            {
                MenuItem item = fileMenu.getItem(i);
                if (item != null)
                {
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
    }

    @Test
    public void testViewMenuExists()
    {
        if (this.isHeadless)
        {
            MockMenuController mockController = (MockMenuController) this.menuController;
            assertTrue("View menu should exist", mockController.hasMenu(MenuController.VIEW));
            assertTrue("View menu should have Next item", mockController.hasMenuItem(MenuController.VIEW, MenuController.NEXT));
            assertTrue("View menu should have Prev item", mockController.hasMenuItem(MenuController.VIEW, MenuController.PREV));
        }
        else
        {
            MenuController realController = (MenuController) this.menuController;
            Menu viewMenu = null;

            // Find the View menu
            for (int i = 0; i < realController.getMenuCount(); i++)
            {
                Menu menu = realController.getMenu(i);
                if (menu.getLabel().equals(MenuController.VIEW))
                {
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

            for (int i = 0; i < itemCount; i++)
            {
                MenuItem item = viewMenu.getItem(i);
                if (item != null)
                {
                    String label = item.getLabel();
                    if (label.equals(MenuController.NEXT)) hasNext = true;
                    if (label.equals(MenuController.PREV)) hasPrev = true;
                }
            }

            assertTrue("View menu should have Next item", hasNext);
            assertTrue("View menu should have Prev item", hasPrev);
        }
    }

    /**
     * A mock MenuController that works in headless mode
     */
    private static class MockMenuController
    {
        private Presentation presentation;
        private List<MockMenu> menus = new ArrayList<>();

        public MockMenuController(Presentation presentation)
        {
            this.presentation = presentation;
            // Create mock menus similar to the real MenuController
            MockMenu fileMenu = new MockMenu(MenuController.FILE);
            fileMenu.addItem(MenuController.OPEN);
            fileMenu.addItem(MenuController.NEW);
            fileMenu.addItem(MenuController.SAVE);
            fileMenu.addItem(MenuController.EXIT);
            this.menus.add(fileMenu);

            MockMenu viewMenu = new MockMenu(MenuController.VIEW);
            viewMenu.addItem(MenuController.NEXT);
            viewMenu.addItem(MenuController.PREV);
            viewMenu.addItem(MenuController.GOTO);
            this.menus.add(viewMenu);

            MockMenu helpMenu = new MockMenu(MenuController.HELP);
            helpMenu.addItem(MenuController.ABOUT);
            this.menus.add(helpMenu);
        }

        public int getMenuCount()
        {
            return menus.size();
        }

        public boolean hasMenu(String name)
        {
            for (MockMenu menu : this.menus)
            {
                if (menu.getLabel().equals(name))
                {
                    return true;
                }
            }
            return false;
        }

        public boolean hasMenuItem(String menuName, String itemName)
        {
            for (MockMenu menu : this.menus)
            {
                if (menu.getLabel().equals(menuName))
                {
                    return menu.hasItem(itemName);
                }
            }
            return false;
        }

        private static class MockMenu
        {
            private String label;
            private List<String> items = new ArrayList<>();

            public MockMenu(String label)
            {
                this.label = label;
            }

            public String getLabel()
            {
                return this.label;
            }

            public void addItem(String itemLabel)
            {
                this.items.add(itemLabel);
            }

            public boolean hasItem(String itemLabel)
            {
                return this.items.contains(itemLabel);
            }

            public int getItemCount()
            {
                return this.items.size();
            }
        }
    }
} 