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

import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractNumericProperty;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.common.properties.impl.objects.delegate.SliderPropertyDelegate;

public class SliderTrackWidthProperty extends AbstractNumericProperty {

	private final SliderPropertyDelegate delegate;

	/**
	 * Creates a property for slider track width.
	 *
	 * @param processor algebra processor for numeric value processing
	 * @param localization localization for property name
	 * @param element GeoElement to create property for
	 * @throws NotApplicablePropertyException if the property is not applicable to the element
	 */
	public SliderTrackWidthProperty(AlgebraProcessor processor,
			Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(processor, localization, "Width");
		delegate = new SliderPropertyDelegate(element);
	}

	@Override
	protected void setNumberValue(GeoNumberValue value) {
		GeoNumeric numeric = delegate.getElement();
		numeric.setSliderWidth(value.getDouble(), true);
		numeric.updateRepaint();
	}

	@Override
	protected NumberValue getNumberValue() {
		GeoNumeric numeric = delegate.getElement();
		return new MyDouble(numeric.getKernel(), numeric.getSliderWidth());
	}
}
