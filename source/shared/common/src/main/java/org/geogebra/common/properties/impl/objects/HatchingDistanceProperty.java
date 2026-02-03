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
import org.geogebra.common.properties.impl.AbstractRangeProperty;
import org.geogebra.common.properties.impl.objects.delegate.FillableDelegate;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

/**
 * Property for controlling the hatching distance (spacing) of a {@link GeoElement}.
 */
public class HatchingDistanceProperty extends AbstractRangeProperty<Integer>
		implements GeoElementDependentProperty {

	private final FillableDelegate delegate;

	/**
	 * @param localization localization
	 * @param element geo element
	 * @throws NotApplicablePropertyException if the element does not support filling
	 */
	public HatchingDistanceProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "Spacing", 5, 50, 5);
		delegate = new FillableDelegate(element);
	}

	@Override
	protected void setValueSafe(Integer value) {
		GeoElement element = delegate.getElement();
		element.setHatchingDistance(value);
		element.updateVisualStyleRepaint(GProperty.HATCHING);
	}

	@Override
	public boolean isAvailable() {
		return delegate.getElement().getFillType().isHatch();
	}

	@Override
	public Integer getValue() {
		return delegate.getElement().getHatchingDistance();
	}

	@Override
	public GeoElement getGeoElement() {
		return delegate.getElement();
	}
}
