/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoEllipseFociLength.java
 *
 * Created on 15. November 2001, 21:37
 */

package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint;

/**
 * 
 * @author Markus
 * @version
 */
public abstract class AlgoConicFociLength extends AlgoElement {

	protected GeoPoint A, B; // input
	protected NumberValue a; // input
	private GeoElement ageo;
	private GeoConic conic; // output

	public AlgoConicFociLength(
			// package private
			Construction cons, String label, GeoPoint A, GeoPoint B,
			NumberValue a) {
		super(cons);
		this.A = A;
		this.B = B;
		this.a = a;
		ageo = a.toGeoElement();
		conic = new GeoConic(cons);
		setInputOutput(); // for AlgoElement

		compute();
		conic.setLabel(label);
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[3];
		input[0] = A;
		input[1] = B;
		input[2] = ageo;

		super.setOutputLength(1);
		super.setOutput(0, conic);
		setDependencies(); // done by AlgoElement
	}

	public GeoConic getConic() {
		return conic;
	}

	public GeoPoint getFocus1() {
		return A;
	}

	public GeoPoint getFocus2() {
		return B;
	}
	
	public NumberValue getLength() {
		return a;
	}

	// compute ellipse with foci A, B and length of half axis a
	@Override
	public final void compute() {
		conic.setEllipseHyperbola(A, B, a.getDouble());
	}

}
