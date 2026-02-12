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

import java.util.List;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.EuclidianStyleBarStatic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.aliases.ColorProperty;
import org.geogebra.common.properties.impl.AbstractEnumeratedProperty;
import org.geogebra.common.properties.impl.DefaultColorValues;
import org.geogebra.common.properties.impl.objects.delegate.ColorPropertyType;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

/**
 * {@code Property} responsible for setting the color of objects, excluding those with separate
 * foreground and background or fill color options.
 */
public class ObjectColorProperty extends AbstractEnumeratedProperty<GColor>
		implements ColorProperty {
	private final GeoElement geoElement;

	/**
	 * Constructs a property for the object's color
	 * @param localization localization for property name
	 * @param geoElement GeoElement to create property for
	 * @throws NotApplicablePropertyException if the property is not applicable to the element
	 */
	public ObjectColorProperty(Localization localization, GeoElement geoElement)
			throws NotApplicablePropertyException {
		super(localization, "Color");
		if (ColorPropertyType.forElement(geoElement) != ColorPropertyType.OPAQUE
			&& ColorPropertyType.forElement(geoElement) != ColorPropertyType.WITH_OPACITY) {
			throw new NotApplicablePropertyException(geoElement);
		}
		this.geoElement = geoElement;
		setValues(DefaultColorValues.BRIGHT);
	}

	@Override
	protected void doSetValue(GColor value) {
		EuclidianStyleBarStatic.applyColor(value, geoElement.getAlphaValue(), List.of(geoElement));
	}

	@Override
	public GColor getValue() {
		return geoElement.getObjectColor();
	}
}
