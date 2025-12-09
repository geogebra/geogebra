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

import org.geogebra.common.kernel.geos.AngleProperties;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.RangeProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

public class AngleArcSizeProperty extends AbstractValuedProperty<Integer>
		implements RangeProperty<Integer> {

	private AngleProperties geoElement;

	/**
	 * @param localization localization
	 * @param element construction element
	 */
	public AngleArcSizeProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "Size");
		if (element instanceof AngleProperties) {
			geoElement = (AngleProperties) element;
		} else {
			throw new NotApplicablePropertyException(element);
		}
	}

	@Override
	public Integer getMin() {
		return 10;
	}

	@Override
	public Integer getMax() {
		return 100;
	}

	@Override
	public Integer getStep() {
		return 1;
	}

	@Override
	public Integer getValue() {
		return geoElement.getArcSize();
	}

	@Override
	protected void doSetValue(Integer value) {
		geoElement.setArcSize(value);
	}
}
