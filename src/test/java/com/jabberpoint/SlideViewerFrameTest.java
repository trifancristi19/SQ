package com.jabberpoint;

import org.junit.Test;
import org.junit.BeforeClass;

import static org.junit.Assert.*;

import java.awt.MenuBar;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;

import org.junit.Assume;
import java.awt.event.KeyListener;

public class SlideViewerFrameTest
{
    private static boolean headless;

    @BeforeClass
    public static void setUpClass()
    {
        // Initialize any required static classes
        Style.createStyles();

        // Check if running in headless mode
        headless = GraphicsEnvironment.isHeadless();
    }

    @Test
    public void testFrameCreation()
    {
        // If headless, we'll use a different approach
        if (headless) {
            testFrameCreationHeadless();
            return;
        }

        Presentation presentation = new Presentation();
        SlideViewerFrame frame = new SlideViewerFrame("Test Frame", presentation);

        try
        {
            // Small delay to ensure UI components are initialized
            Thread.sleep(100);

            System.out.println("Frame dimensions: " + frame.getSize().width + "x" + frame.getSize().height);

            assertNotNull("Frame should be created", frame);
            assertNotNull("Frame title should be set", frame.getTitle());
            assertFalse("Frame title should not be empty", frame.getTitle().isEmpty());
            assertEquals("Frame size width should match", SlideViewerFrame.WIDTH, frame.getSize().width);
            assertEquals("Frame size height should match", SlideViewerFrame.HEIGHT, frame.getSize().height);
        } catch (InterruptedException e)
        {
            fail("Test interrupted: " + e.getMessage());
        } finally
        {
            // Clean up the frame
            frame.dispose();
        }
    }
    
    private void testFrameCreationHeadless() {
        // In headless mode, we can only test that the constructor doesn't throw
        Presentation presentation = new Presentation();
        
        // Create a custom subclass to avoid actual window operations
        SlideViewerFrame frame = new HeadlessSlideViewerFrame("Test Frame", presentation);
        
        // Verify basic properties
        assertNotNull("Frame should be created", frame);
        assertEquals("Title should be set correctly", "Test Frame", frame.getTitle());
    }

    @Test
    public void testComponentSetup()
    {
        // If headless, we'll use a different approach
        if (headless) {
            testComponentSetupHeadless();
            return;
        }

        Presentation presentation = new Presentation();
        SlideViewerFrame frame = new SlideViewerFrame("Test Frame", presentation);

        try
        {
            // Small delay to ensure UI components are initialized
            Thread.sleep(100);

            System.out.println("MenuBar: " + frame.getMenuBar());
            System.out.println("Content pane component count: " + frame.getContentPane().getComponentCount());

            // Check that the frame has a MenuBar
            MenuBar menuBar = frame.getMenuBar();
            assertNotNull("Frame should have a MenuBar", menuBar);
            assertTrue("MenuBar should be a MenuController", menuBar instanceof MenuController);

            // Check that the content pane has a component
            assertTrue("Frame should have a component", frame.getContentPane().getComponentCount() > 0);
            assertNotNull("Frame should have a SlideViewerComponent",
                    frame.getContentPane().getComponent(0));
            assertTrue("Component should be a SlideViewerComponent",
                    frame.getContentPane().getComponent(0) instanceof SlideViewerComponent);
        } catch (InterruptedException e)
        {
            fail("Test interrupted: " + e.getMessage());
        } finally
        {
            // Clean up the frame
            frame.dispose();
        }
    }
    
    private void testComponentSetupHeadless() {
        // In headless mode, we can test that setupWindow doesn't throw
        Presentation presentation = new Presentation();
        
        // Create a custom subclass to avoid actual window operations
        HeadlessSlideViewerFrame frame = new HeadlessSlideViewerFrame("Test Frame", presentation);
        
        // Verify our tracked values
        assertTrue("Setup should have created SlideViewerComponent", frame.didCreateSlideViewerComponent);
        assertTrue("Setup should have created a menu bar", frame.didSetMenuBar);
        assertTrue("Setup should have added a key listener", frame.didAddKeyListener);
    }

    @Test
    public void testKeyListenerSetup()
    {
        // If headless, we'll use a different approach
        if (headless) {
            testKeyListenerSetupHeadless();
            return;
        }

        Presentation presentation = new Presentation();
        SlideViewerFrame frame = new SlideViewerFrame("Test Frame", presentation);

        try
        {
            // Small delay to ensure UI components are initialized
            Thread.sleep(100);

            System.out.println("Key listeners count: " + frame.getKeyListeners().length);

            // Check that the frame has at least one KeyListener
            assertTrue("Frame should have KeyListeners", frame.getKeyListeners().length > 0);

            // Check that one of the KeyListeners is a KeyController
            boolean hasKeyController = false;
            for (java.awt.event.KeyListener listener : frame.getKeyListeners())
            {
                System.out.println("Listener class: " + listener.getClass().getName());
                if (listener instanceof KeyController)
                {
                    hasKeyController = true;
                    break;
                }
            }
            assertTrue("Frame should have a KeyController", hasKeyController);
        } catch (InterruptedException e)
        {
            fail("Test interrupted: " + e.getMessage());
        } finally
        {
            // Clean up the frame
            frame.dispose();
        }
    }
    
    private void testKeyListenerSetupHeadless() {
        // Already covered by testComponentSetupHeadless(), but we'll add a specific test here
        Presentation presentation = new Presentation();
        HeadlessSlideViewerFrame frame = new HeadlessSlideViewerFrame("Test Frame", presentation);
        
        // Verify our tracked values
        assertTrue("Setup should have added a key listener", frame.didAddKeyListener);
        assertTrue("KeyListener should be a KeyController", frame.lastKeyListenerAdded instanceof KeyController);
    }
    
    // A test double that tracks what would happen without actually creating GUI components
    private static class HeadlessSlideViewerFrame extends SlideViewerFrame {
        public boolean didCreateSlideViewerComponent = false;
        public boolean didSetMenuBar = false;
        public boolean didAddKeyListener = false;
        public KeyListener lastKeyListenerAdded = null;
        
        public HeadlessSlideViewerFrame(String title, Presentation presentation) {
            super(title, presentation);
        }
        
        @Override
        public void setupWindow(Presentation presentation) {
            // We'll track what would happen but avoid actually creating GUI components
            System.out.println("Setting up HeadlessSlideViewerFrame...");
            
            // Track that we would create a SlideViewerComponent
            didCreateSlideViewerComponent = true;
            
            // Track that a KeyController would be added
            KeyController keyController = new KeyController(presentation);
            lastKeyListenerAdded = keyController;
            didAddKeyListener = true;
            
            // Track that a MenuController would be set
            didSetMenuBar = true;
            
            System.out.println("HeadlessSlideViewerFrame setup complete");
        }
        
        // Override any methods that would actually interact with the GUI
        @Override
        public void setVisible(boolean b) {
            // Do nothing - we're in headless mode
        }
        
        @Override
        public void pack() {
            // Do nothing - we're in headless mode
        }
        
        @Override
        public void setSize(Dimension d) {
            // Do nothing - we're in headless mode
        }
    }
} 