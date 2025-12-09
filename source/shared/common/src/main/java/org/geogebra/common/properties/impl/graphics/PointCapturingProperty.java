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

import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.main.Localization;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.properties.impl.AbstractNamedEnumeratedProperty;

/**
 * Property for setting the point capturing.
 */
public class PointCapturingProperty extends AbstractNamedEnumeratedProperty<Integer> {

	private final EuclidianViewInterfaceCommon view;

	/**
	 * Constructs a point capturing property.
	 * @param view Euclidian view
	 * @param localization localization
	 */
	public PointCapturingProperty(Localization localization,
			EuclidianViewInterfaceCommon view) {
		super(localization, "PointCapturing");
		this.view = view;
		setNamedValues(List.of(
				entry(EuclidianStyleConstants.POINT_CAPTURING_AUTOMATIC, "Labeling.automatic"),
				entry(EuclidianStyleConstants.POINT_CAPTURING_ON, "SnapToGrid"),
				entry(EuclidianStyleConstants.POINT_CAPTURING_ON_GRID, "FixedToGrid"),
				entry(EuclidianStyleConstants.POINT_CAPTURING_OFF, "Off")
		));
	}

	@Override
	public Integer getValue() {
		return view.getPointCapturingMode();
	}

	@Override
	protected void doSetValue(Integer value) {
		view.setPointCapturing(value);
	}
}
