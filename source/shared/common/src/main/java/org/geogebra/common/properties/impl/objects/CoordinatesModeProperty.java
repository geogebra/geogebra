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

import static java.util.Map.entry;

import java.util.List;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.VectorNDValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractNamedEnumeratedProperty;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

public class CoordinatesModeProperty extends AbstractNamedEnumeratedProperty<Integer> {
	private final VectorNDValue element;

	/**
	 * @param localization localization
	 * @param element construction element
	 * @throws NotApplicablePropertyException if not a point or vector
	 */
	public CoordinatesModeProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "Coordinates");
		if (!(element instanceof VectorNDValue)) {
			throw new NotApplicablePropertyException(element);
		}
		setNamedValues(List.of(
				entry(Kernel.COORD_CARTESIAN, "CartesianCoords"),
				entry(Kernel.COORD_POLAR, "PolarCoords"),
				entry(Kernel.COORD_COMPLEX, "ComplexNumber"),
				entry(Kernel.COORD_CARTESIAN_3D, "CartesianCoords3D"),
				entry(Kernel.COORD_SPHERICAL, "Spherical")
		));
		this.element = (VectorNDValue) element;
	}

	@Override
	protected void doSetValue(Integer value) {
		element.setMode(value);
		((GeoElement) element).updateRepaint();
	}

	@Override
	public Integer getValue() {
		return element.getToStringMode();
	}
}
