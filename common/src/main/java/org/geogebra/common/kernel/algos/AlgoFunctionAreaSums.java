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

import org.apache.commons.math.distribution.BinomialDistributionImpl;
import org.apache.commons.math.distribution.HypergeometricDistributionImpl;
import org.apache.commons.math.distribution.IntegerDistribution;
import org.apache.commons.math.distribution.PascalDistributionImpl;
import org.apache.commons.math.distribution.PoissonDistributionImpl;
import org.apache.commons.math.distribution.ZipfDistributionImpl;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.optimization.ExtremumFinder;
import org.geogebra.common.kernel.optimization.NegativeRealRootFunction;
import org.geogebra.common.kernel.roots.RealRootFunction;
import org.geogebra.common.util.debug.Log;

/**
 * Superclass for lower/upper sum of function f in interval [a, b] with n
 * intervals
 */
public abstract class AlgoFunctionAreaSums extends AlgoElement implements
		DrawInformationAlgo {

	// largest possible number of rectangles
	private static final int MAX_RECTANGLES = 10000;
	/**
	 * number of points used for checking that function is defined on the
	 * interval
	 **/
	double CHECKPOINTS = 100;
	// subsample every 5 pixels
	private static final int SAMPLE_PIXELS = 5;

	// find global minimum in an interval with the following heuristic:
	// 1) sample the function for some values of x in [a, b]
	// 2) get x[i] with minimal f(x[i])
	// 3) use parabolic interpolation and Brent's Method in One Dimension
	// for interval x[i-1] to x[i+1]
	// (Numerical Recipes in C++, pp.406)

	private SumType type;

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

		/** Barchart from expression **/
		BARCHART,
		/** Barchart from raw data **/
		BARCHART_RAWDATA,
		/** Barchart from (values,frequencies) **/
		BARCHART_FREQUENCY_TABLE,
		/** Barchart from (values,frequencies) with given width **/
		BARCHART_FREQUENCY_TABLE_WIDTH,

		/**
		 * Histogram from(class boundaries, raw data) with default density = 1
		 * or Histogram from(class boundaries, frequencies) no density required
		 **/
		HISTOGRAM,
		/** Histogram from(class boundaries, raw data) with given density **/
		HISTOGRAM_DENSITY,

		/** barchart of a discrete probability distribution **/
		BARCHART_BINOMIAL, BARCHART_PASCAL, BARCHART_POISSON, BARCHART_HYPERGEOMETRIC, BARCHART_BERNOULLI, BARCHART_ZIPF
	};

	// tolerance for parabolic interpolation
	private static final double TOLERANCE = 1E-7;

	private GeoFunction f; // input
	private GeoNumberValue a, b, n, width, density, p1, p2, p3; // input

	/**
	 * @return the p1
	 */
	public GeoNumberValue getP1() {
		return p1;
	}

	/**
	 * @return the p2
	 */
	public GeoNumberValue getP2() {
		return p2;
	}

	/**
	 * @return the p3
	 */
	public GeoNumberValue getP3() {
		return p3;
	}

	private NumberValue d; // input: divider for Rectangle sum, 0..1
	private GeoList list1, list2, list3; // input
	private GeoElement ageo, bgeo, ngeo, dgeo, widthGeo, densityGeo,
			useDensityGeo, isCumulative, p1geo, p2geo, p3geo;

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

	private GeoNumeric sum; // output sum

	private int N;
	private double STEP;
	private double[] yval; // y value (= min) in interval 0 <= i < N
	private double[] leftBorder; // leftBorder (x val) of interval 0 <= i < N
	// private double [] widths;

	private ExtremumFinder extrFinder;

	// maximum frequency of bar chart
	// this is used by stat dialogs when setting window dimensions
	private double freqMax;

	private boolean histogramRight;

	/**
	 * Returns maximum frequency of a bar chart or histogram
	 * 
	 * @return maximum frequency of a bar chart or histogram
	 */
	public double getFreqMax() {

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
	 * @param label
	 * @param f
	 * @param a
	 * @param b
	 * @param n
	 * @param d
	 * @param type
	 */
	public AlgoFunctionAreaSums(Construction cons, String label, GeoFunction f,
			GeoNumberValue a, GeoNumberValue b, GeoNumberValue n,
			GeoNumberValue d,
			SumType type) {

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
		dgeo = d.toGeoElement();

		sum = new GeoNumeric(cons); // output
		setInputOutput(); // for AlgoElement
		compute();
		sum.setDrawable(true);
		sum.setLabel(label);
	}

	public AlgoFunctionAreaSums(GeoFunction f, GeoNumberValue a,
			GeoNumberValue b, GeoNumberValue n, GeoNumberValue d) {
		super(f.cons, false);
		this.f = f;
		this.a = a;
		this.b = b;
		this.n = n;
		this.d = d;
		ageo = a.toGeoElement();
		bgeo = b.toGeoElement();
		ngeo = n.toGeoElement();
		dgeo = d.toGeoElement();
		N = (int) Math.round(n.getDouble());

	}

	/**
	 * Upper o lower sum
	 * 
	 * @param cons
	 * @param label
	 * @param f
	 * @param a
	 * @param b
	 * @param n
	 * @param type
	 */
	public AlgoFunctionAreaSums(Construction cons, String label, GeoFunction f,
			GeoNumberValue a, GeoNumberValue b, GeoNumberValue n,
			SumType type) {

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

	public AlgoFunctionAreaSums(GeoNumberValue a, GeoNumberValue b,
			GeoNumberValue n,
			SumType type, double[] vals, double[] borders, Construction cons1) {
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
	 * BARCHART
	 * 
	 * @param cons
	 * @param label
	 * @param a
	 * @param b
	 * @param list1
	 */
	public AlgoFunctionAreaSums(Construction cons, String label,
			GeoNumberValue a, GeoNumberValue b, GeoList list1) {

		super(cons);

		type = SumType.BARCHART;

		this.a = a;
		this.b = b;
		this.list1 = list1;
		ageo = a.toGeoElement();
		bgeo = b.toGeoElement();

		sum = new GeoNumeric(cons); // output
		setInputOutput(); // for AlgoElement
		compute();
		sum.setDrawable(true);
		sum.setLabel(label);
	}

	/**
	 * BarChart [<list of data without repetition>, <frequency of each of these
	 * data>]
	 * 
	 * @param cons
	 * @param label
	 * @param list1
	 * @param list2
	 */
	public AlgoFunctionAreaSums(Construction cons, String label, GeoList list1,
			GeoList list2) {

		this(cons, list1, list2);
		sum.setLabel(label);
	}

	public AlgoFunctionAreaSums(Construction cons, GeoList list1, GeoList list2) {

		super(cons);

		type = SumType.BARCHART_FREQUENCY_TABLE;

		this.list1 = list1;
		this.list2 = list2;

		sum = new GeoNumeric(cons); // output
		setInputOutput(); // for AlgoElement
		compute();
		sum.setDrawable(true);
	}

	/**
	 * BarChart [<list of data without repetition>, <frequency of each of these
	 * data>, <width>]
	 * 
	 * @param cons
	 * @param label
	 * @param list1
	 * @param list2
	 * @param width
	 */

	public AlgoFunctionAreaSums(Construction cons, String label, GeoList list1,
			GeoList list2, GeoNumberValue width) {
		this(cons, list1, list2, width);
		sum.setLabel(label);
	}

	public AlgoFunctionAreaSums(Construction cons, GeoList list1,
			GeoList list2, GeoNumberValue width) {

		super(cons);

		type = SumType.BARCHART_FREQUENCY_TABLE_WIDTH;

		this.list1 = list1;
		this.list2 = list2;
		this.width = width;
		widthGeo = width.toGeoElement();

		sum = new GeoNumeric(cons); // output
		setInputOutput(); // for AlgoElement
		compute();
		sum.setDrawable(true);
	}

	/**
	 * BarChart [<list of data>, <width>]
	 * 
	 * @param cons
	 * @param label
	 * @param list1
	 * @param n
	 */
	public AlgoFunctionAreaSums(Construction cons, String label, GeoList list1,
			GeoNumeric n) {

		super(cons);

		type = SumType.BARCHART_RAWDATA;

		this.list1 = list1;
		this.n = n;
		ngeo = n.toGeoElement();

		sum = new GeoNumeric(cons); // output
		setInputOutput(); // for AlgoElement
		compute();
		sum.setDrawable(true);
		sum.setLabel(label);
	}

	/**
	 * BarChart [<list of data>, <width>]
	 * 
	 * @param cons
	 * @param list1
	 * @param n
	 */
	public AlgoFunctionAreaSums(Construction cons, GeoList list1, GeoNumeric n) {

		super(cons);

		type = SumType.BARCHART_RAWDATA;

		this.list1 = list1;
		this.n = n;
		ngeo = n.toGeoElement();

		sum = new GeoNumeric(cons); // output
		setInputOutput(); // for AlgoElement
		compute();
		sum.setDrawable(true);
	}

	/**
	 * HISTOGRAM[ <list of class boundaries>, <list of heights> ]
	 * 
	 * @param cons
	 * @param label
	 * @param list1
	 * @param list2
	 * @param right
	 */
	public AlgoFunctionAreaSums(Construction cons, String label, GeoList list1,
			GeoList list2, boolean right) {
		this(cons, list1, list2, right);
		sum.setLabel(label);
	}

	/**
	 * HISTOGRAM[ <list of class boundaries>, <list of heights> ] (no label)
	 * 
	 * @param cons
	 * @param list1
	 * @param list2
	 * @param right
	 */
	public AlgoFunctionAreaSums(Construction cons, GeoList list1,
			GeoList list2, boolean right) {

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

	public AlgoFunctionAreaSums(Construction cons, double[] vals,
			double[] borders, int N) {
		super(cons, false);

		type = SumType.HISTOGRAM;
		this.leftBorder = borders;
		this.yval = vals;
		this.N = N;
	}

	/**
	 * Histogram [<list of class boundaries>, <list of raw data>, <useDensity>,
	 * <densityFactor>]
	 * 
	 * @param cons
	 * @param label
	 * @param isCumulative
	 * @param list1
	 * @param list2
	 * @param useDensity
	 * @param density
	 * @param right
	 */
	public AlgoFunctionAreaSums(Construction cons, String label,
			GeoBoolean isCumulative, GeoList list1, GeoList list2,
			GeoList list3, GeoBoolean useDensity, GeoNumeric density,
			boolean right) {

		this(cons, isCumulative, list1, list2, list3, useDensity, density,
				right);

		sum.setLabel(label);
	}

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
		if (density != null)
			densityGeo = density.toGeoElement();

		this.useDensityGeo = useDensity;

		sum = new GeoNumeric(cons); // output
		setInputOutput(); // for AlgoElement
		compute();
		if (isCumulative != null && isCumulative.getBoolean()) {
			yval[yval.length - 1] = 0.0;
		}
		sum.setDrawable(true);
	}

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
	 * Discrete distribution bar chart
	 * 
	 * @param cons
	 * @param label
	 * @param p1
	 * @param p2
	 * @param p3
	 * @param isCumulative
	 * @param type
	 */
	public AlgoFunctionAreaSums(Construction cons, String label,
			GeoNumberValue p1, GeoNumberValue p2, GeoNumberValue p3,
			GeoBoolean isCumulative, SumType type) {

		super(cons);

		this.type = type;
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
		p1geo = p1.toGeoElement();
		if (p2 != null)
			p2geo = p2.toGeoElement();
		if (p3 != null)
			p3geo = p3.toGeoElement();
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

	protected AlgoFunctionAreaSums(GeoNumberValue p1, GeoNumberValue p2,
			GeoNumberValue p3, GeoBoolean isCumulative, SumType type,
			GeoNumberValue a, GeoNumberValue b, double[] vals, double[] borders,
			int N, Construction cons) {

		super(cons, false);

		this.type = type;
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
		p1geo = p1.toGeoElement();
		if (p2 != null)
			p2geo = p2.toGeoElement();
		if (p3 != null)
			p3geo = p3.toGeoElement();
		this.isCumulative = isCumulative;
		this.a = a;
		this.b = b;
		this.yval = vals;

		this.leftBorder = borders;
		this.N = N;
	}

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
			input[0] = f;
			input[1] = ageo;
			input[2] = bgeo;
			input[3] = ngeo;
			break;
		case RECTANGLESUM: // Ulven: 09.02.11
			input = new GeoElement[5];
			input[0] = f;
			input[1] = ageo;
			input[2] = bgeo;
			input[3] = ngeo;
			input[4] = dgeo;
			break;
		case BARCHART:
			input = new GeoElement[3];
			input[0] = ageo;
			input[1] = bgeo;
			input[2] = list1;
			break;
		case BARCHART_FREQUENCY_TABLE:
			input = new GeoElement[2];
			input[0] = list1;
			input[1] = list2;
			break;
		case BARCHART_FREQUENCY_TABLE_WIDTH:
			input = new GeoElement[3];
			input[0] = list1;
			input[1] = list2;
			input[2] = widthGeo;
			break;
		case BARCHART_RAWDATA:
			input = new GeoElement[2];
			input[0] = list1;
			input[1] = ngeo;
			break;

		case HISTOGRAM:
		case HISTOGRAM_DENSITY:

			ArrayList<GeoElement> tempList = new ArrayList<GeoElement>();

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
		case BARCHART_BINOMIAL:
		case BARCHART_PASCAL:
		case BARCHART_HYPERGEOMETRIC:
		case BARCHART_POISSON:
		case BARCHART_ZIPF:
			ArrayList<GeoElement> inputList = new ArrayList<GeoElement>();
			inputList.add(p1geo);
			if (p2geo != null)
				inputList.add(p2geo);
			if (p3geo != null)
				inputList.add(p3geo);
			if (isCumulative != null)
				inputList.add(isCumulative);

			input = new GeoElement[inputList.size()];
			input = inputList.toArray(input);
			break;
		}
		setOutputLength(1);
		setOutput(0, sum);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * function
	 * 
	 * @return function
	 */
	public GeoFunction getF() {
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
	public GeoNumeric getD() {
		return (GeoNumeric) dgeo;
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

			if (f == null
					|| !(f.isDefined() && ageo.isDefined() && bgeo.isDefined() && ngeo
							.isDefined())) {
				sum.setUndefined();
				return;
			}

			RealRootFunction fun = f.getRealRootFunctionY();
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
			RealRootFunction fmin = fun;
			if (type == SumType.UPPERSUM)
				fmin = new NegativeRealRootFunction(fun); // use -f to find
															// maximum

			double totalArea = 0;
			double left,
			right,
			min;

			// calulate the min and max x-coords of what actually needs to be
			// drawn
			// do subsampling only in this region

			double visibleMin = Math.max(Math.min(ad, bd),
					kernel.getViewsXMin(sum));

			double visibleMax = Math.min(Math.max(ad, bd),
					kernel.getViewsXMax(sum));

			// subsample every 5 pixels
			double noOfSamples = kernel.getApplication().countPixels(
					visibleMin, visibleMax)
					/ SAMPLE_PIXELS;

			double subStep = Math.abs(visibleMax - visibleMin) / noOfSamples;
			boolean doSubSamples = !Kernel.isZero(subStep)
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
				// Application.debug(left + " "+ visibleMin+" "+right +
				// " "+visibleMax);
				// subsample visible bit only
				if (doSubSamples
						&& ((STEP > 0 ? left : right) < visibleMax && (STEP > 0 ? right
								: left) > visibleMin)) {
					// Application.debug("subsampling from "+left+" to "+right);
					double y, minSample = left;
					for (double x = left; x < right; x += subStep) {
						y = fmin.evaluate(x);
						if (y < min) {
							min = y;
							minSample = x;
						}
					}
					// if the minimum is on the left border then minSample ==
					// left now
					// check right border too
					y = fmin.evaluate(right);
					if (y < min)
						minSample = right;

					// investigate only the interval around the minSample
					// make sure we don't get out of our interval!
					left = Math.max(left, minSample - subStep);
					right = Math.min(right, minSample + subStep);
				}

				// find minimum (resp. maximum) over interval
				double x = extrFinder.findMinimum(left, right, fmin, TOLERANCE);
				double y = fmin.evaluate(x);

				// one of the evaluated sub-samples could be smaller
				// e.g. at the border of this interval
				if (y > min)
					y = min;

				// store min/max
				if (type == SumType.UPPERSUM)
					y = -y;
				yval[i] = y;

				// add to sum
				totalArea += y;
			}

			// calc area of rectangles
			sum.setValue(totalArea * STEP);
			if (!isDefined)
				sum.setUndefined();
			break;

		case TRAPEZOIDALSUM:
		case RECTANGLESUM:
		case LEFTSUM:

			if (f == null
					|| !(f.isDefined() && ageo.isDefined() && bgeo.isDefined() && ngeo
							.isDefined())) {
				sum.setUndefined();
				return;
			}

			/* Rectanglesum needs extra treatment */
			if ((type == SumType.RECTANGLESUM) && (!dgeo.isDefined())) { // extra
																			// parameter
				sum.setUndefined();
			}// if d parameter for rectanglesum

			fun = f.getRealRootFunctionY();

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
			if (yval == null || yval.length < N + 1) {// N+1 for trapezoids
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
						double xVal = Math.min(bd, leftBorder[i] + dd * STEP);

						yval[i] = fun.evaluate(xVal); // divider
																			// into
																			// step-interval
					} else {
						sum.setUndefined();
						return;
					}// if divider ok
				} else {
					yval[i] = fun.evaluate(leftBorder[i]);
				}// if

				totalArea += yval[i];
			}

			// calc area of rectangles or trapezoids
			if (type == SumType.TRAPEZOIDALSUM) {
				totalArea -= (yval[0] + yval[N]) / 2;
			}// if rectangles or trapezoids

			// for (int i=0; i < N+1 ; i++) cumSum += yval[i];
			sum.setValue(totalArea * STEP);
			break;

		case BARCHART:
			if (!(ageo.isDefined() && bgeo.isDefined() && list1.isDefined())) {
				sum.setUndefined();
				return;
			}

			N = list1.size();

			ad = a.getDouble();
			bd = b.getDouble();

			ints = list1.size();
			if (ints < 1) {
				sum.setUndefined();
				return;
			} else if (ints > MAX_RECTANGLES) {
				N = MAX_RECTANGLES;
			} else {
				N = (int) Math.round(ints);
			}
			STEP = (bd - ad) / N;

			if (yval == null || yval.length < N) {
				yval = new double[N];
				leftBorder = new double[N];
			}

			totalArea = 0;

			for (int i = 0; i < N; i++) {
				leftBorder[i] = ad + i * STEP;

				geo = list1.get(i);
				if (geo.isGeoNumeric())
					yval[i] = ((GeoNumeric) geo).getDouble();
				else
					yval[i] = 0;

				totalArea += yval[i];
			}

			// calc area of rectangles
			sum.setValue(totalArea * STEP);
			if (!isDefined) {
				sum.setUndefined();
			}

			break;
		case BARCHART_RAWDATA:
			// BarChart[{1,1,2,3,3,3,4,5,5,5,5,5,5,5,6,8,9,10,11,12},3]
			if (!list1.isDefined() || !ngeo.isDefined()) {
				sum.setUndefined();
				return;
			}

			double mini = Double.POSITIVE_INFINITY;
			double maxi = Double.NEGATIVE_INFINITY;
			int minIndex = -1,
			maxIndex = -1;

			double step = n.getDouble();

			int rawDataSize = list1.size();

			if (step < 0 || Kernel.isZero(step) || rawDataSize < 2) {
				sum.setUndefined();
				return;
			}

			// find max and min
			for (int i = 0; i < rawDataSize; i++) {
				geo = list1.get(i);
				if (!geo.isGeoNumeric()) {
					sum.setUndefined();
					return;
				}
				double val = ((GeoNumeric) geo).getDouble();

				if (val > maxi) {
					maxi = val;
					maxIndex = i;
				}
				if (val < mini) {
					mini = val;
					minIndex = i;
				}
			}

			if (maxi == mini || maxIndex == -1 || minIndex == -1) {
				sum.setUndefined();
				return;
			}

			double totalWidth = maxi - mini;

			double noOfBars = totalWidth / n.getDouble();

			double gap = 0;

			/*
			 * if (kernel.isInteger(noOfBars)) { N = (int)noOfBars + 1; a =
			 * (NumberValue)list1.get(minIndex); b =
			 * (NumberValue)list1.get(maxIndex); } else
			 */
			{
				N = (int) noOfBars + 2;
				gap = ((N - 1) * step - totalWidth) / 2.0;
				a = (new GeoNumeric(cons, mini - gap));
				b = (new GeoNumeric(cons, maxi + gap));
				// Application.debug("gap = "+gap);
			}

			// Application.debug("N = "+N+" maxi = "+maxi+" mini = "+mini);

			if (yval == null || yval.length < N) {
				yval = new double[N];
				leftBorder = new double[N];
			}

			// fill in class boundaries
			// double width = (maxi-mini)/(double)(N-2);
			for (int i = 0; i < N; i++) {
				leftBorder[i] = mini - gap + step * i;
			}

			// zero frequencies
			for (int i = 0; i < N; i++)
				yval[i] = 0;

			// work out frequencies in each class
			double datum,
			valueFrequency = 1;

			for (int i = 0; i < list1.size(); i++) {
				geo = list1.get(i);
				if (geo.isGeoNumeric())
					datum = ((GeoNumeric) geo).getDouble();
				else {
					sum.setUndefined();
					return;
				}

				// if datum is outside the range, set undefined
				// if (datum < leftBorder[0] || datum > leftBorder[N-1] ) {
				// sum.setUndefined(); return; }

				// fudge to make the last boundary eg 10 <= x <= 20
				// all others are 10 <= x < 20
				double oldMaxBorder = leftBorder[N - 1];
				leftBorder[N - 1] += Math.abs(leftBorder[N - 1] / 100000000);

				// check which class this datum is in
				for (int j = 1; j < N; j++) {
					// System.out.println("checking "+leftBorder[j]);
					if (datum < leftBorder[j]) {
						// System.out.println(datum+" "+j);
						yval[j - 1]++;
						break;
					}
				}

				leftBorder[N - 1] = oldMaxBorder;

				// area of rectangles
				sum.setValue(list1.size() * step);

			}

			break;

		case BARCHART_FREQUENCY_TABLE:
		case BARCHART_BINOMIAL:
		case BARCHART_POISSON:
		case BARCHART_HYPERGEOMETRIC:
		case BARCHART_PASCAL:
		case BARCHART_ZIPF:

			if (type != SumType.BARCHART_FREQUENCY_TABLE) {
				if (!prepareDistributionLists()) {
					sum.setUndefined();
					return;
				}
			}

			// BarChart[{11,12,13,14,15},{1,5,0,13,4}]
			if (!list1.isDefined() || !list2.isDefined()) {
				sum.setUndefined();
				return;
			}

			N = list1.size() + 1;

			// if (yval == null || yval.length < N) {
			yval = new double[N];
			leftBorder = new double[N];
			// }

			if (N == 2) {
				// special case, 1 bar

				yval = new double[2];
				leftBorder = new double[2];
				yval[0] = ((GeoNumeric) (list2.get(0))).getDouble();

				leftBorder[0] = ((GeoNumeric) (list1.get(0))).getDouble() - 0.5;
				leftBorder[1] = leftBorder[0] + 1;
				ageo = new GeoNumeric(cons, leftBorder[0]);
				bgeo = new GeoNumeric(cons, leftBorder[1]);
				a = (GeoNumberValue) ageo;
				b = (GeoNumberValue) bgeo;

				sum.setValue(yval[0]);

				return;
			}

			if (list2.size() + 1 != N || N < 3) {
				sum.setUndefined();
				return;
			}

			double start = ((GeoNumeric) (list1.get(0))).getDouble();
			double end = ((GeoNumeric) (list1.get(N - 2))).getDouble();
			step = ((GeoNumeric) (list1.get(1))).getDouble() - start;

			// Application.debug("N = "+N+" start = "+start+" end = "+end+" width = "+width);

			if (!Kernel.isEqual(end - start, step * (N - 2)) // check first list
																// is
																// (consistent)
																// with being AP
					|| step <= 0) {
				sum.setUndefined();
				return;
			}

			ageo = new GeoNumeric(cons, start - step / 2);
			bgeo = new GeoNumeric(cons, end + step / 2);
			a = (GeoNumberValue) ageo;
			b = (GeoNumberValue) bgeo;

			// fill in class boundaries

			for (int i = 0; i < N; i++) {
				leftBorder[i] = start - step / 2 + step * i;
			}

			double area = 0;

			// fill in frequencies
			for (int i = 0; i < N - 1; i++) {
				geo = list2.get(i);
				if (!geo.isGeoNumeric()) {
					sum.setUndefined();
					return;
				}
				yval[i] = ((GeoNumeric) (list2.get(i))).getDouble();

				area += yval[i] * step;
			}

			// area of rectangles = total frequency
			if (type == SumType.BARCHART_FREQUENCY_TABLE) {
				sum.setValue(area);
			} else {
				if (isCumulative != null
						&& ((GeoBoolean) isCumulative).getBoolean()) {
					sum.setValue(Double.POSITIVE_INFINITY);
				} else
					sum.setValue(1);
				sum.updateCascade();
			}

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

			boolean cumulative = (isCumulative != null
					&& ((GeoBoolean) isCumulative).getBoolean());
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

		case BARCHART_FREQUENCY_TABLE_WIDTH:
			// BarChart[{1,2,3,4,5},{1,5,0,13,4}, 0.5]
			if (!list1.isDefined() || !list2.isDefined()) {
				sum.setUndefined();
				return;
			}

			N = list1.size() + 1;

			int NN = 2 * N - 1;

			if (list2.size() + 1 != N || N < 2) {
				sum.setUndefined();
				return;
			}

			start = ((GeoNumeric) (list1.get(0))).getDouble();
			end = ((GeoNumeric) (list1.get(N - 2))).getDouble();
			if (N == 2) {
				// special case, one bar
				step = 1;
			} else {
				step = ((GeoNumeric) (list1.get(1))).getDouble() - start;
			}
			double colWidth = width.getDouble();

			// Application.debug("N = "+N+" start = "+start+" end = "+end+" colWidth = "+colWidth);

			if (!Kernel.isEqual(end - start, step * (N - 2)) // check first list
																// is
																// (consistent)
																// with being AP
					|| step <= 0) {
				sum.setUndefined();
				return;
			}

			ageo = new GeoNumeric(cons, start - colWidth / 2);
			bgeo = new GeoNumeric(cons, end + colWidth / 2);
			a = (GeoNumberValue) ageo;
			b = (GeoNumberValue) bgeo;

			if (yval == null || yval.length < NN - 1) {
				yval = new double[NN - 1];
				leftBorder = new double[NN - 1];
			}

			// fill in class boundaries

			for (int i = 0; i < NN - 1; i += 2) {
				leftBorder[i] = start + step * (i / 2.0) - colWidth / 2.0;
				leftBorder[i + 1] = start + step * (i / 2.0) + colWidth / 2.0;
			}

			area = 0;

			// fill in frequencies
			for (int i = 0; i < NN - 1; i++) {
				if (MyDouble.isOdd(i)) {
					// dummy columns, zero height
					yval[i] = 0;
				} else {
					geo = list2.get(i / 2);
					if (!geo.isGeoNumeric()) {
						sum.setUndefined();
						return;
					}
					yval[i] = ((GeoNumeric) (list2.get(i / 2))).getDouble();

					area += yval[i] * colWidth;
				}
			}

			// area of rectangles = total frequency
			sum.setValue(area);

			N = NN - 1;

			break;

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
						if (geo instanceof GeoNumberValue)
							a = (GeoNumberValue) geo;
						else {
							sum.setUndefined();
							return;
						}
					}
					if (geo.isGeoNumeric())
						leftBorder[i] = ((GeoNumeric) geo).getDouble();
					else {
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
				for (int i = 0; i < N; i++)
					yval[i] = 0;

				// work out frequencies in each class

				// TODO: finish right histogram option for 2nd case below
				valueFrequency = 1;
				for (int i = 0; i < list2.size(); i++) {
					geo = list2.get(i);

					if (geo.isGeoNumeric())
						datum = ((GeoNumeric) geo).getDouble();
					else {
						sum.setUndefined();
						return;
					}
					if (useFrequency) {
						geo2 = list3.get(i);
						if (geo2.isGeoNumeric())
							valueFrequency = ((GeoNumeric) geo2).getDouble();
						else {
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
							if (Kernel.isGreater(leftBorder[j], datum)) {
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
							if (Kernel.isGreaterEqual(leftBorder[j], datum)) {
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
						if (geo instanceof GeoNumberValue)
							a = (GeoNumberValue) geo;
						else {
							sum.setUndefined();
							return;
						}
					}
					if (geo instanceof NumberValue)
						leftBorder[i] = ((NumberValue) geo).getDouble();
					else {
						sum.setUndefined();
						return;
					}

					geo = list2.get(i);
					if (geo instanceof NumberValue) {
						yval[i] = ((NumberValue) geo).getDouble();
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
			double val = f.evaluate(temp);
			if (Double.isNaN(val) || Double.isInfinite(val)) {
				return false;
			}
		}
		double val = f.evaluate(bd);
		if (Double.isNaN(val) || Double.isInfinite(val))
			return false;
		return true;
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

	/**
	 * Prepares list1 and list2 for use with probability distribution bar charts
	 */
	private boolean prepareDistributionLists() {
		IntegerDistribution dist = null;
		int first = 0, last = 0;
		try {
			// get the distribution and the first, last list values for given
			// distribution type
			switch (type) {
			case BARCHART_BINOMIAL:
				if (!(p1geo.isDefined() && p2geo.isDefined()))
					return false;
				int n = (int) Math.round(p1.getDouble());
				double p = p2.getDouble();
				dist = new BinomialDistributionImpl(n, p);
				first = 0;
				last = n;
				break;

			case BARCHART_PASCAL:
				if (!(p1geo.isDefined() && p2geo.isDefined()))
					return false;
				n = (int) Math.round(p1.getDouble());
				p = p2.getDouble();
				dist = new PascalDistributionImpl(n, p);

				first = 0;
				last = (int) Math.max(1, (kernel).getXmax() + 1);
				break;
			case BARCHART_ZIPF:
				if (!(p1geo.isDefined() && p2geo.isDefined()))
					return false;
				n = (int) Math.round(p1.getDouble());
				p = p2.getDouble();
				dist = new ZipfDistributionImpl(n, p);

				first = 0;
				last = n;
				break;
			case BARCHART_POISSON:
				if (!p1geo.isDefined())
					return false;
				double lambda = p1.getDouble();
				dist = new PoissonDistributionImpl(lambda);
				first = 0;
				last = (int) Math.max(1, kernel.getXmax() + 1);
				break;

			case BARCHART_HYPERGEOMETRIC:
				if (!(p1geo.isDefined() && p2geo.isDefined() && p3geo
						.isDefined()))
					return false;
				int pop = (int) p1.getDouble();
				int successes = (int) p2.getDouble();
				int sample = (int) p3.getDouble();
				dist = new HypergeometricDistributionImpl(pop, successes,
						sample);
				first = Math.max(0, successes + sample - pop);
				last = Math.min(successes, sample);
				break;
			}

			// load class list and probability list
			loadDistributionLists(first, last, dist);
		}

		catch (Exception e) {
			Log.debug(e.getMessage());
			return false;
		}

		return true;
	}

	/**
	 * Utility method, creates and loads list1 and list2 with classes and
	 * probabilities for the probability distribution bar charts
	 */
	private void loadDistributionLists(int first, int last,
			IntegerDistribution dist) throws Exception {
		boolean oldSuppress = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);
		if (list1 != null)
			list1.remove();
		list1 = new GeoList(cons);

		if (list2 != null)
			list2.remove();
		list2 = new GeoList(cons);

		double prob;
		double cumProb = 0;

		for (int i = first; i <= last; i++) {
			list1.add(new GeoNumeric(cons, i));
			prob = dist.probability(i);
			cumProb += prob;
			if (isCumulative != null
					&& ((GeoBoolean) isCumulative).getBoolean())
				list2.add(new GeoNumeric(cons, cumProb));
			else
				list2.add(new GeoNumeric(cons, prob));
		}
		cons.setSuppressLabelCreation(oldSuppress);
	}

	@Override
	public void update() {
		if (doStopUpdateCascade()) {
			return;
		}

		// do not update input random numbers without label

		// counter++;
		// startTime = System.currentTimeMillis();

		// compute output from input
		compute();

		// endTime = System.currentTimeMillis();
		// computeTime += (endTime - startTime);
		// startTime = System.currentTimeMillis();

		// update dependent objects
		for (int i = 0; i < getOutputLength(); i++) {
			getOutput(i).update();
		}

		// endTime = System.currentTimeMillis();
		// updateTime += (endTime - startTime );
	}

	/*
	 * This should apply to every subclass. In case it does not, a case per case
	 * should be used. It produces a GeoNumeric, so beware GeoNumeric will be
	 * treated differently than points.
	 */

	
}
