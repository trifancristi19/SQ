package com.jabberpoint;

import org.w3c.dom.Document;
import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * DOM implementation of the XMLParser interface
 * Uses standard DOM API for XML parsing
 */
public class DOMXMLParser implements XMLParser {
    
    @Override
    public Document parseFile(File file) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        
        // Set features to make DTD handling more robust
        factory.setValidating(false);
        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(file);
    }
    
    @Override
    public Document createDocument() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.newDocument();
    }
} 