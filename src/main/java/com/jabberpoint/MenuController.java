package com.jabberpoint;

import java.awt.MenuBar;
import java.awt.Frame;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.JOptionPane;

import com.jabberpoint.io.PresentationLoader;
import com.jabberpoint.io.XMLPresentationLoader;
import com.jabberpoint.error.DialogErrorHandler;
import com.jabberpoint.error.ErrorHandler;

/**
 * <p>The controller for the menu</p>
 *
 * @author Ian F. Darwin, ian@darwinsys.com, Gert Florijn, Sylvia Stuurman
 * @version 1.6 2014/05/16 Sylvia Stuurman
 */
public class MenuController extends MenuBar
{

    private Frame parent; // the frame, only used as parent for the Dialogs
    private transient Presentation presentation; // Commands are given to the presentation
    private final ErrorHandler errorHandler;

    private static final long serialVersionUID = 227L;

    protected static final String ABOUT = "About";
    protected static final String FILE = "File";
    protected static final String EXIT = "Exit";
    protected static final String GOTO = "Go to";
    protected static final String HELP = "Help";
    protected static final String NEW = "New";
    protected static final String NEXT = "Next";
    protected static final String OPEN = "Open";
    protected static final String PAGENR = "Page number?";
    protected static final String PREV = "Prev";
    protected static final String SAVE = "Save";
    protected static final String VIEW = "View";

    protected static final String TESTFILE = "test.xml";
    protected static final String SAVEFILE = "dump.xml";

    protected static final String IOEX = "IO Exception: ";
    protected static final String LOADERR = "Load Error";
    protected static final String SAVEERR = "Save Error";

    public MenuController(Frame frame, Presentation pres)
    {
        this.parent = frame;
        this.presentation = pres;
        this.errorHandler = new DialogErrorHandler(frame, "Jabberpoint");
        buildFileMenu();
        buildViewMenu();
        buildHelpMenu();        // needed for portability (Motif, etc.).
    }

    public void initialize()
    {
        // Menus already built in constructor
    }

    private void buildFileMenu()
    {
        Menu fileMenu = new Menu(FILE);
        MenuItem menuItem;
        fileMenu.add(menuItem = mkMenuItem(OPEN));
        menuItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent actionEvent)
            {
                try
                {
                    // Use loader directly from io package
                    PresentationLoader loader = new XMLPresentationLoader();
                    loader.loadPresentation(presentation, TESTFILE);
                    presentation.setSlideNumber(0);
                } catch (Exception exc)
                {
                    errorHandler.handleError("Could not load presentation", exc);
                }
                parent.repaint();
            }
        });
        fileMenu.add(menuItem = mkMenuItem(NEW));
        menuItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent actionEvent)
            {
                presentation.clear();
                parent.repaint();
            }
        });
        fileMenu.add(menuItem = mkMenuItem(SAVE));
        menuItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                try
                {
                    // Use loader directly from io package
                    PresentationLoader loader = new XMLPresentationLoader();
                    loader.savePresentation(presentation, SAVEFILE);
                } catch (Exception exc)
                {
                    errorHandler.handleError("Could not save presentation", exc);
                }
            }
        });
        fileMenu.addSeparator();
        fileMenu.add(menuItem = mkMenuItem(EXIT));
        menuItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent actionEvent)
            {
                presentation.exit(0);
            }
        });
        add(fileMenu);
    }

    private void buildViewMenu()
    {
        Menu viewMenu = new Menu(VIEW);
        MenuItem menuItem;
        viewMenu.add(menuItem = mkMenuItem(NEXT));
        menuItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent actionEvent)
            {
                presentation.nextSlide();
            }
        });
        viewMenu.add(menuItem = mkMenuItem(PREV));
        menuItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent actionEvent)
            {
                presentation.prevSlide();
            }
        });
        viewMenu.add(menuItem = mkMenuItem(GOTO));
        menuItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent actionEvent)
            {
                String pageNumberStr = JOptionPane.showInputDialog((Object) PAGENR);
                int pageNumber = Integer.parseInt(pageNumberStr);
                presentation.setSlideNumber(pageNumber - 1);
            }
        });
        add(viewMenu);
    }

    private void buildHelpMenu()
    {
        Menu helpMenu = new Menu(HELP);
        MenuItem menuItem;
        helpMenu.add(menuItem = mkMenuItem(ABOUT));
        menuItem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent actionEvent)
            {
                AboutBox.show(parent);
            }
        });
        setHelpMenu(helpMenu);
    }

    // create a menu item
    public MenuItem mkMenuItem(String name)
    {
        return new MenuItem(name, new MenuShortcut(name.charAt(0)));
    }
}
