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

import javax.annotation.CheckForNull;

import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.properties.FillType;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.IconsEnumeratedProperty;
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.common.properties.impl.AbstractEnumeratedProperty;
import org.geogebra.common.properties.impl.objects.delegate.AbstractGeoElementDelegate;
import org.geogebra.common.properties.impl.objects.delegate.FillingStylePropertyDelegate;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

/**
 * Filling style
 */
public class FillingStyleProperty extends AbstractEnumeratedProperty<FillType>
			implements IconsEnumeratedProperty<FillType> {

	private static final Map<FillType, PropertyResource> icons = Map.of(
			FillType.STANDARD, PropertyResource.ICON_NO_FILLING,
			FillType.HATCH, PropertyResource.ICON_FILLING_HATCHED,
			FillType.CROSSHATCHED, PropertyResource.ICON_FILLING_CROSSHATCHED,
			FillType.CHESSBOARD, PropertyResource.ICON_FILLING_CHESSBOARD,
			FillType.DOTTED, PropertyResource.ICON_FILLING_DOTTED,
			FillType.HONEYCOMB, PropertyResource.ICON_FILLING_HONEYCOMB,
			FillType.BRICK, PropertyResource.ICON_FILLING_BRICK,
			FillType.WEAVING, PropertyResource.ICON_FILLING_WEAVING,
			FillType.SYMBOLS, PropertyResource.ICON_FILLING_SYMBOL,
			FillType.IMAGE, PropertyResource.ICON_FILLING_IMAGE);

	private final AbstractGeoElementDelegate delegate;

	/***/
	public FillingStyleProperty(Localization localization, GeoElement element, boolean extended)
			throws NotApplicablePropertyException {
		super(localization, "Filling");
		delegate = new FillingStylePropertyDelegate(element);
		setValues(extended ? List.of(FillType.values()) : List.of(FillType.STANDARD,
				FillType.HATCH,
				FillType.DOTTED,
				FillType.CROSSHATCHED,
				FillType.HONEYCOMB));
	}

	@Override
	public PropertyResource[] getValueIcons() {
		return getValues().stream().map(icons::get).toArray(PropertyResource[]::new);
	}

	@Override
	public @CheckForNull String[] getToolTipLabels() {
		return null;
	}

	@Override
	protected void doSetValue(FillType fillType) {
		GeoElement element = delegate.getElement();
		element.setFillType(fillType);
		element.updateVisualStyleRepaint(GProperty.HATCHING);
	}

	@Override
	public boolean isEnabled() {
		return delegate.getElement().isEuclidianVisible();
	}

	@Override
	public FillType getValue() {
		return delegate.getElement().getFillType();
	}
}
