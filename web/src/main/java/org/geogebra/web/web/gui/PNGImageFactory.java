package org.geogebra.web.web.gui;

import org.geogebra.web.web.gui.images.PerspectiveResources;
import org.geogebra.web.web.gui.images.PngPerspectiveResources;
import org.geogebra.web.web.gui.toolbar.images.MyIconResourceBundle;
import org.geogebra.web.web.gui.toolbar.images.ToolbarResources;

import com.google.gwt.core.shared.GWT;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Provides access to PNG image resources
 * 
 * @author Zbynek
 */

@SuppressFBWarnings({ "ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD",
		"tb and pr should be static" })
public class PNGImageFactory implements ImageFactory {
	private static ToolbarResources tb;

	@Override
	public ToolbarResources getToolbarResources() {
		if (tb == null) {
			tb = GWT.create(MyIconResourceBundle.class);
		}
		return tb;
	}

	private static PerspectiveResources pr;

	@Override
	public PerspectiveResources getPerspectiveResources() {
		if (pr == null) {
			pr = GWT.create(PngPerspectiveResources.class);
		}
		return pr;

	}

}
