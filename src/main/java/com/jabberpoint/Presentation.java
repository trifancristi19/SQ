package com.jabberpoint;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Presentation maintains the slides in the presentation.</p>
 * <p>There is only instance of this class.</p>
 *
 * @author Ian F. Darwin, ian@darwinsys.com, Gert Florijn, Sylvia Stuurman
 * @version 1.6 2014/05/16 Sylvia Stuurman
 */

public class Presentation
{
    private String title;
    private List<Slide> slides;
    private int currentSlideNumber = 0;
    private List<PresentationObserver> observers = new ArrayList<>();
    private PresentationLoader loader;  // Strategy pattern
    private SlideViewerComponent slideViewComponent = null; // the viewcomponent of the Slides

    public Presentation()
    {
        slides = new ArrayList<>();
        title = "New Presentation";
    }

    public void setLoader(PresentationLoader loader)
    {
        this.loader = loader;
    }

    public void loadPresentation(String fileName) throws Exception
    {
        if (loader == null)
        {
            throw new IllegalStateException("No presentation loader set");
        }
        loader.loadPresentation(this, fileName);
        notifyPresentationChanged();
    }

    public void savePresentation(String fileName) throws Exception
    {
        if (loader == null)
        {
            throw new IllegalStateException("No presentation loader set");
        }
        loader.savePresentation(this, fileName);
    }

    public void addObserver(PresentationObserver observer)
    {
        observers.add(observer);
    }

    public void removeObserver(PresentationObserver observer)
    {
        observers.remove(observer);
    }

    private void notifySlideChanged()
    {
        for (PresentationObserver observer : observers)
        {
            observer.onSlideChanged(currentSlideNumber);
        }
    }

    private void notifyPresentationChanged()
    {
        for (PresentationObserver observer : observers)
        {
            observer.onPresentationChanged();
        }
    }

    public int getSize()
    {
        return slides.size();
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public void setShowView(SlideViewerComponent slideViewerComponent)
    {
        this.slideViewComponent = slideViewerComponent;
    }

    // give the number of the current slide
    public int getSlideNumber()
    {
        return currentSlideNumber;
    }

    // change the current slide number and signal it to the window
    public void setSlideNumber(int number)
    {
        if (number < 0) {
            currentSlideNumber = 0;
        } else if (number >= slides.size()) {
            currentSlideNumber = slides.size() - 1;
        } else {
            currentSlideNumber = number;
        }
        notifySlideChanged();
    }

    // go to the previous slide unless your at the beginning of the presentation
    public void prevSlide()
    {
        if (slides.isEmpty()) {
            currentSlideNumber = -1;
            return;
        }
        if (currentSlideNumber > 0)
        {
            setSlideNumber(currentSlideNumber - 1);
        }
    }

    // go to the next slide unless your at the end of the presentation.
    public void nextSlide()
    {
        if (slides.isEmpty()) {
            currentSlideNumber = -1;
            return;
        }
        if (currentSlideNumber < (slides.size() - 1))
        {
            setSlideNumber(currentSlideNumber + 1);
        }
    }

    // Delete the presentation to be ready for the next one.
    void clear()
    {
        slides = new ArrayList<>();
        currentSlideNumber = -1;
        notifyPresentationChanged();
    }

    // Add a slide to the presentation
    public void append(Slide slide)
    {
        slides.add(slide);
    }

    // Get a slide with a certain slidenumber
    public Slide getSlide(int number)
    {
        if (number < 0 || number >= getSize())
        {
            return null;
        }
        return slides.get(number);
    }

    // Give the current slide
    public Slide getCurrentSlide()
    {
        if (currentSlideNumber < 0 || currentSlideNumber >= slides.size()) {
            return null;
        }
        return slides.get(currentSlideNumber);
    }

    public void exit(int n) {
        doExit(n);
    }
    
    // Protected method that can be overridden in tests
    protected void doExit(int n) {
        System.exit(n);
    }

    public List<Slide> getSlides()
    {
        return slides;
    }

    public void setSlides(List<Slide> slides)
    {
        this.slides = (slides != null) ? slides : new ArrayList<>();
        notifyPresentationChanged();
    }

    public int getCurrentSlideNumber()
    {
        return currentSlideNumber;
    }
}
