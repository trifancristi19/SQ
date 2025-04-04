package com.jabberpoint;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for the PresentationObserverManager class
 */
public class PresentationObserverManagerTest {

    private PresentationObserverManager manager;
    private TestPresentationObserver observer;

    private static class TestPresentationObserver implements PresentationObserver {
        private boolean slideChangedCalled = false;
        private boolean presentationChangedCalled = false;
        private int lastSlideNumber = -1;

        @Override
        public void onSlideChanged(int slideNumber) {
            slideChangedCalled = true;
            lastSlideNumber = slideNumber;
        }

        @Override
        public void onPresentationChanged() {
            presentationChangedCalled = true;
        }

        public void reset() {
            slideChangedCalled = false;
            presentationChangedCalled = false;
            lastSlideNumber = -1;
        }

        public boolean wasSlideChangedCalled() {
            return slideChangedCalled;
        }

        public boolean wasPresentationChangedCalled() {
            return presentationChangedCalled;
        }

        public int getLastSlideNumber() {
            return lastSlideNumber;
        }
    }

    @Before
    public void setUp() {
        manager = new PresentationObserverManager();
        observer = new TestPresentationObserver();
    }

    @Test
    public void testAddObserver() {
        // Add observer
        manager.addObserver(observer);

        // Notify slide changed
        manager.notifySlideChanged(5);

        // Verify observer was notified
        assertTrue("Observer should be notified of slide change", observer.wasSlideChangedCalled());
        assertEquals("Slide number should be passed correctly", 5, observer.getLastSlideNumber());
    }

    @Test
    public void testRemoveObserver() {
        // Add observer
        manager.addObserver(observer);

        // Remove observer
        manager.removeObserver(observer);

        // Notify slide changed
        manager.notifySlideChanged(5);

        // Verify observer was NOT notified
        assertFalse("Observer should not be notified after removal", observer.wasSlideChangedCalled());
    }

    @Test
    public void testNotifySlideChanged() {
        // Add observer
        manager.addObserver(observer);

        // Notify slide changed
        manager.notifySlideChanged(10);

        // Verify observer was notified
        assertTrue("Observer should be notified of slide change", observer.wasSlideChangedCalled());
        assertEquals("Slide number should be passed correctly", 10, observer.getLastSlideNumber());

        // Reset and test with different slide number
        observer.reset();
        manager.notifySlideChanged(20);
        assertEquals("Slide number should be updated", 20, observer.getLastSlideNumber());
    }

    @Test
    public void testNotifyPresentationChanged() {
        // Add observer
        manager.addObserver(observer);

        // Notify presentation changed
        manager.notifyPresentationChanged();

        // Verify observer was notified
        assertTrue("Observer should be notified of presentation change", observer.wasPresentationChangedCalled());
        assertFalse("Slide changed should not be called", observer.wasSlideChangedCalled());
    }

    @Test
    public void testAddNullObserver() {
        // Add null observer - should not throw exception
        manager.addObserver(null);

        // Notify changes - should not throw exception
        manager.notifySlideChanged(1);
        manager.notifyPresentationChanged();
    }

    @Test
    public void testMultipleObservers() {
        // Create second observer
        TestPresentationObserver observer2 = new TestPresentationObserver();

        // Add both observers
        manager.addObserver(observer);
        manager.addObserver(observer2);

        // Notify slide changed
        manager.notifySlideChanged(5);

        // Verify both observers were notified
        assertTrue("First observer should be notified", observer.wasSlideChangedCalled());
        assertTrue("Second observer should be notified", observer2.wasSlideChangedCalled());
        assertEquals("First observer should receive correct slide number", 5, observer.getLastSlideNumber());
        assertEquals("Second observer should receive correct slide number", 5, observer2.getLastSlideNumber());
    }
} 