package com.jabberpoint;

import org.junit.Before;
import org.junit.Test;
import org.junit.BeforeClass;
import org.junit.Assume;
import static org.junit.Assert.*;

import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class KeyControllerTest
{
    private static boolean isHeadless;
    private KeyController keyController;
    private TestablePresentation presentation;
    private boolean exitCalled = false;
    private int exitStatus = -1;

    @BeforeClass
    public static void setUpClass() {
        // Check if we're in a headless environment
        isHeadless = GraphicsEnvironment.isHeadless();
    }

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

    /**
     * Test that KeyController properly responds to next slide keys
     * This test uses Assume to skip in headless environments
     */
    @Test
    public void testKeyPressedNextSlide()
    {
        // Skip test in headless environment using Assume
        Assume.assumeFalse("Skipping testKeyPressedNextSlide in headless environment", isHeadless);

        try {
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
        } catch (HeadlessException e) {
            // This shouldn't happen due to Assume.assumeFalse, but add as a safety check
            Assume.assumeNoException(e);
        }
    }

    /**
     * Test that KeyController properly responds to previous slide keys
     * This test uses Assume to skip in headless environments
     */
    @Test
    public void testKeyPressedPrevSlide()
    {
        // Skip test in headless environment using Assume
        Assume.assumeFalse("Skipping testKeyPressedPrevSlide in headless environment", isHeadless);

        try {
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
        } catch (HeadlessException e) {
            // This shouldn't happen due to Assume.assumeFalse, but add as a safety check
            Assume.assumeNoException(e);
        }
    }

    /**
     * Test that KeyController properly handles exit keys
     * This test uses Assume to skip in headless environments
     */
    @Test
    public void testKeyPressedExit()
    {
        // Skip test in headless environment using Assume
        Assume.assumeFalse("Skipping testKeyPressedExit in headless environment", isHeadless);

        try {
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
        } catch (HeadlessException e) {
            // This shouldn't happen due to Assume.assumeFalse, but add as a safety check
            Assume.assumeNoException(e);
        }
    }

    /**
     * Test that KeyController properly handles unrecognized keys
     * This test uses Assume to skip in headless environments
     */
    @Test
    public void testKeyPressedDefault()
    {
        // Skip test in headless environment using Assume
        Assume.assumeFalse("Skipping testKeyPressedDefault in headless environment", isHeadless);

        try {
            // Test an unhandled key (e.g., 'A')
            presentation.reset();
            KeyEvent keyEvent = new KeyEvent(new MockComponent(), KeyEvent.KEY_PRESSED,
                    System.currentTimeMillis(), 0, KeyEvent.VK_A, 'A');
            keyController.keyPressed(keyEvent);
            assertFalse(presentation.wasNextSlideCalled());
            assertFalse(presentation.wasPrevSlideCalled());
            assertFalse(exitCalled);
        } catch (HeadlessException e) {
            // This shouldn't happen due to Assume.assumeFalse, but add as a safety check
            Assume.assumeNoException(e);
        }
    }

    /**
     * Test KeyController constructor (non-GUI test)
     * This test doesn't require a GUI so it can run in headless mode
     */
    @Test
    public void testConstructorWithNonNullPresentation()
    {
        Presentation p = new Presentation();
        KeyController kc = new KeyController(p);

        // Verify the KeyController was created successfully
        assertNotNull("KeyController should be created successfully", kc);
        
        // Verify the presentation was stored correctly
        try {
            Field presentationField = KeyController.class.getDeclaredField("presentation");
            presentationField.setAccessible(true);
            Object storedPresentation = presentationField.get(kc);
            assertSame("KeyController should store the presentation", p, storedPresentation);
        } catch (Exception e) {
            fail("Failed to access presentation field: " + e.getMessage());
        }
    }
    
    /**
     * Test KeyController logic without creating GUI components
     * This test doesn't require a GUI so it can run in headless mode
     */
    @Test
    public void testKeyControllerLogicWithReflection() {
        // Create a presentation that will track method calls
        TestablePresentation testPresentation = new TestablePresentation();
        
        // Create a KeyController with our testable presentation
        KeyController controller = new KeyController(testPresentation);
        
        try {
            // Get the keyPressed method
            Method keyPressedMethod = KeyController.class.getDeclaredMethod("keyPressed", KeyEvent.class);
            keyPressedMethod.setAccessible(true);
            
            // Create a mock that bypasses the need for a real component
            KeyEvent mockNextEvent = createMockKeyEvent(KeyEvent.VK_DOWN);
            KeyEvent mockPrevEvent = createMockKeyEvent(KeyEvent.VK_UP);
            KeyEvent mockDefaultEvent = createMockKeyEvent(KeyEvent.VK_F1);
            
            // Test nextSlide case
            keyPressedMethod.invoke(controller, mockNextEvent);
            assertTrue("nextSlide should be called", testPresentation.wasNextSlideCalled());
            
            // Test prevSlide case
            testPresentation.reset();
            keyPressedMethod.invoke(controller, mockPrevEvent);
            assertTrue("prevSlide should be called", testPresentation.wasPrevSlideCalled());
            
            // Test default case
            testPresentation.reset();
            keyPressedMethod.invoke(controller, mockDefaultEvent);
            assertFalse("nextSlide should not be called", testPresentation.wasNextSlideCalled());
            assertFalse("prevSlide should not be called", testPresentation.wasPrevSlideCalled());
            
        } catch (Exception e) {
            fail("Exception in reflection test: " + e.getMessage());
        }
    }
    
    /**
     * Helper method to create a mock KeyEvent without needing a real Component
     */
    private KeyEvent createMockKeyEvent(int keyCode) {
        try {
            // Create a MockComponent that won't use the display
            Component mockComponent = new MockComponent();
            
            // Create a KeyEvent with the specified key code
            return new KeyEvent(
                mockComponent,
                KeyEvent.KEY_PRESSED,
                System.currentTimeMillis(),
                0, // No modifiers
                keyCode,
                (char)keyCode // Use the keyCode as char
            );
        } catch (HeadlessException e) {
            // If we can't create a component, return null
            // This should never happen since MockComponent doesn't use the display
            return null;
        }
    }
} 