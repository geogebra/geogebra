package org.geogebra.web.html5.main.toolbox;

import org.geogebra.web.html5.gui.view.IconSpec;

public interface ToolboxIconProvider {

	IconSpec matchIconWithResource(ToolboxIcon icon);
}
