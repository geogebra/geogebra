/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.kernelND.GeoPointND;

public class AlgoFractionTextPoint extends AlgoElement {

	private GeoPointND p; // input
	private GeoText text; // output

	private double xCoord[] = { 0, 0 };
	private double yCoord[] = { 0, 0 };
	private double zCoord[] = { 0, 0 };

	private StringBuilder sb = new StringBuilder();

	public AlgoFractionTextPoint(Construction cons, String label, GeoPointND p) {
		this(cons, p);
		text.setLabel(label);
	}

	AlgoFractionTextPoint(Construction cons, GeoPointND p) {
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

		setOutputLength(1);
		setOutput(0, text);
		setDependencies(); // done by AlgoElement
	}

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
			AlgoFractionText.appendFormula(sb, xCoord, tpl, kernel);
			sb.append(',');
			AlgoFractionText.appendFormula(sb, yCoord, tpl, kernel);
			if (p.getDimension() == 3) {
				sb.append(',');
				AlgoFractionText.appendFormula(sb, zCoord, tpl, kernel);
			}
			sb.append(" \\right) }");

			text.setTextString(sb.toString());

		} else
			text.setTextString("?");
	}

	@Override
	public boolean isLaTeXTextCommand() {
		return true;
	}

	

}
