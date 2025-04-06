package com.jabberpoint.io;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Tests for the DOMXMLParsingStrategy class
 */
public class DOMXMLParsingStrategyTest
{

    private DOMXMLParsingStrategy strategy;
    private static final String TEST_FILE_PATH = "test-parser.xml";
    private static final String XML_WITH_DTD_PATH = "test-dtd.xml";
    private static final String DTD_FILE_PATH = "test.dtd";

    @Before
    public void setUp()
    {
        strategy = new DOMXMLParsingStrategy();

        // Create a simple test XML file
        try (PrintWriter out = new PrintWriter(new FileWriter(TEST_FILE_PATH)))
        {
            out.println("<?xml version=\"1.0\"?>");
            out.println("<root>");
            out.println("  <element id=\"1\">Test Content</element>");
            out.println("</root>");
        } catch (IOException e)
        {
            System.err.println("Error creating test file: " + e.getMessage());
        }

        // Create a test XML file with DTD reference
        try (PrintWriter out = new PrintWriter(new FileWriter(XML_WITH_DTD_PATH)))
        {
            out.println("<?xml version=\"1.0\"?>");
            out.println("<!DOCTYPE root SYSTEM \"test.dtd\">");
            out.println("<root>");
            out.println("  <element id=\"1\">Test Content With DTD</element>");
            out.println("</root>");
        } catch (IOException e)
        {
            System.err.println("Error creating test file with DTD: " + e.getMessage());
        }
    }

    @After
    public void tearDown()
    {
        // Clean up test files
        try
        {
            Files.deleteIfExists(Paths.get(TEST_FILE_PATH));
            Files.deleteIfExists(Paths.get(XML_WITH_DTD_PATH));
            Files.deleteIfExists(Paths.get(DTD_FILE_PATH));
        } catch (IOException e)
        {
            System.err.println("Error during cleanup: " + e.getMessage());
        }
    }

    @Test
    public void testParseFileWithDTD() throws Exception
    {
        // Test parsing a file with DTD reference but without the actual DTD file
        // This should succeed without errors if DTD validation is properly disabled
        Document document = strategy.parseFile(new File(XML_WITH_DTD_PATH));

        // Verify document was parsed
        assertNotNull("Document should not be null even without the DTD", document);

        // Verify root element
        Element root = document.getDocumentElement();
        assertEquals("Root element should be 'root'", "root", root.getTagName());
    }

    @Test
    public void testParseFile() throws Exception
    {
        // Parse the test file
        Document document = strategy.parseFile(new File(TEST_FILE_PATH));

        // Verify document was parsed
        assertNotNull("Document should not be null", document);

        // Verify root element
        Element root = document.getDocumentElement();
        assertEquals("Root element should be 'root'", "root", root.getTagName());

        // Verify child element
        Element element = (Element) root.getElementsByTagName("element").item(0);
        assertNotNull("Child element should exist", element);
        assertEquals("Element ID should match", "1", element.getAttribute("id"));
        assertEquals("Element content should match", "Test Content", element.getTextContent());
    }

    @Test(expected = Exception.class)
    public void testParseNonExistentFile() throws Exception
    {
        // This should throw an exception
        strategy.parseFile(new File("non-existent-file.xml"));
    }

    @Test
    public void testCreateDocument() throws Exception
    {
        // Create a new document
        Document document = strategy.createDocument();

        // Verify document was created
        assertNotNull("Document should not be null", document);

        // Test creating elements
        Element root = document.createElement("root");
        document.appendChild(root);

        Element child = document.createElement("child");
        child.setAttribute("id", "test");
        child.setTextContent("Test Content");
        root.appendChild(child);

        // Verify elements were created
        assertEquals("Root element should be 'root'", "root", document.getDocumentElement().getTagName());
        assertEquals("Child element should be 'child'", "child",
                document.getDocumentElement().getFirstChild().getNodeName());
    }

    @Test
    public void testGetRootElement() throws Exception
    {
        // Parse the test file
        Document document = strategy.parseFile(new File(TEST_FILE_PATH));

        // Get the root element
        Element root = strategy.getRootElement(document);

        // Verify root element
        assertNotNull("Root element should not be null", root);
        assertEquals("Root element should be 'root'", "root", root.getTagName());
    }

    /**
     * Main method to run a direct test of DTD handling
     */
    public static void main(String[] args)
    {
        System.out.println("Testing DTD handling in DOMXMLParsingStrategy");

        // Create test instance
        DOMXMLParsingStrategyTest test = new DOMXMLParsingStrategyTest();
        test.setUp();

        try
        {
            System.out.println("Testing parsing with DTD reference but no DTD file...");
            Document doc = test.strategy.parseFile(new File(XML_WITH_DTD_PATH));
            System.out.println("Success! Document was parsed without error.");
            System.out.println("Root element: " + doc.getDocumentElement().getTagName());

            // Clean up
            test.tearDown();
            System.out.println("Test completed successfully.");
        } catch (Exception e)
        {
            System.err.println("Test failed with exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 