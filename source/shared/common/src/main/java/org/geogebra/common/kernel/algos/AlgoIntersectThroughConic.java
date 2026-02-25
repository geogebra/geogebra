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

package org.geogebra.common.kernel.algos;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoVec3D;
import org.geogebra.common.kernel.geos.LabelManager;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Algorithm to compute the intersection of:
 * - The line passing through two points A and B
 * - A conic (circle, ellipse, etc.)
 *
 * This is a one-step tool that combines Line[A,B] and Intersect[line, conic]
 * without creating the intermediate line object.
 * A line can intersect a conic at up to 2 points.
 */
public class AlgoIntersectThroughConic extends AlgoElement {

	private GeoPointND pointA; // input: first point
	private GeoPointND pointB; // input: second point
	private GeoConic conic; // input: conic to intersect with
	private GeoLine tempLine; // temporary line through A and B (internal)
	private GeoPoint[] intersections; // output: intersection points

	/**
	 * Creates intersection of line through A and B with conic c.
	 * @param cons construction
	 * @param labels labels for output points
	 * @param pointA first point defining the line
	 * @param pointB second point defining the line
	 * @param conic the conic to intersect with
	 */
	public AlgoIntersectThroughConic(Construction cons, String[] labels,
			GeoPointND pointA, GeoPointND pointB, GeoConic conic) {
		super(cons);
		this.pointA = pointA;
		this.pointB = pointB;
		this.conic = conic;

		// Create temporary line for computation (not added to construction)
		tempLine = new GeoLine(cons);

		// Create output points
		intersections = new GeoPoint[2];
		intersections[0] = new GeoPoint(cons);
		intersections[1] = new GeoPoint(cons);

		setInputOutput();
		compute();
		LabelManager.setLabels(labels, intersections);
	}

	@Override
	public Commands getClassName() {
		return Commands.IntersectThrough;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_INTERSECT_THROUGH;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[3];
		input[0] = pointA.toGeoElement();
		input[1] = pointB.toGeoElement();
		input[2] = conic;

		setOutput(intersections);
		setDependencies();
	}

	/**
	 * @return the intersection points
	 */
	public GeoPoint[] getPoints() {
		return intersections;
	}

	@Override
	public final void compute() {
		// Check if all inputs are defined
		if (!pointA.isDefined() || !pointB.isDefined() || !conic.isDefined()) {
			intersections[0].setUndefined();
			intersections[1].setUndefined();
			return;
		}

		// Compute line through A and B
		GeoVec3D.lineThroughPoints((GeoPoint) pointA, (GeoPoint) pointB, tempLine);

		if (!tempLine.isDefined()) {
			intersections[0].setUndefined();
			intersections[1].setUndefined();
			return;
		}

		// Compute intersections of tempLine and conic
		AlgoIntersectLineConic.intersectLineConic(tempLine, conic,
				intersections, Kernel.STANDARD_PRECISION);
	}

	@Override
	final public String toString(StringTemplate tpl) {
		return getLoc().getPlainDefault("IntersectionOfLineABAndC",
				"Intersection of line %0%1 and %2",
				pointA.getLabel(tpl),
				pointB.getLabel(tpl),
				conic.getLabel(tpl));
	}
}
