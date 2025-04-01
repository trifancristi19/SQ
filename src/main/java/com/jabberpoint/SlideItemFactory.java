package com.jabberpoint;

/**
 * Factory for creating different types of SlideItems
 */
public class SlideItemFactory
{
    public static SlideItem createSlideItem(String type, int level, String content)
    {
        switch (type.toLowerCase())
        {
            case "text":
                return new TextItem(level, content);
            case "bitmap":
                return new BitmapItem(level, content);
            default:
                throw new IllegalArgumentException("Unknown slide item type: " + type);
        }
    }
} 