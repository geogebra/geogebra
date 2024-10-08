package org.geogebra.web.full.euclidian.quickstylebar;

import org.geogebra.common.properties.IconsEnumeratedProperty;
import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.common.properties.impl.collections.BooleanPropertyCollection;
import org.geogebra.common.properties.impl.collections.RangePropertyCollection;
import org.geogebra.common.properties.impl.objects.BoldProperty;
import org.geogebra.common.properties.impl.objects.ImageOpacityProperty;
import org.geogebra.common.properties.impl.objects.ItalicProperty;
import org.geogebra.common.properties.impl.objects.UnderlineProperty;
import org.geogebra.web.full.css.MaterialDesignResources;
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
		case ICON_SEGMENT_START_SQUARE_OUTLINE:
			return res.stylingbar_start_square_outlined();
		case ICON_SEGMENT_START_SQUARE:
			return res.stylingbar_start_square();
		case ICON_SEGMENT_START_ARROW:
			return res.stylingbar_start_arrow();
		case ICON_SEGMENT_START_ARROW_FILLED:
			return res.stylingbar_start_arrow_filled();
		case ICON_SEGMENT_START_CIRCLE_OUTLINE:
			return res.stylingbar_start_circle_outlined();
		case ICON_SEGMENT_START_CIRCLE:
			return res.stylingbar_start_circle();
		case ICON_SEGMENT_END_DEFAULT:
			return res.stylingbar_end_default();
		case ICON_SEGMENT_END_LINE:
			return res.stylingbar_end_line();
		case ICON_SEGMENT_END_SQUARE_OUTLINE:
			return res.stylingbar_end_square_outlined();
		case ICON_SEGMENT_END_SQUARE:
			return res.stylingbar_end_square();
		case ICON_SEGMENT_END_ARROW:
			return res.stylingbar_end_arrow();
		case ICON_SEGMENT_END_ARROW_FILLED:
			return res.stylingbar_end_arrow_filled();
		case ICON_SEGMENT_END_CIRCLE_OUTLINE:
			return res.stylingbar_end_circle_outlined();
		case ICON_SEGMENT_END_CIRCLE:
			return res.stylingbar_end_circle();
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
			return PropertiesIconAdapter.getIcon(propertyIcons[
					((IconsEnumeratedProperty<?>) property).getIndex()]);
		} else if (property instanceof RangePropertyCollection<?, ?>
				&& ((RangePropertyCollection<?, ?>) property).getProperties()[0]
				instanceof ImageOpacityProperty) {
			return MaterialDesignResources.INSTANCE.opacity_black();
		} else if (property instanceof BooleanPropertyCollection<?>) {
			Property firstProperty = ((BooleanPropertyCollection<?>) property).getProperties()[0];
			if (firstProperty instanceof BoldProperty) {
				return MaterialDesignResources.INSTANCE.text_bold_black();
			} else if (firstProperty instanceof ItalicProperty) {
				return MaterialDesignResources.INSTANCE.text_italic_black();
			} else if (firstProperty instanceof UnderlineProperty) {
				return MaterialDesignResources.INSTANCE.text_underline_black();
			}
		}
		return MaterialDesignResources.INSTANCE.stylebar_empty();
	}
}
