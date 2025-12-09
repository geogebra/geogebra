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

import org.geogebra.common.gui.dialog.options.model.LineStyleModel;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractRangeProperty;
import org.geogebra.common.properties.impl.objects.delegate.AbstractGeoElementDelegate;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.common.properties.impl.objects.delegate.ThicknessPropertyDelegate;

/**
 * Line thickness
 */
public class ThicknessProperty extends AbstractRangeProperty<Integer> {

	private final AbstractGeoElementDelegate delegate;

	/**
	 * Constructor
	 * @param localization - localization
	 * @param element - geo
	 * @throws NotApplicablePropertyException - exception
	 */
	public ThicknessProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "Thickness", null, 9, 1);
		delegate = new ThicknessPropertyDelegate(element);
	}

	/**
	 * Constructor
	 * @param localization - localization
	 * @param max - maximum of range
	 * @param delegate - delegate
	 */
	public ThicknessProperty(Localization localization, int max,
			AbstractGeoElementDelegate delegate) {
		super(localization, "Thickness", null, max, 1);
		this.delegate = delegate;
	}

	@Override
	public Integer getMin() {
		return delegate.getElement().getMinimumLineThickness();
	}

	@Override
	protected void setValueSafe(Integer value) {
		GeoElement element = delegate.getElement();
		setThickness(element, value);
		element.updateRepaint();
	}

	@Override
	public Integer getValue() {
		return delegate.getElement().getLineThickness();
	}

	private void setThickness(GeoElement element, int size) {
		if (element instanceof GeoList) {
			GeoList list = (GeoList) element;
			for (int i = 0; i < list.size(); i++) {
				setThickness(list.get(i), size);
			}
		} else if (LineStyleModel.match(element)) {
			element.setLineThickness(size);
		}
		element.updateVisualStyleRepaint(GProperty.LINE_STYLE);
	}

	@Override
	public boolean isEnabled() {
		return delegate.getElement().isEuclidianVisible();
	}
}
