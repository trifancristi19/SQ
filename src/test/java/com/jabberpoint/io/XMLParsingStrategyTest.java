package com.jabberpoint.io;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Tests for the XMLParsingStrategy interface and implementations
 */
public class XMLParsingStrategyTest
{

    private static final String TEST_XML_CONTENT =
            "<?xml version=\"1.0\"?>\n" +
                    "<presentation>\n" +
                    "  <title>Test Presentation</title>\n" +
                    "  <slide>\n" +
                    "    <title>Test Slide</title>\n" +
                    "    <text level=\"1\">This is a test slide</text>\n" +
                    "  </slide>\n" +
                    "</presentation>";

    private File testFile;
    private XMLParsingStrategy domStrategy;
    private XMLParsingStrategy saxStrategy;

    @Before
    public void setUp() throws IOException
    {
        // Create a temporary test file
        testFile = File.createTempFile("test-presentation", ".xml");
        testFile.deleteOnExit();

        // Write the test XML content to the file
        try (FileWriter writer = new FileWriter(testFile))
        {
            writer.write(TEST_XML_CONTENT);
        }

        // Create strategy instances
        domStrategy = new DOMXMLParsingStrategy();
        saxStrategy = new SAXXMLParsingStrategy();
    }

    @Test
    public void testDOMStrategyParseFile() throws Exception
    {
        // Test DOM strategy parsing
        Document document = domStrategy.parseFile(testFile);
        assertNotNull("Document should not be null", document);

        // Verify the document content
        Element rootElement = domStrategy.getRootElement(document);
        assertEquals("Root element should be 'presentation'", "presentation", rootElement.getTagName());

        // Check title
        Element titleElement = (Element) rootElement.getElementsByTagName("title").item(0);
        assertEquals("Presentation title should match", "Test Presentation", titleElement.getTextContent());

        // Check slide
        Element slideElement = (Element) rootElement.getElementsByTagName("slide").item(0);
        assertNotNull("Slide element should exist", slideElement);

        // Check slide title
        Element slideTitleElement = (Element) slideElement.getElementsByTagName("title").item(0);
        assertEquals("Slide title should match", "Test Slide", slideTitleElement.getTextContent());
    }

    @Test
    public void testSAXStrategyParseFile() throws Exception
    {
        // Test SAX strategy parsing
        Document document = saxStrategy.parseFile(testFile);
        assertNotNull("Document should not be null", document);

        // Verify the document content
        Element rootElement = saxStrategy.getRootElement(document);
        assertEquals("Root element should be 'presentation'", "presentation", rootElement.getTagName());

        // Check title
        Element titleElement = (Element) rootElement.getElementsByTagName("title").item(0);
        assertEquals("Presentation title should match", "Test Presentation", titleElement.getTextContent());
    }

    @Test
    public void testDOMStrategyCreateDocument() throws Exception
    {
        // Test creating a new document with DOM strategy
        Document document = domStrategy.createDocument();
        assertNotNull("Created document should not be null", document);
    }

    @Test
    public void testSAXStrategyCreateDocument() throws Exception
    {
        // Test creating a new document with SAX strategy
        Document document = saxStrategy.createDocument();
        assertNotNull("Created document should not be null", document);
    }

    @Test
    public void testNonExistentFile()
    {
        // Test parsing a non-existent file
        File nonExistentFile = new File("non-existent-file.xml");
        try
        {
            domStrategy.parseFile(nonExistentFile);
            fail("Should throw an exception for non-existent file");
        } catch (Exception e)
        {
            // Expected exception
            assertTrue("Exception should be related to file not found",
                    e instanceof IOException || e.getMessage().contains("No such file"));
        }
    }

    @Test
    public void testFactoryGetDefaultStrategy()
    {
        // Test the factory default strategy method
        XMLParsingStrategy strategy = XMLParsingStrategyFactory.getDefaultStrategy();
        assertNotNull("Default strategy should not be null", strategy);
        assertTrue("Default strategy should be DOM", strategy instanceof DOMXMLParsingStrategy);
    }

    @Test
    public void testFactoryGetSpecificStrategies()
    {
        // Test getting DOM strategy
        XMLParsingStrategy domStrategy = XMLParsingStrategyFactory.getStrategy(
                XMLParsingStrategyFactory.StrategyType.DOM);
        assertNotNull("DOM strategy should not be null", domStrategy);
        assertTrue("Should be DOM strategy", domStrategy instanceof DOMXMLParsingStrategy);

        // Test getting SAX strategy
        XMLParsingStrategy saxStrategy = XMLParsingStrategyFactory.getStrategy(
                XMLParsingStrategyFactory.StrategyType.SAX);
        assertNotNull("SAX strategy should not be null", saxStrategy);
        assertTrue("Should be SAX strategy", saxStrategy instanceof SAXXMLParsingStrategy);
    }
} 