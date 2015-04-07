/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoDiameterLineVector.java
 *
 * Created on 30. August 2001, 21:37
 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;

/**
 *
 * @author Markus
 * @version
 */
public abstract class AlgoDiameterVectorND extends AlgoElement {

	protected GeoConicND c; // input
	protected GeoVectorND v; // input
	protected GeoLineND diameter; // output

	/** Creates new AlgoDiameterVector */
	public AlgoDiameterVectorND(Construction cons, String label, GeoConicND c,
			GeoVectorND v) {
		super(cons);
		this.v = v;
		this.c = c;
		createOutput(cons);

		setInputOutput(); // for AlgoElement

		compute();
		diameter.setLabel(label);
	}

	abstract protected void createOutput(Construction cons);

	@Override
	public Commands getClassName() {
		return Commands.Diameter;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = (GeoElement) v;
		input[1] = c;

		super.setOutputLength(1);
		super.setOutput(0, (GeoElement) diameter);
		setDependencies(); // done by AlgoElement
	}

	GeoVectorND getVector() {
		return v;
	}

	GeoConicND getConic() {
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
				v.getLabel(tpl));
	}

	// TODO Consider locusequability
}
