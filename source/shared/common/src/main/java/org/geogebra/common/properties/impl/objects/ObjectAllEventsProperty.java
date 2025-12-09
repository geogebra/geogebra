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

import java.util.ArrayList;

import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractProperty;

public class ObjectAllEventsProperty extends AbstractProperty {
	private final ArrayList<ObjectEventProperty> props;

	/**
	 * @param loc localization
	 * @param props properties to wrap
	 */
	public ObjectAllEventsProperty(Localization loc, ArrayList<ObjectEventProperty> props) {
		super(loc, "");
		this.props = props;
	}

	public ArrayList<ObjectEventProperty> getProps() {
		return props;
	}
}
