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
public class MebisMenuIconProvider extends DefaultMenuIconProvider {

	@Override
	public IconSpec matchIconWithResource(Icon icon) {
		if (icon == null) {
			return null;
		}
		switch (icon) {
		case CLEAR:
			return new FaIconSpec("fa-file");
		case SEARCH:
			return new FaIconSpec("fa-folder-open");
		case FOLDER:
			return new FaIconSpec("fa-folder-arrow-up");
		case SAVE:
			return new FaIconSpec("fa-floppy-disk");
		case EXPORT_FILE:
			return new FaIconSpec("fa-share-nodes");
		case EXPORT_IMAGE:
			return new FaIconSpec("fa-images");
		case DOWNLOAD:
			return new FaIconSpec("fa-arrow-down-to-line");
		case PRINT:
			return new FaIconSpec("fa-print");
		case SETTINGS:
			return new FaIconSpec("fa-gear");
		case INFO:
			return new FaIconSpec("fa-circle-info");
		}
		return super.matchIconWithResource(icon);
	}
}