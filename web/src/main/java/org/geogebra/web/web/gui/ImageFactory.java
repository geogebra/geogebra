package org.geogebra.web.web.gui;

import org.geogebra.web.web.css.ToolbarSvgResources;
import org.geogebra.web.web.gui.images.SvgPerspectiveResources;
import org.geogebra.web.web.gui.toolbar.images.ToolbarResources;

import com.google.gwt.core.shared.GWT;

/**
 * Provides access to PNG image resources
 * 
 * @author Zbynek
 */

public class ImageFactory {
	private static ToolbarResources tb;
	private static SvgPerspectiveResources pr;

	public static ToolbarResources getToolbarResources() {
		if (tb == null) {
			tb = GWT.create(ToolbarSvgResources.class);
		}
		return tb;

	}

	public static SvgPerspectiveResources getPerspectiveResources() {
		if (pr == null) {
			pr = GWT.create(SvgPerspectiveResources.class);
		}
		return pr;

	}

}
