/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel.cas;

import geogebra.kernel.AlgoDrawInformation;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoBoolean;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunction;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.roots.RealRootAdapter;
import geogebra.kernel.roots.RealRootFunction;
import geogebra.main.Application;

import org.apache.commons.math.ConvergenceException;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.MaxIterationsExceededException;
import org.apache.commons.math.analysis.integration.LegendreGaussIntegrator;

/**
 * Integral of a function (GeoFunction)
 * 
 * @author Markus Hohenwarter
 */
public class AlgoIntegralDefinite extends AlgoUsingTempCASalgo implements AlgoDrawInformation{

	private static final long serialVersionUID = 1L;
	private GeoFunction f; // input
    private NumberValue a, b; //input
    private GeoBoolean evaluate; //input
    private GeoElement ageo, bgeo;
    private GeoNumeric n; // output g = integral(f(x), x, a, b)   

    // for symbolic integration
    private GeoFunction symbIntegral;
    
    // for numerical adaptive GaussQuad integration  
    private static final int FIRST_ORDER = 3;
    private static final int SECOND_ORDER = 5;   
    private static final int MAX_ITER = 5;   
    private static LegendreGaussIntegrator firstGauss, secondGauss;
    private static int adaptiveGaussQuadCounter = 0;
    private static final int MAX_GAUSS_QUAD_CALLS = 500;     

    public AlgoIntegralDefinite(
        Construction cons,
        String label,
        GeoFunction f,
        NumberValue a,
        NumberValue b) {
        this(cons, f, a, b,null);
        n.setLabel(label);
    }

    public AlgoIntegralDefinite(
            Construction cons,
            String label,
            GeoFunction f,
            NumberValue a,
            NumberValue b,
            GeoBoolean evaluate) {
            this(cons, f, a, b, evaluate);
            n.setLabel(label);
        }

    
    public AlgoIntegralDefinite(
        Construction cons,
        GeoFunction f,
        NumberValue a,
        NumberValue b,
        GeoBoolean evaluate) {
        super(cons);
        this.f = f;
        n = new GeoNumeric(cons); // output
        this.a = a;
        this.b = b;
        ageo = a.toGeoElement();
        bgeo = b.toGeoElement();
        this.evaluate = evaluate;   
        
               
        // create helper algorithm for symbolic integral
        // don't use symbolic integral for conditional functions
        // or if it should not be evaluated (i.e. a shade-only integral)
        if ((evaluate == null || evaluate.getBoolean()) && !f.isGeoFunctionConditional()) {
            AlgoIntegral algoInt = new AlgoIntegral(cons, f, null);
            symbIntegral = (GeoFunction) algoInt.getResult();           
            cons.removeFromConstructionList(algoInt);     
            // make sure algo is removed properly
            algoCAS = algoInt;
        }
        
        setInputOutput(); // for AlgoElement        
        compute();
        n.setDrawable(true);
    }

    public AlgoIntegralDefinite(GeoFunction f, NumberValue a,
			NumberValue b, GeoBoolean evaluate) {
    	super(f.getConstruction(), false);
    	this.f = f;         
         this.a = a;
         this.b = b;
         this.evaluate = evaluate;  
	}

	public String getClassName() {
        return "AlgoIntegralDefinite";
    }

    // for AlgoElement
    protected void setInputOutput() {
    	if(evaluate == null){
    		input = new GeoElement[3];
    		input[0] = f;
    		input[1] = ageo;
    		input[2] = bgeo;
    	}
    	else
    	{
    		input = new GeoElement[4];
    		input[0] = f;
    		input[1] = ageo;
    		input[2] = bgeo;
    		input[3] = evaluate;
    	}

        setOutputLength(1); 
        setOutput(0,n);
        setDependencies(); // done by AlgoElement
    }

    public GeoNumeric getIntegral() {
        return n;
    }

    double getIntegralValue() {
        return n.getValue();
    }

    public GeoFunction getFunction() {
        return f;
    }

    public NumberValue getA() {
        return a;
    }

    public NumberValue getB() {
        return b;
    }

    protected final void compute() {
        if (!f.isDefined() || !ageo.isDefined() || !bgeo.isDefined()) {
            n.setUndefined();
            return;
        }

       
        // check for equal bounds
        double lowerLimit = a.getDouble();
        double upperLimit = b.getDouble();
        if (Kernel.isEqual(lowerLimit, upperLimit)) {
            n.setValue(0);
            return;
        }
        
        // check if f(a) and f(b) are defined
        double fa = f.evaluate(lowerLimit);
        double fb = f.evaluate(upperLimit);
        if (Double.isNaN(fa) || Double.isInfinite(fa)
        	|| Double.isNaN(fb) || Double.isInfinite(fb)) {
        	n.setUndefined();
        	return;
        }

        // return if it should not be evaluated (i.e. is shade-only)
        if (evaluateOnly()){
        	n.setValue(Double.NaN);
        	return;
        }
        
        /* 
         * Try to use symbolic integral
         *
         * We only do this for functions that do NOT include divisions by their variable.
         * Otherwise there might be problems like:
         * Integral[ 1/x, -2, -1 ] would be undefined (log(-1) - log(-2))
         * Integral[ 1/x^2, -1, 1 ] would be defined (-2)
         */
        if (symbIntegral != null && symbIntegral.isDefined() && !f.includesDivisionByVar()) {        
        	double val = symbIntegral.evaluate(upperLimit) - symbIntegral.evaluate(lowerLimit);
	            n.setValue(val);	 
	            if (n.isDefined()) return;	        
        }                

        // numerical integration
       // max_error = ACCURACY; // current maximum error
        //maxstep = 0;           
        
        double integral = numericIntegration(f, lowerLimit, upperLimit);
        n.setValue(integral);
              
        /*
        Application.debug("***\nsteps: " + maxstep);                   
        Application.debug("max_error: " + max_error);
        */
    }
    //  private int maxstep;
    
    /**
     * Computes integral of function fun in interval a, b using an adaptive Gauss 
     * quadrature approach.
     * @param fun function
     * @param a lower bound
     * @param b upper bound
     * @return integral value
     */
    public static double numericIntegration(RealRootFunction fun, double a, double b) {
    	adaptiveGaussQuadCounter = 0;
    	if (a > b) {
    		return -doAdaptiveGaussQuad(fun, b, a);
    	} else {
    		return doAdaptiveGaussQuad(fun, a, b);    		
    	}
    	
    	//System.out.println("calls: " + adaptiveGaussQuadCounter);  

    }
    
    private static double doAdaptiveGaussQuad(RealRootFunction fun, double a, double b) {    		   	
    	if (++adaptiveGaussQuadCounter > MAX_GAUSS_QUAD_CALLS) {
    		return Double.NaN;
    	}
    	
    	// init GaussQuad classes for numerical integration
        if (firstGauss == null) {
            firstGauss = new LegendreGaussIntegrator(FIRST_ORDER, MAX_ITER);
            secondGauss = new LegendreGaussIntegrator(SECOND_ORDER, MAX_ITER);
        }
        
        double firstSum = 0;
        double secondSum = 0;
        
        boolean error = false;
        
        // integrate using gauss quadrature
        try {
	        firstSum = firstGauss.integrate(new RealRootAdapter(fun), a, b);
	        if (Double.isNaN(firstSum)) return Double.NaN;        
	        secondSum = secondGauss.integrate(new RealRootAdapter(fun), a, b);
	        if (Double.isNaN(secondSum)) return Double.NaN;
        } catch (MaxIterationsExceededException e) {
        	error = true;
        } catch (ConvergenceException e) {
        	error = true;
		} catch (FunctionEvaluationException e) {
			return Double.NaN;
		} catch (IllegalArgumentException e) {
			return Double.NaN;
		}
		
		//if (!error) Application.debug(a+" "+b+" "+(firstSum - secondSum), Kernel.isEqual(firstSum, secondSum, Kernel.STANDARD_PRECISION) ? 1 : 0);
		//else Application.debug(a+" "+b+" error",1);
		
        // check if both results are equal
        boolean equal = !error && Kernel.isEqual(firstSum, secondSum, Kernel.STANDARD_PRECISION);
       
        if (equal) { 
        	// success              
            return secondSum;
        } else {           
            double mid = (a + b) / 2;                             
            double left = doAdaptiveGaussQuad(fun, a, mid);
            if (Double.isNaN(left))
                return Double.NaN;
            else
                return left + doAdaptiveGaussQuad(fun, mid, b);           
        }
    }

    final public String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return app.getPlain("IntegralOfAfromBtoC",f.getLabel(),ageo.getLabel(),bgeo.getLabel());
    }
    
    public AlgoDrawInformation copy(){
    	if(evaluate!= null)
    	return new AlgoIntegralDefinite((GeoFunction)f.copy(),
    			(NumberValue)a.deepCopy(kernel),
    			(NumberValue)b.deepCopy(kernel),
    			(GeoBoolean)evaluate.copy());
    	return new AlgoIntegralDefinite((GeoFunction)f.copy(),
    			(NumberValue)a.deepCopy(kernel),
    			(NumberValue)b.deepCopy(kernel),null);
    }

	/*
	 * 		make sure shaded-only integrals are drawn
	 */
    public boolean evaluateOnly() {
		return evaluate !=null && !evaluate.getBoolean();
	}
    

}
