package com.jabberpoint;

import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import javax.swing.JFrame;

/**
 * <p>The application window for a slideviewcomponent</p>
 * @author Ian F. Darwin, ian@darwinsys.com, Gert Florijn, Sylvia Stuurman
 * @version 1.1 2002/12/17 Gert Florijn
 * @version 1.2 2003/11/19 Sylvia Stuurman
 * @version 1.3 2004/08/17 Sylvia Stuurman
 * @version 1.4 2007/07/16 Sylvia Stuurman
 * @version 1.5 2010/03/03 Sylvia Stuurman
 * @version 1.6 2014/05/16 Sylvia Stuurman
*/

public class SlideViewerFrame extends JFrame {
	private static final long serialVersionUID = 3227L;
	
	private static final String JABTITLE = "Jabberpoint 1.6 - OU";
	public final static int WIDTH = 1200;
	public final static int HEIGHT = 800;
	
	public SlideViewerFrame(String title, Presentation presentation) {
		super(title);
		setupWindow(presentation);
	}

// Setup GUI
	public void setupWindow(Presentation presentation) {
		System.out.println("Setting up SlideViewerFrame...");
		SlideViewerComponent slideViewerComponent = new SlideViewerComponent(presentation);
		presentation.setShowView(slideViewerComponent);
		setTitle(JABTITLE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		getContentPane().add(slideViewerComponent);
		addKeyListener(new KeyController(presentation)); // add a controller
		System.out.println("Adding KeyListener: " + getKeyListeners().length);
		setMenuBar(new MenuController(this, presentation));	// add another controller
		System.out.println("MenuBar set: " + (getMenuBar() != null));
		pack(); // Pack the frame
		setSize(new Dimension(WIDTH, HEIGHT)); // Set size before making visible
		System.out.println("Size set: " + getSize().width + "x" + getSize().height);
		setVisible(true);
		System.out.println("SlideViewerFrame setup complete");
	}
}
