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

package org.geogebra.common.properties.impl.objects;

import static java.util.Map.entry;

import java.util.Arrays;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.HasTextFormatter;
import org.geogebra.common.kernel.geos.TextProperties;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractNamedEnumeratedProperty;
import org.geogebra.common.properties.impl.objects.FontSizeProperty.FontSize;
import org.geogebra.common.properties.impl.objects.delegate.FontStyleDelegate;
import org.geogebra.common.properties.impl.objects.delegate.GeoElementDelegate;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

/**
 * {@code Property} responsible for setting the font size of texts.
 */
public class FontSizeProperty extends AbstractNamedEnumeratedProperty<FontSize>
		implements GeoElementDependentProperty {
	/**
	 * Possible values for font sizes.
	 */
	public enum FontSize {
		EXTRA_SMALL("ExtraSmall", 0.5),
		VERY_SMALL("VerySmall", 0.7),
		SMALL("Small", 1.0),
		MEDIUM("Medium", 1.4),
		LARGE("Large", 2.0),
		VERY_LARGE("VeryLarge", 4.0),
		EXTRA_LARGE("ExtraLarge", 8.0);

		private final String translationKey;
		private final double multiplier;

		FontSize(String translationKey, double multiplier) {
			this.translationKey = translationKey;
			this.multiplier = multiplier;
		}

		/**
		 * Determines the {@code FontSize} corresponding to the font size multiplier.
		 * @param multiplier the font size multiplier to match
		 * @return the matching {@code FontSize}
		 */
		public static @Nonnull FontSize withFontSizeMultiplier(double multiplier) {
			// If the multiplier is below the minimum value, use the smallest font size
			if (multiplier <= EXTRA_SMALL.multiplier) {
				return EXTRA_SMALL;
			}
			for (int fontSizeIndex = 1; fontSizeIndex < FontSize.values().length; fontSizeIndex++) {
				FontSize previousFontSize = values()[fontSizeIndex];
				FontSize currentFontSize = values()[fontSizeIndex];
				// Find the two neighboring font sizes between which this multiplier falls
				if (multiplier <= currentFontSize.multiplier) {
					// Return the nearest neighbouring font size
					return (previousFontSize.multiplier + currentFontSize.multiplier) / 2.0
							< multiplier ? currentFontSize : previousFontSize;
				}
			}
			// If the multiplier is above the maximum value, use the largest font size
			return EXTRA_LARGE;
		}

		/**
		 * @return the translation key associated with this font size
		 */
		public @Nonnull String getTranslationKey() {
			return translationKey;
		}

		/**
		 * @return the relative font size
		 */
		public double getMultiplier() {
			return multiplier;
		}
	}

	private final GeoElementDelegate delegate;

	/**
	 * Constructs the property for the given element.
	 * @param localization localization for translating the property name
	 * @param element the element to create the property for
	 * @throws NotApplicablePropertyException if the property is not applicable for the given element
	 */
	public FontSizeProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "FontSize");
		this.delegate = new FontStyleDelegate(element);
		setNamedValues(Arrays.stream(FontSize.values())
				.map(fontSize -> entry(fontSize, fontSize.getTranslationKey()))
				.collect(Collectors.toList()));
	}

	@Override
	protected void doSetValue(FontSize fontSize) {
		GeoElement element = delegate.getElement();
		if (element instanceof TextProperties textProperties
				&& textProperties.getFontSizeMultiplier() != fontSize.multiplier) {
			textProperties.setFontSizeMultiplier(fontSize.multiplier);
		} else if (element instanceof HasTextFormatter hasTextFormatter) {
			hasTextFormatter.format("size", fontSize.multiplier * getBaseFontSize());
		}
		element.updateVisualStyleRepaint(GProperty.FONT);
	}

	@Override
	public FontSize getValue() {
		GeoElement element = delegate.getElement();
		if (element instanceof HasTextFormatter hasTextFormatter) {
			double multiplier = hasTextFormatter.getFormat("size", 0d) / getBaseFontSize();
			return FontSize.withFontSizeMultiplier(multiplier);
		} else if (element instanceof TextProperties textStyle) {
			return FontSize.withFontSizeMultiplier(textStyle.getFontSizeMultiplier());
		}
		return null;
	}

	@Override
	public GeoElement getGeoElement() {
		return delegate.getElement();
	}

	private double getBaseFontSize() {
		// dependency on getApp will be removed when inline text size is switched to absolute
		return delegate.getElement().getApp().getFontSize();
	}
}
