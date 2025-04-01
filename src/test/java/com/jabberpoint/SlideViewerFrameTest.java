package com.jabberpoint;

import org.junit.Test;
import org.junit.BeforeClass;
import org.junit.Assume;
import static org.junit.Assert.*;

import java.awt.MenuBar;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyListener;

public class SlideViewerFrameTest
{
    @BeforeClass
    public static void setUpClass()
    {
        // Initialize any required static classes
        Style.createStyles();
    }

    @Test
    public void testFrameCreation()
    {
        // Skip test in headless mode
        Assume.assumeFalse("Skipping GUI test in headless environment", 
            GraphicsEnvironment.isHeadless());

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

    @Test
    public void testComponentSetup()
    {
        // Skip test in headless mode
        Assume.assumeFalse("Skipping GUI test in headless environment", 
            GraphicsEnvironment.isHeadless());

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

    @Test
    public void testKeyListenerSetup()
    {
        // Skip test in headless mode
        Assume.assumeFalse("Skipping GUI test in headless environment", 
            GraphicsEnvironment.isHeadless());

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
} 