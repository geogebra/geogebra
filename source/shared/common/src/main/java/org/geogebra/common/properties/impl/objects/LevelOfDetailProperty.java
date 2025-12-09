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

import java.util.List;
import java.util.Map;

import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.SurfaceEvaluable;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractNamedEnumeratedProperty;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

public class LevelOfDetailProperty extends
		AbstractNamedEnumeratedProperty<SurfaceEvaluable.LevelOfDetail> {
	private GeoElement element;

	/**
	 * @param localization localization
	 * @param element construction element
	 */
	public LevelOfDetailProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "LevelOfDetail");
		if (!element.hasLevelOfDetail()) {
			throw new NotApplicablePropertyException(element);
		}
		setNamedValues(List.of(
				Map.entry(SurfaceEvaluable.LevelOfDetail.SPEED, "Speed"),
				Map.entry(SurfaceEvaluable.LevelOfDetail.QUALITY, "Quality")
		));
		this.element = element;
	}

	@Override
	protected void doSetValue(SurfaceEvaluable.LevelOfDetail value) {
		((SurfaceEvaluable) element).setLevelOfDetail(value);
		element.updateVisualStyleRepaint(GProperty.COMBINED);
	}

	@Override
	public SurfaceEvaluable.LevelOfDetail getValue() {
		return ((SurfaceEvaluable) element).getLevelOfDetail();
	}
}
