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
		case PAGE_OVERVIEW:
			return new ImageIconSpec(DefaultTopBarIconResources.INSTANCE.page_overview_mebis());
		}
		return super.matchIconWithResource(icon);
	}
}
