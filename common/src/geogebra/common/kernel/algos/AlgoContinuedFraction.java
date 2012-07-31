/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.kernel.locusequ.EquationElement;
import geogebra.common.kernel.locusequ.EquationScope;
import geogebra.common.main.App;

public class AlgoContinuedFraction extends AlgoElement {

	private NumberValue num; // input
	private NumberValue level; // input
	private GeoText text; // output
	private GeoBoolean shorthand;
	private static final int MAX_QUOTIENTS = 15;
	private long denominators[] = new long[MAX_QUOTIENTS];

	private StringBuilder sb = new StringBuilder();
	private boolean dotsNeeded;

	public AlgoContinuedFraction(Construction cons, String label,
			NumberValue num, NumberValue level,GeoBoolean shorthand) {
		this(cons, num, level,shorthand);
		text.setLabel(label);
	}

	AlgoContinuedFraction(Construction cons, NumberValue num, NumberValue level,GeoBoolean shorthand) {
		super(cons);
		this.num = num;
		this.level = level;
		this.shorthand = shorthand;
		text = new GeoText(cons);
		text.setIsTextCommand(true); // stop editing as text

		setInputOutput();
		compute();
	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoContinuedFraction;
	}

	@Override
	protected void setInputOutput() {
		int inputLength = 1 + (level ==null ? 0:1)+(shorthand ==null ? 0:1);
		input = new GeoElement[inputLength];
		input[0] = num.toGeoElement();
		int shorthandPos = 1;
		if (level != null) {
			shorthandPos =2;
			input[1] = level.toGeoElement();
		} 
		if(shorthand!=null){
			input[shorthandPos]= shorthand;
		}

		setOutputLength(1);
		setOutput(0, text);
		setDependencies(); // done by AlgoElement
	}

	public GeoText getResult() {
		return text;
	}

	@Override
	public final void compute() {
		StringTemplate tpl = StringTemplate.get(app.getFormulaRenderingType());
		if (num.isDefined() && (level == null || level.isDefined())) {
			int maxSteps = level == null ? 0 : (int) level.getDouble();
			int steps = DecimalToFraction(num.getDouble(),
					Kernel.STANDARD_PRECISION, denominators, maxSteps);
			if (steps < 1) {
				text.setUndefined();
				return;
			}

			switch (app.getFormulaRenderingType()) {
			case MATHML:
				if (steps == 1) { // integer
					sb.setLength(0);
					sb.append("<cn>");
					sb.append(kernel.format(denominators[0], tpl));
					sb.append("</cn>");
					text.setTextString(sb.toString());
				} else {
					sb.setLength(0);
					for (int i = 0; i < steps - 1; i++) {
						sb.append("<cn>");
						sb.append(denominators[i]);
						sb.append("</cn><plus/><cn>1</cn><apply><divide/>");
					}
					// checkDecimalFraction() needed for eg
					// FractionText[20.0764]

					sb.append("<cn>");
					sb.append(kernel.format(Kernel
							.checkDecimalFraction(denominators[steps - 1]), tpl));
					for (int i = 0; i < steps - 1; i++) {
						sb.append("</cn></apply>");
					}
					text.setTextString(sb.toString());
				}
				break;
			case LATEX:
				if (steps == 1) { // integer
					text.setTextString(kernel.format(
							Math.round(num.getDouble()), tpl));
				} else {
					if(shorthand==null || !shorthand.getBoolean()){
						appendLongLatex(steps,tpl);
					}
					else{
						sb.setLength(0);
						if (num.getDouble() < 0) {
							sb.append('-');
						}
						sb.append('[');
						sb.append(kernel.format(denominators[0], tpl));
						sb.append(';');
						for (int i = 1; i < steps - 1; i++) {

							sb.append(kernel.format(denominators[i], tpl));
							sb.append(",");
						}
						sb.append(kernel.format(denominators[steps-1], tpl));
						if(dotsNeeded)
							sb.append(",\\ldots");
						sb.append(']');
						text.setTextString(sb.toString());
					}
				}
				break;

			}
			text.setLaTeX(true, false);

		} else
			text.setTextString(app.getPlain("undefined"));
	}

	private void appendLongLatex(int steps,StringTemplate tpl) {
		sb.setLength(0);
		int start = 0;
		if (num.getDouble() < 0) {
			sb.append('-');
			sb.append(kernel.format(denominators[0], tpl));
			sb.append("-\\frac{1}{");
			start = 1;
		}
		for (int i = start; i < steps - 1; i++) {

			sb.append(kernel.format(denominators[i], tpl));
			sb.append("+\\frac{1}{");
		}
		sb.append(kernel.format(denominators[steps - 1], tpl));
		if(dotsNeeded)
			sb.append("+\\cdots");
		// checkDecimalFraction() needed for eg
		// FractionText[20.0764]
		for (int i = 0; i < steps - 1; i++) {
			sb.append("}");
		}
		App.debug(sb.toString());
		text.setTextString(sb.toString());
		
	}

	/*
	 * Algorithm To Convert A Decimal To A Fraction by John Kennedy Mathematics
	 * Department Santa Monica College 1900 Pico Blvd. Santa Monica, CA 90405
	 * http://homepage.smc.edu/kennedy_john/DEC2FRAC.PDF
	 */
	private int DecimalToFraction(double dec, double AccuracyFactor,
			long[] denominators, int maxSteps) {
		double FractionNumerator, FractionDenominator;
		double DecimalSign;
		double Z;
		double PreviousDenominator;
		double ScratchValue;

		if (Double.isNaN(dec))
			return -1;

		if (dec == Double.POSITIVE_INFINITY || dec == Double.NEGATIVE_INFINITY) {
			return -1;
		}

		if (dec < 0.0)
			DecimalSign = -1.0;
		else
			DecimalSign = 1.0;

		double decimal = Math.abs(dec);

		if (Math.abs(decimal - Math.floor(decimal)) < AccuracyFactor) { // handles
																		// exact
																		// integers
																		// including
																		// 0 �
			FractionNumerator = decimal * DecimalSign;
			FractionDenominator = 1.0;

			denominators[0] = (int) Math.floor(decimal);
			return 1;
		}
		if (decimal < 1.0E-19) { // X = 0 already taken care of �

			denominators[0] = 0;
			return 2;
		}
		if (decimal > 1.0E19) {
			denominators[0] = 999999999;
			return 1;
		}

		Z = decimal;
		PreviousDenominator = 0.0;
		FractionDenominator = 1.0;
		int steps = 0;
		dotsNeeded = true;
		do {
			App.debug(Z);
			denominators[steps] = (long) Math.floor(Z);
			Z = 1.0 / (Z - Math.floor(Z));
			ScratchValue = FractionDenominator;
			FractionDenominator = FractionDenominator * Math.floor(Z)
					+ PreviousDenominator;
			PreviousDenominator = ScratchValue;
			FractionNumerator = Math.floor(decimal * FractionDenominator + 0.5); // Rounding
																					// Function
			steps++;
			
			//we are too close to integer, next step would be uncertain
			if(Kernel.isEqual(Z, Math.floor(Z))){
				denominators[steps] = (long) Math.floor(Z);
				dotsNeeded = false;
				steps++;
				break;
			}
			
			//the approximation is within standard precision
			if (Math.abs((decimal - (FractionNumerator / FractionDenominator))) <= AccuracyFactor) {
				denominators[steps] = (long) Math.floor(Z);
				steps++;
				break;
			}
			
		} while ((maxSteps == 0 || steps < maxSteps) && Z != Math.floor(Z)
				&& steps < denominators.length);
		return steps;
	}

	@Override
	public boolean isLaTeXTextCommand() {
		return true;
	}

	@Override
	public EquationElement buildEquationElementForGeo(GeoElement element,
			EquationScope scope) {
		return null;
	}

	@Override
	public boolean isLocusEquable() {
		// TODO Consider locusequability
		return false;
	}

}
