package com.jabberpoint;

import org.junit.Test;
<<<<<<< HEAD
=======
import org.junit.Before;
>>>>>>> d6925b5f1f4d3bd3ef515fc7598526c5c7875072
import static org.junit.Assert.*;

import java.io.IOException;

<<<<<<< HEAD
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
=======
public class AccessorTest {

    // Mock implementation of Accessor for testing
    private class MockAccessor extends Accessor {
        private boolean loadCalled = false;
        private boolean saveCalled = false;
        private String lastFilename = null;

        @Override
        public void loadFile(Presentation p, String fn) throws IOException {
            this.loadCalled = true;
            this.lastFilename = fn;
        }

        @Override
        public void saveFile(Presentation p, String fn) throws IOException {
            this.saveCalled = true;
            this.lastFilename = fn;
        }

        public boolean wasLoadCalled() {
            return this.loadCalled;
        }

        public boolean wasSaveCalled() {
            return this.saveCalled;
        }

        public String getLastFilename() {
            return this.lastFilename;
        }
    }
    
    private MockAccessor accessor;
    private Presentation presentation;
    
    @Before
    public void setUp() {
        accessor = new MockAccessor();
        presentation = new Presentation();
    }

    @Test
    public void testAccessorCreation() {
        assertNotNull("Accessor should be created", accessor);
>>>>>>> d6925b5f1f4d3bd3ef515fc7598526c5c7875072
    }
    
    /**
     * Test constants in Accessor class
     */
    @Test
<<<<<<< HEAD
    public void testAccessorConstants() {
        assertEquals("Demo name should match", "Demonstration presentation", Accessor.DEMO_NAME);
        assertEquals("Default extension should match", ".xml", Accessor.DEFAULT_EXTENSION);
=======
    public void testGetDemoAccessor() {
        Accessor demoAccessor = Accessor.getDemoAccessor();
        assertNotNull("Demo accessor should be created", demoAccessor);
        assertTrue("Demo accessor should be an instance of DemoPresentation",
                demoAccessor instanceof DemoPresentation);
>>>>>>> d6925b5f1f4d3bd3ef515fc7598526c5c7875072
    }
    
    /**
     * Test constructor exists and works
     */
    @Test
<<<<<<< HEAD
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
=======
    public void testLoadFile() throws IOException {
        String filename = "test.xml";
        accessor.loadFile(presentation, filename);

        assertTrue("loadFile should be called", accessor.wasLoadCalled());
        assertEquals("Filename should be passed correctly", filename, accessor.getLastFilename());
>>>>>>> d6925b5f1f4d3bd3ef515fc7598526c5c7875072
    }
    
    @Test
<<<<<<< HEAD
    public void testXmlAccessorSubclass() {
        Accessor accessor = new XMLAccessor();
        assertNotNull("XMLAccessor should be created successfully", accessor);
        assertTrue("XMLAccessor should be a subclass of Accessor", accessor instanceof Accessor);
=======
    public void testSaveFile() throws IOException {
        String filename = "test.xml";
        accessor.saveFile(presentation, filename);

        assertTrue("saveFile should be called", accessor.wasSaveCalled());
        assertEquals("Filename should be passed correctly", filename, accessor.getLastFilename());
>>>>>>> d6925b5f1f4d3bd3ef515fc7598526c5c7875072
    }
    
    @Test
<<<<<<< HEAD
    public void testDemoPresentationSubclass() {
        Accessor accessor = new DemoPresentation();
        assertNotNull("DemoPresentation should be created successfully", accessor);
        assertTrue("DemoPresentation should be a subclass of Accessor", accessor instanceof Accessor);
=======
    public void testDefaultExtension() {
        assertEquals("Default extension should be .xml", ".xml", Accessor.DEFAULT_EXTENSION);
    }

    @Test
    public void testDemoName() {
        assertEquals("Demo name should be set correctly",
                "Demonstration presentation", Accessor.DEMO_NAME);
>>>>>>> d6925b5f1f4d3bd3ef515fc7598526c5c7875072
    }
} 