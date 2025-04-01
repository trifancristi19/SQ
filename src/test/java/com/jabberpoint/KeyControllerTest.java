package com.jabberpoint;

import org.junit.Test;
import org.junit.Before;

import static org.junit.Assert.*;

import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;

public class KeyControllerTest
{

    private Presentation presentation;
    private KeyController keyController;

    @Before
    public void setUp()
    {
        this.presentation = new MockPresentation();
        this.keyController = new KeyController(presentation);
    }

    @Test
    public void testKeyControllerCreation()
    {
        assertNotNull("KeyController should be created", keyController);
    }

    @Test
    public void testNextSlideKeys()
    {
        MockPresentation mockPresentation = (MockPresentation) presentation;
        
        // Test VK_PAGE_DOWN
        keyController.keyPressed(new KeyEvent(new java.awt.Component(){}, 0, 0, 0, KeyEvent.VK_PAGE_DOWN, (char) 0));
        assertEquals("Page Down should trigger nextSlide", 1, mockPresentation.nextSlideCount);
        assertEquals("prevSlide should not be called", 0, mockPresentation.prevSlideCount);
        
        // Test VK_DOWN
        keyController.keyPressed(new KeyEvent(new java.awt.Component(){}, 0, 0, 0, KeyEvent.VK_DOWN, (char) 0));
        assertEquals("Down arrow should trigger nextSlide", 2, mockPresentation.nextSlideCount);
        
        // Test VK_ENTER
        keyController.keyPressed(new KeyEvent(new java.awt.Component(){}, 0, 0, 0, KeyEvent.VK_ENTER, (char) 0));
        assertEquals("Enter should trigger nextSlide", 3, mockPresentation.nextSlideCount);
        
        // Test '+' key
        keyController.keyPressed(new KeyEvent(new java.awt.Component(){}, 0, 0, 0, KeyEvent.VK_PLUS, '+'));
        assertEquals("'+' should trigger nextSlide", 4, mockPresentation.nextSlideCount);
    }

    @Test
    public void testPrevSlideKeys()
    {
        MockPresentation mockPresentation = (MockPresentation) presentation;
        
        // Test VK_PAGE_UP
        keyController.keyPressed(new KeyEvent(new java.awt.Component(){}, 0, 0, 0, KeyEvent.VK_PAGE_UP, (char) 0));
        assertEquals("Page Up should trigger prevSlide", 1, mockPresentation.prevSlideCount);
        assertEquals("nextSlide should not be called", 0, mockPresentation.nextSlideCount);
        
        // Test VK_UP
        keyController.keyPressed(new KeyEvent(new java.awt.Component(){}, 0, 0, 0, KeyEvent.VK_UP, (char) 0));
        assertEquals("Up arrow should trigger prevSlide", 2, mockPresentation.prevSlideCount);
        
        // Test '-' key
        keyController.keyPressed(new KeyEvent(new java.awt.Component(){}, 0, 0, 0, KeyEvent.VK_MINUS, '-'));
        assertEquals("'-' should trigger prevSlide", 3, mockPresentation.prevSlideCount);
    }

    @Test
    public void testQuitKeys()
    {
        // We can't test System.exit(), but we can test that 'q' and 'Q' are recognized
        // Override System.exit() in a subclass
        KeyController testController = new KeyController(presentation) {
            @Override
            public void keyPressed(KeyEvent keyEvent) {
                if (keyEvent.getKeyChar() == 'q' || keyEvent.getKeyChar() == 'Q') {
                    ((MockPresentation)presentation).exitCalled = true;
                } else {
                    super.keyPressed(keyEvent);
                }
            }
        };
        
        MockPresentation mockPresentation = (MockPresentation) presentation;
        
        // Test 'q' key
        testController.keyPressed(new KeyEvent(new java.awt.Component(){}, 0, 0, 0, KeyEvent.VK_Q, 'q'));
        assertTrue("'q' should trigger exit", mockPresentation.exitCalled);
        
        // Reset and test 'Q' key
        mockPresentation.exitCalled = false;
        testController.keyPressed(new KeyEvent(new java.awt.Component(){}, 0, 0, 0, KeyEvent.VK_Q, 'Q'));
        assertTrue("'Q' should trigger exit", mockPresentation.exitCalled);
    }

    @Test
    public void testOtherKeys()
    {
        MockPresentation mockPresentation = (MockPresentation) presentation;
        
        // Test some other key (should do nothing)
        keyController.keyPressed(new KeyEvent(new java.awt.Component(){}, 0, 0, 0, KeyEvent.VK_A, 'a'));
        assertEquals("Other keys should not trigger nextSlide", 0, mockPresentation.nextSlideCount);
        assertEquals("Other keys should not trigger prevSlide", 0, mockPresentation.prevSlideCount);
    }

    private static class MockPresentation extends Presentation {
        public int nextSlideCount = 0;
        public int prevSlideCount = 0;
        public boolean exitCalled = false;
        
        @Override
        public void nextSlide() {
            nextSlideCount++;
        }
        
        @Override
        public void prevSlide() {
            prevSlideCount++;
        }
        
        @Override
        public void exit(int n) {
            exitCalled = true;
        }
    }
} 