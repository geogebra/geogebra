/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoSlope.java
 *
 * Created on 30. August 2001, 21:37
 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoNumeric;

/**
 *
 * @author Markus
 */
public class AlgoSlope extends AlgoElement implements DrawInformationAlgo {

	private GeoLine g; // input
	private GeoFunction f;
	private GeoNumeric slope; // output

	/**
	 * Creates new AlgoDirection
	 * 
	 * @param cons
	 *            construction
	 * @param g
	 *            line
	 */
	public AlgoSlope(Construction cons, GeoLine g, GeoFunction f) {
		super(cons);
		this.g = g;
		this.f = f;
		slope = new GeoNumeric(cons);
		setInputOutput(); // for AlgoElement

		compute();

		slope.setDrawableNoSlider();
	}

	/**
	 * For dummy copy only
	 * 
	 * @param g
	 *            line
	 */
	AlgoSlope(GeoLine g) {
		super(g.cons, false);
		this.g = g;
	}

	/**
	 * For dummy copy only
	 * 
	 * @param f
	 *            function
	 */
	AlgoSlope(GeoFunction f) {
		super(f.cons, false);
		this.f = f;
	}

	@Override
	public Commands getClassName() {
		return Commands.Slope;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_SLOPE;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = f == null ? g : f;

		setOnlyOutput(slope);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return resulting slope
	 */
	public GeoNumeric getSlope() {
		return slope;
	}

	/**
	 * Get start point of the slope triangle into array of coords.
	 * 
	 * @param coords
	 *            output coords
	 */
	public void getInhomPointOnLine(double[] coords) {
		if (g != null) {
			g.getInhomPointOnLine(coords);
			if (g.getStartPoint() == null) {
				// get point on y-axis and line g
				coords[0] = 0.0d;
				coords[1] = -g.z / g.y;
			}
		} else {
			coords[0] = 0;
			coords[1] = f.value(0);
		}
	}

	// direction vector of g
	@Override
	public final void compute() {
		if (g != null) {
			slope.setValue(g.getSlope());
		} else {
			if (f.isDefined()) {
				slope.setValue(
						f.getFunction().getDerivativeNoCAS(1).value(0));
			} else {
				slope.setUndefined();
			}
		}
	}

	@Override
	final public String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return getLoc().getPlainDefault("SlopeOfA", "Slope of %0",
				(g != null ? g : f).getLabel(tpl));
	}

	@Override
	public DrawInformationAlgo copy() {
		if (g != null) {
			return new AlgoSlope(g.copy());
		}
		return new AlgoSlope((GeoFunction) f.copy());
	}

}
