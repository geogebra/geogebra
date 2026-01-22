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
 * See https://www.geogebra.org/license for full licensing details'
 */

package org.geogebra.common.properties.impl.objects;

import static org.geogebra.common.kernel.geos.GeoElement.COLORSPACE_HSB;
import static org.geogebra.common.kernel.geos.GeoElement.COLORSPACE_HSL;
import static org.geogebra.common.kernel.geos.GeoElement.COLORSPACE_RGB;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.CheckForNull;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.parser.ParseException;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.aliases.StringProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;

/**
 * {@code Property} responsible for setting the component of an advanced color for an object.
 */
public final class DynamicColorComponentProperty extends AbstractValuedProperty<String>
		implements StringProperty, GeoElementDependentProperty {
	private final GeoElement geoElement;
	private final Integer requiredColorSpace;
	private final int colorIndex;
	private final boolean useColorTranslationKey;

	private DynamicColorComponentProperty(Localization localization, boolean useColorTranslationKey,
			GeoElement geoElement, String name, Integer requiredColorSpace, int colorIndex) {
		super(localization, name);
		this.geoElement = geoElement;
		this.requiredColorSpace = requiredColorSpace;
		this.colorIndex = colorIndex;
		this.useColorTranslationKey = useColorTranslationKey;
	}

	/**
	 * Constructs the property for the red color component
	 * for the given element with the provided localization.
	 * @return a color component property for red
	 */
	public static DynamicColorComponentProperty forRed(
			Localization localization, GeoElement geoElement) {
		return new DynamicColorComponentProperty(localization, true, geoElement,
				"red", COLORSPACE_RGB, 0);
	}

	/**
	 * Constructs the property for the green color component
	 * for the given element with the provided localization.
	 * @return a color component property for green
	 */
	public static DynamicColorComponentProperty forGreen(
			Localization localization, GeoElement geoElement) {
		return new DynamicColorComponentProperty(localization, true, geoElement,
				"green", COLORSPACE_RGB, 1);
	}

	/**
	 * Constructs the property for the blue color component
	 * for the given element with the provided localization.
	 * @return a color component property for blue
	 */
	public static DynamicColorComponentProperty forBlue(
			Localization localization, GeoElement geoElement) {
		return new DynamicColorComponentProperty(localization, true, geoElement,
				"blue", COLORSPACE_RGB, 2);
	}

	/**
	 * Constructs the property for the hue color component
	 * for the given element in HSL color space with the provided localization.
	 * @return a color component property for hue
	 */
	public static DynamicColorComponentProperty forHueHSL(
			Localization localization, GeoElement geoElement) {
		return new DynamicColorComponentProperty(localization, false, geoElement,
				"Hue", COLORSPACE_HSL, 0);
	}

	/**
	 * Constructs the property for the hue color component
	 * for the given element in HSB color space with the provided localization.
	 * @return a color component property for hue
	 */
	public static DynamicColorComponentProperty forHueHSB(
			Localization localization, GeoElement geoElement) {
		return new DynamicColorComponentProperty(localization, false, geoElement,
				"Hue", COLORSPACE_HSB, 0);
	}

	/**
	 * Constructs the property for the saturation color component
	 * for the given element in HSL color space with the provided localization.
	 * @return a color component property for saturation
	 */
	public static DynamicColorComponentProperty forSaturationHSL(
			Localization localization, GeoElement geoElement) {
		return new DynamicColorComponentProperty(localization, false, geoElement,
				"Saturation", COLORSPACE_HSL, 1);
	}

	/**
	 * Constructs the property for the saturation color component
	 * for the given element in HSB color space with the provided localization.
	 * @return a color component property for saturation
	 */
	public static DynamicColorComponentProperty forSaturationHSB(
			Localization localization, GeoElement geoElement) {
		return new DynamicColorComponentProperty(localization, false, geoElement,
				"Saturation", COLORSPACE_HSB, 1);
	}

	/**
	 * Constructs the property for the brightness color component
	 * for the given element with the provided localization.
	 * @return a color component property for value
	 */
	public static DynamicColorComponentProperty forBrightness(
			Localization localization, GeoElement geoElement) {
		return new DynamicColorComponentProperty(localization, false, geoElement,
				"Value", COLORSPACE_HSB, 2);
	}

	/**
	 * Constructs the property for the lightness color component
	 * for the given element with the provided localization.
	 * @return a color component property for lightness
	 */
	public static DynamicColorComponentProperty forLightness(
			Localization localization, GeoElement geoElement) {
		return new DynamicColorComponentProperty(localization, false, geoElement,
				"Lightness", COLORSPACE_HSL, 2);
	}

	/**
	 * Constructs the property for the opacity color component
	 * for the given element with the provided localization.
	 * @return a color component property for opacity
	 */
	public static DynamicColorComponentProperty forOpacity(
			Localization localization, GeoElement geoElement) {
		return new DynamicColorComponentProperty(localization, false, geoElement,
				"Opacity", null, 3);
	}

	@Override
	public @CheckForNull String validateValue(String value) {
		try {
			ValidExpression validExpression = geoElement.getKernel().getParser()
					.parseGeoGebraExpression(value);
			if (!validExpression.evaluatesToNumber(false)) {
				return getLocalization().getError("NumberExpected");
			}
			return null;
		} catch (ParseException parseException) {
			return parseException.getLocalizedMessage();
		}
	}

	@Override
	protected void doSetValue(String value) {
		if (!DynamicColorModeProperty.isDynamicColorModeActivated(geoElement)) {
			DynamicColorModeProperty.activateDynamicColorMode(geoElement);
		}
		if (requiredColorSpace != null && requiredColorSpace != geoElement.getColorSpace()) {
			geoElement.setColorSpace(requiredColorSpace);
		}
		GeoList currentColorComponentValues = geoElement.getColorFunction();
		List<String> colorComponentValues = currentColorComponentValues.elements()
				.map(element -> element.getLabel(StringTemplate.editTemplate))
				.collect(Collectors.toList());
		colorComponentValues.set(colorIndex, value);
		String newAdvancedColorInput = "{" + String.join(",", colorComponentValues) + "}";
		GeoList newAdvancedColorComponents = geoElement.getKernel().getAlgebraProcessor()
				.evaluateToList(newAdvancedColorInput);
		geoElement.setColorFunction(newAdvancedColorComponents);
		geoElement.updateRepaint();
		newAdvancedColorComponents.updateRepaint();
	}

	@Override
	public String getValue() {
		GeoList colorList = geoElement.getColorFunction();
		if (colorList == null) {
			return "";
		}
		if (colorIndex == 3 && colorList.size() != 4) {
			return "";
		}
		return colorList.get(colorIndex).getLabel(StringTemplate.editTemplate);
	}

	@Override
	public boolean isAvailable() {
		if (requiredColorSpace != null && requiredColorSpace != geoElement.getColorSpace()) {
			return false;
		}
		if (colorIndex == 3) { // Opacity is available only for fillable objects
			return geoElement.isFillable();
		}
		return true;
	}

	@Override
	public GeoElement getGeoElement() {
		return geoElement;
	}

	@Override
	public String getName() {
		return useColorTranslationKey ? getLocalization().getColor(getRawName()) : super.getName();
	}
}