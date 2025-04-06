package com.jabberpoint.error;

import javax.swing.JOptionPane;
import java.awt.Component;

/**
 * Dialog implementation of ErrorHandler
 * Handles errors by displaying a dialog and logging to console
 */
public class DialogErrorHandler implements ErrorHandler
{
    private final Component parent;
    private final String applicationName;

    /**
     * Create a dialog error handler
     *
     * @param parent          The parent component for dialogs
     * @param applicationName The application name to show in dialog titles
     */
    public DialogErrorHandler(Component parent, String applicationName)
    {
        this.parent = parent;
        this.applicationName = applicationName != null ? applicationName : "Application Error";
    }

    @Override
    public void logError(String message)
    {
        System.err.println("ERROR: " + message);
    }

    @Override
    public void logError(String message, Throwable e)
    {
        System.err.println("ERROR: " + message);
        if (e != null)
        {
            e.printStackTrace(System.err);
        }
    }

    @Override
    public void handleError(String message, Throwable e)
    {
        // Log the error first
        logError(message, e);

        // Then display a dialog
        String dialogMessage = message;
        if (e != null)
        {
            dialogMessage += ": " + e.getMessage();
        }

        JOptionPane.showMessageDialog(
                parent,
                dialogMessage,
                applicationName,
                JOptionPane.ERROR_MESSAGE
        );
    }
} 