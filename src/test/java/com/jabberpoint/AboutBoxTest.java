package com.jabberpoint;

import org.junit.Test;
<<<<<<< HEAD
=======
import org.junit.Before;
import org.junit.After;
import org.junit.Assume;
import org.junit.BeforeClass;
>>>>>>> d6925b5f1f4d3bd3ef515fc7598526c5c7875072
import static org.junit.Assert.*;

import java.awt.GraphicsEnvironment;
<<<<<<< HEAD

public class AboutBoxTest {
    
    @Test
    public void testShowDoesNotThrow() {
        // Skip test in headless environment
        if (GraphicsEnvironment.isHeadless()) {
            System.out.println("Skipping AboutBoxTest in headless environment");
            return;
        }
        
        try {
            AboutBox.show(null); // Use null instead of Frame to avoid HeadlessException
            assertTrue(true); // If we made it here, no exception was thrown
        } catch (Exception e) {
            fail("AboutBox.show threw an exception: " + e.getMessage());
=======
import java.awt.HeadlessException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.lang.reflect.Field;
import java.awt.Dialog;
import java.awt.Component;
import java.awt.Button;
import java.awt.Label;
import java.awt.event.ActionEvent;

/**
 * Tests for the AboutBox class
 */
public class AboutBoxTest {
    
    private static boolean isHeadless;
    private Frame parentFrame;
    private static boolean showMessageDialogCalled;
    private static Object[] lastShowMessageDialogArgs;
    
    @BeforeClass
    public static void setUpClass() {
        // Check if we're in a headless environment
        isHeadless = GraphicsEnvironment.isHeadless();
        
        // Set up test tracking
        mockJOptionPane();
    }
    
    @Before
    public void setUp() {
        // Set up frame only if not headless
        if (!isHeadless) {
            try {
                parentFrame = new JFrame("Test Parent");
            } catch (HeadlessException e) {
                // If we still get a HeadlessException, make sure we mark as headless
                isHeadless = true;
            }
        }
        
        // Reset tracking variables
        showMessageDialogCalled = false;
        lastShowMessageDialogArgs = null;
        
        // Enable test mode for AboutBox
        AboutBox.setTestMode(true);
    }
    
    @After
    public void tearDown() {
        // Clean up frame if it exists
        if (parentFrame != null) {
            parentFrame.dispose();
            parentFrame = null;
        }
        
        // Disable test mode for AboutBox
        AboutBox.setTestMode(false);
    }
    
    /**
     * Set up our mock tracking
     */
    private static void mockJOptionPane() {
        // The mocking is now handled by AboutBox.setTestMode(true)
        showMessageDialogCalled = false;
        lastShowMessageDialogArgs = null;
    }
    
    /**
     * Test calling show method directly to ensure code coverage
     */
    @Test
    public void testShowMethodDirectly() {
        // This test will now work in headless mode too
        
        // Call AboutBox directly
        AboutBox.show(parentFrame);
        
        // Get the test arguments that would have been passed to showMessageDialog
        Object[] testArgs = AboutBox.getLastTestArgs();
        
        // Verify the test data
        assertNotNull("Test arguments should not be null", testArgs);
        assertEquals("Parent should match", parentFrame, testArgs[0]);
        
        String message = (String) testArgs[1];
        String title = (String) testArgs[2];
        
        assertNotNull("Message should not be null", message);
        assertTrue("Message should contain copyright info", 
                message.contains("Copyright") && 
                message.contains("Darwin"));
        
        assertEquals("Title should be 'About JabberPoint'", "About JabberPoint", title);
    }
    
    /**
     * Test AboutBox functionality in a headless environment
     * This ensures we get coverage even when running in CI environments
     */
    @Test
    public void testAboutBoxHeadless() {
        // This test will always run, even in headless mode
        
        // Call AboutBox with null parent
        AboutBox.show(null);
        
        // Get the test arguments
        Object[] testArgs = AboutBox.getLastTestArgs();
        
        // Verify the test data
        assertNotNull("Test arguments should not be null", testArgs);
        assertNull("Parent should be null", testArgs[0]);
        
        String message = (String) testArgs[1];
        String title = (String) testArgs[2];
        
        assertNotNull("Message should not be null", message);
        assertTrue("Message should contain copyright info", 
                message.contains("Copyright") && 
                message.contains("Darwin"));
        
        assertEquals("Title should be 'About JabberPoint'", "About JabberPoint", title);
    }
    
    /**
     * Test using reflection to directly call the AboutBox.show method
     * This approach ensures code coverage even in CI environments
     */
    @Test
    public void testShowMethodWithReflection() {
        try {
            // Call our AboutBox.show method via reflection
            Method showMethod = AboutBox.class.getDeclaredMethod("show", Frame.class);
            showMethod.setAccessible(true);
            showMethod.invoke(null, (Frame)null);
            
            // Verify the test data was captured
            Object[] testArgs = AboutBox.getLastTestArgs();
            assertNotNull("Test arguments should not be null", testArgs);
            
            String message = (String) testArgs[1];
            String title = (String) testArgs[2];
            
            assertNotNull("Message should not be null", message);
            assertTrue("Message should contain copyright info", 
                    message.contains("Copyright") && 
                    message.contains("Darwin"));
            
            assertEquals("Title should be 'About JabberPoint'", "About JabberPoint", title);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception during reflection test: " + e.getMessage());
        }
    }
    
    /**
     * A test version of AboutBox that tracks calls instead of showing dialogs
     * This is kept for backwards compatibility but is no longer needed with the new test mode
     */
    public static class TestAboutBox {
        static String lastMessage;
        static String lastTitle;
        static Frame lastParent;
        
        public static void initialize() {
            // Initialize with empty values
            lastMessage = null;
            lastTitle = null;
            lastParent = null;
        }
        
        public static void show(Frame parent) {
            // Create a similar message to what AboutBox would use
            String message = "JabberPoint is a primitive slide-show program in Java(tm).\n" +
                             "It is freely copyable as long as you keep this notice intact.\n" +
                             "Copyright (c) 1995-now by Ian F. Darwin, ian@darwinsys.com.\n" +
                             "Adapted by Gert Florijn and Sylvia Stuurman for teaching.\n" +
                             "Adapted by Tricat for SQ 2024.";
            String title = "About JabberPoint";
            
            // Track that this was called
            showMessageDialogCalled = true;
            lastParent = parent;
            lastMessage = message;
            lastTitle = title;
            
            // Store args for verification
            lastShowMessageDialogArgs = new Object[] {
                parent, message, title, JOptionPane.INFORMATION_MESSAGE
            };
>>>>>>> d6925b5f1f4d3bd3ef515fc7598526c5c7875072
        }
    }
} 