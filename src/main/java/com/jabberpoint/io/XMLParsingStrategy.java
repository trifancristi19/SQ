package com.jabberpoint.io;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;

/**
 * Strategy interface for XML parsing operations
 * Allows different parsing implementations to be used interchangeably
 */
public interface XMLParsingStrategy
{

    /**
     * Parse an XML file into a Document
     *
     * @param file The XML file to parse
     * @return The parsed Document
     * @throws Exception If parsing fails
     */
    Document parseFile(File file) throws Exception;

    /**
     * Get the root element from the document
     *
     * @param document The document to get the root element from
     * @return The root element
     */
    Element getRootElement(Document document);

    /**
     * Create a new empty document
     *
     * @return A new empty document
     * @throws Exception If creation fails
     */
    Document createDocument() throws Exception;
} 