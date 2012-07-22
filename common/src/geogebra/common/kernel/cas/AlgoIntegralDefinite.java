/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.kernel.cas;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.AlgoDrawInformation;
import geogebra.common.kernel.algos.AlgoFunctionFreehand;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.locusequ.EquationElement;
import geogebra.common.kernel.locusequ.EquationScope;
import geogebra.common.kernel.roots.RealRootAdapter;
import geogebra.common.kernel.roots.RealRootFunction;
import geogebra.common.main.App;

import org.apache.commons.math.ConvergenceException;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.MaxIterationsExceededException;
import org.apache.commons.math.analysis.integration.LegendreGaussIntegrator;

/**
 * Integral of a function (GeoFunction)
 * 
 * @author Markus Hohenwarter
 */
public class AlgoIntegralDefinite extends AlgoUsingTempCASalgo implements
		AlgoDrawInformation, AlgoIntegralDefiniteInterface {

	private GeoFunction f; // input
	private NumberValue a, b; // input
	private GeoBoolean evaluate; // input
	private GeoElement ageo, bgeo;
	private GeoNumeric n; // output g = integral(f(x), x, a, b)
	private boolean numeric;
	// for symbolic integration
	private GeoFunction symbIntegral;

	// for numerical adaptive GaussQuad integration
	private static final int FIRST_ORDER = 3;
	private static final int SECOND_ORDER = 5;
	private static final int MAX_ITER = 5;
	private static LegendreGaussIntegrator firstGauss, secondGauss;
	private static int adaptiveGaussQuadCounter = 0;
	private static final int MAX_GAUSS_QUAD_CALLS = 500;

	public AlgoIntegralDefinite(Construction cons, String label, GeoFunction f,
			NumberValue a, NumberValue b,boolean numeric) {
		this(cons, f, a, b, null,numeric);
		this.numeric = numeric;
		n.setLabel(label);
	}

	public AlgoIntegralDefinite(Construction cons, String label, GeoFunction f,
			NumberValue a, NumberValue b, GeoBoolean evaluate) {
		this(cons, f, a, b, evaluate);
		n.setLabel(label);
	}

	public AlgoIntegralDefinite(Construction cons, GeoFunction f,
			NumberValue a, NumberValue b, GeoBoolean evaluate) {
		this(cons, f, a, b, evaluate, false);

	}

	public AlgoIntegralDefinite(Construction cons, GeoFunction f,
			NumberValue a, NumberValue b, GeoBoolean evaluate,
			boolean evaluateNumerically) {
		super(cons);
		this.f = f;
		n = new GeoNumeric(cons); // output
		this.a = a;
		this.b = b;
		ageo =  a.toGeoElement();
		bgeo =  b.toGeoElement();
		this.evaluate = evaluate;

		// create helper algorithm for symbolic integral
		// don't use symbolic integral for conditional functions
		// or if it should not be evaluated (i.e. a shade-only integral)
		if ((evaluate == null || evaluate.getBoolean())
				&& !f.isGeoFunctionConditional() && !f.isFreehandFunction() && !evaluateNumerically) {
			AlgoIntegral algoInt = new AlgoIntegral(cons, f, null);
			symbIntegral = (GeoFunction) algoInt.getResult();
			cons.removeFromConstructionList(algoInt);
			// make sure algo is removed properly
			algoCAS = algoInt;
		}

		setInputOutput(); // for AlgoElement
		compute();
		n.setDrawable(true);
	}

	public AlgoIntegralDefinite(GeoFunction f, NumberValue a, NumberValue b,
			GeoBoolean evaluate) {
		super(f.getConstruction(), false);
		this.f = f;
		this.a = a;
		this.b = b;
		this.evaluate = evaluate;
	}

	@Override
	public Algos getClassName() {
		return numeric ? Algos.AlgoIntegralDefinite : Algos.AlgoNIntegral;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		if (evaluate == null) {
			input = new GeoElement[3];
			input[0] = f;
			input[1] = ageo;
			input[2] = bgeo;
		} else {
			input = new GeoElement[4];
			input[0] = f;
			input[1] = ageo;
			input[2] = bgeo;
			input[3] = evaluate;
		}

		setOutputLength(1);
		setOutput(0, n);
		setDependencies(); // done by AlgoElement
	}

	public GeoNumeric getIntegral() {
		return n;
	}

	double getIntegralValue() {
		return n.getValue();
	}

	public GeoFunction getFunction() {
		return f;
	}

	public NumberValue getA() {
		return a;
	}

	public NumberValue getB() {
		return b;
	}

	@Override
	public final void compute() {
		if (!f.isDefined() || !ageo.isDefined() || !bgeo.isDefined()) {
			n.setUndefined();
			return;
		}

		// check for equal bounds
		double lowerLimit = a.getDouble();
		double upperLimit = b.getDouble();
		if (Kernel.isEqual(lowerLimit, upperLimit)) {
			n.setValue(0);
			return;
		}

		// check if f(a) and f(b) are defined
		double fa = f.evaluate(lowerLimit);
		double fb = f.evaluate(upperLimit);
		if (Double.isNaN(fa) || Double.isInfinite(fa) || Double.isNaN(fb)
				|| Double.isInfinite(fb)) {
			n.setUndefined();
			return;
		}

		// return if it should not be evaluated (i.e. is shade-only)
		if (evaluateOnly()) {
			n.setValue(Double.NaN);
			return;
		}

		/*
		 * Try to use symbolic integral
		 * 
		 * We only do this for functions that do NOT include divisions by their
		 * variable. Otherwise there might be problems like: Integral[ 1/x, -2,
		 * -1 ] would be undefined (log(-1) - log(-2)) Integral[ 1/x^2, -1, 1 ]
		 * would be defined (-2)
		 */
		if (symbIntegral != null && symbIntegral.isDefined()
				&& !f.includesDivisionByVar()) {
			double val = symbIntegral.evaluate(upperLimit)
					- symbIntegral.evaluate(lowerLimit);
			n.setValue(val);
			if (n.isDefined())
				return;
		}

		// numerical integration
		// max_error = ACCURACY; // current maximum error
		// maxstep = 0;
		
		if (f.isFreehandFunction()) {
			n.setValue(freehandIntegration(f, lowerLimit, upperLimit));
			
			//AbstractApplication.debug(n.getValue()+" "+numericIntegration(f, lowerLimit, upperLimit));
			
		} else {
			n.setValue(numericIntegration(f, lowerLimit, upperLimit));
			
		}
		/*
		 * Application.debug("***\nsteps: " + maxstep);
		 * Application.debug("max_error: " + max_error);
		 */
	}

	private double freehandIntegration(GeoFunction f2, double lowerLimit,
			double upperLimit) {
		
		int multiplier = 1;
		
		if (lowerLimit > upperLimit) {
			// swap a and b
			double temp = lowerLimit;
			lowerLimit = upperLimit;
			upperLimit = temp;
			multiplier = -1;
		}

		//AbstractApplication.debug("1");

		AlgoFunctionFreehand algo = (AlgoFunctionFreehand) f2.getParentAlgorithm();
		
		GeoList list = algo.getList();
		
		double a1 = ((NumberValue)list.get(0)).getDouble();
		double b1 = ((NumberValue)list.get(1)).getDouble();
		
		if (lowerLimit < a1 || upperLimit > b1) {
			return Double.NaN;
		}
		
		double n = list.size() - 2;
		
		double step = (b1 - a1) / (n - 1);
		
		int startGap = (int) Math.ceil((lowerLimit - a1) / step);
		int endGap = (int) Math.ceil((b1 - upperLimit) / step);
		
		double startx = a1 + step * startGap;
		double endx = b1 - step * endGap;
		
		//int noOfSteps = (int) ((b - step * end - (a + step * start) )/step);
		//int noOfSteps = (int) ((b - step * end - a - step * start) )/step)
		// should be an integer, add Math.round in case of rounding error
		int noOfSteps = (int) Math.round((b1 - a1) / step - endGap - startGap) + 1;
		
		double area = 0;
		double sum = 0;
		//AbstractApplication.debug("noOfSteps = "+noOfSteps);
		//AbstractApplication.debug("step = "+step);
		//AbstractApplication.debug("startx = "+startx);
		//AbstractApplication.debug("endx = "+endx);
		//AbstractApplication.debug("start = "+startGap);
		//AbstractApplication.debug("end = "+endGap);
		// trapezoidal rule
		if (noOfSteps > 0) {
			
			
				for (int i = 0 ; i < noOfSteps ; i++) {
					// y-coordinate
					double y = ((NumberValue)list.get(2 + i + startGap)).getDouble();
					if (i == 0 || (i == noOfSteps - 1)) {
						sum += y;
					} else {
						sum += 2*y;
					}
				}
			// now add the extra bits at the start and end
			
			area = sum * step / 2.0;

			if (!Kernel.isZero(startx - lowerLimit)) {
				// h (a+b) /2
				area += (startx - lowerLimit) * (f.evaluate(startx) + f.evaluate(lowerLimit)) / 2.0;
			}
		
			if (!Kernel.isZero(endx - upperLimit)) {
				// h (a+b) /2
				area += (upperLimit - endx) * (f.evaluate(endx) + f.evaluate(upperLimit)) / 2.0;
			}
		} else {
			// just a trapezium from lowerLimit to upperLimit
			
			area = (upperLimit - lowerLimit) * (f.evaluate(lowerLimit) + f.evaluate(upperLimit)) / 2.0;
		}
	
		return Kernel.checkDecimalFraction(area) * multiplier;
	
	}

	// private int maxstep;

	/**
	 * Computes integral of function fun in interval a, b using an adaptive
	 * Gauss quadrature approach.
	 * 
	 * @param fun
	 *            function
	 * @param a
	 *            lower bound
	 * @param b
	 *            upper bound
	 * @return integral value
	 */
	public static double numericIntegration(RealRootFunction fun, double a,
			double b) {
		adaptiveGaussQuadCounter = 0;
		if (a > b) {
			return -doAdaptiveGaussQuad(fun, b, a);
		}
		return doAdaptiveGaussQuad(fun, a, b);

		// System.out.println("calls: " + adaptiveGaussQuadCounter);

	}

	private static double doAdaptiveGaussQuad(RealRootFunction fun, double a,
			double b) {
		if (++adaptiveGaussQuadCounter > MAX_GAUSS_QUAD_CALLS) {
			return Double.NaN;
		}

		// init GaussQuad classes for numerical integration
		if (firstGauss == null) {
			firstGauss = new LegendreGaussIntegrator(FIRST_ORDER, MAX_ITER);
			secondGauss = new LegendreGaussIntegrator(SECOND_ORDER, MAX_ITER);
		}

		double firstSum = 0;
		double secondSum = 0;

		boolean error = false;

		// integrate using gauss quadrature
		try {
			firstSum = firstGauss.integrate((new RealRootAdapter(fun)), a, b);
			if (Double.isNaN(firstSum))
				return Double.NaN;
			secondSum = secondGauss.integrate((new RealRootAdapter(fun)), a, b);
			if (Double.isNaN(secondSum))
				return Double.NaN;
		} catch (MaxIterationsExceededException e) {
			error = true;
		} catch (ConvergenceException e) {
			error = true;
		} catch (FunctionEvaluationException e) {
			return Double.NaN;
		} catch (IllegalArgumentException e) {
			return Double.NaN;
		}

		// if (!error) Application.debug(a+" "+b+" "+(firstSum - secondSum),
		// Kernel.isEqual(firstSum, secondSum, Kernel.STANDARD_PRECISION) ? 1 :
		// 0);
		// else Application.debug(a+" "+b+" error",1);

		// check if both results are equal
		boolean equal = !error
				&& Kernel.isEqual(firstSum, secondSum,
						Kernel.STANDARD_PRECISION);

		if (equal) {
			// success
			return secondSum;
		}
		double mid = (a + b) / 2;
		double left = doAdaptiveGaussQuad(fun, a, mid);
		if (Double.isNaN(left)) {
			return Double.NaN;
		}
		return left + doAdaptiveGaussQuad(fun, mid, b);
	}

	@Override
	final public String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return app.getPlain("IntegralOfAfromBtoC", f.getLabel(tpl),
				ageo.getLabel(tpl), bgeo.getLabel(tpl));
	}

	public AlgoDrawInformation copy() {
		if (evaluate != null)
			return new AlgoIntegralDefinite((GeoFunction) f.copy(),
					(NumberValue) a.deepCopy(kernel),
					(NumberValue) b.deepCopy(kernel),
					(GeoBoolean) evaluate.copy());
		return new AlgoIntegralDefinite((GeoFunction) f.copy(),
				(NumberValue) a.deepCopy(kernel),
				(NumberValue) b.deepCopy(kernel), null);
	}

	/*
	 * make sure shaded-only integrals are drawn
	 */
	public boolean evaluateOnly() {
		return evaluate != null && !evaluate.getBoolean();
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
