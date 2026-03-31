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
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

/**
 * {@code Property} responsible for setting the opacity of a line.
 */
public class LineOpacityProperty extends AbstractRangeProperty<Integer> {
	private final GeoElement element;

	/**
	 * Constructs the property for the given element.
	 * @param localization localization for translating property names
	 * @param element the element to create the property for
	 * @throws NotApplicablePropertyException if the property is not applicable for the given element
	 */
	public LineOpacityProperty(Localization localization, GeoElement element) throws
			NotApplicablePropertyException {
		super(localization, "LineOpacity", 0, 100, 1);
		if (!element.showLineProperties()) {
			throw new NotApplicablePropertyException(element);
		}
		this.element = element;
	}

	@Override
	protected void setValueSafe(Integer value) {
		element.setLineOpacity(Math.round(value / 100f * 255));
		element.updateVisualStyleRepaint(GProperty.LINE_STYLE);
	}

	@Override
	public Integer getValue() {
		return Math.round(element.getLineOpacity() / 255f * 100f);
	}

	@Override
	public boolean isValueDisplayedAsPercentage() {
		return true;
	}
}
