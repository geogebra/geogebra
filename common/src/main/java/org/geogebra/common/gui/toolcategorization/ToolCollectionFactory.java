package org.geogebra.common.gui.toolcategorization;

/** This class creates ToolCollection objects. */
public interface ToolCollectionFactory {

    /**
     * Create a ToolCollection object.
     *
     * @return a ToolCollection object
     */
    ToolCollection createToolCollection();
}
