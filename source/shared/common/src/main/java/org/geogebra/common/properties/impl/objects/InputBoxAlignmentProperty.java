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
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.kernel.geos.properties.HorizontalAlignment;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractNamedEnumeratedProperty;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

public class InputBoxAlignmentProperty
		extends AbstractNamedEnumeratedProperty<HorizontalAlignment> {

	private GeoInputBox inputBox;

	/**
	 * @param localization localization
	 * @param element construction element
	 */
	public InputBoxAlignmentProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "Alignment");
		setNamedValues(List.of(
				entry(HorizontalAlignment.LEFT, "Left"),
				entry(HorizontalAlignment.CENTER, "Center"),
				entry(HorizontalAlignment.RIGHT, "Right")
		));
		if (!(element instanceof GeoInputBox)) {
			throw new NotApplicablePropertyException(element);
		}
		inputBox = (GeoInputBox) element;
	}

	@Override
	protected void doSetValue(HorizontalAlignment value) {
		inputBox.setAlignment(value);
		inputBox.updateVisualStyleRepaint(GProperty.COMBINED);
	}

	@Override
	public HorizontalAlignment getValue() {
		return inputBox.getAlignment();
	}
}
