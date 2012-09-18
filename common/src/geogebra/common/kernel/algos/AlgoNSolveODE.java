package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.MyPoint;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.FunctionalNVar;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoLocus;
import geogebra.common.kernel.geos.GeoNumeric;

import org.apache.commons.math.ode.DerivativeException;
import org.apache.commons.math.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math.ode.FirstOrderIntegrator;
import org.apache.commons.math.ode.IntegratorException;
import org.apache.commons.math.ode.nonstiff.DormandPrince54Integrator;
import org.apache.commons.math.ode.sampling.StepHandler;
import org.apache.commons.math.ode.sampling.StepInterpolator;


import java.util.ArrayList;


/**
 * @author Bencze Balazs 
 *
 */
public class AlgoNSolveODE extends AlgoElement {

	private GeoList fun; 				// input
	private GeoList startY;				// input
	private GeoNumeric startX;			// input
	private GeoNumeric endX;			// input
	private GeoNumeric relTol;			// input
	
	private GeoList out;				// output
	
	protected ArrayList<MyPoint> al[];
	
	private double t0, y0[];
	protected int dim;

	/**
	 * @param cons cons
	 * @param labels labels
	 * @param fun the list of the functions
	 * @param startX  
	 * @param startY 
	 * @param endX 
	 * @param relTol relative tolerance
	 */
	public AlgoNSolveODE(Construction cons, String labels[], 
			GeoList fun, GeoNumeric startX, GeoList startY, 
			GeoNumeric endX, GeoNumeric relTol) {
		
		super(cons);
		
		this.fun = fun;   
		this.startY = startY;
		this.startX = startX;  
		this.endX = endX;            	
		this.relTol = relTol;
		
		dim = fun.size();
		y0 = new double[dim];
		
		setInputOutput();
		compute();
		out.setLabel(labels[0]);
	}
	
	@Override
	public Algos getClassName() {
		return Algos.AlgoNSolveODE;
	}
	
	@Override
	final public String toString(StringTemplate tpl) {       
	     return getCommandDescription(tpl);
	 }
	
	/**
	 * @return locus
	 */
	public GeoList getResult() {
		return out;
	}
	
	@Override
	protected void setInputOutput() {
		input = new GeoElement[5]; 
		input[0] = fun;
		input[1] = startX;
		input[2] = startY;
		input[3] = endX;
		input[4] = relTol;
		out = new GeoList(cons);
		super.setOutputLength(1);
		super.setOutput(0, out);
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
		if ( !startX.isDefined() || !relTol.isDefined() 
				|| Kernel.isZero(relTol.getDouble()) 
				|| !endX.isDefined() ) {
			setUndefined();
			return;
		}
		
		t0 = startX.getDouble();
		for (int i = 0; i < dim; i++) {
			y0[i] = ((GeoNumeric)startY.get(i)).getDouble();
		}
		
		al = new ArrayList[dim];

		for (int i = 0; i < dim; i++) {
			al[i] = new ArrayList<MyPoint>();
		}
		
		FirstOrderIntegrator integrator = 
				new DormandPrince54Integrator(0.001, 0.01, 0.000001, relTol.getDouble());
		FirstOrderDifferentialEquations ode = new ODEN(fun);
		integrator.addStepHandler(stepHandler);
		
		for(int i = 0; i < dim; i++) {
			al[i].add(new MyPoint(startX.getDouble(), y0[i], false));
		}
		try {
			integrator.integrate(ode, t0, y0, endX.getDouble(), y0);
		} catch (DerivativeException e) {
			e.printStackTrace();
			setUndefined();
			return;
		} catch (IntegratorException e) {
			e.printStackTrace();
			setUndefined();
			return;
		}
		
		for (int i = 0; i < dim; i++) {
			out.add(new GeoLocus(cons));
			((GeoLocus)out.get(i)).setPoints(al[i]);
			((GeoLocus)out.get(i)).setDefined(true);
		}
	}
	
	private void setUndefined() {
		out.setUndefined();
	}
	
	private StepHandler stepHandler = new StepHandler() {
		
		public void reset() {
			//
		}

		public boolean requiresDenseOutput() { 
			return true; 
		}

		public void handleStep(StepInterpolator interpolator, boolean isLast) throws DerivativeException {
			double   t = interpolator.getCurrentTime();
			double[] y1 = interpolator.getInterpolatedState();
			
			for(int i = 0; i < y1.length; i++) {
				al[i].add(new MyPoint(t, y1[i], true));
			}
		}
	};

	private class ODEN implements FirstOrderDifferentialEquations {
		@SuppressWarnings("hiding")
		private GeoList fun;
		
		public ODEN(GeoList fun) {
			this.fun = fun;
		}

		public int getDimension() {
			return dim;
		}
		
		public void computeDerivatives(double t, double[] y, double[] yDot) {
			@SuppressWarnings("hiding")
			double input[] = new double[dim + 1];
			input[0] = t;
			for(int i = 0; i < dim; i++) {
				input[i + 1] = y[i];
			}
			for (int i = 0; i < dim; i++) {
				yDot[i] = ((FunctionalNVar)fun.get(i)).evaluate(input);
			}
		}
		
	}
	
}

