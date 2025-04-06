package com.jabberpoint.error;

/**
 * Console implementation of ErrorHandler
 * Handles errors by logging to the console
 */
public class ConsoleErrorHandler implements ErrorHandler
{

    @Override
    public void logError(String message)
    {
        System.err.println("ERROR: " + message);
    }

    @Override
    public void logError(String message, Throwable e)
    {
        System.err.println("ERROR: " + message);
        e.printStackTrace(System.err);
    }

    @Override
    public void handleError(String message, Throwable e)
    {
        logError(message, e);
    }
} 