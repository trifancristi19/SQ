package com.jabberpoint;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import java.io.IOException;

public class DemoPresentationTest
{
    private DemoPresentation demoPresentation;
    private Presentation presentation;

    @Before
    public void setUp() {
        demoPresentation = new DemoPresentation();
        presentation = new Presentation();
    }

    @Test
    public void testLoadDemoPresentation() throws IOException
    {
        // Load the demo presentation
        demoPresentation.loadFile(presentation, "");

        // Verify the presentation was loaded correctly
        assertEquals("Demo Presentation", presentation.getTitle());
        assertTrue("Demo presentation should have slides", presentation.getSize() > 0);

        // Check the first slide
        Slide firstSlide = presentation.getSlide(0);
        assertNotNull("First slide should exist", firstSlide);
        assertEquals("JabberPoint", firstSlide.getTitle());
        assertTrue("First slide should have items", firstSlide.getSize() > 0);
    }

    @Test(expected = IllegalStateException.class)
    public void testSaveThrowsException() throws IOException
    {
        // Saving is not supported in DemoPresentation
        demoPresentation.saveFile(presentation, "test.xml");
    }

    @Test
    public void testLoadedSlideContents() throws IOException
    {
        // Load the demo presentation
        demoPresentation.loadFile(presentation, "");

        // Check that we have the expected number of slides (3 as per the implementation)
        assertEquals("Demo should have 3 slides", 3, presentation.getSize());

        // Check titles of slides
        assertEquals("First slide title should match", "JabberPoint",
                presentation.getSlide(0).getTitle());
        assertEquals("Second slide title should match", "Demonstration of levels and stijlen",
                presentation.getSlide(1).getTitle());
        assertEquals("Third slide title should match", "The third slide",
                presentation.getSlide(2).getTitle());
    }

    @Test
    public void testLoadWithAlreadyLoadedPresentation() throws IOException {
        // First, add a slide to the presentation
        Slide customSlide = new Slide();
        customSlide.setTitle("Custom Slide");
        presentation.append(customSlide);
        
        // Clear the presentation before loading the demo content
        presentation.clear();
        
        // Now load the demo presentation - it should clear existing slides
        demoPresentation.loadFile(presentation, "");
        
        // Verify that the custom slide was replaced with demo content
        assertEquals("Demo Presentation", presentation.getTitle());
        assertEquals("Demo should have 3 slides", 3, presentation.getSize());
        
        // First slide should be from demo, not our custom slide
        assertNotEquals("Custom Slide", presentation.getSlide(0).getTitle());
    }
    
    @Test
    public void testDemoPresentationImplementsAccessor() {
        // Verify that DemoPresentation extends Accessor
        assertTrue("DemoPresentation should extend Accessor", 
                demoPresentation instanceof Accessor);
    }
    
    @Test
    public void testLoadWithDifferentFilenames() throws IOException {
        // The filename parameter should be ignored in DemoPresentation
        
        // Load with empty string
        Presentation p1 = new Presentation();
        demoPresentation.loadFile(p1, "");
        
        // Load with some filename
        Presentation p2 = new Presentation();
        demoPresentation.loadFile(p2, "some-file.xml");
        
        // Both should produce the same result
        assertEquals("Both presentations should have the same title", 
                p1.getTitle(), p2.getTitle());
        assertEquals("Both presentations should have the same number of slides",
                p1.getSize(), p2.getSize());
    }
} 