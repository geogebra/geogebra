package org.geogebra.web.html5.main.toolbox;

import org.geogebra.web.html5.gui.view.IconSpec;

public class ToolboxIconResource {
	private final ToolboxIconProvider toolboxIconProvider;

	public ToolboxIconResource(ToolboxIconProvider toolboxIconProvider) {
		this.toolboxIconProvider = toolboxIconProvider;
	}

	public IconSpec getImageResource(ToolboxIcon icon) {
		return toolboxIconProvider.matchIconWithResource(icon);
	}
}
