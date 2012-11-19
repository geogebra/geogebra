/*
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.kernel.algos;

//import geogebra.kernel.AlgoElement;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.Function;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.roots.RealRootAdapter;
import geogebra.common.kernel.roots.RealRootUtil;
import geogebra.common.main.App;

import java.util.ArrayList;

import org.apache.commons.math.analysis.solvers.UnivariateRealSolver;
import org.apache.commons.math.analysis.solvers.UnivariateRealSolverFactory;

//import org.apache.commons.math.analysis.solvers.UnivariateRealSolverFactory;

/**
 * Command: Roots[ <function>, <left-x>, <right-x>] (TYPE 0) and Command:
 * Intersect[ <function>, <function>, <left-x>, <right-x>] (TYPE 1) (just uses
 * difference-function instead of one function)
 * 
 * Can be used elsewhere: public static final double[] findRoots(GeoFunction
 * f,double l,double r,int samples) public static final double[]
 * calcSingleRoot(GeoFunction f, double l, double r);
 * 
 * Extends AlgoGeoPointsFunction (abstract), with the label methods, which again
 * extens AlgoElement.
 * 
 * @author Hans-Petter Ulven
 * @version 2011-03-08
 */

public class AlgoRoots extends AlgoGeoPointsFunction {

	private static final int TYPE_ROOTS = 0;
	private static final int TYPE_INTERSECTIONS = 1;
	private static final int PIXELS_BETWEEN_SAMPLES = 5; // Open for empirical
															// adjustments
	private static final int MAX_SAMPLES = 400; // -"- (covers a screen up to
												// 2000 pxs if 5-pix-convention)
	private static final int MIN_SAMPLES = 50; // -"- (covers up to 50 in a 250
												// pxs interval if
												// 5-pix-convention)

	// Input-Output
	// private GeoFunctionable function; // input
	private GeoFunction f0, f1, f2, diff;
	private NumberValue left; // input
	private GeoElement geoleft;
	private NumberValue right; // input
	private GeoElement georight;

	// Vars
	private int type = TYPE_ROOTS;

	/**
	 * Computes "all" Roots of f in <l,r> TYPE_ROOTS
	 */
	public AlgoRoots(Construction cons, String[] labels, GeoFunction function,
			NumberValue left, NumberValue right) {
		super(cons, labels, !cons.isSuppressLabelsActive(), function); // Ancestor
																		// gets
																		// first
																		// function
																		// for
																		// points!
		this.f0 = function;
		this.left = left;
		this.geoleft = left.toGeoElement();
		this.right = right;
		this.georight = right.toGeoElement();

		type = TYPE_ROOTS;

		setInputOutput();

		compute();

		showOneRootInAlgebraView(); // Show at least one root point in algebra
									// view

	}// Constructor TYPE_ROOTS

	public AlgoRoots(Construction cons, GeoFunction function, NumberValue left,
			NumberValue right) {
		super(cons, function); // Ancestor gets first function for points!
		this.f0 = function;
		this.left = left;
		this.geoleft = left.toGeoElement();
		this.right = right;
		this.georight = right.toGeoElement();

		type = TYPE_ROOTS;

		setInputOutput();

		compute();
	}// Constructor TYPE_ROOTS

	/**
	 * Computes "all" Roots of f in <l,r> TYPE_INTERSECTIONS
	 */
	public AlgoRoots(Construction cons, String[] labels, GeoFunction function,
			GeoFunction function2, NumberValue left, NumberValue right) {
		super(cons, labels, !cons.isSuppressLabelsActive(), function); // Ancestor
																		// gets
																		// first
																		// function
																		// for
																		// points!
		this.f1 = function;
		this.f2 = function2;
		this.left = left;
		this.geoleft = left.toGeoElement();
		this.right = right;
		this.georight = right.toGeoElement();

		type = TYPE_INTERSECTIONS;

		setInputOutput();

		compute();

		showOneRootInAlgebraView(); // Show at least one root point in algebra
									// view

	}// Constructor TYPE_INTERSECTIONS

	@Override
	public Algos getClassName() {
		return Algos.AlgoRoots;
	}// getClassName()

	public GeoPoint[] getRootPoints() {
		return getPoints(); // Points in ancestor
	}// getRootPoints()

	@Override
	protected void setInputOutput() {
		switch (type) {
		case TYPE_ROOTS:
			input = new GeoElement[3];
			input[0] = f0.toGeoElement();
			input[1] = geoleft;
			input[2] = georight;
			break;
		case TYPE_INTERSECTIONS:
			input = new GeoElement[4];
			input[0] = f1.toGeoElement();
			input[1] = f2.toGeoElement();
			input[2] = geoleft;
			input[3] = georight;
		}// switch

		super.setOutput(getPoints()); // Points in ancestor

		noUndefinedPointsInAlgebraView(getPoints());

		setDependencies(); // done by AlgoElement
	}// setInputOutput()

	@Override
	public final void compute() {
		boolean ok = false;
		switch (type) {
		case TYPE_ROOTS:
			ok = f0.toGeoElement().isDefined() && geoleft.isDefined()
					&& georight.isDefined();
			break;
		case TYPE_INTERSECTIONS:
			ok = f1.toGeoElement().isDefined() && f2.toGeoElement().isDefined()
					&& geoleft.isDefined() && georight.isDefined();
			break;
		}// switch
		if (!ok) {
			setPoints(new double[1], 0); // debug("error in args");
		} else {
			if (type == TYPE_INTERSECTIONS) {
				diff = new GeoFunction(cons);
				diff = GeoFunction.subtract(diff, f1, f2); // Make a difference
															// geofunction for
															// intersections
				compute2(diff);
			} else {
				compute2(f0);
			}// if type
		}// if ok input
	}// compute()

	private final void compute2(GeoFunction f) {

		double l = left.getDouble();
		double r = right.getDouble();
		double[] roots = new double[0];
		int numberofroots = 0;

		/*
		 * if ( !f.toGeoElement().isDefined() || !geoleft.isDefined() ||
		 * !georight.isDefined() // || (right.getDouble()<=left.getDouble() ) )
		 * { setPoints(new double[1],0); //0 flags no root=>undefined }else {
		 */

		if (l > r) {
			double tmp = l;
			l = r;
			r = tmp;
		} // Correct user input

		// / --- Algorithm --- ///
		int n = findNumberOfSamples(l, r);
		//make sure m is at least 1 even for invisible EV
		int m = Math.max(n,1);
		try { // To catch eventual wrong indexes in arrays...
				// Adjust samples. Some research needed to find best factor in
				// if(numberofroots<m*factor...
			do { // debug("doing samples: "+m);
				roots = findRoots(f, l, r, m);

				if (roots == null) {
					numberofroots = 0;
				} else {
					numberofroots = roots.length;
				} // debug("found xvalues: "+roots);
				if (numberofroots < m / 2) {
					break;
				}
				m = m * 2;
			} while (m < MAX_SAMPLES);
			if (m > MAX_SAMPLES)
				App
						.debug("We have probably lost some roots...");
		} catch (Exception e) {
			App.debug("Exception in compute() " + e.toString());
		}// try-catch

		// }//if

		if (numberofroots == 0) {
			setPoints(new double[1], 0); // 0 flags no root=>undefined
		} else {
			setPoints(roots, roots.length);
		}// if
	}// compute()

	/**
	 * Main algorithm, public for eventual use by other commands Finds a
	 * samplesize n depending on screen coordinates Samples n intervals Collects
	 * roots in intervals where y(l)*y(r)>0
	 */
	public static final double[] findRoots(GeoFunction f, double l, double r,
			int samples) {

		double[] y = new double[samples + 1]; //
		ArrayList<Double> xlist = new ArrayList<Double>();
		double x, xval;
		double deltax = (r - l) / samples;

		for (int i = 0; i <= samples; i++) {
			x = l + i * deltax;
			y[i] = f.evaluate(x);
			// if left endpoint is root by pure luck...
			if ((Math.abs(y[i]) < Kernel.MIN_PRECISION) && (signChanged(f, x))) { // if
																					// left
																					// endpoint
																					// is
																					// root
																					// by
																					// pure
																					// luck...
				xlist.add(x);
			}// if
			if (i > 0) {
				if (((y[i - 1] < 0.0d) && (y[i] > 0.0d)) || // or just
															// y[i-1]*y[i]<0...
						((y[i - 1] > 0.0d) && (y[i] < 0.0d))) {
					xval = calcSingleRoot(f, x - deltax, x);
					if (Math.abs(f.evaluate(xval)) < Kernel.MIN_PRECISION) { // =1E-5:
																				// Quite
																				// large,
																				// but
																				// less
																				// doesn't
																				// work
																				// in
																				// Apache
																				// lib...
						xlist.add(xval);
					}// if check
				}// if possible root
			}// if both ends of interval
		}// for all endpoints
		if (xlist.size() > 0) {
			double[] res = new double[xlist.size()];
			for (int i = 0; i < xlist.size(); i++) {
				res[i] = xlist.get(i);
			}// for all x in xlist
			removeDuplicates(res); // new 08.03.11 to avoid (1,0.00000x) and
									// (1,-0.00000x) ...
			return res;
		}
		// if valid
		return null;
	}// findRoots(f,l,r)

	// / --- Private methods --- ///
	// Make all private after testing...

	/**
	 * Brent's algo Copied from AlgoRootInterval.java.
	 */
	public final static double calcSingleRoot(GeoFunction f, double left,
			double right) {
		UnivariateRealSolver rootFinder = UnivariateRealSolverFactory
				.newInstance().newBrentSolver(); // Apache lib

		if (!f.isDefined())
			return Double.NaN;

		double root = Double.NaN;
		Function fun = f.getFunction();

		try {
			// Brent's method
			root = rootFinder.solve(new RealRootAdapter(fun), left, right);
		} catch (Exception e) {
			try {
				// Let's try again by searching for a valid domain first
				double[] borders = RealRootUtil.getDefinedInterval(fun, left,
						right);
				root = rootFinder.solve(new RealRootAdapter(fun), borders[0],
						borders[1]);
			} catch (Exception ex) {
				root = Double.NaN;
			}// try-catch
		}// try-catch

		return root;
	}// calcSingleRoot(f,l,r)

	public final int findNumberOfSamples(double l, double r) {
		// Find visible area of graphic screen: xmin,xmax,ymin,ymax
		// pixels_in_visible_interval=...
		// n=pixels_in_visible_interval/PIXELS_BETWEEN_SAMPLES;

		// EuclidianView ev = app.getEuclidianView();
		double visiblemax =kernel.getViewsXMax(points[0]);
		double visiblemin = kernel.getViewsXMin(points[0]);
		double visiblepixs = app.countPixels(visiblemin, visiblemax);
		// debug("Visible pixels: "+visiblepixs);
		double pixsininterval = visiblepixs * (r - l)
				/ (visiblemax - visiblemin);
		// debug("Pixels in interval: "+pixsininterval);
		int n = (int) Math.round(Math.max(
				Math.min(pixsininterval / PIXELS_BETWEEN_SAMPLES, MAX_SAMPLES),
				MIN_SAMPLES));

		// debug("Samples: "+n);
		return n;

	}// findNumberOfSamples()

	private static final boolean signChanged(GeoFunction f, double x) {
		double delta = Kernel.MIN_PRECISION * 10; // Used in AlgoRootsPolynomial
		double left, right, lefty, righty;
		boolean signChanged;
		left = x - delta;
		right = x + delta;
		int count = 0;
		while (Math.abs(lefty = f.evaluate(left)) < delta && count++ < 100)
			left = left - delta;
		count = 0;
		while (Math.abs(righty = f.evaluate(right)) < delta && count++ < 100)
			right = right + delta;
		signChanged = lefty * righty < 0.0d;
		return signChanged;
	}// signChanged(f,x,deltax)

	// * //--- SNIP (after debugging and testing) -------------------------
	// / --- Test interface --- ///
	// Running testcases from external testscript Test_Extremum.bsh from plugin
	// scriptrunner.
	// algo=new AlgoRoots(cons)
	// listArray(double[])
	// listLabels(String[])
	// listPoints(GeoPoint[])
	//

	/* Constructor for externel test of static functions */
	public AlgoRoots(Construction cons) {
		super(cons);
	}// Test Constructor

	private static final boolean DEBUG = true;

	private final static void debug(String s) {
		if (DEBUG) {
			App.debug(s);
		}// if()
	}// debug()

	public final static void listArray(double[] a) {
		if (a != null) {
			int l = a.length;
			System.out.println("Length: " + l);
			for (int i = 0; i < l; i++) {
				System.out.println("a[" + i + "]: " + a[i]);
			}// for
		} else {
			debug("array was null!");
		}// if not null
	}// listArray(a)

	public final static void listLabels(String[] a) {
		if (a != null) {
			int l = a.length;
			System.out.println("Length: " + l);
			for (int i = 0; i < l; i++) {
				System.out.println("Label[" + i + "]: " + a[i]);
			}// for
		} else {
			debug("labels was null!");
		}// if not null
	}// listLabels(a)

	public final static void listPoints(GeoPoint[] gpts) {
		if (gpts != null) {
			int n = gpts.length;
			System.out.println("Length: " + n);
			for (int i = 0; i < n; i++) {
				System.out.println("Label: " + gpts[i].getLabel(StringTemplate.defaultTemplate) + "     pt["
						+ i + "]: (" + gpts[i].x + "," + gpts[i] + ")");
			}// for
		} else {
			debug("gpts was null!");
		}// if not null
	}// listPoints(GeoPoint[])

	public final static String testGeoPointX(GeoPoint geo, double answer) {
		double value;
		value = geo.getX();
		if (Math.abs(value - answer) < Kernel.MIN_PRECISION) {
			return " === GOOD ===";
		}
		// if near enough...
		return " ****************** WRONG *******************\n" + " ERROR: "
				+ Math.abs(value - answer) + "  for: "
				+ geo.getNameDescription() + "\n"
				+ " ********************************************\n";
	}// test(label,answer)

	// */ //--- SNIP end ---------------------------------------

	// TODO Consider locusequability

}// class AlgoRoots