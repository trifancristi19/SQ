package com.jabberpoint.io;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

/**
 * DOM implementation of the XMLParsingStrategy interface
 * Uses the standard DOM parser to parse XML files
 */
public class DOMXMLParsingStrategy implements XMLParsingStrategy {

    private final DocumentBuilderFactory factory;
    
    /**
     * Constructor initializes the DocumentBuilderFactory
     */
    public DOMXMLParsingStrategy() {
        this.factory = DocumentBuilderFactory.newInstance();
    }
    
    @Override
    public Document parseFile(File file) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilder builder = factory.newDocumentBuilder();
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