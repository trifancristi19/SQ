package com.jabberpoint;

import org.junit.Test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

public class PresentationTest
{

    @Test
    public void testPresentationCreation()
    {
        Presentation presentation = new Presentation();
        assertNotNull("Presentation should be created", presentation);
        assertEquals("New presentation should have 0 slides", 0, presentation.getSize());
    }

    @Test
    public void testSetTitle()
    {
        Presentation presentation = new Presentation();
        String testTitle = "Test Presentation";
        presentation.setTitle(testTitle);
        assertEquals("Title should be set correctly", testTitle, presentation.getTitle());
    }

    @Test
    public void testAppendSlide()
    {
        Presentation presentation = new Presentation();
        Slide slide = new Slide();
        presentation.append(slide);
        assertEquals("Presentation should have 1 slide", 1, presentation.getSize());
        assertSame("getSlide should return the appended slide", slide, presentation.getSlide(0));
    }

    @Test
    public void testSlideNavigation()
    {
        Presentation presentation = new Presentation();
        Slide slide1 = new Slide();
        Slide slide2 = new Slide();
        Slide slide3 = new Slide();

        presentation.append(slide1);
        presentation.append(slide2);
        presentation.append(slide3);

        presentation.setSlideNumber(0);
        assertEquals("Current slide should be first slide", 0, presentation.getSlideNumber());
        assertSame("getCurrentSlide should return first slide", slide1, presentation.getCurrentSlide());

        presentation.nextSlide();
        assertEquals("Current slide should be second slide", 1, presentation.getSlideNumber());
        assertSame("getCurrentSlide should return second slide", slide2, presentation.getCurrentSlide());

        presentation.prevSlide();
        assertEquals("Current slide should be first slide again", 0, presentation.getSlideNumber());
        assertSame("getCurrentSlide should return first slide again", slide1, presentation.getCurrentSlide());
    }

    @Test
    public void testNavigationBoundary()
    {
        Presentation presentation = new Presentation();
        Slide slide1 = new Slide();
        presentation.append(slide1);

        presentation.setSlideNumber(0);
        presentation.prevSlide(); // Try to go before first slide
        assertEquals("Should not go before first slide", 0, presentation.getSlideNumber());

        presentation.nextSlide(); // Try to go past last slide
        assertEquals("Should not go past last slide", 0, presentation.getSlideNumber());
    }

    @Test
    public void testClear()
    {
        Presentation presentation = new Presentation();
        Slide slide1 = new Slide();
        Slide slide2 = new Slide();

        presentation.append(slide1);
        presentation.append(slide2);
        presentation.setSlideNumber(1);

        presentation.clear();
        assertEquals("Presentation should have 0 slides after clear", 0, presentation.getSize());
        assertEquals("Slide number should be reset after clear", -1, presentation.getSlideNumber());
    }

    @Test
    public void testGetInvalidSlide()
    {
        Presentation presentation = new Presentation();
        assertNull("Getting negative slide number should return null", presentation.getSlide(-1));
        assertNull("Getting out of bounds slide should return null", presentation.getSlide(0));

        presentation.append(new Slide());
        assertNull("Getting out of bounds slide should return null", presentation.getSlide(1));
    }

    @Test
    public void testSetInvalidSlideNumber() {
        Presentation presentation = new Presentation();
        presentation.append(new Slide());
        presentation.append(new Slide());
        
        // Try to set invalid slide numbers
        presentation.setSlideNumber(-1);
        assertEquals("Should not set negative slide number", 0, presentation.getSlideNumber());
        
        presentation.setSlideNumber(2);
        assertEquals("Should not set slide number beyond bounds", 1, presentation.getSlideNumber());
    }

    @Test
    public void testEmptyPresentationNavigation() {
        Presentation presentation = new Presentation();
        
        // Test navigation on empty presentation
        presentation.nextSlide();
        assertEquals("Should not navigate on empty presentation", -1, presentation.getSlideNumber());
        
        presentation.prevSlide();
        assertEquals("Should not navigate on empty presentation", -1, presentation.getSlideNumber());
        
        assertNull("Should return null for current slide on empty presentation", 
                  presentation.getCurrentSlide());
    }

    @Test
    public void testMultipleSlidesNavigation() {
        Presentation presentation = new Presentation();
        Slide slide1 = new Slide();
        Slide slide2 = new Slide();
        Slide slide3 = new Slide();
        
        presentation.append(slide1);
        presentation.append(slide2);
        presentation.append(slide3);
        
        // Test forward navigation
        presentation.setSlideNumber(0);
        presentation.nextSlide();
        presentation.nextSlide();
        assertEquals("Should reach last slide", 2, presentation.getSlideNumber());
        
        // Test backward navigation
        presentation.prevSlide();
        presentation.prevSlide();
        assertEquals("Should reach first slide", 0, presentation.getSlideNumber());
    }

    @Test
    public void testSlideNumberAfterClear() {
        Presentation presentation = new Presentation();
        presentation.append(new Slide());
        presentation.append(new Slide());
        presentation.setSlideNumber(1);
        
        presentation.clear();
        assertEquals("Slide number should be -1 after clear", -1, presentation.getSlideNumber());
        assertNull("Current slide should be null after clear", presentation.getCurrentSlide());
    }

    @Test
    public void testSlideNumberAfterAppend() {
        Presentation presentation = new Presentation();
        presentation.append(new Slide());
        presentation.setSlideNumber(0);
        
        presentation.append(new Slide());
        assertEquals("Slide number should remain unchanged after append", 0, presentation.getSlideNumber());
    }

    @Test
    public void testObserverPattern() {
        Presentation presentation = new Presentation();
        TestPresentationObserver observer = new TestPresentationObserver();
        presentation.addObserver(observer);
        
        // Test slide change notification
        presentation.append(new Slide());
        presentation.setSlideNumber(0);
        assertTrue("Observer should be notified of slide change", observer.wasSlideChanged());
        assertEquals("Observer should receive correct slide number", 0, observer.getLastSlideNumber());
        
        // Test presentation change notification
        presentation.clear();
        assertTrue("Observer should be notified of presentation change", observer.wasPresentationChanged());
        
        // Test observer removal
        presentation.removeObserver(observer);
        observer.reset();
        presentation.setSlideNumber(0);
        assertFalse("Observer should not be notified after removal", observer.wasSlideChanged());
    }

    @Test
    public void testPresentationLoader() throws Exception {
        Presentation presentation = new Presentation();
        TestPresentationLoader loader = new TestPresentationLoader();
        presentation.setLoader(loader);
        
        // Test loading presentation
        presentation.loadPresentation("test.xml");
        assertTrue("Loader should be called for loading", loader.wasLoadCalled());
        
        // Test saving presentation
        presentation.savePresentation("test.xml");
        assertTrue("Loader should be called for saving", loader.wasSaveCalled());
    }

    @Test(expected = IllegalStateException.class)
    public void testLoadPresentationWithoutLoader() throws Exception {
        Presentation presentation = new Presentation();
        presentation.loadPresentation("test.xml");
    }

    @Test(expected = IllegalStateException.class)
    public void testSavePresentationWithoutLoader() throws Exception {
        Presentation presentation = new Presentation();
        presentation.savePresentation("test.xml");
    }

    @Test
    public void testSetSlides() {
        Presentation presentation = new Presentation();
        TestPresentationObserver observer = new TestPresentationObserver();
        presentation.addObserver(observer);
        
        List<Slide> newSlides = new ArrayList<>();
        newSlides.add(new Slide());
        newSlides.add(new Slide());
        
        presentation.setSlides(newSlides);
        assertEquals("Should have correct number of slides", 2, presentation.getSize());
        assertTrue("Observer should be notified of presentation change", observer.wasPresentationChanged());
    }

    @Test
    public void testSetNullSlides() {
        Presentation presentation = new Presentation();
        presentation.append(new Slide());
        
        presentation.setSlides(null);
        assertEquals("Should have empty slides list after setting null", 0, presentation.getSize());
    }

    @Test
    public void testSetShowView() {
        Presentation presentation = new Presentation();
        SlideViewerComponent viewer = new SlideViewerComponent(presentation);
        presentation.setShowView(viewer);
        // No direct way to test this as it's just a setter, but we can verify it doesn't throw
    }

    @Test
    public void testExit() {
        Presentation presentation = new Presentation();
        // Note: We can't actually test System.exit() as it would terminate the JVM
        // We just verify the method exists and can be called
        presentation.exit(0);
    }

    @Test
    public void testGetCurrentSlideNumber() {
        Presentation presentation = new Presentation();
        assertEquals("Initial slide number should be 0", 0, presentation.getCurrentSlideNumber());
        
        presentation.append(new Slide());
        presentation.append(new Slide());
        presentation.setSlideNumber(1);
        assertEquals("Current slide number should be 1", 1, presentation.getCurrentSlideNumber());
        
        presentation.clear();
        assertEquals("Current slide number should be -1 after clear", -1, presentation.getCurrentSlideNumber());
    }

    @Test
    public void testEdgeCaseNavigation() {
        Presentation presentation = new Presentation();
        
        // Test navigation with empty presentation
        presentation.nextSlide();
        assertEquals("Next slide on empty presentation should set number to -1", -1, presentation.getSlideNumber());
        
        presentation.prevSlide();
        assertEquals("Prev slide on empty presentation should set number to -1", -1, presentation.getSlideNumber());
        
        // Add a slide and test boundary conditions
        presentation.append(new Slide());
        presentation.setSlideNumber(0);
        
        presentation.nextSlide();
        assertEquals("Next slide at end should not change number", 0, presentation.getSlideNumber());
        
        presentation.prevSlide();
        assertEquals("Prev slide at start should not change number", 0, presentation.getSlideNumber());
    }

    @Test
    public void testMultipleObservers() {
        Presentation presentation = new Presentation();
        TestPresentationObserver observer1 = new TestPresentationObserver();
        TestPresentationObserver observer2 = new TestPresentationObserver();
        
        presentation.addObserver(observer1);
        presentation.addObserver(observer2);
        
        // Test that both observers are notified
        presentation.append(new Slide());
        presentation.setSlideNumber(0);
        
        assertTrue("First observer should be notified", observer1.wasSlideChanged());
        assertTrue("Second observer should be notified", observer2.wasSlideChanged());
        
        // Remove one observer and verify only the other is notified
        presentation.removeObserver(observer1);
        observer1.reset();
        observer2.reset();
        
        presentation.setSlideNumber(0);
        assertFalse("Removed observer should not be notified", observer1.wasSlideChanged());
        assertTrue("Remaining observer should be notified", observer2.wasSlideChanged());
    }

    // Helper class for testing observer pattern
    private class TestPresentationObserver implements PresentationObserver {
        private boolean slideChanged = false;
        private boolean presentationChanged = false;
        private int lastSlideNumber = -1;

        @Override
        public void onSlideChanged(int slideNumber) {
            slideChanged = true;
            lastSlideNumber = slideNumber;
        }

        @Override
        public void onPresentationChanged() {
            presentationChanged = true;
        }

        public boolean wasSlideChanged() {
            return slideChanged;
        }

        public boolean wasPresentationChanged() {
            return presentationChanged;
        }

        public int getLastSlideNumber() {
            return lastSlideNumber;
        }

        public void reset() {
            slideChanged = false;
            presentationChanged = false;
            lastSlideNumber = -1;
        }
    }

    // Helper class for testing presentation loader
    private class TestPresentationLoader implements PresentationLoader {
        private boolean loadCalled = false;
        private boolean saveCalled = false;

        @Override
        public void loadPresentation(Presentation presentation, String fileName) throws Exception {
            loadCalled = true;
        }

        @Override
        public void savePresentation(Presentation presentation, String fileName) throws Exception {
            saveCalled = true;
        }

        public boolean wasLoadCalled() {
            return loadCalled;
        }

        public boolean wasSaveCalled() {
            return saveCalled;
        }
    }
} 