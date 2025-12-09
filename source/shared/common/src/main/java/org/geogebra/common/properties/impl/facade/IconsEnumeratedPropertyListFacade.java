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

package org.geogebra.common.properties.impl.facade;

import java.util.List;

import javax.annotation.CheckForNull;

import org.geogebra.common.properties.IconsEnumeratedProperty;
import org.geogebra.common.properties.PropertyResource;

/**
 * Handles a collection of IconsEnumeratedProperty objects as a single IconsEnumeratedProperty.
 */
public class IconsEnumeratedPropertyListFacade<T extends IconsEnumeratedProperty<V>, V>
		extends EnumeratedPropertyListFacade<T, V> implements IconsEnumeratedProperty<V> {

	/**
	 * @param properties properties to handle
	 */
	public IconsEnumeratedPropertyListFacade(List<T> properties) {
		super(properties);
	}

	@Override
	public PropertyResource[] getValueIcons() {
		return getFirstProperty().getValueIcons();
	}

	@Override
	public @CheckForNull String[] getToolTipLabels() {
		return null;
	}
}
