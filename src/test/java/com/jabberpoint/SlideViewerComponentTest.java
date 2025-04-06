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
 * Tests for the SlideViewerComponent class without using GUI dependencies
 */
public class SlideViewerComponentTest
{

    private SlideViewerComponent component;
    private Presentation presentation;

    @Before
    public void setUp()
    {
        presentation = new Presentation();
        // Create the component but don't test any GUI functionality
        component = new SlideViewerComponent(presentation);
    }

    @Test
    public void testClassStructure() throws Exception
    {
        // Verify class structure using reflection
        Class<?> clazz = SlideViewerComponent.class;

        // Check that SlideViewerComponent implements PresentationObserver
        assertTrue("SlideViewerComponent should implement PresentationObserver",
                PresentationObserver.class.isAssignableFrom(clazz));

        // Check for required methods
        assertTrue("Should have paintComponent method",
                hasMethod(clazz, "paintComponent", new Class<?>[]{java.awt.Graphics.class}, void.class));
        assertTrue("Should have getPreferredSize method",
                hasMethod(clazz, "getPreferredSize", new Class<?>[0], java.awt.Dimension.class));
    }

    @Test
    public void testConstantsViaReflection() throws Exception
    {
        // Access the constants using reflection
        Field bgColorField = SlideViewerComponent.class.getDeclaredField("BGCOLOR");
        bgColorField.setAccessible(true);
        assertNotNull("BGCOLOR should not be null", bgColorField.get(null));
        assertTrue("BGCOLOR should be a Color", bgColorField.get(null) instanceof Color);

        Field colorField = SlideViewerComponent.class.getDeclaredField("COLOR");
        colorField.setAccessible(true);
        assertNotNull("COLOR should not be null", colorField.get(null));
        assertTrue("COLOR should be a Color", colorField.get(null) instanceof Color);

        Field fontNameField = SlideViewerComponent.class.getDeclaredField("FONTNAME");
        fontNameField.setAccessible(true);
        assertNotNull("FONTNAME should not be null", fontNameField.get(null));
        assertTrue("FONTNAME should be a String", fontNameField.get(null) instanceof String);

        Field fontStyleField = SlideViewerComponent.class.getDeclaredField("FONTSTYLE");
        fontStyleField.setAccessible(true);
        assertEquals("FONTSTYLE should be Font.BOLD", Font.BOLD, fontStyleField.getInt(null));
    }

    @Test
    public void testPresentationInitialization() throws Exception
    {
        // Access presentation field using reflection
        Field presentationField = SlideViewerComponent.class.getDeclaredField("presentation");
        presentationField.setAccessible(true);
        Presentation componentPresentation = (Presentation) presentationField.get(component);

        // Verify the presentation was set correctly
        assertNotNull("Presentation should not be null", componentPresentation);
        assertEquals("Presentation should match the one passed to constructor",
                presentation, componentPresentation);
    }

    @Test
    public void testObserverRegistration() throws Exception
    {
        // Create a presentation
        Presentation presentation = new Presentation();

        // Create a component with this presentation 
        SlideViewerComponent component = new SlideViewerComponent(presentation);

        // Access the observer manager field using reflection
        Field observerManagerField = Presentation.class.getDeclaredField("observerManager");
        observerManagerField.setAccessible(true);
        PresentationObserverManager manager =
                (PresentationObserverManager) observerManagerField.get(presentation);

        // Access the observers list using reflection
        Field observersField = PresentationObserverManager.class.getDeclaredField("observers");
        observersField.setAccessible(true);
        java.util.List<PresentationObserver> observers =
                (java.util.List<PresentationObserver>) observersField.get(manager);

        // Verify the component registered itself as an observer
        boolean found = false;
        for (PresentationObserver observer : observers)
        {
            if (observer == component)
            {
                found = true;
                break;
            }
        }
        assertTrue("Component should register itself as an observer", found);
    }

    @Test
    public void testOnSlideChanged() throws Exception
    {
        // Create test slide
        Slide testSlide = new Slide();
        testSlide.setTitle("Test Slide");
        presentation.append(testSlide);

        // Create a subclass to track if repaint is called
        boolean[] repaintCalled = new boolean[1];
        SlideViewerComponent spyComponent = new SlideViewerComponent(presentation)
        {
            @Override
            public void repaint()
            {
                repaintCalled[0] = true;
                super.repaint();
            }
        };

        // Call onSlideChanged
        spyComponent.onSlideChanged(0);

        // Verify slide was updated
        Field slideField = SlideViewerComponent.class.getDeclaredField("slide");
        slideField.setAccessible(true);
        Slide currentSlide = (Slide) slideField.get(spyComponent);

        // Verify slide was set and repaint was called
        assertNotNull("Slide should be set", currentSlide);
        assertEquals("Correct slide should be set", testSlide, currentSlide);
        assertTrue("repaint() should be called when onSlideChanged is called", repaintCalled[0]);
    }

    @Test
    public void testOnPresentationChanged() throws Exception
    {
        // Create test slide
        Slide testSlide = new Slide();
        testSlide.setTitle("Test Slide");
        presentation.append(testSlide);
        presentation.setSlideNumber(0);

        // Create a subclass to track if repaint is called
        boolean[] repaintCalled = new boolean[1];
        SlideViewerComponent spyComponent = new SlideViewerComponent(presentation)
        {
            @Override
            public void repaint()
            {
                repaintCalled[0] = true;
                super.repaint();
            }
        };

        // Call onPresentationChanged
        spyComponent.onPresentationChanged();

        // Verify slide was updated
        Field slideField = SlideViewerComponent.class.getDeclaredField("slide");
        slideField.setAccessible(true);
        Slide currentSlide = (Slide) slideField.get(spyComponent);

        // Verify slide was set and repaint was called
        assertNotNull("Slide should be set", currentSlide);
        assertEquals("Current slide from presentation should be set", testSlide, currentSlide);
        assertTrue("repaint() should be called when onPresentationChanged is called", repaintCalled[0]);
    }

    @Test
    public void testOnPresentationChangedWithEmptyPresentation() throws Exception
    {
        // Create empty presentation
        Presentation emptyPresentation = new Presentation();

        // Create component with empty presentation
        SlideViewerComponent emptyComponent = new SlideViewerComponent(emptyPresentation);

        // Call onPresentationChanged
        emptyComponent.onPresentationChanged();

        // Verify slide is null
        Field slideField = SlideViewerComponent.class.getDeclaredField("slide");
        slideField.setAccessible(true);
        Slide currentSlide = (Slide) slideField.get(emptyComponent);

        // Verify slide is null for empty presentation
        assertNull("Slide should be null for empty presentation", currentSlide);
    }

    // Helper method to check if a class has a specific method
    private boolean hasMethod(Class<?> clazz, String methodName, Class<?>[] paramTypes, Class<?> returnType)
    {
        try
        {
            Method method = clazz.getDeclaredMethod(methodName, paramTypes);
            return method.getReturnType().equals(returnType);
        } catch (NoSuchMethodException e)
        {
            return false;
        }
    }
} 