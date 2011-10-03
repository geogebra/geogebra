/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel;

import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.NumberValue;


/**
 *Function limited to interval [a, b]
 */
public class AlgoFunctionInterval extends AlgoElement {

	private static final long serialVersionUID = 1L;
	private GeoFunction f; // input    
    private NumberValue a, b; // input
    private GeoElement ageo, bgeo;
    private GeoFunction g; // output g     
           
    /** Creates new AlgoDependentFunction */
    public AlgoFunctionInterval(Construction cons, String label, 
    		GeoFunction f, NumberValue a, NumberValue b) 
    {
        this(cons, f, a, b);
        g.setLabel(label);
    }
    
    public AlgoFunctionInterval(Construction cons, 
    		GeoFunction f, NumberValue a, NumberValue b) 
    {
        super(cons);
        this.f = f;
        this.a = a;
        this.b = b;         
        ageo = a.toGeoElement();
        bgeo = b.toGeoElement();
            
        //g = new GeoFunction(cons); // output
        //g = new GeoFunction(cons); // output
        
        g = (GeoFunction) f.copyInternal(cons);       
        
        //buildFunction();
       // g = initHelperAlgorithm();
        
        setInputOutput(); // for AlgoElement    
        compute();
    }
    
  
    public String getClassName() {
        return "AlgoFunctionInterval";
    }   

    // for AlgoElement
    protected void setInputOutput() {
        input = new GeoElement[3];
        input[0] = f;
        input[1] = ageo;
        input[2] = bgeo;

        output = new GeoElement[1];
        output[0] = g;
        setDependencies(); // done by AlgoElement
    }

    public GeoFunction getFunction() {
        return g;
    }
    
    protected final void compute() {  
        if (!(f.isDefined() && ageo.isDefined() && bgeo.isDefined())) 
            g.setUndefined();
               
        // check if f has changed           
        if (!hasEqualExpressions(f, g)) {
        	g.set(f);
        }
                        
        double ad = a.getDouble();
        double bd = b.getDouble(); 
        if (ad > bd) {
        	g.setUndefined();
        } else {
        	boolean defined = g.setInterval(ad, bd);
         	g.setDefined(defined); 
        }
       
    }
    
    private boolean hasEqualExpressions(GeoFunction f, GeoFunction g) {
    	boolean equal;
    	
    	if (f.isGeoFunctionConditional()) {
    		GeoFunctionConditional geoFun = (GeoFunctionConditional) f;
    		ExpressionNode en2 = null;
    		ExpressionNode en = geoFun.getIfFunction().getFunctionExpression();    		 		
    		equal = exp == en;
    		exp = en;
    		
    		if (geoFun.getElseFunction() != null) {
    			en2 = geoFun.getElseFunction().getFunctionExpression();    			
    			equal = equal && exp2 == en2;
    			exp2 = en2;
    		}
    	} 
    	else {
    		ExpressionNode en = f.getFunctionExpression();    		
    		equal = exp == en;
    		exp = en;
    	}
    	
    	return equal;
    }   
    private ExpressionNode exp, exp2; // current expression of f (needed to notice change of f)
  

    final public String toString() {
        // Michael Borcherds 2008-03-30
        // simplified to allow better Chinese translation
        return app.getPlain("FunctionAonIntervalBC",f.getLabel(),ageo.getLabel(),bgeo.getLabel());

    }

}
