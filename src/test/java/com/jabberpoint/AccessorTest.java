package com.jabberpoint;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import java.io.IOException;

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
    }

    @Test
    public void testGetDemoAccessor() {
        Accessor demoAccessor = Accessor.getDemoAccessor();
        assertNotNull("Demo accessor should be created", demoAccessor);
        assertTrue("Demo accessor should be an instance of DemoPresentation",
                demoAccessor instanceof DemoPresentation);
    }

    @Test
    public void testLoadFile() throws IOException {
        String filename = "test.xml";
        accessor.loadFile(presentation, filename);

        assertTrue("loadFile should be called", accessor.wasLoadCalled());
        assertEquals("Filename should be passed correctly", filename, accessor.getLastFilename());
    }

    @Test
    public void testSaveFile() throws IOException {
        String filename = "test.xml";
        accessor.saveFile(presentation, filename);

        assertTrue("saveFile should be called", accessor.wasSaveCalled());
        assertEquals("Filename should be passed correctly", filename, accessor.getLastFilename());
    }

    @Test
    public void testDefaultExtension() {
        assertEquals("Default extension should be .xml", ".xml", Accessor.DEFAULT_EXTENSION);
    }

    @Test
    public void testDemoName() {
        assertEquals("Demo name should be set correctly",
                "Demonstration presentation", Accessor.DEMO_NAME);
    }
} 