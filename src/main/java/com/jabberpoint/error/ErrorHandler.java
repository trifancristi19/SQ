package com.jabberpoint.error;

/**
 * Interface for handling errors consistently
 * Follows SRP by separating error handling from business logic
 */
public interface ErrorHandler
{
    /**
     * Log an error with a message
     *
     * @param message The error message
     */
    void logError(String message);

    /**
     * Log an error with a message and exception
     *
     * @param message The error message
     * @param e       The exception
     */
    void logError(String message, Throwable e);

    /**
     * Handle an error, which may include logging, displaying a UI message, etc.
     *
     * @param message The error message
     * @param e       The exception
     */
    void handleError(String message, Throwable e);
} 