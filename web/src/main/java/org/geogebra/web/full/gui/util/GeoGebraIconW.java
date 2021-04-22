package org.geogebra.web.full.gui.util;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.kernel.geos.properties.FillType;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.web.full.css.GuiResources;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.html5.gui.util.ImageOrText;
import org.geogebra.web.resources.SVGResource;

/**
 * icon resources (point style, line style, etc.)
 */
public class GeoGebraIconW {

	private static MaterialDesignResources matIcons = MaterialDesignResources.INSTANCE;

	private static SVGResource[] gridStyleIcons = {
			matIcons.stylebar_empty(), matIcons.grid_black(),
			matIcons.grid_polar(), matIcons.grid_isometric() };

	private static SVGResource[] pointStyleSVGIcons = {
			matIcons.point_full(),
			matIcons.point_cross_diag(), matIcons.point_empty(),
			matIcons.point_cross(), matIcons.point_diamond(),
			matIcons.point_diamond_empty(), matIcons.point_up(),
			matIcons.point_down(), matIcons.point_right(),
			matIcons.point_left(), matIcons.point_no_outline() };
	
	private static SVGResource[] lineStyleSVGIcons = {
			matIcons.line_solid(),
			matIcons.line_dashed_long(), matIcons.line_dashed_short(),
			matIcons.line_dotted(), matIcons.line_dash_dot(),
			matIcons.point_cross_diag() };

	/**
	 * creates LineStyle icon
	 * 
	 * @param dashStyle
	 *            dash index (see lineStyleIcons)
	 * @return Canvas with icon drawn
	 */
	public static ImageOrText createLineStyleIcon(int dashStyle) {
		if (dashStyle >= lineStyleSVGIcons.length) {
			return new ImageOrText();
		}
		return new ImageOrText(lineStyleSVGIcons[dashStyle], 24);
	}

	/**
	 * @param pointStyle
	 *            int
	 * @return {@link ImageOrText}
	 */
	public static ImageOrText createPointStyleIcon(int pointStyle) {
		return new ImageOrText(pointStyleSVGIcons[pointStyle], 24);
    }
	
	/**
	 * @param fillType
	 *            fill type
	 * @return {@link ImageOrText}
	 */
	public static ImageOrText createFillStyleIcon(FillType fillType) {
		return new ImageOrText(getFillStyleResource(fillType), 24);
	}

	private static SVGResource getFillStyleResource(FillType fillType) {
		switch (fillType) {
			case STANDARD:
				return matIcons.pattern_filled();
			case HATCH:
				return matIcons.pattern_hatching();
			case DOTTED:
				return matIcons.pattern_dots();
			case CROSSHATCHED:
				return matIcons.pattern_cross_hatching();
			case HONEYCOMB:
				return matIcons.pattern_honeycomb();
			default:
				return null;
		}
	}

	/**
	 * @param pointStyle
	 *            int
	 * @return {@link ImageOrText}
	 */
	public static ImageOrText createGridStyleIcon(int pointStyle) {
		return new ImageOrText(gridStyleIcons[pointStyle], 24);
    }
	
	/**
	 * @param alpha
	 *            {@code float}
	 * @param fgColor
	 *            {@link GColor}
	 * @param bgColor
	 *            {@link GColor}
	 * @return {@link ImageOrText}
	 */

	public static ImageOrText createColorSwatchIcon(double alpha,
			GColor fgColor,
			GColor bgColor) {
		ImageOrText ret = new ImageOrText();
		if (fgColor != null) {
			ret.setFgColor(fgColor.deriveWithAlpha((int) (alpha * 255)));
		}
		if (bgColor != null) {
			ret.setBgColor(bgColor.deriveWithAlpha((int) (alpha * 255)));
		}
		return ret;
	}

	/**
	 * 
	 * @param symbol
	 *            {@code String}
	 * @param fgColor
	 *            {@link GColor}
	 * @param bgColor
	 *            {@link GColor}
	 * @return {@link ImageOrText}
	 */
	public static ImageOrText createTextSymbolIcon(String symbol,
	        GColor fgColor, GColor bgColor) {
		ImageOrText ret = new ImageOrText();
		ret.setText(symbol);
		ret.setFgColor(fgColor);
		ret.setBgColor(bgColor);
		return ret;
	}
	
	/**
	 * @return {@link ImageOrText} Empty icon
	 */
	public static ImageOrText createNullSymbolIcon() {
		return new ImageOrText();
	}

	/**
	 * @param id
	 *            {@code int}
	 * @return {@link ImageOrText}
	 */
	public static ImageOrText createDecorAngleIcon(int id) {
		SVGResource svgResource;
		switch (id) {
		case GeoElementND.DECORATION_ANGLE_TWO_ARCS:
				svgResource =  GuiResources.INSTANCE.deco_angle_2lines();
			break;
		case GeoElementND.DECORATION_ANGLE_THREE_ARCS:
				svgResource =  GuiResources.INSTANCE.deco_angle_3lines();
			break;
		case GeoElementND.DECORATION_ANGLE_ONE_TICK:
				svgResource =  GuiResources.INSTANCE.deco_angle_1stroke();
			break;
		case GeoElementND.DECORATION_ANGLE_TWO_TICKS:
				svgResource =  GuiResources.INSTANCE.deco_angle_2strokes();
			break;
		case GeoElementND.DECORATION_ANGLE_THREE_TICKS:
				svgResource =  GuiResources.INSTANCE.deco_angle_3strokes();
			break;			
		case GeoElementND.DECORATION_ANGLE_ARROW_ANTICLOCKWISE:
				svgResource =  GuiResources.INSTANCE.deco_angle_arrow_up();
			break;
		case GeoElementND.DECORATION_ANGLE_ARROW_CLOCKWISE:
				svgResource =  GuiResources.INSTANCE.deco_angle_arrow_down();
			break;
			default:
				svgResource =  GuiResources.INSTANCE.deco_angle_1line();
		}
		return new ImageOrText(svgResource, 24);
	}

	/**
	 * @param id
	 *            {@code int}
	 * @return {@link ImageOrText}
	 */
	public static ImageOrText createDecorSegmentIcon(int id) {
		SVGResource svgResource;
		switch (id) {		
		case GeoElementND.DECORATION_SEGMENT_ONE_TICK:
			svgResource =  GuiResources.INSTANCE.deco_segment_1stroke();
			break;
		case GeoElementND.DECORATION_SEGMENT_TWO_TICKS:
			svgResource =  GuiResources.INSTANCE.deco_segment_2strokes();
			break;
		case GeoElementND.DECORATION_SEGMENT_THREE_TICKS:
			svgResource =  GuiResources.INSTANCE.deco_segment_3strokes();
			break;
		case GeoElementND.DECORATION_SEGMENT_ONE_ARROW:
			svgResource =  GuiResources.INSTANCE.deco_segment_1arrow();
			break;
		case GeoElementND.DECORATION_SEGMENT_TWO_ARROWS:
			svgResource =  GuiResources.INSTANCE.deco_segment_2arrows();
			break;
		case GeoElementND.DECORATION_SEGMENT_THREE_ARROWS:
			svgResource =  GuiResources.INSTANCE.deco_segment_3arrows();
			break;
		default:
			svgResource =  GuiResources.INSTANCE.deco_segment_none();
			break;
		}
		return new ImageOrText(svgResource, 24);
    }

	/**
	 * @param id
	 *            {@code int}
	 * @return {@link ImageOrText}
	 */
	public static ImageOrText createAxesStyleIcon(int id) {
		SVGResource svgResource;
		switch (id) {
		case EuclidianStyleConstants.AXES_LINE_TYPE_ARROW:
			svgResource = GuiResources.INSTANCE.deco_axes_arrow();
			break;
		case EuclidianStyleConstants.AXES_LINE_TYPE_TWO_ARROWS:
			svgResource = GuiResources.INSTANCE.deco_axes_arrows();
			break;
		case EuclidianStyleConstants.AXES_LINE_TYPE_ARROW_FILLED:
			svgResource = GuiResources.INSTANCE.deco_axes_arrow_filled();
			break;
		case EuclidianStyleConstants.AXES_LINE_TYPE_TWO_ARROWS_FILLED:
			svgResource = GuiResources.INSTANCE.deco_axes_arrows_filled();
			break;
		default:
			svgResource = GuiResources.INSTANCE.deco_axes_none();
		}

		return new ImageOrText(svgResource, 24);
	}
}
