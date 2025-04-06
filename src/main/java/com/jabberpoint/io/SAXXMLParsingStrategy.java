package com.jabberpoint.io;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.sax.SAXSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * SAX implementation of the XMLParsingStrategy interface
 * Uses SAX to parse XML files
 * 
 * Note: This implementation still uses DOM for document creation, 
 * but demonstrates how different parsing strategies can be used
 */
public class SAXXMLParsingStrategy implements XMLParsingStrategy {

    private final DocumentBuilderFactory factory;
    
    /**
     * Constructor initializes the DocumentBuilderFactory
     */
    public SAXXMLParsingStrategy() {
        this.factory = DocumentBuilderFactory.newInstance();
    }
    
    @Override
    public Document parseFile(File file) throws ParserConfigurationException, SAXException, IOException {
        // Create an InputSource from the file
        InputSource inputSource = new InputSource(new FileInputStream(file));
        
        // Create a SAXSource
        SAXSource source = new SAXSource(inputSource);
        
        // Create a DocumentBuilder and parse the source
        DocumentBuilder builder = factory.newDocumentBuilder();
        
        // In a real SAX implementation, we would use custom handlers
        // For demonstration, we'll just parse the file using the DOM builder
        return builder.parse(file);
    }

    @Override
    public Element getRootElement(Document document) {
        return document.getDocumentElement();
    }

    @Override
    public Document createDocument() throws ParserConfigurationException {
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.newDocument();
    }
} 