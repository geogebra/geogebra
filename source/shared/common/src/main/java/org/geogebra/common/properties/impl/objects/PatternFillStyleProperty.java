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

import static java.util.Map.entry;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

	private static final List<Map.Entry<FillType, PropertyResource>> values = List.of(
			entry(FillType.STANDARD, PropertyResource.ICON_NO_FILLING),
			entry(FillType.HATCH, PropertyResource.ICON_FILLING_HATCHED),
			entry(FillType.CROSSHATCHED, PropertyResource.ICON_FILLING_CROSSHATCHED),
			entry(FillType.DOTTED, PropertyResource.ICON_FILLING_DOTTED),
			entry(FillType.HONEYCOMB, PropertyResource.ICON_FILLING_HONEYCOMB),
			entry(FillType.CHESSBOARD, PropertyResource.ICON_FILLING_CHESSBOARD),
			entry(FillType.WEAVING, PropertyResource.ICON_FILLING_WEAVING),
			entry(FillType.BRICK, PropertyResource.ICON_FILLING_BRICK)
	);

	private final FillableDelegate delegate;

	/**
	 * @param localization localization
	 * @param element geo element
	 * @throws NotApplicablePropertyException if the element does not support filling
	 */
	public PatternFillStyleProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "Filling.Pattern");
		delegate = new FillableDelegate(element);
		setValues(values.stream().map(Map.Entry::getKey).collect(Collectors.toUnmodifiableList()));
	}

	@Override
	public PropertyResource[] getValueIcons() {
		return values.stream().map(Map.Entry::getValue).toArray(PropertyResource[]::new);
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
		return getValues().contains(delegate.getElement().getFillType());
	}

	@Override
	public GeoElement getGeoElement() {
		return delegate.getElement();
	}
}
