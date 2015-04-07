package org.geogebra.web.web.gui;

import org.geogebra.web.web.gui.images.PerspectiveResources;
import org.geogebra.web.web.gui.images.SvgPerspectiveResources;
import org.geogebra.web.web.gui.toolbar.images.ToolbarResources;
import org.geogebra.web.web.gui.toolbar.svgimages.SvgToolbarResources;

import com.google.gwt.core.shared.GWT;

public class SVGImageFactory implements ImageFactory {
	private static ToolbarResources tb;

	public ToolbarResources getToolbarResources() {
		if (tb == null) {
			tb = GWT.create(SvgToolbarResources.class);
		}
		return tb;

	}

	private static PerspectiveResources pr;

	public PerspectiveResources getPerspectiveResources() {
		if (pr == null) {
			pr = GWT.create(SvgPerspectiveResources.class);
		}
		return pr;

	}
}
