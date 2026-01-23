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

import org.geogebra.common.awt.GColor;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;

/**
 * {@code Property} responsible for setting whether an element uses dynamic colors.
 */
public class DynamicColorModeProperty extends AbstractValuedProperty<Boolean>
		implements BooleanProperty {
	private final GeoElement geoElement;

	/** Constructs the property for the given element with the provided localization. */
	public DynamicColorModeProperty(Localization localization, GeoElement geoElement) {
		super(localization, "");
		this.geoElement = geoElement;
	}

	@Override
	protected void doSetValue(Boolean value) {
		if (value) {
			activateDynamicColorMode(geoElement);
		} else {
			geoElement.removeColorFunction();
			geoElement.updateRepaint();
		}
	}

	/**
	 * Activate dynamic color mode allowing specific color space and color component specification.
	 * @param geoElement the element for which to activate dynamic color mode
	 * @see DynamicColorModeProperty
	 * @see DynamicColorComponentProperty
	 */
	public static void activateDynamicColorMode(GeoElement geoElement) {
		AlgebraProcessor algebraProcessor = geoElement.getKernel().getAlgebraProcessor();
		GColor color = geoElement.getObjectColor();
		GeoList defaultAdvancedColor = geoElement.isFillable()
				? algebraProcessor.evaluateToList("{"
						+ color.getRed() / 255f + ","
						+ color.getGreen() / 255f + ","
						+ color.getBlue() / 255f + ","
						+ geoElement.getFillColor().getAlpha() / 255f + "}")
				: algebraProcessor.evaluateToList("{"
						+ color.getRed() / 255f + ","
						+ color.getGreen() / 255f + ","
						+ color.getBlue() / 255f + "}");
		geoElement.setColorFunction(defaultAdvancedColor);
		defaultAdvancedColor.updateRepaint();
	}

	/**
	 * Checks whether dynamic color mode is active.
	 * @param geoElement the element for which to check if dynamic color mode is active
	 * @return {@code true} if dynamic color mode is active, {@code false} otherwise
	 */
	public static boolean isDynamicColorModeActivated(GeoElement geoElement) {
		return geoElement.getColorFunction() != null;
	}

	@Override
	public Boolean getValue() {
		return isDynamicColorModeActivated(geoElement);
	}
}
