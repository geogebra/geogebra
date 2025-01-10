package org.geogebra.common.properties.impl.general;

import static java.util.Map.entry;

import java.util.List;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.CoordinatesFormat;
import org.geogebra.common.properties.impl.AbstractNamedEnumeratedProperty;

import com.google.j2objc.annotations.Weak;

/**
 * Property for setting the coordinates.
 */
public class CoordinatesProperty extends AbstractNamedEnumeratedProperty<Integer> {

	@Weak
	private Kernel kernel;

	/**
	 * Constructs a coordinates property.
	 * @param kernel kernel
	 * @param localization localization
	 */
	public CoordinatesProperty(Kernel kernel, Localization localization) {
		super(localization, "Coordinates");
		this.kernel = kernel;
		setNamedValues(List.of(
				entry(CoordinatesFormat.COORD_FORMAT_DEFAULT, "A = (x, y)"),
				entry(CoordinatesFormat.COORD_FORMAT_AUSTRIAN, "A(x | y)"),
				entry(CoordinatesFormat.COORD_FORMAT_FRENCH, "A: (x, y)")
		));
	}

	@Override
	protected void doSetValue(Integer value) {
		kernel.setCoordStyle(value);
		kernel.updateConstruction(false);
	}

	@Override
	public Integer getValue() {
		return kernel.getCoordStyle();
	}
}
