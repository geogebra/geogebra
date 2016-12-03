/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoLinePointVector.java
 *
 * Created on 30. August 2001, 21:37
 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoVec3D;
import org.geogebra.common.kernel.geos.GeoVector;

/**
 *
 * @author Markus
 */
public class AlgoLinePointVector extends AlgoElement {

	private GeoPoint P; // input
	private GeoVector v; // input
	private GeoLine g; // output

	/** Creates new AlgoJoinPoints */
	public AlgoLinePointVector(Construction cons, String label, GeoPoint P,
			GeoVector v) {
		super(cons);
		this.P = P;
		this.v = v;
		g = new GeoLine(cons);
		g.setStartPoint(P);
		setInputOutput(); // for AlgoElement

		// compute line through P, Q
		compute();

		g.setLabel(label);
		addIncidence();
	}

	/**
	 * @author Tam
	 * 
	 *         for special cases of e.g. AlgoIntersectLineConic
	 */
	private void addIncidence() {
		P.addIncidence(g, true);
	}

	@Override
	public Commands getClassName() {
		return Commands.Line;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = P;
		input[1] = v;

		super.setOutputLength(1);
		super.setOutput(0, g);
		setDependencies(); // done by AlgoElement
	}

	public GeoLine getLine() {
		return g;
	}

	GeoPoint getP() {
		return P;
	}

	GeoVector getv() {
		return v;
	}

	// calc the line g through P and Q
	@Override
	public final void compute() {
		// g = cross(P, v)
		GeoVec3D.lineThroughPointVector(P, v, g);
	}

	@Override
	final public String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return getLoc().getPlain("LineThroughAwithDirectionB", P.getLabel(tpl),
				v.getLabel(tpl));
	}

	

}
