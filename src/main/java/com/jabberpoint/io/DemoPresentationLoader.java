package com.jabberpoint.io;

import com.jabberpoint.Presentation;
import com.jabberpoint.DemoPresentation;

/**
 * Demo implementation of PresentationReader
 * Only implements reader interface following LSP - does not pretend to be writable
 */
public class DemoPresentationLoader implements PresentationReader {
    private final DemoPresentation demoPresentation = new DemoPresentation();
    
    @Override
    public void loadPresentation(Presentation presentation, String fileName) throws Exception {
        demoPresentation.loadFile(presentation, fileName);
    }
} 