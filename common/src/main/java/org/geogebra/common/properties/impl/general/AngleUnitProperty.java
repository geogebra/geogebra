package org.geogebra.common.properties.impl.general;

import java.util.Map;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractNamedEnumeratedProperty;

import com.google.j2objc.annotations.Weak;

/**
 * Property for setting the angle unit.
 */
public class AngleUnitProperty extends AbstractNamedEnumeratedProperty<Integer> {

	@Weak
	private Kernel kernel;

	/**
	 * Constructs an angle unit property.
	 * @param kernel kernel
	 * @param localization localization
	 */
	public AngleUnitProperty(Kernel kernel, Localization localization) {
		super(localization, "AngleUnit");
		this.kernel = kernel;
		setNamedValues(Map.of(
				Kernel.ANGLE_DEGREE, "Degree",
				Kernel.ANGLE_RADIANT, "Radiant",
				Kernel.ANGLE_DEGREES_MINUTES_SECONDS, "DegreesMinutesSeconds"
		));
	}

	@Override
	public Integer getValue() {
		return kernel.getAngleUnit();
	}

	@Override
	protected void doSetValue(Integer value) {
		// check back with registry if setting value is allowed? ugly :/
		kernel.setAngleUnit(value);
		kernel.updateConstruction(false);
	}
}
