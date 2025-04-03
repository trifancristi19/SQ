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
        
        try {
            AboutBox.show(null); // Use null instead of Frame to avoid HeadlessException
            assertTrue(true); // If we made it here, no exception was thrown
        } catch (Exception e) {
            fail("AboutBox.show threw an exception: " + e.getMessage());
        }
    }
} 