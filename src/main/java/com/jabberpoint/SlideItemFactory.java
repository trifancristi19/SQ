package com.jabberpoint;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Factory for creating different types of SlideItems
 * Follows the Open/Closed Principle by supporting registration of new item types
 */
public class SlideItemFactory
{
    // Map of item types to creator functions
    private static final Map<String, BiFunction<Integer, String, SlideItem>> itemCreators = new HashMap<>();

    // Register default item types
    static
    {
        registerItemType("text", TextItem::new);
        registerItemType("bitmap", BitmapItem::new);
        registerItemType("image", BitmapItem::new);  // Alias for bitmap
    }

    /**
     * Register a new slide item type with its creation function
     *
     * @param type    The type name (case insensitive)
     * @param creator Function that takes level and content and returns a SlideItem
     */
    public static void registerItemType(String type, BiFunction<Integer, String, SlideItem> creator)
    {
        itemCreators.put(type.toLowerCase(), creator);
    }

    /**
     * Create a slide item of the specified type
     *
     * @param type    The type of slide item to create
     * @param level   The level of the slide item
     * @param content The content of the slide item
     * @return A new SlideItem instance
     * @throws IllegalArgumentException if the type is not registered
     */
    public static SlideItem createSlideItem(String type, int level, String content)
    {
        String lowerType = type.toLowerCase();
        BiFunction<Integer, String, SlideItem> creator = itemCreators.get(lowerType);

        if (creator == null)
        {
            throw new IllegalArgumentException("Unknown slide item type: " + type);
        }

        return creator.apply(level, content);
    }
} 