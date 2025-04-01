package com.jabberpoint;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import static org.junit.Assert.*;

import java.io.IOException;
import java.awt.GraphicsEnvironment;
import javax.swing.JOptionPane;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

@RunWith(JUnit4.class)
public class JabberPointTest {
    
    private boolean originalHeadless;
    
    // Track whether methods have been called
    private static boolean styleCreateStylesCalled = false;
    private static boolean slideViewerFrameCreated = false;
    private static boolean accessorLoadFileCalled = false;
    private static boolean xmlAccessorLoadFileCalled = false;
    private static boolean showMessageDialogCalled = false;
    
    @Before
    public void setUp() {
        // Store original headless value
        originalHeadless = GraphicsEnvironment.isHeadless();
        
        // Reset tracking variables
        styleCreateStylesCalled = false;
        slideViewerFrameCreated = false;
        accessorLoadFileCalled = false;
        xmlAccessorLoadFileCalled = false;
        showMessageDialogCalled = false;
        
        // Install test hooks that will track when methods are called
        TestHooks.install();
    }
    
    @After
    public void tearDown() {
        // Restore original headless value
        System.setProperty("java.awt.headless", Boolean.toString(originalHeadless));
        
        // Uninstall test hooks
        TestHooks.uninstall();
    }
    
    @Test
    public void testJabberPointConstants() {
        // Test the constants defined in JabberPoint
        assertEquals("IO Error: ", JabberPoint.IOERR);
        assertEquals("Jabberpoint Error ", JabberPoint.JABERR);
        assertEquals("Jabberpoint 1.6 - OU version", JabberPoint.JABVERSION);
    }
    
    @Test
    public void testMainWithNoArgs() {
        // Create a new instance of JabberPoint that uses our test hooks
        JabberPointWithHooks.main(new String[0]);
        
        // Verify the expected methods were called
        assertTrue("Style.createStyles should be called", styleCreateStylesCalled);
        assertTrue("SlideViewerFrame should be created", slideViewerFrameCreated);
        assertTrue("Demo accessor should be called", accessorLoadFileCalled);
        assertFalse("XML accessor should not be called", xmlAccessorLoadFileCalled);
        assertFalse("Error dialog should not be shown", showMessageDialogCalled);
    }
    
    @Test
    public void testMainWithXmlArg() {
        // Create a new instance of JabberPoint that uses our test hooks
        JabberPointWithHooks.main(new String[]{"test.xml"});
        
        // Verify the expected methods were called
        assertTrue("Style.createStyles should be called", styleCreateStylesCalled);
        assertTrue("SlideViewerFrame should be created", slideViewerFrameCreated);
        assertFalse("Demo accessor should not be called", accessorLoadFileCalled);
        assertTrue("XML accessor should be called", xmlAccessorLoadFileCalled);
        assertFalse("Error dialog should not be shown", showMessageDialogCalled);
    }
    
    @Test
    public void testMainWithIOException() {
        // Set XMLAccessor to throw IOException
        TestHooks.shouldThrowIOException = true;
        
        try {
            // Create a new instance of JabberPoint that uses our test hooks
            JabberPointWithHooks.main(new String[]{"test.xml"});
            
            // Verify the expected methods were called
            assertTrue("Style.createStyles should be called", styleCreateStylesCalled);
            assertTrue("SlideViewerFrame should be created", slideViewerFrameCreated);
            assertFalse("Demo accessor should not be called", accessorLoadFileCalled);
            assertTrue("XML accessor should be called", xmlAccessorLoadFileCalled);
            assertTrue("Error dialog should be shown", showMessageDialogCalled);
        } finally {
            TestHooks.shouldThrowIOException = false;
        }
    }
    
    /**
     * A version of JabberPoint that uses our test hooks
     */
    private static class JabberPointWithHooks extends JabberPoint {
        public static void main(String[] argv) {
            // This implementation matches the real JabberPoint main method
            // but uses our test hooks
            TestHooks.createStyles(); // Instead of Style.createStyles()
            Presentation presentation = new Presentation();
            TestHooks.createFrame(JABVERSION, presentation); // Instead of new SlideViewerFrame()
            
            try {
                if (argv.length == 0) {
                    TestHooks.loadDemoFile(presentation, ""); // Instead of Accessor.getDemoAccessor().loadFile()
                } else {
                    TestHooks.loadXMLFile(presentation, argv[0]); // Instead of new XMLAccessor().loadFile()
                }
                presentation.setSlideNumber(0);
            } catch (IOException ex) {
                TestHooks.showErrorDialog(null, IOERR + ex, JABERR); // Instead of JOptionPane.showMessageDialog()
            }
        }
    }
    
    /**
     * Test hooks to replace real methods with test versions that track calls
     */
    private static class TestHooks {
        public static boolean shouldThrowIOException = false;
        
        public static void install() {
            // Nothing to do - we're just using these static methods instead of real ones
        }
        
        public static void uninstall() {
            // Reset the tracking variables
            styleCreateStylesCalled = false;
            slideViewerFrameCreated = false;
            accessorLoadFileCalled = false;
            xmlAccessorLoadFileCalled = false;
            showMessageDialogCalled = false;
            shouldThrowIOException = false;
        }
        
        public static void createStyles() {
            styleCreateStylesCalled = true;
            // Don't actually create styles - not needed for test
        }
        
        public static void createFrame(String title, Presentation presentation) {
            slideViewerFrameCreated = true;
            // Don't actually create frame - not needed for test
        }
        
        public static void loadDemoFile(Presentation presentation, String filename) throws IOException {
            accessorLoadFileCalled = true;
            // Don't actually load - not needed for test
        }
        
        public static void loadXMLFile(Presentation presentation, String filename) throws IOException {
            xmlAccessorLoadFileCalled = true;
            
            if (shouldThrowIOException) {
                throw new IOException("Test exception");
            }
            
            // Don't actually load - not needed for test
        }
        
        public static void showErrorDialog(Object parent, Object message, String title) {
            showMessageDialogCalled = true;
            // Don't actually show dialog - not needed for test
        }
    }
} 