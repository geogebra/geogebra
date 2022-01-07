package org.geogebra.common.gui.toolcategorization.impl;

import org.geogebra.common.gui.toolcategorization.ToolCollectionFactory;

public abstract class AbstractToolCollectionFactory implements ToolCollectionFactory {

    boolean isPhoneApp = false;
    boolean supportsImageTool = true;

    /**
     * Set to true if this Factory is created for phone apps.
     *
     * @param isPhoneApp if phone app is using this
     */
    public void setPhoneApp(boolean isPhoneApp) {
        this.isPhoneApp = isPhoneApp;
    }

    /**
     * THIS IS A TMP METHOD, UNTIL WE HAVE IMAGE TOOL FOR BOTH ANDROID AND IOS
     * (It can be removed in APPS-3527)
     *
     * Set to true if the platform supports to have the Image tool
     * @param hasImageTool
     */
    public void setSupportsImageTool(boolean supportsImageTool) {
        this.supportsImageTool = supportsImageTool;
    }
}
