package org.geogebra.web.html5.main.topbar;

import org.geogebra.web.html5.gui.view.IconSpec;
import org.geogebra.web.html5.main.toolbox.FaIconSpec;

public class MebisTopBarIconProvider extends DefaultTopBarIconProvider {

	@Override
	public IconSpec matchIconWithResource(TopBarIcon icon) {
		switch (icon) {
		case MENU:
			return new FaIconSpec("fa-bars");
		case UNDO:
			return new FaIconSpec("fa-arrow-rotate-left");
		case REDO:
			return new FaIconSpec("fa-arrow-rotate-right");
		case ZOOM_IN:
			return new FaIconSpec("fa-magnifying-glass-plus");
		case ZOOM_OUT:
			return new FaIconSpec("fa-magnifying-glass-minus");
		case STANDARD_VIEW:
			return new FaIconSpec("fa-house");
		case PAN_VIEW:
			return new FaIconSpec("fa-arrows-up-down-left-right");
		case FULLSCREEN_ON:
			return new FaIconSpec("fa-expand");
		case FULLSCREEN_OFF:
			return new FaIconSpec("fa-compress");
		case SETTINGS:
			return new FaIconSpec("fa-gear");
		case COLOR:
			return new FaIconSpec("fa-fill-drip");
		}
		return super.matchIconWithResource(icon);
	}
}
