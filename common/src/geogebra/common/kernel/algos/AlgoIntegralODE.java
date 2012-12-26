package geogebra.common.kernel.algos;

import geogebra.common.euclidian.EuclidianView;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.MyPoint;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.FunctionalNVar;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoLocus;
import geogebra.common.kernel.geos.GeoPoint;

import java.util.ArrayList;

import org.apache.commons.math.ode.DerivativeException;
import org.apache.commons.math.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math.ode.FirstOrderIntegrator;
import org.apache.commons.math.ode.nonstiff.ClassicalRungeKuttaIntegrator;
import org.apache.commons.math.ode.sampling.StepHandler;
import org.apache.commons.math.ode.sampling.StepInterpolator;

/**
 * 
 * Integral[ f(x,y), <Point> ]
 * Integral[ SlopeField, <Point> ]
 * based on AlgoSolveODE
 * 
 * @author michael
 *
 */
public class AlgoIntegralODE extends AlgoElement {

	private GeoElement geo; // input
	private GeoPoint p;
	private GeoLocus locus; // output   

	private FunctionalNVar f0 = null;
	
	private AlgoNumerator numAlgo;
	private AlgoDenominator denAlgo;
	private FunctionalNVar num, den;
	
	@SuppressWarnings("javadoc")
	boolean quotient;
	@SuppressWarnings("javadoc")
	ArrayList<MyPoint> al;

	final private static double step = 0.02;
	final private static int n = 20;



	/**
	 * @param cons cons
	 * @param label label
	 * @param geo function(x,y) or locus from SlopeField
	 * @param p Point
	 */
	public AlgoIntegralODE(Construction cons, String label, GeoElement geo, GeoPoint p) {
		super(cons);
		this.geo = geo;            	
		this.p = p;

		if (geo instanceof FunctionalNVar) {
			f0 = (FunctionalNVar) geo;
		} else if (geo.isGeoLocus()){
			// must be a SlopeField
			AlgoElement algo = geo.getParentAlgorithm();
			if (algo.getClassName().equals(Commands.SlopeField)) {
				f0 = (FunctionalNVar) algo.getInput()[0];
			}
		} // else leave f0 = null

		numAlgo = new AlgoNumerator(cons, f0);
		denAlgo = new AlgoDenominator(cons, f0);
		cons.removeFromConstructionList(numAlgo);
		cons.removeFromConstructionList(denAlgo);

		num = (FunctionalNVar) numAlgo.getGeoElements()[0];
		den = (FunctionalNVar) denAlgo.getGeoElements()[0];

		quotient = num.isDefined() && den.isDefined();
		
		locus = new GeoLocus(cons);
		setInputOutput(); // for AlgoElement        
		compute();
		locus.setLabel(label);

		cons.registerEuclidianViewCE(this);
	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoIntegralODE;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = geo;
		input[1] = p;

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
		if (f0 == null || !((GeoElement)f0).isDefined() || !p.isFinite()) {
			locus.setUndefined();
			return;
		}    

		double xmax = -Double.MAX_VALUE;
		double ymin = Double.MAX_VALUE;
		double xmin = Double.MAX_VALUE;
		double ymax = -Double.MAX_VALUE;

		if (!quotient) {
			// make sure it covers all of EV1 & EV2 if appropriate
			
			cons.removeFromAlgorithmList(numAlgo);
			cons.removeFromAlgorithmList(denAlgo);
			
			EuclidianView view = kernel.getApplication().getEuclidianView1();

			if (view.isVisibleInThisView(locus)) {
				xmax = Math.max(xmax,  view.toRealWorldCoordX((view.getWidth())));
				ymax = Math.max(ymax,  view.toRealWorldCoordY(0));
				xmin = Math.min(xmin,  view.toRealWorldCoordX(0));
				ymin = Math.min(ymin,  view.toRealWorldCoordY((view.getHeight())));
			}

			app = kernel.getApplication();
			if (app.hasEuclidianView2()) {
				EuclidianView view2 = app.getEuclidianView2();
				if (view2.isVisibleInThisView(locus)) {
					xmax = Math.max(xmax,  view2.toRealWorldCoordX((view.getWidth())));
					ymax = Math.max(ymax,  view2.toRealWorldCoordY(0));
					xmin = Math.min(xmin,  view2.toRealWorldCoordX(0));
					ymin = Math.min(ymin,  view2.toRealWorldCoordY((view.getHeight())));
				}
			}


			if (xmax == -Double.MAX_VALUE){
				// not visible in either view
				locus.setUndefined();
				return;
			}    
		}


		if (al == null) al = new ArrayList<MyPoint>();
		else al.clear();

		FirstOrderIntegrator integrator = new ClassicalRungeKuttaIntegrator(step);
		FirstOrderDifferentialEquations ode;

		if (!quotient) {
			ode = new ODE(f0);
		} else {
			ode = new ODE2(num, den);
		}

		integrator.addStepHandler(stepHandler);

		al.add(new MyPoint(p.inhomX, p.inhomY, false));

		double[] yy = new double[] { p.inhomY }; // initial state
		double[] yy2 = new double[] { p.inhomX, p.inhomY }; // initial state
		double[] yya = new double[] { p.inhomY }; // initial state
		double[] yy2a = new double[] { p.inhomX, p.inhomY }; // initial state
			if (!quotient) {


				if (p.inhomX < xmax) {
					// draw forwards
					try {
						integrator.integrate(ode, p.inhomX, yy, xmax, yy);
					} catch (Exception e) {
						e.printStackTrace();
					}

					al.add(new MyPoint(p.inhomX, p.inhomY, false));
				}

				if (p.inhomX > xmin) {
					// draw backwards
					try {
						integrator.integrate(ode, p.inhomX, yya, xmin, yya);
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			}
			else {

				// draw forwards
				try {
					integrator.integrate(ode, 0.0, yy2, n, yy2);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// draw backwards
				al.add(new MyPoint(p.inhomX, p.inhomY, false));
				try {
					integrator.integrate(ode, 0.0, yy2a, -n, yy2a);
				} catch (Exception e) {
					e.printStackTrace();
				}
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
			//
		}

		public boolean requiresDenseOutput() { return false; }

		public void handleStep(StepInterpolator interpolator, boolean isLast) throws DerivativeException {
			double   t = interpolator.getCurrentTime();
			double[] y = interpolator.getInterpolatedState();
			//System.out.println(t + " " + y[0]);

			if (!quotient) {
				al.add(new MyPoint(t, y[0], true));
			}
			else
			{
				al.add(new MyPoint(y[0], y[1], true));
			}

		}
	};

	private static class ODE implements FirstOrderDifferentialEquations {

		FunctionalNVar f;

		public ODE(FunctionalNVar f) {
			this.f = f;
		}

		public int getDimension() {
			return 1;
		}

		public void computeDerivatives(double t, double[] y, double[] yDot) {

			double input[] = {t, y[0]};

			// special case for f(y)= (substitute y not x)
			// eg SolveODE[y, x(A), y(A), 5, 0.1]
			if (f instanceof GeoFunction && ((GeoFunction)f).isFunctionOfY()) {
				yDot[0] = ((GeoFunction)f).evaluate(y[0]);
			} else
				yDot[0] = f.evaluate(input);

		}

	}

	private static class ODE2 implements FirstOrderDifferentialEquations {

		FunctionalNVar y0, y1;

		public ODE2(FunctionalNVar y, FunctionalNVar x) {
			this.y0 = y;
			this.y1 = x;
		}

		public int getDimension() {
			return 2;
		}

		public void computeDerivatives(double t, double[] y, double[] yDot) {

			double input[] = {y[0], y[1]};

			// special case for f(y)= (substitute y not x)
			// eg SolveODE[-y, x, x(A), y(A), 5, 0.1]
			if (y1 instanceof GeoFunction && ((GeoFunction)y1).isFunctionOfY()) {
				yDot[0] = ((GeoFunction)y1).evaluate(y[1]);
			} else
				yDot[0] = y1.evaluate(input);

			// special case for f(y)= (substitute y not x)
			// eg SolveODE[-x, y, x(A), y(A), 5, 0.1]
			if (y0 instanceof GeoFunction && ((GeoFunction)y0).isFunctionOfY()) {
				yDot[1] = ((GeoFunction)y0).evaluate(y[1]);
			} else 
				yDot[1] = y0.evaluate(input);

		}

	}
    @Override
	public void remove() {
    	if(removed)
			return;
        super.remove();
        if (quotient && f0 != null) {
	        ((GeoElement) f0).removeAlgorithm(numAlgo);
	        ((GeoElement) f0).removeAlgorithm(denAlgo);
        }
    }

	// TODO Consider locusequability
}

