package org.geogebra.common.kernel.cas;

import java.util.ArrayList;

import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.ClassicalRungeKuttaIntegrator;
import org.apache.commons.math3.ode.sampling.StepHandler;
import org.apache.commons.math3.ode.sampling.StepInterpolator;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.SegmentType;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.kernel.geos.GeoLocus;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.util.debug.Log;

/**
 * Algorithm for second order ODEs
 */
public class AlgoSolveODE2 extends AlgoElement {

	private GeoFunction b, c, f; // input
	private GeoNumeric x, y, yDot, end, step; // input
	// private GeoList g; // output
	private GeoLocus locus; // output
	/** points of the locus */
	ArrayList<MyPoint> al;

	/**
	 * SolveODE[ <b(x)>, <c(x)>, <f(x)>, <Start x>, <Start y>, <Start y'>, <End
	 * x>, <Step>]
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            label for output
	 * @param b
	 *            b
	 * @param c
	 *            c
	 * @param f
	 *            function
	 * @param x
	 *            start x
	 * @param y
	 *            start y
	 * @param yDot
	 *            start y'
	 * @param end
	 *            end parameter
	 * @param step
	 *            step
	 */
	public AlgoSolveODE2(Construction cons, String label, GeoFunctionable b,
			GeoFunctionable c, GeoFunctionable f, GeoNumeric x, GeoNumeric y,
			GeoNumeric yDot, GeoNumeric end, GeoNumeric step) {
		super(cons);
		this.b = b.getGeoFunction();
		this.c = c.getGeoFunction();
		this.f = f.getGeoFunction();
		this.x = x;
		this.y = y;
		this.yDot = yDot;
		this.end = end;
		this.step = step;

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
		input = new GeoElement[8];

		input[0] = b;
		input[1] = c;
		input[2] = f;
		input[3] = x;
		input[4] = y;
		input[5] = yDot;
		input[6] = end;
		input[7] = step;

		super.setOutputLength(1);
		super.setOutput(0, locus);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return resulting locus
	 */
	public GeoLocus getResult() {
		return locus;
	}

	@Override
	public final void compute() {
		if (!b.isDefined() || !c.isDefined() || !f.isDefined() || !x.isDefined()
				|| !y.isDefined() || !yDot.isDefined() || !step.isDefined()
				|| !end.isDefined() || Kernel.isZero(step.getDouble())) {
			locus.setUndefined();
			return;
		}

		// g.clear();
		if (al == null) {
			al = new ArrayList<MyPoint>();
		} else {
			al.clear();
		}

		FirstOrderIntegrator integrator = new ClassicalRungeKuttaIntegrator(
				step.getDouble());
		FirstOrderDifferentialEquations ode;

		ode = new ODE2(b, c, f);
		integrator.addStepHandler(stepHandler);

		// boolean oldState = cons.isSuppressLabelsActive();
		// cons.setSuppressLabelCreation(true);
		// g.add(new GeoPoint(cons, null, x.getDouble(), y.getDouble(), 1.0));
		al.add(new MyPoint(x.getDouble(), y.getDouble(), SegmentType.MOVE_TO));
		// cons.setSuppressLabelCreation(oldState);

		double[] yy2 = new double[] { y.getDouble(), yDot.getDouble() }; // initial
																			// state
		try {
			integrator.integrate(ode, x.getDouble(), yy2, end.getDouble(), yy2);
		} catch (RuntimeException e) {
			// catches ArithmeticException, IllegalStateException and
			// ArithmeticException
			e.printStackTrace();
		}

		locus.setPoints(al);

		locus.setDefined(true);
	}

	private StepHandler stepHandler = new StepHandler() {

		private Construction cons1 = kernel.getConstruction();

		public void init(double t0, double[] y0, double t) {
			Log.error("unimplemented");

		}

		@Override
		public void handleStep(StepInterpolator interpolator, boolean isLast) {
			double t = interpolator.getCurrentTime();
			double[] y1 = interpolator.getInterpolatedState();
			// System.out.println(t + " " + y[0]+ " "+y[1]);

			boolean oldState = cons1.isSuppressLabelsActive();
			cons1.setSuppressLabelCreation(true);

			// g.add(new GeoPoint(cons, null, t, y[0], 1.0));
			al.add(new MyPoint(t, y1[0], SegmentType.LINE_TO));

			cons1.setSuppressLabelCreation(oldState);
		}
	};

	// integrator.addStepHandler(stepHandler);

	private static class ODE2 implements FirstOrderDifferentialEquations {

		GeoFunction b, c, f;

		public ODE2(GeoFunction b, GeoFunction c, GeoFunction f) {
			this.b = b;
			this.c = c;
			this.f = f;
		}

		@Override
		public int getDimension() {
			return 2;
		}

		/*
		 * Transform 2nd order into 2 linked first order y0'' + b y0' + c y0 =
		 * f(x) substitute y0' = y1 (1) y1' + b y1 + c y = f(x) y1' = f(x) - b
		 * y1 - c y0 (2)
		 */

		@Override
		public void computeDerivatives(double t, double[] y, double[] yDot) {

			yDot[0] = y[1]; // (1)
			yDot[1] = f.value(t) - b.value(t) * y[1] - c.value(t) * y[0]; // (2)
		}
	}

}
