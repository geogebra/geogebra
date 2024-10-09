/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoCircleTwoPoints.java
 *
 * Created on 15. November 2001, 21:37
 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoQuadricND;

/**
 *
 * @author Markus + Mathieu
 */
public abstract class AlgoSphereNDTwoPoints extends AlgoElement {

	private GeoPointND M; // input
	private GeoPointND P; // input
	private GeoQuadricND sphereND; // output

	/**
	 * @param cons
	 *            construction
	 * @param M
	 *            center
	 * @param P
	 *            point on sphere
	 */
	public AlgoSphereNDTwoPoints(Construction cons, GeoPointND M,
			GeoPointND P) {
		super(cons);
		this.M = M;
		this.P = P;
		sphereND = createSphereND(cons);
		setInputOutput(); // for AlgoElement

		compute();
	}

	abstract protected GeoQuadricND createSphereND(Construction cons1);

	protected AlgoSphereNDTwoPoints(Construction cons, String label,
			GeoPointND M, GeoPointND P) {
		this(cons, M, P);
		sphereND.setLabel(label);
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = (GeoElement) M;
		input[1] = (GeoElement) P;

		setOnlyOutput(sphereND);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return resulting sphere
	 */
	public GeoQuadricND getSphereND() {
		return sphereND;
	}

	/**
	 * Method added for LocusEqu project.
	 * 
	 * @return center of sphere.
	 */
	public GeoPointND getCenter() {
		return this.getM();
	}

	protected GeoPointND getM() {
		return M;
	}

	/**
	 * Method added for LocusEqu project.
	 * 
	 * @return external point of sphere.
	 */
	public GeoPointND getExternalPoint() {
		return this.getP();
	}

	protected GeoPointND getP() {
		return P;
	}

	// compute circle with midpoint M and radius r
	@Override
	public final void compute() {
		sphereND.setSphereND(M, P);
	}

}
