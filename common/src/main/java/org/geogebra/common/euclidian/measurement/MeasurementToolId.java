package org.geogebra.common.euclidian.measurement;

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_PROTRACTOR;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_RULER;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_TRIANGLE_PROTRACTOR;

import java.util.List;

public enum MeasurementToolId {
	NONE(-1),
	RULER(MODE_RULER, new RectangleEdge(1, 2), new RectangleEdge(3, 4)),
	PROTRACTOR(MODE_PROTRACTOR, true, null),
	TRIANGLE_PROTRACTOR(MODE_TRIANGLE_PROTRACTOR, true, new RectangleEdge(1, 2),
			new LegEdge(LegEdge.Legs.A), new LegEdge(LegEdge.Legs.B));

	private final int mode;
	private final boolean protactor;
	private final List<RulerEdge> edges;

	MeasurementToolId(int mode) {
		this(mode, false, null);
	}

	MeasurementToolId(int mode, RulerEdge... edges) {
		this(mode, false, edges);
	}

	MeasurementToolId(int mode, boolean protactor, RulerEdge... edges) {
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

	public List<RulerEdge> getEdges() {
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
