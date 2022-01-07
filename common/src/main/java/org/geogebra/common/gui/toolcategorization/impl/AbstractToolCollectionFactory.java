package org.geogebra.common.gui.toolcategorization.impl;

import org.geogebra.common.gui.toolcategorization.ToolCollectionFactory;

public abstract class AbstractToolCollectionFactory implements ToolCollectionFactory {

    boolean isPhoneApp = false;

    /**
     * Set to true if this Factory is created for phone apps.
     *
     * @param isPhoneApp if phone app is using this
     */
    public void setPhoneApp(boolean isPhoneApp) {
        this.isPhoneApp = isPhoneApp;
    }
}
