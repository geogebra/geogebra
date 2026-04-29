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

package org.geogebra.common.kernel.interval.function;

import java.util.Objects;

import org.geogebra.common.kernel.interval.IntervalSet;

/**
 * Tuple of (x, y) intervals
 * @author laszlo
 */
public record IntervalTuple(IntervalSet xSet, IntervalSet ySet) {

	/**
	 * @return the interval of x coordinates
	 */
	@Override
	public IntervalSet xSet() {
		return xSet;
	}

	/**
	 * @return the interval of y coordinates
	 */
	@Override
	public IntervalSet ySet() {
		return ySet;
	}

	/**
	 * @return if tuple is an empty one
	 */
	public boolean isEmpty() {
		return ySet.isEmpty();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IntervalTuple other) {
			return xSet.equals(other.xSet) && ySet.equals(other.ySet);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(xSet, ySet);
	}
}
