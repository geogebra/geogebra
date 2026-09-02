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

import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

public class ListAsComboBoxProperty extends AbstractValuedProperty<Boolean> implements
		BooleanProperty {
	private final GeoList element;

	/**
	 * @param localization localization
	 * @param element construction element
	 */
	public ListAsComboBoxProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "DrawAsDropDownList");
		if (!(element instanceof GeoList)) {
			throw new NotApplicablePropertyException(element);
		}
		this.element = (GeoList) element;
	}

	/**
	 * Initialize related properties for a list that just became a dropdown.
	 * @param element changed list
	 */
	public static void initializeVisualProperties(GeoList element) {
		// Set default object and background color if needed
		if (!element.isBackgroundColorSet() && element.isDefaultObjectColorSet()) {
			element.setObjColor(GeoGebraColorConstants.NEUTRAL_900);
			element.setBackgroundColor(element.getBackgroundColor());
		}
		element.setEuclidianVisible(true);
	}

	@Override
	protected void doSetValue(Boolean value) {
		element.setDrawAsComboBox(value);
		if (value) {
			initializeVisualProperties(element);
		}
		element.updateVisualStyleRepaint(GProperty.COMBINED);
	}

	@Override
	public Boolean getValue() {
		return element.drawAsComboBox();
	}
}

