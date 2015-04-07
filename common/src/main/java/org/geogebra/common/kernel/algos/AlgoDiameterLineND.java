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
 * @version
 */
public abstract class AlgoDiameterLineND extends AlgoElement {

	protected GeoConicND c; // input
	protected GeoLineND g; // input
	protected GeoLineND diameter; // output

	/** Creates new AlgoJoinPoints */
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
	 * @param cons
	 *            construction
	 */
	abstract protected void createOutput(Construction cons);

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

		super.setOutputLength(1);
		super.setOutput(0, (GeoElement) diameter);
		setDependencies(); // done by AlgoElement
	}

	// Made public for LocusEqu
	public GeoLineND getLine() {
		return g;
	}

	// Made public for LocusEqu
	public GeoConicND getConic() {
		return c;
	}

	public GeoLineND getDiameter() {
		return diameter;
	}

	@Override
	final public String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return getLoc().getPlain("DiameterOfAConjugateToB", c.getLabel(tpl),
				g.getLabel(tpl));
	}

}
