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

package org.geogebra.common.euclidian.plot.implicit.classification.topology;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.geogebra.common.kernel.MyPoint;

/**
 * Extracted face of planar graph with outer component and optional holes.
 */
public final class Face {
	private final int id;
	private int outerHalfEdgeId = -1;
	private final List<Integer> holeHalfEdgeIds = new ArrayList<>();
	private boolean exterior;
	private MyPoint samplePoint;

	public Face(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public int getOuterHalfEdgeId() {
		return outerHalfEdgeId;
	}

	public void setOuterHalfEdgeId(int outerHalfEdgeId) {
		this.outerHalfEdgeId = outerHalfEdgeId;
	}

	/**
	 * Adds a boundary component that is excluded from this face.
	 * @param holeHalfEdgeId first half-edge of the hole boundary
	 */
	public void addHoleHalfEdgeId(int holeHalfEdgeId) {
		holeHalfEdgeIds.add(holeHalfEdgeId);
	}

	public List<Integer> getHoleHalfEdgeIds() {
		return Collections.unmodifiableList(holeHalfEdgeIds);
	}

	public boolean isExterior() {
		return exterior;
	}

	public void setExterior(boolean exterior) {
		this.exterior = exterior;
	}

	public MyPoint getSamplePoint() {
		return samplePoint;
	}

	public void setSamplePoint(MyPoint samplePoint) {
		this.samplePoint = samplePoint;
	}
}
