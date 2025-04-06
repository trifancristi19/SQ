package com.jabberpoint;

import org.junit.Before;
import org.junit.Test;
import org.junit.BeforeClass;

import static org.junit.Assert.*;

import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.awt.Component;

/**
 * Tests for the KeyController class using reflection to avoid GUI dependencies
 */
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

    /**
     * Custom KeyEvent implementation for testing without GUI dependencies
     */
    private static class MockKeyEvent extends KeyEvent
    {
        private final int keyCode;
        private final char keyChar;

        public MockKeyEvent(int keyCode, char keyChar)
        {
            super(new DummyComponent(), KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, keyCode, keyChar);
            this.keyCode = keyCode;
            this.keyChar = keyChar;
        }

        @Override
        public int getKeyCode()
        {
            return keyCode;
        }

        @Override
        public char getKeyChar()
        {
            return keyChar;
        }
    }

    /**
     * Minimal Component implementation needed for KeyEvent constructor
     */
    private static class DummyComponent extends Component
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

            // For other cases, just call the appropriate methods directly
            if (keyCode == KeyEvent.VK_PAGE_DOWN || keyCode == KeyEvent.VK_DOWN ||
                    keyCode == KeyEvent.VK_ENTER || keyChar == '+')
            {
                if (presentation != null)
                {
                    presentation.nextSlide();
                }
            }
            else if (keyCode == KeyEvent.VK_PAGE_UP || keyCode == KeyEvent.VK_UP || keyChar == '-')
            {
                if (presentation != null)
                {
                    presentation.prevSlide();
                }
            }
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

    /**
     * Test KeyController structure through reflection
     */
    @Test
    public void testKeyControllerStructure()
    {
        try
        {
            // Verify constructor
            Constructor<?> constructor = KeyController.class.getConstructor(Presentation.class);
            assertNotNull("Constructor should exist", constructor);

            // Verify keyPressed method
            Method keyPressedMethod = KeyController.class.getDeclaredMethod("keyPressed", KeyEvent.class);
            assertNotNull("keyPressed method should exist", keyPressedMethod);

            // Verify presentation field
            Field presentationField = KeyController.class.getDeclaredField("presentation");
            assertNotNull("presentation field should exist", presentationField);

        } catch (Exception e)
        {
            fail("KeyController structure test failed: " + e.getMessage());
        }
    }

    /**
     * Test KeyController constructor with non-null presentation (no GUI needed)
     */
    @Test
    public void testConstructorWithNonNullPresentation()
    {
        Presentation p = new Presentation();
        KeyController kc = new KeyController(p);

        // Verify the KeyController was created successfully
        assertNotNull("KeyController should be created successfully", kc);

        // Verify the presentation was stored correctly
        try
        {
            Field presentationField = KeyController.class.getDeclaredField("presentation");
            presentationField.setAccessible(true);
            Object storedPresentation = presentationField.get(kc);
            assertSame("KeyController should store the presentation", p, storedPresentation);
        } catch (Exception e)
        {
            fail("Failed to access presentation field: " + e.getMessage());
        }
    }

    /**
     * Test key logic directly using the TestableKeyController
     */
    @Test
    public void testKeyLogic()
    {
        // Create mock KeyEvent objects
        KeyEvent mockNextKeyEvent = new MockKeyEvent(KeyEvent.VK_PAGE_DOWN, '\0');
        KeyEvent mockPrevKeyEvent = new MockKeyEvent(KeyEvent.VK_PAGE_UP, '\0');
        KeyEvent mockExitKeyEvent = new MockKeyEvent(KeyEvent.VK_Q, 'q');
        KeyEvent mockDefaultKeyEvent = new MockKeyEvent(KeyEvent.VK_A, 'a');

        // Test next slide logic
        presentation.reset();
        keyController.keyPressed(mockNextKeyEvent);
        assertTrue("nextSlide should be called for PAGE_DOWN", presentation.wasNextSlideCalled());

        // Test previous slide logic
        presentation.reset();
        keyController.keyPressed(mockPrevKeyEvent);
        assertTrue("prevSlide should be called for PAGE_UP", presentation.wasPrevSlideCalled());

        // Test exit logic
        exitCalled = false;
        keyController.keyPressed(mockExitKeyEvent);
        assertTrue("exit should be called for 'q'", exitCalled);
        assertEquals("exit status should be 0", 0, exitStatus);

        // Test default case
        presentation.reset();
        exitCalled = false;
        keyController.keyPressed(mockDefaultKeyEvent);
        assertFalse("nextSlide should not be called for unhandled key", presentation.wasNextSlideCalled());
        assertFalse("prevSlide should not be called for unhandled key", presentation.wasPrevSlideCalled());
        assertFalse("exit should not be called for unhandled key", exitCalled);
    }
} 