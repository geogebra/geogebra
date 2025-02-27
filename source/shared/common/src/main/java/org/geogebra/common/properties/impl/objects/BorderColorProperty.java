package org.geogebra.common.properties.impl.objects;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInline;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.color.ColorValues;
import org.geogebra.common.main.color.GeoColorValues;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.common.properties.impl.objects.delegate.TextMindmapDelegate;

public class BorderColorProperty extends ElementColorProperty {
	private final GeoElement element;

	/**
	 * @param localization - localization
	 * @param element - element
	 * @throws NotApplicablePropertyException when one of the elements has no color
	 */
	public BorderColorProperty(Localization localization,
			GeoElement element) throws NotApplicablePropertyException {
		super(localization, new TextMindmapDelegate(element), "stylebar.Borders");
		this.element = element;
		setValues(Arrays.stream(GeoColorValues.values()).map(ColorValues::getColor)
				.collect(Collectors.toList()));
	}

	@Override
	public void doSetValue(GColor color) {
		GeoInline geoInline = (GeoInline) element;
		if (!geoInline.getBorderColor().equals(color)) {
			geoInline.setBorderColor(color);
			element.updateVisualStyle(GProperty.LINE_STYLE);
		}
	}

	@Override
	public GColor getValue() {
		GeoInline geoInline = (GeoInline) element;
		return geoInline.getBorderColor();
	}
}
