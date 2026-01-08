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
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractNumericProperty;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

public class InputBoxSizeProperty extends AbstractNumericProperty {
	private final GeoInputBox inputBox;

	/**
	 * @param localization localization
	 * @param element construction element
	 */
	public InputBoxSizeProperty(AlgebraProcessor processor,
			Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(processor, localization, "Width");
		if (!(element instanceof GeoInputBox)) {
			throw new NotApplicablePropertyException(element);
		}
		this.inputBox = (GeoInputBox) element;
	}

	@Override
	protected void setNumberValue(GeoNumberValue value) {
		inputBox.setLength((int) value.getDouble());
	}

	@Override
	protected NumberValue getNumberValue() {
		return new MyDouble(inputBox.getKernel(), inputBox.getLength());
	}
}
