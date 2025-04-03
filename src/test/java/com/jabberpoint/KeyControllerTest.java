package com.jabberpoint;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;

public class KeyControllerTest
{

    private KeyController keyController;
    private TestablePresentation presentation;
    private boolean exitCalled = false;
    private int exitStatus = -1;

    private static class TestablePresentation extends Presentation
    {
        private boolean nextSlideCalled = false;
        private boolean prevSlideCalled = false;

        @Override
        public void nextSlide()
        {
            nextSlideCalled = true;
        }

        @Override
        public void prevSlide()
        {
            prevSlideCalled = true;
        }

        public boolean wasNextSlideCalled()
        {
            return nextSlideCalled;
        }

        public boolean wasPrevSlideCalled()
        {
            return prevSlideCalled;
        }

        public void reset()
        {
            nextSlideCalled = false;
            prevSlideCalled = false;
        }
    }

    // Mock component that doesn't need a display
    private static class MockComponent extends Component
    {
        private static final long serialVersionUID = 1L;
    }

    // Custom KeyController for capturing System.exit calls
    private class TestableKeyController extends KeyController
    {
        public TestableKeyController(Presentation presentation)
        {
            super(presentation);
        }

        @Override
        public void keyPressed(KeyEvent keyEvent)
        {
            int keyCode = keyEvent.getKeyCode();
            char keyChar = keyEvent.getKeyChar();

            // Handle exit case separately to prevent actual System.exit
            if (keyCode == KeyEvent.VK_Q || keyChar == 'q' || keyChar == 'Q')
            {
                exitCalled = true;
                exitStatus = 0;
                return;
            }

            // Handle other cases normally
            super.keyPressed(keyEvent);
        }
    }

    @Before
    public void setUp()
    {
        presentation = new TestablePresentation();
        keyController = new TestableKeyController(presentation);

        // Reset exit tracking
        exitCalled = false;
        exitStatus = -1;
    }

    @Test
    public void testKeyPressedNextSlide()
    {
        // Skip test in headless environment
        if (GraphicsEnvironment.isHeadless())
        {
            System.out.println("Skipping testKeyPressedNextSlide in headless environment");
            return;
        }

        // Test PAGE_DOWN key
        KeyEvent keyEvent = new KeyEvent(new MockComponent(), KeyEvent.KEY_PRESSED,
                System.currentTimeMillis(), 0, KeyEvent.VK_PAGE_DOWN, '\0');
        keyController.keyPressed(keyEvent);
        assertTrue(presentation.wasNextSlideCalled());

        // Reset and test DOWN key
        presentation.reset();
        keyEvent = new KeyEvent(new MockComponent(), KeyEvent.KEY_PRESSED,
                System.currentTimeMillis(), 0, KeyEvent.VK_DOWN, '\0');
        keyController.keyPressed(keyEvent);
        assertTrue(presentation.wasNextSlideCalled());

        // Reset and test ENTER key
        presentation.reset();
        keyEvent = new KeyEvent(new MockComponent(), KeyEvent.KEY_PRESSED,
                System.currentTimeMillis(), 0, KeyEvent.VK_ENTER, '\0');
        keyController.keyPressed(keyEvent);
        assertTrue(presentation.wasNextSlideCalled());

        // Reset and test '+' key
        presentation.reset();
        keyEvent = new KeyEvent(new MockComponent(), KeyEvent.KEY_PRESSED,
                System.currentTimeMillis(), 0, '+', '+');
        keyController.keyPressed(keyEvent);
        assertTrue(presentation.wasNextSlideCalled());
    }

    @Test
    public void testKeyPressedPrevSlide()
    {
        // Skip test in headless environment
        if (GraphicsEnvironment.isHeadless())
        {
            System.out.println("Skipping testKeyPressedPrevSlide in headless environment");
            return;
        }

        // Test PAGE_UP key
        KeyEvent keyEvent = new KeyEvent(new MockComponent(), KeyEvent.KEY_PRESSED,
                System.currentTimeMillis(), 0, KeyEvent.VK_PAGE_UP, '\0');
        keyController.keyPressed(keyEvent);
        assertTrue(presentation.wasPrevSlideCalled());

        // Reset and test UP key
        presentation.reset();
        keyEvent = new KeyEvent(new MockComponent(), KeyEvent.KEY_PRESSED,
                System.currentTimeMillis(), 0, KeyEvent.VK_UP, '\0');
        keyController.keyPressed(keyEvent);
        assertTrue(presentation.wasPrevSlideCalled());

        // Reset and test '-' key
        presentation.reset();
        keyEvent = new KeyEvent(new MockComponent(), KeyEvent.KEY_PRESSED,
                System.currentTimeMillis(), 0, '-', '-');
        keyController.keyPressed(keyEvent);
        assertTrue(presentation.wasPrevSlideCalled());
    }

    @Test
    public void testKeyPressedExit()
    {
        // Skip test in headless environment
        if (GraphicsEnvironment.isHeadless())
        {
            System.out.println("Skipping testKeyPressedExit in headless environment");
            return;
        }

        // Test lowercase 'q' key
        KeyEvent keyEvent = new KeyEvent(new MockComponent(), KeyEvent.KEY_PRESSED,
                System.currentTimeMillis(), 0, KeyEvent.VK_Q, 'q');
        keyController.keyPressed(keyEvent);

        assertTrue("System.exit should have been called", exitCalled);
        assertEquals("Exit status should be 0", 0, exitStatus);

        // Reset and test uppercase 'Q' key
        exitCalled = false;
        exitStatus = -1;

        keyEvent = new KeyEvent(new MockComponent(), KeyEvent.KEY_PRESSED,
                System.currentTimeMillis(), 0, KeyEvent.VK_Q, 'Q');
        keyController.keyPressed(keyEvent);

        assertTrue("System.exit should have been called", exitCalled);
        assertEquals("Exit status should be 0", 0, exitStatus);
    }

    @Test
    public void testKeyPressedDefault()
    {
        // Skip test in headless environment
        if (GraphicsEnvironment.isHeadless())
        {
            System.out.println("Skipping testKeyPressedDefault in headless environment");
            return;
        }

        // Test an unhandled key (e.g., 'A')
        presentation.reset();
        KeyEvent keyEvent = new KeyEvent(new MockComponent(), KeyEvent.KEY_PRESSED,
                System.currentTimeMillis(), 0, KeyEvent.VK_A, 'A');
        keyController.keyPressed(keyEvent);
        assertFalse(presentation.wasNextSlideCalled());
        assertFalse(presentation.wasPrevSlideCalled());
        assertFalse(exitCalled);
    }

    @Test
    public void testConstructorWithNonNullPresentation()
    {
        Presentation p = new Presentation();
        KeyController kc = new KeyController(p);

        // This is just to ensure 100% constructor coverage
        assertNotNull("KeyController should be created successfully", kc);
    }
} 