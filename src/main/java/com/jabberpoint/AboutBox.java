package com.jabberpoint;

import java.awt.Frame;
import javax.swing.JOptionPane;

/**
 * De About-box voor JabberPoint.
 *
 * @author Ian F. Darwin, ian@darwinsys.com, Gert Florijn, Sylvia Stuurman
 * @version 1.6 2014/05/16 Sylvia Stuurman
 */

public class AboutBox
{
    // Test hook - normally false, but can be set to true in tests
    private static boolean inTestMode = false;
    private static Object[] lastTestArgs = null;

    // For test purposes only - not to be used in production code
    public static void setTestMode(boolean testMode)
    {
        inTestMode = testMode;
    }

    // For test purposes only - allows test code to see what was passed to showMessageDialog
    public static Object[] getLastTestArgs()
    {
        return lastTestArgs;
    }

    public static void show(Frame parent)
    {
        // Create the message content
        String message =
                "JabberPoint is a primitive slide-show program in Java(tm). It\n" +
                        "is freely copyable as long as you keep this notice and\n" +
                        "the splash screen intact.\n" +
                        "Copyright (c) 1995-1997 by Ian F. Darwin, ian@darwinsys.com.\n" +
                        "Adapted by Gert Florijn (version 1.1) and " +
                        "Sylvia Stuurman (version 1.2 and higher) for the Open" +
                        "University of the Netherlands, 2002 -- now." +
                        "Author's version available from http://www.darwinsys.com/";
        String title = "About JabberPoint";
        int messageType = JOptionPane.INFORMATION_MESSAGE;

        // Store args for tests if in test mode
        if (inTestMode)
        {
            lastTestArgs = new Object[]{parent, message, title, messageType};
            return; // Don't show actual dialog in test mode
        }

        JOptionPane.showMessageDialog(parent, message, title, messageType);
    }
}
