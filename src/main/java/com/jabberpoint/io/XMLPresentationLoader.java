package com.jabberpoint.io;

import com.jabberpoint.Presentation;
import com.jabberpoint.XMLAccessor;
import com.jabberpoint.XMLParser;
import com.jabberpoint.DOMXMLParser;

/**
 * XML implementation of PresentationLoader
 * Uses XMLAccessor for actual reading/writing
 */
public class XMLPresentationLoader implements PresentationLoader {
    private final XMLAccessor accessor;
    
    /**
     * Create a loader with the default XML parser
     */
    public XMLPresentationLoader() {
        this(new DOMXMLParser());
    }
    
    /**
     * Create a loader with a specific XML parser
     * @param parser The XML parser to use
     */
    public XMLPresentationLoader(XMLParser parser) {
        this.accessor = new XMLAccessor(parser);
    }
    
    @Override
    public void loadPresentation(Presentation presentation, String fileName) throws Exception {
        this.accessor.loadFile(presentation, fileName);
    }
    
    @Override
    public void savePresentation(Presentation presentation, String fileName) throws Exception {
        this.accessor.saveFile(presentation, fileName);
    }
} 