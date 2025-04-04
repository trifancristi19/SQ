package com.jabberpoint;

import org.w3c.dom.Document;
import java.io.File;

/**
 * Strategy interface for XML parsing operations
 * Allows different parsing implementations to be used
 */
public interface XMLParser {
    /**
     * Parse an XML file and return a Document
     * @param file The file to parse
     * @return The parsed Document
     * @throws Exception If any parsing error occurs
     */
    Document parseFile(File file) throws Exception;
    
    /**
     * Create a Document for writing XML
     * @return A new Document for XML output
     * @throws Exception If the document cannot be created
     */
    Document createDocument() throws Exception;
} 