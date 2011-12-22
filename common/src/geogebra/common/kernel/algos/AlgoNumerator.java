/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.common.kernel.algos;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.kernel.arithmetic.Function;
import geogebra.common.kernel.arithmetic.FunctionVariable;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.arithmetic.Operation;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;

/**
 * Find Numerator
 * 
 * @author Michael Borcherds
 */
public class AlgoNumerator extends AlgoElement {

	private GeoFunction f; // input
    private GeoFunction g; // output        
    
    public AlgoNumerator(Construction cons, String label, GeoFunction f) {
    	super(cons);
        this.f = f;            	
    	
        g = new GeoFunction(cons);                
        setInputOutput(); // for AlgoElement        
        compute();
        g.setLabel(label);
    }
    
    @Override
	public Algos getClassName() {
        return Algos.AlgoNumerator;
    }
    
    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = new GeoElement[1];
        input[0] = f;

        super.setOutputLength(1);
        super.setOutput(0, g);
        setDependencies(); // done by AlgoElement
    }

    public GeoFunction getResult() {
        return g;
    }

    @Override
	public final void compute() {       
        if (!f.isDefined()) {
        	g.setUndefined();
        	return;
        }    
        
        ExpressionNode root = f.getFunctionExpression();
        if (root.operation != Operation.DIVIDE) {
        	g.setUndefined();
    		return;
    	}    
        
        ExpressionValue ev = getPart(root); // get Numerator
        
        //Application.debug(root.left.getClass()+"");
          
        if (ev.isExpressionNode()) {
        	Function fun = new Function((ExpressionNode)ev, f.getFunction().getFunctionVariable());
        	g.setFunction(fun);
        } else if (ev instanceof FunctionVariable) {
        	g.set(kernel.getAlgebraProcessor().evaluateToFunction("x", false));
        }
        else if (ev.isNumberValue()) {
        	double val = ((NumberValue)ev).getDouble();
        	g.set(kernel.getAlgebraProcessor().evaluateToFunction(""+val, false));
        }
        else
        {
        	//Application.debug(ev.getClass()+"");
        	g.setUndefined();
    		return;
    	}    
	
    }
    
    /*
     * over-ridden in AlgoDenominator
     */
    protected ExpressionValue getPart(ExpressionNode node) {
    	return node.left;
    }
    
    
    @Override
	final public String toString() {
    	return getCommandDescription();
    } 

}
