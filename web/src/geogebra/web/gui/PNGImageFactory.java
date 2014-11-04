package geogebra.web.gui;

import geogebra.web.gui.toolbar.images.MyIconResourceBundle;
import geogebra.web.gui.toolbar.images.ToolbarResources;

import com.google.gwt.core.shared.GWT;

public class PNGImageFactory implements ImageFactory{

    public ToolbarResources getToolbarResources() {
	    return GWT.create(MyIconResourceBundle.class);
    }

}
