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

import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

public class InverseFillProperty extends AbstractValuedProperty<Boolean> implements
		BooleanProperty {
	private final GeoElement element;

	/**
	 * @param localization this is used to localize the name
	 * @param element construction element
	 */
	public InverseFillProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "InverseFill");
		if (!element.isFillable()) {
			throw new NotApplicablePropertyException(element);
		}
		this.element = element;
	}

	@Override
	protected void doSetValue(Boolean value) {
		element.setInverseFill(value);
		element.updateVisualStyleRepaint(GProperty.COMBINED);
	}

	@Override
	public Boolean getValue() {
		return element.isInverseFill();
	}
}
