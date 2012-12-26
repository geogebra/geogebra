package geogebra.common.kernel.cas;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.MyPoint;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoFunctionable;
import geogebra.common.kernel.geos.GeoLocus;
import geogebra.common.kernel.geos.GeoNumeric;

import java.util.ArrayList;

import org.apache.commons.math.ode.DerivativeException;
import org.apache.commons.math.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math.ode.FirstOrderIntegrator;
import org.apache.commons.math.ode.IntegratorException;
import org.apache.commons.math.ode.nonstiff.ClassicalRungeKuttaIntegrator;
import org.apache.commons.math.ode.sampling.StepHandler;
import org.apache.commons.math.ode.sampling.StepInterpolator;

/**
 * Algorithm for second order ODEs
 */
public class AlgoSolveODE2 extends AlgoElement {

	private GeoFunction b, c, f; // input
	private GeoNumeric x, y, yDot, end, step; // input
	// private GeoList g; // output
	private GeoLocus locus; // output
	/** points of the locus*/
	ArrayList<MyPoint> al;

	/**
	 * SolveODE[ <b(x)>, <c(x)>, <f(x)>, <Start x>, <Start y>, <Start y'>, <End x>, <Step>] 
	 * @param cons construction
	 * @param label label for output
	 * @param b b 
	 * @param c c
	 * @param f function
	 * @param x start x
	 * @param y start y
	 * @param yDot start y'
	 * @param end end parameter
	 * @param step step
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
		if (!b.isDefined() || !c.isDefined() || !f.isDefined()
				|| !x.isDefined() || !y.isDefined() || !yDot.isDefined()
				|| !step.isDefined() || !end.isDefined()
				|| Kernel.isZero(step.getDouble())) {
			locus.setUndefined();
			return;
		}

		// g.clear();
		if (al == null)
			al = new ArrayList<MyPoint>();
		else
			al.clear();

		FirstOrderIntegrator integrator = new ClassicalRungeKuttaIntegrator(
				step.getDouble());
		FirstOrderDifferentialEquations ode;

		ode = new ODE2(b, c, f);
		integrator.addStepHandler(stepHandler);

		// boolean oldState = cons.isSuppressLabelsActive();
		// cons.setSuppressLabelCreation(true);
		// g.add(new GeoPoint(cons, null, x.getDouble(), y.getDouble(), 1.0));
		al.add(new MyPoint(x.getDouble(), y.getDouble(), false));
		// cons.setSuppressLabelCreation(oldState);

		double[] yy2 = new double[] { y.getDouble(), yDot.getDouble() }; // initial
																			// state
		try {
			integrator.integrate(ode, x.getDouble(), yy2, end.getDouble(), yy2);
		} catch (DerivativeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IntegratorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		locus.setPoints(al);

		locus.setDefined(true);
	}

	@Override
	final public String toString(StringTemplate tpl) {
		return getCommandDescription(tpl);
	}

	private StepHandler stepHandler = new StepHandler() {
		public void reset() {
			//do nothing
		}

		private Construction cons1 = kernel.getConstruction();

		public boolean requiresDenseOutput() {
			return false;
		}

		public void handleStep(StepInterpolator interpolator, boolean isLast)
				throws DerivativeException {
			double t = interpolator.getCurrentTime();
			double[] y1 = interpolator.getInterpolatedState();
			// System.out.println(t + " " + y[0]+ " "+y[1]);

			boolean oldState = cons1.isSuppressLabelsActive();
			cons1.setSuppressLabelCreation(true);

			// g.add(new GeoPoint(cons, null, t, y[0], 1.0));
			al.add(new MyPoint(t, y1[0], true));

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

		public int getDimension() {
			return 2;
		}

		/*
		 * Transform 2nd order into 2 linked first order y0'' + b y0' + c y0 =
		 * f(x) substitute y0' = y1 (1) y1' + b y1 + c y = f(x) y1' = f(x) - b
		 * y1 - c y0 (2)
		 */

		public void computeDerivatives(double t, double[] y, double[] yDot) {

			yDot[0] = y[1]; // (1)
			yDot[1] = f.evaluate(t) - b.evaluate(t) * y[1] - c.evaluate(t)
					* y[0]; // (2)
		}
	}

	// TODO Consider locusequability
	
}
