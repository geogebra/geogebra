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

package org.geogebra.common.euclidian.plot.interval;

import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.function.GeoFunctionConverter;
import org.geogebra.common.kernel.interval.function.IntervalNodeFunction;
import org.geogebra.common.kernel.interval.function.IntervalTuple;
import org.geogebra.common.kernel.interval.function.IntervalTupleList;

/**
 * Class to manipulate data of an interval function.
 */
public class IntervalFunctionData {
	private final IntervalTupleList tuples;
	private final GeoFunctionConverter converter;
	private EuclidianViewBounds bounds;
	private final GeoFunction geoFunction;

	/**
	 * @param geoFunction to encapsulate
	 * @param converter of GeoFunction to IntervalNodeFunction.
	 * @param bounds of the view displaying the function
	 * @param tuples to store evaluated data
	 */
	public IntervalFunctionData(GeoFunction geoFunction, GeoFunctionConverter converter,
			EuclidianViewBounds bounds,
			IntervalTupleList tuples) {
		this(geoFunction, converter, tuples);
		this.bounds = bounds;
	}

	/**
	 *
	 * @param geoFunction to encapsulate
	 * @param tuples to store evaluated data
	 */
	public IntervalFunctionData(GeoFunction geoFunction, GeoFunctionConverter converter,
			IntervalTupleList tuples) {
		this.geoFunction = geoFunction;
		this.converter = converter;
		this.tuples = tuples;
	}

	/**
	 *
	 * @return list representing the function (x, y) interval pairs.
	 */
	public IntervalTupleList tuples() {
		return tuples;
	}

	/**
	 * Adds (x, y) interval pair to the end of the list.
	 * @param x {@link Interval}
	 * @param y {@link Interval}
	 */
	public void append(Interval x, Interval y) {
		tuples.add(new IntervalTuple(x, y));
	}

	/**
	 * Adds (x, y) interval pair to the beginning of the list.
	 * @param x {@link Interval}
	 * @param y {@link Interval}
	 */
	public void prepend(Interval x, Interval y) {
		tuples.prepend(new IntervalTuple(x, y));
	}

	/**
	 * Clears the whole data.
	 */
	public void clear() {
		tuples.clear();
	}

	/**
	 * Adds (x, y) interval pair to the beginning of the list and removes the last one,
	 * if it is offscreen (preserves list length)
	 * @param x {@link Interval}
	 * @param y {@link Interval}
	 */
	public void extendLeft(Interval x, Interval y) {
		prepend(x, y);
		double low = tuples.last().x().getLow();
		if (low >= bounds.getXmax()) {
			tuples.removeLast();
		}
	}

	/**
	 * Adds (x, y) interval pair to the end of the list and removes the first one,
	 * if it is offscreen (preserves list length)
	 * @param x {@link Interval}
	 * @param y {@link Interval}
	 */
	public void extendRight(Interval x, Interval y) {
		append(x, y);
		IntervalTuple first = tuples.first();
		if (first.x().getHigh() <= bounds.getXmin()) {
			tuples.removeFirst();
		}
	}

	/**
	 *
	 * @return {@link GeoFunction}
	 */
	public GeoFunction getGeoFunction() {
		return geoFunction;
	}

	/**
	 *
	 * @return if contains valid, displayable data.
	 */
	public boolean isValid() {
		return tuples.isValid();
	}

	public IntervalNodeFunction getFunction() {
		return converter.convert(geoFunction);
	}
}
