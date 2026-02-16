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
import org.geogebra.common.properties.IconsEnumeratedProperty;
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.common.properties.impl.AbstractEnumeratedProperty;
import org.geogebra.common.properties.impl.objects.delegate.FillableDelegate;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

/**
 * Property for selecting a pattern-based fill style (e.g., hatched, dotted, honeycomb) of a
 * {@link GeoElement}.
 */
public class PatternFillStyleProperty extends AbstractEnumeratedProperty<FillType>
		implements IconsEnumeratedProperty<FillType>, GeoElementDependentProperty {
	private final FillableDelegate delegate;

	static final List<FillType> patternFillTypes = List.of(
			FillType.STANDARD, FillType.HATCH, FillType.CROSSHATCHED, FillType.DOTTED,
			FillType.HONEYCOMB, FillType.CHESSBOARD, FillType.WEAVING, FillType.BRICK);
	static final List<PropertyResource> patternFillTypeIcons = List.of(
			PropertyResource.ICON_NO_FILLING, PropertyResource.ICON_FILLING_HATCHED,
			PropertyResource.ICON_FILLING_CROSSHATCHED, PropertyResource.ICON_FILLING_DOTTED,
			PropertyResource.ICON_FILLING_HONEYCOMB, PropertyResource.ICON_FILLING_CHESSBOARD,
			PropertyResource.ICON_FILLING_WEAVING, PropertyResource.ICON_FILLING_BRICK);

	/**
	 * @param localization localization
	 * @param element geo element
	 * @throws NotApplicablePropertyException if the element does not support filling
	 */
	public PatternFillStyleProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "Filling.Pattern");
		delegate = new FillableDelegate(element);
		setValues(patternFillTypes);
	}

	@Override
	public PropertyResource[] getValueIcons() {
		return patternFillTypeIcons.stream().toArray(PropertyResource[]::new);
	}
	
	@Override
	public @CheckForNull String[] getToolTipLabels() {
		return null;
	}

	@Override
	protected void doSetValue(FillType value) {
		GeoElement element = delegate.getElement();
		element.setFillType(value);
		element.updateVisualStyleRepaint(GProperty.HATCHING);
	}

	@Override
	public FillType getValue() {
		return delegate.getElement().getFillType();
	}

	@Override
	public boolean isAvailable() {
		return FillCategory.fromFillType(delegate.getElement().getFillType())
				== FillCategory.PATTERN;
	}

	@Override
	public GeoElement getGeoElement() {
		return delegate.getElement();
	}
}
