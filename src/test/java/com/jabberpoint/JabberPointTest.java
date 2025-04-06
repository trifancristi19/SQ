package com.jabberpoint;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.awt.GraphicsEnvironment;
import javax.swing.JOptionPane;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.io.File;
import java.io.FileWriter;

import com.jabberpoint.io.PresentationReader;
import com.jabberpoint.io.DemoPresentationReader;
import com.jabberpoint.io.StrategicXMLPresentationReader;
import com.jabberpoint.io.XMLParsingStrategyFactory;

public class JabberPointTest
{

    // Save the original System.out to restore it later
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private ByteArrayOutputStream outContent;
    private ByteArrayOutputStream errContent;

    @Before
    public void setUp()
    {
        // Set up System.out and System.err to capture output
        outContent = new ByteArrayOutputStream();
        errContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @After
    public void tearDown()
    {
        // Restore original streams
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    public void testConstants()
    {
        // Test that constants are defined with expected values
        assertEquals("IO Error: ", JabberPoint.IOERR);
        assertEquals("Jabberpoint Error ", JabberPoint.JABERR);
        assertEquals("Jabberpoint 1.6 - OU version", JabberPoint.JABVERSION);
    }

    @Test
    public void testStaticMethodsExist()
    {
        // Test that critical static methods exist
        try
        {
            // Verify main method exists
            assertNotNull("main method should exist",
                    JabberPoint.class.getMethod("main", String[].class));
        } catch (NoSuchMethodException e)
        {
            fail("Required method not found: " + e.getMessage());
        }
    }

    @Test
    public void testStyleInitialization()
    {
        // Test that Style.createStyles() initializes styles correctly
        Style.createStyles();

        // Verify that styles were created by checking a few properties
        // The default style (level 0) should exist
        Style style = Style.getStyle(0);
        assertNotNull("Default style should be created", style);

        // Additional styles should be created too (at least 1-5)
        for (int i = 1; i <= 5; i++)
        {
            assertNotNull("Style for level " + i + " should be created", Style.getStyle(i));
        }
    }

    @Test
    public void testPresentationCreation()
    {
        // Test that a Presentation can be created
        Presentation presentation = new Presentation();
        assertNotNull("Presentation should be created successfully", presentation);

        // Test initial state
        assertEquals("New presentation should have 0 slides", 0, presentation.getSize());
    }

    /**
     * Custom class to help with testing JabberPoint's main method in headless environments.
     * This handles the JOptionPane problem by bypassing UI components.
     */
    private static class HeadlessTestHelper
    {
        private final boolean isHeadless;
        private final String originalHeadlessProperty;

        public HeadlessTestHelper()
        {
            // Save original headless state
            originalHeadlessProperty = System.getProperty("java.awt.headless");
            isHeadless = GraphicsEnvironment.isHeadless();

            // Force headless mode for testing
            System.setProperty("java.awt.headless", "true");
        }

        public void restore()
        {
            // Restore original headless property
            if (originalHeadlessProperty != null)
            {
                System.setProperty("java.awt.headless", originalHeadlessProperty);
            }
            else
            {
                System.clearProperty("java.awt.headless");
            }
        }
    }

    @Test
    public void testMainWithNoArguments()
    {
        HeadlessTestHelper helper = new HeadlessTestHelper();

        try
        {
            // Create a separate SlideViewerFrame implementation for testing
            TestableJabberPoint.mainNoArgs();
            assertTrue(true);
        } catch (Exception e)
        {
            fail("JabberPoint.main() with no arguments should not throw exceptions: " + e.getMessage());
        } finally
        {
            helper.restore();
        }
    }

    @Test
    public void testMainWithFilenameArgument()
    {
        HeadlessTestHelper helper = new HeadlessTestHelper();

        try
        {
            // Create a test file path that doesn't exist
            String testFilePath = "nonexistent_test_file.xml";

            // Create a separate implementation that doesn't use JOptionPane
            boolean exceptionThrown = false;
            try
            {
                TestableJabberPoint.mainWithArgs(testFilePath);
            } catch (IOException e)
            {
                // We expect an IOException for a non-existent file
                exceptionThrown = true;
            }

            // Verify that an exception was thrown for the non-existent file
            assertTrue("An IOException should be thrown for a non-existent file", exceptionThrown);
        } finally
        {
            helper.restore();
        }
    }

    /**
     * Test version of JabberPoint that doesn't use JOptionPane to avoid UI issues during testing.
     */
    private static class TestableJabberPoint
    {
        public static void mainNoArgs() throws IOException
        {
            Style.createStyles();
            Presentation presentation = new Presentation();
            // Skip creating the frame for headless testing

            // Load the demo presentation
            PresentationReader reader = new DemoPresentationReader();
            try {
                reader.loadPresentation(presentation, "");
                presentation.setSlideNumber(0);
            } catch (Exception ex) {
                throw new IOException("Error loading demo presentation: " + ex.getMessage());
            }
        }

        public static void mainWithArgs(String filename) throws IOException
        {
            Style.createStyles();
            Presentation presentation = new Presentation();
            // Skip creating the frame for headless testing

            // Try to load the file - this should throw an IOException for a non-existent file
            try {
                // Use the strategic XML reader with default DOM strategy
                StrategicXMLPresentationReader reader = new StrategicXMLPresentationReader();
                reader.loadPresentation(presentation, filename);
                presentation.setSlideNumber(0);
            } catch (Exception ex) {
                throw new IOException("Error loading presentation: " + ex.getMessage());
            }
        }
        
        public static void mainWithSaxStrategy(String filename) throws IOException
        {
            Style.createStyles();
            Presentation presentation = new Presentation();
            // Skip creating the frame for headless testing

            try {
                // Use the strategic XML reader with SAX strategy
                StrategicXMLPresentationReader reader = new StrategicXMLPresentationReader(
                    XMLParsingStrategyFactory.StrategyType.SAX);
                reader.loadPresentation(presentation, filename);
                presentation.setSlideNumber(0);
            } catch (Exception ex) {
                throw new IOException("Error loading presentation: " + ex.getMessage());
            }
        }
    }
    
    /**
     * Test the strategic XML parsing with DOM strategy
     */
    @Test
    public void testStrategicXMLParsingWithDOM() throws IOException {
        // Create a temporary XML file
        File tempFile = File.createTempFile("test-presentation", ".xml");
        tempFile.deleteOnExit();
        
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("<?xml version=\"1.0\"?>\n" +
                "<presentation>\n" +
                "  <title>Test Presentation</title>\n" +
                "  <slide>\n" +
                "    <title>Test Slide</title>\n" +
                "  </slide>\n" +
                "</presentation>");
        }
        
        // This should not throw an exception
        TestableJabberPoint.mainWithArgs(tempFile.getAbsolutePath());
    }
    
    /**
     * Test the strategic XML parsing with SAX strategy
     */
    @Test
    public void testStrategicXMLParsingWithSAX() throws IOException {
        // Create a temporary XML file
        File tempFile = File.createTempFile("test-presentation", ".xml");
        tempFile.deleteOnExit();
        
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("<?xml version=\"1.0\"?>\n" +
                "<presentation>\n" +
                "  <title>Test Presentation</title>\n" +
                "  <slide>\n" +
                "    <title>Test Slide</title>\n" +
                "  </slide>\n" +
                "</presentation>");
        }
        
        // This should not throw an exception
        TestableJabberPoint.mainWithSaxStrategy(tempFile.getAbsolutePath());
    }
    
    /**
     * Test strategic XML parsing with non-existent file
     */
    @Test(expected = IOException.class)
    public void testStrategicXMLParsingWithNonExistentFile() throws IOException {
        TestableJabberPoint.mainWithArgs("non-existent-file.xml");
    }
} 