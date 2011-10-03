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
import geogebra.kernel.roots.RealRootUtil;

import org.apache.commons.math.analysis.solvers.UnivariateRealSolver;
import org.apache.commons.math.analysis.solvers.UnivariateRealSolverFactory;

/**
 * Finds one real root of a function in the given interval using Brent's method. 
 */
public class AlgoRootInterval extends AlgoElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GeoFunction f; // input    
    private NumberValue a, b; // interval bounds
    private GeoPoint rootPoint; // output 

    private GeoElement aGeo, bGeo;
    private UnivariateRealSolver rootFinder;

    public AlgoRootInterval(
        Construction cons,
        String label,
        GeoFunction f,
        NumberValue a,
        NumberValue b) {
        super(cons);
        this.f = f;
        this.a = a;
        this.b = b;
        aGeo = a.toGeoElement();
        bGeo = b.toGeoElement();

        // output
        rootPoint = new GeoPoint(cons);
        setInputOutput(); // for AlgoElement    
        compute();
        rootPoint.setLabel(label);
    }

    public String getClassName() {
        return "AlgoRootInterval";
    }

    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[3];
        input[0] = f;
        input[1] = aGeo;
        input[2] = bGeo;

        output = new GeoElement[1];
        output[0] = rootPoint;
        setDependencies();
    }

    public GeoPoint getRootPoint() {
        return rootPoint;
    }

    protected final void compute() {
        rootPoint.setCoords(calcRoot(), 0.0, 1.0);
    }

    final double calcRoot() {
        if (!(f.isDefined() && aGeo.isDefined() && bGeo.isDefined()))
			return Double.NaN;
        
        double root = Double.NaN;
        Function fun = f.getFunction();
        
        if (rootFinder == null) {
        	UnivariateRealSolverFactory fact = UnivariateRealSolverFactory.newInstance();
        	rootFinder = fact.newBrentSolver();
        }
        
        try {
        	// Brent's method
            root = rootFinder.solve(new RealRootAdapter(fun), a.getDouble(), b.getDouble());          
        } catch (Exception e) {        	
            try {                               	        	
            	// Let's try again by searchin for a valid domain first
            	double [] borders = RealRootUtil.getDefinedInterval(fun, a.getDouble(), b.getDouble());            	
            	root = rootFinder.solve(new RealRootAdapter(fun), borders[0], borders[1]);                
            } catch (Exception ex) {   
            	root = Double.NaN;
            } 
        }
        
        // check result
        if (Math.abs(fun.evaluate(root)) < Kernel.MIN_PRECISION)
            return root;
        else
        	return Double.NaN;
    }

    final public String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
    	return app.getPlain("RootOfAonIntervalBC",f.getLabel(),aGeo.getLabel(),bGeo.getLabel());
    }
}
