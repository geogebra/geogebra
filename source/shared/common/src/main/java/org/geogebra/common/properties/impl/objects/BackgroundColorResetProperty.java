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
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.common.properties.aliases.ActionableIconProperty;
import org.geogebra.common.properties.impl.AbstractActionableProperty;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

/**
 * Property to reset/clear the background color of a GeoElement.
 */
public class BackgroundColorResetProperty extends AbstractActionableProperty
		implements ActionableIconProperty {

	private final GeoElement element;

	/**
	 * Creates a property to reset the background color.
	 * @param localization the localization
	 * @param element the element to reset background color for
	 * @throws NotApplicablePropertyException if the element does not support background color
	 */
	public BackgroundColorResetProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "noColor");
		if (!element.hasBackgroundColor()) {
			throw new NotApplicablePropertyException(element);
		}
		this.element = element;
	}

	@Override
	protected void doPerformAction() {
		element.setBackgroundColor(null);
		element.updateVisualStyle(GProperty.COLOR_BG);
		element.getKernel().notifyRepaint();
	}

	@Override
	public PropertyResource getIcon() {
		return PropertyResource.ICON_CLEAR_COLOR;
	}
}
