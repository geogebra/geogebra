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

package org.geogebra.common.kernel.interval;

import java.util.stream.Stream;

import org.geogebra.common.kernel.interval.function.IntervalTuple;
import org.geogebra.common.kernel.interval.function.IntervalTupleList;

public class TuplesQuery {
	private final IntervalTupleList tuples;

	public TuplesQuery(IntervalTupleList tuples) {
		this.tuples = tuples;
	}

	private Stream<IntervalTuple> emptyTuples() {
		return tuples.stream().filter(t -> t.y().isUndefined());
	}

	/**
	 * @return whether all tuples have undefined y-value
	 */
	public boolean noDefinedTuples() {
		return tuples.count() == emptyTuples().count();
	}
}
