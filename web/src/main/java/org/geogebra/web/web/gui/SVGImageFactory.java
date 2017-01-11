package org.geogebra.web.web.gui;

import org.geogebra.web.web.gui.images.PerspectiveResources;
import org.geogebra.web.web.gui.images.SvgPerspectiveResources;
import org.geogebra.web.web.gui.toolbar.images.ToolbarResources;
import org.geogebra.web.web.gui.toolbar.svgimages.SvgToolbarResources;

import com.google.gwt.core.shared.GWT;

/**
 * Provides access to SVG image resources
 * 
 * @author Zbynek
 */
public class SVGImageFactory implements ImageFactory {
	private static ToolbarResources tb;

	@Override
	public ToolbarResources getToolbarResources() {
		if (tb == null) {
			tb = GWT.create(SvgToolbarResources.class);
		}
		return tb;

	}

	private static PerspectiveResources pr;

	@Override
	public PerspectiveResources getPerspectiveResources() {
		if (pr == null) {
			pr = GWT.create(SvgPerspectiveResources.class);
		}
		return pr;

	}
}
