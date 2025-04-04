package com.jabberpoint;

import java.util.ArrayList;
import java.util.List;

import com.jabberpoint.io.PresentationLoader;
import com.jabberpoint.io.PresentationReader;
import com.jabberpoint.io.PresentationWriter;

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
    private PresentationObserverManager observerManager;
    private SlideViewerComponent slideViewComponent = null; // the viewcomponent of the Slides

    public Presentation()
    {
        this.slides = new ArrayList<>();
        this.title = "New Presentation";
        this.observerManager = new PresentationObserverManager();
    }

    /**
     * @deprecated Use com.jabberpoint.io.PresentationReader or PresentationWriter directly
     */
    @Deprecated
    public void setLoader(com.jabberpoint.PresentationLoader loader)
    {
        // Deprecated - kept for backward compatibility
    }

    /**
     * @deprecated Use com.jabberpoint.io.PresentationReader directly
     */
    @Deprecated
    public void loadPresentation(String fileName) throws Exception
    {
        throw new UnsupportedOperationException("Use PresentationReader.loadPresentation(presentation, fileName) instead");
    }

    /**
     * @deprecated Use com.jabberpoint.io.PresentationWriter directly
     */
    @Deprecated
    public void savePresentation(String fileName) throws Exception
    {
        throw new UnsupportedOperationException("Use PresentationWriter.savePresentation(presentation, fileName) instead");
    }

    public void addObserver(PresentationObserver observer)
    {
        this.observerManager.addObserver(observer);
    }

    public void removeObserver(PresentationObserver observer)
    {
        this.observerManager.removeObserver(observer);
    }

    private void notifySlideChanged()
    {
        this.observerManager.notifySlideChanged(this.currentSlideNumber);
    }

    private void notifyPresentationChanged()
    {
        this.observerManager.notifyPresentationChanged();
    }

    public int getSize()
    {
        return this.slides.size();
    }

    public String getTitle()
    {
        return this.title;
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
        return this.currentSlideNumber;
    }

    // change the current slide number and signal it to the window
    public void setSlideNumber(int number)
    {
        if (number < 0)
        {
            this.currentSlideNumber = 0;
        }
        else if (number >= this.slides.size())
        {
            this.currentSlideNumber = this.slides.size() - 1;
        }
        else
        {
            this.currentSlideNumber = number;
        }
        notifySlideChanged();
    }

    // go to the previous slide unless your at the beginning of the presentation
    public void prevSlide()
    {
        if (this.slides.isEmpty())
        {
            this.currentSlideNumber = -1;
            return;
        }
        if (this.currentSlideNumber > 0)
        {
            setSlideNumber(this.currentSlideNumber - 1);
        }
    }

    // go to the next slide unless your at the end of the presentation.
    public void nextSlide()
    {
        if (this.slides.isEmpty())
        {
            this.currentSlideNumber = -1;
            return;
        }
        if (this.currentSlideNumber < (this.slides.size() - 1))
        {
            setSlideNumber(this.currentSlideNumber + 1);
        }
    }

    // Delete the presentation to be ready for the next one.
    void clear()
    {
        this.slides = new ArrayList<>();
        this.currentSlideNumber = -1;
        notifyPresentationChanged();
    }

    // Add a slide to the presentation
    public void append(Slide slide)
    {
        this.slides.add(slide);
    }

    // Get a slide with a certain slidenumber
    public Slide getSlide(int number)
    {
        if (number < 0 || number >= getSize())
        {
            return null;
        }
        return this.slides.get(number);
    }

    // Give the current slide
    public Slide getCurrentSlide()
    {
        if (this.currentSlideNumber < 0 || this.currentSlideNumber >= this.slides.size())
        {
            return null;
        }
        return this.slides.get(this.currentSlideNumber);
    }

    public void exit(int n)
    {
        doExit(n);
    }

    // Protected method that can be overridden in tests
    protected void doExit(int n)
    {
        System.exit(n);
    }

    public List<Slide> getSlides()
    {
        return this.slides;
    }

    public void setSlides(List<Slide> slides)
    {
        this.slides = (slides != null) ? slides : new ArrayList<>();
        notifyPresentationChanged();
    }

    public int getCurrentSlideNumber()
    {
        return this.currentSlideNumber;
    }
}
