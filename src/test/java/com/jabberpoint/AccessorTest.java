package com.jabberpoint;

import org.junit.Test;
import static org.junit.Assert.*;

import java.io.IOException;

/**
 * Tests for the abstract Accessor class
 */
public class AccessorTest {
    
    /**
     * Test the getDemoAccessor factory method
     */
    @Test
    public void testGetDemoAccessor() {
        Accessor accessor = Accessor.getDemoAccessor();
        assertNotNull("Demo accessor should not be null", accessor);
        assertTrue("Should get a DemoPresentation instance", accessor instanceof DemoPresentation);
    }
    
    /**
     * Test constants in Accessor class
     */
    @Test
    public void testAccessorConstants() {
        assertEquals("Demo name should match", "Demonstration presentation", Accessor.DEMO_NAME);
        assertEquals("Default extension should match", ".xml", Accessor.DEFAULT_EXTENSION);
    }
    
    /**
     * Test constructor exists and works
     */
    @Test
    public void testConstructor() {
        // Create a concrete implementation for testing
        Accessor testAccessor = new Accessor() {
            @Override
            public void loadFile(Presentation p, String fn) {
                // No implementation needed for test
            }
            
            @Override
            public void saveFile(Presentation p, String fn) {
                // No implementation needed for test
            }
        };
        
        assertNotNull("Accessor implementation should be created", testAccessor);
    }
    
    @Test
    public void testXmlAccessorSubclass() {
        Accessor accessor = new XMLAccessor();
        assertNotNull("XMLAccessor should be created successfully", accessor);
        assertTrue("XMLAccessor should be a subclass of Accessor", accessor instanceof Accessor);
    }
    
    @Test
    public void testDemoPresentationSubclass() {
        Accessor accessor = new DemoPresentation();
        assertNotNull("DemoPresentation should be created successfully", accessor);
        assertTrue("DemoPresentation should be a subclass of Accessor", accessor instanceof Accessor);
    }
} 