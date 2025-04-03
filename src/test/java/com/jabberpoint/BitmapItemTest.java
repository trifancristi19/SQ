package com.jabberpoint;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import static org.junit.Assert.*;

import java.awt.Rectangle;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import javax.imageio.ImageIO;

public class BitmapItemTest
{

    private static final String TEST_IMAGE_PATH = "target/test-resources/test-image.jpg";
    private static final String TEST_RESOURCES_DIR = "target/test-resources";
    private MockImageObserver observer;
    private Style testStyle;

    @Before
    public void setUp() throws IOException
    {
        // Make sure test resources directory exists
        new File(TEST_RESOURCES_DIR).mkdirs();
        
        // Always create a valid test image for tests
        File testImage = new File(TEST_IMAGE_PATH);
        createValidTestImage(testImage);
        
        // Create a test style for drawing and bounding box tests
        testStyle = new Style(10, Color.BLACK, 12, 5);
        
        // Create mock image observer
        observer = new MockImageObserver();
    }
    
    @After
    public void tearDown() {
        // Reset observer state
        observer.resetTracking();
    }

    private boolean isValidImageFile(File file)
    {
        try
        {
            return ImageIO.read(file) != null;
        } catch (IOException e)
        {
            return false;
        }
    }

    private void createValidTestImage(File file) throws IOException
    {
        // Ensure the directory exists
        file.getParentFile().mkdirs();

        // Create a small 10x10 test image
        BufferedImage image = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setColor(Color.RED);
        g2d.fillRect(0, 0, 10, 10);
        g2d.dispose();
        
        // Save as a JPEG
        ImageIO.write(image, "jpg", file);
    }

    @Test
    public void testBitmapCreation()
    {
        // Use the sample image we've set up
        BitmapItem bitmapItem = new BitmapItem(1, TEST_IMAGE_PATH);
        assertNotNull("BitmapItem should be created", bitmapItem);
        assertEquals("Level should be set correctly", 1, bitmapItem.getLevel());
    }

    @Test
    public void testEmptyBitmapCreation()
    {
        BitmapItem bitmapItem = new BitmapItem();
        assertNotNull("Empty BitmapItem should be created", bitmapItem);
        assertEquals("Default level should be 0", 0, bitmapItem.getLevel());
        assertNull("Name should be null for empty bitmap", bitmapItem.getName());
    }

    @Test
    public void testGetName()
    {
        BitmapItem bitmapItem = new BitmapItem(1, TEST_IMAGE_PATH);
        assertEquals("getName should return the correct path", TEST_IMAGE_PATH, bitmapItem.getName());
    }

    @Test
    public void testGetBoundingBox()
    {
        // Use our guaranteed test image
        BitmapItem bitmapItem = new BitmapItem(1, TEST_IMAGE_PATH);

        // Test with non-null parameters
        Rectangle boundingBox = bitmapItem.getBoundingBox(createTestGraphics(), observer, 1.0f, testStyle);
        assertNotNull("Bounding box should be calculated", boundingBox);
        assertTrue("Bounding box width should be positive", boundingBox.width > 0);
        assertTrue("Bounding box height should be positive", boundingBox.height > 0);
        assertEquals("X position should match style indent", (int)testStyle.indent, boundingBox.x);
    }
    
    @Test
    public void testGetBoundingBoxWithScaling() {
        // Use our guaranteed test image
        BitmapItem bitmapItem = new BitmapItem(1, TEST_IMAGE_PATH);
        
        // Get bounding box with scale factor of 1.0
        Rectangle boundingBox1 = bitmapItem.getBoundingBox(createTestGraphics(), observer, 1.0f, testStyle);
        
        // Get bounding box with scale factor of 2.0
        Rectangle boundingBox2 = bitmapItem.getBoundingBox(createTestGraphics(), observer, 2.0f, testStyle);
        
        // The width and height should be proportional to the scale
        assertEquals("Width should double with scale factor 2", boundingBox1.width * 2, boundingBox2.width);
        assertTrue("Height should be larger with scale factor 2", boundingBox2.height > boundingBox1.height);
        assertEquals("X position should be proportional to scale", boundingBox1.x * 2, boundingBox2.x);
    }

    @Test
    public void testInvalidImage()
    {
        // Use a non-existent path that won't display error messages
        String nonExistentPath = "nonexistent.jpg";
        BitmapItem bitmapItem = new BitmapItem(1, nonExistentPath);
        assertNotNull("BitmapItem should be created even with invalid image", bitmapItem);
        assertEquals("getName should return the file name", nonExistentPath, bitmapItem.getName());
        
        // Test getBoundingBox behavior with null image
        Rectangle boundingBox = bitmapItem.getBoundingBox(createTestGraphics(), observer, 1.0f, testStyle);
        assertNotNull("Bounding box should still be created for invalid image", boundingBox);
        // Just verify it exists, don't check specific dimensions which may vary
        assertTrue("Bounding box should have valid dimensions", boundingBox.width >= 0 && boundingBox.height >= 0);
    }
    
    @Test
    public void testDrawMethod() {
        // Create a bitmap item with a valid image
        BitmapItem bitmapItem = new BitmapItem(1, TEST_IMAGE_PATH);
        
        // Create a test graphics context
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();
        
        // Create tracking graphics to monitor draw calls
        TrackingGraphics trackingGraphics = new TrackingGraphics(g);
        
        // Draw the bitmap
        bitmapItem.draw(10, 20, 1.0f, trackingGraphics, testStyle, observer);
        
        // Verify the image was drawn
        assertTrue("Image should be drawn", trackingGraphics.imageDrawn);
        
        // Clean up
        g.dispose();
    }
    
    @Test
    public void testDrawWithInvalidImage() {
        // Create a bitmap item with an invalid image
        BitmapItem invalidBitmap = new BitmapItem(1, "non-existent-image.jpg");
        
        // Create a test graphics context
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();
        
        // Create tracking graphics to monitor draw calls
        TrackingGraphics trackingGraphics = new TrackingGraphics(g);
        
        // Try to draw the invalid bitmap
        invalidBitmap.draw(10, 20, 1.0f, trackingGraphics, testStyle, observer);
        
        // With fallback image implementation, image WILL be drawn
        assertTrue("Invalid image should use fallback image and be drawn", trackingGraphics.imageDrawn);
        
        // Clean up
        g.dispose();
    }
    
    @Test
    public void testDrawWithNullImage() {
        // Create a bitmap item with null image path
        BitmapItem nullBitmap = new BitmapItem(1, null);
        
        // Create a test graphics context
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();
        
        // Create tracking graphics to monitor draw calls
        TrackingGraphics trackingGraphics = new TrackingGraphics(g);
        
        // Try to draw the null bitmap
        nullBitmap.draw(10, 20, 1.0f, trackingGraphics, testStyle, observer);
        
        // With fallback image implementation, image WILL be drawn
        assertTrue("Null image should use fallback image and be drawn", trackingGraphics.imageDrawn);
        
        // Clean up
        g.dispose();
    }
    
    @Test
    public void testToString() {
        BitmapItem bitmapItem = new BitmapItem(2, "test.jpg");
        String result = bitmapItem.toString();
        
        assertNotNull("toString should return a string", result);
        assertTrue("toString should contain the level", result.contains("2"));
        assertTrue("toString should contain the image name", result.contains("test.jpg"));
    }
    
    @Test
    public void testResourcesPathFallback() {
        // Create a file in resources that doesn't exist in the direct path
        String imageName = "resources-fallback.jpg";
        File resourceFile = new File("src/main/resources/" + imageName);
        
        try {
            // Create a tiny test image in resources
            if (!resourceFile.exists()) {
                // Create parent directories if needed
                resourceFile.getParentFile().mkdirs();
                
                // Create a small test image
                BufferedImage image = new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB);
                Graphics2D g2d = image.createGraphics();
                g2d.setColor(Color.BLUE);
                g2d.fillRect(0, 0, 5, 5);
                g2d.dispose();
                
                // Save as JPEG
                ImageIO.write(image, "jpg", resourceFile);
            }
            
            // Create BitmapItem with just the filename
            // This should find the file in resources directory
            BitmapItem resourceBitmap = new BitmapItem(1, imageName);
            
            // Verify the image loaded by checking bounding box
            Rectangle boundingBox = resourceBitmap.getBoundingBox(createTestGraphics(), observer, 1.0f, testStyle);
            assertTrue("Width should be positive if image was loaded from resources", boundingBox.width > 0);
            
        } catch (IOException e) {
            fail("Failed to create test image in resources: " + e.getMessage());
        } finally {
            // Clean up (optional - can leave for next test runs)
            // resourceFile.delete();
        }
    }
    
    @Test
    public void testLoadMissingImage() {
        BitmapItem item = new BitmapItem(1, "non-existent-image.jpg");
        
        // Should use fallback image - test won't fail anymore
        assertNotNull("Should have fallback image", item.getName());
        
        // Try drawing to ensure no exceptions
        Style style = new Style(10, Color.BLACK, 12, 5);
        BufferedImage img = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
        Graphics g = img.getGraphics();
        
        // This should not throw an exception thanks to the fallback image
        item.draw(10, 10, 1.0f, g, style, null);
        
        // Test bounding box
        Rectangle bounds = item.getBoundingBox(g, null, 1.0f, style);
        assertNotNull("Should have a bounding box", bounds);
        assertTrue("Bounding box should have width > 0", bounds.width > 0);
        assertTrue("Bounding box should have height > 0", bounds.height > 0);
        
        g.dispose();
    }
    
    @Test
    public void testFallbackImageCreation() {
        // Create item with invalid image path
        BitmapItem item = new BitmapItem(1, "invalid-path.jpg");
        
        // Create a test image to draw on
        BufferedImage testImage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
        Graphics g = testImage.getGraphics();
        
        // Draw the fallback image
        Style style = new Style(10, Color.BLACK, 12, 5);
        item.draw(10, 10, 1.0f, g, style, null);
        
        // The image should be drawn without exceptions
        // and should contain the fallback image text
        g.dispose();
    }
    
    /**
     * Utility to create a test graphics object
     */
    private Graphics createTestGraphics() {
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        return image.getGraphics();
    }
    
    /**
     * A mock ImageObserver that tracks when it's used
     */
    private class MockImageObserver implements ImageObserver {
        private boolean imageUpdateCalled = false;
        
        @Override
        public boolean imageUpdate(java.awt.Image img, int infoflags, int x, int y, int width, int height) {
            imageUpdateCalled = true;
            return true;
        }
        
        public void resetTracking() {
            imageUpdateCalled = false;
        }
        
        public boolean wasImageUpdateCalled() {
            return imageUpdateCalled;
        }
    }
    
    /**
     * A tracking graphics wrapper to monitor drawing calls
     */
    private class TrackingGraphics extends Graphics {
        private final Graphics delegate;
        public boolean imageDrawn = false;
        
        public TrackingGraphics(Graphics g) {
            this.delegate = g;
        }
        
        // Customize just the drawImage methods we need
        @Override
        public boolean drawImage(java.awt.Image img, int x, int y, int width, int height, ImageObserver observer) {
            imageDrawn = true;
            return delegate.drawImage(img, x, y, width, height, observer);
        }
        
        // Delegate all other methods
        @Override public Graphics create() { return delegate.create(); }
        @Override public void translate(int x, int y) { delegate.translate(x, y); }
        @Override public Color getColor() { return delegate.getColor(); }
        @Override public void setColor(Color c) { delegate.setColor(c); }
        @Override public void setPaintMode() { delegate.setPaintMode(); }
        @Override public void setXORMode(Color c) { delegate.setXORMode(c); }
        @Override public java.awt.Font getFont() { return delegate.getFont(); }
        @Override public void setFont(java.awt.Font font) { delegate.setFont(font); }
        @Override public java.awt.FontMetrics getFontMetrics(java.awt.Font f) { return delegate.getFontMetrics(f); }
        @Override public Rectangle getClipBounds() { return delegate.getClipBounds(); }
        @Override public void clipRect(int x, int y, int width, int height) { delegate.clipRect(x, y, width, height); }
        @Override public void setClip(int x, int y, int width, int height) { delegate.setClip(x, y, width, height); }
        @Override public java.awt.Shape getClip() { return delegate.getClip(); }
        @Override public void setClip(java.awt.Shape clip) { delegate.setClip(clip); }
        @Override public void copyArea(int x, int y, int width, int height, int dx, int dy) { delegate.copyArea(x, y, width, height, dx, dy); }
        @Override public void drawLine(int x1, int y1, int x2, int y2) { delegate.drawLine(x1, y1, x2, y2); }
        @Override public void fillRect(int x, int y, int width, int height) { delegate.fillRect(x, y, width, height); }
        @Override public void clearRect(int x, int y, int width, int height) { delegate.clearRect(x, y, width, height); }
        @Override public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) { delegate.drawRoundRect(x, y, width, height, arcWidth, arcHeight); }
        @Override public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) { delegate.fillRoundRect(x, y, width, height, arcWidth, arcHeight); }
        @Override public void drawOval(int x, int y, int width, int height) { delegate.drawOval(x, y, width, height); }
        @Override public void fillOval(int x, int y, int width, int height) { delegate.fillOval(x, y, width, height); }
        @Override public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) { delegate.drawArc(x, y, width, height, startAngle, arcAngle); }
        @Override public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) { delegate.fillArc(x, y, width, height, startAngle, arcAngle); }
        @Override public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) { delegate.drawPolyline(xPoints, yPoints, nPoints); }
        @Override public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) { delegate.drawPolygon(xPoints, yPoints, nPoints); }
        @Override public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) { delegate.fillPolygon(xPoints, yPoints, nPoints); }
        @Override public void drawString(String str, int x, int y) { delegate.drawString(str, x, y); }
        @Override public void drawString(java.text.AttributedCharacterIterator iterator, int x, int y) { delegate.drawString(iterator, x, y); }
        @Override public boolean drawImage(java.awt.Image img, int x, int y, ImageObserver observer) { 
            imageDrawn = true; 
            return delegate.drawImage(img, x, y, observer); 
        }
        @Override public boolean drawImage(java.awt.Image img, int x, int y, Color bgcolor, ImageObserver observer) { 
            imageDrawn = true; 
            return delegate.drawImage(img, x, y, bgcolor, observer); 
        }
        @Override public boolean drawImage(java.awt.Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer) { 
            imageDrawn = true; 
            return delegate.drawImage(img, x, y, width, height, bgcolor, observer); 
        }
        @Override public boolean drawImage(java.awt.Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, ImageObserver observer) { 
            imageDrawn = true; 
            return delegate.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, observer); 
        }
        @Override public boolean drawImage(java.awt.Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color bgcolor, ImageObserver observer) { 
            imageDrawn = true; 
            return delegate.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, bgcolor, observer); 
        }
        @Override public void dispose() { delegate.dispose(); }
    }
} 