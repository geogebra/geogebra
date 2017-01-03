package org.geogebra.web.web.gui;

import org.geogebra.web.web.gui.images.PerspectiveResources;
import org.geogebra.web.web.gui.toolbar.images.ToolbarResources;

/**
 * Abstract factory for image resources
 *
 */
public interface ImageFactory {
	/**
	 * @return resources for toolbar
	 */
	public ToolbarResources getToolbarResources();

	/**
	 * @return resources for menu and stylebar
	 */
	public PerspectiveResources getPerspectiveResources();
}
