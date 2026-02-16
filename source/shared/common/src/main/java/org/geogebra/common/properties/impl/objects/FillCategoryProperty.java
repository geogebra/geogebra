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

import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.properties.FillType;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractNamedEnumeratedProperty;
import org.geogebra.common.properties.impl.objects.delegate.FillableDelegate;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

/**
 * Property for selecting the fill category (pattern, symbol, or image) of a
 * {@link GeoElement}.
 */
public class FillCategoryProperty extends AbstractNamedEnumeratedProperty<FillCategory> {
	
	private FillType previousPatternFillType = FillType.STANDARD;

	private final FillableDelegate delegate;

	/**
	 * @param localization localization
	 * @param element geo element
	 * @throws NotApplicablePropertyException if the element does not support filling
	 */
	public FillCategoryProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "");
		this.delegate = new FillableDelegate(element);
		setNamedValues(List.of(
				entry(FillCategory.PATTERN, "Filling.Pattern"),
				entry(FillCategory.SYMBOL, "Filling.Symbol"),
				entry(FillCategory.IMAGE, "Filling.Image"))
		);
	}

	@Override
	protected void doSetValue(FillCategory value) {
		GeoElement element = delegate.getElement();
		FillType elementFillType = element.getFillType();
		FillCategory previousValue = FillCategory.fromFillType(elementFillType);
		if (previousValue == value) {
			// If the group did not change, ignore
			return;
		}
		if (previousValue == FillCategory.PATTERN) {
			// If changed from pattern, store default type
			previousPatternFillType = elementFillType;
		}
		element.setFillType(value.toFillType(previousPatternFillType));
		element.updateVisualStyleRepaint(GProperty.HATCHING);
	}

	@Override
	public FillCategory getValue() {
		FillType type = delegate.getElement().getFillType();
		return FillCategory.fromFillType(type);
	}
}
