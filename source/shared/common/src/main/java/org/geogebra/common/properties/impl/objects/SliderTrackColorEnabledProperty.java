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
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.common.properties.impl.objects.delegate.SliderPropertyDelegate;

public class SliderTrackColorEnabledProperty extends AbstractValuedProperty<Boolean>
		implements BooleanProperty {

	private final SliderPropertyDelegate delegate;

	/**
	 * Creates a property to enable or disable a different color for the slider track.
	 *
	 * @param loc localization for property name
	 * @param element GeoElement to create property for
	 * @throws NotApplicablePropertyException if the property is not applicable to the element
	 */
	public SliderTrackColorEnabledProperty(Localization loc, GeoElement element)
			throws NotApplicablePropertyException {
		super(loc, "ObjectProperties.UseDifferentColor");
		delegate = new SliderPropertyDelegate(element);
	}

	@Override
	protected void doSetValue(Boolean value) {
		GeoNumeric numeric = delegate.getElement();
		if (value) {
			GColor color = numeric.getSelColor();
			if (color == null) {
				color = GColor.BLACK;
			}
			numeric.setBackgroundColor(color);
		} else {
			numeric.setBackgroundColor(null);
		}
		numeric.updateVisualStyleRepaint(GProperty.COLOR);
	}

	@Override
	public Boolean getValue() {
		return delegate.getElement().getBackgroundColor() != null;
	}
}