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
import org.geogebra.web.resources.SVGResource;

public class DefaultGeneralIconProvider implements GeneralIconProvider {

	private static final DefaultGeneralIconResources res = DefaultGeneralIconResources.INSTANCE;

	@Override
	public IconSpec matchIconWithResource(GeneralIcon icon) {
		return new ImageIconSpec(findImage(icon));
	}

	private SVGResource findImage(GeneralIcon icon) {
		return switch (icon) {
			case NEW_TAB -> res.new_tab();
			case DELETE -> res.delete();
			case MORE -> res.more();
			case CROP -> res.crop();
			case NO_COLOR -> res.no_color();
			case CHECK_MARK -> res.check_mark();
			case CUT -> res.cut();
			case COPY -> res.copy();
			case PASTE -> res.paste();
			case LOCK -> res.lock();
			case SETTINGS -> res.settings();
			case ARROW_RIGHT -> res.arrow_right();
			case ARROW_LEFT -> res.arrow_left();
			case RENAME -> res.rename();
			case PLUS -> res.plus();
			case DUPLICATE -> res.duplicate();
			case TABLE_HEADING_COLUMN -> res.table_heading_column();
			case TABLE_HEADING_ROW -> res.table_heading_row();
			case X_SQUARE -> res.x_square();
			case X_2 -> res.x_2();
			case BULLET_LIST -> res.bullet_list();
			case NUMBERED_LIST -> res.numbered_list();
			case LINE_CHART -> res.line_chart();
			case BAR_CHART -> res.bar_chart();
			case PIE_CHART -> res.pie_chart();
			default -> null;
		};
	}
}
