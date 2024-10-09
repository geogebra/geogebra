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
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.util.DoubleUtil;

/**
 * Algo for converting decimals to fractions
 *
 */
public class AlgoFractionText extends AlgoElement {

	private GeoBoolean singleFraction;
	private GeoNumberValue num; // input
	private GeoText text; // output

	private double[] frac = { 0, 0 };

	private StringBuilder sb = new StringBuilder();

	/**
	 * @param cons
	 *            construction
	 * @param num
	 *            input number
	 */
	public AlgoFractionText(Construction cons, GeoNumberValue num, GeoBoolean singleFraction) {
		super(cons);
		this.num = num;
		this.singleFraction = singleFraction;

		text = new GeoText(cons);
		text.setIsTextCommand(true); // stop editing as text

		text.setLaTeX(true, false);

		setInputOutput();
		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.FractionText;
	}

	@Override
	protected void setInputOutput() {
		if (singleFraction == null) {
			input = new GeoElement[]{num.toGeoElement()};
		} else {
			input  = new GeoElement[]{num.toGeoElement(), singleFraction};
		}
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
	public void compute() {
		// StringTemplate tpl =
		// StringTemplate.get(app.getFormulaRenderingType());
		StringTemplate tpl = text.getStringTemplate();
		if (input[0].isDefined()) {
			frac = decimalToFraction(num.getDouble(),
					Kernel.STANDARD_PRECISION);

			sb.setLength(0);
			boolean asSingleFraction = singleFraction == null || singleFraction.getBoolean();
			appendFormula(sb, frac, tpl, asSingleFraction, kernel);

			text.setTextString(sb.toString());
			text.setLaTeX(true, false);

		} else {
			text.setTextString("?");
		}
	}

	// https://web.archive.org/web/20111027100847/http://homepage.smc.edu/kennedy_john/DEC2FRAC.PDF
	/**
	 * Algorithm To Convert A Decimal To A Fraction by John Kennedy Mathematics
	 * Department Santa Monica College 1900 Pico Blvd. Santa Monica, CA 90405
	 * http://homepage.smc.edu/kennedy_john/DEC2FRAC.PDF
	 * 
	 * @param decimal
	 *            to be converted to fraction
	 * @param accuracyFactor
	 *            accuracy
	 * @return [numerator, denominator]
	 */
	public static double[] decimalToFraction(double decimal,
			double accuracyFactor) {
		double fractionNumerator, fractionDenominator;
		double decimalSign;

		double[] ret = { 0, 0 };
		if (Double.isNaN(decimal)) {
			return ret; // return 0/0
		}

		if (decimal == Double.POSITIVE_INFINITY) {
			ret[0] = 1;
			ret[1] = 0; // 1/0
			return ret;
		}
		if (decimal == Double.NEGATIVE_INFINITY) {
			ret[0] = -1;
			ret[1] = 0; // -1/0
			return ret;
		}

		if (decimal < 0.0) {
			decimalSign = -1.0;
		} else {
			decimalSign = 1.0;
		}

		double decimalAbs = Math.abs(decimal);

		if (Math.abs(decimalAbs - Math.floor(decimalAbs)) < accuracyFactor) {
			// handles exact integers including 0
			fractionNumerator = decimalAbs * decimalSign;
			fractionDenominator = 1.0;
			ret[0] = fractionNumerator;
			ret[1] = fractionDenominator;
			return ret;
		}
		if (decimalAbs < 1.0E-19) { // X = 0 already taken care of
			fractionNumerator = decimalSign;
			fractionDenominator = 9999999999999999999.0;

			ret[0] = fractionNumerator;
			ret[1] = fractionDenominator;
			return ret;
		}
		if (decimalAbs > 1.0E19) {
			fractionNumerator = 9999999999999999999.0 * decimalSign;
			fractionDenominator = 1.0;

			ret[0] = fractionNumerator;
			ret[1] = fractionDenominator;
			return ret;
		}

		double z = decimalAbs;
		double previousDenominator = 0.0;
		double scratchValue;
		fractionDenominator = 1.0;
		do {
			z = 1.0 / (z - Math.floor(z));
			scratchValue = fractionDenominator;
			fractionDenominator = fractionDenominator * Math.floor(z)
					+ previousDenominator;
			previousDenominator = scratchValue;
			fractionNumerator = Math.floor(decimalAbs * fractionDenominator
					+ 0.5); // Rounding
																					// Function
		} while (Math
				.abs(decimalAbs - (fractionNumerator
						/ fractionDenominator)) > accuracyFactor
				&& !MyDouble.exactEqual(z, Math.floor(z)));
		fractionNumerator = decimalSign * fractionNumerator;

		ret[0] = fractionNumerator;
		ret[1] = fractionDenominator;
		return ret;
	}

	@Override
	public boolean isLaTeXTextCommand() {
		return true;
	}

	/**
	 * @param sb
	 *            builder
	 * @param left
	 *            numerator
	 * @param right
	 *            denominator
	 */
	public static void appendFraction(StringBuilder sb,
			String left, String right) {
		sb.append(" \\frac{ ");
		sb.append(left);
		sb.append(" }{ ");
		sb.append(right);
		sb.append(" } ");
	}

	/**
	 * Appends plus or minus infinity to sb
	 * 
	 * @param sb
	 *            builder
	 * @param tpl
	 *            template
	 * @param numer
	 *            numerator (to decide +-)
	 */
	public static void appendInfinity(StringBuilder sb, StringTemplate tpl,
			double numer) {
		if (numer > 0) {
			sb.append(" \\infty ");
		} else {
			sb.append(" - \\infty ");
		}
	}

	/**
	 * @param sb
	 *            builder
	 * @param frac
	 *            [numerator, denominator]
	 * @param tpl
	 *            output template
	 * @param kernel
	 *            kernel
	 */
	public static void appendFormula(StringBuilder sb, double[] frac,
			StringTemplate tpl, boolean asSingleFraction, Kernel kernel) {
		if (frac[1] == 1) { // integer
			sb.append(kernel.format(frac[0], tpl));
		} else if (frac[1] == 0) { // 1 / 0 or -1 / 0
			appendInfinity(sb, tpl, frac[0]);
		} else {
			if (!asSingleFraction && frac[0] < 0) {
				frac[0] *= -1;
				sb.append('-');
			}
			appendFraction(sb,
					kernel.format(DoubleUtil.checkDecimalFraction(frac[0]), tpl),
					kernel.format(DoubleUtil.checkDecimalFraction(frac[1]), tpl));
		}
	}

}
