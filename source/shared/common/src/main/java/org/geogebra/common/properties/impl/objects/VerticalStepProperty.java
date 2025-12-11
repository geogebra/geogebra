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
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractNumericProperty;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

/**
 * Object settings / Advanced tab: Interaction / vertical increment
 */
public class VerticalStepProperty extends AbstractNumericProperty
		implements GeoElementDependentProperty {

	private final GeoPointND element;

	/**
	 * Constructor
	 * @param algebraProcessor algebra processor
	 * @param localization localization
	 * @param element the GeoElement
	 * @throws NotApplicablePropertyException if {@code element} is not a {@link GeoPointND}
	 */
	public VerticalStepProperty(AlgebraProcessor algebraProcessor,
			Localization localization, GeoElement element) throws NotApplicablePropertyException {
		super(algebraProcessor, localization, "IncrementVertical");
		if (!(element instanceof GeoPointND)) {
			throw new NotApplicablePropertyException(element);
		}
		this.element = (GeoPointND) element;
	}

	@Override
	protected void setNumberValue(GeoNumberValue value) {
		element.setVerticalIncrement(value);
		element.updateVisualStyleRepaint(GProperty.COMBINED);
	}

	@Override
	protected NumberValue getNumberValue() {
		NumberValue step = element.getVerticalIncrement();
		if (step == null) {
			step = ((GeoElement) element).getAnimationStepObject();
		}
		return step;
	}

	@Override
	public boolean isEnabled() {
		return !element.isLocked()
				// "vertical increment" is only enabled if "selection allowed"
				&& ((GeoElement) element).isSelectionAllowed(null);
	}

	@Override
	public GeoElement getGeoElement() {
		return (GeoElement) element;
	}
}

