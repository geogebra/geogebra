/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoMidPoint.java
 *
 * Created on 24. September 2001, 21:37
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
 * @version
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

	AlgoVertexSegment(Construction cons, GeoSegmentND segment, GeoNumberValue index) {
		super(cons);
		this.segment = segment;
		this.index = index;

		P = segment.getStartPoint();
		Q = segment.getEndPoint();

		// create new Point (2D or 3D as appropriate)
		if (Math.round(index.evaluateDouble()) == 2) {
			M = (GeoPointND) Q.copy();
		} else {
			M = (GeoPointND) P.copy();
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

		super.setOutputLength(1);
		super.setOutput(0, (GeoElement) M);
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

	// calc midpoint
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
			return;		
		}

	}

}
