/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoDistancePoints.java
 *
 * Created on 30. August 2001, 21:37
 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 *
 * @author Markus
 * @version
 */
public class AlgoDistancePoints extends AlgoElement implements DistanceAlgo {

	private GeoPointND P, Q; // input
	private GeoNumeric dist; // output

	public AlgoDistancePoints(Construction cons, GeoPointND P, GeoPointND Q) {
		super(cons);
		this.P = P;
		this.Q = Q;
		dist = new GeoNumeric(cons);
		setInputOutput(); // for AlgoElement

		// compute length
		compute();
	}

	public AlgoDistancePoints(Construction cons, String label, GeoPointND P,
			GeoPointND Q) {
		this(cons, P, Q);
		dist.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.Distance;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_DISTANCE;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = (GeoElement) P;
		input[1] = (GeoElement) Q;

		super.setOutputLength(1);
		super.setOutput(0, dist);
		setDependencies(); // done by AlgoElement
	}

	public GeoNumeric getDistance() {
		return dist;
	}

	/*
	 * GeoPoint getP() { return P; } GeoPoint getQ() { return Q; }
	 */

	// calc length of vector v
	@Override
	public final void compute() {
		dist.setValue(P.distance(Q));
	}

	@Override
	final public String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return getLoc().getPlain("DistanceOfAandB", P.getLabel(tpl),
				Q.getLabel(tpl));

	}

	// TODO Consider locusequability
}
