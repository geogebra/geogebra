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

import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.properties.FillType;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractRangeProperty;
import org.geogebra.common.properties.impl.objects.delegate.FillableDelegate;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

/**
 * Property for controlling the hatching angle of a {@link GeoElement}.
 */
public class HatchingAngleProperty extends AbstractRangeProperty<Integer>
		implements GeoElementDependentProperty {

	private static final List<FillType> supportedFillTypes = List.of(
			FillType.HATCH, FillType.CROSSHATCHED,
			FillType.CHESSBOARD, FillType.BRICK, FillType.WEAVING
	);

	private final FillableDelegate delegate;

	/**
	 * @param localization localization
	 * @param element geo element
	 * @throws NotApplicablePropertyException if the element does not support filling
	 */
	public HatchingAngleProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "Angle", 0, null, null);
		delegate = new FillableDelegate(element);
	}

	@Override
	protected void setValueSafe(Integer value) {
		GeoElement element = delegate.getElement();
		element.setHatchingAngle(value);
		element.updateVisualStyleRepaint(GProperty.HATCHING);
	}

	@Override
	public boolean isAvailable() {
		FillType type = delegate.getElement().getFillType();
		return supportedFillTypes.contains(type);
	}

	@Override
	public Integer getValue() {
		return (int) Math.round(delegate.getElement().getHatchingAngle());
	}

	@Override
	public Integer getMax() {
		return switch (delegate.getElement().getFillType()) {
			case HATCH, BRICK -> 180;
			case CROSSHATCHED, CHESSBOARD,  WEAVING -> 45;
			default -> null;
		};
	}

	@Override
	public Integer getStep() {
		return switch (delegate.getElement().getFillType()) {
			case HATCH -> 5;
			case CROSSHATCHED, CHESSBOARD, BRICK, WEAVING -> 45;
			default -> null;
		};
	}

	@Override
	public GeoElement getGeoElement() {
		return delegate.getElement();
	}
}
