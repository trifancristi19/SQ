package com.jabberpoint;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;
import java.util.Vector;

/**
 * <p>A slide. This class has a drawing functionality.</p>
 *
 * @author Ian F. Darwin, ian@darwinsys.com, Gert Florijn, Sylvia Stuurman
 * @version 1.6 2014/05/16 Sylvia Stuurman
 */

public class Slide
{
    public final static int WIDTH = 1200;
    public final static int HEIGHT = 800;
    protected String title; // title is saved separately
    protected Vector<SlideItem> items; // slide items are saved in a Vector

    public Slide()
    {
        items = new Vector<SlideItem>();
    }

    // Add a slide item
    public void append(SlideItem anItem)
    {
        // Skip null items
        if (anItem != null) {
            items.addElement(anItem);
        }
    }

    // give the title of the slide
    public String getTitle()
    {
        return title;
    }

    // change the title of the slide
    public void setTitle(String newTitle)
    {
        title = newTitle;
    }

    // Create TextItem of String, and add the TextItem
    public void append(int level, String message)
    {
        // Skip null messages
        if (message != null) {
            append(new TextItem(level, message));
        }
    }

    // give the  SlideItem
    public SlideItem getSlideItem(int number)
    {
        return items.elementAt(number);
    }

    // give all SlideItems in a Vector
    public Vector<SlideItem> getSlideItems()
    {
        // Return a copy of the items vector to prevent external modification
        return new Vector<SlideItem>(items);
    }

    // give the size of the Slide
    public int getSize()
    {
        return items.size();
    }

    // draw the slide
    public void draw(Graphics g, Rectangle area, ImageObserver view)
    {
        float scale = getScale(area);
        int y = area.y;
        // Title is handled separately
        String title = getTitle();
        SlideItem slideItem = new TextItem(0, title != null ? title : "");
        Style style = Style.getStyle(slideItem.getLevel());
        slideItem.draw(area.x, y, scale, g, style, view);
        y += slideItem.getBoundingBox(g, view, scale, style).height;
        for (int number = 0; number < getSize(); number++)
        {
            slideItem = getSlideItems().elementAt(number);
            style = Style.getStyle(slideItem.getLevel());
            slideItem.draw(area.x, y, scale, g, style, view);
            y += slideItem.getBoundingBox(g, view, scale, style).height;
        }
    }

    // Give the scale for drawing
    private float getScale(Rectangle area)
    {
        return Math.min(((float) area.width) / ((float) WIDTH), ((float) area.height) / ((float) HEIGHT));
    }
}
