package com.jabberpoint;

import java.awt.Rectangle;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.font.TextLayout;
import java.awt.font.TextAttribute;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.ImageObserver;
import java.text.AttributedString;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

/**
 * <p>A tekst item.</p>
 * <p>A TextItem has drawingfunctionality.</p>
 *
 * @author Ian F. Darwin, ian@darwinsys.com, Gert Florijn, Sylvia Stuurman
 * @version 1.6 2014/05/16 Sylvia Stuurman
 */

public class TextItem extends SlideItem
{
    private String text;

    private static final String EMPTYTEXT = "No Text Given";

    // a textitem of level level, with the text string
    public TextItem(int level, String string)
    {
        super(level);
        this.text = string;
    }

    // an empty textitem
    public TextItem()
    {
        this(0, EMPTYTEXT);
    }

    // give the text
    public String getText()
    {
        return this.text == null ? "" : text;
    }

    // geef de AttributedString voor het item
    public AttributedString getAttributedString(Style style, float scale)
    {
        String textToUse = getText();
        // If text is empty, use a space character to avoid empty AttributedString issues
        if (textToUse.isEmpty())
        {
            textToUse = " ";
        }
        AttributedString attrStr = new AttributedString(textToUse);
        attrStr.addAttribute(TextAttribute.FONT, style.getFont(scale), 0, textToUse.length());
        return attrStr;
    }

    // give the bounding box of the item
    public Rectangle getBoundingBox(Graphics g, ImageObserver observer, float scale, Style myStyle)
    {
        List<TextLayout> layouts = getLayouts(g, myStyle, scale);
        int xsize = 0, ysize = (int) (myStyle.leading * scale);
        if (layouts != null)
        {
            for (TextLayout layout : layouts)
            {
                Rectangle2D bounds = layout.getBounds();
                if (bounds.getWidth() > xsize)
                {
                    xsize = (int) bounds.getWidth();
                }
                if (bounds.getHeight() > 0)
                {
                    ysize += (int) bounds.getHeight();
                }
                ysize += (int) (layout.getLeading() + layout.getDescent());
            }
        }
        return new Rectangle((int) (myStyle.indent * scale), 0, xsize, ysize);
    }

    // draw the item
    public void draw(int x, int y, float scale, Graphics g, Style myStyle, ImageObserver o)
    {
        if (text == null || this.text.length() == 0)
        {
            return;
        }
        List<TextLayout> layouts = getLayouts(g, myStyle, scale);
        Point pen = new Point(x + (int) (myStyle.indent * scale), y + (int) (myStyle.leading * scale));
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(myStyle.color);
        for (TextLayout layout : layouts)
        {
            pen.y += (int) layout.getAscent();
            layout.draw(g2d, pen.x, pen.y);
            pen.y += (int) layout.getDescent();
        }
    }

    private List<TextLayout> getLayouts(Graphics g, Style s, float scale)
    {
        List<TextLayout> layouts = new ArrayList<TextLayout>();
        String text = getText();

        // Handle empty text case
        if (text.isEmpty())
        {
            return layouts;
        }

        AttributedString attrStr = getAttributedString(s, scale);
        Graphics2D g2d = (Graphics2D) g;
        FontRenderContext frc = g2d.getFontRenderContext();
        LineBreakMeasurer measurer = new LineBreakMeasurer(attrStr.getIterator(), frc);
        float wrappingWidth = (Slide.WIDTH - s.indent) * scale;
        while (measurer.getPosition() < text.length())
        {
            TextLayout layout = measurer.nextLayout(wrappingWidth);
            layouts.add(layout);
        }
        return layouts;
    }

    public String toString()
    {
        return "TextItem[" + getLevel() + "," + getText() + "]";
    }
}
