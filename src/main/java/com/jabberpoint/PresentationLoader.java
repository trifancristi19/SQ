package com.jabberpoint;

/**
 * Strategy interface for loading presentations
 */
public interface PresentationLoader {
    void loadPresentation(Presentation presentation, String fileName) throws Exception;
    void savePresentation(Presentation presentation, String fileName) throws Exception;
}

/**
 * XML implementation of PresentationLoader
 */
class XMLPresentationLoader implements PresentationLoader {
    private final XMLAccessor accessor = new XMLAccessor();

    @Override
    public void loadPresentation(Presentation presentation, String fileName) throws Exception {
        accessor.loadFile(presentation, fileName);
    }

    @Override
    public void savePresentation(Presentation presentation, String fileName) throws Exception {
        accessor.saveFile(presentation, fileName);
    }
}

/**
 * Demo implementation of PresentationLoader
 */
class DemoPresentationLoader implements PresentationLoader {
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