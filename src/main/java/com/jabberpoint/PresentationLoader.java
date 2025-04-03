package com.jabberpoint;

/**
 * Strategy interface for loading presentations
 */
public interface PresentationLoader
{
    void loadPresentation(Presentation presentation, String fileName) throws Exception;

    void savePresentation(Presentation presentation, String fileName) throws Exception;
} 