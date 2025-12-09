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
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.PointProperties;
import org.geogebra.common.main.Localization;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.properties.impl.AbstractRangeProperty;
import org.geogebra.common.properties.impl.objects.delegate.AbstractGeoElementDelegate;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.common.properties.impl.objects.delegate.PointSizePropertyDelegate;

/**
 * Point size
 */
public class PointSizeProperty extends AbstractRangeProperty<Integer> {

	private final AbstractGeoElementDelegate delegate;

	/***/
	public PointSizeProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "Size", 1, 9, 1);
		delegate = new PointSizePropertyDelegate(element);
	}

	@Override
	protected void setValueSafe(Integer value) {
		GeoElement element = delegate.getElement();
		setSize(element, value);
		element.updateVisualStyleRepaint(GProperty.POINT_STYLE);
	}

	@Override
	public Integer getValue() {
		GeoElement element = delegate.getElement();
		if (element instanceof PointProperties) {
			return ((PointProperties) element).getPointSize();
		}
		return EuclidianStyleConstants.DEFAULT_POINT_SIZE;
	}

	private void setSize(GeoElement element, int size) {
		if (element instanceof GeoList) {
			GeoList list = (GeoList) element;
			for (int i = 0; i < list.size(); i++) {
				setSize(list.get(i), size);
			}
		} else if (element instanceof PointProperties) {
			((PointProperties) element).setPointSize(size);
		}
	}

	@Override
	public boolean isEnabled() {
		return delegate.getElement().isEuclidianVisible();
	}
}
