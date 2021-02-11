/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.cas;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.apache.commons.math3.util.ArithmeticUtils;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoFractionText;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.MyMathExact.MyDecimal;
import org.geogebra.common.util.MyMathExact.MyDecimalMatrix;
import org.geogebra.common.util.debug.Log;

import com.himamis.retex.editor.share.util.Unicode;

/**
 * @author Tam
 *
 */
public class AlgoSurdText extends AlgoElement implements UsesCAS {

	// private DfpField decFull = new DfpField(64);
	// DfpField decLess = new DfpField(16);
	private final static int fullScale = 64;
	private final static int lessScale = 16;

	private GeoNumberValue num; // input
	private GeoList list; // input
	private GeoText text; // output

	private StringBuilder sb = new StringBuilder();

	// double debug0, debug1, debug2;
	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            label for output
	 * @param num
	 *            number
	 * @param list
	 *            list of hints
	 */
	public AlgoSurdText(Construction cons, String label, GeoNumberValue num,
			GeoList list) {
		this(cons, num, list);
		text.setLabel(label);
	}

	/**
	 * @param cons
	 *            construction
	 * @param num
	 *            number
	 * @param list
	 *            list of hints
	 */
	AlgoSurdText(Construction cons, GeoNumberValue num, GeoList list) {
		super(cons);
		this.num = num;
		this.list = list;

		text = new GeoText(cons);

		text.setLaTeX(true, false);

		text.setIsTextCommand(true); // stop editing as text

		setInputOutput();
		compute();

		// debug
		// IntRelationFinder irf = new IntRelationFinder(3, new
		// double[]{1.41421356,1,0.70710678}, decFull, decLess);
	}

	/**
	 * @param cons
	 *            construction
	 */
	public AlgoSurdText(Construction cons) {
		super(cons);
		// not needed, only called from SurdTextPoint
		// cons.addCASAlgo(this);
	}

	@Override
	public Commands getClassName() {
		return Commands.SurdText;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[list == null ? 1 : 2];
		input[0] = num.toGeoElement();
		if (list != null) {
			input[1] = list;
		}

		setOutputLength(1);
		setOutput(0, text);
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

		// make sure answer is formatted as eg \sqrt not sqrt
		StringTemplate tpl = text.getStringTemplate();
		if (input[0].isDefined()) {

			sb.setLength(0);

			/*
			 * int[] primes = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41,
			 * 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97, 101,
			 * 
			 * 103, 107, 109, 113, 127, 131, 137, 139, 149, 151, 157, 163, 167,
			 * 173, 179, 181, 191, 193, 197, 199, 211, 223, 227, 229, 233, 239,
			 * 
			 * 241, 251, 257, 263, 269, 271, 277, 281, 283, 293, 307, 311, 313,
			 * 317, 331, 337, 347, 349, 353, 359, 367, 373, 379, 383, 389, 397,
			 * 
			 * 401, 409, 419, 421, 431, 433, 439, 443, 449, 457, 461, 463, 467,
			 * 479, 487, 491, 499, 503, 509, 521, 523, 541, 547, 557, 563, 569,
			 * 
			 * 571, 577, 587, 593, 599, 601, 607, 613, 617, 619, 631, 641, 643,
			 * 647, 653, 659, 661, 673, 677, 683, 691, 701, 709, 719, 727, 733,
			 * 
			 * 739, 743, 751, 757, 761, 769, 773, 787, 797, 809, 811, 821, 823,
			 * 827, 829, 839, 853, 857, 859, 863, 877, 881, 883, 887, 907, 911,
			 * 
			 * 919, 929, 937, 941, 947, 953, 967, 971, 977, 983, 991, 997};
			 * 
			 * 
			 * debug0 = Double.MAX_VALUE; debug1 = Double.MAX_VALUE; debug2 =
			 * Double.MAX_VALUE; double debug0max = Double.MAX_VALUE; double
			 * debug1max = Double.MAX_VALUE; double debug2max =
			 * Double.MAX_VALUE;
			 * 
			 * 
			 * Log.debug("START"); for (int p = 0 ; p < primes.length / 2; p++)
			 * { for (int q = 1 ; q < 100 ; q++) { // up to 400
			 * 
			 * double num = q + Math.sqrt(primes[p]);
			 * 
			 * sb.setLength(0); PSLQappendQuadratic(sb, num, tpl);
			 * 
			 * 
			 * 
			 * if (!sb.toString().equals(q+"+\\sqrt{"+primes[p]+"}")) {
			 * //Log.debug ("error:"+sb.toString()+"       "
			 * +q+"+\\sqrt{"+primes[p]+"}");
			 * 
			 * if (Math.abs(debug0) > Math.abs(debug1) && Math.abs(debug0) >
			 * Math.abs(debug2)) { if (Math.abs(debug0) < debug0max) { debug0max
			 * = Math.abs(debug0); } } else if (Math.abs(debug1) >
			 * Math.abs(debug2) && Math.abs(debug1) > Math.abs(debug0)) { if
			 * (Math.abs(debug1) < debug1max) { debug1max = Math.abs(debug1); }
			 * } else if (Math.abs(debug2) > Math.abs(debug1) &&
			 * Math.abs(debug2) > Math.abs(debug0)) { if (Math.abs(debug2) <
			 * debug2max) { debug2max = Math.abs(debug2); } } }
			 * 
			 * 
			 * for (int r = 2 ; r < 100 ; r++) { num = (q +
			 * Math.sqrt(primes[p]))/r;
			 * 
			 * sb.setLength(0); PSLQappendQuadratic(sb, num, tpl);
			 * 
			 * if (sb.toString().indexOf("\\frac") > -1 &&
			 * !sb.toString().equals(
			 * "\\frac{"+q+"+\\sqrt{"+primes[p]+"}}{"+r+"}")) {
			 * //Log.debug("error:" +sb.toString()+"       \\frac{"
			 * +q+"+\\sqrt{"+primes [p]+"}}{"+r+"}      "+sbDebug.toString());
			 * 
			 * 
			 * if (Math.abs(debug0) > Math.abs(debug1) && Math.abs(debug0) >
			 * Math.abs(debug2)) { if (Math.abs(debug0) < debug0max) { debug0max
			 * = Math.abs(debug0); } } else if (Math.abs(debug1) >
			 * Math.abs(debug2) && Math.abs(debug1) > Math.abs(debug0)) { if
			 * (Math.abs(debug1) < debug1max) { debug1max = Math.abs(debug1); }
			 * } else if (Math.abs(debug2) > Math.abs(debug1) &&
			 * Math.abs(debug2) > Math.abs(debug0)) { if (Math.abs(debug2) <
			 * debug2max) { debug2max = Math.abs(debug2); } }
			 * 
			 * 
			 * }
			 * 
			 * }
			 * 
			 * } } Log.debug("END "+debug0max+" "+debug1max+" "+debug2max);
			 */

			double decimal = num.getDouble();

			if (DoubleUtil.isEqual(decimal - Math.round(decimal), 0.0,
					Kernel.MAX_PRECISION)) {
				sb.append(kernel.format(Math.round(decimal), tpl));
			} else {

				if (list == null) {
					pslqAppendQuadratic(sb, decimal, tpl);
				} else {
					pslqAppendGeneral(sb, decimal, tpl);
				}
			}

			text.setTextString(sb.toString());
			text.setLaTeX(true, false);

		} else {

			// if the number is undefined, display a "?"
			text.setTextString("?");
			text.setLaTeX(true, false);

			// old behaviour
			// text.setUndefined();
		}
	}

	private void fractionAppend(StringBuilder sBuilder, int numer0, int denom0,
			StringTemplate tpl) {
		int numer = numer0;
		int denom = denom0;
		if (denom < 0) {
			denom = -denom;
			numer = -numer;
		}

		// maybe we can simplify
		int gcdiv = (int) Kernel.gcd(Math.abs(numer), denom);
		if (gcdiv != 1) {
			denom = denom / gcdiv;
			numer = numer / gcdiv;
		}

		if (denom == 1) { // integer
			sBuilder.append(kernel.format(numer, tpl));
		} else if (denom == 0) { // 1 / 0 or -1 / 0
			AlgoFractionText.appendInfinity(sBuilder, tpl, numer);
		} else {
			boolean negative = numer < 0;
			if (negative) {
				numer = -numer;
				sBuilder.append("-");
			}
			AlgoFractionText.appendFraction(sBuilder, tpl,
					kernel.format(numer, tpl), kernel.format(denom, tpl));

		}
	}

	/**
	 * Goal: modifies a StringBuilder object sb to be a radical up to quartic
	 * roots The precision is adapted, according to setting
	 * 
	 * @param sBuilder
	 *            string builder
	 * @param number
	 *            number to be converted
	 * @param tpl
	 *            template for CAS and formal solution
	 */
	protected void pslqAppendGeneral(StringBuilder sBuilder, double number,
			StringTemplate tpl) {

		// Zero Test: Is num 0?
		if (DoubleUtil.isZero(number)) {
			sBuilder.append(kernel.format(0, tpl));
			return;
		}

		// Rational Number Test. num is not 0. Is num rational (with small
		// denominator <= 1000) ?
		AlgebraicFit fitter = new AlgebraicFit(null, null,
				AlgebraicFittingType.RATIONAL_NUMBER, tpl);
		fitter.setCoeffBound(1000);
		fitter.compute(number);

		ValidExpression ve = sbToCAS(fitter.formalSolution);

		if (fitter.formalSolution.length() > 0
				&& DoubleUtil.isEqual(ve.evaluateDouble(), number)) {
			sBuilder.append(kernel.getGeoGebraCAS().evaluateGeoGebraCAS(ve,
					null, tpl, null, kernel));
			return;
		}

		double[] testValues;
		String[] testNames;

		if (list != null) {

			ArrayList<Double> values = new ArrayList<>();
			ArrayList<String> names = new ArrayList<>();

			for (int i = 0; i < list.size(); i++) {
				double x = list.get(i).evaluateDouble();

				if (DoubleUtil.isEqual(x, Math.PI)) {
					values.add(Math.PI);
					names.add("pi");
				} else if (DoubleUtil.isEqual(x, 1 / Math.PI)) {
					values.add(1 / Math.PI);
					names.add("1/pi");
				} else if (DoubleUtil.isEqual(x, Math.PI * Math.PI)) {
					values.add(Math.PI * Math.PI);
					names.add("pi^2");
				} else if (DoubleUtil.isEqual(x, Math.sqrt(Math.PI))) {
					values.add(Math.sqrt(Math.PI));
					names.add("sqrt(pi)");
				} else if (DoubleUtil.isEqual(x, Math.E)) {
					values.add(Math.E);
					names.add(Unicode.EULER_STRING);
				} else if (DoubleUtil.isEqual(x, 1 / Math.E)) {
					values.add(1 / Math.E);
					names.add("1/" + Unicode.EULER_STRING);
				} else if (DoubleUtil.isEqual(x, Math.E * Math.E)) {
					values.add(Math.E * Math.PI);
					names.add(Unicode.EULER_STRING + "^2");
				} else if (DoubleUtil.isEqual(x, Math.sqrt(Math.E))) {
					values.add(Math.sqrt(Math.E));
					names.add("sqrt(" + Unicode.EULER_STRING + ")");
				} else {
					int j;
					for (j = 2; j < 100; j++) {
						double sqrt = Math.sqrt(j);
						if (!DoubleUtil.isInteger(sqrt)
								&& DoubleUtil.isEqual(x, sqrt)) {
							values.add(sqrt);
							names.add("sqrt(" + j + ")");
							break;
						}

						double ln = Math.log(j);
						if (DoubleUtil.isEqual(x, ln)) {
							values.add(ln);
							names.add("ln(" + j + ")");
							break;
						}
					}
				}
			}

			testValues = new double[values.size()];
			testNames = new String[values.size()];

			for (int i = 0; i < values.size(); i++) {
				testValues[i] = values.get(i);
				testNames[i] = names.get(i);

				// Log.debug(testNames[i]);
			}

		} else {

			// default constants if none supplied
			testValues = new double[] { Math.sqrt(2.0), Math.sqrt(3.0),
					Math.sqrt(5.0), Math.sqrt(6.0), Math.sqrt(7.0),
					Math.sqrt(10.0), Math.PI };
			testNames = new String[] { "sqrt(2)", "sqrt(3)", "sqrt(5)",
					"sqrt(6)", "sqrt(7)", "sqrt(10)", "pi" };
		}

		boolean success = fitLinearComb(number, testNames, testValues, 100,
				sBuilder, tpl);

		if (success) {
			return;
		}

		sBuilder.append(kernel.format(number, StringTemplate.maxPrecision));

	}

	private boolean fitLinearComb(double y, String[] constNameSet,
			double[] constValueSet, int coeffBound, StringBuilder sb1,
			StringTemplate tpl) {

		// long t1= System.currentTimeMillis();
		// long t2;

		AlgebraicFit fitter0 = new AlgebraicFit(constNameSet, constValueSet,
				AlgebraicFittingType.LINEAR_COMBINATION, tpl);
		fitter0.setCoeffBound(coeffBound);
		fitter0.compute(y);

		// t2 = System.currentTimeMillis();
		// System.out.println("time of algebraic fit compute: " + (t2-t1));
		// t1 = t2;

		ValidExpression ve0 = sbToCAS(fitter0.formalSolution);

		// t2 = System.currentTimeMillis();
		// System.out.println("time of sb to ve: " + (t2-t1));
		// t1 = t2;

		if (fitter0.formalSolution.length() > 0
				&& DoubleUtil.isEqual(ve0.evaluateDouble(), y)) {
			sb1.append(kernel.getGeoGebraCAS().evaluateGeoGebraCAS(ve0, null,
					tpl, null, kernel));

			// t2 = System.currentTimeMillis();
			// System.out.println("time of ve to CAS: " + (t2-t1));
			// t1 = t2;

			return true;
		}
		return false;

	}

	private ValidExpression sbToCAS(StringBuilder sBuilder) {
		if (sBuilder != null) {
			return kernel.getGeoGebraCAS().getCASparser()
					.parseGeoGebraCASInputAndResolveDummyVars(
							sBuilder.toString(), getKernel(), null);
		}
		return null;
	}

	/**
	 * returns the sum of constValue[j] * coeffs[offset+j*step] over j
	 * 
	 * @param n
	 *            number of summands
	 * @param constValue
	 *            values
	 * @param coeffs
	 *            coefficient
	 * @param offset
	 *            coeffs offset
	 * @param step
	 *            coefficients index step
	 * @return linear combination
	 */
	static double evaluateCombination(int n, double[] constValue, int[] coeffs,
			int offset, int step) {
		double sum = 0;

		for (int j = 0; j < n; j++) {
			sum += constValue[j] * coeffs[offset + j * step];
		}

		return sum;
	}

	// append a linear combination coeffs[offset + j*step] * vars[j] to the
	// StringBuilder sb
	/**
	 * @param sbToCAS
	 *            cas string builder
	 * @param numOfTerms
	 *            number of terms
	 * @param vars
	 *            variables
	 * @param coeffs
	 *            coefficients
	 * @param offset
	 *            coefficients index offset
	 * @param step
	 *            coefficients index step
	 * @param tpl
	 *            template sending numbers to CAS
	 */
	void appendCombination(StringBuilder sbToCAS, int numOfTerms, String[] vars,
			int[] coeffs, int offset, int step, StringTemplate tpl) {

		int numOfAllTerms = vars.length;
		if (numOfAllTerms - 1 > Math
				.floor((coeffs.length - 1 - step - offset) / (double) step)) { // checksum
			// appendUndefined();
			return;
		}

		if (numOfTerms == 0) {
			return;
		}

		int counter = numOfTerms - 1; // number of pluses

		for (int j = 0; j < numOfAllTerms; j++) {

			// if (coeffs[offset+j*step]==0) {
			// continue;
			// } else if (coeffs[offset+j*step]!=1) {
			sbToCAS.append(kernel.format(coeffs[offset + j * step], tpl));
			sbToCAS.append("*");
			// }

			sbToCAS.append(vars[j]);

			if (counter > 0) {
				sbToCAS.append(" + ");
				counter--;
			}

		}

	}

	private void appendUndefined(StringBuilder sb1,
			double num1) {

		// eg SurdText[1.23456789012345] returns 1.23456789012345
		sb1.append(kernel.format(num1, StringTemplate.maxPrecision));
	}

	/**
	 * Goal: modifies a StringBuilder object sb to be a radical up to quartic
	 * roots The precision is adapted, according to setting
	 * 
	 * @param sBuilder
	 *            string builder
	 * @param num1
	 *            converted number
	 * @param tpl
	 *            output template
	 */
	protected void pslqAppendQuartic(StringBuilder sBuilder, double num1,
			StringTemplate tpl) {
		double[] numPowers = new double[5];
		double temp = 1.0;

		for (int i = 4; i >= 0; i--) {
			numPowers[i] = temp;
			temp *= num1;
		}

		int[] coeffs = pslq(numPowers, Kernel.STANDARD_PRECISION, 10);

		if (coeffs[0] == 0 && coeffs[1] == 0) {

			if (coeffs[2] == 0 && coeffs[3] == 0 && coeffs[4] == 0) {
				appendUndefined(sBuilder, num1);
			} else if (coeffs[2] == 0) {
				// coeffs[1]: denominator; coeffs[2]: numerator
				int denom = coeffs[3];
				int numer = -coeffs[4];
				fractionAppend(sBuilder, numer, denom, tpl);

			} else {

				// coeffs, if found, shows the equation
				// coeffs[2]+coeffs[1]x+coeffs[0]x^2=0"
				// We want x=\frac{a +/- b1\sqrt{b2}}{c}
				// where c=coeffs[0], a=-coeffs[1], b=coeffs[1]^2 -
				// 4*coeffs[0]*coeffs[2]
				int a = -coeffs[3];
				int b2 = coeffs[3] * coeffs[3] - 4 * coeffs[2] * coeffs[4];
				int b1 = 1;
				int c = 2 * coeffs[2];

				if (b2 <= 0) { // should not happen!
					appendUndefined(sBuilder, num1);
					return;
				}

				// free the squares of b2
				while (b2 % 4 == 0) {
					b2 = b2 / 4;
					b1 = b1 * 2;
				}
				for (int s = 3; s <= Math.sqrt(b2); s += 2) {
					while (b2 % (s * s) == 0) {
						b2 = b2 / (s * s);
						b1 = b1 * s;
					}
				}

				if (c < 0) {
					a = -a;
					c = -c;
				}

				boolean positive;
				if (num1 > (a + 0.0) / c) {
					positive = true;
					if (b2 == 1) {
						a += b1;
						b1 = 0;
						b2 = 0;
					}
				} else {
					positive = false;
					if (b2 == 1) {
						a -= b1;
						b1 = 0;
						b2 = 0;
					}
				}

				int gcd = ArithmeticUtils.gcd(ArithmeticUtils.gcd(a, b1), c);
				if (gcd != 1) {
					a = a / gcd;
					b1 = b1 / gcd;
					c = c / gcd;
				}

				ExpressionNode en;

				if (DoubleUtil.isZero(b1)) {
					// eg SurdText[0.33]
					// eg SurdText[0.235]
					en = new ExpressionNode(kernel, a);
				} else {
					en = (new ExpressionNode(kernel, b2)).sqrt().multiplyR(b1);

					if (positive) {
						en = en.plusR(a);
					} else {
						en = en.subtractR(a);
					}
				}

				en = en.divide(c);

				sBuilder.append(en.toString(tpl));

			}
		} else if (coeffs[0] == 0) {
			sBuilder.append("Root of a cubic equation: ");
			sBuilder.append(kernel.format(coeffs[1], tpl));
			sBuilder.append("x^3 + ");
			sBuilder.append(kernel.format(coeffs[2], tpl));
			sBuilder.append("x^2 + ");
			sBuilder.append(kernel.format(coeffs[3], tpl));
			sBuilder.append("x + ");
			sBuilder.append(kernel.format(coeffs[4], tpl));

		} else {
			sBuilder.append("Root of a quartic equation: ");
			sBuilder.append(kernel.format(coeffs[0], tpl));
			sBuilder.append("x^4 + ");
			sBuilder.append(kernel.format(coeffs[1], tpl));
			sBuilder.append("x^3 + ");
			sBuilder.append(kernel.format(coeffs[2], tpl));
			sBuilder.append("x^2 + ");
			sBuilder.append(kernel.format(coeffs[3], tpl));
			sBuilder.append("x + ");
			sBuilder.append(kernel.format(coeffs[4], tpl));

		}

	}

	/**
	 * Quadratic Case. modifies a StringBuilder object sb to be the
	 * quadratic-radical expression of num, within certain precision.
	 * 
	 * @param sBuilder
	 *            string builder
	 * @param num1
	 *            number
	 * @param tpl
	 *            output template
	 */
	protected void pslqAppendQuadratic(StringBuilder sBuilder, double num1,
			StringTemplate tpl) {

		if (DoubleUtil.isZero(num1)) {
			sBuilder.append("0");
			return;
		}

		double[] numPowers = { num1 * num1, num1, 1.0 };
		int[] coeffs = pslq(numPowers, 1E-10, 10);

		if (coeffs == null) {
			appendUndefined(sBuilder, num1);
			return;
		}

		// debug0 = coeffs[0];
		// debug1 = coeffs[1];
		// debug2 = coeffs[2];

		// Log.debug(coeffs[0]+" "+coeffs[1]+" "+coeffs[2]);
		if ((coeffs[0] == 0 && coeffs[1] == 0 && coeffs[2] == 0)

				// try to minimize possibility of wrong answer
				// and maximize usefulness
				// numbers determined by commented-out code in compute() method
				|| Math.abs(coeffs[0]) > 570 || Math.abs(coeffs[1]) > 729
				|| Math.abs(coeffs[2]) > 465) {
			// Log.debug(coeffs[0]+" "+coeffs[1]+" "+coeffs[2]);
			appendUndefined(sBuilder, num1);
		} else if (coeffs[0] == 0) {
			// coeffs[1]: denominator; coeffs[2]: numerator
			int denom = coeffs[1];
			int numer = -coeffs[2];
			fractionAppend(sBuilder, numer, denom, tpl);

		} else {

			// coeffs, if found, shows the equation
			// coeffs[2]+coeffs[1]x+coeffs[0]x^2=0"
			// We want x=\frac{a +/- b1\sqrt{b2}}{c}
			// where c=coeffs[0], a=-coeffs[1], b=coeffs[1]^2 -
			// 4*coeffs[0]*coeffs[2]
			int a = -coeffs[1];
			int b2 = coeffs[1] * coeffs[1] - 4 * coeffs[0] * coeffs[2];
			int b1 = 1;
			int c = 2 * coeffs[0];

			if (b2 <= 0) { // should not happen!
				appendUndefined(sBuilder, num1);
				return;
			}

			// free the squares of b2
			while (b2 % 4 == 0) {
				b2 = b2 / 4;
				b1 = b1 * 2;
			}
			for (int s = 3; s <= Math.sqrt(b2); s += 2) {
				while (b2 % (s * s) == 0) {
					b2 = b2 / (s * s);
					b1 = b1 * s;
				}
			}

			if (c < 0) {
				a = -a;
				c = -c;
			}

			boolean positive;
			if (num1 > (a + 0.0) / c) {
				positive = true;
				if (b2 == 1) {
					a += b1;
					b1 = 0;
					b2 = 0;
				}
			} else {
				positive = false;
				if (b2 == 1) {
					a -= b1;
					b1 = 0;
					b2 = 0;
				}
			}

			int gcd = ArithmeticUtils.gcd(ArithmeticUtils.gcd(a, b1), c);
			if (gcd != 1) {
				a = a / gcd;
				b1 = b1 / gcd;
				c = c / gcd;
			}

			ExpressionNode en;
			if (DoubleUtil.isZero(b1)) {
				// eg SurdText[0.33]
				// eg SurdText[0.235]
				en = new ExpressionNode(kernel, a);
			} else {
				en = (new ExpressionNode(kernel, b2)).sqrt().multiplyR(b1);

				// eg SurdText((-7 * 3^(1 / 2)) / 2)
				if (DoubleUtil.isZero(a)) {
					if (!positive) {
						// make sure minus sign is before fraction
						if (DoubleUtil.isEqual(c, 1)) {
							en = (new ExpressionNode(kernel, b2)).sqrt().multiplyR(-b1);
						} else {
							en = en.divide(c).multiplyR(-1);
						}
						sBuilder.append(en.toString(tpl));
						return;
					}
				} else {
					en = positive ? en.plusR(a) : en.subtractR(a);
				}
			}
			en = en.divide(c);

			sBuilder.append(en.toString(tpl));
		}
	}

	private static int[] pslq(double[] x, double AccuracyFactor, int bound) {
		return pslq(x.length, x, AccuracyFactor, bound, null, null);
	}

	/*
	 * Algorithm PSLQ from Ferguson and Bailey (1992)
	 */
	private static int[] pslq(int n, double[] x_input, double accuracyFactor,
			int bound, int[][] B_mutable, double[] xB_mutable) {

		double[] x = new double[n];
		for (int i = 0; i < n; i++) { // need a copy of the input
			x[i] = x_input[i];
		}

		// returning single solution
		int[] coeffs = new int[n];

		double[] xB;
		if (xB_mutable == null) {
			xB = new double[n];
		} else {
			xB = xB_mutable;
		}

		int[][] B;
		if (B_mutable == null) {
			B = new int[n][n];
		} else {
			B = B_mutable;
		}

		// other working variables

		for (int i = 0; i < n; i++) {
			coeffs[i] = 0;
		}

		if (n <= 1) {
			return coeffs;
		}

		for (int i = 0; i < n; i++) {
			if (Double.isNaN(x[i])) {
				return coeffs;
			}
		}

		// PSLQ Algorithm (Ferguson et al, 1999)
		//
		// Initialize:
		// Given input array x=(x[0]..x[n-1]),
		// define partial sum of squares ss[j] = sum of x[k]x[k] from k=j to n-1
		// define Hx as n x (n-1) matrix, where H[i][j] = ss[i+1]/ss[i] if
		// 1<=i=j<=n-1, =-x[i]x[j]/(s[j]s[j+1]) if 1<=j<i<=n, =0 otherwise
		// define Px as I_n - (xt.x)
		// [Debug] check the following properties -- (1)Hxt.Hx=I_(n-1), (2)
		// |Hx|^2=|Px|^2=n-1, (3)x.Hx=0, (4)Pxt=Px, (5)Px=Hx.Hxt
		// [Important Note] Px.mt = mt for any m such that m.xt = 0
		//
		//
		//
		//
		// set H = Hx, A=B=I_n
		// perform Hermite reduction on H, producing D

		// Fix a constant gamma > 2/sqrt(3)
		// replace x by xD^-1, H by DH, A by DA, B by BD^-1
		//
		// 4-step iteration. Step 1 "Exchange", Step 2 "Corner", Step 3
		// "Reduction", Step 4 "Termination"
		//
		// Number of iteration: if a relation m exists within norm M, then we
		// need no more than (n(n-1)/2) log(gamma^(n-1)M)/log(tau) iterations.
		// Quality of the result: the result found by PSLQ has norm no more than
		// gamma^(n-2)*M
		// Multiple relations: when one of the coordinate of xB is zero, the
		// corresponding column of B will be a relation. Then we may use the
		// n-1 remaining coordinates to find another relation.

		// x=(x1..xn), xB=(y1..yn) where yj=0, y=(y1..~yj..yn), yC=(z1..zn-1),
		// zk=0.
		// m=B[,j] is a relation for x, m2=C[,k] is a relation for (xB[,1],.
		// ~xB[,j],..xB[,n])
		// x.B[,1] C[1,k]
		// xBCD

		// normalize x
		double normX = 0;
		for (int i = 0; i < n; i++) {
			normX += x[i] * x[i];
		}
		normX = Math.sqrt(normX);
		for (int i = 0; i < n; i++) {
			x[i] = x[i] / normX;
		}

		// partial sums of squares
		double[] ss = new double[n];
		ss[n - 1] = x[n - 1] * x[n - 1];
		for (int i = n - 2; i >= 0; i--) {
			ss[i] = ss[i + 1] + x[i] * x[i];
		}
		for (int i = 0; i < n; i++) {
			ss[i] = Math.sqrt(ss[i]);
		}

		// pre-calculate ss[j]*ss[j+1]
		double[] Pss = new double[n - 1];
		for (int i = 0; i < n - 1; i++) {
			Pss[i] = ss[i] * ss[i + 1];
		}

		// initialize Matrix H (lower trapezoidal
		double[][] H = new double[n][n - 1];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < i; j++) {
				H[i][j] = -x[i] * x[j] / Pss[j];
			}

			if (i < n - 1) {
				H[i][i] = ss[i + 1] / ss[i];
			}

			for (int j = i + 1; j < n - 1; j++) {
				H[i][j] = 0;
			}
		}

		// test property of H: the n-1 columns are orthogonal
		/*
		 * for (int i =0 ; i<n-1; i++) { for (int j=0; j<n-1; j++) { double sum
		 * = 0; for (int k=0; k<n; k++) { sum += H[k][i]*H[k][j]; }
		 * System.out.println(sum); } }
		 */

		// matrix P = In - x.x
		// P = new double[n][n];
		// for (int i = 0; i < n; i++)
		// for (int j = 0; j < n; j++)
		// P[i][j] = -x[i] * x[j];
		// for (int i = 0; i < n; i++)
		// P[i][i] += 1;

		// debug: |P|^2=|H|^2 = n-1
		// AbstractApplication.debug("Frobenius Norm Squares: \n"
		// + "|P|^2 = " + frobNormSq(P,n,n)
		// + "|H|^2 = " + frobNormSq(H,n,n-1)
		// );

		// initialize matrices R
		int[][][] R = new int[n - 1][n][n];
		for (int j = 0; j < n - 1; j++) {
			for (int i = 0; i < n; i++) {
				for (int k = 0; k < n; k++) {
					R[j][i][k] = 0;
				}
			}
			for (int i = 0; i < n; i++) {
				R[j][i][i] = 1;
			}
			R[j][j][j] = 0;
			R[j][j][j + 1] = 1;
			R[j][j + 1][j] = 1;
			R[j][j + 1][j + 1] = 0;
		}

		double gamma = 1.5;
		double deltaSq = 3.0 / 4 - (1.0 / gamma) / gamma;

		// initialize A, B = I_n
		int[][] A = new int[n][n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				A[i][j] = 0;
			}
		}
		for (int i = 0; i < n; i++) {
			A[i][i] = 1;
		}
		// B = new int[n][n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				B[i][j] = 0;
			}
		}
		for (int i = 0; i < n; i++) {
			B[i][i] = 1;
		}

		// iteration
		int itCount = 0;
		double itBound = 2.0 * gamma / deltaSq * n * n * (n + 1)
				* Math.log(Math.sqrt(bound * bound * n) * n * n) / Math.log(2);
		// AbstractApplication.debug("itBound = " + itBound);
		while (itCount < itBound) {

			// 0. test if we have found a relation in a column of B
			// xB = new double[n];
			boolean solutionFound = false;
			boolean firstSolutionRecorded = false;
			for (int i = 0; i < n; i++) {
				xB[i] = 0;
				for (int k = 0; k < n; k++) {
					xB[i] += x[k] * B[k][i];
				}
				if (DoubleUtil.isEqual(xB[i], 0, accuracyFactor / normX)) {

					solutionFound = true;

					if (!firstSolutionRecorded) {
						for (int k = 0; k < n; k++) {
							coeffs[k] = B[k][i];
						}

						firstSolutionRecorded = true;
					}
				}
			}

			if (solutionFound) {
				return coeffs;
			}
			// 0.5. calculate D, E
			// matrix D
			int[][] D = new int[n][n];
			double[][] D0 = new double[n][n]; // testing
			for (int i = 0; i < n; i++) {
				// define backwards. the 0's and 1's should be defined first.
				for (int j = n - 1; j >= i + 1; j--) {
					D[i][j] = 0;
					D0[i][j] = 0;
				}
				D[i][i] = 1;
				D0[i][i] = 1;

				for (int j = i - 1; j >= 0; j--) {
					double sum = 0;
					double sum0 = 0;
					for (int k = j + 1; k <= i; k++) {
						sum += D[i][k] * H[k][j];
						sum0 += D0[i][k] * H[k][j];
					}

					D[i][j] = (int) Math.floor(-1.0 / H[j][j] * sum + 0.5);
					D0[i][j] = -1.0 / H[j][j] * sum0;
				}

			}

			// matrix E = D^{-1}
			int[][] E = new int[n][n];
			for (int i = 0; i < n; i++) {
				// define backwards. the 0's and 1's should be defined first.
				for (int j = n - 1; j >= i + 1; j--) {
					E[i][j] = 0;
				}
				E[i][i] = 1;
				for (int j = i - 1; j >= 0; j--) {
					int sum = 0;
					for (int k = j + 1; k <= i; k++) {
						sum += E[i][k] * D[k][j];
					}

					E[i][j] = -sum;
				}

			}

			// 1. replace H by DH
			double[][] newH = new double[n][n - 1];
			double[][] newH0 = new double[n][n - 1];
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n - 1; j++) {
					newH[i][j] = 0;
					newH0[i][j] = 0;
					for (int k = 0; k < n; k++) {
						newH[i][j] += D[i][k] * H[k][j];
						newH0[i][j] += D0[i][k] * H[k][j];
					}

				}
			}

			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n - 1; j++) {
					H[i][j] = newH[i][j];
				}
			}

			// 2. find j to maximize gamma^j |h_jj|
			double gammaPow = 1;
			double temp;
			double max = 0;
			int index = 0;

			for (int j = 0; j < n - 1; j++) {
				gammaPow *= gamma;
				temp = gammaPow * Math.abs(H[j][j]);
				if (max < temp) {
					max = temp;
					index = j;
				}
			}

			// 2.5 calculate matrices G[0], G[1],... G[n-2]
			double[][][] G = new double[n - 1][n - 1][n - 1];
			for (int i = 0; i < n - 1; i++) {
				for (int k = 0; k < n - 1; k++) {
					G[n - 2][i][k] = 0;
				}
			}
			for (int i = 0; i < n - 1; i++) {
				G[n - 2][i][i] = 1;
			}

			for (int j = 0; j < n - 2; j++) {
				double b = H[j + 1][j];
				double c = H[j + 1][j + 1];
				double d = Math.sqrt(b * b + c * c);
				for (int i = 0; i < n - 2; i++) {
					for (int k = 0; k < n - 2; k++) {
						G[j][i][k] = 0;
					}
				}
				for (int i = 0; i < j; i++) {
					G[j][i][i] = 1;
				}
				for (int i = j + 2; i < n - 1; i++) {
					G[j][i][i] = 1;
				}
				G[j][j][j] = b / d;
				G[j][j][j + 1] = -c / d;
				G[j][j + 1][j] = -G[j][j][j + 1]; // =c/d
				G[j][j + 1][j + 1] = G[j][j][j]; // = b/d
			}

			// 3. replace H by R_jHG_j, A by R_jDA, B by BER_j
			newH = new double[n][n - 1];
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n - 1; j++) {
					newH[i][j] = 0;
					for (int k = 0; k < n; k++) {
						for (int l = 0; l < n - 1; l++) {
							newH[i][j] += R[index][i][k] * H[k][l]
									* G[index][l][j];
						}
					}
				}
			}
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n - 1; j++) {
					H[i][j] = newH[i][j];
				}
			}

			int[][] newAorB = new int[n][n];
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++) {
					newAorB[i][j] = 0;
					for (int k = 0; k < n; k++) {
						for (int l = 0; l < n; l++) {
							newAorB[i][j] += R[index][i][k] * D[k][l] * A[l][j];
						}
					}
				}
			}
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++) {
					A[i][j] = newAorB[i][j];
				}
			}

			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++) {
					newAorB[i][j] = 0;
					for (int k = 0; k < n; k++) {
						for (int l = 0; l < n; l++) {
							newAorB[i][j] += B[i][k] * E[k][l] * R[index][l][j];
						}
					}
				}
			}
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++) {
					B[i][j] = newAorB[i][j];
				}
			}

			itCount++;
		}

		return null;
	}

	@Override
	public boolean isLaTeXTextCommand() {
		return true;
	}

	/*
	 * 
	 * public class FullDec extends MyDecimal{
	 * 
	 * 
	 * protected FullDec() { super(fullScale); // TODO Auto-generated
	 * constructor stub }
	 * 
	 * protected FullDec(double x) { super(full,x);
	 * 
	 * // TODO Auto-generated constructor stub }
	 * 
	 * protected FullDec(MyDecimal md) { super(md); }
	 * 
	 * }
	 * 
	 * private class DoubleDec extends MyDecimal{
	 * 
	 * protected DoubleDec() { super(decLess); // TODO Auto-generated
	 * constructor stub }
	 * 
	 * protected DoubleDec(double x) { super(decLess,x); // TODO Auto-generated
	 * constructor stub }
	 * 
	 * protected DoubleDec(FullDec x) { super(decLess, x); }
	 * 
	 * protected DoubleDec(MyDecimal md) { super(md); }
	 * 
	 * }
	 */

	private static class IntRelationFinder {

		// constants (defined later)
		private MyDecimal zero;
		MyDecimal zeroLess;
		private MyDecimal oneLess;

		// parameters
		private double tau;
		private double rho;
		private double gamma;
		double err; // error bound for x

		// input
		private int n;
		private double[] x;
		private MyDecimal[] x_full;
		private MyDecimal[] x_double;

		// working variables
		private int fullScale1;
		private int lessScale1;
		private MyDecimal xNorm;
		private MyDecimalMatrix mHfull;
		private MyDecimalMatrix mH;
		MyDecimalMatrix mI;
		private MyDecimalMatrix mA;
		private MyDecimalMatrix mB;
		private MyDecimalMatrix mD;
		// private Array2DRowFieldMatrix<Dfp> B_comp; //B_comp: *= new B_rest
		// (B_rest is in IntRelation class)
		private MyDecimalMatrix xB;

		private MyDecimal b;
		private MyDecimal l;
		private MyDecimal d;
		private int r;

		// results
		ArrayList<IntRelation> result;

		// PSLQ with initialization for exact calculation
		IntRelationFinder(int n, double[] x, int fullScale_input,
				int lessScale_input, double err, double bound) {

			this.fullScale1 = fullScale_input;
			this.lessScale1 = lessScale_input;
			this.n = n;
			this.err = err;
			result = new ArrayList<>();

			int digitsNeeded = (int) Math.ceil(-Math.log10(err));
			// int digitsAllocated = lessScale;

			lessScale1 = digitsNeeded;
			fullScale1 = digitsNeeded * n;
			/*
			 * boolean needSizeChange = false; while (digitsAllocated <=
			 * digitsNeeded) { needSizeChange = true; digitsAllocated *=2; } if
			 * (needSizeChange) { lessScale = digitsAllocated; }
			 * 
			 * needSizeChange = false; digitsNeeded = n*digitsNeeded;
			 * digitsAllocated = fullScale; while (digitsAllocated <=
			 * digitsNeeded) { needSizeChange = true; digitsAllocated *=2; } if
			 * (needSizeChange) { fullScale = digitsAllocated; }
			 */

			zero = new MyDecimal(fullScale1, BigDecimal.ZERO);
			zeroLess = new MyDecimal(lessScale1, BigDecimal.ZERO);
			oneLess = new MyDecimal(lessScale1, BigDecimal.ONE);

			if (n < 1) {
				result.clear();
				return;
			}

			if (n == 1) {
				if (x == null || x[0] >= err) {
					result.clear();
					return;
				}

				mB = new MyDecimalMatrix(lessScale1, 1, 1);
				mB.setEntry(0, 0, oneLess);
				xB = new MyDecimalMatrix(lessScale1, 1, 1);
				xB.setEntry(0, 0, zeroLess);
				IntRelation m = new IntRelation(n, mB, xB, 1);
				result.add(m);
				return;
			}

			this.x = new double[n];
			this.x_full = new MyDecimal[n]; // normalized, full precision
			this.x_double = new MyDecimal[n]; // normalized, double precision

			// int[] orthoIndices = new int[n];

			for (int i = 0; i < n; i++) {
				this.x[i] = x[i];
				this.x_full[i] = new MyDecimal(fullScale1, x[i]);
			}

			rho = 2;
			tau = 1.5; // arbitrarily chosen between 1+ and 2-
			gamma = 1 / Math.sqrt(1 / tau / tau - 1 / rho / rho);

			initialize_full();
			b = new MyDecimal(lessScale1);
			l = new MyDecimal(lessScale1);
			d = new MyDecimal(lessScale1);

			boolean loopTillExhausted = true;
			int iterCount = 0;
			double iterBound = n
					* (n + 1) / 2.0 * ((n - 1) * Math.log(gamma)
							+ 0.5 * Math.log(n) + Math.log(bound))
					/ Math.log(tau);

			while (iterCount < iterBound) {

				// "Exchange": find r to maximize gamma^r |h_rr|, exchange rows
				// r and r+1.
				double gammaPow = 1.0;
				double temp;
				double max = 0;

				for (int index = 0; index < n - 1; index++) {
					gammaPow *= gamma;
					temp = gammaPow
							* mH.getEntry(index, index).abs().doubleValue();
					if (max < temp) {
						max = temp;
						r = index;
					}
				}

				if (r < n - 2) { // for r=n-2 we don't need to define these.
									// Also l will be undefined
					b = mH.getEntry(r + 1, r);
					l = mH.getEntry(r + 1, r + 1);
					d = b.multiply(b).add(l.multiply(l)).sqrt();
				}

				MyDecimal temp2 = new MyDecimal(xB.getEntry(0, r));
				xB.setEntry(0, r, xB.getEntry(0, r + 1));
				xB.setEntry(0, r + 1, temp2);

				MyDecimal[] temp3 = mH.getRow(r);
				mH.setRow(r, mH.getRow(r + 1));
				mH.setRow(r + 1, temp3);

				temp3 = mA.getRow(r);
				mA.setRow(r, mA.getRow(r + 1));
				mA.setRow(r + 1, temp3);

				temp3 = mB.getColumn(r);
				mB.setColumn(r, mB.getColumn(r + 1));
				mB.setColumn(r + 1, temp3);

				// Corner
				MyDecimal[] temp4;
				if (r < n - 2) {

					temp3 = mH.getColumn(r);
					temp4 = mH.getColumn(r + 1);

					for (int i = r; i < n; i++) {
						mH.setEntry(i, r, b.multiply(temp3[i])
								.add(l.multiply(temp4[i])).divide(d));
						mH.setEntry(i, r + 1, l.negate().multiply(temp3[i])
								.add(b.multiply(temp4[i])).divide(d));
					}
				}
				// pretermination, just for double-check. Not mentioned in the
				// article.
				boolean relationExhausted = false;
				for (int j = 0; j < n - 1; j++) {
					if (mH.getEntry(j, j).abs().doubleValue() < Math.pow(10,
							-Math.min(mH.getScale(), 5))) {
						relationExhausted = true;
						Log.warn("relation pre-Exhausted at iteration "
								+ iterCount + "with r = " + j + "where n-1 = "
								+ (n - 1));
					}
				}

				// reduction

				if (!relationExhausted) {
					hermiteReduction();
				}

				// termination
				boolean relationFound = false;
				relationExhausted = false;
				for (int j = 0; j < n - 1; j++) {
					if (mH.getEntry(j, j).abs().doubleValue() < Math.pow(10,
							-Math.min(mH.getScale(), 5))) {
						relationExhausted = true;
						Log.warn("relation Exhausted at iteration " + iterCount
								+ "with r = " + j + "where n-1 = " + (n - 1));
					}
				}

				for (int j = 0; j < n; j++) { // we look at the j-th column of B
					double sumBCol = 0;
					double maxxB = 0;
					for (int i = 0; i < n; i++) {
						sumBCol += mB.getEntry(i, j).abs().doubleValue();
						maxxB = Math.max(maxxB,
								xB.getEntry(0, i).abs().doubleValue());
					}

					if (xB.getEntry(0, j).signum() == 0 || xB.getEntry(0, j)
							.abs().doubleValue() < sumBCol * err) {

						relationFound = true;
						IntRelation m = new IntRelation(n, mB, xB, err / maxxB);
						result.add(m);

						break;
					}
				}

				// System.out.println("");

				if (relationFound && !loopTillExhausted || relationExhausted) {
					break;
				}

				iterCount++;
			}
		}

		void initialize_full() {

			xNorm = new MyDecimal(lessScale1);

			// normalize x
			Log.debug("normalizing " + n);
			for (int i = 0; i < n; i++) {
				Log.debug(x_full[i]);
				xNorm = xNorm.add(x_full[i].multiply(x_full[i]));
			}

			xNorm = xNorm.sqrt();

			// System.out.println(xNorm.toFullString());

			for (int i = 0; i < n; i++) {
				if (xNorm.getImpl().compareTo(BigDecimal.ZERO) != 0) {
					x_full[i] = x_full[i].divide(xNorm);
				}
				x_double[i] = new MyDecimal(lessScale1, x_full[i].getImpl());

			}

			// partial sums of squares
			MyDecimal[] ss = new MyDecimal[n];
			for (int i = 0; i < n; i++) {
				ss[i] = new MyDecimal(fullScale1);
			}

			ss[n - 1] = x_full[n - 1].multiply(x_full[n - 1]);

			for (int i = n - 2; i >= 0; i--) {
				ss[i] = ss[i + 1].add(x_full[i].multiply(x_full[i]));
			}

			for (int i = 0; i < n; i++) {
				ss[i] = ss[i].sqrt();
			}

			// pre-calculate ss[j]*ss[j+1]
			MyDecimal[] Pss = new MyDecimal[n - 1];
			for (int i = 0; i < n - 1; i++) {
				Pss[i] = new MyDecimal(fullScale1,
						ss[i].multiply(ss[i + 1]).getImpl());
			}

			// initialize Matrix H (lower trapezoidal
			mHfull = new MyDecimalMatrix(fullScale1, n, n - 1);

			for (int i = 0; i < n; i++) {
				for (int j = 0; j < i; j++) {
					mHfull.setEntry(i, j, x_full[i].multiply(x_full[j])
							.divide(Pss[j]).negate());
				}

				if (i < n - 1) {
					mHfull.setEntry(i, i, ss[i + 1].divide(ss[i]));
				}

				for (int j = i + 1; j < n - 1; j++) {
					mHfull.setEntry(i, j, zero);
				}
			}

			// test property of H: the n-1 columns are orthogonal

			// System.out.println(H.transpose().multiply(H).toString());

			// matrix P = In - x.x
			/*
			 * P = new double[n][n]; for (int i=0; i<n; i++) for (int j=0; j<n;
			 * j++) P[i][j] = -x[i]*x[j]; for (int i=0; i<n; i++) P[i][i]+=1;
			 */

			/*
			 * P = new Array2DRowFieldMatrix<Dfp>(decFull, n,n);
			 * 
			 * for (int i=0; i<n; i++) { for (int j=0; j<n; j++) { P.setEntry(i,
			 * j, x1[i].multiply(x1[j]).negate()); } }
			 * 
			 * 
			 * for (int i=0; i<n; i++) { P.setEntry(i, i, P.getEntry(i,
			 * i).add(ONE)); }
			 */

			/*
			 * //P = H.multiply( ((Array2DRowFieldMatrix<Dfp>) H.transpose()));
			 * 
			 * System.out.println(P.toString());
			 * 
			 * //debug: |P|^2=|H|^2 = n-1 AbstractApplication.debug(
			 * "Frobenius Norm Squares: \n" + "|P|^2 = " + frobNormSq(P,n,n) +
			 * "|H|^2 = " + frobNormSq(H,n,n-1) );
			 */

			mH = new MyDecimalMatrix(lessScale1, n, n - 1);
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n - 1; j++) {
					mH.setEntry(i, j, new MyDecimal(lessScale1,
							mHfull.getEntry(i, j).getImpl()));
				}
			}

			// initialize matrices R
			/*
			 * R = new int[n-1][n][n]; for (int j=0; j<n-1; j++) { for (int i=0;
			 * i<n; i++) for (int k=0; k<n; k++) R[j][i][k]=0; for (int i=0;
			 * i<n; i++) R[j][i][i]=1; R[j][j][j]=0; R[j][j][j+1]=1;
			 * R[j][j+1][j]=1; R[j][j+1][j+1]=0; }
			 * 
			 * gamma = 1.5; deltaSq = 3.0/4 - (1.0/gamma)/gamma;
			 * 
			 * //initialize A, B = I_n A = new int[n][n]; for (int i=0; i<n;
			 * i++) for (int j=0; j<n; j++) A[i][j]=0; for (int i=0; i<n; i++)
			 * A[i][i]=1; //B = new int[n][n]; for (int i=0; i<n; i++) for (int
			 * j=0; j<n; j++) B[i][j]=0; for (int i=0; i<n; i++) B[i][i]=1;
			 */

			mI = new MyDecimalMatrix(lessScale1, n, n);
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++) {
					if (i == j) {
						mI.setEntry(i, i, oneLess);
					} else {
						mI.setEntry(i, j, zeroLess);
					}
				}

			}

			mA = mI.copy();
			mB = mI.copy();
			xB = new MyDecimalMatrix(lessScale1, 1, n);
			for (int i = 0; i < n; i++) {
				xB.setEntry(0, i, x_double[i]);
			}
			// System.out.println(A.toString());

		}

		private void hermiteReduction() {
			mD = mI.copy();

			for (int i = 1; i < n; i++) {
				for (int j = i - 1; j >= 0; j--) {
					// MyDecimal q = new MyDecimal(H.getEntry(i,
					// j).divide(H.getEntry(j, j)).divide(ONE, 0,
					// BigDecimal.ROUND_DOWN));
					MyDecimal q = new MyDecimal(lessScale1,
							Math.rint(mH.getEntry(i, j).doubleValue()
									/ mH.getEntry(j, j).doubleValue()));
					for (int k = 0; k <= j; k++) {
						mH.setEntry(i, k, mH.getEntry(i, k)
								.subtract(q.multiply(mH.getEntry(j, k))));
					}
					for (int k = 0; k < n; k++) {
						mD.setEntry(i, k, mD.getEntry(i, k)
								.subtract(q.multiply(mD.getEntry(j, k))));
						mA.setEntry(i, k, mA.getEntry(i, k)
								.subtract(q.multiply(mA.getEntry(j, k))));
						mB.setEntry(k, j, mB.getEntry(k, j)
								.add(q.multiply(mB.getEntry(k, i))));
					}
					xB.setEntry(0, j, xB.getEntry(0, j)
							.add(q.multiply(xB.getEntry(0, i))));
				}
			}

		}

		private class IntRelation implements Comparable<IntRelation> {

			private int size;
			final double sig;
			// private double norm2; //norm of the vector m squared
			// private int normInf;

			// these are all copied from IntRelationFinder

			private int nilDim; // dimension of the nil-subspace corresponding
								// to vector m, that is, the # cols of B_sol
			private MyDecimalMatrix B1;
			private MyDecimalMatrix B_sol;
			private MyDecimalMatrix B_rest;
			MyDecimalMatrix xB1;
			int[] orthoIndices;

			public IntRelation(int n, MyDecimalMatrix B, MyDecimalMatrix xB,
					double sig) {

				if (n == 0) {
					this.sig = 0;
					return;
				}
				this.size = n;
				this.B1 = B.copy();
				this.xB1 = xB.copy();
				this.sig = sig;

				orthoIndices = new int[n];
				nilDim = 0;

				for (int j = 0; j < n; j++) { // we look at the j-th column of B
					double sumBCol = 0;
					double maxxB = 0;
					for (int i = 0; i < n; i++) {
						sumBCol += B.getEntry(i, j).abs().doubleValue();
						maxxB = Math.max(maxxB,
								xB.getEntry(0, i).abs().doubleValue());
					}

					if (xB.getEntry(0, j).equals(zeroLess) || Math.abs(
							xB.getEntry(0, j).doubleValue()) < sumBCol * err) {

						orthoIndices[j] = 1;
						nilDim++;

					} else {

						orthoIndices[j] = 0;

					}
				}

				// B_sol: solution columns of B; B_rest: non solution columns
				if (nilDim > 0) {
					B_sol = new MyDecimalMatrix(B.getScale(), size, nilDim);
				} else {
					B_sol = null;
				}
				if (nilDim < size) {
					B_rest = new MyDecimalMatrix(B.getScale(), size,
							size - nilDim);
				} else {
					B_rest = null;
				}

				int is, ir;
				is = ir = 0;

				for (int j = 0; j < n; j++) {
					if (orthoIndices[j] == 1) {
						B_sol.setColumn(is++, B.getColumn(j));
					} else {
						B_rest.setColumn(ir++, B.getColumn(j));
					}
				}

			}

			public MyDecimalMatrix getBMatrix() {
				return B1.copy();
			}

			public MyDecimalMatrix getBSolMatrix() {
				if (B_sol != null) {
					return B_sol.copy();
				}
				return null;
			}

			public MyDecimalMatrix getBRestMatrix() {
				if (B_rest != null) {
					return B_rest.copy();
				}
				return null;
			}

			@Override
			public int compareTo(IntRelation m2) {
				if (this.size != m2.size) {
					return -100 * (this.size - m2.size); // should throw an
															// exception
				}

				if (DoubleUtil.isGreater(this.sig, m2.sig, 10E-7)) {
					return 1;
				} else if (DoubleUtil.isGreater(m2.sig, this.sig, 10E-7)) {
					return -1;
				}

				return 0;
			}

			@Override
			public boolean equals(Object o) {
				if (o instanceof IntRelation) {
					return compareTo((IntRelation) o) == 0;
				}
				return false;
			}

			@Override
			public int hashCode() {
				return (int) (1E8 * sig);
			}

		}

	}

	/**
	 * @author tam
	 *
	 */
	public enum AlgebraicFittingType {
		/**
		 * Given x, we search for the form x = A/B where A,B are integers.
		 */
		RATIONAL_NUMBER,

		/**
		 * Given x and constants Ck, we search for x = (sum of AkCk)/B where Ak
		 * and B are integers.
		 */
		LINEAR_COMBINATION,

		/**
		 * Given x and constants Ck, search for x = (sum of AkCk)/(sum of BkCk)
		 * where Ak and Bk are integers.
		 */
		RATIONAL_COMBINATION,

		/**
		 * Given x and constants Ek, search for x = product of Ek^(pk/qk) where
		 * pk,qk are integers.
		 */
		POWER_PRODUCT,

		/**
		 * Given x and constants Ck, search for x = (A+B sqrt(C))/D, where
		 * A,B,C,D are linear combinations of Ck
		 */
		QUADRATIC_RADICAL,

		/**
		 * Given x and invertible function f, find x = f(y) where y is fit in
		 * the type of RATIONAL_NUMBER
		 */
		FUNCTION_OF_RATIONAL_NUMBER,

		/**
		 * Given x, constants Ck, and invertible function f, find x = f(y) where
		 * y is fit in the type of LINEAR_COMINATION
		 */
		FUNCTION_OF_LINEAR_COMBINATION,

		/**
		 * Given x, constants Ck, and invertible function f, find x = f(y) where
		 * y is fit in the type of POWER_PRODUCT
		 */
		FUNCTION_OF_POWER_PRODUCT,

		/**
		 * Given x, constants Ck, and invertible function f, find x = f(y) where
		 * y is fit in the type of QUADRATIC_RADICAL
		 */
		FUNCTION_OF_QUADRATIC_RADICAL
	}

	private class AlgebraicFit {

		// input
		private double num1;

		// parameters
		private int numOfConsts;
		private int numOfRadicals;
		private double[] constValues;
		private String[] constStrings;
		private int coeffBound; // largest acceptable absolute value of
								// coefficients
		private double err;
		private AlgebraicFittingType aft;

		private StringTemplate tpl;

		// working variables
		private double[] numList;
		private int[][] coeffs; // relations generated from mPSLQ
		private int s; // number of candidate relations

		private int numOfPenalties;
		private int[][] penalties;
		private int numOfConstsUsed; // does not include 1
		private int maxCoeff;
		private int sumCoeffs;
		private boolean isOneUsed;
		private int bestIndex;
		private int[] bestRelation;

		// output
		public StringBuilder formalSolution;

		/**
		 * @param constStrings
		 *            the strings of the constants
		 * @param constValues
		 *            the double value of them
		 * @param aft
		 *            fitting type
		 * @param tpl
		 *            template for CAS and formal solution
		 */
		public AlgebraicFit(String[] constStrings, double[] constValues,
				AlgebraicFittingType aft, StringTemplate tpl) {
			this.numOfConsts = constValues == null ? 0 : constValues.length;
			this.numOfRadicals = this.numOfConsts;
			// this.constValues = constValues.clone(); //not available in GWT
			if (constValues != null) {
				this.constValues = new double[constValues.length];
				for (int i = 0; i < constValues.length; i++) {
					this.constValues[i] = constValues[i];
				}
			}
			// this.constStrings = constStrings.clone(); //not available in GWT
			if (constStrings != null) {
				this.constStrings = new String[constStrings.length];
				for (int i = 0; i < constStrings.length; i++) {
					this.constStrings[i] = constStrings[i];
				}
			}
			this.aft = aft;
			this.tpl = tpl;

			err = Math.min(Kernel.MAX_PRECISION, Kernel.STANDARD_PRECISION);
			coeffBound = 100;
			formalSolution = new StringBuilder();
			formalSolution.setLength(0);

			// numList = new double[(numOfConstants+1)*(deg+1)];
		}

		public void compute(double number) {
			switch (aft) {
			case RATIONAL_NUMBER:
				computeRationalNumber(number);
				return;
			case LINEAR_COMBINATION:
				computeConstant(number);
				return;
			case RATIONAL_COMBINATION:
				// TODO
				return;
			case POWER_PRODUCT:
				// TODO
				return;
			case QUADRATIC_RADICAL:
				computeQuadratic(number);
				return;
			case FUNCTION_OF_RATIONAL_NUMBER:
				// TODO
				return;
			case FUNCTION_OF_LINEAR_COMBINATION:
				// TODO
				return;
			case FUNCTION_OF_POWER_PRODUCT:
				// TODO
				return;
			case FUNCTION_OF_QUADRATIC_RADICAL:
				// TODO
				return;
			}
		}

		private void computeQuadratic(double number) {

			num1 = number;

			numList = new double[(1 + numOfConsts) * 3]; // (numOfConsts+1)(degree+1)

			double temp;
			for (int j = 0; j < numOfConsts; j++) {
				temp = constValues[j];
				for (int i = 0; i < 3; i++) {
					numList[j * 3 + i] = temp;
					temp *= num1;
				}
			}

			temp = 1.0;
			// Note: it turns out to be better to put the 1 term at the end
			// instead of in the front
			for (int i = 0; i < 3; i++) {
				numList[numOfConsts * 3 + i] = temp;
				temp *= num1;
			}

			coeffs = mPSLQ((numOfConsts + 1) * 3, numList, err, coeffBound);

			if (coeffs == null || coeffs[0].length == 0) {
				return;
			}
			s = coeffs[0].length;

			// penalties calculation
			int[][] termCount = new int[numOfConsts + 1][s]; // count number of
																// terms within
																// one big
																// coefficient
			int[][] termMax = new int[numOfConsts + 1][s]; // max abs value of
															// coefficient
															// within one big
															// coefficient
			double[][] w = new double[4][s]; // w[0]+w[1]x+w[2]x^2=0,
												// w[3]=w[1]^2-4w[0]*w[2]
			numOfPenalties = 7;
			penalties = new int[numOfPenalties][s];
			for (int j = 0; j < s; j++) { // for the j-th solution, check its
											// characteristics and make
											// penalties

				// first penalties[0][j]: is the largest coefficient in a big
				// coefficient greater than 100? Yes: +1, No: 0
				// second penalties[1][j]: number of constants used
				// third penalties[2][j]: maximum number of terms in each "big"
				// coefficient (i.e. linear combination of constants)
				// fourth penalties[3][j]: form -- "A" - 0, "A/B" - 1,
				// "sqrt(A)/B" - 2, (B+sqrt(A)) - 3, (B+sqrt(A))/C - 4
				// fifth penalties[4][j]: coefficient -- its absolute value when
				// less than 100. 100 or more -- 100.
				// sixth penalties[5][j]: is multiple of "1" used? Yes: 1, No:0
				// last penalties[6][j]: -j (the larger index the better)

				for (int i = 0; i < numOfPenalties; i++) {
					penalties[i][j] = 0;
				}

				for (int a = 0; a < numOfConsts + 1; a++) {
					termCount[a][j] = 0;
					termMax[a][j] = 0;
					for (int b = 0; b < 3; b++) {
						if (coeffs[a * 3 + b][j] != 0) {
							termCount[a][j]++;
						}
						termMax[a][j] = Math.max(termMax[a][j],
								Math.abs(coeffs[a * 3 + b][j]));
					}
				}

				int[] wj = new int[(numOfConsts + 1) * 3];
				for (int i = 0; i < wj.length; i++) {
					wj[i] = coeffs[i][j];
				}
				for (int b = 0; b < 3; b++) {
					w[b][j] = evaluateCombination(numOfConsts, constValues, wj,
							b, 3) + coeffs[numOfConsts * 3 + b][j];
				}
				w[3][j] = w[1][j] * w[1][j] - 4 * w[0][j] * w[2][j];

				if (DoubleUtil.isZero(w[2][j])) { // w[0][j]+w[1][j]x=0
					if (DoubleUtil.isZero(w[1][j])) {
						penalties[3][j] = 10000; // bad case
					} else if (DoubleUtil.isEqual(Math.abs(w[1][j]), 1.0)) {
						// an
																		// integer
						penalties[3][j] = 0;
					} else {
						penalties[3][j] = 1;
					}
				} else {
					if (DoubleUtil.isEqual(Math.abs(w[2][j]), 0.5)) { // 0 or 2 or 4
						if (DoubleUtil.isZero(w[3][j])) {
							penalties[3][j] = 0;
						} else if (DoubleUtil.isZero(w[1][j])) {
							penalties[3][j] = 2;
						} else {
							penalties[3][j] = 4;
						}
					} else { // 1 or 3 or 5
						if (DoubleUtil.isZero(w[3][j])) {
							penalties[3][j] = 1;
						} else if (DoubleUtil.isZero(w[1][j])) {
							penalties[3][j] = 3;
						} else {
							penalties[3][j] = 5;
						}
					}
				}

				for (int a = 0; a < numOfConsts + 1; a++) {

					penalties[0][j] += termMax[a][j] > coeffBound ? 1 : 0;
					penalties[1][j] += termCount[a][j] > 0 ? 1 : 0;
					penalties[2][j] = Math.max(penalties[2][j],
							termCount[a][j]);

					// penalties[3][j]: form -- integer - 0, fraction - 1,
					// sqrt(w3) - 2
					// sqrt(w3)/2w2" - 3, (-w1+sqrt(w3)) - 4, (-w1+sqrt(w3))/2w2
					// - 5

					if (termMax[a][j] >= 100) {
						penalties[4][j] += 100;
					} else {
						penalties[4][j] += termMax[a][j];
					}
				}

				boolean isOneUsed1 = false;
				for (int b = 0; b < 3; b++) {
					isOneUsed1 = isOneUsed1
							|| coeffs[numOfConsts * 3 + b][j] != 0;
				}
				penalties[5][j] = isOneUsed1 ? 1 : 0;

				penalties[6][j] = -j;
			}

			bestIndex = leastPenaltyIndex();
			bestRelation = new int[(1 + numOfConsts) * 3];
			for (int a = 0; a < (1 + numOfConsts) * 3; a++) {
				bestRelation[a] = coeffs[a][bestIndex];
			}

			// clear the solutions that do not fulfill strict requirements
			if (penalties[0][bestIndex] >= 1) {
				bestIndex = -1;
				formalSolution.setLength(0);
				return;
			}

			// Suppose A+Bx+Cx^2 = 0, where A,B,C are linear combinations of 1
			// and values in constValueboolean isAZero = true;
			boolean isAZero = true;
			boolean isARational = true;
			boolean isBZero = true;
			boolean isBRational = true;
			boolean isCZero = true;
			boolean isCRational = true;
			if (bestRelation[numOfConsts * 3] != 0) {
				isAZero = false;
			}
			if (bestRelation[numOfConsts * 3 + 1] != 0) {
				isBZero = false;
			}
			if (bestRelation[numOfConsts * 3 + 2] != 0) {
				isCZero = false;
			}

			for (int j = 0; j < numOfConsts; j++) {
				if (bestRelation[j * 3] != 0) {
					isAZero = false;
					isARational = false;
				}
				if (bestRelation[j * 3 + 1] != 0) {
					isBZero = false;
					isBRational = false;
				}
				if (bestRelation[j * 3 + 2] != 0) {
					isCZero = false;
					isCRational = false;
				}
			}

			/*
			 * if (isARational && isBRational && isCRational) { //TODO: optimize
			 * this PSLQappendQuadratic(sb, num, tpl); //return; }
			 */

			if (isAZero && isBZero && isCZero) {
				formalSolution.setLength(0);
			} else if (isCZero) {
				if (isBZero) {
					formalSolution.setLength(0);
				} else {

					StringBuilder AString = new StringBuilder();
					StringBuilder BString = new StringBuilder();

					AString.append(
							kernel.format(bestRelation[numOfConsts * 3], tpl));
					if (!isARational) {
						AString.append("+");
						// appendCombination(AString,(bestRelation[numOfConsts*3]==0)?
						// numOfTermsInA : numOfTermsInA-1, constStrings,
						// bestRelation, 0, 3, tpl);
						appendCombination(AString, numOfConsts, constStrings,
								bestRelation, 0, 3, tpl);
					}

					BString.append(kernel
							.format(bestRelation[numOfConsts * 3 + 1], tpl));
					if (!isBRational) {
						BString.append("+");
						appendCombination(BString, numOfConsts, constStrings,
								bestRelation, 1, 3, tpl);
					}

					formalSolution.append("-(");
					formalSolution.append(AString.toString());
					formalSolution.append(")/(");
					formalSolution.append(BString.toString());
					formalSolution.append(")");
				}
			} else {
				StringBuilder AString = new StringBuilder(
						kernel.format(bestRelation[numOfConsts * 3], tpl));
				if (!isARational) {
					AString.append("+");
					appendCombination(AString, numOfConsts, constStrings,
							bestRelation, 0, 3, tpl);
				}

				StringBuilder BString = new StringBuilder(
						kernel.format(bestRelation[numOfConsts * 3 + 1], tpl));
				if (!isBRational) {
					BString.append("+");
					appendCombination(BString, numOfConsts, constStrings,
							bestRelation, 1, 3, tpl);
				}

				StringBuilder CString = new StringBuilder(
						kernel.format(bestRelation[numOfConsts * 3 + 2], tpl));
				if (!isCRational) {
					CString.append("+");
					appendCombination(CString, numOfConsts, constStrings,
							bestRelation, 2, 3, tpl);
				}

				formalSolution.append("(");

				formalSolution.append("-(");
				formalSolution.append(BString.toString());
				formalSolution.append(")");

				double Avalue = bestRelation[numOfConsts * 3]
						+ evaluateCombination(numOfConsts, constValues,
								bestRelation, 0, 3);
				double Bvalue = bestRelation[numOfConsts * 3 + 1]
						+ evaluateCombination(numOfConsts, constValues,
								bestRelation, 1, 3);
				double Cvalue = bestRelation[numOfConsts * 3 + 2]
						+ evaluateCombination(numOfConsts, constValues,
								bestRelation, 2, 3);
				double discr = Bvalue * Bvalue - 4 * Avalue * Cvalue;
				if (!DoubleUtil.isZero(discr)) {
					if (num1 * 2 * Cvalue + Bvalue >= 0) {
						formalSolution.append("+");
					} else {
						formalSolution.append("-");
					}

					formalSolution.append("sqrt(");

					formalSolution.append("(");
					formalSolution.append(BString.toString());
					formalSolution.append(")^2");

					formalSolution.append("-4*(");
					formalSolution.append(CString.toString());
					formalSolution.append(")*(");
					formalSolution.append(AString.toString());
					formalSolution.append(")");

					formalSolution.append(")");

				}

				formalSolution.append(")/(");

				formalSolution.append("2*(");
				formalSolution.append(CString.toString());
				formalSolution.append(")");
				formalSolution.append(")");
			}
		}

		/**
		 * @param bound
		 *            norm bound for coefficients
		 */
		private int[][] mPSLQ(int n, double[] x, double accuracyFactor,
				int bound) {

			MyDecimalMatrix r2;

			int rCols = 0; // tracks the number of solutions globally

			// int[] orthoIndices = new int[n];
			// int[][] B1 = new int[n][n];
			// int[][] M = new int[n][n];
			// int[][] B2 = new int[n][n];
			// double[] xB2 = new double[n];

			// now n>=2.
			int p = n; // length of current x

			/*
			 * for (int i=0; i<n; i++) { B2[i][i] = 1; }
			 */

			// First cycle of the loop has something different to do, so it is
			// explicitly written here.
			int oldp = p;
			int q;

			IntRelationFinder irf = new IntRelationFinder(oldp, x, fullScale,
					lessScale, accuracyFactor, bound);

			if (irf.result.size() == 0) {
				return null;
			}

			IntRelationFinder.IntRelation m = irf.result.get(0); // the most
																	// significant
																	// resulting
																	// relations

			// r2 stores all possible results. Numbers are initialized here
			// because
			// we need the correct field.
			r2 = new MyDecimalMatrix(m.getBMatrix().getScale(), n, n);

			MyDecimalMatrix result2 = m.getBSolMatrix();
			if (result2 != null) {
				q = result2.getColumnDimension();
			} else {
				q = 0;
			}

			// store the results to r2
			for (int j = 0; j < q; j++) {
				for (int i = 0; i < n; i++) {
					r2.setEntry(i, rCols, result2.getEntry(i, j));
				}
				if (rCols == n - 1) {
					Log.warn("There should not be that many solutions.");
				}
				rCols++;
			}

			p = 0;
			for (int j = 0; j < oldp; j++) {
				if (m.orthoIndices[j] == 0) {
					x[p++] = new Double(m.xB1.getEntry(0, j).toString());
				}
			}

			MyDecimalMatrix B_comp = m.getBRestMatrix();

			// second and more cycles. If p<oldp means at least one solution has
			// been found in the last cycle.
			// also the dimension of x should be at least 2.
			while (oldp >= 2 && p < oldp && rCols < n - 1) {

				oldp = p;
				p = 0;
				irf = new IntRelationFinder(oldp, x, fullScale, lessScale,
						accuracyFactor, bound);
				if (irf.result.size() == 0) {
					break;
				}
				m = irf.result.get(0);

				if (m.getBSolMatrix() == null) {
					break;
				}

				result2 = B_comp.multiply(m.getBSolMatrix());
				q = result2.getColumnDimension();

				// store the resulting columns B_comp.result2 to r2
				for (int j = 0; j < q; j++) {

					// we don't accept all zeros as a relation
					boolean allZero = true;
					for (int i = 0; i < n; i++) {
						allZero = allZero
								&& result2.getEntry(i, j).intValue() == 0;
					}
					if (allZero) {
						break;
					}

					// we don't accept any entry's absolute value being larger
					// than
					// bound
					boolean tooLargeEntry = false;
					for (int i = 0; i < n; i++) {
						tooLargeEntry = tooLargeEntry || result2.getEntry(i, j)
								.abs().intValue() > bound;
					}
					if (tooLargeEntry) {
						break;
					}

					for (int i = 0; i < n; i++) {
						r2.setEntry(i, rCols, result2.getEntry(i, j));
					}
					if (rCols == n - 1) {
						Log.warn("There should not be that many solutions.");
					}
					rCols++;
				}

				// x<-the non-zero elements of new xB
				p = 0;
				for (int j = 0; j < oldp; j++) {
					if (m.orthoIndices[j] == 0) {
						x[p++] = new Double(m.xB1.getEntry(0, j).toString());
					}
				}

				// B_comp <- B_comp . the new B_rest (nxq,qxq')
				if (m.getBRestMatrix() != null) {
					B_comp = B_comp.multiply(m.getBRestMatrix());
				}

			}
			/*
			 * result = new int[n][rCols]; for (int i=0; i<n; i++){ for (int
			 * j=0; j<rCols; j++) { result[i][j]=r[i][j]; } }
			 */

			int[][] result = new int[n][rCols];
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < rCols; j++) {
					result[i][j] = r2.getEntry(i, j).intValue();
				}
			}
			return result;
		}

		/**
		 * Assume that number = A/B, then we need to find relation (B,-A) to the
		 * vector (number,1)
		 * 
		 * @param number
		 *            real number
		 */
		private void computeRationalNumber(double number) {
			numOfConsts = 0;
			numList = new double[] { number, 1 };
			coeffs = mPSLQ(2, numList, err, coeffBound);

			if (coeffs == null) {
				return;
			}

			// get number of solutions
			s = coeffs[0].length;

			// compute penalties, using leastPenaltyIndex() to find the
			// bestIndex
			numOfPenalties = 3;
			penalties = new int[numOfPenalties][s];
			for (int j = 0; j < s; j++) {
				maxCoeff = Math.max(Math.abs(coeffs[0][j]),
						Math.abs(coeffs[1][j]));
				penalties[0][j] = maxCoeff > coeffBound ? 1 : 0;
				penalties[1][j] = Math.abs(coeffs[0][j]); // magnitude of
															// denominator
				penalties[2][j] = Math.abs(coeffs[1][j]);
			}
			bestIndex = leastPenaltyIndex();

			// retrieve the bestRelation
			bestRelation = new int[numOfConsts + 2];
			for (int i = 0; i < numOfConsts + 2; i++) {
				bestRelation[i] = coeffs[i][bestIndex];
			}

			// clear the solutions that do not fulfill strict requirements
			if (penalties[0][bestIndex] == 1) {
				bestIndex = -1;
				formalSolution.setLength(0);
				return;
			}

			// construct CAS String of the formal solution
			formalSolution.append(kernel.format(-bestRelation[1], tpl));
			formalSolution.append("/(");
			formalSolution.append(kernel.format(bestRelation[0], tpl));
			formalSolution.append(")");
		}

		// constant test
		public void computeConstant(double number) {

			numList = new double[numOfConsts + 2]; // {the constants} U {1} U
													// {num}

			for (int j = 0; j < numOfConsts; j++) {
				numList[j] = constValues[j];
			}
			numList[numOfConsts] = 1.0;
			numList[numOfConsts + 1] = number;

			coeffs = mPSLQ(numOfConsts + 2, numList, err, coeffBound);
			if (coeffs == null) {
				return;
			}
			s = coeffs[0].length;

			// penalties calculation
			int numOfRadicalsUsed = 0;
			int numOfOthersUsed = 0;

			for (int j = 0; j < s; j++) {

				for (int i = 0; i < numOfRadicals; i++) {
					if (coeffs[i][j] != 0) {
						numOfRadicalsUsed++;
					}
				}

				for (int i = numOfRadicals; i < numOfConsts; i++) {
					if (coeffs[i][j] != 0) {
						numOfOthersUsed++;
					}
				}

				numOfConstsUsed = numOfRadicalsUsed + numOfOthersUsed;

				isOneUsed = coeffs[numOfConsts][j] == 1;
				maxCoeff = 0;
				sumCoeffs = 0;
				for (int i = 0; i < numOfConsts + 1; i++) {
					sumCoeffs += Math.abs(coeffs[i][j]);
					maxCoeff = Math.max(maxCoeff, Math.abs(coeffs[i][j]));
				}
			}

			numOfPenalties = 7;
			penalties = new int[numOfPenalties][s];
			// candidates = new boolean[s];

			for (int j = 0; j < s; j++) { // for the j-th solution, check its
											// characteristics and make
											// penalties

				// penalties[0][j]: coefficient of num can't be zero. Is zero:
				// 1, No: 0
				// penalties[1][j]: is the largest coefficient in a big
				// coefficient greater than acceptable bound? Yes: +1, No: 0
				// penalties[2][j]: is non-algebraic? Yes: 1, no: 0
				// penalties[3][j]: number of constants used
				// penalties[4][j]: coefficient -- its absolute value when no
				// greater than bound. bound+1 or more -- bound+1.
				// penalties[5][j]: is multiple of "1" used? Yes: 1, No:0
				// penalties[6][j]: -j (the larger index the better)

				penalties[0][j] = (coeffs[numOfConsts + 1][j] == 0) ? 1 : 0;
				penalties[1][j] = (maxCoeff > coeffBound) ? 1 : 0;
				penalties[2][j] = numOfOthersUsed > 0 ? 1 : 0;
				penalties[3][j] = numOfConstsUsed;
				penalties[4][j] = Math.min(sumCoeffs, coeffBound + 1);
				penalties[5][j] = isOneUsed ? 1 : 0;
				penalties[6][j] = -j;
			}

			bestIndex = leastPenaltyIndex();
			bestRelation = new int[numOfConsts + 2];
			for (int i = 0; i < numOfConsts + 2; i++) {
				bestRelation[i] = coeffs[i][bestIndex];
			}

			if (penalties[0][bestIndex] == 1 || penalties[1][bestIndex] == 1) {
				bestIndex = -1;
				formalSolution.setLength(0);
				return;
			}

			// construct a formal solution in CAS format
			formalSolution.append("(");
			appendCombination(formalSolution, numOfConsts, constStrings,
					bestRelation, 0, 1, tpl);
			formalSolution.append("+");
			formalSolution
					.append(kernel.format(bestRelation[numOfConsts], tpl));
			formalSolution.append(")/(");
			formalSolution
					.append(kernel.format(-bestRelation[numOfConsts + 1], tpl));
			formalSolution.append(")");
		}

		private int leastPenaltyIndex() {

			boolean[] candidates = new boolean[s];

			// find the best index
			int bestIndex1 = -1;

			for (int j = 0; j < s; j++) {
				candidates[j] = true;
			}

			for (int k = 0; k < numOfPenalties; k++) {

				int minPenalty = Integer.MAX_VALUE;

				for (int j = 0; j < s; j++) {
					if (candidates[j]) {
						if (penalties[k][j] < minPenalty) {
							minPenalty = penalties[k][j];
						}
					}
				}

				for (int j = 0; j < s; j++) {
					if (candidates[j]) {
						if (penalties[k][j] > minPenalty) {
							candidates[j] = false;
						} else {
							bestIndex1 = j;
						}
					}
				}
			}
			return bestIndex1;
		}

		/*
		 * 
		 * void computeLinear(double num) {
		 * 
		 * }
		 * 
		 * void testZero(double num) {
		 * 
		 * }
		 */
		public void setCoeffBound(int b) {
			coeffBound = b;
		}
		/*
		 * public int getCoeffBound() { return coeffBound; }
		 * 
		 * public void setConsts(int n, String[] listOfNames, double[]
		 * listOfValues) {
		 * 
		 * if (listOfNames.length < n || listOfValues.length !=
		 * listOfNames.length) { Log.debug("error: size does not match");
		 * return; } numOfConsts = n; constStrings = listOfNames.clone();
		 * constValues = listOfValues.clone(); }
		 */
		/**
		 * By default, it is just an identity function. User can change the
		 * call() method according to the underlying function and the type T of
		 * the argument. Requirements: the function should be an invertible one,
		 * and the type of the values should also be T.
		 * 
		 * @author lightest
		 *
		 * @param <T>
		 */
		/*
		 * public class FunctionForFit<T> implements Callable {
		 * 
		 * private T arg; public FunctionForFit(T arg) { this.arg = arg; }
		 * 
		 * public T call() throws Exception { return arg; }
		 * 
		 * }
		 */
	}

}
