/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.algos;

import java.util.ArrayList;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.optimization.ExtremumFinderI;
import org.geogebra.common.kernel.optimization.NegativeRealRootFunction;
import org.geogebra.common.util.DoubleUtil;

/**
 * Superclass for lower/upper sum of function f in interval [a, b] with n
 * intervals
 * 
 * Find global minimum in an interval with the following heuristic:
 * 
 * 1) sample the function for some values of x in [a, b]
 * 
 * 2) get x[i] with minimal f(x[i])
 * 
 * 3) use parabolic interpolation and Brent's Method in One Dimension for
 * interval x[i-1] to x[i+1] (Numerical Recipes in C++, pp.406)
 */
public abstract class AlgoFunctionAreaSums extends AlgoElement
		implements DrawInformationAlgo {

	// largest possible number of rectangles
	private static final int MAX_RECTANGLES = 10000;
	// tolerance for parabolic interpolation
	private static final double TOLERANCE = 1E-7;
	// subsample every 5 pixels
	private static final int SAMPLE_PIXELS = 5;

	/**
	 * number of points used for checking that function is defined on the
	 * interval
	 **/
	double CHECKPOINTS = 100;
	private SumType type;

	private GeoNumberValue d; // input: divider for Rectangle sum, 0..1
	// input
	private GeoList list1;
	private GeoList list2;
	private GeoList list3;
	private GeoElement ageo;
	private GeoElement bgeo;
	private GeoElement ngeo;
	private GeoElement densityGeo;
	private GeoElement useDensityGeo;
	private GeoElement isCumulative;
	private GeoElement p1geo;
	private GeoNumeric sum; // output sum

	private int N;
	private double STEP;
	private double[] yval; // y value (= min) in interval 0 <= i < N
	private double[] leftBorder; // leftBorder (x val) of interval 0 <= i < N
	// private double [] widths;
	private ExtremumFinderI extrFinder;

	// maximum frequency of bar chart
	// this is used by stat dialogs when setting window dimensions
	private double freqMax;

	private boolean histogramRight;

	// input
	private GeoFunctionable f;
	private GeoNumberValue a;
	private GeoNumberValue b;
	private GeoNumberValue n;
	private GeoNumberValue density;
	private GeoNumberValue p1;

	/**
	 * Bar chart type
	 */
	public enum SumType {
		/** Upper Rieeman sum **/

		UPPERSUM,
		/** Lower Rieman sum **/
		LOWERSUM,
		/** Left Rieman sum (Ulven: 09.02.11) **/
		LEFTSUM,
		/** Rectangle sum with divider for step interval (Ulven: 09.02.11) **/
		RECTANGLESUM,
		/** Trapezoidal sum **/
		TRAPEZOIDALSUM,

		/**
		 * Histogram from(class boundaries, raw data) with default density = 1
		 * or Histogram from(class boundaries, frequencies) no density required
		 **/
		HISTOGRAM,
		/** Histogram from(class boundaries, raw data) with given density **/
		HISTOGRAM_DENSITY,

		/** barchart of a discrete probability distribution **/
		BARCHART_BERNOULLI
	}

	/**
	 * @return the p1
	 */
	public GeoNumberValue getP1() {
		return p1;
	}

	/**
	 * @return the densityGeo
	 */
	public GeoElement getDensityGeo() {
		return densityGeo;
	}

	/**
	 * @return the useDensityGeo
	 */
	public GeoElement getUseDensityGeo() {
		return useDensityGeo;
	}

	/**
	 * @return the isCumulative
	 */
	public GeoElement getIsCumulative() {
		return isCumulative;
	}

	/**
	 * Returns maximum frequency of a bar chart or histogram
	 * 
	 * @return maximum frequency of a bar chart or histogram
	 */
	public double getFreqMax() {
		if (yval == null) {
			return Double.NaN;
		}
		freqMax = 0;
		for (int k = 0; k < yval.length; ++k) {
			freqMax = Math.max(yval[k], freqMax);
		}
		return freqMax;
	}

	/**
	 * Returns left class borders of a bar chart or histogram
	 * 
	 * @return left class borders of a bar chart or histogram
	 */
	public double[] getLeftBorder() {
		return leftBorder;
	}

	/**
	 * Rectangle sum
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param f
	 *            function
	 * @param a
	 *            left bound
	 * @param b
	 *            right bound
	 * @param n
	 *            number of bars
	 * @param d
	 *            bar percentage for rectangle sum
	 * @param type
	 *            sum type
	 */
	public AlgoFunctionAreaSums(Construction cons, String label,
			GeoFunctionable f, GeoNumberValue a, GeoNumberValue b,
			GeoNumberValue n, GeoNumberValue d, SumType type) {

		super(cons);

		this.type = type;

		this.f = f;
		this.a = a;
		this.b = b;
		this.n = n;
		this.d = d;
		ageo = a.toGeoElement();
		bgeo = b.toGeoElement();
		ngeo = n.toGeoElement();

		sum = new GeoNumeric(cons); // output
		setInputOutput(); // for AlgoElement
		compute();
		sum.setDrawable(true);
		sum.setLabel(label);
	}

	/**
	 * Rectangle sum: copy
	 * 
	 * @param f
	 *            function
	 * @param a
	 *            left bound
	 * @param b
	 *            right bound
	 * @param n
	 *            number of bars
	 * @param d
	 *            bar percentage for rectangle sum
	 */
	public AlgoFunctionAreaSums(GeoFunctionable f, GeoNumberValue a,
			GeoNumberValue b, GeoNumberValue n, GeoNumberValue d) {
		super(f.getConstruction(), false);
		this.type = SumType.RECTANGLESUM;
		this.f = f;
		this.a = a;
		this.b = b;
		this.n = n;
		this.d = d;
		ageo = a.toGeoElement();
		bgeo = b.toGeoElement();
		ngeo = n.toGeoElement();

		sum = new GeoNumeric(cons); // output
		setInputOutput(); // for AlgoElement
		compute();
		sum.setDrawable(true);
	}

	/**
	 * Upper o lower sum
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param f
	 *            function
	 * @param a
	 *            left bound
	 * @param b
	 *            right bound
	 * @param n
	 *            number of bars
	 * @param type
	 *            sum type
	 */
	public AlgoFunctionAreaSums(Construction cons, String label,
			GeoFunctionable f, GeoNumberValue a, GeoNumberValue b,
			GeoNumberValue n, SumType type) {

		super(cons);

		this.type = type;

		extrFinder = cons.getExtremumFinder();

		this.f = f;
		this.a = a;
		this.b = b;
		this.n = n;
		ageo = a.toGeoElement();
		bgeo = b.toGeoElement();
		ngeo = n.toGeoElement();

		sum = new GeoNumeric(cons); // output
		setInputOutput(); // for AlgoElement
		compute();
		sum.setDrawable(true);
		sum.setLabel(label);

	}

	/**
	 * Upper / lower / trapezoidal / left sum: copy
	 * 
	 * @param cons1
	 *            construction
	 * @param vals
	 *            function values
	 * @param a
	 *            left bound
	 * @param b
	 *            right bound
	 * @param n
	 *            number of bars
	 * @param type
	 *            sum type
	 * @param borders
	 *            interval borders
	 */
	public AlgoFunctionAreaSums(GeoNumberValue a, GeoNumberValue b,
			GeoNumberValue n, SumType type, double[] vals, double[] borders,
			Construction cons1) {
		super(cons1, false);
		this.type = type;

		this.a = a;
		this.b = b;
		this.n = n;
		this.yval = vals;
		this.leftBorder = borders;
		N = (int) Math.round(n.getDouble());

	}

	/**
	 * HISTOGRAM[ &lt;list of class boundaries&gt;, &lt;list of heights&gt; ]
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param list1
	 *            class boundaries
	 * @param list2
	 *            heights
	 * @param right
	 *            right histogram?
	 */
	public AlgoFunctionAreaSums(Construction cons, String label, GeoList list1,
			GeoList list2, boolean right) {
		this(cons, list1, list2, right);
		sum.setLabel(label);
	}

	/**
	 * HISTOGRAM[ &lt;list of class boundaries&gt;, &lt;list of heights&gt; ]
	 * (no label)
	 * 
	 * @param cons
	 *            construction
	 * @param list1
	 *            class boundaries
	 * @param list2
	 *            heights
	 * @param right
	 *            right histogram?
	 */
	public AlgoFunctionAreaSums(Construction cons, GeoList list1, GeoList list2,
			boolean right) {

		super(cons);

		type = SumType.HISTOGRAM;
		this.histogramRight = right;
		this.list1 = list1;
		this.list2 = list2;

		sum = new GeoNumeric(cons); // output
		setInputOutput(); // for AlgoElement
		compute();

		sum.setDrawable(true);
	}

	/**
	 * Histogram copy
	 * 
	 * @param cons
	 *            construction
	 * @param vals
	 *            heights
	 * @param borders
	 *            bclass borders
	 * @param N
	 *            number of classes
	 */
	public AlgoFunctionAreaSums(Construction cons, double[] vals,
			double[] borders, int N) {
		super(cons, false);

		type = SumType.HISTOGRAM;
		this.leftBorder = borders;
		this.yval = vals;
		this.N = N;
	}

	/**
	 * Histogram [&lt;list of class boundaries&gt;, &lt;list of raw data&gt;,
	 * &lt;useDensity&gt;, &lt;densityFactor&gt;]
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param isCumulative
	 *            cumulative?
	 * @param list1
	 *            class boundaries
	 * @param list2
	 *            raw data
	 * @param list3
	 *            optional frequencies
	 * @param useDensity
	 *            use density?
	 * @param density
	 *            density
	 * @param right
	 *            right histogram?
	 */
	public AlgoFunctionAreaSums(Construction cons, String label,
			GeoBoolean isCumulative, GeoList list1, GeoList list2,
			GeoList list3, GeoBoolean useDensity, GeoNumeric density,
			boolean right) {

		this(cons, isCumulative, list1, list2, list3, useDensity, density,
				right);

		sum.setLabel(label);
	}

	/**
	 * Histogram [&lt;list of class boundaries&gt;, &lt;list of raw data&gt;,
	 * &lt;useDensity&gt;, &lt;densityFactor&gt;]
	 * 
	 * @param cons
	 *            construction
	 * @param isCumulative
	 *            cumulative?
	 * @param list1
	 *            class boundaries
	 * @param list2
	 *            raw data
	 * @param list3
	 *            optional frequencies
	 * @param useDensity
	 *            use density?
	 * @param density
	 *            density
	 * @param right
	 *            right histogram?
	 */
	public AlgoFunctionAreaSums(Construction cons, GeoBoolean isCumulative,
			GeoList list1, GeoList list2, GeoList list3, GeoBoolean useDensity,
			GeoNumeric density, boolean right) {

		super(cons);
		this.histogramRight = right;
		type = SumType.HISTOGRAM_DENSITY;

		this.isCumulative = isCumulative;
		this.list1 = list1;
		this.list2 = list2;
		this.list3 = list3;
		this.density = density;
		if (density != null) {
			densityGeo = density.toGeoElement();
		}

		this.useDensityGeo = useDensity;

		sum = new GeoNumeric(cons); // output
		setInputOutput(); // for AlgoElement
		compute();
		if (isCumulative != null && isCumulative.getBoolean()) {
			yval[yval.length - 1] = 0.0;
		}
		sum.setDrawable(true);
	}

	/**
	 * Histogram density copy constructor.
	 * 
	 * @param isCumulative
	 *            cumulative?
	 * @param useDensity
	 *            use density?
	 * @param density
	 *            density
	 * @param vals
	 *            heights
	 * @param borders
	 *            class borders
	 * @param N
	 *            number of classes
	 */
	public AlgoFunctionAreaSums(GeoBoolean isCumulative, GeoBoolean useDensity,
			GeoNumeric density, double[] vals, double[] borders, int N) {
		super(useDensity.getConstruction(), false);

		type = SumType.HISTOGRAM_DENSITY;

		this.isCumulative = isCumulative;
		this.N = N;
		this.density = density;
		this.useDensityGeo = useDensity;

		this.leftBorder = borders;
		this.yval = vals;
	}

	/**
	 * Bernoulli bar chart
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            output label
	 * @param p1
	 *            success probability
	 * @param isCumulative
	 *            cumulative?
	 * @param type
	 *            (Bernoulli)
	 */
	public AlgoFunctionAreaSums(Construction cons, String label,
			GeoNumberValue p1,
			GeoBoolean isCumulative, SumType type) {

		super(cons);

		this.type = type;
		this.p1 = p1;
		p1geo = p1.toGeoElement();

		this.isCumulative = isCumulative;
		sum = new GeoNumeric(cons); // output
		setInputOutput(); // for AlgoElement
		compute();
		sum.setDrawable(true);
		sum.setLabel(label);

		if (yval == null) {
			yval = new double[0];
			leftBorder = new double[0];
		}
	}

	protected AlgoFunctionAreaSums(GeoNumberValue p1, GeoBoolean isCumulative,
			SumType type,
			GeoNumberValue a, GeoNumberValue b, double[] vals, double[] borders,
			int N, Construction cons) {

		super(cons, false);

		this.type = type;
		this.p1 = p1;
		p1geo = p1.toGeoElement();

		this.isCumulative = isCumulative;
		this.a = a;
		this.b = b;
		this.yval = vals;

		this.leftBorder = borders;
		this.N = N;
	}

	/**
	 * @return whether this is right histogram
	 */
	public boolean isRight() {
		return histogramRight;
	}

	@Override
	final public boolean euclidianViewUpdate() {
		compute(true);
		return false;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {

		switch (type) {
		case UPPERSUM:
		case LOWERSUM:
		case TRAPEZOIDALSUM:
		case LEFTSUM: // Ulven: 09.02.11
			input = new GeoElement[4];
			input[0] = f.toGeoElement();
			input[1] = ageo;
			input[2] = bgeo;
			input[3] = ngeo;
			break;
		case RECTANGLESUM: // Ulven: 09.02.11
			input = new GeoElement[5];
			input[0] = f.toGeoElement();
			input[1] = ageo;
			input[2] = bgeo;
			input[3] = ngeo;
			input[4] = d.toGeoElement();
			break;

		case HISTOGRAM:
		case HISTOGRAM_DENSITY:

			ArrayList<GeoElement> tempList = new ArrayList<>();

			if (isCumulative != null) {
				tempList.add(isCumulative);
			}
			tempList.add(list1);
			tempList.add(list2);
			if (list3 != null) {
				tempList.add(list3);
			}
			if (useDensityGeo != null) {
				tempList.add(useDensityGeo);
			}
			if (densityGeo != null) {
				tempList.add(densityGeo);
			}
			input = new GeoElement[tempList.size()];
			input = tempList.toArray(input);

			break;

		case BARCHART_BERNOULLI:
			ArrayList<GeoElement> inputList = new ArrayList<>();
			inputList.add(p1geo);

			if (isCumulative != null) {
				inputList.add(isCumulative);
			}

			input = new GeoElement[inputList.size()];
			input = inputList.toArray(input);
			break;
		}
		setOnlyOutput(sum);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * function
	 * 
	 * @return function
	 */
	public GeoFunctionable getF() {
		return f;
	}

	/**
	 * number of intervals
	 * 
	 * @return number of intervals
	 */
	public int getIntervals() {
		return N;
	}

	/**
	 * Returns length of step for sums
	 * 
	 * @return length of step for sums
	 */
	public double getStep() {
		return STEP;
	}

	/**
	 * Returns list of function values
	 * 
	 * @return list of function values
	 */
	public final double[] getValues() {
		return yval;
	}

	/**
	 * Returns the resulting sum
	 * 
	 * @return the resulting sum
	 */
	public GeoNumeric getSum() {
		return sum;
	}

	/**
	 * Returns lower bound for sums
	 * 
	 * @return lower bound for sums
	 */
	public GeoNumberValue getA() {
		return a == null ? new GeoNumeric(cons, Double.NaN) : a;
	}

	/**
	 * Returns upper bound for sums
	 * 
	 * @return upper bound for sums
	 */
	public GeoNumberValue getB() {
		return b == null ? new GeoNumeric(cons, Double.NaN) : b;
	}

	/**
	 * Returns n
	 * 
	 * @return n
	 */
	public GeoNumeric getN() {
		return (GeoNumeric) ngeo;
	}

	/**
	 * Returns d
	 * 
	 * @return d
	 */
	public GeoNumberValue getD() {
		return d;
	}

	/**
	 * Returns list of raw data
	 * 
	 * @return list of raw data
	 */
	public GeoList getList1() {
		return list1;
	}

	/**
	 * Returns list of frequencies for histogram
	 * 
	 * @return list of frequencies for histogram
	 */
	public GeoList getList2() {
		return list2;
	}

	@Override
	public final void compute() {
		compute(false);
	}

	private void compute(boolean onlyZoom) {
		GeoElement geo, geo2; // temporary variables

		boolean isDefined = true;

		// problem with Sequence[LowerSum[x^2, i, i + 1, 1], i, 1, 5] on file
		// load
		if (sum == null) {
			sum = new GeoNumeric(cons);
		}

		switch (type) {
		case LOWERSUM:
		case UPPERSUM:

			if (f == null || !(f.isDefined() && ageo.isDefined()
					&& bgeo.isDefined() && ngeo.isDefined())) {
				sum.setUndefined();
				return;
			}

			UnivariateFunction fun = f.getFunction();
			double ad = a.getDouble();
			double bd = b.getDouble();
			if (!onlyZoom) {
				isDefined = functionDefined(ad, bd);
			} else {
				isDefined = sum.isDefined();
			}
			double ints = n.getDouble();
			if (ints < 1) {
				sum.setUndefined();
				return;
			} else if (ints > MAX_RECTANGLES) {
				N = MAX_RECTANGLES;
			} else {
				N = (int) Math.round(ints);
			}
			STEP = (bd - ad) / N;

			// calc minimum in every interval

			if (yval == null || yval.length < N) {
				yval = new double[N];
				leftBorder = new double[N];
			}
			UnivariateFunction fmin = fun;
			if (type == SumType.UPPERSUM) {
				fmin = new NegativeRealRootFunction(fun); // use -f to find
															// maximum
			}

			double totalArea = 0;
			double left, right, min;

			// calculate the min and max x-coords of what actually needs to be
			// drawn
			// do subsampling only in this region

			double visibleMin = Math.max(Math.min(ad, bd),
					kernel.getViewsXMin(sum));

			double visibleMax = Math.min(Math.max(ad, bd),
					kernel.getViewsXMax(sum));

			// subsample every 5 pixels
			double noOfSamples = kernel.getApplication().countPixels(visibleMin,
					visibleMax) / SAMPLE_PIXELS;

			double subStep = Math.abs(visibleMax - visibleMin) / noOfSamples;
			boolean doSubSamples = !DoubleUtil.isZero(subStep)
					&& Math.abs(STEP) > subStep;
			boolean positiveStep = STEP >= 0;
			for (int i = 0; i < N; i++) {
				leftBorder[i] = ad + i * STEP;

				if (positiveStep) {
					left = leftBorder[i];
					right = leftBorder[i] + STEP;
				} else {
					left = leftBorder[i] + STEP;
					right = leftBorder[i];
				}

				min = Double.POSITIVE_INFINITY;

				// heuristic: take some samples in this interval
				// and find smallest one
				// subsampling needed in case there are two eg minimums and we
				// get the wrong one with extrFinder.findMinimum()
				// subsample visible bit only
				if (doSubSamples && ((STEP > 0 ? left : right) < visibleMax
						&& (STEP > 0 ? right : left) > visibleMin)) {
					double y, minSample = left;
					for (double x = left; x < right; x += subStep) {
						y = fmin.value(x);
						if (y < min) {
							min = y;
							minSample = x;
						}
					}
					// if the minimum is on the left border then minSample ==
					// left now
					// check right border too
					y = fmin.value(right);
					if (y < min) {
						minSample = right;
					}

					// investigate only the interval around the minSample
					// make sure we don't get out of our interval!
					left = Math.max(left, minSample - subStep);
					right = Math.min(right, minSample + subStep);
				}

				// find minimum (resp. maximum) over interval
				double x = extrFinder.findMinimum(left, right, fmin, TOLERANCE);
				double y = fmin.value(x);

				// one of the evaluated sub-samples could be smaller
				// e.g. at the border of this interval
				if (y > min) {
					y = min;
				}

				// store min/max
				if (type == SumType.UPPERSUM) {
					y = -y;
				}
				yval[i] = y;

				// add to sum
				totalArea += y;
			}

			// calc area of rectangles
			sum.setValue(totalArea * STEP);
			if (!isDefined) {
				sum.setUndefined();
			}
			break;

		case TRAPEZOIDALSUM:
		case RECTANGLESUM:
		case LEFTSUM:

			if (f == null || !(f.isDefined() && ageo.isDefined()
					&& bgeo.isDefined() && ngeo.isDefined())) {
				sum.setUndefined();
				return;
			}

			/* Rectanglesum needs extra treatment */
			if ((type == SumType.RECTANGLESUM) && (!d.isDefined())) { // extra
																			// parameter
				sum.setUndefined();
			} // if d parameter for rectanglesum

			fun = f.getFunction();

			// lower bound
			ad = a.getDouble();
			// upper bound
			bd = b.getDouble();

			if (!onlyZoom) {
				isDefined = functionDefined(ad, bd);
			} else {
				isDefined = sum.isDefined();
			}
			ints = n.getDouble();
			if (ints < 1) {
				sum.setUndefined();
				return;
			} else if (ints > MAX_RECTANGLES) {
				N = MAX_RECTANGLES;
			} else {
				N = (int) Math.round(ints);
			}
			STEP = (bd - ad) / N;

			// calc minimum in every interval
			if (yval == null || yval.length < N + 1) { // N+1 for trapezoids
				yval = new double[N + 1]; // N+1 for trapezoids
				leftBorder = new double[N + 1]; // N+1 for trapezoids
			}

			totalArea = 0;
			int upperBound = type == SumType.TRAPEZOIDALSUM ? N + 1 : N;
			for (int i = 0; i < upperBound; i++) { // N+1 for trapezoids
				leftBorder[i] = ad + i * STEP;

				/* Extra treatment for RectangleSum */
				if (type == SumType.RECTANGLESUM) {
					double dd = d.getDouble();
					if ((dd >= 0) && (dd <= 1)) {

						// make sure we don't get an overflow
						// eg sqrt(1-1.00000000000001)
						double rightBorder = Math.max(ad, bd);
						double xVal = Math.min(rightBorder,
								leftBorder[i] + dd * STEP);

						yval[i] = fun.value(xVal); // divider
														// into
														// step-interval

					} else {
						sum.setUndefined();
						return;
					} // if divider ok
				} else {
					yval[i] = fun.value(leftBorder[i]);
				} // if

				totalArea += yval[i];
			}

			// calc area of rectangles or trapezoids
			if (type == SumType.TRAPEZOIDALSUM) {
				totalArea -= (yval[0] + yval[N]) / 2;
			} // if rectangles or trapezoids

			// for (int i=0; i < N+1 ; i++) cumSum += yval[i];
			sum.setValue(totalArea * STEP);
			break;

		case BARCHART_BERNOULLI:
			double p = p1.getDouble();
			if (p < 0 || p > 1) {
				sum.setUndefined();
				return;
			}
			N = 3;

			// special case, 2 bars

			leftBorder = new double[3];
			yval = new double[3];

			boolean cumulative = isCumulative != null
					&& ((GeoBoolean) isCumulative).getBoolean();
			yval[0] = 1 - p;
			yval[1] = cumulative ? 1 : p;
			leftBorder[0] = -0.5;
			leftBorder[1] = 0.5;
			leftBorder[2] = 1.5;
			ageo = new GeoNumeric(cons, leftBorder[0]);
			bgeo = new GeoNumeric(cons, leftBorder[1]);
			a = (GeoNumberValue) ageo;
			b = (GeoNumberValue) bgeo;

			if (cumulative) {
				sum.setValue(Double.POSITIVE_INFINITY);
			} else {
				sum.setValue(1);
			}
			sum.updateCascade();
			return;

		case HISTOGRAM:
		case HISTOGRAM_DENSITY:

			if (!list1.isDefined() || !list2.isDefined()) {
				sum.setUndefined();
				return;
			}

			N = list1.size();

			if (N < 2) {
				sum.setUndefined();
				return;
			}

			boolean useFrequency = list3 != null;

			if (useFrequency && !list3.isDefined()) {
				sum.setUndefined();
				return;
			}

			// set the density scale factor
			// default is 1; densityFactor == -1 means do not convert from
			// frequency to density
			double densityFactor;

			if (useDensityGeo == null) {
				densityFactor = 1;
			} else if (!((GeoBoolean) useDensityGeo).getBoolean()) {
				densityFactor = -1;
			} else {
				densityFactor = (density != null) ? density.getDouble() : 1;
				if (densityFactor <= 0 && densityFactor != -1) {
					sum.setUndefined();
					return;
				}
			}

			// list2 contains raw data
			// eg
			// Histogram[{1,1.5,2,4},{1.0,1.1,1.1,1.2,1.7,1.7,1.8,2.2,2.5,4.0}]
			// problem: if N-1 = list2.size() then raw data is not assumed
			// fix for now is to check if other parameters are present, then it
			// must be raw data
			if (N - 1 != list2.size() || useDensityGeo != null
					|| isCumulative != null) {

				if (yval == null || yval.length < N) {
					yval = new double[N];
					leftBorder = new double[N];
				}

				// fill in class boundaries
				for (int i = 0; i < N - 1; i++) {

					geo = list1.get(i);
					if (i == 0) {
						if (geo instanceof GeoNumberValue) {
							a = (GeoNumberValue) geo;
						} else {
							sum.setUndefined();
							return;
						}
					}
					if (geo.isGeoNumeric()) {
						leftBorder[i] = ((GeoNumeric) geo).getDouble();
					} else {
						sum.setUndefined();
						return;
					}

				}

				geo = list1.get(N - 1);
				if (geo instanceof GeoNumberValue) {
					b = (GeoNumberValue) geo;
				} else {
					sum.setUndefined();
					return;
				}
				leftBorder[N - 1] = ((GeoNumeric) geo).getDouble();

				// zero frequencies
				for (int i = 0; i < N; i++) {
					yval[i] = 0;
				}

				// work out frequencies in each class

				// TODO: finish right histogram option for 2nd case below
				double valueFrequency = 1;
				double datum;
				for (int i = 0; i < list2.size(); i++) {
					geo = list2.get(i);

					if (geo.isGeoNumeric()) {
						datum = ((GeoNumeric) geo).getDouble();
					} else {
						sum.setUndefined();
						return;
					}
					if (useFrequency) {
						geo2 = list3.get(i);
						if (geo2.isGeoNumeric()) {
							valueFrequency = ((GeoNumeric) geo2).getDouble();
						} else {
							sum.setUndefined();
							return;
						}
					}

					// if negative frequency, set undefined
					if (valueFrequency < 0) {
						sum.setUndefined();
						return;
					}

					// if datum is outside the range, set undefined
					if (datum < leftBorder[0] || datum > leftBorder[N - 1]) {
						sum.setUndefined();
						return;
					}

					if (!this.histogramRight) {
						// fudge to make the last boundary eg 10 <= x <= 20
						// all others are 10 <= x < 20
						double oldMaxBorder = leftBorder[N - 1];
						leftBorder[N - 1] += Math
								.abs(leftBorder[N - 1] / 100000000);

						// check which class this datum is in
						for (int j = 1; j < N; j++) {
							if (DoubleUtil.isGreater(leftBorder[j], datum)) {
								yval[j - 1] += valueFrequency;
								break;
							}
						}

						leftBorder[N - 1] = oldMaxBorder;

					} else {
						// fudge to make the first boundary eg 10 <= x <= 20
						// all others are 10 < x <= 20 (HistogramRight)
						double oldMinBorder = leftBorder[0];
						leftBorder[0] += Math.abs(leftBorder[0] / 100000000);

						// check which class this datum is in
						for (int j = 1; j < N; j++) {
							if (DoubleUtil.isGreaterEqual(leftBorder[j], datum)) {
								yval[j - 1] += valueFrequency;
								break;
							}
						}
						leftBorder[0] = oldMinBorder;
					}
				}

			} else { // list2 contains the heights

				// make sure heights not rescaled #2579
				densityFactor = -1;

				if (yval == null || yval.length < N) {
					yval = new double[N];
					leftBorder = new double[N];
				}

				for (int i = 0; i < N - 1; i++) {

					geo = list1.get(i);
					if (i == 0) {
						if (geo instanceof GeoNumberValue) {
							a = (GeoNumberValue) geo;
						} else {
							sum.setUndefined();
							return;
						}
					}
					if (geo.isNumberValue()) {
						leftBorder[i] = geo.evaluateDouble();
					} else {
						sum.setUndefined();
						return;
					}

					geo = list2.get(i);
					if (geo.isNumberValue()) {
						yval[i] = geo.evaluateDouble();
					} else {
						sum.setUndefined();
						return;
					}

				}

				// yval[N - 1] = yval[N - 2];

				geo = list1.get(N - 1);
				if (geo instanceof GeoNumberValue) {
					b = (GeoNumberValue) geo;
				} else {
					sum.setUndefined();
					return;
				}
				leftBorder[N - 1] = ((GeoNumeric) geo).getDouble();
			}

			// convert to cumulative frequencies if cumulative option is set
			if (isCumulative != null
					&& ((GeoBoolean) isCumulative).getBoolean()) {
				for (int i = 1; i < N; i++) {
					yval[i] += yval[i - 1];
				}

				yval[N - 1] = 0.0;
			}

			// turn frequencies into frequency densities
			// if densityFactor = -1 then do not convert frequency to
			// density
			if (densityFactor != -1) {
				for (int i = 1; i < N; i++) {
					yval[i - 1] = densityFactor * yval[i - 1]
							/ (leftBorder[i] - leftBorder[i - 1]);
				}
			}

			totalArea = 0;
			for (int i = 1; i < N; i++) {
				totalArea += (leftBorder[i] - leftBorder[i - 1]) * yval[i - 1];
			}

			// area of rectangles
			sum.setValue(totalArea);

			break;
		}
	}

	private boolean functionDefined(double ad, double bd) {
		double interval = (bd - ad) / CHECKPOINTS;
		for (double temp = ad; (interval > 0 && temp < bd)
				|| (interval < 0 && temp > bd); temp += interval) {
			double val = f.value(temp);
			if (Double.isNaN(val) || Double.isInfinite(val)) {
				return false;
			}
		}
		double val = f.value(bd);
		return Double.isFinite(val);
	}

	/**
	 * Returns true iff this is trapezoidal sum
	 * 
	 * @return true iff this is trapezoidal sums
	 */
	public boolean useTrapeziums() {
		switch (type) {
		case TRAPEZOIDALSUM:
			return true;
		default:
			return false;
		}
	}

	/**
	 * Returns true iff this is histogram
	 * 
	 * @return true iff this is histogram
	 */
	public boolean isHistogram() {
		switch (type) {
		case HISTOGRAM:
		case HISTOGRAM_DENSITY:
			return true;
		default:
			return false;
		}
	}

	/**
	 * Returns type of the sum, see TYPE_* constants of this class
	 * 
	 * @return type of the sum
	 */
	public SumType getType() {
		return type;
	}

	@Override
	public void update() {
		if (doStopUpdateCascade()) {
			return;
		}

		// do not update input random numbers without label

		// compute output from input
		compute();

		// update dependent objects
		for (int i = 0; i < getOutputLength(); i++) {
			getOutput(i).update();
		}
	}

	/*
	 * This should apply to every subclass. In case it does not, a case per case
	 * should be used. It produces a GeoNumeric, so beware GeoNumeric will be
	 * treated differently than points.
	 */

}
