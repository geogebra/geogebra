package org.geogebra.common.euclidian.measurement;

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_PROTRACTOR;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_RULER;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_TRIANGLE_PROTRACTOR;

import java.util.Collections;
import java.util.List;

public enum MeasurementToolId {
	/**
	 * No tool
	 */
	NONE(-1, Collections.emptyList()),

	/**
	 * Ordinal ruler with two edges
	 */
	RULER(MODE_RULER, List.of(new RectangleEdge(1, 2), new RectangleEdge(3, 4))),

	/**
	 * Ordinal protractor
	 */
	PROTRACTOR(MODE_PROTRACTOR, true, List.of(new RectangleEdge(1, 2))),

	/**
	 * Right triangle shaped protractor with three edges: hypo and two legs.
	 */
	TRIANGLE_PROTRACTOR(MODE_TRIANGLE_PROTRACTOR, true, List.of(new RectangleEdge(1, 2),
			new LegEdge(1), new LegEdge(2)));

	private final int mode;
	private final boolean protractor;
	private final List<MeasurementToolEdge> edges;

	MeasurementToolId(int mode, List<MeasurementToolEdge> edges) {
		this(mode, false, edges);
	}

	MeasurementToolId(int mode, boolean protractor, List<MeasurementToolEdge> edges) {
		this.mode = mode;
		this.protractor = protractor;
		this.edges = edges;
	}

	public int getMode() {
		return mode;
	}

	public boolean isProtractor() {
		return protractor;
	}

	public List<MeasurementToolEdge> getEdges() {
		return edges;
	}
}
