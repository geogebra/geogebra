/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.kernel.arithmetic.Function;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.roots.RealRootAdapter;
import geogebra.kernel.roots.RealRootDerivAdapter;
import geogebra.kernel.roots.RealRootDerivFunction;
import geogebra.kernel.roots.RealRootUtil;

import org.apache.commons.math.analysis.solvers.BrentSolver;
import org.apache.commons.math.analysis.solvers.NewtonSolver;

/**
 * Finds one real root of a function with newtons method.
 * The first derivative of this function must exist. 
 */
public class AlgoRootNewton extends AlgoIntersectAbstract {

	public static final int MAX_ITERATIONS = 100;
	
	private static final long serialVersionUID = 1L;
	private GeoFunction f; // input, g for intersection of functions       
    private NumberValue start; // start value for root of f 
    private GeoPoint rootPoint; // output 

    private GeoElement startGeo;
    private NewtonSolver rootFinderNewton; 
    private BrentSolver rootFinderBrent;

    public AlgoRootNewton(
        Construction cons,
        String label,
        GeoFunction f,
        NumberValue start) {
        super(cons);
        this.f = f;
        this.start = start;
        startGeo = start.toGeoElement();

        // output
        rootPoint = new GeoPoint(cons);
        setInputOutput(); // for AlgoElement    
        compute();

        rootPoint.setLabel(label);
    }

    AlgoRootNewton(Construction cons) {
        super(cons);
    }

    public String getClassName() {
        return "AlgoRootNewton";
    }

    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[2];
        input[0] = f;
        input[1] = startGeo;
                
        output = new GeoPoint[1];
        output[0] = rootPoint;
        setDependencies();       
    }

    public GeoPoint getRootPoint() {
        return rootPoint;
    }

    protected void compute() {
        if (!(f.isDefined() && startGeo.isDefined())) {
            rootPoint.setUndefined();
        } else {
        	double startValue = start.getDouble();
        	Function fun = f.getFunction(startValue);        	
        	
            // calculate root
            rootPoint.setCoords(calcRoot(fun, startValue), 0.0, 1.0);                       
        }
    }

    final double calcRoot(Function fun, double start) {
    	double root = Double.NaN;
  		if (rootFinderBrent == null)
    		rootFinderBrent = new BrentSolver(Kernel.STANDARD_PRECISION);
    	
    	// try Brent method with borders close to start value
    	try {	
     		double step = (kernel.getXmax() - kernel.getXmin()) / 10;
    		root = rootFinderBrent.solve(MAX_ITERATIONS, new RealRootAdapter(fun), 
    				start - step, start + step, start);
    		if (checkRoot(fun, root)) {
    			//System.out.println("1. Brent worked: " + root);
    			return root;
    		}
    	} catch (Exception e) {
    		root = Double.NaN;
    	}
    	
    	// try Brent method on valid interval around start
    	double [] borders = getDomain(fun, start);	
		try {
    		root = rootFinderBrent.solve(MAX_ITERATIONS, new RealRootAdapter(fun), 
    				borders[0], borders[1], start);
       		if (checkRoot(fun, root)) {
    			//System.out.println("2. Brent worked: " + root);
    			return root;
    		}
    	} catch (Exception e) {
    		root = Double.NaN;
    	}
    	
    	// try Newton's method
        RealRootDerivFunction derivFun = fun.getRealRootDerivFunction();        
        if (derivFun != null) {       	
        	// check if fun(start) is defined
        	double eval = fun.evaluate(start);
        	if (Double.isNaN(eval) || Double.isInfinite(eval)) {
        		// shift left border slightly right
        		borders[0] =  0.9 * borders[0] + 0.1 * borders[1];
        		start = (borders[0] + borders[1])/2;
        	}
        	
        	if (rootFinderNewton == null) {
        		rootFinderNewton = new NewtonSolver();
        	}
        	
            try {        	                  	
            	root = rootFinderNewton.solve(MAX_ITERATIONS, new RealRootDerivAdapter(derivFun), 
            			borders[0], borders[1], start);                      
            	if (checkRoot(fun, root)) {
            		//System.out.println("Newton worked: " + root);
        			return root;
        		}
            } 
            catch (Exception e) {  
            	root = Double.NaN;
            }
        }
        
       // neither Brent nor Newton worked
       return Double.NaN;
    }
    
    private boolean checkRoot(Function fun, double root) {
    	 // check what we got
        return !Double.isNaN(root) && (Math.abs(fun.evaluate(root)) < Kernel.MIN_PRECISION );
    }
    
    /**
     * Tries to find a valid domain for the given function around it's starting value.
     */
    private double [] getDomain(Function fun, double start) {
    	double screenWidth = kernel.getXmax() - kernel.getXmin();
    	return RealRootUtil.getDefinedInterval(fun, start - screenWidth, start + screenWidth);
    }

    public String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return app.getPlain("RootOfAWithInitialValueB",f.getLabel(),startGeo.getLabel());

    }
}
