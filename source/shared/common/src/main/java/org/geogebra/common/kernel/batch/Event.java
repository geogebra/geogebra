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

package org.geogebra.common.kernel.batch;

import java.util.Arrays;

class Event {

	private String name;
	private Object[] parameters;

	Event(String name, Object[] parameters) {
		this.name = name;
		this.parameters = parameters;
	}

	String getName() {
		return name;
	}

	Object[] getParameters() {
		return parameters;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Event event = (Event) o;

		if (!name.equals(event.name)) {
			return false;
		}
		return Arrays.equals(parameters, event.parameters);
	}

	@Override
	public int hashCode() {
		int result = name.hashCode();
		result = 31 * result + Arrays.hashCode(parameters);
		return result;
	}
}
