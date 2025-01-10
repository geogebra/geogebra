package org.geogebra.common.kernel.algos;

import java.util.ArrayList;

import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.DormandPrince54Integrator;
import org.apache.commons.math3.ode.sampling.StepHandler;
import org.apache.commons.math3.ode.sampling.StepInterpolator;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.SegmentType;
import org.geogebra.common.kernel.arithmetic.FunctionalNVar;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoLocus;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.LabelManager;

/**
 * @author Bencze Balazs
 */
public class AlgoNSolveODE extends AlgoElement {

	private GeoList fun; // input
	private GeoList startY; // input
	private GeoNumeric startX; // input
	private GeoNumeric endX; // input

	private GeoLocus[] out; // output
	/** list of solutions */
	protected ArrayList<ArrayList<MyPoint>> al;

	private double t0;
	private double[] y0;
	/** dimension (number of functions) */
	protected int dim;

	/**
	 * @param cons
	 *            cons
	 * @param labels
	 *            labels
	 * @param fun
	 *            the list of the functions
	 * @param startX
	 *            from where should be integrated (X-coords)
	 * @param startY
	 *            from where should be integrated (y-coords)
	 * @param endX
	 *            until when should be integrated (X-coords)
	 */
	public AlgoNSolveODE(Construction cons, String[] labels, GeoList fun,
			GeoNumeric startX, GeoList startY, GeoNumeric endX) {

		super(cons);

		this.fun = fun;
		this.startY = startY;
		this.startX = startX;
		this.endX = endX;

		dim = fun.size();
		y0 = new double[dim];
		setInputOutput();
		compute();
		LabelManager.setLabels(labels, out);
	}

	@Override
	public Commands getClassName() {
		return Commands.NSolveODE;
	}

	/**
	 * @return locus
	 */
	public GeoLocus[] getResult() {
		return out;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[4];
		input[0] = fun;
		input[1] = startX;
		input[2] = startY;
		input[3] = endX;
		out = new GeoLocus[dim];
		for (int i = 0; i < dim; i++) {
			out[i] = new GeoLocus(cons);
		}
		super.setOutputLength(dim);
		for (int i = 0; i < dim; i++) {
			super.setOutput(i, out[i]);
		}
		setDependencies();
	}

	@Override
	public void compute() {
		for (int i = 0; i < dim; i++) {
			if (!fun.get(i).isDefined() || !startY.get(i).isDefined()) {
				setUndefined();
				return;
			}
		}
		if (!startX.isDefined() || !endX.isDefined()) {
			setUndefined();
			return;
		}

		t0 = startX.getDouble();
		for (int i = 0; i < dim; i++) {
			y0[i] = ((GeoNumeric) startY.get(i)).getDouble();
		}

		al = new ArrayList<>(dim);

		for (int i = 0; i < dim; i++) {
			al.add(new ArrayList<MyPoint>());
		}

		FirstOrderIntegrator integrator = new DormandPrince54Integrator(0.001,
				0.01, 0.000001, 0.0001);
		FirstOrderDifferentialEquations ode = new ODEN(fun);

		integrator.addStepHandler(stepHandler);

		for (int i = 0; i < dim; i++) {
			al.get(i).add(new MyPoint(startX.getDouble(), y0[i],
					SegmentType.MOVE_TO));
		}
		try {
			integrator.integrate(ode, t0, y0, endX.getDouble(), y0);
		} catch (RuntimeException e) {
			// catches ArithmeticException, IllegalStateException and
			// ArithmeticException
			setUndefined();
			return;
		}

		for (int i = 0; i < dim; i++) {
			out[i].setPoints(al.get(i));
			out[i].setDefined(true);
		}
	}

	private void setUndefined() {
		for (int i = 0; i < out.length; i++) {
			out[i].setUndefined();
		}
	}

	private StepHandler stepHandler = new StepHandler() {

		@Override
		public void init(double ts0, double[] ys0, double t) {
			//this.
		}

		@Override
		public void handleStep(StepInterpolator interpolator, boolean isLast) {
			double t = interpolator.getCurrentTime();
			if (!Double.isFinite(t)) {
				throw new IllegalArgumentException(
						"Invalid value of time:" + t);
			}
			double[] y1 = interpolator.getInterpolatedState();
			for (int i = 0; i < y1.length; i++) {
				al.get(i).add(new MyPoint(t, y1[i], SegmentType.LINE_TO));
			}
		}
	};

	private class ODEN implements FirstOrderDifferentialEquations {
		private GeoList fun1;

		public ODEN(GeoList fun) {
			this.fun1 = fun;
		}

		@Override
		public int getDimension() {
			return dim;
		}

		@Override
		public void computeDerivatives(double t, double[] y, double[] yDot) {
			double[] input1 = new double[dim + 1];
			input1[0] = t;
			for (int i = 0; i < dim; i++) {
				input1[i + 1] = y[i];
			}
			for (int i = 0; i < dim; i++) {
				yDot[i] = ((FunctionalNVar) fun1.get(i)).evaluate(input1);
			}
		}

	}

}
