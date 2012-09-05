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
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.util.MyMath2;

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
	public Algos getClassName() {
		return Algos.AlgoRandomBinomial;
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
	
	/*
	 * The generation of binomial random variates (1993) 
	 * by Wolfgang Hormann, Inst Statistik, Wirtschaftuniv Wien, A- Wien
	 * Journal of Statistical Computation and Simulation
	 * http://eeyore.wu-wien.ac.at/papers/92-04-07.wh.ps.gz 
	 * http://epub.wu-wien.ac.at/dyn/virlib/wp/eng/mediate/epub-wu-01_6f1.pdf?ID=epub-wu-01_6f1
	 * 
	 * Algorithm BTRS
	 */
	private int randomBinomialTRS(int n, double p) {

		if (p > 0.5) return n - randomBinomialTRS(n, 1 - p);

		if (n * p < 10) return randomBinomial(n, p);

		double spq = Math.sqrt(n*p*(1-p));

		double b = 1.15 + 2.53 * spq;
		double a = -0.0873 + 0.0248 * b + 0.01 * p;
		double c = n * p + 0.5;
		double v_r = 0.92 - 4.2 / b;

		double us = 0;
		double v = 0;

		while (true) {

			int k = -1;
			while (k < 0 || k > n) {
				double u = app.getRandomNumber() - 0.5;
				v = app.getRandomNumber();
				us = 0.5 - Math.abs(u);
				k = (int)Math.floor((2 * a / us + b) * u + c);
				if (us >= 0.07 && v < v_r) return k;
			}

			double alpha = (2.83 + 5.1/b) * spq;
			double lpq = Math.log(p / (1 - p));
			int m = (int)Math.floor((n + 1) * p);
			double h = logOfKFactorial(m) + logOfKFactorial((n-m));

			v = v * alpha / (a / (us * us) + b);

			if (v <= h - logOfKFactorial(k) - logOfKFactorial(n-k) + (k-m)*lpq) return k;
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

	private static double logOfKFactorial(int k) {
		if (k<10) {
			if (logtable[k] == 0) logtable[k] = Math.log(MyMath2.factorial(k+1d));
			return logtable[k];
		}

		// Stirling approximation
		return halflog2pi + (k+0.5) * Math.log(k+1) - (k+1) + (1/12.0 - (1/360.0 - 1/1260.0/(k+1)/(k+1))/(k+1)/(k+1))/(k+1);
	}

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
