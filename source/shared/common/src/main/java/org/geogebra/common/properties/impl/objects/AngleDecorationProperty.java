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

import org.geogebra.common.kernel.geos.AngleProperties;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.IconsEnumeratedProperty;
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.common.properties.impl.AbstractEnumeratedProperty;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

public class AngleDecorationProperty extends AbstractEnumeratedProperty<Integer>
		implements IconsEnumeratedProperty<Integer> {

	private static List<Integer> values = List.of(GeoAngle.getDecoTypes());
	private final GeoElement element;

	/**
	 * @param localization localization
	 * @param element construction element
	 */
	public AngleDecorationProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "Decoration");
		if (!(element instanceof AngleProperties)) {
			throw new NotApplicablePropertyException(element);
		}
		setValues(values);
		this.element = element;
	}

	@Override
	public PropertyResource[] getValueIcons() {
		return new PropertyResource[] {
				PropertyResource.ICON_ANGLE_DECO_NONE,
				PropertyResource.ICON_ANGLE_DECO_TWO_ARCS,
				PropertyResource.ICON_ANGLE_DECO_THREE_ARCS,
				PropertyResource.ICON_ANGLE_DECO_ONE_TICK,
				PropertyResource.ICON_ANGLE_DECO_TWO_TICKS,
				PropertyResource.ICON_ANGLE_DECO_THREE_TICKS,
				PropertyResource.ICON_ANGLE_DECO_ARROW_ANTICLOCKWISE,
				PropertyResource.ICON_ANGLE_DECO_ARROW_CLOCKWISE
		};
	}

	@Override
	protected void doSetValue(Integer value) {
		element.setDecorationType(values.get(value));
		element.updateVisualStyleRepaint(GProperty.DECORATION);
	}

	@Override
	public Integer getValue() {
		return values.indexOf(element.getDecorationType());
	}

	@Override
	public @CheckForNull String[] getToolTipLabels() {
		return null;
	}
}
