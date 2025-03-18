package com.jabberpoint;

import org.junit.Test;
import org.junit.BeforeClass;
import static org.junit.Assert.*;
import java.awt.MenuBar;
import java.awt.Dimension;

public class SlideViewerFrameTest {
    
    @BeforeClass
    public static void setUpClass() {
        // Initialize any required static classes
        Style.createStyles();
    }
    
    @Test
    public void testFrameCreation() {
        Presentation presentation = new Presentation();
        SlideViewerFrame frame = new SlideViewerFrame("Test Frame", presentation);
        
        try {
            assertNotNull("Frame should be created", frame);
            assertNotNull("Frame title should be set", frame.getTitle());
            assertFalse("Frame title should not be empty", frame.getTitle().isEmpty());
            assertEquals("Frame size width should match", SlideViewerFrame.WIDTH, frame.getSize().width);
            assertEquals("Frame size height should match", SlideViewerFrame.HEIGHT, frame.getSize().height);
        } finally {
            // Clean up the frame
            frame.dispose();
        }
    }
    
    @Test
    public void testComponentSetup() {
        Presentation presentation = new Presentation();
        SlideViewerFrame frame = new SlideViewerFrame("Test Frame", presentation);
        
        try {
            // Check that the frame has a MenuBar
            MenuBar menuBar = frame.getMenuBar();
            assertNotNull("Frame should have a MenuBar", menuBar);
            assertTrue("MenuBar should be a MenuController", menuBar instanceof MenuController);
            
            // Check that the content pane has a component
            assertTrue("Frame should have a component", frame.getContentPane().getComponentCount() > 0);
            assertNotNull("Frame should have a SlideViewerComponent", 
                         frame.getContentPane().getComponent(0));
            assertTrue("Component should be a SlideViewerComponent", 
                     frame.getContentPane().getComponent(0) instanceof SlideViewerComponent);
        } finally {
            // Clean up the frame
            frame.dispose();
        }
    }
    
    @Test
    public void testKeyListenerSetup() {
        Presentation presentation = new Presentation();
        SlideViewerFrame frame = new SlideViewerFrame("Test Frame", presentation);
        
        try {
            // Check that the frame has at least one KeyListener
            assertTrue("Frame should have KeyListeners", frame.getKeyListeners().length > 0);
            
            // Check that one of the KeyListeners is a KeyController
            boolean hasKeyController = false;
            for (java.awt.event.KeyListener listener : frame.getKeyListeners()) {
                if (listener instanceof KeyController) {
                    hasKeyController = true;
                    break;
                }
            }
            assertTrue("Frame should have a KeyController", hasKeyController);
        } finally {
            // Clean up the frame
            frame.dispose();
        }
    }
} 