package org.geogebra.web.html5.main.topbar;

import org.geogebra.web.html5.gui.view.IconSpec;

public interface TopBarIconProvider {

	IconSpec matchIconWithResource(TopBarIcon icon);
}
