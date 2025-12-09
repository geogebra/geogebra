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

package org.geogebra.common.euclidian.measurement;

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_PROTRACTOR;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_RULER;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_TRIANGLE_PROTRACTOR;

import java.util.Collections;
import java.util.List;

/**
 * Measurement tool types.
 */
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
