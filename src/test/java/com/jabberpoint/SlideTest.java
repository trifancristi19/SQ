   package com.jabberpoint;

   import org.junit.Test;
   import static org.junit.Assert.*;

   public class SlideTest {
       
       @Test
       public void testSlideCreation() {
           Slide slide = new Slide();
           assertNotNull("Slide should be created", slide);
           assertEquals("New slide should have 0 items", 0, slide.getSize());
       }
       
       @Test
       public void testAppendItem() {
           Slide slide = new Slide();
           TextItem item = new TextItem(1, "Test Text");
           slide.append(item);
           assertEquals("Slide should have 1 item", 1, slide.getSize());
       }
   }