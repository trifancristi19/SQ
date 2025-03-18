package com.jabberpoint;

import org.junit.Test;
import static org.junit.Assert.*;
import java.io.IOException;

public class DemoPresentationTest {
    
    @Test
    public void testLoadDemoPresentation() throws IOException {
        DemoPresentation demoPresentation = new DemoPresentation();
        Presentation presentation = new Presentation();
        
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
    public void testSaveThrowsException() throws IOException {
        DemoPresentation demoPresentation = new DemoPresentation();
        Presentation presentation = new Presentation();
        
        // Saving is not supported in DemoPresentation
        demoPresentation.saveFile(presentation, "test.xml");
    }
    
    @Test
    public void testLoadedSlideContents() throws IOException {
        DemoPresentation demoPresentation = new DemoPresentation();
        Presentation presentation = new Presentation();
        
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
} 