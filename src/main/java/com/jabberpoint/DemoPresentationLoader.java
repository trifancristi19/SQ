package com.jabberpoint;

/**
 * Demo implementation of PresentationLoader
 * @deprecated Use com.jabberpoint.io.DemoPresentationReader instead
 */
@Deprecated
public class DemoPresentationLoader implements PresentationLoader {
    private final DemoPresentation demoPresentation = new DemoPresentation();

    @Override
    public void loadPresentation(Presentation presentation, String fileName) {
        demoPresentation.loadFile(presentation, fileName);
    }

    @Override
    public void savePresentation(Presentation presentation, String fileName) {
        // Demo presentations cannot be saved
        throw new UnsupportedOperationException("Demo presentations cannot be saved");
    }
} 