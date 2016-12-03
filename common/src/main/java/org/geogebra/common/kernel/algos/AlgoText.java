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

package org.geogebra.common.kernel.algos;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Returns the name of a GeoElement as a GeoText.
 * 
 * @author Markus
 */
public class AlgoText extends AlgoElement {

	private GeoElement geo; // input
	private GeoBoolean substituteVars, latex; // optional input
	private GeoPointND startPoint, startPointCopy; // optional input
	private GeoText text; // output

	public AlgoText(Construction cons, String label, GeoElement geo) {
		this(cons, label, geo, null, null, null);
	}

	public AlgoText(Construction cons, String label, GeoElement geo,
			GeoBoolean substituteVars) {
		this(cons, label, geo, null, substituteVars, null);
	}

	public AlgoText(Construction cons, String label, GeoElement geo,
			GeoPointND p) {
		this(cons, label, geo, p, null, null);
	}

	public AlgoText(Construction cons, String label, GeoElement geo,
			GeoPointND p, GeoBoolean substituteVars) {
		this(cons, label, geo, p, substituteVars, null);
	}

	public AlgoText(Construction cons, String label, GeoElement geo,
			GeoPointND p, GeoBoolean substituteVars, GeoBoolean latex) {
		this(cons, geo, p, substituteVars, latex);
		text.setLabel(label);
	}

	public AlgoText(Construction cons, GeoElement geo, GeoPointND p,
			GeoBoolean substituteVars, GeoBoolean latex) {
		super(cons);
		this.geo = geo;
		this.startPoint = p;
		this.substituteVars = substituteVars;
		this.latex = latex;

		text = new GeoText(cons);
		text.setIsTextCommand(true); // stop editing as text

		// set startpoint
		if (startPoint != null) {
			startPointCopy = (GeoPointND) startPoint.copyInternal(cons);

			try {
				text.setStartPoint(startPointCopy);
			} catch (CircularDefinitionException e) {
				e.printStackTrace();
			}
			text.setAlwaysFixed(true); // disable dragging if p != null
		}

		setInputOutput(); // for AlgoElement

		// compute value of dependent number
		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.Text;
	}

	@Override
	public int getRelatedModeID() {
		return EuclidianConstants.MODE_TEXT;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {

		int inputs = 1;
		if (startPoint != null)
			inputs++;
		if (substituteVars != null)
			inputs++;
		if (latex != null)
			inputs++;

		int i = 0;
		input = new GeoElement[inputs];
		input[i++] = geo;
		if (geo.isGeoText())
			((GeoText) geo).addTextDescendant(text);
		if (startPoint != null)
			input[i++] = (GeoElement) startPoint;
		if (substituteVars != null)
			input[i++] = substituteVars;
		if (latex != null)
			input[i++] = latex;

		super.setOutputLength(1);
		super.setOutput(0, text);
		setDependencies(); // done by AlgoElement
	}

	public GeoText getGeoText() {
		return text;
	}

	@Override
	public final void compute() {

		// undefined text
		if (!geo.isDefined() || (startPoint != null && !startPoint.isDefined())
				|| (substituteVars != null && !substituteVars.isDefined())) {
			text.setUndefined();
			return;
		}

		// standard case: set text
		boolean bool = substituteVars == null ? true : substituteVars
				.getBoolean();
		boolean formula = latex == null ? false : latex.getBoolean();
		if (geo.isGeoText()) {
			// needed for eg Text commands eg Text[Text[
			text.setTextString(((GeoText) geo).getTextString());
		} else {
			text.setTextString(geo.getFormulaString(text.getStringTemplate(),
					bool));
		}
		text.setLaTeX(formula, false);
		text.update();

		// update startpoint position of text
		if (startPointCopy != null) {
			startPointCopy.setCoordsFromPoint(startPoint);
		}
	}

	
}
