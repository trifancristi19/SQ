package com.jabberpoint;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;

public class KeyControllerTest {
    
    private Presentation presentation;
    private KeyController keyController;
    
    @Before
    public void setUp() {
        presentation = new Presentation();
        keyController = new KeyController(presentation);
    }
    
    @Test
    public void testKeyControllerCreation() {
        assertNotNull("KeyController should be created", keyController);
    }
    
    @Test
    public void testNextSlideKeyPresses() {
        // Create a presentation with multiple slides
        Slide slide1 = new Slide();
        Slide slide2 = new Slide();
        presentation.append(slide1);
        presentation.append(slide2);
        presentation.setSlideNumber(0);
        
        // Test Page Down key
        KeyEvent pageDownEvent = new KeyEvent(new java.awt.Component(){}, 
                                             KeyEvent.KEY_PRESSED, 
                                             System.currentTimeMillis(), 
                                             0, 
                                             KeyEvent.VK_PAGE_DOWN,
                                             KeyEvent.CHAR_UNDEFINED);
        keyController.keyPressed(pageDownEvent);
        assertEquals("Page Down should move to next slide", 1, presentation.getSlideNumber());
        
        // Reset position
        presentation.setSlideNumber(0);
        
        // Test Down arrow key
        KeyEvent downArrowEvent = new KeyEvent(new java.awt.Component(){}, 
                                             KeyEvent.KEY_PRESSED, 
                                             System.currentTimeMillis(), 
                                             0, 
                                             KeyEvent.VK_DOWN,
                                             KeyEvent.CHAR_UNDEFINED);
        keyController.keyPressed(downArrowEvent);
        assertEquals("Down arrow should move to next slide", 1, presentation.getSlideNumber());
        
        // Reset position
        presentation.setSlideNumber(0);
        
        // Test Enter key
        KeyEvent enterEvent = new KeyEvent(new java.awt.Component(){}, 
                                         KeyEvent.KEY_PRESSED, 
                                         System.currentTimeMillis(), 
                                         0, 
                                         KeyEvent.VK_ENTER,
                                         KeyEvent.CHAR_UNDEFINED);
        keyController.keyPressed(enterEvent);
        assertEquals("Enter should move to next slide", 1, presentation.getSlideNumber());
    }
    
    @Test
    public void testPreviousSlideKeyPresses() {
        // Create a presentation with multiple slides
        Slide slide1 = new Slide();
        Slide slide2 = new Slide();
        presentation.append(slide1);
        presentation.append(slide2);
        presentation.setSlideNumber(1);
        
        // Test Page Up key
        KeyEvent pageUpEvent = new KeyEvent(new java.awt.Component(){}, 
                                           KeyEvent.KEY_PRESSED, 
                                           System.currentTimeMillis(), 
                                           0, 
                                           KeyEvent.VK_PAGE_UP,
                                           KeyEvent.CHAR_UNDEFINED);
        keyController.keyPressed(pageUpEvent);
        assertEquals("Page Up should move to previous slide", 0, presentation.getSlideNumber());
        
        // Reset position
        presentation.setSlideNumber(1);
        
        // Test Up arrow key
        KeyEvent upArrowEvent = new KeyEvent(new java.awt.Component(){}, 
                                           KeyEvent.KEY_PRESSED, 
                                           System.currentTimeMillis(), 
                                           0, 
                                           KeyEvent.VK_UP,
                                           KeyEvent.CHAR_UNDEFINED);
        keyController.keyPressed(upArrowEvent);
        assertEquals("Up arrow should move to previous slide", 0, presentation.getSlideNumber());
    }
    
    @Test
    public void testUnknownKey() {
        // Create a presentation with multiple slides
        Slide slide1 = new Slide();
        Slide slide2 = new Slide();
        presentation.append(slide1);
        presentation.append(slide2);
        presentation.setSlideNumber(0);
        
        // Test an unhandled key
        KeyEvent unknownEvent = new KeyEvent(new java.awt.Component(){}, 
                                           KeyEvent.KEY_PRESSED, 
                                           System.currentTimeMillis(), 
                                           0, 
                                           KeyEvent.VK_F1,
                                           KeyEvent.CHAR_UNDEFINED);
        keyController.keyPressed(unknownEvent);
        
        // Slide number should remain the same
        assertEquals("Unknown key should not change slide", 0, presentation.getSlideNumber());
    }
} 