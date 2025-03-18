package com.jabberpoint;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import java.awt.Rectangle;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import javax.imageio.ImageIO;

public class BitmapItemTest {
    
    private static final String TEST_IMAGE_PATH = "src/test/resources/test-image.jpg";
    
    @Before
    public void setUp() throws IOException {
        // Create a valid test image file if it doesn't exist or is not a valid image
        File testImage = new File(TEST_IMAGE_PATH);
        if (!testImage.exists() || !isValidImageFile(testImage)) {
            // Create a tiny 1x1 pixel test image
            createValidTestImage(testImage);
        }
    }
    
    private boolean isValidImageFile(File file) {
        try {
            ImageIO.read(file);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    
    private void createValidTestImage(File file) throws IOException {
        // Use a default test image from the main resources if available
        // Otherwise create a 1x1 pixel image programmatically
        File defaultImage = new File("src/main/resources/JabberPoint.jpg");
        if (defaultImage.exists()) {
            Files.copy(defaultImage.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } else {
            // Ensure the directory exists
            file.getParentFile().mkdirs();
            
            // Create a test image with content
            Files.write(file.toPath(), "This is a placeholder file for tests".getBytes());
        }
    }
    
    @Test
    public void testBitmapCreation() {
        // Use the sample image we've set up
        BitmapItem bitmapItem = new BitmapItem(1, TEST_IMAGE_PATH);
        assertNotNull("BitmapItem should be created", bitmapItem);
        assertEquals("Level should be set correctly", 1, bitmapItem.getLevel());
    }
    
    @Test
    public void testEmptyBitmapCreation() {
        BitmapItem bitmapItem = new BitmapItem();
        assertNotNull("Empty BitmapItem should be created", bitmapItem);
        assertEquals("Default level should be 0", 0, bitmapItem.getLevel());
        assertNull("Name should be null for empty bitmap", bitmapItem.getName());
    }
    
    @Test
    public void testGetName() {
        BitmapItem bitmapItem = new BitmapItem(1, TEST_IMAGE_PATH);
        assertEquals("getName should return the correct path", TEST_IMAGE_PATH, bitmapItem.getName());
    }
    
    @Test
    public void testGetBoundingBox() {
        // This assumes test-image.jpg exists
        BitmapItem bitmapItem = new BitmapItem(1, "src/test/resources/test-image.jpg");
        
        // Basic check - we mainly want to verify it doesn't throw exceptions
        // Create a style with indent=10, color=black, font size=12, leading=5
        Rectangle boundingBox = bitmapItem.getBoundingBox(null, null, 1.0f, new Style(10, Color.BLACK, 12, 5));
        assertNotNull("Bounding box should be calculated", boundingBox);
    }
    
    @Test
    public void testInvalidImage() {
        // Test with non-existent image file
        String nonExistentPath = "non-existent-image.jpg";
        BitmapItem bitmapItem = new BitmapItem(1, nonExistentPath);
        assertNotNull("BitmapItem should be created even with invalid image", bitmapItem);
        assertEquals("getName should return the file name", nonExistentPath, bitmapItem.getName());
    }
} 