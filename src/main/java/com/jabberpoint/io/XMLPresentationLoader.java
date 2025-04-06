package com.jabberpoint.io;

import com.jabberpoint.Presentation;
import com.jabberpoint.XMLAccessor;
import com.jabberpoint.io.XMLParsingStrategy;
import com.jabberpoint.io.DOMXMLParsingStrategy;
import com.jabberpoint.io.XMLParsingStrategyFactory;

/**
 * XML implementation of PresentationLoader
 * Uses XMLAccessor for actual reading/writing
 */
public class XMLPresentationLoader implements PresentationLoader
{
    private final XMLAccessor accessor;

    /**
     * Create a loader with the default XML parsing strategy
     */
    public XMLPresentationLoader()
    {
        this(XMLParsingStrategyFactory.getDefaultStrategy());
    }

    /**
     * Create a loader with a specific XML parsing strategy
     *
     * @param strategy The XML parsing strategy to use
     */
    public XMLPresentationLoader(XMLParsingStrategy strategy)
    {
        this.accessor = new XMLAccessor(strategy);
    }

    @Override
    public void loadPresentation(Presentation presentation, String fileName) throws Exception
    {
        this.accessor.loadFile(presentation, fileName);
    }

    @Override
    public void savePresentation(Presentation presentation, String fileName) throws Exception
    {
        this.accessor.saveFile(presentation, fileName);
    }
} 