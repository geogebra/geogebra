/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.kernel.statistics;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.SetRandomValue;
import geogebra.common.kernel.algos.AlgoTwoNumFunction;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.Commands;

/**
 * Computes RandomNormal[a, b]
 * 
 * @author Michael Borcherds
 * @version
 */
public class AlgoRandomBinomial extends AlgoTwoNumFunction implements SetRandomValue {

	public AlgoRandomBinomial(Construction cons, String label, NumberValue a,
			NumberValue b) {
		super(cons, label, a, b);

		// output is random number
		cons.addRandomGeo(num);
	}

	@Override
	public Commands getClassName() {
		return Commands.RandomBinomial;
	}

	@Override
	public final void compute() {
		//int frac[] = {0,0};
		//int [] frac = DecimalToFraction(b.getDouble(),0.00000001);
		//Application.debug(frac[0]+" "+frac[1]);

		if (input[0].isDefined() && input[1].isDefined()) {
			if (b.getDouble() < 0)
				num.setUndefined();
			else {
				// disabled randomBinomialTRS() as it doesn't work well
				// eg when p is near 0.5
				// http://www.geogebra.org/forum/viewtopic.php?f=8&t=18685
				//num.setValue(randomBinomialTRS((int)a.getDouble(), b.getDouble()));
				num.setValue(randomBinomial((int)a.getDouble(), b.getDouble()));
			}
			
		} else
			num.setUndefined();
	}

	public void setRandomValue(double d) {
		d = Math.round(Kernel.checkInteger(d));
		
		if (d >= 0 && d <= a.getDouble()) {
			num.setValue(d);
			num.updateRepaint();
		}
			
	}
	
	private int randomBinomial(double n, double p) {

		int count = 0;
		for (int i = 0 ; i < n ; i++) {
			if (app.getRandomNumber() < p) count ++;
		}

		return count;

	}

	private static double halflog2pi = 0.5 * Math.log(2 * Math.PI);

	private static double logtable[] = new double[10];

//	private int[] DecimalToFraction(double Decimal, double AccuracyFactor) {
//		double FractionNumerator, FractionDenominator;
//		double DecimalSign;
//		double Z;
//		double PreviousDenominator;
//		double ScratchValue;
//
//		int ret[] = {0,0};
//		if (Decimal < 0.0) DecimalSign = -1.0; else DecimalSign = 1.0;
//		Decimal = Math.abs(Decimal);
//		if (Decimal == Math.floor(Decimal)) { // handles exact integers including 0 �
//			FractionNumerator = Decimal * DecimalSign;
//			FractionDenominator = 1.0;
//			ret[0] = (int)FractionNumerator;
//			ret[1] = (int)FractionDenominator;
//			return ret;
//		}
//		if (Decimal < 1.0E-19) { // X = 0 already taken care of �
//			FractionNumerator = DecimalSign;
//			FractionDenominator = 9999999999999999999.0;
//			ret[0] = (int)FractionNumerator;
//			ret[1] = (int)FractionDenominator;
//			return ret;
//		}
//		if (Decimal > 1.0E19) {
//			FractionNumerator = 9999999999999999999.0*DecimalSign;
//			FractionDenominator = 1.0;
//			ret[0] = (int)FractionNumerator;
//			ret[1] = (int)FractionDenominator;
//			return ret;
//		}
//		Z = Decimal;
//		PreviousDenominator = 0.0;
//		FractionDenominator = 1.0;
//		do {
//			Z = 1.0/(Z - Math.floor(Z));
//			ScratchValue = FractionDenominator;
//			FractionDenominator = FractionDenominator * Math.floor(Z) + PreviousDenominator;
//			PreviousDenominator = ScratchValue;
//			FractionNumerator = Math.floor(Decimal * FractionDenominator + 0.5); // Rounding Function
//		} while ( Math.abs((Decimal - (FractionNumerator /FractionDenominator))) > AccuracyFactor && Z != Math.floor(Z));
//		FractionNumerator = DecimalSign*FractionNumerator;
//
//		ret[0] = (int)FractionNumerator;
//		ret[1] = (int)FractionDenominator;
//		return ret;
//	}

}
