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

package org.geogebra.web.full.mow.header;

import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.html5.gui.view.IconSpec;
import org.geogebra.web.html5.gui.view.ImageIconSpec;
import org.geogebra.web.html5.main.topbar.DefaultTopBarIconResources;
import org.geogebra.web.html5.main.topbar.TopBarIcon;
import org.geogebra.web.html5.main.topbar.TopBarIconProvider;
import org.geogebra.web.resources.SVGResource;

public class DefaultTopBarIconProvider implements TopBarIconProvider {

	private static final DefaultTopBarIconResources res = DefaultTopBarIconResources.INSTANCE;

	@Override
	public IconSpec matchIconWithResource(TopBarIcon icon) {
		return new ImageIconSpec(findImage(icon));
	}

	private SVGResource findImage(TopBarIcon icon) {
		MaterialDesignResources materialIcons = MaterialDesignResources.INSTANCE;
		return switch (icon) {
			case MENU -> res.menu();
			case UNDO -> res.undo_border();
			case REDO -> res.redo_border();
			case ZOOM_IN -> materialIcons.add_black();
			case ZOOM_OUT -> materialIcons.minus_black();
			case PAN_VIEW -> res.pan_view();
			case FULLSCREEN_ON -> res.fullscreen();
			case FULLSCREEN_OFF -> res.fullscreen_exit();
			case SETTINGS -> res.settings();
			case RULING -> res.ruling();
			case COLOR -> res.color();
			case PAGE_OVERVIEW -> res.page_overview();
		};
	}
}
