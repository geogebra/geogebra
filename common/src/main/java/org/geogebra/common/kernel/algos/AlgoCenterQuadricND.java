/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoMidpointConic.java
 *
 * Created on 11. November 2001, 21:37
 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoQuadricND;

/**
 * Center of a quadric/conic
 */
public abstract class AlgoCenterQuadricND extends AlgoElement {

	protected GeoQuadricND c; // input
	protected GeoPointND midpoint; // output

	public AlgoCenterQuadricND(Construction cons, String label, GeoQuadricND c) {
		super(cons);
		this.c = c;
		midpoint = newGeoPoint(cons);
		setInputOutput(); // for AlgoElement

		compute();
		midpoint.setLabel(label);
	}

	/**
	 * 
	 * @param cons
	 * @return new GeoPoint
	 */
	abstract public GeoPointND newGeoPoint(Construction cons);

	@Override
	public Commands getClassName() {
		return Commands.Center;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_MIDPOINT;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = c;

		super.setOutputLength(1);
		super.setOutput(0, (GeoElement) midpoint);
		setDependencies(); // done by AlgoElement
	}

	public GeoPointND getPoint() {
		return midpoint;
	}

	@Override
	public final void compute() {
		if (!c.isDefined()) {
			midpoint.setUndefined();
			return;
		}

		setCoords();
	}

	/**
	 * set midpoint coords
	 */
	abstract protected void setCoords();

	@Override
	final public String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return getLoc().getPlain("CenterOfA", c.getLabel(tpl));
	}

	// TODO Consider locusequability
}
