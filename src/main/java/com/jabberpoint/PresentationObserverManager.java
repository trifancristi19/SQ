package com.jabberpoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages observers for a presentation
 * Extracted to follow Single Responsibility Principle
 */
public class PresentationObserverManager {
    private List<PresentationObserver> observers = new ArrayList<>();
    
    /**
     * Add an observer to be notified of presentation changes
     * @param observer The observer to add
     */
    public void addObserver(PresentationObserver observer) {
        if (observer != null) {
            this.observers.add(observer);
        }
    }
    
    /**
     * Remove an observer 
     * @param observer The observer to remove
     */
    public void removeObserver(PresentationObserver observer) {
        this.observers.remove(observer);
    }
    
    /**
     * Notify all observers that the slide has changed
     * @param slideNumber The current slide number
     */
    public void notifySlideChanged(int slideNumber) {
        for (PresentationObserver observer : this.observers) {
            observer.onSlideChanged(slideNumber);
        }
    }
    
    /**
     * Notify all observers that the presentation has changed
     */
    public void notifyPresentationChanged() {
        for (PresentationObserver observer : this.observers) {
            observer.onPresentationChanged();
        }
    }
} 