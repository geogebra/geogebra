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

package org.geogebra.web.full.euclidian.quickstylebar.icon;

import org.geogebra.common.properties.Property;
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.web.html5.gui.view.IconSpec;

public class PropertiesIconResource {

	private final PropertiesIconProvider propertiesIconProvider;

	public PropertiesIconResource(PropertiesIconProvider propertiesIconProvider) {
		this.propertiesIconProvider = propertiesIconProvider;
	}

	/**
	 * @param propertyResource icon
	 * @return spec for given icon
	 */
	public IconSpec getImageResource(PropertyResource propertyResource) {
		return propertiesIconProvider.matchIconWithResource(propertyResource);
	}

	/**
	 * @param property property
	 * @return spec for given icon
	 */
	public IconSpec getImageResource(Property property) {
		return propertiesIconProvider.matchIconWithResource(property);
	}
}
