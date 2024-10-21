package org.geogebra.common.properties.impl.general;

import static java.util.Map.entry;

import java.util.List;

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
		setNamedValues(List.of(
				entry(Kernel.ANGLE_DEGREE, "Degree"),
				entry(Kernel.ANGLE_RADIANT, "Radiant"),
				entry(Kernel.ANGLE_DEGREES_MINUTES_SECONDS, "DegreesMinutesSeconds")
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
