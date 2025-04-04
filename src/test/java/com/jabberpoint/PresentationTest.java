package com.jabberpoint;

import org.junit.Test;
import org.junit.Before;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.jabberpoint.io.PresentationLoader;

/**
 * Tests for the Presentation class
 */
public class PresentationTest
{

    private Presentation presentation;
    private boolean notificationReceived = false;
    private int slideNumberNotified = -1;
    private boolean presentationChangedNotified = false;

    @Before
    public void setUp()
    {
        this.presentation = new Presentation();
        presentation.setTitle("Test Presentation");

        // Create a slide
        Slide slide = new Slide();
        slide.setTitle("Test Slide 1");
        slide.append(1, "Test Item 1");

        // Add the slide to the presentation
        presentation.append(slide);

        // Reset notification flags
        notificationReceived = false;
        slideNumberNotified = -1;
        presentationChangedNotified = false;
    }

    @Test
    public void testConstructor()
    {
        Presentation p = new Presentation();
        assertNotNull("Presentation should not be null", p);
        assertEquals("Presentation size should be 0", 0, p.getSize());
    }

    @Test
    public void testAppendSlide()
    {
        Slide slide = new Slide();
        slide.setTitle("Test Slide 2");

        int initialSize = presentation.getSize();
        presentation.append(slide);

        assertEquals("Presentation size should increase by 1", initialSize + 1, presentation.getSize());
    }

    @Test
    public void testGetCurrentSlide()
    {
        Slide currentSlide = presentation.getCurrentSlide();
        assertNotNull("Current slide should not be null", currentSlide);
        assertEquals("Current slide title should match", "Test Slide 1", currentSlide.getTitle());
    }

    @Test
    public void testGetCurrentSlideWithInvalidNumber()
    {
        // Set an invalid slide number
        presentation.setSlideNumber(999);

        // Note: In the actual implementation, if slide number is invalid, the current slide is still returned
        // So we just verify it's not null rather than expecting null
        Slide slide = presentation.getCurrentSlide();
        assertNotNull("Current slide should still be valid even with invalid slide number", slide);
    }

    @Test
    public void testGetSlide()
    {
        Slide slide = presentation.getSlide(0);
        assertNotNull("Slide at index 0 should not be null", slide);
        assertEquals("Slide title should match", "Test Slide 1", slide.getTitle());
    }

    @Test
    public void testGetSlideWithInvalidIndex()
    {
        Slide slide = presentation.getSlide(999);
        assertNull("Slide should be null for invalid index", slide);
    }

    @Test
    public void testClear()
    {
        presentation.clear();
        assertEquals("Presentation size should be 0 after clear", 0, presentation.getSize());
    }

    @Test
    public void testObserverManagement()
    {
        // Create a mock observer
        PresentationObserver mockObserver = new PresentationObserver()
        {
            @Override
            public void onSlideChanged(int slideNumber)
            {
                // No implementation needed for this test
            }

            @Override
            public void onPresentationChanged()
            {
                // No implementation needed for this test
            }
        };

        // Test adding observer
        presentation.addObserver(mockObserver);

        try
        {
            // Get the observer manager field
            Field observerManagerField = Presentation.class.getDeclaredField("observerManager");
            observerManagerField.setAccessible(true);
            PresentationObserverManager manager = (PresentationObserverManager) observerManagerField.get(presentation);
            
            // Get the observers list from the manager
            Field observersField = PresentationObserverManager.class.getDeclaredField("observers");
            observersField.setAccessible(true);
            List<?> observers = (List<?>) observersField.get(manager);
            
            assertTrue("Observer should be in the list", observers.contains(mockObserver));

            // Remove observer
            presentation.removeObserver(mockObserver);
            assertFalse("Observer should not be in the list after removal", observers.contains(mockObserver));
        } catch (Exception e)
        {
            fail("Failed to access observers field: " + e.getMessage());
        }
    }

    @Test
    public void testSetTitle()
    {
        String newTitle = "New Presentation Title";
        presentation.setTitle(newTitle);
        assertEquals("Title should be updated", newTitle, presentation.getTitle());
    }

    @Test
    public void testSetSlideNumber()
    {
        // Add another slide
        Slide slide = new Slide();
        slide.setTitle("Test Slide 2");
        presentation.append(slide);

        // Set slide number to 1
        presentation.setSlideNumber(1);
        assertEquals("Slide number should be updated", 1, presentation.getSlideNumber());

        // Test with invalid slide number (negative)
        presentation.setSlideNumber(-1);
        assertEquals("Slide number should be set to 0 with invalid negative number", 0, presentation.getSlideNumber());

        // Set slide number back to 1 for the next test
        presentation.setSlideNumber(1);

        // Test with invalid slide number (too large)
        presentation.setSlideNumber(999);
        assertEquals("Slide number should be set to last slide with invalid large number", 1, presentation.getSlideNumber());
    }

    @Test
    public void testNextAndPrevSlide()
    {
        // Add another slide
        Slide slide = new Slide();
        slide.setTitle("Test Slide 2");
        presentation.append(slide);

        // Start with slide 0
        presentation.setSlideNumber(0);
        assertEquals("Current slide number should be 0", 0, presentation.getSlideNumber());

        // Go to next slide
        presentation.nextSlide();
        assertEquals("Current slide number should be 1", 1, presentation.getSlideNumber());

        // Try to go past the end
        presentation.nextSlide();
        assertEquals("Current slide number should still be 1", 1, presentation.getSlideNumber());

        // Go back to previous slide
        presentation.prevSlide();
        assertEquals("Current slide number should be 0", 0, presentation.getSlideNumber());

        // Try to go before the beginning
        presentation.prevSlide();
        assertEquals("Current slide number should still be 0", 0, presentation.getSlideNumber());
    }

    @Test
    public void testSetSlides()
    {
        // Create new slides list
        List<Slide> newSlides = new ArrayList<>();
        Slide slide1 = new Slide();
        slide1.setTitle("New Slide 1");
        Slide slide2 = new Slide();
        slide2.setTitle("New Slide 2");

        newSlides.add(slide1);
        newSlides.add(slide2);

        // Set the new slides
        presentation.setSlides(newSlides);

        // Check if slides were updated
        assertEquals("Presentation size should match new slides list size", 2, presentation.getSize());
        assertEquals("First slide title should match", "New Slide 1", presentation.getSlide(0).getTitle());
        assertEquals("Second slide title should match", "New Slide 2", presentation.getSlide(1).getTitle());
    }

    @Test
    public void testGetSlides()
    {
        List<Slide> slides = presentation.getSlides();
        assertNotNull("Slides list should not be null", slides);
        assertEquals("Slides list size should match presentation size", presentation.getSize(), slides.size());
    }

    @Test
    public void testGetCurrentSlideNumber()
    {
        assertEquals("Current slide number should be 0", 0, presentation.getCurrentSlideNumber());

        // Change slide number
        presentation.setSlideNumber(0);
        assertEquals("Current slide number should be updated", 0, presentation.getCurrentSlideNumber());
    }

    /**
     * Test the observer notification mechanism for slide changes
     */
    @Test
    public void testNotifySlideChanged()
    {
        // Create a mock observer that tracks notifications
        PresentationObserver observer = new PresentationObserver()
        {
            @Override
            public void onSlideChanged(int slideNumber)
            {
                notificationReceived = true;
                slideNumberNotified = slideNumber;
            }

            @Override
            public void onPresentationChanged()
            {
                presentationChangedNotified = true;
            }
        };

        // Add the observer
        presentation.addObserver(observer);

        // Change the slide number to trigger notification
        presentation.setSlideNumber(0);

        // Verify the observer was notified
        assertTrue("Observer should have been notified", notificationReceived);
        assertEquals("Correct slide number should be in notification", 0, slideNumberNotified);

        // Remove the observer and reset flags
        presentation.removeObserver(observer);
        notificationReceived = false;
        slideNumberNotified = -1;

        // Change slide again
        presentation.setSlideNumber(0);

        // Verify observer was not notified after removal
        assertFalse("Observer should not be notified after removal", notificationReceived);
    }

    /**
     * Test the observer notification mechanism for presentation changes
     */
    @Test
    public void testNotifyPresentationChanged()
    {
        // Create a mock observer that tracks notifications
        PresentationObserver observer = new PresentationObserver()
        {
            @Override
            public void onSlideChanged(int slideNumber)
            {
                notificationReceived = true;
                slideNumberNotified = slideNumber;
            }

            @Override
            public void onPresentationChanged()
            {
                presentationChangedNotified = true;
            }
        };

        // Add the observer
        presentation.addObserver(observer);

        // Clear the presentation to trigger notification
        presentation.clear();

        // Verify the observer was notified
        assertTrue("Observer should have been notified of presentation change", presentationChangedNotified);

        // Remove the observer and reset flags
        presentation.removeObserver(observer);
        presentationChangedNotified = false;

        // Set slides to trigger another notification
        presentation.setSlides(new ArrayList<>());

        // Verify observer was not notified after removal
        assertFalse("Observer should not be notified after removal", presentationChangedNotified);
    }

    /**
     * Test next and prev methods with empty presentation
     */
    @Test
    public void testNextAndPrevWithEmptyPresentation()
    {
        // Create empty presentation
        Presentation emptyPresentation = new Presentation();

        // Try to go to next slide on empty presentation
        emptyPresentation.nextSlide();
        assertEquals("Current slide should be -1 in empty presentation", -1, emptyPresentation.getCurrentSlideNumber());

        // Try to go to previous slide on empty presentation
        emptyPresentation.prevSlide();
        assertEquals("Current slide should be -1 in empty presentation", -1, emptyPresentation.getCurrentSlideNumber());
    }

    /**
     * Test getCurrentSlide with empty presentation
     */
    @Test
    public void testGetCurrentSlideWithEmptyPresentation()
    {
        // Create empty presentation
        Presentation emptyPresentation = new Presentation();

        // Try to get current slide
        Slide slide = emptyPresentation.getCurrentSlide();
        assertNull("Current slide should be null for empty presentation", slide);
    }

    /**
     * Test exit method
     */
    @Test
    public void testExit()
    {
        // Create a test presentation that overrides doExit
        TestPresentation testPresentation = new TestPresentation();

        // Call exit
        testPresentation.exit(42);

        // Verify doExit was called with the correct code
        assertTrue("doExit should have been called", testPresentation.exitCalled);
        assertEquals("Exit code should be passed to doExit", 42, testPresentation.exitCode);
    }

    /**
     * Test loader related methods 
     * Tests the deprecated methods have been properly marked as deprecated
     */
    @Test
    @SuppressWarnings("deprecation")
    public void testLoader()
    {
        // Skip the actual test since the methods now throw UnsupportedOperationException
        // This test just verifies the methods still exist but are deprecated

        // Ensure the methods exist and are deprecated
        assertTrue("loadPresentation method should be deprecated", 
            isMethodDeprecated(Presentation.class, "loadPresentation", String.class));
        assertTrue("savePresentation method should be deprecated", 
            isMethodDeprecated(Presentation.class, "savePresentation", String.class));
        assertTrue("setLoader method should be deprecated", 
            isMethodDeprecated(Presentation.class, "setLoader", com.jabberpoint.PresentationLoader.class));
        
        // Test with new interfaces directly
        MockPresentationLoader loader = new MockPresentationLoader();
        
        try {
            // Load directly with the loader
            loader.loadPresentation(presentation, "test.xml");
            
            // Verify loader was called
            assertTrue("Loader should have been called for loading", loader.loadCalled);
            assertEquals("Filename should be passed to loader", "test.xml", loader.loadedFile);
            
            // Save directly with the loader
            loader.savePresentation(presentation, "output.xml");
            
            // Verify loader was called
            assertTrue("Loader should have been called for saving", loader.saveCalled);
            assertEquals("Filename should be passed to loader", "output.xml", loader.savedFile);
        } catch (Exception e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }

    /**
     * Test that deprecated methods throw UnsupportedOperationException
     */
    @Test
    @SuppressWarnings("deprecation")
    public void testDeprecatedMethodsThrowException()
    {
        try {
            // Try to load - should throw UnsupportedOperationException
            try {
                presentation.loadPresentation("test.xml");
                fail("Should throw UnsupportedOperationException as this method is deprecated");
            } catch (UnsupportedOperationException e) {
                // Expected
                assertTrue(e.getMessage().contains("Use PresentationReader"));
            }
            
            // Try to save - should throw UnsupportedOperationException
            try {
                presentation.savePresentation("output.xml");
                fail("Should throw UnsupportedOperationException as this method is deprecated");
            } catch (UnsupportedOperationException e) {
                // Expected
                assertTrue(e.getMessage().contains("Use PresentationWriter"));
            }
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }
    
    /**
     * Helper method to check if a method is deprecated
     */
    private boolean isMethodDeprecated(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        try {
            java.lang.reflect.Method method = clazz.getMethod(methodName, parameterTypes);
            return method.isAnnotationPresent(Deprecated.class);
        } catch (NoSuchMethodException e) {
            fail("Method " + methodName + " not found in " + clazz.getName());
            return false;
        }
    }

    /**
     * Mock loader for testing loader-related methods
     */
    private class MockPresentationLoader implements PresentationLoader
    {
        public boolean loadCalled = false;
        public boolean saveCalled = false;
        public String loadedFile = null;
        public String savedFile = null;

        @Override
        public void loadPresentation(Presentation p, String filename) throws Exception
        {
            loadCalled = true;
            loadedFile = filename;
        }

        @Override
        public void savePresentation(Presentation p, String filename) throws Exception
        {
            saveCalled = true;
            savedFile = filename;
        }
    }

    /**
     * Test subclass for testing exit functionality
     */
    private class TestPresentation extends Presentation
    {
        public boolean exitCalled = false;
        public int exitCode = -1;

        @Override
        protected void doExit(int n)
        {
            exitCalled = true;
            exitCode = n;
            // Don't actually exit
        }
    }

    /**
     * Test setSlides with null parameter
     */
    @Test
    public void testSetSlidesWithNull()
    {
        // Set initial slides
        List<Slide> slides = new ArrayList<>();
        Slide slide = new Slide();
        slides.add(slide);
        presentation.setSlides(slides);

        // Now set null slides
        presentation.setSlides(null);

        // Verify an empty list was created instead of null
        assertNotNull("Slides should not be null after setting null", presentation.getSlides());
        assertEquals("Slides list should be empty after setting null", 0, presentation.getSize());
    }

    /**
     * Test setting ShowView
     */
    @Test
    public void testSetShowView()
    {
        // Create a mock SlideViewerComponent
        SlideViewerComponent mockComponent = new SlideViewerComponent(presentation);

        // Set the component
        presentation.setShowView(mockComponent);

        // Verify component was set using reflection
        try
        {
            Field field = Presentation.class.getDeclaredField("slideViewComponent");
            field.setAccessible(true);
            Object value = field.get(presentation);

            assertSame("SlideViewerComponent should be stored correctly", mockComponent, value);
        } catch (Exception e)
        {
            fail("Exception accessing field: " + e.getMessage());
        }
    }
} 