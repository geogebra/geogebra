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

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

public class SimplifyCoefficientsProperty extends AbstractValuedProperty<Boolean> implements
		BooleanProperty {

	private final GeoFunction function;

	/**
	 * Constructs the property for the given element with the provided localization.
	 */
	public SimplifyCoefficientsProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "SimplifyCoefficients");
		if (!(element instanceof GeoFunction geoFunction)) {
			throw new NotApplicablePropertyException(element);
		}
		this.function = geoFunction;
	}

	@Override
	public Boolean getValue() {
		return function.hasSimplifiedCoefficients();
	}

	@Override
	protected void doSetValue(Boolean value) {
		function.setSimplifyCoefficients(value);
		function.updateRepaint();
	}

	@Override
	public boolean isAvailable() {
		return function.isDefined();
	}
}
