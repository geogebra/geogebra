/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoDiameterLine.java
 *
 * Created on 30. August 2001, 21:37
 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoLineND;

/**
 *
 * @author Markus
 */
public abstract class AlgoDiameterLineND extends AlgoElement {
	/** input conic */
	protected GeoConicND c;
	/** input line */
	protected GeoLineND g;
	/** output diameter */
	protected GeoLineND diameter;

	/**
	 * Creates new algo for Diameter
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param c
	 *            conic
	 * @param g
	 *            parallel line
	 */
	public AlgoDiameterLineND(Construction cons, String label, GeoConicND c,
			GeoLineND g) {
		super(cons);
		this.c = c;
		this.g = g;
		createOutput(cons);

		setInputOutput(); // for AlgoElement

		compute();
		diameter.setLabel(label);
	}

	/**
	 * create the output needed
	 * 
	 * @param cons1
	 *            construction
	 */
	abstract protected void createOutput(Construction cons1);

	@Override
	public Commands getClassName() {
		return Commands.Diameter;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_POLAR_DIAMETER;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = (GeoElement) g;
		input[1] = c;

		setOnlyOutput(diameter);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * Made public for LocusEqu
	 * 
	 * @return line
	 */
	public GeoLineND getLine() {
		return g;
	}

	/**
	 * Made public for LocusEqu
	 * 
	 * @return conic
	 */
	public GeoConicND getConic() {
		return c;
	}

	/**
	 * @return result
	 */
	public GeoLineND getDiameter() {
		return diameter;
	}

	@Override
	final public String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return getLoc().getPlainDefault("DiameterOfAConjugateToB",
				"Diameter of %0 conjugate to %1", c.getLabel(tpl),
				g.getLabel(tpl));
	}

}
