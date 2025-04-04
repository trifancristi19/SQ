package com.jabberpoint.io;

import java.io.File;
import org.w3c.dom.Document;
import com.jabberpoint.Presentation;

/**
 * Interface for writing XML documents
 * Further splits responsibilities (SRP)
 */
public interface XMLDocumentWriter {
    /**
     * Write a presentation to an XML document
     * @param presentation The presentation to write
     * @return The XML document
     * @throws Exception If creation fails
     */
    Document createDocument(Presentation presentation) throws Exception;
    
    /**
     * Write a document to a file
     * @param document The document to write
     * @param file The file to write to
     * @throws Exception If writing fails
     */
    void writeDocument(Document document, File file) throws Exception;
} 