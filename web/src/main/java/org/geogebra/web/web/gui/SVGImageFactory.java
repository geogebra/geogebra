package org.geogebra.web.web.gui;

import org.geogebra.web.web.css.ToolbarSvgResources;
import org.geogebra.web.web.gui.images.PerspectiveResources;
import org.geogebra.web.web.gui.images.SvgPerspectiveResources;
import org.geogebra.web.web.gui.toolbar.images.ToolbarResources;

import com.google.gwt.core.shared.GWT;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Provides access to SVG image resources
 * 
 * @author Zbynek
 */
@SuppressFBWarnings({ "ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD",
		"tb and pr should be static" })
public class SVGImageFactory implements ImageFactory {
	private static ToolbarResources tb;
	private static PerspectiveResources pr;

	@Override
	public ToolbarResources getToolbarResources() {
		if (tb == null) {
			tb = GWT.create(ToolbarSvgResources.class);
		}
		return tb;

	}

	@Override
	public PerspectiveResources getPerspectiveResources() {
		if (pr == null) {
			pr = GWT.create(SvgPerspectiveResources.class);
		}
		return pr;

	}
}
