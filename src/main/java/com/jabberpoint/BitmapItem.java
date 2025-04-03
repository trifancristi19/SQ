package com.jabberpoint;

import java.awt.Rectangle;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;

import javax.imageio.ImageIO;

import java.io.IOException;


/**
 * <p>De klasse voor een Bitmap item</p>
 * <p>Bitmap items have the responsibility to draw themselves.</p>
 *
 * @author Ian F. Darwin, ian@darwinsys.com, Gert Florijn, Sylvia Stuurman
 * @version 1.6 2014/05/16 Sylvia Stuurman
 */

public class BitmapItem extends SlideItem
{
    private BufferedImage bufferedImage;
    private String imageName;

    protected static final String FILE = "File ";
    protected static final String NOTFOUND = " not found";

    // level is equal to item-level; name is the name of the file with the Image
    public BitmapItem(int level, String name)
    {
        super(level);
        imageName = name;
        if (imageName != null)
        {
            try
            {
                // Try loading from exact path first
                File file = new File(imageName);
                if (file.exists())
                {
                    bufferedImage = ImageIO.read(file);
                }
                else
                {
                    // Try loading from classpath resources first (works in JARs too)
                    java.io.InputStream is = getClass().getClassLoader().getResourceAsStream(imageName);
                    if (is != null)
                    {
                        bufferedImage = ImageIO.read(is);
                    }
                    else
                    {
                        // Fallback to resources directory
                        String resourcePath = "src/main/resources/" + imageName;
                        File resourceFile = new File(resourcePath);
                        if (resourceFile.exists())
                        {
                            bufferedImage = ImageIO.read(resourceFile);
                        }
                        else
                        {
                            // Try with fallback image if provided one doesn't exist
                            is = getClass().getClassLoader().getResourceAsStream("resources-fallback.jpg");
                            if (is != null) {
                                bufferedImage = ImageIO.read(is);
                                System.err.println(FILE + imageName + NOTFOUND + ", using fallback image");
                            } else {
                                System.err.println(FILE + imageName + NOTFOUND);
                            }
                        }
                    }
                }
            } catch (IOException e)
            {
                System.err.println(FILE + imageName + NOTFOUND + ": " + e.getMessage());
                
                // Try with fallback image if there was an error
                try {
                    java.io.InputStream is = getClass().getClassLoader().getResourceAsStream("resources-fallback.jpg");
                    if (is != null) {
                        bufferedImage = ImageIO.read(is);
                        System.err.println("Using fallback image");
                    }
                } catch (IOException fallbackEx) {
                    // If even fallback fails, just report error
                    System.err.println("Fallback image also failed: " + fallbackEx.getMessage());
                }
            }
        }
    }

    // An empty bitmap-item
    public BitmapItem()
    {
        this(0, null);
    }

    // give the filename of the image
    public String getName()
    {
        return imageName;
    }

    // give the  bounding box of the image
    public Rectangle getBoundingBox(Graphics g, ImageObserver observer, float scale, Style myStyle)
    {
        if (bufferedImage == null)
        {
            return new Rectangle((int) (myStyle.indent * scale), 0, 0, 0);
        }
        return new Rectangle((int) (myStyle.indent * scale), 0, (int) (bufferedImage.getWidth(observer) * scale), ((int) (myStyle.leading * scale)) + (int) (bufferedImage.getHeight(observer) * scale));
    }

    // draw the image
    public void draw(int x, int y, float scale, Graphics g, Style myStyle, ImageObserver observer)
    {
        if (bufferedImage == null)
        {
            return;
        }
        int width = x + (int) (myStyle.indent * scale);
        int height = y + (int) (myStyle.leading * scale);
        g.drawImage(bufferedImage, width, height, (int) (bufferedImage.getWidth(observer) * scale), (int) (bufferedImage.getHeight(observer) * scale), observer);
    }

    public String toString()
    {
        return "BitmapItem[" + getLevel() + "," + imageName + "]";
    }
}
