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

import javax.annotation.Nonnull;

import org.geogebra.common.euclidian3D.EuclidianView3DInterface;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractNamedEnumeratedProperty;

/** {@code Property} responsible for changing the AR ratio unit. */
public final class ARRatioUnitProperty extends AbstractNamedEnumeratedProperty<Integer>
		implements EuclidianView3DDependentProperty {
	private final EuclidianView3DInterface euclidianView3D;

	/**
	 * Constructs the property.
	 * @param localization localization for the title and option translations
	 * @param euclidianView3D the 3D euclidian view
	 */
	ARRatioUnitProperty(Localization localization, EuclidianView3DInterface euclidianView3D) {
		super(localization, "Settings.Unit");
		this.euclidianView3D = euclidianView3D;
		setNamedValues(List.of(
				entry(EuclidianView3D.RATIO_UNIT_METERS_CENTIMETERS_MILLIMETERS, "Unit.cm"),
				entry(EuclidianView3D.RATIO_UNIT_INCHES, "Unit.inch")
		));
	}

	@Override
	protected void doSetValue(Integer value) {
		euclidianView3D.setARRatioMetricSystem(value);
	}

	@Override
	public Integer getValue() {
		return euclidianView3D.getARRatioMetricSystem();
	}

	@Override
	public boolean isEnabled() {
		return euclidianView3D.isARRatioShown();
	}

	@Override
	public boolean isAvailable() {
		return euclidianView3D.isXREnabled();
	}

	@Override
	public @Nonnull EuclidianView3DInterface getEuclidianView3D() {
		return euclidianView3D;
	}
}
