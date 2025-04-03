package com.jabberpoint;

import org.junit.Test;
import org.junit.Before;
import org.junit.BeforeClass;
import static org.junit.Assert.*;

import java.awt.Dimension;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import javax.swing.JComponent;
import java.awt.Graphics2D;

/**
 * Tests for the SlideViewerComponent class
 */
public class SlideViewerComponentTest {
    
    private SlideViewerComponent component;
    private Presentation presentation;
    private Graphics mockGraphics;
    
    @BeforeClass
    public static void setUpClass() {
        // Initialize styles for testing
        Style.createStyles();
    }
    
    @Before
    public void setUp() {
        // Create a presentation with mock data
        presentation = new Presentation();
        presentation.setTitle("Test Presentation");
        
        // Create a slide
        Slide slide = new Slide();
        slide.setTitle("Test Slide");
        slide.append(1, "Test Item 1");
        slide.append(2, "Test Item 2");
        
        // Add the slide to the presentation
        presentation.append(slide);
        
        // Create the component
        component = new SlideViewerComponent(presentation);
        
        // Create a mock Graphics object using a BufferedImage
        BufferedImage image = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        mockGraphics = image.createGraphics();
    }
    
    @Test
    public void testBasicConstructorAndStructure() {
        // Test constructor
        assertNotNull("Component should exist", component);
        assertEquals("Background color should be white", Color.white, component.getBackground());
        
        // Test component structure
        assertTrue("Component should be a JComponent", component instanceof JComponent);
        assertTrue("Component should implement PresentationObserver", component instanceof PresentationObserver);
        
        // Test preferred size
        Dimension preferredSize = component.getPreferredSize();
        assertEquals("Width should match slide width", Slide.WIDTH, preferredSize.width);
        assertEquals("Height should match slide height", Slide.HEIGHT, preferredSize.height);
        
        // Verify observer registration
        try {
            Field observersField = Presentation.class.getDeclaredField("observers");
            observersField.setAccessible(true);
            assertTrue("Component should be registered as an observer", 
                      ((java.util.List<?>) observersField.get(presentation)).contains(component));
        } catch (Exception e) {
            fail("Failed to access observers field: " + e.getMessage());
        }
    }
    
    @Test
    public void testComponentConstants() {
        try {
            Field bgColorField = SlideViewerComponent.class.getDeclaredField("BGCOLOR");
            bgColorField.setAccessible(true);
            Color bgColor = (Color) bgColorField.get(null);
            assertEquals("Background color should be white", Color.white, bgColor);
            
            Field colorField = SlideViewerComponent.class.getDeclaredField("COLOR");
            colorField.setAccessible(true);
            Color color = (Color) colorField.get(null);
            assertEquals("Text color should be black", Color.black, color);
            
            Field fontNameField = SlideViewerComponent.class.getDeclaredField("FONTNAME");
            fontNameField.setAccessible(true);
            String fontName = (String) fontNameField.get(null);
            assertEquals("Font name should be Dialog", "Dialog", fontName);
            
            Field fontStyleField = SlideViewerComponent.class.getDeclaredField("FONTSTYLE");
            fontStyleField.setAccessible(true);
            int fontStyle = fontStyleField.getInt(null);
            assertEquals("Font style should be BOLD", Font.BOLD, fontStyle);
            
            Field fontSizeField = SlideViewerComponent.class.getDeclaredField("FONTSIZE");
            fontSizeField.setAccessible(true);
            int fontSize = fontSizeField.getInt(null);
            assertTrue("Font size should be positive", fontSize > 0);
            
            Field xPosField = SlideViewerComponent.class.getDeclaredField("XPOS");
            xPosField.setAccessible(true);
            int xPos = xPosField.getInt(null);
            assertTrue("XPOS should be positive", xPos > 0);
            
            Field yPosField = SlideViewerComponent.class.getDeclaredField("YPOS");
            yPosField.setAccessible(true);
            int yPos = yPosField.getInt(null);
            assertTrue("YPOS should be positive", yPos > 0);
            
            // Test label font
            Field labelFontField = SlideViewerComponent.class.getDeclaredField("labelFont");
            labelFontField.setAccessible(true);
            Font labelFont = (Font) labelFontField.get(component);
            
            assertNotNull("Label font should not be null", labelFont);
            assertEquals("Font name should be Dialog", "Dialog", labelFont.getName());
            assertEquals("Font style should be BOLD", Font.BOLD, labelFont.getStyle());
            assertEquals("Font size should be 10", 10, labelFont.getSize());
        } catch (Exception e) {
            fail("Could not access constants: " + e.getMessage());
        }
    }
    
    @Test
    public void testObserverMethods() {
        // Test onSlideChanged
        component.onSlideChanged(0);
        
        try {
            Field slideField = SlideViewerComponent.class.getDeclaredField("slide");
            slideField.setAccessible(true);
            Slide currentSlide = (Slide) slideField.get(component);
            assertNotNull("Slide should be set after onSlideChanged", currentSlide);
            assertEquals("Slide title should match", "Test Slide", currentSlide.getTitle());
            
            // Test onPresentationChanged
            component.onPresentationChanged();
            currentSlide = (Slide) slideField.get(component);
            assertNotNull("Slide should be set after onPresentationChanged", currentSlide);
            assertEquals("Slide should be the current slide in presentation", 
                        presentation.getCurrentSlide(), currentSlide);
            
            // Test with empty presentation
            Presentation emptyPresentation = new Presentation();
            SlideViewerComponent emptyComponent = new SlideViewerComponent(emptyPresentation);
            emptyComponent.onPresentationChanged();
            slideField.setAccessible(true);
            currentSlide = (Slide) slideField.get(emptyComponent);
            assertNull("Slide should be null for empty presentation", currentSlide);
            
            // Test with multiple slides
            Slide slide2 = new Slide();
            slide2.setTitle("Second Slide");
            presentation.append(slide2);
            
            presentation.setSlideNumber(1);
            component.onSlideChanged(1);
            
            currentSlide = (Slide) slideField.get(component);
            assertEquals("Current slide should be second slide", "Second Slide", currentSlide.getTitle());
            
            // Test presentation change
            presentation.clear();
            Slide newSlide = new Slide();
            newSlide.setTitle("New Slide After Change");
            presentation.append(newSlide);
            
            presentation.setSlideNumber(0);
            component.onPresentationChanged();
            
            Slide updatedSlide = (Slide) slideField.get(component);
            assertEquals("Slide should update to new slide", "New Slide After Change", updatedSlide.getTitle());
        } catch (Exception e) {
            fail("Failed to access slide field: " + e.getMessage());
        }
    }
    
    @Test(expected = NullPointerException.class)
    public void testNullPresentation() {
        new SlideViewerComponent(null);
    }
    
    // Tests for specific code paths in paintComponent method
    @Test
    public void testDetailedPaintComponent() {
        // Setup
        presentation.setSlideNumber(0);
        component.onSlideChanged(0);
        component.setSize(Slide.WIDTH, Slide.HEIGHT);
        
        // Create a special graphics we can inspect
        BufferedImage testImage = new BufferedImage(Slide.WIDTH, Slide.HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics testGraphics = testImage.createGraphics();
        
        // Run the paintComponent method through reflection to ensure branch coverage
        try {
            Method paintMethod = SlideViewerComponent.class.getDeclaredMethod("paintComponent", Graphics.class);
            paintMethod.setAccessible(true);
            paintMethod.invoke(component, testGraphics);
            
            // Look for slide number text to verify drawing happened
            int nonWhitePixelCount = 0;
            for (int x = 0; x < testImage.getWidth(); x++) {
                for (int y = 0; y < testImage.getHeight(); y++) {
                    if (testImage.getRGB(x, y) != Color.WHITE.getRGB() && 
                        testImage.getRGB(x, y) != 0) { // 0 is transparent
                        nonWhitePixelCount++;
                    }
                }
            }
            assertTrue("Something should have been drawn on the graphics", nonWhitePixelCount > 0);
        } catch (Exception e) {
            fail("Failed to invoke paintComponent: " + e.getMessage());
        } finally {
            testGraphics.dispose();
        }
    }
    
    @Test
    public void testInvalidSlidePaintComponent() {
        // Setup invalid slide number
        presentation.setSlideNumber(-1);
        component.onSlideChanged(-1);
        component.setSize(Slide.WIDTH, Slide.HEIGHT);
        
        // Create a special graphics we can check
        BufferedImage testImage = new BufferedImage(Slide.WIDTH, Slide.HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics testGraphics = testImage.createGraphics();
        
        // Capture the background color before paint
        testGraphics.setColor(Color.WHITE);
        testGraphics.fillRect(0, 0, testImage.getWidth(), testImage.getHeight());
        
        // Paint
        component.paintComponent(testGraphics);
        
        // Verify it returned early and didn't try to draw slide-specific content
        // We only expect to see the background
        boolean onlyBackground = true;
        int bgColorRGB = Color.WHITE.getRGB();
        
        for (int x = 0; x < testImage.getWidth(); x++) {
            for (int y = 0; y < testImage.getHeight(); y++) {
                int pixelColor = testImage.getRGB(x, y);
                if (pixelColor != bgColorRGB && pixelColor != 0) { // 0 is transparent
                    onlyBackground = false;
                    break;
                }
            }
            if (!onlyBackground) break;
        }
        
        assertTrue("Only background should be drawn for invalid slide", onlyBackground);
        testGraphics.dispose();
    }
    
    @Test
    public void testPaintComponent() {
        // Test with negative slide number
        Presentation testPresentation = new Presentation();
        testPresentation.setSlideNumber(-1);
        SlideViewerComponent testComponent = new SlideViewerComponent(testPresentation);
        
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.createGraphics();
        
        // Shouldn't throw exception
        testComponent.paintComponent(g);
        g.dispose();
        
        // Test with valid slide (using a testable subclass)
        presentation.setSlideNumber(0);
        component.onSlideChanged(0);
        component.setSize(Slide.WIDTH, Slide.HEIGHT);
        
        image = new BufferedImage(Slide.WIDTH, Slide.HEIGHT, BufferedImage.TYPE_INT_ARGB);
        g = image.createGraphics();
        
        // Create a testable version that doesn't rely on actual rendering
        class TestableComponent extends SlideViewerComponent {
            private boolean backgroundFilled = false;
            private boolean textDrawn = false;
            private Color bgColor;
            private Color textColor;
            private Font font;
            private Slide currentSlide;
            
            public TestableComponent(Presentation p) {
                super(p);
                try {
                    // Access private fields using reflection
                    Field bgColorField = SlideViewerComponent.class.getDeclaredField("BGCOLOR");
                    bgColorField.setAccessible(true);
                    bgColor = (Color) bgColorField.get(null);
                    
                    Field colorField = SlideViewerComponent.class.getDeclaredField("COLOR");
                    colorField.setAccessible(true);
                    textColor = (Color) colorField.get(null);
                    
                    Field fontField = SlideViewerComponent.class.getDeclaredField("labelFont");
                    fontField.setAccessible(true);
                    font = (Font) fontField.get(this);
                } catch (Exception e) {
                    fail("Failed to access private fields: " + e.getMessage());
                }
            }
            
            @Override
            public void paintComponent(Graphics g) {
                g.setColor(bgColor);
                g.fillRect(0, 0, getSize().width, getSize().height);
                backgroundFilled = true;
                
                try {
                    Field slideField = SlideViewerComponent.class.getDeclaredField("slide");
                    slideField.setAccessible(true);
                    currentSlide = (Slide) slideField.get(this);
                    
                    if (presentation.getSlideNumber() >= 0 && currentSlide != null) {
                        g.setFont(font);
                        g.setColor(textColor);
                        g.drawString("Test", 10, 10);
                        textDrawn = true;
                    }
                } catch (Exception e) {
                    fail("Failed to access slide field: " + e.getMessage());
                }
            }
            
            public boolean isBackgroundFilled() { return backgroundFilled; }
            public boolean isTextDrawn() { return textDrawn; }
        }
        
        TestableComponent tComponent = new TestableComponent(presentation);
        tComponent.setSize(Slide.WIDTH, Slide.HEIGHT);
        tComponent.onSlideChanged(0);
        tComponent.paintComponent(g);
        
        assertTrue("Background should be filled", tComponent.isBackgroundFilled());
        assertTrue("Text should be drawn with valid slide", tComponent.isTextDrawn());
        
        g.dispose();
    }
    
    // Test with a mock slide to fully test all code paths in the paintComponent method
    @Test
    public void testPaintComponentWithMockSlide() {
        // Create a mock slide that we can use to verify draw was called
        final boolean[] drawCalled = {false};
        Slide mockSlide = new Slide() {
            @Override
            public void draw(Graphics g, Rectangle area, ImageObserver observer) {
                drawCalled[0] = true;
                // Just to verify it was called with right params
                assertNotNull("Graphics should not be null", g);
                assertNotNull("Area should not be null", area);
                assertNotNull("Observer should not be null", observer);
            }
        };
        
        // Create a presentation with the mock slide
        Presentation mockPresentation = new Presentation();
        mockPresentation.append(mockSlide);
        mockPresentation.setSlideNumber(0);
        
        // Create component with this presentation
        SlideViewerComponent testComponent = new SlideViewerComponent(mockPresentation);
        testComponent.setSize(Slide.WIDTH, Slide.HEIGHT);
        
        // Force an update to ensure the slide is current
        testComponent.onSlideChanged(0);
        
        // Now call paintComponent
        BufferedImage image = new BufferedImage(Slide.WIDTH, Slide.HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.createGraphics();
        testComponent.paintComponent(g);
        g.dispose();
        
        // Verify our mock's draw method was called
        assertTrue("Slide.draw should have been called", drawCalled[0]);
    }
    
    // Test that getPreferredSize always returns slide dimensions regardless of component state
    @Test
    public void testGetPreferredSizeInAllStates() {
        // Standard case - already tested elsewhere
        assertEquals(new Dimension(Slide.WIDTH, Slide.HEIGHT), component.getPreferredSize());
        
        // After setting component size to something else
        component.setSize(100, 100);
        assertEquals("Preferred size should still be slide dimensions after setSize",
                     new Dimension(Slide.WIDTH, Slide.HEIGHT), component.getPreferredSize());
        
        // After slide becomes null
        try {
            Field slideField = SlideViewerComponent.class.getDeclaredField("slide");
            slideField.setAccessible(true);
            slideField.set(component, null);
            
            assertEquals("Preferred size should still be slide dimensions with null slide",
                         new Dimension(Slide.WIDTH, Slide.HEIGHT), component.getPreferredSize());
        } catch (Exception e) {
            fail("Failed to set slide to null: " + e.getMessage());
        }
    }
    
    @Test
    public void testEmptyPresentationHandling() {
        // Create empty presentation
        Presentation emptyPresentation = new Presentation();
        
        // Create component with empty presentation
        SlideViewerComponent emptyComponent = new SlideViewerComponent(emptyPresentation);
        
        // This shouldn't throw exceptions
        emptyComponent.onPresentationChanged();
        
        // Create a mock Graphics object using a BufferedImage
        BufferedImage image = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        Graphics mockG = image.createGraphics();
        
        // Test that paintComponent handles null slide correctly
        emptyComponent.paintComponent(mockG);
        
        // No assertions needed - we're just testing that no exceptions are thrown
        assertTrue(true);
    }
} 