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

package org.geogebra.common.properties.factory;

import java.util.List;

import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.PropertiesRegistry;

/**
 * Creates properties for the GeoGebra application.
 */
public interface PropertiesFactory {

	/**
	 * Creates properties list.
	 * @param app properties for app
	 * @param localization localization for properties
	 * @param propertiesRegistry the properties registry
	 * @return a list of properties
	 */
	List<PropertiesArray> createProperties(App app, Localization localization,
			PropertiesRegistry propertiesRegistry);
}
