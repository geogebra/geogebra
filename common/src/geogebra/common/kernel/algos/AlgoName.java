/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * AlgoDependentNumber.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoText;

/**
 * Returns the name of a GeoElement as a GeoText.
 * 
 * @author Markus
 * @version
 */
public class AlgoName extends AlgoElement {

	private GeoElement geo; // input
	private GeoText text; // output

	/**
	 * Creates text containing name of the geo
	 * 
	 * @param cons
	 *            Construction
	 * @param geo
	 *            Element whose name shoul be used
	 */
	public AlgoName(Construction cons, GeoElement geo) {


		super(cons);
		this.geo = geo;

		text = new GeoText(cons);
		text.setIsTextCommand(true); // stop editing as text
		setInputOutput(); // for AlgoElement

		// compute value of dependent number
		compute();
	}

	/**
	 * Creates text containing name of the geo
	 * 
	 * @param cons
	 *            Construction
	 * @param label
	 *            Label of the resulting text
	 * @param geo
	 *            Element whose name shoul be used
	 */
	public AlgoName(Construction cons, String label, GeoElement geo) {

		this(cons,geo);
		text.setLabel(label);
	}

	@Override
	public Commands getClassName() {
    	return Commands.Name;
    } 

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = geo;

		setOutputLength(1);
		setOutput(0, text);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * Returns the text element
	 * 
	 * @return text element containing the name
	 */
	public GeoText getGeoText() {
		return text;
	}

	// calc the current value of the arithmetic tree
	@Override
	public final void compute() {

		String returnLabel = geo.getLabel(StringTemplate.realTemplate);
		if (returnLabel != null) {
			text.setTextString(returnLabel);
		} else {
			// eg Name[a+3]
			text.setTextString(geo.getFormulaString(
					StringTemplate.realTemplate, false));
		}
	}

	// TODO Consider locusequability
}
