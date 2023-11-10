/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoText;

/**
 * 
 * writes numbers in the forum 1.23 * 10 ^ -3 (in LaTeX)
 * 
 * @author michael
 *
 */
public class AlgoScientificText extends AlgoElement {

	private GeoNumeric num; // input
	private NumberValue precision; // input
	private GeoText text; // output

	private StringBuilder sb = new StringBuilder();

	/**
	 * @param cons
	 *            construction
	 * @param num
	 *            number
	 * @param precision
	 *            precision
	 */
	public AlgoScientificText(Construction cons, GeoNumeric num,
			GeoNumeric precision) {
		super(cons);
		this.num = num;
		this.precision = precision;

		text = new GeoText(cons);
		text.setIsTextCommand(true); // stop editing as text
		text.setLaTeX(true, false);

		setInputOutput();
		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.ScientificText;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[precision == null ? 1 : 2];
		input[0] = num;
		if (precision != null) {
			input[1] = (GeoElement) precision;
		}

		setOnlyOutput(text);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * Returns resulting text
	 * 
	 * @return resulting text
	 */
	public GeoText getResult() {
		return text;
	}

	@Override
	public void compute() {

		boolean rounding = precision != null;

		if (num.isDefined() && (precision == null || precision.isDefined())) {

			sb.setLength(0);

			double decimal = num.getDouble();

			int prec = precision == null ? 15 : (int) precision.getDouble();

			if (prec < 1 || prec > 15) {
				text.setUndefined();
				return;
			}

			StringTemplate stl = StringTemplate
					.printScientific(StringType.GEOGEBRA, prec, false);

			// returns string like 3456E-7
			String str = kernel.format(decimal, stl);

			String[] strs = str.split("E");

			if (strs.length != 2) {
				text.setUndefined();
				return;
			}

			sb.append(strs[0]);

			if (!rounding) {
				// we want 1.23 not 1.230000
				while (sb.charAt(sb.length() - 1) == '0') {
					sb.setLength(sb.length() - 1);
				}

				// for 1.0000 we need to remove the . too
				if (sb.charAt(sb.length() - 1) == '.') {
					sb.setLength(sb.length() - 1);
				}
			}

			// remove . from end (if it's there)
			int l = sb.length();
			if (sb.charAt(l - 1) == '.') {
				sb.setLength(l - 1);
			}

			sb.append(" \\times ");
			sb.append("10");
			sb.append("^{");
			sb.append(strs[1]);
			sb.append("}");

			/*
			 * Prefer LaTeX over unicode superscripts. Unicode doesn't work too well as
			 * Unicode.Superscript_0 is the wrong size.
			 */

			text.setTextString(sb.toString());
			text.setLaTeX(true, false);

		} else {
			text.setUndefined();
		}
	}

	@Override
	public boolean isLaTeXTextCommand() {
		return true;
	}

}
