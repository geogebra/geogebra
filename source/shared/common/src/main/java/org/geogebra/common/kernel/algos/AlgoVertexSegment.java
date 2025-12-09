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

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;

/**
 *
 * @author Markus
 */
public class AlgoVertexSegment extends AlgoElement {

	private GeoSegmentND segment; // input
	private GeoNumberValue index; // input
	private GeoPointND M; // output
	private GeoPointND P;
	private GeoPointND Q; // endpoints of segment

	/** Creates new AlgoVector */
	public AlgoVertexSegment(Construction cons, String label,
			GeoSegmentND segment, GeoNumberValue index) {
		this(cons, segment, index);
		M.setLabel(label);
	}

	AlgoVertexSegment(Construction cons, GeoSegmentND segment,
			GeoNumberValue index) {
		super(cons);
		this.segment = segment;
		this.index = index;

		P = segment.getStartPoint();
		Q = segment.getEndPoint();

		// create new Point (2D or 3D as appropriate)
		if (Math.round(index.evaluateDouble()) == 2) {
			M = Q.copy();
		} else {
			M = P.copy();
		}
		setInputOutput();

		// compute M = (P + Q)/2
		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.Vertex;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = (GeoElement) segment;
		input[1] = (GeoElement) index;

		setOnlyOutput(M);
		setDependencies(); // done by AlgoElement
	}

	// Created for LocusEqu
	public GeoPointND getP() {
		return P;
	}

	// Created for LocusEqu
	public GeoPointND getQ() {
		return Q;
	}

	public GeoPointND getPoint() {
		return M;
	}

	@Override
	public final void compute() {

		if (!index.isDefined()) {
			M.setUndefined();
			return;
		}

		int indexd = (int) Math.round(index.evaluateDouble());

		if (indexd == 1) {
			M.set(P);
		} else if (indexd == 2) {
			M.set(Q);
		} else {
			M.setUndefined();
		}
	}

}
