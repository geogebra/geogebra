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

package org.geogebra.web.full.euclidian.quickstylebar.icon;

import org.geogebra.common.properties.PropertyResource;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.html5.gui.view.IconSpec;
import org.geogebra.web.html5.gui.view.ImageIconSpec;
import org.geogebra.web.html5.main.toolbox.FaIconSpec;
import org.geogebra.web.resources.SVGResource;

public class MebisPropertiesIconProvider extends DefaultPropertiesIconProvider {

	private final static MaterialDesignResources res = MaterialDesignResources.INSTANCE;

	@Override
	protected IconSpec getTextSizeIcon() {
		return new FaIconSpec("fa-text-size");
	}

	@Override
	protected IconSpec getOpacityIcon() {
		return new FaIconSpec("fa-droplet");
	}

	@Override
	protected IconSpec getTextColorIcon() {
		return new ImageIconSpec(res.font_color());
	}

	@Override
	protected IconSpec getColorIcon() {
		return new FaIconSpec("fa-fill-drip");
	}

	@Override
	public IconSpec matchIconWithResource(PropertyResource propertyResource) {
		return switch (propertyResource) {
			case ICON_LINE_TYPE_FULL -> new FaIconSpec("fa-horizontal-rule");
			case ICON_CELL_BORDER_ALL -> new FaIconSpec("fa-table-cells-large");
			case ICON_CELL_BORDER_INNER -> new FaIconSpec("fa-border-inner");
			case ICON_CELL_BORDER_OUTER -> new FaIconSpec("fa-border-outer");
			case ICON_ALIGNMENT_LEFT -> new FaIconSpec("fa-align-left");
			case ICON_ALIGNMENT_CENTER -> new FaIconSpec("fa-align-center");
			case ICON_ALIGNMENT_RIGHT -> new FaIconSpec("fa-align-right");
			case ICON_ALIGNMENT_BOTTOM -> new FaIconSpec("fa-arrow-down-to-line");
			case ICON_ALIGNMENT_MIDDLE -> new FaIconSpec("fa-arrows-to-line");
			case ICON_ALIGNMENT_TOP -> new FaIconSpec("fa-arrow-up-to-line");
			case ICON_BORDER_THIN -> new FaIconSpec("fa-square");
			case ICON_UNDERLINE -> new FaIconSpec("fa-underline");
			case ICON_BOLD -> new FaIconSpec("fa-bold");
			case ICON_ITALIC -> new FaIconSpec("fa-italic");
			case ICON_SEGMENT_START_DEFAULT, ICON_SEGMENT_START_LINE, ICON_SEGMENT_START_ARROW,
				 ICON_SEGMENT_START_CROWS_FOOT, ICON_SEGMENT_START_ARROW_OUTLINE,
				 ICON_SEGMENT_START_ARROW_FILLED, ICON_SEGMENT_START_CIRCLE_OUTLINE,
				 ICON_SEGMENT_START_CIRCLE, ICON_SEGMENT_START_SQUARE_OUTLINE,
				 ICON_SEGMENT_START_SQUARE, ICON_SEGMENT_START_DIAMOND_OUTLINE,
				 ICON_SEGMENT_START_DIAMOND, ICON_SEGMENT_END_DEFAULT, ICON_SEGMENT_END_LINE,
				 ICON_SEGMENT_END_ARROW, ICON_SEGMENT_END_CROWS_FOOT,
				 ICON_SEGMENT_END_ARROW_OUTLINE, ICON_SEGMENT_END_ARROW_FILLED,
				 ICON_SEGMENT_END_CIRCLE_OUTLINE, ICON_SEGMENT_END_CIRCLE,
				 ICON_SEGMENT_END_SQUARE_OUTLINE, ICON_SEGMENT_END_SQUARE,
				 ICON_SEGMENT_END_DIAMOND_OUTLINE, ICON_SEGMENT_END_DIAMOND,
				 ICON_CELL_BORDER_NONE, ICON_FILLING_HATCHED, ICON_FILLING_DOTTED,
				 ICON_FILLING_CROSSHATCHED, ICON_FILLING_HONEYCOMB, ICON_NO_FILLING,
				 ICON_LINE_TYPE_DASHED_DOTTED, ICON_LINE_TYPE_DASHED_LONG, ICON_LINE_TYPE_DOTTED,
				 ICON_LINE_TYPE_DASHED_SHORT -> getFallbackIcon(propertyResource);
			default -> super.matchIconWithResource(propertyResource);
		};
	}

	private ImageIconSpec getFallbackIcon(PropertyResource propertyResource) {
		SVGResource svgResource = switch (propertyResource) {
			case ICON_SEGMENT_START_DEFAULT -> res.line_start();
			case ICON_SEGMENT_START_LINE -> res.stop_line_start();
			case ICON_SEGMENT_START_ARROW -> res.arrow_start();
			case ICON_SEGMENT_START_CROWS_FOOT -> res.crow_feet_start();
			case ICON_SEGMENT_START_ARROW_OUTLINE -> res.arrow_line_start();
			case ICON_SEGMENT_START_ARROW_FILLED -> res.arrow_filled_start();
			case ICON_SEGMENT_START_CIRCLE_OUTLINE -> res.circle_start();
			case ICON_SEGMENT_START_CIRCLE -> res.circle_filled_start();
			case ICON_SEGMENT_START_SQUARE_OUTLINE -> res.cube_start();
			case ICON_SEGMENT_START_SQUARE -> res.cube_filled_start();
			case ICON_SEGMENT_START_DIAMOND_OUTLINE -> res.diamond_start();
			case ICON_SEGMENT_START_DIAMOND -> res.diamond_filled_start();
			case ICON_SEGMENT_END_DEFAULT -> res.line_end();
			case ICON_SEGMENT_END_LINE -> res.stop_line_end();
			case ICON_SEGMENT_END_ARROW -> res.arrow_end();
			case ICON_SEGMENT_END_CROWS_FOOT -> res.crow_feet_end();
			case ICON_SEGMENT_END_ARROW_OUTLINE -> res.arrow_line_end();
			case ICON_SEGMENT_END_ARROW_FILLED -> res.arrow_filled_end();
			case ICON_SEGMENT_END_CIRCLE_OUTLINE -> res.circle_end();
			case ICON_SEGMENT_END_CIRCLE -> res.circle_filled_end();
			case ICON_SEGMENT_END_SQUARE_OUTLINE -> res.cube_end();
			case ICON_SEGMENT_END_SQUARE -> res.cube_filled_end();
			case ICON_SEGMENT_END_DIAMOND_OUTLINE -> res.diamond_end();
			case ICON_SEGMENT_END_DIAMOND -> res.diamond_filled_end();
			case ICON_FILLING_HATCHED -> res.filling_hatched();
			case ICON_FILLING_DOTTED -> res.filling_dotted();
			case ICON_FILLING_CROSSHATCHED -> res.filling_crosshatched();
			case ICON_FILLING_HONEYCOMB -> res.filling_honeycomb();
			case ICON_NO_FILLING -> res.filling_off();
			case ICON_LINE_TYPE_DASHED_DOTTED -> res.line_dash_dot_fontawesome();
			case ICON_LINE_TYPE_DASHED_LONG -> res.line_dashed_long_fontawesome();
			case ICON_LINE_TYPE_DOTTED -> res.line_dotted_fontawesome();
			case ICON_LINE_TYPE_DASHED_SHORT -> res.line_dashed_short_fontawesome();
			case ICON_CELL_BORDER_NONE -> res.border_none();
			default -> res.text_serif_black();
		};
		return new ImageIconSpec(svgResource);
	}
}
