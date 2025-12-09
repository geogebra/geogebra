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

package org.geogebra.common.kernel;

import org.geogebra.common.kernel.kernelND.GeoElementND;

/**
 * Interface for algos that are random and hence allow setting result value eg
 * from SetValue[] command
 */
public interface SetRandomValue {

	/**
	 * Changes the random value
	 * 
	 * @param d
	 *            random value
	 * @return whether setting was successful (argument in range)
	 */
	boolean setRandomValue(GeoElementND d);

	/**
	 * @return whether the value is actually random
	 */
	default boolean canSetRandomValue() {
		return true;
	}
}
