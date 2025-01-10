package org.geogebra.web.html5.main.topbar;

import org.geogebra.web.html5.gui.view.IconSpec;
import org.geogebra.web.html5.gui.view.ImageIconSpec;
import org.geogebra.web.resources.SVGResource;

public class DefaultTopBarIconProvider implements TopBarIconProvider {

	private static final DefaultTopBarIconResources res = DefaultTopBarIconResources.INSTANCE;

	@Override
	public IconSpec matchIconWithResource(TopBarIcon icon) {
		return new ImageIconSpec(findImage(icon));
	}

	private SVGResource findImage(TopBarIcon icon) {
		switch (icon) {
		case MENU:
			return res.menu();
		case UNDO:
			return res.undo_border();
		case REDO:
			return res.redo_border();
		case ZOOM_IN:
			return res.zoom_in();
		case ZOOM_OUT:
			return res.zoom_out();
		case STANDARD_VIEW:
			return res.home();
		case PAN_VIEW:
			return res.pan_view();
		case FULLSCREEN_ON:
			return res.fullscreen();
		case FULLSCREEN_OFF:
			return res.fullscreen_exit();
		case SETTINGS:
			return res.settings();
		case RULING:
			return res.ruling();
		case COLOR:
			return res.color();
		case PAGE_OVERVIEW:
			return res.page_overview();
		default:
			return null;
		}
	}
}
