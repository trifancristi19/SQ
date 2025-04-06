package com.jabberpoint;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import javax.imageio.ImageIO;

public class BitmapItemTest
{

    private BitmapItem bitmapItem;
    private Style defaultStyle;
    private Graphics mockGraphics;
    private ImageObserver mockObserver;

    @Before
    public void setUp()
    {
        defaultStyle = new Style(0, Color.BLACK, 12, 0);

        // Create mock graphics
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        mockGraphics = image.getGraphics();

        // Create simple mock observer that always returns true
        mockObserver = (img, infoflags, x, y, width, height) -> true;
    }

    @Test
    public void testConstructorWithValidValues()
    {
        bitmapItem = new BitmapItem(1, "test-image.jpg");
        assertEquals("Level should be set correctly", 1, bitmapItem.getLevel());
        assertEquals("Image name should be set correctly", "test-image.jpg", bitmapItem.getName());
    }

    @Test
    public void testEmptyConstructor()
    {
        bitmapItem = new BitmapItem();
        assertEquals("Default level should be 0", 0, bitmapItem.getLevel());
        assertNull("Image name should be null", bitmapItem.getName());
    }

    @Test
    public void testGetName()
    {
        String imageName = "test-bitmap.png";
        bitmapItem = new BitmapItem(2, imageName);
        assertEquals("getName should return the correct image name", imageName, bitmapItem.getName());
    }

    @Test
    public void testGetBoundingBoxWithNullImage()
    {
        // Create a BitmapItem with a non-existent image (which results in null bufferedImage)
        bitmapItem = new BitmapItem(1, "non-existent-image.jpg");

        float scale = 1.0f;
        Style style = new Style(10, Color.BLACK, 12, 0); // Set indent to 10

        Rectangle boundingBox = bitmapItem.getBoundingBox(mockGraphics, mockObserver, scale, style);

        // For null image, the method might actually return values based on fallback image
        // Just check that x coordinate matches the expected calculation
        assertEquals("X coordinate should be style indent * scale", (int) (style.indent * scale), boundingBox.x);
        assertEquals("Y coordinate should be 0", 0, boundingBox.y);
    }

    @Test
    public void testGetBoundingBoxWithImage()
    {
        // Create a BitmapItem with a test image that we can control
        bitmapItem = new BitmapItem(1, "test-image.jpg");

        // Set a controlled bufferedImage using reflection
        try
        {
            BufferedImage testImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
            Field imageField = BitmapItem.class.getDeclaredField("bufferedImage");
            imageField.setAccessible(true);
            imageField.set(bitmapItem, testImage);

            float scale = 2.0f; // Use scale > 1 to test scaling
            Style style = new Style(10, Color.BLACK, 5, 0); // indent 10, leading 5

            Rectangle boundingBox = bitmapItem.getBoundingBox(mockGraphics, mockObserver, scale, style);

            // Test that the bounding box has expected dimensions
            assertEquals("X coordinate should be style indent * scale", (int) (style.indent * scale), boundingBox.x);
            assertEquals("Y coordinate should be 0", 0, boundingBox.y);
            assertEquals("Width should be image width * scale", (int) (testImage.getWidth() * scale), boundingBox.width);
            assertEquals("Height should include leading + image height * scale",
                    (int) (style.leading * scale) + (int) (testImage.getHeight() * scale), boundingBox.height);
        } catch (Exception e)
        {
            fail("Failed to set test image: " + e.getMessage());
        }
    }

    @Test
    public void testDrawWithNullImage()
    {
        // Create a BitmapItem with a non-existent image (resulting in null bufferedImage)
        bitmapItem = new BitmapItem(1, "non-existent-image.jpg");

        // This should not throw an exception for null images
        bitmapItem.draw(10, 10, 1.0f, mockGraphics, defaultStyle, mockObserver);

        // Test passes if no exception is thrown
    }

    @Test
    public void testDrawWithImage()
    {
        // Create a BitmapItem with a test image that we can control
        bitmapItem = new BitmapItem(1, "test-image.jpg");

        // Set a controlled bufferedImage using reflection
        try
        {
            BufferedImage testImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
            Field imageField = BitmapItem.class.getDeclaredField("bufferedImage");
            imageField.setAccessible(true);
            imageField.set(bitmapItem, testImage);

            // Draw should not throw exception
            bitmapItem.draw(10, 10, 1.0f, mockGraphics, defaultStyle, mockObserver);

            // Test passes if no exception is thrown
        } catch (Exception e)
        {
            fail("Failed to set test image or draw image: " + e.getMessage());
        }
    }

    @Test
    public void testToString()
    {
        int level = 3;
        String imageName = "test.png";
        bitmapItem = new BitmapItem(level, imageName);

        String expected = "BitmapItem[" + level + "," + imageName + "]";
        assertEquals("toString should return the correct string representation", expected, bitmapItem.toString());
    }

    @Test
    public void testNonExistentImageHandling()
    {
        bitmapItem = new BitmapItem(1, "definitely-does-not-exist.jpg");
        // The constructor should handle non-existent images gracefully
        // We're just testing that it doesn't throw an exception and that getName works
        assertEquals("Image name should be preserved", "definitely-does-not-exist.jpg", bitmapItem.getName());
    }

    @Test
    public void testImageLoadingPaths()
    {
        // Test the various image loading paths by creating test files

        String tempImageName = "temp-test-image.jpg";
        File tempImageFile = null;

        try
        {
            // Create a temporary image file
            tempImageFile = new File(tempImageName);
            BufferedImage tempImage = new BufferedImage(50, 50, BufferedImage.TYPE_INT_ARGB);
            ImageIO.write(tempImage, "jpg", tempImageFile);

            // Test loading from exact path
            bitmapItem = new BitmapItem(1, tempImageName);
            assertNotNull("Image name should be set", bitmapItem.getName());
            assertEquals("Image name should match", tempImageName, bitmapItem.getName());

            // Test the draw method with the loaded image
            bitmapItem.draw(0, 0, 1.0f, mockGraphics, defaultStyle, mockObserver);

        } catch (IOException e)
        {
            System.err.println("Error in image test: " + e.getMessage());
        } finally
        {
            // Clean up
            if (tempImageFile != null && tempImageFile.exists())
            {
                tempImageFile.delete();
            }
        }
    }

    @Test
    public void testConstantsValues()
    {
        // Test the constant values in BitmapItem
        assertEquals("FILE constant should be correct", "File ", BitmapItem.FILE);
        assertEquals("NOTFOUND constant should be correct", " not found", BitmapItem.NOTFOUND);
    }

    @Test
    public void testResourcePathLoading()
    {
        // Test loading from classpath resources path
        String resourceName = "test-resource.jpg";

        // We don't need to create actual resources since the constructor will 
        // handle failures gracefully and use fallback image
        bitmapItem = new BitmapItem(1, resourceName);

        // Just verify that the item was created without errors
        assertEquals("Image name should be preserved", resourceName, bitmapItem.getName());

        // Test drawing the fallback image (or null image if fallback fails)
        // This should not throw an exception
        bitmapItem.draw(10, 10, 1.0f, mockGraphics, defaultStyle, mockObserver);
    }

    @Test
    public void testLoadingWithIOException()
    {
        // Create a BitmapItem with a name that will trigger an IOException
        // We can't easily create this scenario directly, so we'll use reflection to simulate
        try
        {
            // First create a bitmap item with a valid name but null image
            bitmapItem = new BitmapItem(1, "test.jpg");

            // Now set the bufferedImage to null explicitly to test the error paths
            Field imageField = BitmapItem.class.getDeclaredField("bufferedImage");
            imageField.setAccessible(true);
            imageField.set(bitmapItem, null);

            // Test getBoundingBox with null image
            Rectangle boundingBox = bitmapItem.getBoundingBox(mockGraphics, mockObserver, 1.0f, defaultStyle);

            // For null image, the bounding box should have width and height of 0
            assertEquals("X coordinate should be style indent", (int) defaultStyle.indent, boundingBox.x);
            assertEquals("Y coordinate should be 0", 0, boundingBox.y);
            assertEquals("Width should be 0 for null image", 0, boundingBox.width);
            assertEquals("Height should be 0 for null image", 0, boundingBox.height);

            // Test draw with null image - should not throw exception
            bitmapItem.draw(10, 10, 1.0f, mockGraphics, defaultStyle, mockObserver);
        } catch (Exception e)
        {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }

    @Test
    public void testScaledDrawing()
    {
        // Test drawing with different scale factors

        // Create a BitmapItem with a test image that we can control
        bitmapItem = new BitmapItem(1, "test-scaled.jpg");

        try
        {
            // Create a simple test image
            BufferedImage testImage = new BufferedImage(50, 50, BufferedImage.TYPE_INT_ARGB);
            // Draw something on the image so it's not blank
            Graphics g = testImage.getGraphics();
            g.setColor(Color.RED);
            g.fillRect(0, 0, 50, 50);
            g.dispose();

            // Set the bufferedImage using reflection
            Field imageField = BitmapItem.class.getDeclaredField("bufferedImage");
            imageField.setAccessible(true);
            imageField.set(bitmapItem, testImage);

            // Create a scaled output image to draw on
            BufferedImage outputImage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
            Graphics outputGraphics = outputImage.getGraphics();

            // Test drawing with various scales
            float[] scales = {0.5f, 1.0f, 2.0f};
            for (float scale : scales)
            {
                // Draw the image at the current scale
                bitmapItem.draw(20, 20, scale, outputGraphics, defaultStyle, mockObserver);

                // Verify the bounding box at this scale
                Rectangle boundingBox = bitmapItem.getBoundingBox(outputGraphics, mockObserver, scale, defaultStyle);
                assertEquals("Width should be scaled correctly", (int) (testImage.getWidth() * scale), boundingBox.width);
                assertEquals("Height should include leading + scaled height",
                        (int) (defaultStyle.leading * scale) + (int) (testImage.getHeight() * scale), boundingBox.height);
            }

            outputGraphics.dispose();
        } catch (Exception e)
        {
            fail("Exception should not be thrown during scaled drawing test: " + e.getMessage());
        }
    }
} 