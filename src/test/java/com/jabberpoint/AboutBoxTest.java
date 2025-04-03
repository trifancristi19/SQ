package com.jabberpoint;

import org.junit.Test;
import static org.junit.Assert.*;

import java.awt.GraphicsEnvironment;

public class AboutBoxTest {
    
    @Test
    public void testShowDoesNotThrow() {
        // Skip test in headless environment
        if (GraphicsEnvironment.isHeadless()) {
            System.out.println("Skipping AboutBoxTest in headless environment");
            return;
        }
        
        // We can't verify the actual message dialog in a headless environment,
        // but we can at least verify that the method doesn't throw an exception
        // when called with an empty frame.
        
        // This is a very minimal test - ideally we would use a mocking framework
        // to verify the JOptionPane was called with the right parameters.
        try {
            AboutBox.show(null); // Use null instead of Frame to avoid HeadlessException
            assertTrue(true); // If we made it here, no exception was thrown
        } catch (Exception e) {
            fail("AboutBox.show threw an exception: " + e.getMessage());
        }
    }
} 