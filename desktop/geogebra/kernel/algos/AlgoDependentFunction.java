/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel.algos;

import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.kernel.arithmetic.FunctionVariable;
import geogebra.common.kernel.arithmetic.Functional;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.arithmetic.Operation;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.kernel.Construction;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Function;
import geogebra.kernel.geos.GeoFunction;
import geogebra.main.Application;

/**
 * This class is only needed to handle dependencies
 * 
 * @author Markus Hohenwarter
 */
public class AlgoDependentFunction extends AlgoElement {

    protected Function fun;
    protected GeoFunction f; // output         
    
    private Function expandedFun;    
    private ExpressionNode expression;
    private boolean expContainsFunctions; // expression contains functions

    /** Creates new AlgoDependentFunction */
    public AlgoDependentFunction(Construction cons, String label, Function fun) {
        this(cons, fun);
        
        String derivativeLabel = null;      
        
        // auto label for f'' to be f'' etc
        if (label == null) {
        	derivativeLabel = getDerivativeLabel(fun);
        }
        
       	f.setLabel(derivativeLabel != null ? derivativeLabel : label);
    }
    
    public AlgoDependentFunction(Construction cons, Function fun) {
        super(cons);
        this.fun = fun;
        f = new GeoFunction(cons);
        f.setFunction(fun);
        
        // look for FUNCTION or DERIVATIVE nodes in function
        expression = fun.getExpression();
        expContainsFunctions = containsFunctions(expression);
        if (expContainsFunctions) {
            expandedFun = new Function(fun, kernel);
        }
        
        setInputOutput(); // for AlgoElement
        
        compute();
    }
    
    public AlgoDependentFunction(Construction cons) {
		super(cons);
	}

	@Override
	public String getClassName() {
        return "AlgoDependentFunction";
    }
    
    // for AlgoElement
    @Override
	protected void setInputOutput() {
        input = fun.getGeoElementVariables();

        super.setOutputLength(1);
        super.setOutput(0, f);
        setDependencies(); // done by AlgoElement
    }

    public GeoFunction getFunction() {
        return f;
    }

    @Override
	public final void compute() {
        // evaluation of function will be done in view (see geogebra.euclidian.DrawFunction)
        
        // check if function is defined
        boolean isDefined = true;
        for (int i=0; i < input.length; i++) {
            if (!input[i].isDefined()) {
                isDefined = false;
                break;
            }
        }
        f.setDefined(isDefined);
               
        if (isDefined && expContainsFunctions) {
            // expand the functions and derivatives in expression tree
            ExpressionValue ev = null;
            
            try { // needed for eg f(x)=floor(x) f'(x)
            	
        		boolean internationalizeDigits = Kernel.internationalizeDigits;
        		Kernel.internationalizeDigits = false;
       	
            	ev = expandFunctionDerivativeNodes(expression.deepCopy(kernel));

        		Kernel.internationalizeDigits = internationalizeDigits;

            } catch (Exception e) {
            	e.printStackTrace();
            	Application.debug("derivative failed");
            }
            
            if (ev == null) {
            	f.setUndefined();
            	return;
            }

            ExpressionNode node;
            if (ev.isExpressionNode()) 
                node = (ExpressionNode) ev;
            else
                node = new ExpressionNode(kernel, ev);
                
            expandedFun.setExpression(node);
            f.setFunction(expandedFun);
            if(f.isBooleanFunction())
            	f.resetIneqs();
        }
        else if(f.isBooleanFunction())
        	f.getFunction().updateIneqs();        
    }
    
    /**
     * Expandes all FUNCTION and DERIVATIVE nodes in the given
     * expression. 
     * @return new ExpressionNode as result
     */
    private static ExpressionValue expandFunctionDerivativeNodes(ExpressionValue ev) {
        if (ev != null && ev.isExpressionNode()) {
            ExpressionNode node = (ExpressionNode) ev;
            ExpressionValue leftValue = node.getLeft();
            
            switch (node.getOperation()) {
                case FUNCTION:                                   
                    // could be DERIVATIVE node
                    if (leftValue.isExpressionNode()) {
                    	leftValue = expandFunctionDerivativeNodes(leftValue);
                        node.setLeft(leftValue);
                        if (leftValue.isExpressionNode())
                        	return node;
                    }                        
                    
                	// we do NOT expand GeoFunctionConditional objects in expression tree
                    if (leftValue.isGeoElement() &&
                    	((GeoElement)leftValue).isGeoFunctionConditional()) 
                    		return node;
                
                	Function fun = (Function)((Functional) leftValue).getFunction();
                	FunctionVariable x = fun.getFunctionVariable();
                	//  don't destroy the function
                	ExpressionNode funcExpression = fun.getExpression().getCopy(fun.getKernel());
                	// now replace every x in function by the expanded argument
                	return funcExpression.replaceAndWrap(x, 
                                    expandFunctionDerivativeNodes(node.getRight()));                    
            
                case DERIVATIVE:                		
                	// don't expand derivative of GeoFunctionConditional 
                    if (leftValue.isGeoElement() &&
                    	((GeoElement)leftValue).isGeoFunctionConditional()) {
                    	return node;
                    }
                    // STANDARD case
                    else {
                    	int order = (int) Math.round(((NumberValue)node.getRight()).getDouble());                        
                    	return (GeoFunction)((Functional) leftValue).getGeoDerivative(order);	
                    }
                
                // remove spreadsheet $ references, i.e. $A1 -> A1 
                case $VAR_ROW:
				case $VAR_COL:
				case $VAR_ROW_COL:				
					return leftValue;
                    
                default: // recursive calls
                    node.setLeft(expandFunctionDerivativeNodes(leftValue));
                    node.setRight(expandFunctionDerivativeNodes(node.getRight()));
                    return node;
            }
        } else
			return ev;
    }
    
    public static boolean containsFunctions(ExpressionValue ev) {
        if (ev != null && ev.isExpressionNode()) {
            ExpressionNode node = (ExpressionNode) ev;
            Operation op = node.getOperation();
            if (op.equals(Operation.FUNCTION) || 
                op.equals(Operation.DERIVATIVE))
                return true;
			else
				return containsFunctions(node.getLeft()) || 
                            containsFunctions(node.getRight());
        }
        return false;
    }
    
    StringBuilder sb;
    @Override
	public String toString() {
        if (sb == null) sb = new StringBuilder();
        else sb.setLength(0);
        if (f.isLabelSet() && !f.isBooleanFunction()) {
            sb.append(f.label);
            sb.append("(");
			sb.append(f.getVarString());
			sb.append(") = ");
        }  
        sb.append(fun.toString());
        return sb.toString();
    }
    
    @Override
	public String toRealString() {
        if (sb == null) sb = new StringBuilder();
        else sb.setLength(0);
        if (f.isLabelSet() && !f.isBooleanFunction()) {
            sb.append(f.getRealLabel());
            sb.append("(");
			sb.append(f.getVarString());
			sb.append(") = ");
        }  
        sb.append(fun.getExpression().toRealString());
        return sb.toString();
    }
    
    /*
     * checks to see if this is an nth derivative,
     * and return an appropriate label eg f''' for 3rd derivative
     */
    private static String getDerivativeLabel(Function fun) {
        ExpressionValue ev = fun.getExpression().getLeft();
        if (ev.isExpressionNode()) {
        	ExpressionNode enL = (ExpressionNode)(fun.getExpression().getLeft());
        	if (enL.getOperation().equals(Operation.DERIVATIVE)) {
	        	if (enL.getLeft().isGeoElement()) {
	
	        	  GeoElement geo = (GeoElement)enL.getLeft();

	        	  if (geo.getLabel() != null) {
	        		  
	        		  ExpressionValue evR = (enL.getRight());
	        		  
	        		 if (evR.isNumberValue()) {
	        			 NumberValue num = (NumberValue)evR;
	        			 double val = num.getDouble();

	        			 if (val > 0d && Kernel.isInteger(val)) {
	        				 
	        				 // eg f''' if val == 3
	        				 return geo.getLabel() + geogebra.util.Util.string("'",(int)val); // eg f''''

	        			 }
	        		 }
	        		  
	        	  }
	        	}
        	}
        }
    	return null;

    }

}
