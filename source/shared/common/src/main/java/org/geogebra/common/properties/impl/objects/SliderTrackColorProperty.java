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
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.DefaultColorValues;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.common.properties.impl.objects.delegate.SliderPropertyDelegate;

public class SliderTrackColorProperty extends ElementColorProperty
		implements GeoElementDependentProperty {

	/**
	 * Creates a property for slider track color.
	 *
	 * @param localization localization for property name
	 * @param element GeoElement to create property for
	 * @throws NotApplicablePropertyException if the property is not applicable to the element
	 */
	public SliderTrackColorProperty(Localization localization,
			GeoElement element) throws NotApplicablePropertyException {
		super(localization, new SliderPropertyDelegate(element), "Color");
		setValues(DefaultColorValues.BRIGHT);
	}

	@Override
	public void doSetValue(GColor color) {
		element.setBackgroundColor(color);
		element.updateVisualStyleRepaint(GProperty.COLOR);
	}

	@Override
	public GColor getValue() {
		return element.getBackgroundColor();
	}

	@Override
	public boolean isEnabled() {
		return element.getBackgroundColor() != null;
	}

	@Override
	public GeoElement getGeoElement() {
		return element;
	}
}
