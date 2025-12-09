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

package org.geogebra.common.kernel.interval.evaluators;

import java.util.function.Consumer;

import org.geogebra.common.kernel.interval.Interval;

/**
 * A collection of consecutive intervals.
 */
public interface DiscreteSpace {

	/**
	 * Rebuild the intervals
	 * @param interval interval
	 * @param count number of sub-intervals
	 */
	void rescale(Interval interval, int count);

	/**
	 * Add intervals to the left
	 * @param domain new domain
	 * @param cb callback, called on all created intervals
	 */
	void extendLeft(Interval domain, ExtendSpace cb);

	/**
	 * Add intervals to the right
	 * @param domain new domain
	 * @param cb callback, called on all created intervals
	 */
	void extendRight(Interval domain, ExtendSpace cb);

	/**
	 * Add intervals to both sides
	 * @param domain new domain
	 * @param cbLeft callback, called on all created intervals added on the left
	 * @param cbRight callback, called on all created intervals added on the right
	 */
	void extend(Interval domain, ExtendSpace cbLeft, ExtendSpace cbRight);

	/**
	 * Perform an action for each interval.
	 * @param action action
	 */
	void forEach(Consumer<Interval> action);
}
