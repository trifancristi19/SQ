package com.jabberpoint;

import java.awt.Color;
import java.awt.Font;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;
import javax.swing.JComponent;

/**
 * <p>SlideViewerComponent is a graphical component that can show slides.</p>
 *
 * @author Ian F. Darwin, ian@darwinsys.com, Gert Florijn, Sylvia Stuurman
 * @version 1.6 2014/05/16 Sylvia Stuurman
 */

public class SlideViewerComponent extends JComponent implements PresentationObserver
{

    private Slide slide; // current slide
    private Font labelFont; // font of labels
    private Presentation presentation; // the presentation

    private static final long serialVersionUID = 227L;

    private static final Color BGCOLOR = Color.white;
    private static final Color COLOR = Color.black;
    private static final String FONTNAME = "Dialog";
    private static final int FONTSTYLE = Font.BOLD;
    private static final int FONTSIZE = 10;
    private static final int XPOS = 1100;
    private static final int YPOS = 20;

    public SlideViewerComponent(Presentation presentation)
    {
        setBackground(BGCOLOR);
        labelFont = new Font(FONTNAME, FONTSTYLE, FONTSIZE);
        this.presentation = presentation;
        this.presentation.addObserver(this);
    }

    @Override
    public void onSlideChanged(int slideNumber)
    {
        this.slide = presentation.getSlide(slideNumber);
        repaint();
    }

    @Override
    public void onPresentationChanged()
    {
        if (presentation.getSize() > 0)
        {
            this.slide = presentation.getCurrentSlide();
        }
        else
        {
            this.slide = null;
        }
        repaint();
    }

    public Dimension getPreferredSize()
    {
        return new Dimension(Slide.WIDTH, Slide.HEIGHT);
    }

    public void paintComponent(Graphics g)
    {
        g.setColor(BGCOLOR);
        g.fillRect(0, 0, getSize().width, getSize().height);
        if (presentation.getSlideNumber() < 0 || slide == null)
        {
            return;
        }
        g.setFont(labelFont);
        g.setColor(COLOR);
        g.drawString("Slide " + (1 + presentation.getSlideNumber()) + " of " + presentation.getSize(), XPOS, YPOS);
        Rectangle area = new Rectangle(0, YPOS, getWidth(), (getHeight() - YPOS));
        slide.draw(g, area, this);
    }
}
