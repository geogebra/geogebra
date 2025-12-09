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

package org.geogebra.common.properties.impl.collections;

import java.util.List;

import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.ActionableProperty;

public class ActionablePropertyCollection<T extends ActionableProperty> extends
		AbstractPropertyCollection<ActionableProperty> {
	/**
	 * Constructs an ActionablePropertyCollection.
	 * @param localization localization
	 * @param properties list of {@link ActionableProperty} properties
	 */
	public ActionablePropertyCollection(Localization localization,
			List<T> properties) {
		super(localization, "");
		setProperties(properties.toArray(new ActionableProperty[0]));
	}

	@Override
	public T[] getProperties() {
		return (T[]) super.getProperties();
	}
}
