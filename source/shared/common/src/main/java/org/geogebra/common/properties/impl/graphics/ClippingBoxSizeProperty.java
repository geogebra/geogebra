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
