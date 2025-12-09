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
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

public class EmphasizeRightAngleProperty extends AbstractValuedProperty<Boolean> implements
		BooleanProperty {
	private final GeoAngle element;

	/**
	 * @param localization localization
	 * @param element construction element
	 */
	public EmphasizeRightAngleProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "EmphasizeRightAngle");
		if (!(element instanceof GeoAngle)) {
			throw new NotApplicablePropertyException(element);
		}
		this.element = (GeoAngle) element;
	}

	@Override
	protected void doSetValue(Boolean value) {
		element.setEmphasizeRightAngle(value);
		element.updateVisualStyleRepaint(GProperty.COMBINED);
	}

	@Override
	public Boolean getValue() {
		return element.isEmphasizeRightAngle();
	}
}
