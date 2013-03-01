/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.common.kernel.arithmetic;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.Traversing.GeoDummyReplacer;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App;
import geogebra.common.plugin.Operation;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * stores left and right hand side of an equation as
 * Exprssions
 */
public class Equation extends ValidExpression {

    private ExpressionNode lhs;
    private ExpressionNode rhs;
    
    private Polynomial leftPoly, rightPoly; // polynomial in normalForm   
    private Polynomial normalForm; // polynomial in normalForm
    private boolean isFunctionDependent; //Equation depends (non-constant) on functions (set in InitEquation)
    private Kernel kernel;
   
    /** check whether ExpressionNodes are evaluable to instances of Polynomial
     * or NumberValue and build an Equation out of them
     * @param kernel kernel
     * @param lhs LHS
     * @param rhs RHS
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
    
    /**
     * @return RHS of this equation
     */
    public ExpressionNode getRHS() {
    	return rhs;
    }
    
    /**
     * @param rhs new RHS 
     */
    public void setRHS(ExpressionNode rhs){
    	if(rhs != null)
    		this.rhs = rhs;
    }
    
    /**
     * @return LHS of this equation
     */
    public ExpressionNode getLHS() {
    	return lhs;
    }
    
    /**
     * @param lhs new LHS
     */
    public void setLHS(ExpressionNode lhs){
    	if(lhs != null)
    		this.lhs = lhs;
    }
      
    private boolean forcePlane = false, forceLine = false;
    private boolean forceConic = false, forceImplicitPoly = false ;
 

    /**
     * Force this to evaluate to line
     */
    public void setForceLine() {
        // this expression should be considered as a line, not a plane
        forceLine = true;
    }
    
    /**
     * @return true if this is forced to evaluate to line
     */
    final public boolean isForcedLine() {
    	return forceLine;
    }    
    
    /**
     * Force this to evaluate to plane
     */
    public void setForcePlane() {
        // this expression should be considered as a plane, not a line
        forcePlane = true;
    }
    
    /**
     * @return true if this is forced to evaluate to plane
     */
    final public boolean isForcedPlane() {
    	return forcePlane;
    }
    
    /**
     * @return true if this is forced to evaluate to conic
     */
    public boolean isForcedConic() {
		return forceConic;
	}

	/**
	 * Force this to evaluate to conic
	 */
	public void setForceConic() {
		this.forceConic = true;
	}
	
	 /**
	 * @return true if this is forced to evaluate to implicit poly
	 */
	public boolean isForcedImplicitPoly() {
			return forceImplicitPoly;
	}

	/**
	 * Force this to evaluate to implicit poly
	 */
	public void setForceImplicitPoly() {
		this.forceImplicitPoly = true;
	}

	/**
     * Adds/subtracts/muliplies/divides ev to this equation to get lhs + ev = rhs = ev
	 * @param operation operation to apply
	 * @param ev  other operand
	 * @param switchOrder true to compute other * this
     */
    public void applyOperation(Operation operation, ExpressionValue ev, boolean switchOrder) {
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
    	traverse(GeoDummyReplacer.getReplacer("x", new Polynomial(kernel, "x")));
    	traverse(GeoDummyReplacer.getReplacer("y", new Polynomial(kernel, "y")));
    	traverse(GeoDummyReplacer.getReplacer("z", new Polynomial(kernel, "z")));
           
        // resolve variables in lhs         
        if (lhs.isLeaf() && lhs.getLeft().isVariable()) {
        	// avoid auto creation of GeoElement when lhs is a single variable
            // e.g. A4 = x^2
        	Variable leftVar = (Variable) lhs.getLeft();
        	lhs.setLeft(leftVar.resolve(false,true)); // don't allow auto creation of variables
        } 
        else {
        	// standard case for lhs
        	lhs.resolveVariables(true);
        }
     
        // resolve variables in rhs
        rhs.resolveVariables(true);
        
        // build normal form polynomial        
        // copy the expression trees
        ExpressionNode leftEN  = lhs.getCopy(kernel);
        ExpressionNode rightEN = rhs.getCopy(kernel);
        
 
        // ensure that they only consist of polynomials
        leftEN.makePolynomialTree(this);
        rightEN.makePolynomialTree(this);		

        // simplify the both sides to single polynomials
        leftPoly  = (Polynomial) leftEN.evaluate(StringTemplate.defaultTemplate);
        rightPoly = (Polynomial) rightEN.evaluate(StringTemplate.defaultTemplate);	      
        		
        // bring to normal form left - right = 0
        normalForm = new Polynomial(kernel, rightPoly);
        normalForm.multiply(-1.0d);
        normalForm.add(leftPoly);             		   		   
    }
    
    /**
     * @param isFunctionDependent true iff contains functions
     */
    public void setFunctionDependent(boolean isFunctionDependent) {
		this.isFunctionDependent = isFunctionDependent;
	}

	/**
	 * @return true iff contains functions
	 */
	public boolean isFunctionDependent() {
		return isFunctionDependent;
	}

	/**
	 * @return LHS-RHS
	 */
	public Polynomial getNormalForm() {        
        return normalForm;
    }           
                
    /**
     *  @return the degree of the equation's normalform
     *  (max length of variables in a Term of the normalform)
     */
    public int degree() {        
        return normalForm.degree();
    } 
    
    /**
     * @return the max degree on a single var, eg 3 for x^3 y^2
     */
    public int singleDegree() {        
        return normalForm.singleDegree();
    } 
    /**
     * @param variables variable string
     * @return coefficient
     */
    public ExpressionValue getCoefficient(String variables) {         
        return normalForm.getCoefficient(variables);        
    }
    
    /**
     * @param variables variable string
     * @return coefficient
     */
    public double getCoeffValue(String variables) { 
        ExpressionValue ev = getCoefficient(variables);
        
        try {
            NumberValue nv = (NumberValue) ev;
            return nv.getDouble();
        } catch (Exception e) {
            App.debug("getCoeffValue("+variables+") failed:" + e);
            return Double.NaN;
        }
    }
    
    /**
     * @return GeoElement variables
     */
    final public GeoElement [] getGeoElementVariables() {
        Set<GeoElement> varSet;
        Set<GeoElement> leftVars = lhs.getVariables();
		Set<GeoElement> rightVars = rhs.getVariables();
		if (leftVars == null) {
			varSet=rightVars;
		} else if (rightVars == null) {
			varSet=leftVars;
		} else {
			leftVars.addAll(rightVars);
			varSet=leftVars;
		}
		if (varSet==null){
			return new GeoElement[0];
		}
        
        Iterator<GeoElement> i = varSet.iterator();        
        GeoElement [] ret = new GeoElement[varSet.size()];
        int j=0;
        while (i.hasNext()) {
            ret[j++] = i.next();
        }                
        return ret;
    }
    
    
    /**
     * @param var variable in which this could be explicit
     * @return true if this Equation is explicit
     * (lhs is "+1var" and rhs does not contain var)
     * or (rhs is "+1var" and lhs does not contain var)
     */
    public boolean isExplicit(String var) {  
        Polynomial lhsp = leftPoly;
        Polynomial rhsp = rightPoly;
        
        // var = ... || ... = var
        return (    lhsp.length() == 1 && 
                    lhsp.getCoefficient(var).evaluateNum().getDouble() 
                        == 1 && 
                    !rhsp.contains(var)                  ) 
                || 
               (    rhsp.length() == 1 && 
                    rhsp.getCoefficient(var).evaluateNum().getDouble() 
                        == 1 && 
                    !lhsp.contains(var)                  ) ;
    }
    
    /**
     * @return true if this Equation is implicit (not explicit)
     */
    public boolean isImplicit() {
        return !isExplicit("x") && !isExplicit("y") && !isExplicit("z");
    }

	public boolean contains(ExpressionValue ev) {
		return lhs.contains(ev) || rhs.contains(ev);
	}

	public ExpressionValue deepCopy(Kernel kernel1) {
		return new Equation(kernel1, lhs.getCopy(kernel1), rhs.getCopy(kernel1));
	}

	@Override
	public ExpressionValue evaluate(StringTemplate tpl) {		
		boolean oldFlag = kernel.getConstruction().isSuppressLabelsActive();
		kernel.getConstruction().setSuppressLabelCreation(true);
		GeoElement ge = kernel.getAlgebraProcessor().processEquation(this)[0];
		kernel.getConstruction().setSuppressLabelCreation(oldFlag);
		return ge;
	}


	public HashSet<GeoElement> getVariables() {
		HashSet<GeoElement> leftVars = lhs.getVariables();
        HashSet<GeoElement> rightVars = rhs.getVariables();        
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

	public void resolveVariables(boolean forEquation) {    
        lhs.resolveVariables(forEquation);
        rhs.resolveVariables(forEquation);		
	}

	public String toLaTeXString(boolean symbolic,StringTemplate tpl) {
		StringBuilder sb = new StringBuilder();
	        
        if (lhs != null) 
        	sb.append(lhs.toLaTeXString(symbolic,tpl));
        else 
        	sb.append('0');
        sb.append(" = ");
        
        if (rhs != null) 
        	sb.append(rhs.toLaTeXString(symbolic,tpl));
        else sb.append('0');
        return sb.toString();
	}

	@Override
	final public String toValueString(StringTemplate tpl) {
		StringBuilder sb = new StringBuilder();
        
        if (lhs != null) 
        	sb.append(lhs.toValueString(tpl));
        else 
        	sb.append('0');
        
        // equal sign
        switch (tpl.getStringType()){
			case MATH_PIPER:
		        sb.append(" == ");
		        break;
				
			default:	       	        
		        sb.append(" = ");	        
        }
        
        if (rhs != null) 
        	sb.append(rhs.toValueString(tpl));
        else sb.append('0');
        return sb.toString();
	}
	
	@Override
	public String toString(StringTemplate tpl) {
        StringBuilder sb = new StringBuilder();
        
        // left hand side
        if (lhs != null) 
        	sb.append(lhs.toString(tpl));
        else 
        	sb.append('0');
        
        // equal sign
        switch (tpl.getStringType()){
			case MATH_PIPER:
		        sb.append(" == ");
		        break;
		        
			default:	       	        
		        sb.append(" = ");	        
        }
        
        // right hand side
        if (rhs != null) 
        	sb.append(rhs.toString(tpl));
        else
        	sb.append('0');
        
        return sb.toString();
    }
	
	 @Override
	public String getAssignmentOperator() {
		 return ": ";
	 }
	 
	 @Override
	public String getAssignmentOperatorLaTeX() {
		 return ": \\, ";
	 }

	public boolean isVector3DValue() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public String toOutputValueString(StringTemplate tpl) {
		return toValueString(tpl);
	}
	
	public Kernel getKernel() {
		return kernel;
	}


	
	@Override
	public ExpressionValue traverse(Traversing t) {
		ExpressionValue v = t.process(this);
		lhs = lhs.traverse(t).wrap();
		rhs = rhs.traverse(t).wrap();
		return v;
	}
	
	@Override
	public boolean inspect(Inspecting t){
		return t.check(this) || lhs.inspect(t) || rhs.inspect(t);
	}
 
} // end of class Equation
