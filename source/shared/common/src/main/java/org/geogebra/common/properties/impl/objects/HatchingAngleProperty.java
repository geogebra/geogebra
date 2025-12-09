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
import org.geogebra.common.properties.RangeProperty;
import org.geogebra.common.properties.impl.AbstractRangeProperty;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

public class HatchingAngleProperty extends AbstractRangeProperty<Integer>
		implements RangeProperty<Integer> {
	private final GeoElement element;

	/**
	 * Create a new property
	 */
	public HatchingAngleProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "Angle", 0, 90, 45);
		if (!element.getFillType().isHatch()) {
			throw new NotApplicablePropertyException(element);
		}
		this.element = element;
	}

	@Override
	protected void setValueSafe(Integer value) {
		element.setHatchingAngle(value);
		element.updateVisualStyleRepaint(GProperty.COMBINED);
	}

	@Override
	public Integer getValue() {
		return (int) element.getHatchingAngle();
	}
}
