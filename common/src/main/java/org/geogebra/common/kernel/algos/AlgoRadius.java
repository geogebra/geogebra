/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoRadius.java
 *
 * Created on 30. August 2001, 21:37
 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoConicNDConstants;
import org.geogebra.common.kernel.kernelND.GeoQuadricND;

/**
 *
 * @author Markus
 */
public class AlgoRadius extends AlgoElement {

	private GeoQuadricND c; // input
	private GeoNumeric num; // output

	/**
	 * @param cons
	 *            construction
	 * @param c
	 *            quadric
	 */
	public AlgoRadius(Construction cons, GeoQuadricND c) {
		super(cons);
		this.c = c;
		num = new GeoNumeric(cons);
		setInputOutput(); // for AlgoElement
		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.Radius;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[]{c};
		setOnlyOutput(num);
		setDependencies(); // done by AlgoElement
	}

	public GeoNumeric getRadius() {
		return num;
	}

	GeoQuadricND getQuadricOrConic() {
		return c;
	}

	// set parameter of parabola
	@Override
	public final void compute() {
		if (c.type == GeoConicNDConstants.CONIC_CIRCLE) { // notice that
															// constants
															// CONIC_CIRCLE and
															// QUADRIC_SPHERE
															// are equal
			num.setValue(c.getHalfAxis(0));
		} else if (c.type == GeoConicNDConstants.CONIC_SINGLE_POINT) {
			num.setValue(0);
		} else {
			num.setUndefined();
		}
	}

	@Override
	final public String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return getLoc().getPlainDefault("RadiusOfA", "Radius of %0",
				c.getLabel(tpl));
	}

}
