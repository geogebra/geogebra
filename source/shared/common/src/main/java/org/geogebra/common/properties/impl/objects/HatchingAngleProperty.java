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

import javax.annotation.CheckForNull;

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
	private final FillableDelegate delegate;

	static final List<FillType> supportedHatchingAngleFillTypes = List.of(
			FillType.HATCH, FillType.CROSSHATCHED,
			FillType.CHESSBOARD, FillType.BRICK, FillType.WEAVING
	);

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

	static @CheckForNull Integer getHatchingAngleStep(FillType fillType) {
		return switch (fillType) {
			case HATCH -> 5;
			case CROSSHATCHED, CHESSBOARD, BRICK, WEAVING -> 45;
			default -> null;
		};
	}

	static @CheckForNull Integer getMaxHatchingAngleValue(FillType fillType) {
		return switch (fillType) {
			case HATCH, BRICK -> 180;
			case CROSSHATCHED, CHESSBOARD,  WEAVING -> 45;
			default -> null;
		};
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
		return supportedHatchingAngleFillTypes.contains(type);
	}

	@Override
	public Integer getValue() {
		return (int) Math.round(delegate.getElement().getHatchingAngle());
	}

	@Override
	public Integer getMax() {
		return getMaxHatchingAngleValue(delegate.getElement().getFillType());
	}

	@Override
	public Integer getStep() {
		return getHatchingAngleStep(delegate.getElement().getFillType());
	}

	@Override
	public GeoElement getGeoElement() {
		return delegate.getElement();
	}
}
