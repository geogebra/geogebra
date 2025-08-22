package org.geogebra.web.full.euclidian.quickstylebar;

import org.geogebra.common.properties.IconsEnumeratedProperty;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.common.properties.impl.AbstractValuedProperty;
import org.geogebra.common.properties.impl.collections.BooleanPropertyCollection;
import org.geogebra.common.properties.impl.collections.ColorPropertyCollection;
import org.geogebra.common.properties.impl.collections.NamedEnumeratedPropertyCollection;
import org.geogebra.common.properties.impl.collections.RangePropertyCollection;
import org.geogebra.common.properties.impl.collections.StringPropertyCollection;
import org.geogebra.common.properties.impl.objects.BoldProperty;
import org.geogebra.common.properties.impl.objects.BorderColorProperty;
import org.geogebra.common.properties.impl.objects.ImageOpacityProperty;
import org.geogebra.common.properties.impl.objects.ItalicProperty;
import org.geogebra.common.properties.impl.objects.NotesColorWithOpacityProperty;
import org.geogebra.common.properties.impl.objects.ObjectColorProperty;
import org.geogebra.common.properties.impl.objects.TextBackgroundColorProperty;
import org.geogebra.common.properties.impl.objects.TextFontColorProperty;
import org.geogebra.common.properties.impl.objects.TextFontSizeProperty;
import org.geogebra.common.properties.impl.objects.UnderlineProperty;
import org.geogebra.web.full.css.GuiResources;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.css.ToolbarSvgResourcesSync;
import org.geogebra.web.resources.SVGResource;

public class PropertiesIconAdapter {

	/**
	 * Get icon of property
	 * @param propertyResource - property
	 * @return icon of property
	 */
	public static SVGResource getIcon(PropertyResource propertyResource) {
		MaterialDesignResources res = MaterialDesignResources.INSTANCE;
		switch (propertyResource) {
		case ICON_LINE_TYPE_FULL:
			return res.line_solid();
		case ICON_LINE_TYPE_DASHED_DOTTED:
			return res.line_dash_dot();
		case ICON_LINE_TYPE_DASHED_LONG:
			return res.line_dashed_long();
		case ICON_LINE_TYPE_DOTTED:
			return res.line_dotted();
		case ICON_LINE_TYPE_DASHED_SHORT:
			return res.line_dashed_short();
		case ICON_FILLING_HATCHED:
			return res.pattern_hatching();
		case ICON_FILLING_DOTTED:
			return res.pattern_dots();
		case ICON_FILLING_CROSSHATCHED:
			return res.pattern_cross_hatching();
		case ICON_FILLING_HONEYCOMB:
			return res.pattern_honeycomb();
		case ICON_NO_FILLING:
			return res.no_pattern();
		case ICON_ALIGNMENT_LEFT:
			return res.horizontal_align_left();
		case ICON_ALIGNMENT_CENTER:
			return res.horizontal_align_center();
		case ICON_ALIGNMENT_RIGHT:
			return res.horizontal_align_right();
		case ICON_ALIGNMENT_BOTTOM:
			return res.vertical_align_bottom();
		case ICON_ALIGNMENT_MIDDLE:
			return res.vertical_align_middle();
		case ICON_ALIGNMENT_TOP:
			return res.vertical_align_top();
		case ICON_SEGMENT_START_DEFAULT:
			return res.stylingbar_start_default();
		case ICON_SEGMENT_START_LINE:
			return res.stylingbar_start_line();
		case ICON_SEGMENT_START_ARROW:
			return res.stylingbar_start_arrow();
		case ICON_SEGMENT_START_CROWS_FOOT:
			return res.stylingbar_start_crows_foot();
		case ICON_SEGMENT_START_ARROW_OUTLINE:
			return res.stylingbar_start_arrow_outlined();
		case ICON_SEGMENT_START_ARROW_FILLED:
			return res.stylingbar_start_arrow_filled();
		case ICON_SEGMENT_START_CIRCLE_OUTLINE:
			return res.stylingbar_start_circle_outlined();
		case ICON_SEGMENT_START_CIRCLE:
			return res.stylingbar_start_circle();
		case ICON_SEGMENT_START_SQUARE_OUTLINE:
			return res.stylingbar_start_square_outlined();
		case ICON_SEGMENT_START_SQUARE:
			return res.stylingbar_start_square();
		case ICON_SEGMENT_START_DIAMOND_OUTLINE:
			return res.stylingbar_start_diamond_outlined();
		case ICON_SEGMENT_START_DIAMOND:
			return res.stylingbar_start_diamond_filled();
		case ICON_SEGMENT_END_DEFAULT:
			return res.stylingbar_end_default();
		case ICON_SEGMENT_END_LINE:
			return res.stylingbar_end_line();
		case ICON_SEGMENT_END_ARROW:
			return res.stylingbar_end_arrow();
		case ICON_SEGMENT_END_CROWS_FOOT:
			return res.stylingbar_end_crows_foot();
		case ICON_SEGMENT_END_ARROW_OUTLINE:
			return res.stylingbar_end_arrow_outlined();
		case ICON_SEGMENT_END_ARROW_FILLED:
			return res.stylingbar_end_arrow_filled();
		case ICON_SEGMENT_END_CIRCLE_OUTLINE:
			return res.stylingbar_end_circle_outlined();
		case ICON_SEGMENT_END_CIRCLE:
			return res.stylingbar_end_circle();
		case ICON_SEGMENT_END_SQUARE_OUTLINE:
			return res.stylingbar_end_square_outlined();
		case ICON_SEGMENT_END_SQUARE:
			return res.stylingbar_end_square();
		case ICON_SEGMENT_END_DIAMOND_OUTLINE:
			return res.stylingbar_end_diamond_outlined();
		case ICON_SEGMENT_END_DIAMOND:
			return res.stylingbar_end_diamond_filled();
		case ICON_CELL_BORDER_ALL:
			return res.border_all();
		case ICON_CELL_BORDER_INNER:
			return res.border_inner();
		case ICON_CELL_BORDER_OUTER:
			return res.border_outer();
		case ICON_CELL_BORDER_NONE:
			return res.border_clear();
		case ICON_POINT_STYLE_DOT:
			return res.point_full();
		case ICON_POINT_STYLE_CIRCLE:
			return res.point_empty();
		case ICON_POINT_STYLE_FILLED_DIAMOND:
			return res.point_diamond();
		case ICON_POINT_STYLE_EMPTY_DIAMOND:
			return res.point_diamond_empty();
		case ICON_POINT_STYLE_CROSS:
			return res.point_cross_diag();
		case ICON_POINT_STYLE_PLUS:
			return res.point_cross();
		case ICON_POINT_STYLE_NO_OUTLINE:
			return res.point_no_outline();
		case ICON_POINT_STYLE_TRIANGLE_NORTH:
			return res.point_up();
		case ICON_POINT_STYLE_TRIANGLE_SOUTH:
			return res.point_down();
		case ICON_POINT_STYLE_TRIANGLE_WEST:
			return res.point_left();
		case ICON_POINT_STYLE_TRIANGLE_EAST:
			return res.point_right();
		case ICON_CARTESIAN:
			return res.grid_black();
		case ICON_CARTESIAN_MINOR:
			return res.minor_gridlines();
		case ICON_POLAR:
			return res.grid_polar();
		case ICON_ISOMETRIC:
			return res.grid_isometric();
		case ICON_DOTS:
			return res.pattern_dots();
		case ICON_AXES_BOLD:
			return res.text_bold_black();
		case ICON_AXES_ITALIC:
			return res.text_italic_black();
		case ICON_AXES_SERIF:
			return res.text_serif_black();
		case ICON_AXES_LINE_TYPE_FULL:
			return GuiResources.INSTANCE.deco_axes_none();
		case ICON_AXES_LINE_TYPE_ARROW:
			return GuiResources.INSTANCE.deco_axes_arrow();
		case ICON_AXES_LINE_TYPE_ARROW_FILLED:
			return GuiResources.INSTANCE.deco_axes_arrow_filled();
		case ICON_AXES_LINE_TYPE_TWO_ARROWS:
			return GuiResources.INSTANCE.deco_axes_arrows();
		case ICON_AXES_LINE_TYPE_TWO_ARROWS_FILLED:
			return GuiResources.INSTANCE.deco_axes_arrows_filled();
		}
		return res.stylebar_empty();
	}

	/**
	 * @param property - property
	 * @return icon based on property
	 */
	public static SVGResource getIcon(Property property) {
		if (property instanceof IconsEnumeratedProperty<?>) {
			PropertyResource[] propertyIcons = ((IconsEnumeratedProperty<?>)
					property).getValueIcons();
			int selectedIndex = ((IconsEnumeratedProperty<?>) property).getIndex();
			return selectedIndex == -1 ? PropertiesIconAdapter.getIcon(propertyIcons[0])
					: PropertiesIconAdapter.getIcon(propertyIcons[selectedIndex]);
		} else if (property instanceof RangePropertyCollection<?>
				&& ((RangePropertyCollection<?>) property).getFirstProperty()
				instanceof ImageOpacityProperty) {
			return MaterialDesignResources.INSTANCE.opacity_black();
		} else if (property instanceof BooleanPropertyCollection<?>) {
			Property firstProperty = ((BooleanPropertyCollection<?>) property).getFirstProperty();
			if (firstProperty instanceof BoldProperty) {
				return MaterialDesignResources.INSTANCE.text_bold_black();
			} else if (firstProperty instanceof ItalicProperty) {
				return MaterialDesignResources.INSTANCE.text_italic_black();
			} else if (firstProperty instanceof UnderlineProperty) {
				return MaterialDesignResources.INSTANCE.text_underline_black();
			}
		} else if (property instanceof NamedEnumeratedPropertyCollection
			&& ((NamedEnumeratedPropertyCollection<?, ?>) property).getFirstProperty()
			instanceof TextFontSizeProperty) {
			return MaterialDesignResources.INSTANCE.text_size_black();
		} else if (property instanceof ColorPropertyCollection) {
			AbstractValuedProperty<?> firstProperty = ((ColorPropertyCollection<?>) property)
					.getFirstProperty();
			if (firstProperty instanceof ObjectColorProperty
				|| firstProperty instanceof TextBackgroundColorProperty
				|| firstProperty instanceof NotesColorWithOpacityProperty) {
				return MaterialDesignResources.INSTANCE.color_black();
			} else if (firstProperty instanceof TextFontColorProperty) {
				return MaterialDesignResources.INSTANCE.text_color();
			} else if (firstProperty instanceof BorderColorProperty) {
				return MaterialDesignResources.INSTANCE.color_border();
			}
		} else if (property instanceof StringPropertyCollection<?>) {
			return ToolbarSvgResourcesSync.INSTANCE.mode_showhidelabel_32();
		}

		return MaterialDesignResources.INSTANCE.stylebar_empty();
	}
}
