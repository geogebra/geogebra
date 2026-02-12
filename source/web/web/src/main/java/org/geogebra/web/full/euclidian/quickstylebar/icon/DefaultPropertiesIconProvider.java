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

import org.geogebra.common.properties.IconAssociatedProperty;
import org.geogebra.common.properties.IconsEnumeratedProperty;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.common.properties.impl.facade.AbstractPropertyListFacade;
import org.geogebra.common.properties.impl.objects.BorderColorProperty;
import org.geogebra.common.properties.impl.objects.FontSizeProperty;
import org.geogebra.common.properties.impl.objects.ImageOpacityProperty;
import org.geogebra.common.properties.impl.objects.NameCaptionProperty;
import org.geogebra.common.properties.impl.objects.NotesColorWithOpacityProperty;
import org.geogebra.common.properties.impl.objects.OldObjectColorProperty;
import org.geogebra.common.properties.impl.objects.TextBackgroundColorProperty;
import org.geogebra.common.properties.impl.objects.TextFontColorProperty;
import org.geogebra.web.full.css.GuiResources;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.css.ToolbarSvgResourcesSync;
import org.geogebra.web.html5.gui.view.IconSpec;
import org.geogebra.web.html5.gui.view.ImageIconSpec;
import org.geogebra.web.resources.SVGResource;

public class DefaultPropertiesIconProvider implements PropertiesIconProvider {

	/**
	 * Get icon of property
	 * @param propertyResource - property
	 * @return icon of property
	 */
	private SVGResource getIcon(PropertyResource propertyResource) {
		MaterialDesignResources res = MaterialDesignResources.INSTANCE;
		return switch (propertyResource) {
			case ICON_LINE_TYPE_FULL -> res.line_solid();
			case ICON_LINE_TYPE_DASHED_DOTTED -> res.line_dash_dot();
			case ICON_LINE_TYPE_DASHED_LONG -> res.line_dashed_long();
			case ICON_LINE_TYPE_DOTTED -> res.line_dotted();
			case ICON_LINE_TYPE_DASHED_SHORT -> res.line_dashed_short();
			case ICON_FILLING_HATCHED -> res.pattern_hatching();
			case ICON_FILLING_DOTTED -> res.pattern_dots();
			case ICON_FILLING_CROSSHATCHED -> res.pattern_cross_hatching();
			case ICON_FILLING_HONEYCOMB -> res.pattern_honeycomb();
			case ICON_NO_FILLING -> res.no_pattern();
			case ICON_ALIGNMENT_LEFT -> res.horizontal_align_left();
			case ICON_ALIGNMENT_CENTER -> res.horizontal_align_center();
			case ICON_ALIGNMENT_RIGHT -> res.horizontal_align_right();
			case ICON_ALIGNMENT_BOTTOM -> res.vertical_align_bottom();
			case ICON_ALIGNMENT_MIDDLE -> res.vertical_align_middle();
			case ICON_ALIGNMENT_TOP -> res.vertical_align_top();
			case ICON_SEGMENT_START_DEFAULT -> res.stylingbar_start_default();
			case ICON_SEGMENT_START_LINE -> res.stylingbar_start_line();
			case ICON_SEGMENT_START_ARROW -> res.stylingbar_start_arrow();
			case ICON_SEGMENT_START_CROWS_FOOT -> res.stylingbar_start_crows_foot();
			case ICON_SEGMENT_START_ARROW_OUTLINE -> res.stylingbar_start_arrow_outlined();
			case ICON_SEGMENT_START_ARROW_FILLED -> res.stylingbar_start_arrow_filled();
			case ICON_SEGMENT_START_CIRCLE_OUTLINE -> res.stylingbar_start_circle_outlined();
			case ICON_SEGMENT_START_CIRCLE -> res.stylingbar_start_circle();
			case ICON_SEGMENT_START_SQUARE_OUTLINE -> res.stylingbar_start_square_outlined();
			case ICON_SEGMENT_START_SQUARE -> res.stylingbar_start_square();
			case ICON_SEGMENT_START_DIAMOND_OUTLINE -> res.stylingbar_start_diamond_outlined();
			case ICON_SEGMENT_START_DIAMOND -> res.stylingbar_start_diamond_filled();
			case ICON_SEGMENT_END_DEFAULT -> res.stylingbar_end_default();
			case ICON_SEGMENT_END_LINE -> res.stylingbar_end_line();
			case ICON_SEGMENT_END_ARROW -> res.stylingbar_end_arrow();
			case ICON_SEGMENT_END_CROWS_FOOT -> res.stylingbar_end_crows_foot();
			case ICON_SEGMENT_END_ARROW_OUTLINE -> res.stylingbar_end_arrow_outlined();
			case ICON_SEGMENT_END_ARROW_FILLED -> res.stylingbar_end_arrow_filled();
			case ICON_SEGMENT_END_CIRCLE_OUTLINE -> res.stylingbar_end_circle_outlined();
			case ICON_SEGMENT_END_CIRCLE -> res.stylingbar_end_circle();
			case ICON_SEGMENT_END_SQUARE_OUTLINE -> res.stylingbar_end_square_outlined();
			case ICON_SEGMENT_END_SQUARE -> res.stylingbar_end_square();
			case ICON_SEGMENT_END_DIAMOND_OUTLINE -> res.stylingbar_end_diamond_outlined();
			case ICON_SEGMENT_END_DIAMOND -> res.stylingbar_end_diamond_filled();
			case ICON_CELL_BORDER_ALL -> res.border_all();
			case ICON_CELL_BORDER_INNER -> res.border_inner();
			case ICON_CELL_BORDER_OUTER -> res.border_outer();
			case ICON_CELL_BORDER_NONE -> res.border_clear();
			case ICON_POINT_STYLE_DOT -> res.point_full();
			case ICON_POINT_STYLE_CIRCLE -> res.point_empty();
			case ICON_POINT_STYLE_FILLED_DIAMOND -> res.point_diamond();
			case ICON_POINT_STYLE_EMPTY_DIAMOND -> res.point_diamond_empty();
			case ICON_POINT_STYLE_CROSS -> res.point_cross_diag();
			case ICON_POINT_STYLE_PLUS -> res.point_cross();
			case ICON_POINT_STYLE_NO_OUTLINE -> res.point_no_outline();
			case ICON_POINT_STYLE_TRIANGLE_NORTH -> res.point_up();
			case ICON_POINT_STYLE_TRIANGLE_SOUTH -> res.point_down();
			case ICON_POINT_STYLE_TRIANGLE_WEST -> res.point_left();
			case ICON_POINT_STYLE_TRIANGLE_EAST -> res.point_right();
			case ICON_CARTESIAN -> res.grid_black();
			case ICON_CARTESIAN_MINOR -> res.minor_gridlines();
			case ICON_POLAR -> res.grid_polar();
			case ICON_ISOMETRIC -> res.grid_isometric();
			case ICON_DOTS -> res.pattern_dots();
			case ICON_PROJECTION_PARALLEL -> res.projection_orthographic();
			case ICON_PROJECTION_PERSPECTIVE -> res.projection_perspective();
			case ICON_PROJECTION_GLASSES -> res.projection_glasses();
			case ICON_PROJECTION_OBLIQUE -> res.projection_oblique();
			case ICON_BOLD -> res.text_bold_black();
			case ICON_ITALIC -> res.text_italic_black();
			case ICON_UNDERLINE -> res.text_underline_black();
			case ICON_SERIF -> res.text_serif_black();
			case ICON_CLEAR_COLOR -> res.no_color();
			case ICON_BORDER_THIN -> res.color_border();
			case ICON_AXES_LINE_TYPE_FULL -> GuiResources.INSTANCE.deco_axes_none();
			case ICON_AXES_LINE_TYPE_ARROW -> GuiResources.INSTANCE.deco_axes_arrow();
			case ICON_AXES_LINE_TYPE_ARROW_FILLED -> GuiResources.INSTANCE.deco_axes_arrow_filled();
			case ICON_AXES_LINE_TYPE_TWO_ARROWS -> GuiResources.INSTANCE.deco_axes_arrows();
			case ICON_AXES_LINE_TYPE_TWO_ARROWS_FILLED ->
					GuiResources.INSTANCE.deco_axes_arrows_filled();
			case ICON_RIGHT_ANGLE_STYLE_NONE -> res.right_angle_style_off();
			case ICON_RIGHT_ANGLE_STYLE_SQUARE -> res.right_angle_style_rectangle();
			case ICON_RIGHT_ANGLE_STYLE_DOT -> res.right_angle_style_dot();
			case ICON_RIGHT_ANGLE_STYLE_L -> res.right_angle_style_stroke();
			case ICON_AXIS_TICK_MAJOR -> res.axis_tick_major();
			case ICON_AXIS_TICK_MAJOR_AND_MINOR -> res.axis_tick_major_minor();
			case ICON_AXIS_TICK_OFF -> res.axis_tick_off();
			case ICON_VIEW_DIRECTION_XY -> res.viewXY();
			case ICON_VIEW_DIRECTION_XZ -> res.viewXZ();
			case ICON_VIEW_DIRECTION_YZ -> res.viewYZ();
			case ICON_SEGMENT_DECO_1STROKE -> GuiResources.INSTANCE.deco_segment_1stroke();
			case ICON_SEGMENT_DECO_2STROKES -> GuiResources.INSTANCE.deco_segment_2strokes();
			case ICON_SEGMENT_DECO_3STROKES -> GuiResources.INSTANCE.deco_segment_3strokes();
			case ICON_SEGMENT_DECO_1ARROW -> GuiResources.INSTANCE.deco_segment_1arrow();
			case ICON_SEGMENT_DECO_2ARROWS -> GuiResources.INSTANCE.deco_segment_2arrows();
			case ICON_SEGMENT_DECO_3ARROWS -> GuiResources.INSTANCE.deco_segment_3arrows();
			case ICON_SEGMENT_DECO_NONE -> GuiResources.INSTANCE.deco_segment_none();
			case ICON_ANGLE_DECO_TWO_ARCS -> GuiResources.INSTANCE.deco_angle_2lines();
			case ICON_ANGLE_DECO_THREE_ARCS -> GuiResources.INSTANCE.deco_angle_3lines();
			case ICON_ANGLE_DECO_ONE_TICK -> GuiResources.INSTANCE.deco_angle_1stroke();
			case ICON_ANGLE_DECO_TWO_TICKS -> GuiResources.INSTANCE.deco_angle_2strokes();
			case ICON_ANGLE_DECO_THREE_TICKS -> GuiResources.INSTANCE.deco_angle_3strokes();
			case ICON_ANGLE_DECO_ARROW_ANTICLOCKWISE -> GuiResources.INSTANCE.deco_angle_arrow_up();
			case ICON_ANGLE_DECO_ARROW_CLOCKWISE -> GuiResources.INSTANCE.deco_angle_arrow_down();
			case ICON_ANGLE_DECO_NONE -> GuiResources.INSTANCE.deco_angle_1line();
			case ICON_VECTOR_DECO_ARROW ->
					MaterialDesignResources.INSTANCE.stylingbar_end_arrow_filled();
			case ICON_VECTOR_DECO_DEFAULT ->
					MaterialDesignResources.INSTANCE.stylingbar_end_arrow();
			case ICON_FILLING_IMAGE -> res.export_image_black(); // TODO
			case ICON_FILLING_BRICK, ICON_FILLING_SYMBOL, ICON_FILLING_WEAVING,
				 ICON_FILLING_CHESSBOARD -> res.pattern_hatching();
			default -> res.stylebar_empty();
		};
	}

	@Override
	public final IconSpec matchIconWithResource(Property property) {
		if (property instanceof IconsEnumeratedProperty<?> enumeratedProperty) {
			PropertyResource[] propertyIcons = enumeratedProperty.getValueIcons();
			int selectedIndex = enumeratedProperty.getIndex();
			return selectedIndex == -1 ? matchIconWithResource(propertyIcons[0])
					: matchIconWithResource(propertyIcons[selectedIndex]);
		} else if (property instanceof AbstractPropertyListFacade<?> listFacade) {
			Property firstProperty = listFacade.getFirstProperty();
			if (firstProperty instanceof ImageOpacityProperty) {
				return getOpacityIcon();
			} else if (firstProperty instanceof IconAssociatedProperty iconProperty) {
				return matchIconWithResource(iconProperty.getIcon());
			} else if (firstProperty instanceof FontSizeProperty) {
				return getTextSizeIcon();
			} else if (firstProperty instanceof OldObjectColorProperty
					|| firstProperty instanceof TextBackgroundColorProperty
					|| firstProperty instanceof NotesColorWithOpacityProperty) {
				return getColorIcon();
			} else if (firstProperty instanceof TextFontColorProperty) {
				return getTextColorIcon();
			} else if (firstProperty instanceof BorderColorProperty) {
				return matchIconWithResource(PropertyResource.ICON_BORDER_THIN);
			} else if (firstProperty instanceof NameCaptionProperty) {
				return new ImageIconSpec(ToolbarSvgResourcesSync.INSTANCE.mode_showhidelabel_32());
			}
		}

		return new ImageIconSpec(MaterialDesignResources.INSTANCE.stylebar_empty());
	}

	protected IconSpec getTextSizeIcon() {
		return new ImageIconSpec(MaterialDesignResources.INSTANCE.text_size_black());
	}

	protected IconSpec getOpacityIcon() {
		return new ImageIconSpec(MaterialDesignResources.INSTANCE.opacity_black());
	}

	protected IconSpec getTextColorIcon() {
		return new ImageIconSpec(MaterialDesignResources.INSTANCE.text_color());
	}

	protected IconSpec getColorIcon() {
		return new ImageIconSpec(MaterialDesignResources.INSTANCE.color_black());
	}

	@Override
	public IconSpec matchIconWithResource(PropertyResource propertyResource) {
		return new ImageIconSpec(getIcon(propertyResource));
	}
}
