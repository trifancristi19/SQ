package com.jabberpoint;

import com.jabberpoint.io.DemoPresentationReader;

/**
 * Demo implementation of PresentationLoader
 * @deprecated Use com.jabberpoint.io.DemoPresentationReader instead
 */
@Deprecated
public class DemoPresentationLoader implements PresentationLoader {
    private final DemoPresentationReader demoReader = new DemoPresentationReader();

    @Override
    public void loadPresentation(Presentation presentation, String fileName) {
        try {
            demoReader.loadPresentation(presentation, fileName);
        } catch (Exception e) {
            // Maintain backward compatibility by not throwing exception
            System.err.println("Error loading demo presentation: " + e.getMessage());
        }
    }

    @Override
    public void savePresentation(Presentation presentation, String fileName) {
        // Demo presentations cannot be saved
        throw new UnsupportedOperationException("Demo presentations cannot be saved");
    }
} 