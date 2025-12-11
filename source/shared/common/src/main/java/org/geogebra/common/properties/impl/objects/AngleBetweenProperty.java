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
 * See https://www.geogebra.org/license for full licensing details'
 */

package org.geogebra.common.properties.impl.objects;

import static org.geogebra.common.gui.dialog.options.model.OptionsModel.isAngleList;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.kernel.geos.AngleProperties;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractNamedEnumeratedProperty;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

/**
 * Property for configuring the angle interval style (anticlockwise, reflex, non-reflex, unbounded).
 */
public class AngleBetweenProperty extends AbstractNamedEnumeratedProperty<GeoAngle.AngleStyle> {

	private final AngleProperties angle;

	/**
	 * Creates an angle interval property for the given element.
	 * @param localization the localization
	 * @param element the element
	 * @throws NotApplicablePropertyException if the element doesn't support angle intervals
	 */
	public AngleBetweenProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "AngleBetween");
		if (!isApplicable(element)) {
			throw new NotApplicablePropertyException(element);
		}
		angle = (AngleProperties) element;

		setValues(getOptions());
	}

	@Override
	protected void doSetValue(GeoAngle.AngleStyle value) {
		angle.setAngleStyle(value);
		angle.updateVisualStyleRepaint(GProperty.ANGLE_INTERVAL);
	}

	@Override
	public GeoAngle.AngleStyle getValue() {
		return angle.getAngleStyle();
	}

	@Override
	public String[] getValueNames() {
		return getValues().stream()
				.map(value -> getLocalization().getPlain("AandB",
						value.getMin(), value.getMax()))
				.toArray(String[]::new);
	}

	private List<GeoAngle.AngleStyle> getOptions() {
		List<GeoAngle.AngleStyle> result = new ArrayList<>();

		if (angle.hasOrientation()) {
			result.add(GeoAngle.AngleStyle.ANTICLOCKWISE);
			result.add(GeoAngle.AngleStyle.NOTREFLEX);
			result.add(GeoAngle.AngleStyle.ISREFLEX);
			if (!angle.isDrawable()) {
				// don't want to allow (-inf, +inf)
				result.add(GeoAngle.AngleStyle.UNBOUNDED);
			}
		} else { // only 180degree wide intervals are possible
			result.add(GeoAngle.AngleStyle.NOTREFLEX);
			result.add(GeoAngle.AngleStyle.ISREFLEX);
		}
		return result;
	}

	private boolean isApplicable(GeoElement element) {
		return !element.isIndependent()
				&& (element instanceof AngleProperties)
				&& !element.isGeoList()
				|| isAngleList(element);
	}
}
