package org.geogebra.common.euclidian.measurement;

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_PROTRACTOR;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_RULER;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_TRIANGLE_PROTRACTOR;

import java.util.List;

public enum MeasurementToolId {
	/**
	 * No tool
	 */
	NONE(-1),

	/**
	 * Ordinal ruler with two edges
	 */
	RULER(MODE_RULER, new RectangleEdge(1, 2), new RectangleEdge(3, 4)),

	/**
	 * Ordinal protractor
	 */
	PROTRACTOR(MODE_PROTRACTOR, true, null),

	/**
	 * Right triangle shaped protactor with three edges: hypo and two legs.
	 */
	TRIANGLE_PROTRACTOR(MODE_TRIANGLE_PROTRACTOR, true, new RectangleEdge(1, 2),
			new LegEdge(Legs.A), new LegEdge(Legs.B));

	private final int mode;
	private final boolean protactor;
	private final List<MeasurementToolEdge> edges;

	MeasurementToolId(int mode) {
		this(mode, false, null);
	}

	MeasurementToolId(int mode, MeasurementToolEdge... edges) {
		this(mode, false, edges);
	}

	MeasurementToolId(int mode, boolean protactor, MeasurementToolEdge... edges) {
		this.mode = mode;
		this.protactor = protactor;
		this.edges = edges != null ? List.of(edges) : null;
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

	public static MeasurementToolId byMode(int mode) {
		for (MeasurementToolId type: values()) {
			if (type.mode == mode) {
				return type;
			}
		}
		return NONE;
	}

	public static MeasurementToolId byOrder(int order) {
		for (MeasurementToolId type: values()) {
			if (type.ordinal() == order) {
				return type;
			}
		}
		return NONE;
	}
}
