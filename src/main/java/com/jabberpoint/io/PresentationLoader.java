package com.jabberpoint.io;

/**
 * Combined interface for both loading and saving presentations
 * Follows Interface Segregation Principle by extending specific interfaces
 * rather than creating one large interface
 */
public interface PresentationLoader extends PresentationReader, PresentationWriter
{
    // No additional methods needed - this interface combines 
    // functionality from both parent interfaces
} 