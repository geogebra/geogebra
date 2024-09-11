package org.geogebra.common.gui.toolcategorization.impl;

import org.geogebra.common.gui.toolcategorization.ToolCollectionFactory;

public abstract class AbstractToolCollectionFactory implements ToolCollectionFactory {

    final boolean isMobileApp;

    public AbstractToolCollectionFactory(boolean isMobileApp) {
        this.isMobileApp = isMobileApp;
    }
}
