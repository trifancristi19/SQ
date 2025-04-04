package com.jabberpoint;

import com.jabberpoint.io.PresentationReader;
import com.jabberpoint.io.PresentationWriter;

/**
 * Base interface for presentation loading and saving capabilities
 * @deprecated Use com.jabberpoint.io.PresentationLoader instead
 */
@Deprecated
public interface PresentationLoader extends PresentationReader, PresentationWriter {
    // No additional methods needed - inherits from both interfaces
} 