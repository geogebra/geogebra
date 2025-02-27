package org.geogebra.common.properties.impl.objects;

import static java.util.Map.entry;
import static org.geogebra.common.kernel.geos.properties.TextFontSize.EXTRA_LARGE;
import static org.geogebra.common.kernel.geos.properties.TextFontSize.EXTRA_SMALL;
import static org.geogebra.common.kernel.geos.properties.TextFontSize.LARGE;
import static org.geogebra.common.kernel.geos.properties.TextFontSize.MEDIUM;
import static org.geogebra.common.kernel.geos.properties.TextFontSize.SMALL;
import static org.geogebra.common.kernel.geos.properties.TextFontSize.VERY_LARGE;
import static org.geogebra.common.kernel.geos.properties.TextFontSize.VERY_SMALL;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.geos.HasTextFormatter;
import org.geogebra.common.kernel.geos.TextProperties;
import org.geogebra.common.kernel.geos.TextStyle;
import org.geogebra.common.kernel.geos.properties.TextFontSize;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractNamedEnumeratedProperty;
import org.geogebra.common.properties.impl.objects.delegate.FontStyleDelegate;
import org.geogebra.common.properties.impl.objects.delegate.GeoElementDelegate;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

public class TextFontSizeProperty extends AbstractNamedEnumeratedProperty<TextFontSize> {
	private final List<TextFontSize> fontSizes = Arrays.asList(
			EXTRA_SMALL, VERY_SMALL, SMALL, MEDIUM, LARGE, VERY_LARGE, EXTRA_LARGE);
	private final GeoElementDelegate delegate;
	private final EuclidianView ev;

	/**
	 * Text font size property
	 * @param localization localization
	 * @param element geo element
	 * @param ev euclidian view
	 */
	public TextFontSizeProperty(Localization localization, GeoElement element, EuclidianView ev)
			throws NotApplicablePropertyException {
		super(localization, "FontSize");
		delegate = new FontStyleDelegate(element);
		this.ev = ev;
		setValues(fontSizes);
		setNamedValues(List.of(
				entry(EXTRA_SMALL, EXTRA_SMALL.getName()),
				entry(VERY_SMALL, VERY_SMALL.getName()),
				entry(SMALL, SMALL.getName()),
				entry(MEDIUM, MEDIUM.getName()),
				entry(LARGE, LARGE.getName()),
				entry(VERY_LARGE, VERY_LARGE.getName()),
				entry(EXTRA_LARGE, EXTRA_LARGE.getName())
		));
	}

	@Override
	protected void doSetValue(TextFontSize value) {
		GeoElement element = delegate.getElement();
		double size = GeoText.getRelativeFontSize(fontSizes.indexOf(value));
		if (element instanceof TextProperties && ((TextProperties) element)
				.getFontSizeMultiplier() != size) {
			((TextProperties) element).setFontSizeMultiplier(size);
		} else if (element instanceof HasTextFormatter) {
			size = size * ev.getFontSize();
			((HasTextFormatter) element).getFormatter().format("size", size);
		}
		element.updateVisualStyleRepaint(GProperty.FONT);
	}

	@Override
	public TextFontSize getValue() {
		GeoElement element = delegate.getElement();
		return fontSizes.get(GeoText.getFontSizeIndex(
				((TextStyle) element).getFontSizeMultiplier()));
	}
}
