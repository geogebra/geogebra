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
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Returns the name of a GeoElement as a GeoText.
 * 
 * @author Markus
 */
public class AlgoText extends AlgoElement {

	private GeoElement geo; // input
	private GeoBoolean substituteVars; // optional input
	private GeoBoolean latex; // optional input
	private GeoPointND startPoint; // optional input
	private GeoPointND startPointCopy; // optional input
	private GeoNumeric horizontalAlign; // optional input
	private GeoNumeric verticalAlign; // optional input
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

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label
	 * @param geo
	 *            described geo
	 * @param p
	 *            start point
	 * @param substituteVars
	 *            whether to show variables as values
	 * @param latex
	 *            whether to use LaTeX output
	 * @param horizontalAlign
	 * 	     	  horizontal alignment for text [-1|0|1]
	 * @param verticalAlign
	 * 			  vertical alignment for text [-1|0|1]
	 */
	public AlgoText(Construction cons, String label, GeoElement geo,
			GeoPointND p, GeoBoolean substituteVars, GeoBoolean latex,
			GeoNumeric horizontalAlign, GeoNumeric verticalAlign) {
		this(cons, geo, p, substituteVars, latex, horizontalAlign, verticalAlign);
		text.setLabel(label);
	}

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label
	 * @param geo
	 *            described geo
	 * @param p
	 *            start point
	 * @param substituteVars
	 *            whether to show variables as values
	 * @param latex
	 *            whether to use LaTeX output
	 */
	public AlgoText(Construction cons, String label, GeoElement geo,
			GeoPointND p, GeoBoolean substituteVars, GeoBoolean latex) {
		this(cons, geo, p, substituteVars, latex, null, null);
		text.setLabel(label);
	}

	/**
	 * @param cons
	 *            construction
	 * @param geo
	 *            described geo
	 * @param p
	 *            start point
	 * @param substituteVars
	 *            whether to show variables as values
	 * @param latex
	 *            whether to use LaTeX output
	 * @param horizontalAlign
	 * 			  horizontal alignment for text [-1|0|1]
	 * @param verticalAlign
	 * 			  vertical alignment for text [-1|0|1]
	 */
	public AlgoText(Construction cons, GeoElement geo, GeoPointND p,
			GeoBoolean substituteVars, GeoBoolean latex,
			GeoNumeric horizontalAlign, GeoNumeric verticalAlign) {
		super(cons);
		this.geo = geo;
		this.startPoint = p;
		this.substituteVars = substituteVars;
		this.latex = latex;
		this.horizontalAlign = horizontalAlign;
		this.verticalAlign = verticalAlign;

		text = new GeoText(cons);
		text.setIsTextCommand(true); // stop editing as text
		if (horizontalAlign != null) {
			text.setNeedsUpdatedBoundingBox(true);
			text.update();
			text.setHorizontalAlignment(horizontalAlign);
			if (verticalAlign != null) {
				text.setVerticalAlignment(verticalAlign);
			}
		}

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
		if (startPoint != null) {
			inputs++;
		}
		if (substituteVars != null) {
			inputs++;
		}
		if (latex != null) {
			inputs++;
		}
		if (horizontalAlign != null) {
			inputs++;
			if (verticalAlign != null) {
				inputs++;
			}
		}

		int i = 0;
		input = new GeoElement[inputs];
		input[i++] = geo;
		if (geo.isGeoText()) {
			((GeoText) geo).addTextDescendant(text);
		}
		if (startPoint != null) {
			input[i++] = (GeoElement) startPoint;
		}
		if (substituteVars != null) {
			input[i++] = substituteVars;
		}
		if (latex != null) {
			input[i++] = latex;
		}
		if (horizontalAlign != null) {
			input[i++] = horizontalAlign;
			if (verticalAlign != null) {
				input[i++] = verticalAlign;
			}
		}

		super.setOutputLength(1);
		super.setOutput(0, text);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return resulting text
	 */
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
		boolean bool = substituteVars == null || substituteVars.getBoolean();
		boolean formula = latex != null && latex.getBoolean();
		if (geo.isGeoText()) {
			// needed for eg Text commands eg Text[Text[
			text.setTextString(((GeoText) geo).getTextString());
		} else {
			text.setTextString(
					getGeoString(geo, text.getStringTemplate(), bool));
		}
		text.setLaTeX(formula, false);
		text.update();

		// update startpoint position of text
		if (startPointCopy != null) {
			startPointCopy.setCoordsFromPoint(startPoint);
		}
	}

}
