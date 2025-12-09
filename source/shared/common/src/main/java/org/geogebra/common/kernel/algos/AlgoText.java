/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
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
import org.geogebra.common.util.debug.Log;

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
	 *            horizontal alignment for text [-1|0|1]
	 * @param verticalAlign
	 *            vertical alignment for text [-1|0|1]
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
	 *            horizontal alignment for text [-1|0|1]
	 * @param verticalAlign
	 *            vertical alignment for text [-1|0|1]
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
			text.setHorizontalAlignment((int) horizontalAlign.getValue());
			if (verticalAlign != null) {
				text.setVerticalAlignment((int) verticalAlign.getValue());
			}
		}

		// set startpoint
		if (startPoint != null) {
			startPointCopy = (GeoPointND) startPoint.copyInternal(cons);

			try {
				text.setStartPoint(startPointCopy);
			} catch (CircularDefinitionException e) {
				Log.debug(e);
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

		setOnlyOutput(text);
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
			text.setTextString(getGeoString(geo, text.getStringTemplate(), bool));
		}
		if (horizontalAlign != null) {
			text.setHorizontalAlignment((int) horizontalAlign.getValue());
		}
		if (verticalAlign != null) {
			text.setVerticalAlignment((int) verticalAlign.getValue());
		}
		text.setLaTeX(formula, false);
		text.update();

		// update startpoint position of text
		if (startPointCopy != null) {
			startPointCopy.setCoordsFromPoint(startPoint);
		}
	}

}
