package com.jabberpoint;

import javax.swing.JOptionPane;
import java.io.IOException;
import com.jabberpoint.io.PresentationReader;
import com.jabberpoint.io.PresentationLoader;
import com.jabberpoint.io.XMLPresentationLoader;
import com.jabberpoint.io.DemoPresentationReader;
import com.jabberpoint.io.StrategicXMLPresentationReader;
import com.jabberpoint.io.XMLParsingStrategyFactory;
import com.jabberpoint.error.DialogErrorHandler;
import com.jabberpoint.error.ErrorHandler;

/**
 * JabberPoint Main Programma
 * <p>This program is distributed under the terms of the accompanying
 * COPYRIGHT.txt file (which is NOT the GNU General Public License).
 * Please read it. Your use of the software constitutes acceptance
 * of the terms in the COPYRIGHT.txt file.</p>
 *
 * @author Ian F. Darwin, ian@darwinsys.com, Gert Florijn, Sylvia Stuurman
 * @version 1.6 2014/05/16 Sylvia Stuurman
 */

public class JabberPoint
{
    protected static final String IOERR = "IO Error: ";
    protected static final String JABERR = "Jabberpoint Error ";
    protected static final String JABVERSION = "Jabberpoint 1.6 - OU version";

    public static void main(String argv[])
    {
        // Create styles first
        Style.createStyles();
        
        // Create the presentation
        Presentation presentation = new Presentation();
        
        // Create the slide viewer frame
        SlideViewerFrame slideViewerFrame = new SlideViewerFrame(JABVERSION, presentation);
        
        // Create error handler
        ErrorHandler errorHandler = new DialogErrorHandler(slideViewerFrame, JABERR);
        
        try
        {
            // Create appropriate loader based on command line arguments
            PresentationReader reader;
            String filename = "";
            
            if (argv.length == 0)
            {
                reader = new DemoPresentationReader();
            }
            else if (argv.length == 1)
            {
                // Use the strategic XML reader with the default DOM strategy
                reader = new StrategicXMLPresentationReader();
                filename = argv[0];
            }
            else if (argv.length == 2 && argv[0].equals("--sax"))
            {
                // Use the strategic XML reader with SAX strategy if specified
                reader = new StrategicXMLPresentationReader(
                    XMLParsingStrategyFactory.StrategyType.SAX);
                filename = argv[1];
            }
            else
            {
                // Default to DOM strategy
                reader = new StrategicXMLPresentationReader(
                    XMLParsingStrategyFactory.StrategyType.DOM);
                filename = argv[0];
            }
            
            // Load the presentation
            reader.loadPresentation(presentation, filename);
            presentation.setSlideNumber(0);
        } 
        catch (Exception ex)
        {
            // Use the error handler instead of directly showing dialog
            errorHandler.handleError(IOERR, ex);
        }
    }
}
