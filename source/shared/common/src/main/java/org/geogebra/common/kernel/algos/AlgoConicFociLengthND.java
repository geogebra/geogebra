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
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * 
 * @author Markus
 */
public abstract class AlgoConicFociLengthND extends AlgoElement {

	protected GeoPointND A; // input
	protected GeoPointND B; // input
	protected GeoNumberValue a; // input
	protected GeoElement ageo;
	protected GeoConicND conic; // output

	protected AlgoConicFociLengthND(Construction cons, String label, GeoPointND A, GeoPointND B,
			GeoNumberValue a, GeoDirectionND orientation) {
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
	 * @param cons1
	 *            construction
	 * @return new conic
	 */
	abstract protected GeoConicND newGeoConic(Construction cons1);

	/**
	 * set the input
	 */
	abstract protected void setInput();

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		setInput();
		setOnlyOutput(conic);
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
