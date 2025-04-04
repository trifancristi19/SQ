package com.jabberpoint;

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
 * Tests for the DOMXMLParser class
 */
public class DOMXMLParserTest {

    private DOMXMLParser parser;
    private static final String TEST_FILE_PATH = "test-parser.xml";

    @Before
    public void setUp() {
        parser = new DOMXMLParser();
        
        // Create a simple test XML file
        try (PrintWriter out = new PrintWriter(new FileWriter(TEST_FILE_PATH))) {
            out.println("<?xml version=\"1.0\"?>");
            out.println("<root>");
            out.println("  <element id=\"1\">Test Content</element>");
            out.println("</root>");
        } catch (IOException e) {
            System.err.println("Error creating test file: " + e.getMessage());
        }
    }

    @After
    public void tearDown() {
        // Clean up test file
        try {
            Files.deleteIfExists(Paths.get(TEST_FILE_PATH));
        } catch (IOException e) {
            System.err.println("Error during cleanup: " + e.getMessage());
        }
    }

    @Test
    public void testParseFile() throws Exception {
        // Parse the test file
        Document document = parser.parseFile(new File(TEST_FILE_PATH));
        
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
    public void testParseNonExistentFile() throws Exception {
        // This should throw an exception
        parser.parseFile(new File("non-existent-file.xml"));
    }

    @Test
    public void testCreateDocument() throws Exception {
        // Create a new document
        Document document = parser.createDocument();
        
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
} 