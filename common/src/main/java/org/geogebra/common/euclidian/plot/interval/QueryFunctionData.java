package org.geogebra.common.euclidian.plot.interval;

import java.util.function.IntConsumer;

import org.geogebra.common.euclidian.plot.TupleNeighbours;
import org.geogebra.common.kernel.interval.function.IntervalTuple;

public interface QueryFunctionData {
	IntervalTuple at(int index);

	boolean hasNext(int index);

	boolean isInvertedAt(int index);

	int getCount();

	boolean isWholeAt(int index);

	boolean hasValidData();

	boolean nonDegenerated(int index);

	void forEach(IntConsumer action);

	TupleNeighbours neighboursAt(int index);

	IntervalTuple getAnchor();
}
