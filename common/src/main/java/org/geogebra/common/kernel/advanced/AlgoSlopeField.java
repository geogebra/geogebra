package org.geogebra.common.kernel.advanced;

import java.util.ArrayList;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoNumeratorDenominatorFun;
import org.geogebra.common.kernel.arithmetic.Evaluate2Var;
import org.geogebra.common.kernel.arithmetic.FunctionalNVar;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLocus;
import org.geogebra.common.kernel.geos.GeoNumeric;

/**
 * 
 * eg SlopeField[ x/y ] eg SlopeField[ x/y, 20 ] eg SlopeField[ x/y, 20, 0.8 ]
 * eg SlopeField[ x/y, 20, 0.8, 0, 0, 5, 5 ]
 * 
 * @author michael
 *
 */
public class AlgoSlopeField extends AlgoElement {

	private Evaluate2Var func; // input
	private GeoNumeric n, lengthRatio, minX, minY, maxX, maxY;
	// private GeoList g; // output
	private GeoLocus locus; // output
	@SuppressWarnings("javadoc")
	ArrayList<MyPoint> al;

	private AlgoNumeratorDenominatorFun numAlgo;
	private AlgoNumeratorDenominatorFun denAlgo;
	private FunctionalNVar num, den;
	private boolean quotient;
	private EuclidianView mainView;

	/**
	 * @param cons
	 *            cons
	 * @param label
	 *            label
	 * @param func
	 *            fucntion
	 * @param n
	 *            length of grid
	 * @param lengthRatio
	 *            between 0 and 1
	 * @param minX
	 *            minX
	 * @param minY
	 *            minY
	 * @param maxX
	 *            maxX
	 * @param maxY
	 *            maxY
	 */
	public AlgoSlopeField(Construction cons, String label, Evaluate2Var func,
			GeoNumeric n, GeoNumeric lengthRatio, GeoNumeric minX,
			GeoNumeric minY, GeoNumeric maxX, GeoNumeric maxY) {
		super(cons);
		this.func = func;

		this.n = n;
		this.lengthRatio = lengthRatio;
		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;

		numAlgo = new AlgoNumeratorDenominatorFun(cons, func,
				Commands.Numerator);
		denAlgo = new AlgoNumeratorDenominatorFun(cons, func,
				Commands.Denominator);
		cons.removeFromConstructionList(numAlgo);
		cons.removeFromConstructionList(denAlgo);

		num = (FunctionalNVar) numAlgo.getGeoElements()[0];
		den = (FunctionalNVar) denAlgo.getGeoElements()[0];

		quotient = num.isDefined() && den.isDefined();

		if (!quotient) {
			cons.removeFromAlgorithmList(numAlgo);
			cons.removeFromAlgorithmList(denAlgo);
		}

		// g = new GeoList(cons);
		locus = new GeoLocus(cons);
		setInputOutput(); // for AlgoElement
		compute();
		// g.setLabel(label);
		locus.setLabel(label);

		cons.registerEuclidianViewCE(this);

	}

	@Override
	public Commands getClassName() {
		return Commands.SlopeField;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {

		int noOfInputs = 1;
		if (n != null)
			noOfInputs++;
		if (lengthRatio != null)
			noOfInputs++;
		if (minX != null)
			noOfInputs++;
		if (minY != null)
			noOfInputs++;
		if (maxX != null)
			noOfInputs++;
		if (maxY != null)
			noOfInputs++;

		input = new GeoElement[noOfInputs];
		int i = 0;

		input[i++] = (GeoElement) func;
		if (n != null)
			input[i++] = n;
		if (lengthRatio != null)
			input[i++] = lengthRatio;
		if (minX != null)
			input[i++] = minX;
		if (minY != null)
			input[i++] = minY;
		if (maxX != null)
			input[i++] = maxX;
		if (maxY != null)
			input[i++] = maxY;

		super.setOutputLength(1);
		super.setOutput(0, locus);
		setDependencies(); // done by AlgoElement
	}

	/**
	 * @return locus
	 */
	public GeoLocus getResult() {
		return locus;
	}

	@Override
	public final void compute() {
		if (!((GeoElement) func).isDefined()) {
			locus.setUndefined();
			return;
		}

		if (al == null)
			al = new ArrayList<MyPoint>();
		else
			al.clear();

		mainView = null;
		double xmax = -Double.MAX_VALUE;
		double ymin = Double.MAX_VALUE;
		double xmin = Double.MAX_VALUE;
		double ymax = -Double.MAX_VALUE;

		if (minX != null) {
			xmax = maxX.getDouble();
			ymax = maxY.getDouble();
			xmin = minX.getDouble();
			ymin = minY.getDouble();
			mainView = kernel.getApplication().getEuclidianView1();
			if (kernel.getApplication().hasEuclidianView2(1)
					&& kernel.getApplication().getEuclidianView2(1)
							.isVisibleInThisView(locus)
					&& !mainView.isVisibleInThisView(locus)) {
				mainView = kernel.getApplication().getEuclidianView2(1);
			}
		} else {

			// make sure it covers all of EV1 & EV2 if appropriate

			EuclidianView view = kernel.getApplication().getEuclidianView1();

			if (view.isVisibleInThisView(locus)) {
				mainView = view;
				xmax = Math
						.max(xmax, view.toRealWorldCoordX((view.getWidth())));
				ymax = Math.max(ymax, view.toRealWorldCoordY(0));
				xmin = Math.min(xmin, view.toRealWorldCoordX(0));
				ymin = Math.min(ymin,
						view.toRealWorldCoordY((view.getHeight())));
			}

			if (kernel.getApplication().hasEuclidianView2(1)) {
				EuclidianView view2 = kernel.getApplication()
						.getEuclidianView2(1);
				if (view2.isVisibleInThisView(locus)) {
					if (mainView == null)
						mainView = view2;
					xmax = Math.max(xmax,
							view2.toRealWorldCoordX((view.getWidth())));
					ymax = Math.max(ymax, view2.toRealWorldCoordY(0));
					xmin = Math.min(xmin, view2.toRealWorldCoordX(0));
					ymin = Math.min(ymin,
							view2.toRealWorldCoordY((view.getHeight())));
				}
			}
		}

		// if it's visible in at least one view, calculate visible portion
		if (xmax > -Double.MAX_VALUE) {
			int nD = (int) (n == null ? 39 : n.getDouble() - 1);

			if (nD < 2 || nD > 100) {
				nD = 39;
			}

			double xStep = (xmax - xmin) / nD;
			double yStep = (ymax - ymin) / nD;

			double length = (lengthRatio == null ? 0.5 : lengthRatio
					.getDouble());

			if (length < 0 || length > 1 || Double.isInfinite(length)
					|| Double.isNaN(length)) {
				length = 0.5;
			}

			length = Math.min(xStep, yStep * mainView.getScaleRatio()) * length
					* 0.5;
			// double yLength = yStep * length * 0.5;


			// AbstractApplication.debug(xStep+" "+yStep+" "+step);

			for (double xx = xmin; xx < xmax + xStep / 2; xx += xStep) {
				for (double yy = ymin; yy < ymax + yStep / 2; yy += yStep) {

					// double[] input1 = { xx, yy };
					// double gradient = func.evaluate(input1);

					// AbstractApplication.debug(num.isDefined()+" "+den.isDefined());

					if (num.isDefined() && den.isDefined()) {
						// quotient function like x / y

						// make sure eg SlopeField[(2 - y) / 2] works


						double numD = num.evaluate(xx, yy);
						double denD = den.evaluate(xx, yy);

						if (Kernel.isZero(denD)) {
							if (Kernel.isZero(numD)) {
								// just a dot
								al.add(new MyPoint(xx, yy, false));
								al.add(new MyPoint(xx, yy, true));
							} else {
								// vertical line
								drawLine(0, 1, length, xx, yy);
							}
						} else {

							// standard case
							double gradient = numD / denD;
							drawLine(1, gradient, length, xx, yy);
						}
					} else {
						// non-quotient function like x y
						double gradient;

						gradient = func.evaluate(xx, yy);

						drawLine(1, gradient, length, xx, yy);

					}

				}
			}
		}

		locus.setPoints(al);
		locus.setDefined(true);

	}

	private void drawLine(double dx0, double dy0, double length, double xx,
			double yy) {
		/*
		 * double theta = Math.atan(gradient); double dx = Math.cos(theta);
		 * double dy = Math.sin(theta);
		 */
		double dyScaled = dy0 * mainView.getScaleRatio();
		double coeff = Math.sqrt(dx0 * dx0 + dyScaled * dyScaled);
		double dx = dx0 * length / coeff;
		double dy = dy0 * length / coeff;
		al.add(new MyPoint(xx - dx, yy - dy, false));
		al.add(new MyPoint(xx + dx, yy + dy, true));

	}

	@Override
	public void remove() {
		if (removed)
			return;
		super.remove();
		((GeoElement) func).removeAlgorithm(numAlgo);
		((GeoElement) func).removeAlgorithm(denAlgo);
	}

	

}
