package com.jabberpoint.io;

import com.jabberpoint.Presentation;

/**
 * Interface for saving presentations
 * Part of Interface Segregation Principle - separating read and write operations
 */
public interface PresentationWriter {
    /**
     * Saves a presentation to a file
     * @param presentation The presentation to save
     * @param fileName The filename to save to
     * @throws Exception If any error occurs during saving
     */
    void savePresentation(Presentation presentation, String fileName) throws Exception;
} 