package com.jabberpoint;

/*
 * Observer interface for presentation changes
 */
public interface PresentationObserver
{
    void onSlideChanged(int slideNumber);

    void onPresentationChanged();
} 