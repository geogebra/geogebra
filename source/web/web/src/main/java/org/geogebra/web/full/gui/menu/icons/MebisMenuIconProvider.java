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

package org.geogebra.web.full.gui.menu.icons;

import org.geogebra.common.gui.menu.Icon;
import org.geogebra.web.html5.gui.view.IconSpec;
import org.geogebra.web.html5.main.toolbox.FaIconSpec;

/**
 * Gives access to Mebis menu icons.
 */
public final class MebisMenuIconProvider extends DefaultMenuIconProvider {

	@Override
	public IconSpec matchIconWithResource(Icon icon) {
		if (icon == null) {
			return null;
		}
		return switch (icon) {
			case CLEAR -> new FaIconSpec("fa-xmark");
			case DOWNLOAD -> new FaIconSpec("fa-arrow-down-to-line");
			case ABOUT_BOARD -> new FaIconSpec("fa-circle-info");
			case HELP -> new FaIconSpec("fa-circle-question");
			case TEMPLATES -> new FaIconSpec("fa-store");
			case INFO -> new FaIconSpec("fa-section");
			default -> super.matchIconWithResource(icon);
		};
	}
}
