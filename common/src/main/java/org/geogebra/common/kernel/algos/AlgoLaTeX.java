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

import java.util.ArrayList;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.geos.HasSymbolicMode;

/**
 * Returns a description of a GeoElement as a GeoText in LaTeX format.
 * 
 * @author Markus
 */
public class AlgoLaTeX extends AlgoElement {

	private GeoElement geo; // input
	private GeoBoolean substituteVars;
	private GeoBoolean showName;
	private GeoText text; // output

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label
	 * @param geo
	 *            element to be printed as LaTeX
	 * @param substituteVars
	 *            wheher to show value rather than definition
	 * @param showName
	 *            whether to append left hand side of the definition
	 */
	public AlgoLaTeX(Construction cons, String label, GeoElement geo,
			GeoBoolean substituteVars, GeoBoolean showName) {
		super(cons);
		this.geo = geo;
		this.substituteVars = substituteVars;
		this.showName = showName;
		text = new GeoText(cons);
		text.setLaTeX(true, false);

		if (geo instanceof HasSymbolicMode) {
			if (((HasSymbolicMode) geo).isSymbolicMode()) {
				text.setSymbolicMode(true, false);
			}
		}
		if (substituteVars == null) {
			text.setIsTextCommand(true);
		}
		setInputOutput(); // for AlgoElement

		// compute value of dependent number
		compute();
		text.setLabel(label);

		// set sans-serif LaTeX default
		text.setSerifFont(false);
	}

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param geo
	 *            element to be printed
	 */
	public AlgoLaTeX(Construction cons, String label, GeoElement geo) {
		this(cons, label, geo, null, null);
	}

	@Override
	public Commands getClassName() {
		return Commands.LaTeX;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {

		ArrayList<GeoElement> geos = new ArrayList<>();
		geos.add(geo);
		if (substituteVars != null) {
			geos.add(substituteVars);
		}
		if (showName != null) {
			geos.add(showName);
		}
		if (geo.isGeoText()) {
			((GeoText) geo).addTextDescendant(text);
		}
		input = new GeoElement[geos.size()];
		for (int i = 0; i < input.length; i++) {
			input[i] = geos.get(i);
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

	// calc the current value of the arithmetic tree
	@Override
	public final void compute() {

		// whether to use a formula renderer
		boolean useLaTeX = true;

		boolean substitute = substituteVars == null
				|| substituteVars.getBoolean();

		// undefined 0/0 should be ?, undefined If[x>0,"a"] should be ""
		if (!geo.isDefined() && !geo.isGeoText()) {
			text.setTextString("?");
		}
		if ((substituteVars != null && !substituteVars.isDefined())
				|| showName != null && !showName.isDefined()) {
			text.setTextString("");

		} else {

			boolean show = showName != null && showName.getBoolean();

			if (!geo.isLabelSet()) {
				// eg FormulaText[(1,1), true, true]
				show = false;
			}

			StringTemplate tpl = text.getStringTemplate().deriveReal();
			// Application.debug(geo.getFormulaString(StringType.LATEX,
			// substitute ));
			GeoElement geoToShow = geo;
			if (geo.getCorrespondingCasCell() != null) {
				// it's a twin geo, display the corresponding CAS cell
				geoToShow = geo.getCorrespondingCasCell();
			}
			if (show) {
				if (geoToShow.isGeoCasCell()) {
					// input: overriding rounding is probably OK
					text.setTextString(((GeoCasCell) geoToShow).getOutputOrInput(
									StringTemplate.numericLatex, substitute));

				} else {
					text.setTextString(
							geoToShow.getLaTeXAlgebraDescription(substitute,
									tpl));
				}
				if (text.getTextString() == null) {
					String desc = geoToShow
							.getAlgebraDescription(text.getStringTemplate());
					if (geoToShow.hasIndexLabel()) {
						desc = GeoElement.indicesToHTML(desc, true);
					}
					text.setTextString(desc);
					useLaTeX = false;
				}
			} else {
				if (geoToShow.isGeoText()) {
					// needed for eg Text commands eg FormulaText[Text[
					text.setTextString(((GeoText) geo).getTextString());
				} else if (geoToShow.isGeoCasCell() && ((GeoCasCell) geoToShow)
						.getValue() != null) {
					text.setTextString(
							((GeoCasCell) geoToShow).getValue()
									.toString(((GeoCasCell) geoToShow)
											.getLaTeXTemplate()));
				} else {
					text.setTextString(getGeoString(geoToShow, tpl, substitute));
				}
			}

		}

		text.setLaTeX(useLaTeX, false);
	}

	@Override
	public boolean isLaTeXTextCommand() {
		return true;
	}

}
