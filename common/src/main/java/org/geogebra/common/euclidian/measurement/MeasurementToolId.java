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
	 * Right triangle shaped protactor with three edges: hypo and two legs.
	 */
	TRIANGLE_PROTRACTOR(MODE_TRIANGLE_PROTRACTOR, true, List.of(new RectangleEdge(1, 2),
			new LegEdge(Legs.A), new LegEdge(Legs.B)));

	private final int mode;
	private final boolean protactor;
	private final List<MeasurementToolEdge> edges;

	MeasurementToolId(int mode, List<MeasurementToolEdge> edges) {
		this(mode, false, edges);
	}

	MeasurementToolId(int mode, boolean protactor, List<MeasurementToolEdge> edges) {
		this.mode = mode;
		this.protactor = protactor;
		this.edges = edges;
	}

	public int getMode() {
		return mode;
	}

	public boolean isProtactor() {
		return protactor;
	}

	public List<MeasurementToolEdge> getEdges() {
		return edges;
	}

	/**
	 * Returns the id by mode
	 *
	 * @param mode to search
	 * @return the id with the given mode
	 */
	public static MeasurementToolId byMode(int mode) {
		for (MeasurementToolId type: values()) {
			if (type.mode == mode) {
				return type;
			}
		}
		return NONE;
	}

	/**
	 * Returns the id by order
	 *
	 * @param order to search
	 * @return the id with the given order
	 */
	public static MeasurementToolId byOrder(int order) {
		for (MeasurementToolId type: values()) {
			if (type.ordinal() == order) {
				return type;
			}
		}
		return NONE;
	}
}
