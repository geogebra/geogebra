/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel.arithmetic;

import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.main.Application;

import java.util.HashSet;
import java.util.Iterator;

/**
 * stores left and right hand side of an equation as
 * Exprssions
 */
public class Equation extends ValidExpression implements ReplaceableValue {

    protected ExpressionNode lhs;
    protected ExpressionNode rhs;
    
    private Polynomial leftPoly, rightPoly; // polynomial in normalForm   
    private Polynomial normalForm; // polynomial in normalForm
    private boolean isFunctionDependent; //Equation depends (non-constant) on functions (set in InitEquation)
    protected Kernel kernel;
   
    /** check whether ExpressionNodes are evaluable to instances of Polynomial
     * or NumberValue and build an Equation out of them
     */
    public Equation(Kernel kernel, ExpressionValue lhs, ExpressionValue rhs) {
    	if (lhs.isExpressionNode())
    		this.lhs = (ExpressionNode) lhs;
    	else
    		this.lhs = new ExpressionNode(kernel, lhs);
    	
    	if (rhs.isExpressionNode())
    		this.rhs = (ExpressionNode) rhs;
    	else
    		this.rhs = new ExpressionNode(kernel, rhs);
    
    	this.kernel = kernel; 
    	isFunctionDependent=false;
    }  
    
    public ExpressionNode getRHS() {
    	return rhs;
    }
    
    public ExpressionNode getLHS() {
    	return lhs;
    }
    
    
    
    private boolean forcePlane = false, forceLine = false;
    private boolean forceConic = false, forceImplicitPoly = false ;
 

    public void setForceLine() {
        // this expression should be considered as a line, not a plane
        forceLine = true;
    }
    
    final public boolean isForcedLine() {
    	return forceLine;
    }    
    
    public void setForcePlane() {
        // this expression should be considered as a plane, not a line
        forcePlane = true;
    }
    
    final public boolean isForcedPlane() {
    	return forcePlane;
    }
    
    public boolean isForcedConic() {
		return forceConic;
	}

	public void setForceConic() {
		this.forceConic = true;
	}
	
	 public boolean isForcedImplicitPoly() {
			return forceImplicitPoly;
	}

	public void setForceImplicitPoly() {
		this.forceImplicitPoly = true;
	}

	/**
     * Adds/subtracts/muliplies/divides ev to this equation to get lhs + ev = rhs = ev
     */
    public void applyOperation(int operation, ExpressionValue ev, boolean switchOrder) {
    	ExpressionValue left, right;
    	
    	if (ev instanceof Equation) {
    		Equation equ = (Equation) ev;
    		left = equ.lhs;
        	right = equ.rhs; 
    	} else {
    		left = ev;
        	right = ev;     		
    	}
    	
    	if (switchOrder) {
    		// ev <operation> equ
    		lhs = new ExpressionNode(kernel, left, operation, lhs);
    		rhs = new ExpressionNode(kernel, right, operation, rhs);
    	} 
    	else {
    		// equ <operation> ev
    		lhs = new ExpressionNode(kernel, lhs, operation, left);
    		rhs = new ExpressionNode(kernel, rhs, operation, right);
    	}
    	
    }
    
    
    /**
     * Call this method to check that this is a valid equation.
     * May throw MyError (InvalidEquation).     
     */
    public void initEquation() {
 /*   	ExpressionNode en=lhs.getCopy(kernel);
    	en.makePolynomialTree();
//    	en.evaluate();
    	lhs=en; */
//        boolean valid = lhs.includesPolynomial() || rhs.includesPolynomial();
//
//        if (!valid)            
//			throw new MyError(kernel.getApplication(), "InvalidEquation");      
    	

    	// replace GeoDummyVariables for "x", "y", "z" which may be coming from CAS view
    	replaceGeoDummyVariables("x", new Polynomial(kernel, "x"));
		replaceGeoDummyVariables("y", new Polynomial(kernel, "y"));
		replaceGeoDummyVariables("z", new Polynomial(kernel, "z"));
           
        // resolve variables in lhs         
        if (lhs.isLeaf() && lhs.getLeft().isVariable()) {
        	// avoid auto creation of GeoElement when lhs is a single variable
            // e.g. A4 = x^2
        	Variable leftVar = (Variable) lhs.getLeft();
        	lhs.setLeft(leftVar.resolve(false)); // don't allow auto creation of variables
        } 
        else {
        	// standard case for lhs
        	lhs.resolveVariables();
        }
     
        // resolve variables in rhs
        rhs.resolveVariables();
        
        // build normal form polynomial        
        // copy the expression trees
        ExpressionNode leftEN  = lhs.getCopy(kernel);
        ExpressionNode rightEN = rhs.getCopy(kernel);
        
 
        // ensure that they only consist of polynomials
        leftEN.makePolynomialTree(this);
        rightEN.makePolynomialTree(this);		

        // simplify the both sides to single polynomials
        leftPoly  = (Polynomial) leftEN.evaluate();
        rightPoly = (Polynomial) rightEN.evaluate();	      
        		
        // bring to normal form left - right = 0
        normalForm = new Polynomial(kernel, rightPoly);
        normalForm.multiply(-1.0d);
        normalForm.add(leftPoly);             		   		   
    }
    
    public void setFunctionDependent(boolean isFunctionDependent) {
		this.isFunctionDependent = isFunctionDependent;
	}

	public boolean isFunctionDependent() {
		return isFunctionDependent;
	}

	public Polynomial getNormalForm() {        
        return normalForm;
    }           
                
    /**
     *  returns the degree of the equation's normalform
     *  (max length of variables in a Term of the normalform)
     */
    public int degree() {        
        return normalForm.degree();
    } 
    
    /**
     *  returns the max degree on a single var, eg 3 for x^3 y^2
     */
    public int singleDegree() {        
        return normalForm.singleDegree();
    } 
    
    public ExpressionValue getCoefficient(String variables) {         
        return normalForm.getCoefficient(variables);        
    }
    
    public double getCoeffValue(String variables) { 
        ExpressionValue ev = getCoefficient(variables);
        
        try {
            NumberValue nv = (NumberValue) ev;
            return nv.getDouble();
        } catch (Exception e) {
            Application.debug("getCoeffValue("+variables+") failed:" + e);
            return Double.NaN;
        }
    }
    
    final public GeoElement [] getGeoElementVariables() {
        HashSet varset = new HashSet();
        try { varset.addAll(lhs.getVariables()); } catch (Exception e) {e.printStackTrace();}
        try { varset.addAll(rhs.getVariables()); } catch (Exception e) {e.printStackTrace();}
        
        Iterator i = varset.iterator();        
        GeoElement [] ret = new GeoElement[varset.size()];
        int j=0;
        while (i.hasNext()) {
            ret[j++] = (GeoElement) i.next();
        }                
        return ret;
    }
    
    final public boolean isIndependent() {
        GeoElement [] vars = getGeoElementVariables();
        return (vars == null || vars.length == 0);
    }
     
    /**
     * returns true if this Equation is explicit
     * (lhs is "+1y" and rhs does not contain y)
     * or (rhs is "+1y" and lhs does not contain y)
     */
    public boolean isExplicit(String var) {  
        Polynomial lhs = leftPoly;
        Polynomial rhs = rightPoly;
        
        // var = ... || ... = var
        return (    lhs.length() == 1 && 
                    ((NumberValue)lhs.getCoefficient(var).evaluate()).getDouble() 
                        == 1 && 
                    !rhs.contains(var)                  ) 
                || 
               (    rhs.length() == 1 && 
                    ((NumberValue)rhs.getCoefficient(var).evaluate()).getDouble() 
                        == 1 && 
                    !lhs.contains(var)                  ) ;
    }
    
    /**
     * returns true if this Equation is implicit (not explicit)
     */
    public boolean isImplicit() {
        return !isExplicit("x") && !isExplicit("y") && !isExplicit("z");
    }

	public boolean contains(ExpressionValue ev) {
		return lhs.contains(ev) || rhs.contains(ev);
	}

	public ExpressionValue deepCopy(Kernel kernel) {
		return new Equation(kernel, lhs.getCopy(kernel), rhs.getCopy(kernel));
	}

	public ExpressionValue evaluate() {		
		return null;
	}

	public HashSet getVariables() {
		HashSet leftVars = lhs.getVariables();
        HashSet rightVars = rhs.getVariables();        
        if (leftVars == null) {
        	return rightVars;        		
        } 
        else if (rightVars == null) {
        	return leftVars;
        }
        else {        	
        	leftVars.addAll(rightVars);        	
        	return leftVars;
        }     
	}

	public boolean isBooleanValue() {
		return false;
	}

	public boolean isConstant() {
		return lhs.isConstant() && rhs.isConstant();
	}

	public boolean isExpressionNode() {	
		return false;
	}

	public boolean isLeaf() {
		return false;
	}

	public boolean isListValue() {
		return false;
	}

	public boolean isNumberValue() {
		return false;
	}

	public boolean isPolynomialInstance() {
		return false;
	}

	public boolean isTextValue() {		
		return false;
	}

	public boolean isVectorValue() {
		return false;
	}

	public void resolveVariables() {    
        lhs.resolveVariables();
        rhs.resolveVariables();		
	}

	public String toLaTeXString(boolean symbolic) {
		StringBuilder sb = new StringBuilder();
	        
        if (lhs != null) 
        	sb.append(lhs.toLaTeXString(symbolic));
        else 
        	sb.append('0');
        sb.append(" = ");
        
        if (rhs != null) 
        	sb.append(rhs.toLaTeXString(symbolic));
        else sb.append('0');
        return sb.toString();
	}

	final public String toValueString() {
		StringBuilder sb = new StringBuilder();
        
        if (lhs != null) 
        	sb.append(lhs.toValueString());
        else 
        	sb.append('0');
        
        // equal sign
        switch (kernel.getCASPrintForm()){
			case ExpressionNode.STRING_TYPE_MATH_PIPER:
		        sb.append(" == ");
		        break;
				
			default:	       	        
		        sb.append(" = ");	        
        }
        
        if (rhs != null) 
        	sb.append(rhs.toValueString());
        else sb.append('0');
        return sb.toString();
	}
	
	public String toString() {
        StringBuilder sb = new StringBuilder();
        
        // left hand side
        if (lhs != null) 
        	sb.append(lhs);
        else 
        	sb.append('0');
        
        // equal sign
        switch (kernel.getCASPrintForm()){
			case ExpressionNode.STRING_TYPE_MATH_PIPER:
		        sb.append(" == ");
		        break;
		        
			default:	       	        
		        sb.append(" = ");	        
        }
        
        // right hand side
        if (rhs != null) 
        	sb.append(rhs);
        else
        	sb.append('0');
        
        return sb.toString();
    }
	
	 public String getAssignmentOperator() {
		 return ": ";
	 }
	 
	 public String getAssignmentOperatorLaTeX() {
		 return ": \\, ";
	 }

	public boolean isVector3DValue() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public String toOutputValueString() {
		return toValueString();
	}

	public ExpressionValue replace(ExpressionValue oldOb, ExpressionValue newOb) {
		lhs = lhs.replaceAndWrap(oldOb, newOb);
		rhs = rhs.replaceAndWrap(oldOb, newOb);
        return this;
    }
	
	/**
	 * Looks for GeoDummyVariable objects that hold String var in the tree and replaces
	 * them by their newOb.
	 * @return whether replacement was done
	 */
	public boolean replaceGeoDummyVariables(String var, ExpressionValue newOb) {
		boolean didReplacement = lhs.replaceGeoDummyVariables(var, newOb);
		didReplacement = rhs.replaceGeoDummyVariables(var, newOb) || didReplacement;
		return didReplacement;
	}
	
	public Kernel getKernel() {
		return kernel;
	}
 
} // end of class Equation
