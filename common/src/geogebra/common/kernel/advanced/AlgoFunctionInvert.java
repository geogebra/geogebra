/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.common.kernel.advanced;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.AlgoFractionText;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.kernel.arithmetic.Function;
import geogebra.common.kernel.arithmetic.FunctionVariable;
import geogebra.common.kernel.arithmetic.MyDouble;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.plugin.Operation;

/**
 * Inverts a function
 * only works if there is one "x" in the function
 * 
 * works by analyzing the EXpressionNode and reversing it
 * 
 * doesn't take account of domain/range so sin inverts to arcsin, sqrt(x) to x^2
 * 
 * @author Michael Borcherds
 */
public class AlgoFunctionInvert extends AlgoElement {

	private GeoFunction f; // input
    private GeoFunction g; // output        
    
    public AlgoFunctionInvert(Construction cons, String label, GeoFunction f) {
    	this(cons, f);
        g.setLabel(label);
    }
    
    public AlgoFunctionInvert(Construction cons, GeoFunction f) {
    	super(cons);
        this.f = f;            	
    	
        g = new GeoFunction(cons);     
        setInputOutput(); // for AlgoElement        
        compute();
    }
    
    @Override
	public Commands getClassName() {
        return Commands.Invert;
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
        
        ExpressionValue root = f.getFunctionExpression();
        FunctionVariable oldFV = f.getFunction().getFunctionVariable();
        
        // make sure sin(y) inverts to arcsin(y)
        FunctionVariable x = new FunctionVariable(kernel, oldFV.getSetVarString());
        ExpressionNode newRoot = new ExpressionNode(kernel, x);
        
        
    	boolean fvLeft;
		while (root != null && !root.isLeaf() && root.isExpressionNode()) {
        	
        	ExpressionValue left = ((ExpressionNode) root).getLeft();
        	ExpressionValue right = ((ExpressionNode) root).getRight();
        	
	        Operation op;
			switch (op = ((ExpressionNode) root).getOperation() ) {
	        case SIN:
	        case COS:
	        case TAN:
	        case ARCSIN:
	        case ARCCOS:
	        case ARCTAN:
	        case SINH:
	        case COSH:
	        case TANH:
	        case ASINH:
	        case ACOSH:
	        case ATANH:
	        case EXP:
	        case LOG:

	        	newRoot = new ExpressionNode(kernel, newRoot, inverse(op), null);
	        	root = left;
	        	break;
	        	
	        case COT:
	        	// acot(x) can be written as atan(1/x)
	        	newRoot = new ExpressionNode(kernel, new ExpressionNode(kernel,new MyDouble(kernel, 1.0) , Operation.DIVIDE, newRoot), Operation.ARCTAN, null);
	        	root = left;
	        	break;
	        	
	        case SEC:
	        	// asec(x) can be written as acos(1/x)
	        	newRoot = new ExpressionNode(kernel, new ExpressionNode(kernel,new MyDouble(kernel, 1.0) , Operation.DIVIDE, newRoot), Operation.ARCCOS, null);
	        	root = left;
	        	break;
	        	
	        case CSC:
	        	// acsc(x) can be written as asin(1/x)
	        	newRoot = new ExpressionNode(kernel, new ExpressionNode(kernel,new MyDouble(kernel, 1.0) , Operation.DIVIDE, newRoot), Operation.ARCSIN, null);
	        	root = left;
	        	break;
	        	
	        case COTH:
	        	// acoth(x) can be written as atanh(1/x)
	        	newRoot = new ExpressionNode(kernel, new ExpressionNode(kernel,new MyDouble(kernel, 1.0) , Operation.DIVIDE, newRoot), Operation.ATANH, null);
	        	root = left;
	        	break;
	        	
	        case SECH:
	        	// asech(x) can be written as acosh(1/x)
	        	newRoot = new ExpressionNode(kernel, new ExpressionNode(kernel,new MyDouble(kernel, 1.0) , Operation.DIVIDE, newRoot), Operation.ACOSH, null);
	        	root = left;
	        	break;
	        	
	        case CSCH:
	        	// acsch(x) can be written as asinh(1/x)
	        	newRoot = new ExpressionNode(kernel, new ExpressionNode(kernel,new MyDouble(kernel, 1.0) , Operation.DIVIDE, newRoot), Operation.ASINH, null);
	        	root = left;
	        	break;
	        	
	        case CBRT:

	        	newRoot = new ExpressionNode(kernel, newRoot, Operation.POWER, new MyDouble(kernel, 3.0));
	        	root = left;
	        	break;
	        	
	        case SQRT:
	        case SQRT_SHORT:

	        	newRoot = new ExpressionNode(kernel, newRoot, Operation.POWER, new MyDouble(kernel, 2.0));
	        	root = left;
	        	break;

	        case LOG2:

	        	newRoot = new ExpressionNode(kernel, new MyDouble(kernel, 2.0), Operation.POWER, newRoot);
	        	root = left;
	        	break;

	        case LOG10:

	        	newRoot = new ExpressionNode(kernel, new MyDouble(kernel, 10.0), Operation.POWER, newRoot);
	        	root = left;
	        	break;

	        case LOGB:
				if ((fvLeft = left.contains(oldFV)) && (right.contains(oldFV))) {
	        		g.setUndefined();
	        		return;
	        	}

	        	newRoot = new ExpressionNode(kernel, left, Operation.POWER, right);
	        	root = left;
	        	break;

	        case POWER:
	        	if (!left.contains(oldFV)) {
	        		newRoot = new ExpressionNode(kernel, left, Operation.LOGB, newRoot);
	        		root = right;
	        	} else if (!right.contains(oldFV)) {
	        		if (right.isNumberValue()) {
	        			double index = (((NumberValue) (right.evaluate(StringTemplate.maxPrecision))).getDouble());
	        			if (Kernel.isEqual(index, 3)) {
	        				// inverse of x^3 is cbrt(x)
	        				newRoot = new ExpressionNode(kernel, newRoot, Operation.CBRT, null);
	        			} else if (Kernel.isEqual(index, 2)) {
	        				// inverse of x^2 is sqrt(x)
	        				newRoot = new ExpressionNode(kernel, newRoot, Operation.SQRT, null);
	        			} else if (Kernel.isEqual(index, -1)) {
	        				// inverse of x^-1 is x^-1
	        				newRoot = new ExpressionNode(kernel, newRoot, Operation.POWER, new MyDouble(kernel, -1.0));
	        			} else if (right.isExpressionNode() && ((ExpressionNode) right).getOperation().equals(Operation.DIVIDE)) {
	        				// special case for x^(a/b) convert to x^(b/a)
	        				//AbstractApplication.debug("special case for x^(a/b) convert to x^(b/a)");
	        				
	        				ExpressionValue num = ((ExpressionNode) right).getLeft();
	        				ExpressionValue den = ((ExpressionNode) right).getRight();
	        				
	        				newRoot = new ExpressionNode(kernel, newRoot, Operation.POWER, new ExpressionNode(kernel, den, Operation.DIVIDE, num));
	        			} else {
	        				// inverse of x^a is x^(1/a)
	        				
	        				// check if its a rational with small denominator (eg not over 999)
	        				double[] frac = AlgoFractionText.DecimalToFraction(index, Kernel.STANDARD_PRECISION);
	        				
	        				// make sure the minus is at the top of the new fraction
	        				if (frac[0] < 0) {
	        					frac[0] *= -1;
	        					frac[1] *= -1;
	        				}
	        				
	        				if (frac[1] == 0 || frac[0] == 0) {
	        	        		g.setUndefined();
	        	        		return;	        			        					
	        				} else if (frac[0] < 100 && frac[1] < 100) {
	        					// nice form for x^(23/45)
	        					newRoot = new ExpressionNode(kernel, newRoot, Operation.POWER, new ExpressionNode(kernel, new MyDouble(kernel, frac[1]), Operation.DIVIDE, new MyDouble(kernel, frac[0])));
	        				} else {
	        					// just use decimals for fractions like 101/43
	        					newRoot = new ExpressionNode(kernel, newRoot, Operation.POWER, new MyDouble(kernel, 1.0 / index ));
	        				}
	        			}        							
	        		} else {
	        			// inverse of x^a is x^(1/a)
	        			newRoot = new ExpressionNode(kernel, newRoot, Operation.POWER, new ExpressionNode(kernel, new MyDouble(kernel, 1.0), Operation.DIVIDE, right));
	        		}
	        		root = left;	
	        	} else {
	        		//AbstractApplication.debug("failed at POWER");
	        		g.setUndefined();
	        		return;	        		
	        	}
	        	break;
	        	
	        case PLUS:
	        case MULTIPLY:
				if ((fvLeft = left.contains(oldFV)) && (right.contains(oldFV))) {
	        		g.setUndefined();
	        		return;
	        	}
	        	//AbstractApplication.debug("left"+((ExpressionNode) root).getLeft().isConstant());
	        	//AbstractApplication.debug("right"+((ExpressionNode) root).getRight().isConstant());

	        	if (!fvLeft) {
	        		newRoot = new ExpressionNode(kernel, newRoot, inverse(op), left);
	        		root = right;
	        	} else {
	        		newRoot = new ExpressionNode(kernel, newRoot, inverse(op), right);
	        		root = left;	        		
	        	}
	        	
	        	break;
	        	
	        case MINUS:
	        case DIVIDE:
				if ((fvLeft = left.contains(oldFV)) && (right.contains(oldFV))) {
	        		g.setUndefined();
	        		return;
	        	}
	        	//AbstractApplication.debug("left"+((ExpressionNode) root).getLeft().isConstant());
	        	//AbstractApplication.debug("right"+((ExpressionNode) root).getRight().isConstant());

	        	if (!fvLeft) {
	        		// inverse of 3-x is 3-x
	        		newRoot = new ExpressionNode(kernel, left, op, newRoot);
	        		root = right;
	        	} else {
	        		if (op.equals(Operation.DIVIDE)) {
	        			// inverse of x/3 is 3*x (not x*3)
	        			newRoot = new ExpressionNode(kernel, right, inverse(op), newRoot);	        			
	        		} else {
	        			// inverse of x-3 is x+3
	        			newRoot = new ExpressionNode(kernel, newRoot, inverse(op), right);
	        		}
        			root = left;	        		
	        	}
	        	
	        	break;
	        	
	        	default: // eg ABS, CEIL etc
	        		//AbstractApplication.debug("failed at"+ ((ExpressionNode) root).getOperation().toString());
	        		g.setUndefined();
	        		return;
	        }
        }
        
        
		Function tempFun = new Function(newRoot, x);
		tempFun.initFunction();
		g.setDefined(true);
		g.setFunction(tempFun);		

	
    }
    
    private static Operation inverse(Operation op) {
		switch (op) {
		case PLUS:
			return Operation.MINUS;
		case MINUS:
			return Operation.PLUS;
		case MULTIPLY:
			return Operation.DIVIDE;
		case DIVIDE:
			return Operation.MULTIPLY;
        case SIN:
			return Operation.ARCSIN;
        case COS:
			return Operation.ARCCOS;
        case TAN:
			return Operation.ARCTAN;
        case ARCSIN:
			return Operation.SIN;
        case ARCCOS:
			return Operation.COS;
        case ARCTAN:
			return Operation.TAN;
        case SINH:
			return Operation.ASINH;
        case COSH:
			return Operation.ACOSH;
        case TANH:
			return Operation.ATANH;
        case ASINH:
			return Operation.SINH;
        case ACOSH:
			return Operation.COSH;
        case ATANH:
			return Operation.TANH;
        case EXP:
			return Operation.LOG;
        case LOG:
			return Operation.EXP;
		}
		
		return null;
	}

	// TODO Consider locusequability



}
