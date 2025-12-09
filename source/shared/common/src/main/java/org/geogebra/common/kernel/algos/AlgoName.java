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

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;

/**
 * Returns the name of a GeoElement as a GeoText.
 * 
 * @author Markus
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
	 *            Element whose name should be used
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
	 *            Element whose name should be used
	 */
	public AlgoName(Construction cons, String label, GeoElement geo) {

		this(cons, geo);
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

		setOnlyOutput(text);
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

		String returnLabel = null;
		if (geo.getParentAlgorithm() instanceof AlgoListElement) {
			AlgoListElement algo = (AlgoListElement) geo.getParentAlgorithm();

			returnLabel = algo.getLabel();

		}
		if (returnLabel == null) {
			returnLabel = geo.getLabel(StringTemplate.realTemplate);
		}

		if (returnLabel != null) {
			text.setTextString(returnLabel);
		} else {
			// eg Name[a+3]
			text.setTextString(
					geo.getFormulaString(StringTemplate.realTemplate, false));
		}
	}

}
