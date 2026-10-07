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

import org.geogebra.web.html5.gui.view.IconSpec;
import org.geogebra.web.html5.gui.view.ImageIconSpec;
import org.geogebra.web.html5.main.toolbox.FaIconSpec;
import org.geogebra.web.html5.main.topbar.DefaultTopBarIconResources;
import org.geogebra.web.html5.main.topbar.TopBarIcon;

public class MebisTopBarIconProvider extends DefaultTopBarIconProvider {

	@Override
	public IconSpec matchIconWithResource(TopBarIcon icon) {
		return switch (icon) {
			case MENU -> new FaIconSpec("fa-bars");
			case UNDO -> new FaIconSpec("fa-arrow-rotate-left");
			case REDO -> new FaIconSpec("fa-arrow-rotate-right");
			case ZOOM_IN -> new FaIconSpec("fa-plus");
			case ZOOM_OUT -> new FaIconSpec("fa-minus");
			case PAN_VIEW -> new FaIconSpec("fa-arrows-up-down-left-right");
			case FULLSCREEN_ON -> new FaIconSpec("fa-expand");
			case FULLSCREEN_OFF -> new FaIconSpec("fa-compress");
			case SETTINGS -> new FaIconSpec("fa-gear");
			case RULING -> new ImageIconSpec(DefaultTopBarIconResources.INSTANCE.ruling_fontawesome());
			case COLOR -> new FaIconSpec("fa-fill-drip");
			case PAGE_OVERVIEW ->
				new ImageIconSpec(DefaultTopBarIconResources.INSTANCE.page_overview_mebis());
		};
	}
}
