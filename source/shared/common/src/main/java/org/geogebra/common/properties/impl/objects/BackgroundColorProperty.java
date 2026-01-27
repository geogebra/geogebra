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
import org.geogebra.common.properties.aliases.ColorProperty;
import org.geogebra.common.properties.impl.AbstractEnumeratedProperty;
import org.geogebra.common.properties.impl.DefaultColorValues;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

/**
 * Property to set the background color of a GeoElement.
 */
public class BackgroundColorProperty extends AbstractEnumeratedProperty<GColor>
		implements ColorProperty, GeoElementDependentProperty {
	
	private final GeoElement element;

	/**
	 * Creates a property to set the background color.
	 * @param localization the localization
	 * @param element the element to set background color for
	 * @throws NotApplicablePropertyException if the element does not support background color
	 */
	public BackgroundColorProperty(Localization localization,
			GeoElement element) throws NotApplicablePropertyException {
		super(localization, "BackgroundColor");
		if (!element.hasBackgroundColor()) {
			throw new NotApplicablePropertyException(element);
		}
		this.element = element;
		setValues(DefaultColorValues.PALE);
	}

	@Override
	public GColor getValue() {
		return element.getBackgroundColor();
	}

	@Override
	public void doSetValue(GColor color) {
		element.setBackgroundColor(color);
		element.updateVisualStyle(GProperty.COLOR_BG);
		element.getKernel().notifyRepaint();
	}

	@Override
	public GeoElement getGeoElement() {
		return element;
	}
}
