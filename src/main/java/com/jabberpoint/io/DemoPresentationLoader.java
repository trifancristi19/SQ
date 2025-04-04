package com.jabberpoint.io;

import com.jabberpoint.Presentation;

/**
 * Demo implementation of PresentationReader
 * Only implements reader interface following LSP - does not pretend to be writable
 */
public class DemoPresentationLoader implements PresentationReader {
    private final DemoPresentationReader demoReader = new DemoPresentationReader();
    
    @Override
    public void loadPresentation(Presentation presentation, String fileName) throws Exception {
        demoReader.loadPresentation(presentation, fileName);
    }
} 