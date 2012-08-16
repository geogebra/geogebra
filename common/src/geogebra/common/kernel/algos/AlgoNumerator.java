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
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.kernel.arithmetic.Function;
import geogebra.common.kernel.arithmetic.FunctionNVar;
import geogebra.common.kernel.arithmetic.FunctionVariable;
import geogebra.common.kernel.arithmetic.FunctionalNVar;
import geogebra.common.kernel.arithmetic.MyDouble;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoFunctionNVar;
import geogebra.common.kernel.locusequ.EquationElement;
import geogebra.common.kernel.locusequ.EquationScope;
import geogebra.common.plugin.Operation;

/**
 * Find Numerator
 * 
 * @author Michael Borcherds
 */
public class AlgoNumerator extends AlgoElement {

	private FunctionalNVar f; // input
    private GeoElement g; // output        
    
    public AlgoNumerator(Construction cons, String label, FunctionalNVar f) {
    	this(cons, f);
        g.setLabel(label);
    }
    
    public AlgoNumerator(Construction cons, FunctionalNVar f) {
    	super(cons);
        this.f = f;            	
    	
        if (f instanceof GeoFunction) {
        	g = new GeoFunction(cons);     
        } else {
        	g = new GeoFunctionNVar(cons);             	
        }
        setInputOutput(); // for AlgoElement        
        compute();
    }
    
    @Override
	public Algos getClassName() {
        return Algos.AlgoNumerator;
    }
    
    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = new GeoElement[1];
        input[0] = (GeoElement) f;

        super.setOutputLength(1);
        super.setOutput(0, g);
        setDependencies(); // done by AlgoElement
    }

    public GeoElement getResult() {
        return g;
    }

    @Override
	public final void compute() {       
        if (!f.isDefined()) {
        	g.setUndefined();
        	return;
        }    
        
        ExpressionNode root = f.getFunctionExpression();
        if (root.getOperation() != Operation.DIVIDE) {
        	g.setUndefined();
    		return;
    	}    
        
        ExpressionValue ev = getPart(root); // get Numerator
        
        //Application.debug(root.left.getClass()+"");
          
        if (ev.isExpressionNode()) {
        	
        	if (f instanceof GeoFunction) {
        	
        	Function fun = new Function((ExpressionNode)ev, f.getFunction().getFunctionVariables()[0]);
        	((GeoFunction)g).setFunction(fun);
        	} else {
            	FunctionNVar fun = new FunctionNVar((ExpressionNode)ev, f.getFunction().getFunctionVariables());       		
        		((GeoFunctionNVar)g).setFunction(fun);
        	}
        } else if (ev instanceof FunctionVariable) {
        	if (f instanceof GeoFunction) {
        			
        		// construct function f(x) = x
    			FunctionVariable fv = new FunctionVariable(kernel);	
    			ExpressionNode en = new ExpressionNode(kernel,fv);
    			Function tempFun = new Function(en,fv);
    			tempFun.initFunction();
    			((GeoFunction)g).setFunction(tempFun);		

        	} else {
        		
        		// construct eg f(a,b)=b
        		GeoFunctionNVar ff = ((GeoFunctionNVar)f);
        		FunctionNVar fun = ff.getFunction();
        		FunctionVariable[] vars = fun.getFunctionVariables();        		
    			ExpressionNode en = new ExpressionNode(kernel,ev);    			
    			FunctionNVar newFun = new FunctionNVar(en, vars);
        		((GeoFunctionNVar)g).setFunction(newFun);

        		
        	}
        }
        else if (ev.isNumberValue()) {

    		// construct function f(x) = 1
			FunctionVariable fv = new FunctionVariable(kernel);	
			ExpressionNode en = new ExpressionNode(kernel,new MyDouble(kernel, ((NumberValue)ev).getDouble()));
			Function tempFun = new Function(en,fv);
			tempFun.initFunction();
			((GeoFunction)g).setFunction(tempFun);		
			
        }
        else
        {
        	//Application.debug(ev.getClass()+"");
        	g.setUndefined();
    		return;
    	}    
	
    }
    
    /**
     * over-ridden in AlgoDenominator
     */
    protected ExpressionValue getPart(ExpressionNode node) {
    	return node.getLeft();
    }
    
    
    @Override
	final public String toString(StringTemplate tpl) {
    	return getCommandDescription(tpl);
    } 

	// TODO Consider locusequability

}
