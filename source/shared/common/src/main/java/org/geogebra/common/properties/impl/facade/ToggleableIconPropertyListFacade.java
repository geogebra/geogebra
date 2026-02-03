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

import org.geogebra.common.properties.PropertyResource;
import org.geogebra.common.properties.ToggleableIconProperty;

/**
 * Handles a collection of {@link ToggleableIconProperty}
 * as a single {@link ToggleableIconProperty}.
 */
public final class ToggleableIconPropertyListFacade
		extends AbstractValuedPropertyListFacade<ToggleableIconProperty, Boolean>
		implements ToggleableIconProperty {
	/**
	 * Constructs the list facade with the given properties
	 * @param properties the properties to build the facade list from
	 */
	public ToggleableIconPropertyListFacade(List<ToggleableIconProperty> properties) {
		super(properties);
	}

	@Override
	public PropertyResource getIcon() {
		return getFirstProperty().getIcon();
	}
}
