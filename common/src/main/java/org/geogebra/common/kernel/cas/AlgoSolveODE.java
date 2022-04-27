package org.geogebra.common.kernel.cas;

import java.util.ArrayList;

import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.ClassicalRungeKuttaIntegrator;
import org.apache.commons.math3.ode.sampling.StepHandler;
import org.apache.commons.math3.ode.sampling.StepInterpolator;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.SegmentType;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoNumeratorDenominatorFun;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionalNVar;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoLocus;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.debug.Log;

/**
 * eg SolveODE[x/y,x(A),y(A),5,0.1]
 * 
 * @author michael
 *
 */
public class AlgoSolveODE extends AlgoElement {

	private FunctionalNVar f0; // input
	private FunctionalNVar f1;
	private GeoNumeric x;
	private GeoNumeric y;
	private GeoNumeric end;
	private GeoNumeric step; // input
	private GeoLocus locus; // output
	/** integral line points */
	ArrayList<MyPoint> al;
	private AlgoNumeratorDenominatorFun numAlgo;
	private AlgoNumeratorDenominatorFun denAlgo;
	private FunctionalNVar num;
	private FunctionalNVar den;
	/** whether expression is in the form f/g */
	boolean quotient;

	/**
	 * @param cons
	 *            cons
	 * @param label
	 *            label
	 * @param f0
	 *            numerator of function (if f1 != null), otherwise function
	 * @param f1
	 *            denominator, or null
	 * @param x
	 *            start x
	 * @param y
	 *            start y
	 * @param end
	 *            end
	 * @param step
	 *            step
	 */
	public AlgoSolveODE(Construction cons, String label, FunctionalNVar f0,
			FunctionalNVar f1, GeoNumeric x, GeoNumeric y, GeoNumeric end,
			GeoNumeric step) {
		super(cons);
		this.f0 = f0;
		this.f1 = f1;
		this.x = x;
		this.y = y;
		this.end = end;
		this.step = step;

		if (f1 == null) {
			numAlgo = new AlgoNumeratorDenominatorFun(cons, f0,
					Commands.Numerator);
			denAlgo = new AlgoNumeratorDenominatorFun(cons, f0,
					Commands.Denominator);
			cons.removeFromConstructionList(numAlgo);
			cons.removeFromConstructionList(denAlgo);

			num = (FunctionalNVar) numAlgo.getGeoElements()[0];
			den = (FunctionalNVar) denAlgo.getGeoElements()[0];
			ExpressionValue denVal = den.getFunctionExpression();
			boolean constDen = denVal == null
					|| denVal.unwrap() instanceof GeoNumberValue
					|| (denVal.unwrap() instanceof MyDouble
							&& denVal.isConstant());
			quotient = num.isDefined() && den.isDefined() && !constDen;

			if (!quotient) {
				cons.removeFromAlgorithmList(numAlgo);
				cons.removeFromAlgorithmList(denAlgo);
			}

		} else {
			num = f0;
			den = f1;
			quotient = true;
		}

		// g = new GeoList(cons);
		locus = new GeoLocus(cons);
		setInputOutput(); // for AlgoElement
		compute();
		// g.setLabel(label);
		locus.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.SolveODE;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[f1 == null ? 5 : 6];
		int i = 0;

		input[i++] = (GeoElement) f0;
		if (f1 != null) {
			input[i++] = (GeoElement) f1;
		}
		input[i++] = x;
		input[i++] = y;
		input[i++] = end;
		input[i++] = step;

		super.setOutputLength(1);
		// super.setOutput(0, g);
		super.setOutput(0, locus);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return locus
	 */
	public GeoLocus getResult() {
		// return g;
		return locus;
	}

	@Override
	public final void compute() {
		if (!((GeoElement) f0).isDefined() || !x.isDefined() || !y.isDefined()
				|| !step.isDefined() || !end.isDefined()
				|| DoubleUtil.isZero(step.getDouble())) {
			// g.setUndefined();
			locus.setUndefined();
			return;
		}

		// g.clear();
		if (al == null) {
			al = new ArrayList<>();
		} else {
			al.clear();
		}

		// FirstOrderIntegrator integrator = new
		// DormandPrince853Integrator(1.0e-8, 100.0, 1.0e-10, 1.0e-10);
		FirstOrderIntegrator integrator = new ClassicalRungeKuttaIntegrator(
				step.getDouble());
		FirstOrderDifferentialEquations ode;

		if (!quotient) {
			ode = new ODE(f0);
		} else {
			ode = new ODE2(num, den);
		}
		integrator.addStepHandler(stepHandler);

		al.add(new MyPoint(x.getDouble(), y.getDouble(), SegmentType.MOVE_TO));

		double[] yy = new double[] { y.getDouble() }; // initial state
		double[] yy2 = new double[] { x.getDouble(), y.getDouble() }; // initial
																		// state
		try {
			if (!quotient) {
				integrator.integrate(ode, x.getDouble(), yy, end.getDouble(),
						yy);
			} else {
				integrator.integrate(ode, 0.0, yy2, end.getDouble(), yy2);
			}
		} catch (RuntimeException e) {
			// catches ArithmeticException, IllegalStateException and
			// ArithmeticException
			Log.debug(e);
			locus.setDefined(false);
		} // now y contains final state at time t=16.0

		// g.setDefined(true);
		locus.setPoints(al);
		locus.setDefined(true);

	}

	private StepHandler stepHandler = new StepHandler() {

		@Override
		public void handleStep(StepInterpolator interpolator, boolean isLast)
				throws IllegalArgumentException {
			double t = interpolator.getCurrentTime();
			double[] y1 = interpolator.getInterpolatedState();
			// System.out.println(t + " " + y[0]);

			if (!quotient) {
				al.add(new MyPoint(t, y1[0], SegmentType.LINE_TO));
			} else {
				al.add(new MyPoint(y1[0], y1[1], SegmentType.LINE_TO));
			}

		}

		@Override
		public void init(double t0, double[] y0, double t) {
			Log.error("unimplemented");

		}
	};

	// integrator.addStepHandler(stepHandler);

	private static class ODE implements FirstOrderDifferentialEquations {

		FunctionalNVar f;

		public ODE(FunctionalNVar f) {
			this.f = f;
		}

		@Override
		public int getDimension() {
			return 1;
		}

		@Override
		public void computeDerivatives(double t, double[] y, double[] yDot) {

			double[] input = { t, y[0] };

			// special case for f(y)= (substitute y not x)
			// eg SolveODE[y, x(A), y(A), 5, 0.1]
			if (f instanceof GeoFunction && ((GeoFunction) f).isFunctionOfY()) {
				yDot[0] = ((GeoFunction) f).value(y[0]);
			} else {
				yDot[0] = f.evaluate(input);
			}

		}

	}

	private static class ODE2 implements FirstOrderDifferentialEquations {

		FunctionalNVar y0;
		FunctionalNVar y1;

		public ODE2(FunctionalNVar y, FunctionalNVar x) {
			this.y0 = y;
			this.y1 = x;
		}

		@Override
		public int getDimension() {
			return 2;
		}

		@Override
		public void computeDerivatives(double t, double[] y, double[] yDot) {

			double[] input = { y[0], y[1] };

			// special case for f(y)= (substitute y not x)
			// eg SolveODE[-y, x, x(A), y(A), 5, 0.1]
			if (y1 instanceof GeoFunction
					&& ((GeoFunction) y1).isFunctionOfY()) {
				yDot[0] = ((GeoFunction) y1).value(y[1]);
			} else {
				yDot[0] = y1.evaluate(input);
			}

			// special case for f(y)= (substitute y not x)
			// eg SolveODE[-x, y, x(A), y(A), 5, 0.1]
			if (y0 instanceof GeoFunction
					&& ((GeoFunction) y0).isFunctionOfY()) {
				yDot[1] = ((GeoFunction) y0).value(y[1]);
			} else {
				yDot[1] = y0.evaluate(input);
			}
		}
	}

	@Override
	public void remove() {
		if (removed) {
			return;
		}
		super.remove();

		if (f1 == null) {
			((GeoElement) f0).removeAlgorithm(numAlgo);
			((GeoElement) f0).removeAlgorithm(denAlgo);
		}
	}

}
