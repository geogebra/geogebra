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

package org.geogebra.common.kernel.kernelND;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;

/**
 *
 * @author Markus
 */
public abstract class AlgoMidpointND extends AlgoElement {

	private GeoPointND P; // input
	private GeoPointND Q; // input
	private GeoPointND M; // output

	/**
	 * 
	 * @param cons
	 *            construction
	 * @param P
	 *            first point
	 * @param Q
	 *            second point
	 */
	protected AlgoMidpointND(Construction cons, GeoPointND P, GeoPointND Q) {
		super(cons);
		this.P = P;
		this.Q = Q;
		// create new Point
		M = newGeoPoint(cons);
		setInputOutput();

		// compute M = (P + Q)/2
		compute();
	}

	/**
	 * 
	 * used for midpoint of a segment
	 * 
	 * @param cons
	 *            construction
	 * @param segment
	 *            segment
	 */
	protected AlgoMidpointND(Construction cons, GeoSegmentND segment) {
		super(cons);

		P = segment.getStartPoint();
		Q = segment.getEndPoint();

		// create new Point
		M = newGeoPoint(cons);
	}

	/**
	 * 
	 * @param construction
	 *            construction
	 * @return new GeoPointND
	 */
	protected abstract GeoPointND newGeoPoint(Construction construction);

	@Override
	public final Commands getClassName() {
		return Commands.Midpoint;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_MIDPOINT;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = (GeoElement) P;
		input[1] = (GeoElement) Q;

		setOnlyOutput(M);
		setDependencies(); // done by AlgoElement
	}

	// calc midpoint
	@Override
	public final void compute() {

		boolean pInf = P.isInfinite();
		boolean qInf = Q.isInfinite();

		if (!pInf && !qInf) {
			// M = (P + Q) / 2
			computeMidCoords();
		} else if (pInf && qInf) {
			M.setUndefined();
		} else if (pInf) {
			copyCoords(P);
		} else {
			copyCoords(Q);
		}
	}

	/**
	 * copy coords of the point to the output point
	 * 
	 * @param point
	 *            input point
	 */
	abstract protected void copyCoords(GeoPointND point);

	/**
	 * compute output point as midpoint of input points
	 */
	abstract protected void computeMidCoords();

	/**
	 * 
	 * @return the output point
	 */
	public GeoPointND getPoint() {
		return M;
	}

	/**
	 * 
	 * @return first input point
	 */
	protected GeoPointND getP() {
		return P;
	}

	/**
	 * 
	 * @return second input point
	 */
	protected GeoPointND getQ() {
		return Q;
	}

	@Override
	public String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return getLoc().getPlainDefault("MidpointOfAB", "Midpoint of %0, %1",
				P.getLabel(tpl), Q.getLabel(tpl));

	}
}
