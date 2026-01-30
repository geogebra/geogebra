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

package org.geogebra.web.html5.main.general;

import org.geogebra.web.html5.gui.view.IconSpec;
import org.geogebra.web.html5.gui.view.ImageIconSpec;
import org.geogebra.web.html5.main.toolbox.FaIconSpec;
import org.geogebra.web.resources.SVGResource;

public class MebisGeneralIconProvider extends DefaultGeneralIconProvider {

	private static final DefaultGeneralIconResources res = DefaultGeneralIconResources.INSTANCE;

	@Override
	public IconSpec matchIconWithResource(GeneralIcon icon) {
		return switch (icon) {
			case NEW_TAB -> new FaIconSpec("fa-arrow-up-right-from-square");
			case DELETE -> new FaIconSpec("fa-trash");
			case MORE -> new FaIconSpec("fa-ellipsis-vertical");
			case CROP -> new FaIconSpec("fa-crop-simple");
			case NO_COLOR -> new FaIconSpec("fa-droplet-slash");
			case CHECK_MARK -> new FaIconSpec("fa-check");
			case LINE_CHART -> new FaIconSpec("fa-chart-line");
			case BAR_CHART -> new FaIconSpec("fa-chart-simple");
			case PIE_CHART -> new FaIconSpec("fa-chart-pie-simple");
			case BULLET_LIST -> new FaIconSpec("fa-list-ul");
			case NUMBERED_LIST -> new FaIconSpec("fa-list-ol");
			case COPY -> new FaIconSpec("fa-copy");
			case PASTE -> new FaIconSpec("fa-clipboard");
			case LOCK -> new FaIconSpec("fa-lock-keyhole");
			case SETTINGS -> new FaIconSpec("fa-gear");
			case ARROW_RIGHT -> new FaIconSpec("fa-caret-right");
			case ARROW_LEFT -> new FaIconSpec("fa-caret-left");
			case RENAME -> new FaIconSpec("fa-pencil-line");
			case PLUS -> new FaIconSpec("fa-plus");
			case DUPLICATE -> new FaIconSpec("fa-clone-plus");
			case CUT, TABLE_HEADING_COLUMN, TABLE_HEADING_ROW, X_SQUARE,
				 X_2 -> new ImageIconSpec(getFallbackSVG(icon));
		default -> super.matchIconWithResource(icon);
		};
	}

	private SVGResource getFallbackSVG(GeneralIcon icon) {
		return switch (icon) {
			case CUT -> res.scissors_fontawesome();
			case TABLE_HEADING_COLUMN -> res.table_heading_column_fontawesome();
			case TABLE_HEADING_ROW -> res.table_heading_row_fontawesome();
			case X_SQUARE -> res.x_square_fontawesome();
			case X_2 -> res.x_2_fontawesome();
			default -> null;
		};
	}
}
