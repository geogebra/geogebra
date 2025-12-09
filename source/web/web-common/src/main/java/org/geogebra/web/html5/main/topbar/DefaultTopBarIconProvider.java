/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
		case ZOOM_TO_FIT:
			return res.show_all_objects();
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
