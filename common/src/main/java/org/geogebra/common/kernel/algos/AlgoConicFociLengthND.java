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

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * 
 * @author Markus
 * @version
 */
public abstract class AlgoConicFociLengthND extends AlgoElement {

	protected GeoPointND A, B; // input
	protected NumberValue a; // input
	protected GeoElement ageo;
	protected GeoConicND conic; // output

	public AlgoConicFociLengthND(
			// package private
			Construction cons, String label, GeoPointND A, GeoPointND B,
			NumberValue a, GeoDirectionND orientation) {
		super(cons);
		this.A = A;
		this.B = B;
		this.a = a;
		ageo = a.toGeoElement();
		setOrientation(orientation);

		conic = newGeoConic(cons);
		setInputOutput(); // for AlgoElement

		initCoords();
		compute();
		conic.setLabel(label);
	}

	/**
	 * init Coords values
	 */
	protected void initCoords() {
		// none here
	}

	/**
	 * set orientation (in 3D)
	 * 
	 * @param orientation
	 *            orientation
	 */
	abstract protected void setOrientation(GeoDirectionND orientation);

	/**
	 * 
	 * @param cons
	 *            construction
	 * @return new conic
	 */
	abstract protected GeoConicND newGeoConic(Construction cons);

	/**
	 * set the input
	 */
	abstract protected void setInput();

	// for AlgoElement
	@Override
	protected void setInputOutput() {

		setInput();

		super.setOutputLength(1);
		super.setOutput(0, conic);
		setDependencies(); // done by AlgoElement
	}

	public GeoConicND getConic() {
		return conic;
	}

	public GeoPointND getFocus1() {
		return A;
	}

	public GeoPointND getFocus2() {
		return B;
	}

	public NumberValue getLength() {
		return a;
	}

	// compute ellipse with foci A, B and length of half axis a
	@Override
	public void compute() {
		conic.setEllipseHyperbola(getA2d(), getB2d(), a.getDouble());
	}

	/**
	 * 
	 * @return point A 2d coords
	 */
	abstract protected GeoPoint getA2d();

	/**
	 * 
	 * @return point B 2d coords
	 */
	abstract protected GeoPoint getB2d();

}
