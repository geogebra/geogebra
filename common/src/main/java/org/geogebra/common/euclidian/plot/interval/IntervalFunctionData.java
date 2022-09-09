package org.geogebra.common.euclidian.plot.interval;

import org.geogebra.common.euclidian.plot.TupleNeighbours;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.interval.Interval;
import org.geogebra.common.kernel.interval.function.IntervalTuple;
import org.geogebra.common.kernel.interval.function.IntervalTupleList;

public class IntervalFunctionData {
	private final IntervalTupleList tuples;
	private EuclidianViewBounds bounds;
	private final GeoFunction geoFunction;


	private final TupleNeighbours neighbours = new TupleNeighbours();

	public IntervalFunctionData(GeoFunction geoFunction, EuclidianViewBounds bounds) {
		this(geoFunction);
		this.bounds = bounds;
	}

	public IntervalFunctionData(GeoFunction geoFunction) {
		this.geoFunction = geoFunction;
		this.tuples = new IntervalTupleList();
	}

	public IntervalTupleList tuples() {
		return tuples;
	}

	public void append(Interval x, Interval y) {
		tuples.add(new IntervalTuple(x, y));
	}

	public void prepend(Interval x, Interval y) {
		tuples.prepend(new IntervalTuple(x, y));
	}

	public void clear() {
		tuples.clear();
	}

	public void extendLeft(Interval x, Interval y) {
		prepend(x, y);
		if (tuples.last().x().getLow() > bounds.getXmax()) {
			tuples.removeLast();
		}
	}

	public void extendRight(Interval x, Interval y) {
		append(x, y);
		if (tuples.first().x().getHigh() < bounds.getXmin()) {
			tuples.removeFirst();
		}
	}

	public int count() {
		return tuples.count();
	}

	public IntervalTuple at(int index) {
		return tuples.get(index);
	}

	public boolean isEmpty() {
		return tuples.isEmpty();
	}

	public GeoFunction getGeoFunction() {
		return geoFunction;
	}

	/**
	 * @param index to get the neighbours at.
	 * @return the neighbours around tuple given by index (including itself)
	 */
	public TupleNeighbours neighboursAt(int index) {
		neighbours.set(at(index - 1), at(index), at(index + 1));
		return neighbours;
	}

	public void pack(Interval domain) {
		while (tuples.first().x().getHigh() < domain.getLow()
			&& !tuples.isEmpty()) {
			tuples.removeFirst();
		}

		while (tuples.last().x().getLow() > domain.getHigh()
			&& !tuples.isEmpty()) {
			tuples.removeLast();
		}
	}
}
