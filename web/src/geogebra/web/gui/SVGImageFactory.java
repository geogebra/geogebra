package geogebra.web.gui;

import geogebra.web.gui.toolbar.images.ToolbarResources;
import geogebra.web.gui.toolbar.svgimages.SvgToolbarResources;

import com.google.gwt.core.shared.GWT;

public class SVGImageFactory implements ImageFactory {
    public ToolbarResources getToolbarResources() {
	    return GWT.create(SvgToolbarResources.class);
    }
}
