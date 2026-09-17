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

package org.geogebra.common.euclidian.plot.implicit.classification;

import java.util.List;

import org.geogebra.common.awt.GGeneralPath;
import org.geogebra.common.awt.GPoint2D;

public class ClassifiedRegion {
	private final GGeneralPath outerBoundary;
	private final List<GGeneralPath> holes;
	private final GPoint2D samplePoint;
	private final int sourceFaceId;
	private boolean filled;

	/**
	 * Creates one classified region with an outer boundary, optional holes, and a sample point.
	 *
	 * @param outerBoundary outer boundary path
	 * @param holes hole paths inside the region
	 * @param samplePoint interior sample point used for classification
	 */
	public ClassifiedRegion(
			GGeneralPath outerBoundary, List<GGeneralPath> holes, GPoint2D samplePoint) {
		this(outerBoundary, holes, samplePoint, -1);
	}

	/**
	 * Creates one classified region with graph face provenance for diagnostics.
	 *
	 * @param outerBoundary outer boundary path
	 * @param holes hole paths inside the region
	 * @param samplePoint interior sample point used for classification
	 * @param sourceFaceId planar graph face id used to build this region
	 */
	public ClassifiedRegion(
			GGeneralPath outerBoundary,
			List<GGeneralPath> holes,
			GPoint2D samplePoint,
			int sourceFaceId) {
		this.outerBoundary = outerBoundary;
		this.holes = holes;
		this.samplePoint = samplePoint;
		this.sourceFaceId = sourceFaceId;
	}

	public GGeneralPath getOuterBoundary() {
		return outerBoundary;
	}

	public List<GGeneralPath> getHoles() {
		return holes;
	}

	public boolean isFilled() {
		return filled;
	}

	public GPoint2D getSamplePoint() {
		return samplePoint;
	}

	public int getSourceFaceId() {
		return sourceFaceId;
	}

	public void setFilled(boolean filled) {
		this.filled = filled;
	}

	@Override
	public String toString() {
		return "ClassifiedRegion{"
				+ "outerBoundary= " + (outerBoundary != null ? "yes" : "no")
				+ ", holes=" + holes.size()
				+ ", samplePoint=" + samplePoint
				+ ", sourceFaceId=" + sourceFaceId
				+ ", filled=" + filled
				+ '}';
	}
}
