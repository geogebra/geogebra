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

import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractNumericProperty;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

public class AnimationSpeedProperty extends AbstractNumericProperty {

	private final GeoElement element;

	/***/
	public AnimationSpeedProperty(AlgebraProcessor algebraProcessor,
			Localization localization, GeoElement element) throws NotApplicablePropertyException {
		super(algebraProcessor, localization, "AnimationSpeed");
		if (!element.isAnimatable()) {
			throw new NotApplicablePropertyException(element);
		}
		this.element = element;
	}

	@Override
	protected void setNumberValue(GeoNumberValue value) {
		element.setAnimationSpeedObject(value);
		element.updateVisualStyleRepaint(GProperty.COMBINED);
	}

	@Override
	protected NumberValue getNumberValue() {
		NumberValue value = (NumberValue) element.getAnimationSpeedObject();
		if (value == null) {
			GeoNumeric defaultNumber = element.getKernel().getAlgoDispatcher()
					.getDefaultNumber(element.isAngle());
			return (NumberValue) defaultNumber.getAnimationSpeedObject();
		}
		return value;
	}
}
