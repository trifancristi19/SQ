package com.jabberpoint;

/**
 * XML implementation of PresentationLoader
 */
public class XMLPresentationLoader implements PresentationLoader
{
    private final XMLAccessor accessor = new XMLAccessor();

    @Override
    public void loadPresentation(Presentation presentation, String fileName) throws Exception
    {
        this.accessor.loadFile(presentation, fileName);
    }

    @Override
    public void savePresentation(Presentation presentation, String fileName) throws Exception
    {
        this.accessor.saveFile(presentation, fileName);
    }
} 