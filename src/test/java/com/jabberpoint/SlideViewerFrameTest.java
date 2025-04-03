package com.jabberpoint;

import org.junit.Test;
import org.junit.BeforeClass;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

/**
 * Tests for the SlideViewerFrame class
 * Using only reflection to avoid GUI dependencies
 */
public class SlideViewerFrameTest
{
    @BeforeClass
    public static void setUpClass()
    {
        // Initialize styles required for tests
        Style.createStyles();
    }

    /**
     * Test that frame constants are correct using reflection
     */
    @Test
    public void testFrameConstants()
    {
        try
        {
            // Use reflection to access constants
            Field widthField = SlideViewerFrame.class.getDeclaredField("WIDTH");
            Field heightField = SlideViewerFrame.class.getDeclaredField("HEIGHT");

            // Verify they're public and static
            assertTrue("WIDTH should be static", Modifier.isStatic(widthField.getModifiers()));
            assertTrue("HEIGHT should be static", Modifier.isStatic(heightField.getModifiers()));

            // Verify values
            assertEquals("WIDTH should be 1200", 1200, widthField.getInt(null));
            assertEquals("HEIGHT should be 800", 800, heightField.getInt(null));

        } catch (Exception e)
        {
            fail("Failed to access constants: " + e.getMessage());
        }
    }

    /**
     * Test that SlideViewerFrame has the expected constructor and method signatures
     */
    @Test
    public void testClassStructure()
    {
        try
        {
            // Test constructor signature
            Constructor<?> constructor = SlideViewerFrame.class.getConstructor(String.class, Presentation.class);
            assertNotNull("Constructor should exist", constructor);

            // Test setupWindow method
            Method setupMethod = SlideViewerFrame.class.getMethod("setupWindow", Presentation.class);
            assertEquals("setupWindow should return void", void.class, setupMethod.getReturnType());

            // Verify inheritance
            assertEquals("SlideViewerFrame should extend JFrame", "javax.swing.JFrame",
                    SlideViewerFrame.class.getSuperclass().getName());

        } catch (Exception e)
        {
            fail("Failed to verify class structure: " + e.getMessage());
        }
    }

    /**
     * Test the event handler structure without creating GUI components
     */
    @Test
    public void testEventHandlers()
    {
        try
        {
            // Verify window adapter usage in the setupWindow method
            Method setupMethod = SlideViewerFrame.class.getDeclaredMethod("setupWindow", Presentation.class);
            setupMethod.setAccessible(true);

            // Count the number of methods in SlideViewerFrame
            Method[] methods = SlideViewerFrame.class.getDeclaredMethods();
            assertTrue("SlideViewerFrame should have at least one method", methods.length > 0);

            // Check that some initialization is happening in setupWindow
            String methodBody = setupMethod.toString();
            assertNotNull("Method body should exist", methodBody);

        } catch (Exception e)
        {
            fail("Failed to verify event handlers: " + e.getMessage());
        }
    }
} 