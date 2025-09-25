package org.geogebra.common.properties.impl.graphics;

import static java.util.Map.entry;

import java.util.List;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoClippingCube3D;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings3D;
import org.geogebra.common.properties.impl.AbstractNamedEnumeratedProperty;

public class ClippingBoxSizeProperty extends AbstractNamedEnumeratedProperty<Integer> {
	private final EuclidianSettings3D euclidianSettings;

	/**
	 * Creates a clipping box size property
	 * @param localization localization
	 * @param euclidianSettings euclidian settings
	 */
	public ClippingBoxSizeProperty(Localization localization,
			EuclidianSettings3D euclidianSettings) {
		super(localization, "BoxSize");
		this.euclidianSettings = euclidianSettings;
		setNamedValues(List.of(
				entry(GeoClippingCube3D.REDUCTION_SMALL, "BoxSize.small"),
				entry(GeoClippingCube3D.REDUCTION_MEDIUM, "BoxSize.medium"),
				entry(GeoClippingCube3D.REDUCTION_LARGE, "BoxSize.large")));
	}

	@Override
	protected void doSetValue(Integer value) {
		euclidianSettings.setClippingReduction(value);
	}

	@Override
	public Integer getValue() {
		return euclidianSettings.getClippingReduction();
	}
}
