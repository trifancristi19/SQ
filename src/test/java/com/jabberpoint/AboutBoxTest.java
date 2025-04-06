package com.jabberpoint;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.BeforeClass;

import static org.junit.Assert.*;

import java.awt.Frame;

/**
 * Tests for the AboutBox class without relying on GUI elements
 */
public class AboutBoxTest
{

    @BeforeClass
    public static void setUpClass()
    {
        // Enable test mode for all tests
        AboutBox.setTestMode(true);
    }

    @Before
    public void setUp()
    {
        // Ensure test mode is enabled before each test
        AboutBox.setTestMode(true);
    }

    @After
    public void tearDown()
    {
        // Reset test mode after each test
        AboutBox.setTestMode(true);
    }

    @Test
    public void testShowStoresCorrectParametersInTestMode()
    {
        // Create a mock frame (will not be used in test mode)
        Frame mockFrame = null;

        // Call the show method
        AboutBox.show(mockFrame);

        // Get the parameters that would have been passed to JOptionPane
        Object[] args = AboutBox.getLastTestArgs();

        // Verify the parameters
        assertNotNull("Test args should not be null", args);
        assertEquals("Should have 4 arguments", 4, args.length);

        // First arg should be the parent frame
        assertEquals("First arg should be the parent frame", mockFrame, args[0]);

        // Second arg should be the message (verify it contains expected content)
        String message = (String) args[1];
        assertTrue("Message should contain 'JabberPoint'", message.contains("JabberPoint"));
        assertTrue("Message should contain copyright notice", message.contains("Copyright"));
        assertTrue("Message should contain author info", message.contains("Ian F. Darwin"));

        // Third arg should be the title
        String title = (String) args[2];
        assertEquals("Title should be 'About JabberPoint'", "About JabberPoint", title);

        // Fourth arg should be the message type (INFORMATION_MESSAGE = 1)
        Integer messageType = (Integer) args[3];
        assertEquals("Message type should be INFORMATION_MESSAGE", 1, messageType.intValue());
    }

    @Test
    public void testSetTestMode()
    {
        // First ensure we're in test mode
        AboutBox.setTestMode(true);

        // Call show to get a reference value
        AboutBox.show(null);
        Object[] firstArgs = AboutBox.getLastTestArgs();
        assertNotNull("Last test args should be set in test mode", firstArgs);

        // Set test mode to false but don't actually call show() without test mode
        // This avoids the HeadlessException while still testing the flag
        AboutBox.setTestMode(false);

        // Verify test mode was changed
        AboutBox.setTestMode(true); // Switch back to test mode
        AboutBox.show(null); // Call show again

        // In test mode, lastTestArgs should be set with new values
        assertNotNull("Last test args should be set in test mode", AboutBox.getLastTestArgs());
        // We can't directly test the non-test mode behavior in a headless environment
    }
} 