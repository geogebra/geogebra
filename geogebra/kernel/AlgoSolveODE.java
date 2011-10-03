package geogebra.kernel;

import geogebra.kernel.arithmetic.FunctionalNVar;

import java.util.ArrayList;

import org.apache.commons.math.ode.DerivativeException;
import org.apache.commons.math.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math.ode.FirstOrderIntegrator;
import org.apache.commons.math.ode.IntegratorException;
import org.apache.commons.math.ode.nonstiff.ClassicalRungeKuttaIntegrator;
import org.apache.commons.math.ode.sampling.StepHandler;
import org.apache.commons.math.ode.sampling.StepInterpolator;

public class AlgoSolveODE extends AlgoElement {

		private static final long serialVersionUID = 1L;
		private FunctionalNVar f0, f1; // input
		private GeoNumeric x, y, end, step; // input
	    //private GeoList g; // output        
	    private GeoLocus locus; // output   
	    private ArrayList<MyPoint> al;
	    
	    public AlgoSolveODE(Construction cons, String label, FunctionalNVar f0, FunctionalNVar f1, GeoNumeric x, GeoNumeric y, GeoNumeric end, GeoNumeric step) {
	    	super(cons);
	        this.f0 = f0;            	
	        this.f1 = f1;            	
	        this.x = x;            	
	        this.y = y;            	   	
	        this.end = end;            	
	        this.step = step;            	
	    	
	        //g = new GeoList(cons);   
	        locus = new GeoLocus(cons);
	        setInputOutput(); // for AlgoElement        
	        compute();
	        //g.setLabel(label);
	        locus.setLabel(label);
	    }
	    
	    public String getClassName() {
	        return "AlgoSolveODE";
	    }
	    
	    // for AlgoElement
	    protected void setInputOutput() {
	        input = new GeoElement[f1 == null ? 5 : 6];
	    	int i = 0;
	    	
	        input[i++] = (GeoElement)f0;
	        if (f1 != null) input[i++] = (GeoElement)f1;
	        input[i++] = x;
	        input[i++] = y;
	        input[i++] = end;
	        input[i++] = step;

	        output = new GeoElement[1];
	        //output[0] = g;
	        output[0] = locus;
	        setDependencies(); // done by AlgoElement
	    }

	    public GeoLocus getResult() {
	        //return g;
	        return locus;
	    }

	    protected final void compute() {       
	        if (!((GeoElement)f0).isDefined() || !x.isDefined() || !y.isDefined() || !step.isDefined() || !end.isDefined() || kernel.isZero(step.getDouble())) {
	        	//g.setUndefined();
	        	locus.setUndefined();
	        	return;
	        }    
	        
	        //g.clear();
	        if (al == null) al = new ArrayList<MyPoint>();
	        else al.clear();
	        
	        //FirstOrderIntegrator integrator = new DormandPrince853Integrator(1.0e-8, 100.0, 1.0e-10, 1.0e-10);
	        FirstOrderIntegrator integrator = new ClassicalRungeKuttaIntegrator(step.getDouble());
	        FirstOrderDifferentialEquations ode;
	        
	        if (f1 == null) ode = new ODE(f0); else ode = new ODE2(f0,f1);
	        integrator.addStepHandler(stepHandler);
	        
            //boolean oldState = cons.isSuppressLabelsActive();
            //cons.setSuppressLabelCreation(true);
            //g.add(new GeoPoint(cons, null, x.getDouble(), y.getDouble(), 1.0));
            al.add(new MyPoint(x.getDouble(), y.getDouble(), false));
            //cons.setSuppressLabelCreation(oldState);

	        double[] yy = new double[] { y.getDouble() }; // initial state
	        double[] yy2 = new double[] { x.getDouble(), y.getDouble() }; // initial state
	        try {
	        	if (f1 == null)
					integrator.integrate(ode, x.getDouble(), yy, end.getDouble(), yy);
	        	else
	        		integrator.integrate(ode, 0.0, yy2, end.getDouble(), yy2);
			} catch (DerivativeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				locus.setDefined(false);
				return;
			} catch (IntegratorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				locus.setDefined(false);
				return;
			} // now y contains final state at time t=16.0
			
			//g.setDefined(true);
			locus.setPoints(al);
			locus.setDefined(true);
			
	    }
	    
	    final public String toString() {
	    	return getCommandDescription();
	    }

	    StepHandler stepHandler = new StepHandler() {
	        public void reset() {}
	        
	        Construction cons = kernel.getConstruction();
	                
	        public boolean requiresDenseOutput() { return false; }
	                
	        public void handleStep(StepInterpolator interpolator, boolean isLast) throws DerivativeException {
	            double   t = interpolator.getCurrentTime();
	            double[] y = interpolator.getInterpolatedState();
	            //System.out.println(t + " " + y[0]);
	            
	            boolean oldState = cons.isSuppressLabelsActive();
	            cons.setSuppressLabelCreation(true);
	            
	            if (f1 == null) {
	            	//g.add(new GeoPoint(cons, null, t, y[0], 1.0));
	            	al.add(new MyPoint(t, y[0], true));
	            }
	            else
	            {
		            //g.add(new GeoPoint(cons, null, y[0], y[1], 1.0));
	            	al.add(new MyPoint(y[0], y[1], true));
	            }
	            	
	            cons.setSuppressLabelCreation(oldState);
	        }
	    };
	    //integrator.addStepHandler(stepHandler);
	    
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
	        	if (f instanceof GeoFunction && "y".equals(((GeoFunction)f).getFunction().getFunctionVariable().toString())) {
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
	        	if (y1 instanceof GeoFunction && "y".equals(((GeoFunction)y1).getFunction().getFunctionVariable().toString())) {
	        		yDot[0] = ((GeoFunction)y1).evaluate(y[1]);
	        	} else
	        		yDot[0] = y1.evaluate(input);

	        	// special case for f(y)= (substitute y not x)
	        	// eg SolveODE[-x, y, x(A), y(A), 5, 0.1]
	        	if (y0 instanceof GeoFunction && "y".equals(((GeoFunction)y0).getFunction().getFunctionVariable().toString())) {
	        		yDot[1] = ((GeoFunction)y0).evaluate(y[1]);
	        	} else 
	        		yDot[1] = y0.evaluate(input);

	        }

	    }
	}

