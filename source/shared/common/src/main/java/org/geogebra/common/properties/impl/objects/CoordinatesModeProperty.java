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
