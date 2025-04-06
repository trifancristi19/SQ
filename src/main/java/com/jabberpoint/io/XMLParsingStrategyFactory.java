package com.jabberpoint.io;

/**
 * Factory for creating XMLParsingStrategy instances
 * Allows clients to get the appropriate strategy without knowing implementation details
 */
public class XMLParsingStrategyFactory {

    /**
     * Strategy types available
     */
    public enum StrategyType {
        DOM,
        SAX
    }
    
    /**
     * Get the default strategy (DOM)
     * @return The default XMLParsingStrategy
     */
    public static XMLParsingStrategy getDefaultStrategy() {
        return new DOMXMLParsingStrategy();
    }
    
    /**
     * Get a specific parsing strategy based on the type
     * @param type The type of strategy to get
     * @return The requested XMLParsingStrategy
     */
    public static XMLParsingStrategy getStrategy(StrategyType type) {
        switch (type) {
            case SAX:
                return new SAXXMLParsingStrategy();
            case DOM:
            default:
                return new DOMXMLParsingStrategy();
        }
    }
} 