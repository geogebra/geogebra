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

package org.geogebra.common.kernel.interval.samplers;

import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.function.IntervalTupleList;

/**
 * Interval function sampler.
 */
public interface IntervalFunctionSampler {

	/**
	 * @return interval tuples
	 */
	IntervalTupleList tuples();

	/**
	 * Extend to an interval. Try reusing values if possible, fall back to resample.
	 * @param domain new domain
	 */
	void extend(Interval domain);

	/**
	 * Reset the domain and rebuild all value tuples.
	 * @param domain new domain
	 */
	void resample(Interval domain);
}
