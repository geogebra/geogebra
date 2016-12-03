package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.implicit.GeoImplicitCurve;
import org.geogebra.common.main.MyError;
import org.geogebra.common.util.debug.Log;

/**
 * Contour lines of a given function
 */
public class AlgoContourPlot extends AlgoElement {

	private GeoFunctionNVar func; // input expression
	private double xmin, xmax, ymin, ymax; // the definition domain where the
											// contour plot is defined
	private GeoNumeric contourStep;
	private GeoList list; // output
	private Equation equ;
	private ExpressionNode en;
	private GeoImplicitCurve implicitPoly;
	private double min, max, step, xstep, ystep;
	private int divisionPoints;
	private double calcmin, calcmax, calcxmin, calcxmax, calcymin, calcymax,
			minadded, maxadded;
	private boolean fixed;
	private static final int minContours = 7;
	private static final int maxContours = 25;

	/**
	 * Creates a new algorithm to create a list of implicit functions that form
	 * the contour plot of a function
	 * 
	 * @param c
	 *            construction
	 * @param label
	 *            label
	 * @param func
	 *            function
	 * @param xmin
	 *            lower bound of x
	 * @param xmax
	 *            upper bound of x
	 * @param ymin
	 *            lower bound of y
	 * @param ymax
	 *            upper bound of y
	 */
	public AlgoContourPlot(Construction c, String label, GeoFunctionNVar func,
			double xmin, double xmax, double ymin, double ymax) {
		super(c);
		c.registerEuclidianViewCE(this);
		step = 0;
		this.xmin = xmin;
		this.xmax = xmax;
		this.ymin = ymin;
		this.ymax = ymax;
		this.func = func;
		this.divisionPoints = 5;
		this.fixed = false;
		list = new GeoList(cons);
		setInputOutput();
		compute();
		list.setLabel(label);
	}

	/**
	 * Creates a new algorithm to create a list of implicit functions that form
	 * the contour plot of a function
	 * 
	 * @param c
	 *            construction
	 * @param label
	 *            label
	 * @param func
	 *            function
	 * @param xmin
	 *            lower bound of x
	 * @param xmax
	 *            upper bound of x
	 * @param ymin
	 *            lower bound of y
	 * @param ymax
	 *            upper bound of y
	 * @param contourStep
	 *            the value of the contour line height multiplier
	 */
	public AlgoContourPlot(Construction c, String label, GeoFunctionNVar func,
			double xmin, double xmax, double ymin, double ymax,
			double contourStep) {
		super(c);
		c.registerEuclidianViewCE(this);
		step = contourStep;
		this.xmin = xmin;
		this.xmax = xmax;
		this.ymin = ymin;
		this.ymax = ymax;
		this.func = func;
		this.divisionPoints = 5;
		this.fixed = true;
		list = new GeoList(cons);
		setInputOutput();
		compute();
		list.setLabel(label);
	}

	@Override
	protected void setInputOutput() {
		list.setTypeStringForXML("implicitpoly");
		contourStep = new GeoNumeric(cons, step);
		if (this.fixed) {
			input = new GeoElement[2];
			input[1] = contourStep;
		} else {
			input = new GeoElement[1];
		}
		input[0] = func;
		setOutputLength(1);
		setOutput(0, list);
		setDependencies(); // done by AlgoElement
	}

	private void addToList(GeoList list1, double value) {
		equ = new Equation(kernel, en, new MyDouble(kernel, value));
		equ.initEquation();
		implicitPoly.fromEquation(equ, null);
		list1.add(new GeoImplicitCurve(implicitPoly));
	}

	private double checkPolyValue(int i, int j) {
		double x = xmin + xstep * i;
		double y = ymin + ystep * j;
		return implicitPoly.evaluateImplicitCurve(x, y);
	}

	private int calculateBoundary(int order) {
		double val;
		int newContours = 0;
		for (int i = order - 1; i < divisionPoints + order - 1; i++) {
			val = checkPolyValue(i, -order);
			if (val < min) {
				calcmin = val;
			}
			if (val > max) {
				calcmax = val;
			}
			val = checkPolyValue(i, divisionPoints + order - 1);
			if (val < min) {
				calcmin = val;
			}
			if (val > max) {
				calcmax = val;
			}
			val = checkPolyValue(-order, i);
			if (val < min) {
				calcmin = val;
			}
			if (val > max) {
				calcmax = val;
			}
			val = checkPolyValue(divisionPoints + order - 1, i);
			if (val < min) {
				calcmin = val;
			}
			if (val > max) {
				calcmax = val;
			}

		}
		// add the 4 edges
		val = checkPolyValue(-order, -order);
		if (val < min) {
			calcmin = val;
		}
		if (val > max) {
			calcmax = val;
		}
		val = checkPolyValue(-order, divisionPoints + order);
		if (val < min) {
			calcmin = val;
		}
		if (val > max) {
			calcmax = val;
		}
		val = checkPolyValue(-order, divisionPoints + order);
		if (val < min) {
			calcmin = val;
		}
		if (val > max) {
			calcmax = val;
		}
		val = checkPolyValue(divisionPoints + order, divisionPoints + order);
		if (val < min) {
			calcmin = val;
		}
		if (val > max) {
			calcmax = val;
		}
		newContours += minadded > calcmin ? Math.ceil(Math.abs(minadded
				- calcmin)
				/ step) : 0;
		newContours += maxadded < calcmax ? Math.ceil(Math.abs(calcmax
				- maxadded)
				/ step) : 0;
		calcxmin -= xstep;
		calcxmax += xstep;
		calcymin -= ystep;
		calcymax += ystep;
		return newContours;
	}

	private void addAdditionalElements(GeoList list1) {
		calcmin = min;
		calcmax = max;
		// add boundaries
		calculateBoundary(1);
		calculateBoundary(2);
		for (double i = minadded - step; i > calcmin - step; i -= step) {
			addToList(list1, i);
			minadded = i;
		}
		for (double i = maxadded + step; i < calcmax + step; i += step) {
			addToList(list1, i);
			maxadded = i;
		}
	}

	@Override
	public void compute() {
		calcxmin = xmin;
		calcxmax = xmax;
		calcymin = ymin;
		calcymax = ymax;
		min = Double.MAX_VALUE;
		max = -Double.MAX_VALUE;
		implicitPoly = new GeoImplicitCurve(cons);
		implicitPoly.setDefined();
		FunctionNVar f = func.getFunction();
		FunctionVariable[] fvars = f.getFunctionVariables();
		xstep = (xmax - xmin) / (divisionPoints - 1.0);
		ystep = (ymax - ymin) / (divisionPoints - 1.0);
		if (Kernel.isEqual(xstep, 0) || Kernel.isEqual(ystep, 0)) {
			list.setUndefined();
			return;
		}
		if (fvars.length != 2) {
			implicitPoly.setUndefined();
			return;
		}
		try {
			en = f.getExpression().getCopy(kernel);
			FunctionVariable xVar = new FunctionVariable(kernel, "x");
			FunctionVariable yVar = new FunctionVariable(kernel, "y");
			en.replace(fvars[0], xVar);
			en.replace(fvars[1], yVar);
			equ = new Equation(kernel, en, new MyDouble(kernel));
			implicitPoly.fromEquation(equ, null);
			for (int i = 0; i < divisionPoints; i++) {
				for (int j = 0; j < divisionPoints; j++) {
					double val = checkPolyValue(i, j);
					if (val < min) {
						min = val;
					}
					if (val > max) {
						max = val;
					}
				}
			}
			if (Kernel.isEqual(max, min)) {
				list.setUndefined();
				return;
			}
			double freeTerm = 0;
			if (step == 0 && !fixed) {
				freeTerm = implicitPoly.evaluateImplicitCurve(0, 0);
				step = Math.abs((max - min) / 10.0);
				contourStep.setValue(step);
			}

			if ((min <= freeTerm) && (max >= freeTerm)) {
				for (double i = freeTerm; i > min - step; i -= step) {
					addToList(list, i);
					minadded = i;
				}
				for (double i = freeTerm + step; i < max + step; i += step) {
					addToList(list, i);
					maxadded = i;
				}
			} else {
				minadded = step * Math.floor((min - freeTerm) / step);
				for (double i = minadded; i < max + step; i += step) {
					addToList(list, i);
					maxadded = i;
				}
			}
			addAdditionalElements(list);
		} catch (MyError e) {
			Log.debug(e.getMessage());
			implicitPoly.setUndefined();
			list.add(new GeoImplicitCurve(implicitPoly));
		}
	}

	private boolean movedOut() {
		return xmin < calcxmin || xmax > calcxmax || ymin < calcymin
				|| ymax > calcymax;
	}

	// TODO implement isOnScreen for implicit curves
	private int getVisibleContourCount() {
		int count = 0;
		for (int i = 0; i < list.size(); i++) {
			/* if (((GeoImplicitCurve) (list.get(i))).isOnScreen()) { */
				count++;
			// }
		}
		return count;
	}

	@Override
	public void update() {
		xmin = cons.getApplication().getActiveEuclidianView().getXmin();
		xmax = cons.getApplication().getActiveEuclidianView().getXmax();
		ymin = cons.getApplication().getActiveEuclidianView().getYmin();
		ymax = cons.getApplication().getActiveEuclidianView().getYmax();
		int visible = getVisibleContourCount();
		if (movedOut()) {
			list.clear();
			compute();
		}
		if (visible < minContours && !fixed) {
			step = step / 2;
			contourStep.setValue(step);
			list.clear();
			compute();
		}
		if (visible > maxContours && !fixed) {
			step = step * 2;
			contourStep.setValue(step);
			list.clear();
			compute();
		}
		getOutput(0).update();
	}

	@Override
	public GetCommand getClassName() {
		return Commands.ContourPlot;
	}

}
