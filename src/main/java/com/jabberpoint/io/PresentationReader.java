package com.jabberpoint.io;

import com.jabberpoint.Presentation;

/**
 * Interface for loading presentations
 * Part of Interface Segregation Principle - separating read and write operations
 */
public interface PresentationReader
{
    /**
     * Loads a presentation from a file
     *
     * @param presentation The presentation to load data into
     * @param fileName     The filename to load from
     * @throws Exception If any error occurs during loading
     */
    void loadPresentation(Presentation presentation, String fileName) throws Exception;
} 