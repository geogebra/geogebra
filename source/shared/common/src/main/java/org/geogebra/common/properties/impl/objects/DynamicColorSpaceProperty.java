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

import java.util.List;
import java.util.Map;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractNamedEnumeratedProperty;

/**
 * {@code Property} responsible for setting the color space to be used for defining the color of the given object.
 */
public class DynamicColorSpaceProperty extends AbstractNamedEnumeratedProperty<Integer> {
	private static final List<Map.Entry<Integer, String>> colorSpaceValues = List.of(
			entry(GeoElement.COLORSPACE_RGB, "RGB"),
			entry(GeoElement.COLORSPACE_HSB, "HSV"),
			entry(GeoElement.COLORSPACE_HSL, "HSL"));

	private final GeoElement geoElement;

	/** Constructs the property for the given element with the provided localization. */
	public DynamicColorSpaceProperty(Localization localization, GeoElement geoElement) {
		super(localization, "");
		this.geoElement = geoElement;
		setNamedValues(colorSpaceValues);
	}

	@Override
	protected void doSetValue(Integer value) {
		if (!DynamicColorModeProperty.isDynamicColorModeActivated(geoElement)) {
			DynamicColorModeProperty.activateDynamicColorMode(geoElement);
		}
		geoElement.setColorSpace(value);
		geoElement.updateRepaint();
	}

	@Override
	public Integer getValue() {
		return geoElement.getColorSpace();
	}
}
