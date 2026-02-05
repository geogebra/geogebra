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
import static org.geogebra.common.properties.impl.objects.SliderOrientationProperty.SliderOrientation.HORIZONTAL;
import static org.geogebra.common.properties.impl.objects.SliderOrientationProperty.SliderOrientation.VERTICAL;

import java.util.List;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractNamedEnumeratedProperty;
import org.geogebra.common.properties.impl.objects.SliderOrientationProperty.SliderOrientation;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

/**
 * {@code Property} responsible for setting the orientation of a slider.
 */
public class SliderOrientationProperty extends AbstractNamedEnumeratedProperty<SliderOrientation> {
	/**
	 * The possible orientations of a slider.
	 */
	public enum SliderOrientation {
		HORIZONTAL, VERTICAL,
	}

	private final GeoNumeric geoNumeric;

	/**
	 * Creates a property for the slider orientation.
	 * @param localization localization for property name
	 * @param geoElement GeoElement to create property for
	 * @throws NotApplicablePropertyException if the property is not applicable to the element
	 */
	public SliderOrientationProperty(Localization localization, GeoElement geoElement)
			throws NotApplicablePropertyException {
		super(localization, "ObjectProperties.SliderOrientation");
		if (!(geoElement instanceof GeoNumeric geoNumeric && geoNumeric.isSlider())) {
			throw new NotApplicablePropertyException(geoElement);
		}
		this.geoNumeric = geoNumeric;
		setNamedValues(List.of(
				entry(HORIZONTAL, "horizontal"),
				entry(VERTICAL, "vertical")));
	}

	@Override
	protected void doSetValue(SliderOrientation value) {
		switch (value) {
		case HORIZONTAL -> geoNumeric.setSliderHorizontal(true);
		case VERTICAL -> geoNumeric.setSliderHorizontal(false);
		}
		geoNumeric.updateRepaint();
	}

	@Override
	public SliderOrientation getValue() {
		return geoNumeric.isSliderHorizontal() ? HORIZONTAL : VERTICAL;
	}
}
