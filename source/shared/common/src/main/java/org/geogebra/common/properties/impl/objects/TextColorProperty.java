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
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.HasTextFormatter;
import org.geogebra.common.kernel.geos.TextProperties;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.aliases.ColorProperty;
import org.geogebra.common.properties.impl.AbstractEnumeratedProperty;
import org.geogebra.common.properties.impl.DefaultColorValues;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

/**
 * {@code Property} responsible for setting the foreground color of a text.
 */
public final class TextColorProperty extends AbstractEnumeratedProperty<GColor>
		implements ColorProperty {
	private final GeoElement geoElement;

	/**
	 * Constructs the property for the given element.
	 * @param localization localization for translating the property name
	 * @param geoElement the element to create the property for
	 * @throws NotApplicablePropertyException if the property is not applicable for the given element
	 */
	public TextColorProperty(Localization localization, GeoElement geoElement)
			throws NotApplicablePropertyException {
		super(localization, "Color");
		if (!(geoElement instanceof TextProperties) && !(geoElement instanceof HasTextFormatter)) {
			throw new NotApplicablePropertyException(geoElement);
		}
		this.geoElement = geoElement;
		setValues(DefaultColorValues.BRIGHT);
	}

	/**
	 * Constructs the property for the given element with predefined color values.
	 * @param localization localization for translating the property name
	 * @param geoElement the element to create the property for
	 * @param values list of colors
	 * @throws NotApplicablePropertyException if the property is not applicable for the given element
	 */
	public TextColorProperty(Localization localization, GeoElement geoElement,
			List<GColor> values)
			throws NotApplicablePropertyException {
		super(localization, "Color");
		if (!(geoElement instanceof TextProperties) && !(geoElement instanceof HasTextFormatter)) {
			throw new NotApplicablePropertyException(geoElement);
		}
		this.geoElement = geoElement;
		setValues(values);
	}

	@Override
	protected void doSetValue(GColor value) {
		if (geoElement instanceof TextProperties) {
			geoElement.setObjColor(value);
		} else {
			((HasTextFormatter) geoElement).format("color", value.toString());
		}
		geoElement.updateVisualStyleRepaint(GProperty.COLOR);
	}

	@Override
	public GColor getValue() {
		if (geoElement instanceof HasTextFormatter) {
			return GColor.getGColor(((HasTextFormatter) geoElement)
					.getFormat("color", null));
		}

		return geoElement.getObjectColor();
	}
}
