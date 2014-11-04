package geogebra.web.gui;

import geogebra.web.gui.images.PerspectiveResources;
import geogebra.web.gui.toolbar.images.ToolbarResources;

public interface ImageFactory {
	public ToolbarResources getToolbarResources();

	public PerspectiveResources getPerspectiveResources();
}
