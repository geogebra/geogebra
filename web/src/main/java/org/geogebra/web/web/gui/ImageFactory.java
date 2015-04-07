package org.geogebra.web.web.gui;

import org.geogebra.web.web.gui.images.PerspectiveResources;
import org.geogebra.web.web.gui.toolbar.images.ToolbarResources;

public interface ImageFactory {
	public ToolbarResources getToolbarResources();

	public PerspectiveResources getPerspectiveResources();
}
