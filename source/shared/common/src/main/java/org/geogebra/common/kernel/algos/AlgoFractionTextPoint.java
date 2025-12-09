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
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * Text representation of point as (a/b, c/d).
 */
public class AlgoFractionTextPoint extends AlgoElement {

	private GeoPointND p; // input
	private GeoText text; // output

	private double[] xCoord = { 0, 0 };
	private double[] yCoord = { 0, 0 };
	private double[] zCoord = { 0, 0 };

	private StringBuilder sb = new StringBuilder();

	/**
	 * @param cons
	 *            construction
	 * @param p
	 *            point
	 */

	public AlgoFractionTextPoint(Construction cons, GeoPointND p) {
		super(cons);
		this.p = p;

		text = new GeoText(cons);

		text.setLaTeX(true, false);

		text.setIsTextCommand(true); // stop editing as text

		setInputOutput();
		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.FractionText;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = (GeoElement) p;

		setOnlyOutput(text);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return resulting text
	 */
	public GeoText getResult() {
		return text;
	}

	@Override
	public final void compute() {
		StringTemplate tpl = text.getStringTemplate();
		if (input[0].isDefined()) {

			Coords coords = p.getInhomCoords();

			xCoord = AlgoFractionText.decimalToFraction(coords.getX(),
					Kernel.STANDARD_PRECISION);
			yCoord = AlgoFractionText.decimalToFraction(coords.getY(),
					Kernel.STANDARD_PRECISION);
			zCoord = AlgoFractionText.decimalToFraction(coords.getZ(),
					Kernel.STANDARD_PRECISION);

			sb.setLength(0);
			sb.append("{ \\left( ");
			AlgoFractionText.appendFormula(sb, xCoord, tpl, true, kernel);
			sb.append(',');
			AlgoFractionText.appendFormula(sb, yCoord, tpl, true, kernel);
			if (p.getDimension() == 3) {
				sb.append(',');
				AlgoFractionText.appendFormula(sb, zCoord, tpl, true, kernel);
			}
			sb.append(" \\right) }");

			text.setTextString(sb.toString());

		} else {
			text.setTextString("?");
		}
	}

	@Override
	public boolean isLaTeXTextCommand() {
		return true;
	}

}
