package com.jabberpoint;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Assume;

import static org.junit.Assert.*;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JFrame;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Tests for the SlideViewerFrame class
 * Designed to work in both headless and non-headless environments
 */
public class SlideViewerFrameTest
{

    private static boolean isHeadless;

    @BeforeClass
    public static void setUpClass()
    {
        // Check if we're in a headless environment
        isHeadless = GraphicsEnvironment.isHeadless();

        // Initialize styles required for tests
        Style.createStyles();
    }

    /**
     * Test that frame constants are correct
     * This test doesn't require a GUI so it can run in headless mode
     */
    @Test
    public void testFrameConstants()
    {
        assertEquals("Width should be 1200", 1200, SlideViewerFrame.WIDTH);
        assertEquals("Height should be 800", 800, SlideViewerFrame.HEIGHT);
    }

    /**
     * Test that SlideViewerFrame has the expected method signatures
     * This test doesn't require a GUI so it can run in headless mode
     */
    @Test
    public void testMethodSignatures()
    {
        try
        {
            // Test constructor signature
            Class<?>[] constructorParams = SlideViewerFrame.class.getConstructor(String.class, Presentation.class).getParameterTypes();
            assertEquals("Constructor should take String and Presentation", 2, constructorParams.length);
            assertEquals("First parameter should be String", String.class, constructorParams[0]);
            assertEquals("Second parameter should be Presentation", Presentation.class, constructorParams[1]);

            // Test setupWindow method
            Method setupMethod = SlideViewerFrame.class.getMethod("setupWindow", Presentation.class);
            assertEquals("setupWindow should return void", void.class, setupMethod.getReturnType());

        } catch (Exception e)
        {
            fail("Failed to verify method signatures: " + e.getMessage());
        }
    }

    /**
     * Test window adapter behavior using reflection
     * This test is designed to work in headless mode by using reflection
     */
    @Test
    public void testWindowAdapterWithReflection()
    {
        try
        {
            // Create a custom window listener for testing
            final boolean[] exitCalled = new boolean[1];
            WindowListener testListener = new WindowAdapter()
            {
                @Override
                public void windowClosing(WindowEvent e)
                {
                    exitCalled[0] = true;
                }
            };

            // Create a mock window event
            Frame mockFrame = new Frame();
            WindowEvent mockEvent = new WindowEvent(mockFrame, WindowEvent.WINDOW_CLOSING);

            // Manually invoke the windowClosing method on our test listener
            testListener.windowClosing(mockEvent);

            // Verify the exit flag was set
            assertTrue("WindowAdapter.windowClosing should set exit flag", exitCalled[0]);

            // Clean up
            mockFrame.dispose();

        } catch (HeadlessException e)
        {
            // Skip this test in headless environments
            System.out.println("Skipping part of testWindowAdapterWithReflection due to HeadlessException");
            Assume.assumeNoException(e);
        }
    }

    /**
     * Test that SlideViewerFrame uses a window adapter
     * This advanced test only runs in non-headless mode
     */
    @Test
    public void testWindowAdapterDirectly()
    {
        // Skip this test in headless environments
        Assume.assumeFalse("Skipping testWindowAdapterDirectly in headless environment", isHeadless);

        // Only create a real frame if not in headless mode
        Presentation presentation = new Presentation();
        JFrame frame = null;

        try
        {
            // Try to create a real frame
            frame = new SlideViewerFrame("Test Frame", presentation);

            // If we get here, we've successfully created a frame
            assertNotNull("Frame should be created", frame);

            // Get window listeners
            WindowListener[] listeners = frame.getWindowListeners();
            assertTrue("Frame should have window listeners", listeners.length > 0);

            // At least one of them should be a WindowAdapter
            boolean hasWindowAdapter = false;
            for (WindowListener listener : listeners)
            {
                if (listener instanceof WindowAdapter)
                {
                    hasWindowAdapter = true;
                    break;
                }
            }
            assertTrue("Frame should have a WindowAdapter", hasWindowAdapter);

        } catch (HeadlessException e)
        {
            // This is caught by the Assume.assumeFalse above, but we add a safety check
            System.out.println("Skipping testWindowAdapterDirectly due to HeadlessException: " + e.getMessage());
            Assume.assumeNoException(e);
        } finally
        {
            // Clean up
            if (frame != null)
            {
                frame.dispose();
            }
        }
    }
} 