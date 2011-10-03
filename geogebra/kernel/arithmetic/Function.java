/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.kernel.arithmetic;

import geogebra.kernel.GeoFunction;
import geogebra.kernel.GeoLine;
import geogebra.kernel.Kernel;
import geogebra.kernel.roots.RealRootDerivFunction;
import geogebra.kernel.roots.RealRootFunction;
import geogebra.main.Application;

import java.util.LinkedList;
import java.util.List;

/**
 * Function of one variable x that returns either a number
 * or a boolean. This depends on the expression this function
 * is based on.
 * 
 * @author Markus Hohenwarter
 */
public class Function extends FunctionNVar 
implements ExpressionValue, RealRootFunction, Functional {

	 /**
     * Creates new Function from expression where x is
     * the variable. 
     * Note: call {@link #initFunction()} after this constructor.
	 * @param expression 
     */ 
    public Function(ExpressionNode expression) {
    	super(expression);
    }
    
    
    /**
     * Creates new Function from expression where the function
     * variable in expression is already known.
     * @param exp 
     * @param fVar 
     */ 
    public Function(ExpressionNode exp, FunctionVariable fVar) {
    	super(exp, new FunctionVariable [] {fVar});
    }
    
    /**
     * Creates a Function that has no expression yet. Use setExpression() to
     * do this later.    
     * @param kernel 
     */ 
    public Function(Kernel kernel) {
       super(kernel);
       fVars = new FunctionVariable[1];
    }
    
    
    /**
     * Copy constructor
     * @param f source function
     * @param kernel
     */
    public Function(Function f, Kernel kernel) {   
        super(f.expression.getCopy(kernel));
        fVars = f.fVars; // no deep copy of function variable            
        isBooleanFunction = f.isBooleanFunction;
        isConstantFunction = f.isConstantFunction;
       
        this.kernel = kernel;
    }
    
    public ExpressionValue deepCopy(Kernel kernel) {
        return new Function(this, kernel);        
    }   
    
    /**
     * Use this method only if you really know
     * what you are doing.
     * @param exp expression
     * @param var variable
     */
    public void setExpression(ExpressionNode exp, FunctionVariable var) {
        super.setExpression(exp, new FunctionVariable[] {var});
    }
    
    final public Function getFunction() {
        return this;
    }
    
    /**
     * @return variable
     */
    public FunctionVariable getFunctionVariable() {
        return fVars[0];
    }
    
    final public String getVarString() {
    	if (fVars == null) {
    		return kernel.printVariableName("x");
    	} else {
    		return fVars[0].toString();
    	}	
    }

    /**
     * Call this function to resolve variables and init the function.
     * May throw MyError (InvalidFunction).
     */
    public void initFunction() {              	
        if (fVars == null) {
        	// try function variable x
        	fVars = new FunctionVariable[] {new FunctionVariable(kernel)};   
        }
        
        super.initFunction();
    }
    
    /**
     * Returns this function's value at position x.    
     * @param x
     * @return f(x)
     */
    public double evaluate(double x) {
    	if (isBooleanFunction) {
    		// BooleanValue
    		return evaluateBoolean(x) ? 1 : 0;
    	}
    	else {
    		// NumberValue
    		fVars[0].set(x);
    		return ((NumberValue) expression.evaluate()).getDouble();
    	}     
    }
    
    /**
     * Returns this function's value at position x.
     * (Note: use this method if isBooleanFunction() returns true.
     * @param x
     * @return f(x)
     */
    final public boolean evaluateBoolean(double x) {       
        fVars[0].set(x);
        return ((BooleanValue) expression.evaluate()).getBoolean();       
    }
    
    /**
	 * Shifts the function by vx to right and by vy up
	 * @param vx horizontal shift
	 * @param vy vertical shift
	 */
    final public void translate(double vx, double vy) {                  
        boolean isLeaf = expression.isLeaf();
        ExpressionValue left = expression.getLeft();

        // translate x
        if (!Kernel.isZero(vx)) {
            if (isLeaf && left == fVars[0]) { // special case: f(x) = x
                expression = shiftXnode(vx);            
             } else {
                //  replace every x in tree by (x - vx)
                // i.e. replace fVar with (fvar - vx)
                translateX(expression, vx);
             }
        }
         
        // translate y
        if (!Kernel.isZero(vy)) {                       
            if (isLeaf && left != fVars[0]) { // special case f(x) = constant               
                MyDouble c = ((NumberValue) expression.getLeft()).getNumber();
                c.set(kernel.checkDecimalFraction(c.getDouble() + vy));
                expression.setLeft(c);
            } else {                
                // f(x) = f(x) + vy
                translateY(vy);
            }
        }       
        
        // make sure that expression object is changed!
        // this is needed to know that the expression has changed
        if (expression.isLeaf() && expression.getLeft().isExpressionNode()) {
        	expression = new ExpressionNode( (ExpressionNode) expression.getLeft());    
        } else {
            expression = new ExpressionNode(expression);
        }
    }
    
    // replace every x in tree by (x - vx)
    // i.e. replace fVar with (fvar - vx)
    final private void translateX(ExpressionNode en, double vx) {                               
        ExpressionValue left = en.getLeft();
        ExpressionValue right = en.getRight();  
        
        // left tree
        if (left == fVars[0]) {         
            try { // is there a constant number to the right?
                MyDouble num = (MyDouble) right;
                double temp;
                switch (en.getOperation()) {
                    case ExpressionNode.PLUS :
                        temp = kernel.checkDecimalFraction(num.getDouble() - vx);                    
                        if (Kernel.isZero(temp)) {                      
                            expression = expression.replaceAndWrap(en, fVars[0]);                          
                        } else if (temp < 0) {
                            en.setOperation(ExpressionNode.MINUS);
                            num.set(-temp);
                        } else {
                            num.set(temp);
                        }
                        return;

                    case ExpressionNode.MINUS :
                        temp = kernel.checkDecimalFraction(num.getDouble() + vx);
                        if (Kernel.isZero(temp)) {
                            expression = expression.replaceAndWrap(en, fVars[0]);                      
                        } else if (temp < 0) {
                            en.setOperation(ExpressionNode.PLUS);
                            num.set(-temp);
                        } else {
                            num.set(temp);
                        }
                        return;

                    default :
                        en.setLeft(shiftXnode(vx));
                }
            } catch (Exception e) {
                en.setLeft(shiftXnode(vx));
            }   
        }
        else if (left instanceof ExpressionNode) {
            translateX((ExpressionNode) left, vx);
        }       

        // right tree
        if (right == fVars[0]) {
            en.setRight(shiftXnode(vx));
        }
        else if (right instanceof ExpressionNode) {
            translateX((ExpressionNode) right, vx);
        }       
    }
    
    // node for (x - vx)
    final private ExpressionNode shiftXnode(double vx) {
    	
    	vx = kernel.checkDecimalFraction(vx);
    	
        ExpressionNode node;        
        if (vx > 0) {
            node =
                new ExpressionNode(kernel,
                		fVars[0],
                    ExpressionNode.MINUS,
                    new MyDouble(kernel,vx));
        } else {
            node =
                new ExpressionNode(kernel,
                		fVars[0],
                    ExpressionNode.PLUS,
                    new MyDouble(kernel,-vx));
        }
        return node;
    }
    /**
     * Shifts the function by vy up
     * @param vy vertical translation
     */
    final public void translateY(double vy) {                                  
        try { // is there a constant number to the right
            MyDouble num = (MyDouble) expression.getRight();
            if (num == fVars[0]) { // right side might be the function variable
                addNumber(kernel.checkDecimalFraction(vy));
                return;
            }
            double temp;
            switch (expression.getOperation()) {
                case ExpressionNode.PLUS :
                    temp = kernel.checkDecimalFraction(num.getDouble() + vy);
                    if (Kernel.isZero(temp)) {
                        expression = expression.getLeftTree();
                    } else if (temp < 0) {
                        expression.setOperation(ExpressionNode.MINUS);
                        num.set(-temp);
                    } else {
                        num.set(temp);
                    }
                    break;

                case ExpressionNode.MINUS :
                    temp = kernel.checkDecimalFraction(num.getDouble() - vy);
                    if (Kernel.isZero(temp)) {
                        expression = expression.getLeftTree();
                    } else if (temp < 0) {
                        expression.setOperation(ExpressionNode.PLUS);
                        num.set(-temp);
                    } else {
                        num.set(temp);
                    }
                    break;                              

                default :
                    addNumber(vy);
            }
        } catch (Exception e) {         
            addNumber(vy);
        }
    }
    
    final private void addNumber(double n) {        
        if (n > 0) {
            expression =
                new ExpressionNode(kernel, 
                    expression,
                    ExpressionNode.PLUS,
                    new MyDouble(kernel,n));
        } else {
            expression =
                new ExpressionNode(kernel,
                    expression,
                    ExpressionNode.MINUS,
                    new MyDouble(kernel,-n));
        }   
    }
    
    /* ********************
     * POLYNOMIAL FACTORING
     * ********************/
     
    // remember calculated factors
    // do factoring only if expression changed
    private ExpressionNode factorParentExp;
    
    //  factors of polynomial function   
    private LinkedList<PolyFunction> symbolicPolyFactorList;
    private LinkedList<PolyFunction> numericPolyFactorList;
    private boolean symbolicPolyFactorListDefined;
    
    /**
     * Returns all non-constant polynomial factors of this function
     * relevant for root finding. A list of PolyFunction (resp. SymbolicPolyFunction) objects
     * is returned. Note: may return null if this function is no polynomial.
     * 
     * @param rootFindingSimplification for root finding factors may be simplified, e.g. sqrt(x) may be simplified to x
     * @return all non-constant polynomial factors of this function
     * 
     */
    final public LinkedList<PolyFunction> getPolynomialFactors(boolean rootFindingSimplification) { 
    	// try to get symbolic polynomial factors
    	LinkedList<PolyFunction> result = getSymbolicPolynomialFactors(rootFindingSimplification);     	          	    
    	
    	// if this didn't work try to get numeric polynomial factors
    	if (result == null) {
    		result = getNumericPolynomialFactors(rootFindingSimplification);   
    	}
    	return result;
    }
    
    /**
     * Returns all non-constant polynomial factors of the n-th derivative
     * of this function
     * relevant for root finding. A list of PolyFunction (resp. SymbolicPolyFunction) objects
     * is returned. Note: may return null if the n-th derivative is no polynomial.
     * @param n 
     * 
     * @param rootFindingSimplification for root finding factors may be simplified, e.g. sqrt(x) may be simplified to x  
     * @return all non-constant polynomial factors of the n-th derivative
     */
    final public LinkedList<PolyFunction> getSymbolicPolynomialDerivativeFactors(int n, boolean rootFindingSimplification) { 
    	Function deriv = getDerivative(n);
    	if (deriv == null)
			return null;
    	
    	// try to get symbolic polynomial factors
    	return deriv.getSymbolicPolynomialFactors(rootFindingSimplification);
    }
 
    /**
     * Tries to expand this function to a polynomial with numeric coefficients
     * and returns its n-th derivative as a PolyFunction object.
     * Note: may return null if the n-th derivative is no polynomial.
     * @param n order
     * @return derivative
     * 
     */
    final public PolyFunction getNumericPolynomialDerivative(int n) {      	
    	// we expand the numerical expression of this function (all variables are
    	// replaced by their values) and try to get a polynomial.
    	// Then we take the derivative of this polynomial.    	
    	PolyFunction poly = expandToPolyFunction(expression, false);
		if (poly != null) { // we got a polynomial
			for (int i=0; i<n; i++) {
				poly = poly.getDerivative();
			}
		}	       	
    	return poly;
    }
       
    /**
     * Returns all symbolic non-constant polynomial factors of this function
     * relevant for root finding. A list of PolyFunction (resp. SymbolicPolyFunction) objects
     * is returned. Note: may return null if this function is no polynomial.
     * 
     * @param rootFindingSimplification for root finding factors may be simplified, e.g. sqrt(x) may be simplified to x
     * @return all symbolic non-constant polynomial factors of this function
     */
    public LinkedList<PolyFunction> getSymbolicPolynomialFactors(boolean rootFindingSimplification) {       	
        if (factorParentExp != expression) { 
            // new expression
            factorParentExp = expression;
            
            if (symbolicPolyFactorList == null)
            	symbolicPolyFactorList = new LinkedList<PolyFunction>();
            else
            	symbolicPolyFactorList.clear();
            symbolicPolyFactorListDefined = 
            	addPolynomialFactors(expression, 
            			symbolicPolyFactorList, true, rootFindingSimplification);                   
        }               
        
        if (symbolicPolyFactorListDefined && symbolicPolyFactorList.size() > 0)
            return symbolicPolyFactorList;
        else
            return null;
    }
    
    /**
     * Returns all numeric non-constant polynomial factors of this function
     * relevant for root finding. A list of SymbolicPolyFunction objects
     * is returned. Note: may return null if this function is no polynomial.
     * 
     * Note: we use the values of variables here (different to getSymbolicPolynomialFactors()).
     * 
     * @param rootFindingSimplification for root finding factors may be simplified, e.g. sqrt(x) may be simplified to x
     */
    private LinkedList<PolyFunction> getNumericPolynomialFactors(boolean rootFindingSimplification) {  
    	if (numericPolyFactorList == null)
    		numericPolyFactorList = new LinkedList<PolyFunction>();
        else
        	numericPolyFactorList.clear();
        
    	boolean success = addPolynomialFactors(expression, numericPolyFactorList, false, rootFindingSimplification);
        if (success && numericPolyFactorList.size() > 0)
        	return numericPolyFactorList;
        else
        	return null;
    }

    
    /**
     * Adds all polynomial factors in ev to the given list (ev is
     * an ExpressionNode in the beginning).
     * @return false when a non-polynomial was found (e.g. sin(x))
     * @param symbolic true for symbolic coefficients, false for numeric coefficients
     * @param rootFindingSimplification for root finding factors may be simplified, e.g. sqrt(x) may be simplified to x
     */
    private boolean addPolynomialFactors(ExpressionValue ev, List<PolyFunction> l, 
    									 boolean symbolic, boolean rootFindingSimplification) {
        if (ev.isExpressionNode()) {
            ExpressionNode node = (ExpressionNode) ev;
            switch (node.operation) {
                case ExpressionNode.MULTIPLY:
                    return addPolynomialFactors(node.getLeft(), l, symbolic, rootFindingSimplification) && 
                                addPolynomialFactors(node.getRight(), l, symbolic, rootFindingSimplification);
                    
            // try some simplifications of factors for root finding                                
                case ExpressionNode.POWER:
                case ExpressionNode.DIVIDE:   
                	if (!rootFindingSimplification) break;
                	
              	  	// divide: x in denominator: no polynomial
                	// power: x in exponent: no polynomial
                	if (node.getRight().contains(fVars[0]))
						return false;

                    // power: 
                    // symbolic: non-zero constants in exponent may be omitted   
                    // numeric: non-zero values in exponent may be omitted
                    if (!symbolic || node.getRight().isConstant()) {
                    	double rightVal;
                    	try {
                    		rightVal = ((NumberValue) node.getRight().evaluate()).getDouble();           
                    	} catch (Exception e) {
                    		e.printStackTrace();
                    		return false;
                    	}                    	   
                 		if (node.operation == ExpressionNode.POWER) {                    			
                			if (Kernel.isZero(rightVal))
                				// left^0 = 1
                				return addPolynomialFactors(new MyDouble(kernel, 1), l, symbolic, rootFindingSimplification);
                			else if (rightVal > 0) 
                				// left ^ right = 0  <=>  left = 0     for right > 0
                				return addPolynomialFactors(node.getLeft(), l, symbolic, rootFindingSimplification);       
                		}                				
            			else { // division            				               				                			    
                    		if (Kernel.isZero(rightVal))
								// left / 0 = undefined	 
               					return false;
							else
								// left / right = 0  <=>  left = 0     for right != null
                    			return addPolynomialFactors(node.getLeft(), l, symbolic, rootFindingSimplification);
            			}
                    }                   
                    break;                                                             
                                                                            
                case ExpressionNode.ABS:
                case ExpressionNode.SGN:
                case ExpressionNode.SQRT:
                	if (!rootFindingSimplification) break;
                	
                    // these functions can be omitted as f(x) = 0 iff x = 0         
                    return addPolynomialFactors(node.getLeft(), l, symbolic, rootFindingSimplification);                                              
            }           
        }
        
        // if we get here we have to add the ExpressionValue ev
        // add only non constant factors that are relevant for root finding
        if (!ev.isConstant()) {
            // build the factor: expanded ev, get the coefficients and build 
            // a polynomial with them         	
            PolyFunction factor = expandToPolyFunction(ev, symbolic);
            if (factor == null)
				return false; // did not work
            l.add(factor);
        }
        return true;
    }
    
    
    /**
     * Expands the given expression and builds a PolyFunction (or SymbolicPolyFunction) object
     * with the coefficients of the resulting polynomial.    
     * @return null when node is not a polynomial
     * @param symbolic true for symbolic coefficients (SymbolicPolyFunction), false for numeric coefficients (PolyFunction)
     */
    private PolyFunction expandToPolyFunction(ExpressionValue ev, boolean symbolic) {
    	PolyFunction ret = null;
    	ExpressionNode node;
        if (ev.isExpressionNode()) {
            node = (ExpressionNode) ev;
        } else {
            // wrap expressionValue
            node = new ExpressionNode(kernel, ev);
        }
        
        // get coefficients as strings
        boolean oldUseTempVarPrefix = kernel.isUseTempVariablePrefix();
        int oldPrintForm = kernel.getCASPrintForm();
        kernel.setUseTempVariablePrefix(true);
        kernel.setCASPrintForm(ExpressionNode.STRING_TYPE_MPREDUCE);
        String function,var;
        
        //See #1322
        try {
        	function = node.getCASstring(ExpressionNode.STRING_TYPE_MPREDUCE, symbolic); 
        	var = fVars[0].toString();
        } catch (NullPointerException e) {
        	// this is not a valid polynomial           
            return null;
        } finally {
        	 kernel.setCASPrintForm(oldPrintForm);
             kernel.setUseTempVariablePrefix(oldUseTempVarPrefix);
        }
        
        String [] strCoeffs = kernel.getPolynomialCoeffs(function, var);
        
        if (strCoeffs == null)
			// this is not a valid polynomial           
            return null;
        
        // convert sring coefficients to coefficients of a SymbolicPolyFunction resp. PolyFunction
        int degree = strCoeffs.length - 1;
        if (symbolic) { 
        	// build SymbolicPolyFunction
	        SymbolicPolyFunction symbPolyFun = new SymbolicPolyFunction(degree);        
	        ExpressionNode [] symbCoeffs = symbPolyFun.getSymbolicCoeffs();                 
	        for (int i=0; i < strCoeffs.length; i++) {
	            symbCoeffs[i] = evaluateToExpressionNode(strCoeffs[i]);         
	            if (symbCoeffs[i] == null)
					return null; 
	            symbCoeffs[i].simplifyConstantIntegers();
	        }                       
	        return symbPolyFun;   
        } else { 
        	// build PolyFunction
        	try {
	        	PolyFunction polyFun = new PolyFunction(degree);                        
	  	        for (int i=0; i < strCoeffs.length; i++) {
	  	            polyFun.coeffs[i] = ((NumberValue) evaluateToExpressionNode(strCoeffs[i]).evaluate()).getDouble();         
	  	        }                       
	  	        return polyFun; 
        	} catch (Exception e) {
        		Application.debug("error in buildPolyFunction:");
        		e.printStackTrace();
        		return null;
        	}
        }
    }
    
    /**
     * Parses given String str and tries to evaluate it to an ExpressionNode.
     * Returns null if something went wrong.
     */
    private ExpressionNode evaluateToExpressionNode(String str) {
         try {
            ExpressionNode en = kernel.getParser().parseExpression(str);
            en.resolveVariables();
            return en;
         }
         catch (Exception e) {
            e.printStackTrace();
             return null;
         } 
         catch (Error e) {
            Application.debug("error in evaluateToExpressionNode: " + str);
            e.printStackTrace();
             return null;
         }
    }
    



/* ***************
 * CALULUS
 * ***************/
    
    /**
     * Returns n-th derivative of this function wrapped
     * as a GeoFunction object.
     */
    public GeoFunction getGeoDerivative(int n) {
    	if (geoDeriv == null)
    		geoDeriv = new GeoFunction(kernel.getConstruction());
    	
    	Function deriv = getDerivative(n);
    	geoDeriv.setFunction(deriv);
    	geoDeriv.setDefined(deriv != null);
    	return geoDeriv;
    }
    private GeoFunction geoDeriv;
 
    /**
     * Returns n-th derivative of this function
     * @param n order
     * @return derivative
     */
    final public Function getDerivative(int n) {
    	return getDerivative(n, true);
    }
    
    /**
     * Returns n-th derivative of this function where fractions are not kept (faster).
     * @param n order
     * @return derivative
     */
    final public Function getDerivativeNoFractions(int n) {
    	return getDerivative(n, false);
    }
    	
    final Function getDerivative(int n, boolean keepFractions) {
		// get variable string with tmp prefix, 
		// e.g. "x" becomes "ggbtmpvarx" here
		boolean isUseTempVariablePrefix = kernel.isUseTempVariablePrefix();
		kernel.setUseTempVariablePrefix(true);
		String varStr = fVars[0].toString();
		kernel.setUseTempVariablePrefix(isUseTempVariablePrefix);
    	
    	StringBuilder sb = new StringBuilder();
    	sb.append("Derivative(");
    	if (!keepFractions)
    		sb.append("Numeric(");
    	sb.append("%");
    	if (!keepFractions)
    		sb.append(")");
    	sb.append(",");
    	sb.append(varStr);
    	sb.append(",");
    	sb.append(n);
    	sb.append(")");
    	
    	return (Function) evalCasCommand(sb.toString(), true);
    }
    
    
    

    
    /**
     * Creates the difference expression (a - b) and stores the result in
     * Function c.
     * @param a 
     * @param b 
     * @param c 
     */
    final public static void difference(Function a, Function b, 
                    Function c) {
        // copy only the second function and replace b.fVar by a.fVar
        ExpressionNode left = a.expression;
        ExpressionNode right = b.expression.getCopy(a.kernel);
        
        // replace b.fVar in right by a.fVar to have only one function
        // variable in our function
        right.replaceAndWrap(b.fVars[0], a.fVars[0]);
        
        ExpressionNode diffExp= new ExpressionNode(a.kernel, left, ExpressionNode.MINUS, right);
        c.setExpression(diffExp);
        c.fVars[0] = a.fVars[0];
    }

    /**
     * Creates the difference expression (a - line) and stores the result in
     * Function c. This is needed for the intersection of function a and line ax + by + c = 0.
     * b != 0 is assumed.
     * @param f 
     * @param line 
     * @param c 
     */
    final public static void difference(Function f, GeoLine line, Function c) {     
        // build expression for line: ax + by + c = 0 (with b != 0) 
        // explicit form: line: y = -a/b x - c/b
        // we need f - line: f(x) + a/b x + c/b
        double coeffX = line.x / line.y;
        double coeffConst = line.z / line.y;
        
        // build expression f - line: f(x) + a/b x + c/b
        ExpressionNode temp;
        // f(x) + a/b * x
        if (coeffX > 0) {
            temp = new ExpressionNode(f.kernel, 
                                    f.expression, 
                                    ExpressionNode.PLUS, 
                                    new ExpressionNode(f.kernel, 
                                        new MyDouble(f.kernel, coeffX),
                                        ExpressionNode.MULTIPLY, 
                                        f.fVars[0])
                                    );      
        } else {
            temp = new ExpressionNode(f.kernel, 
                                    f.expression, 
                                    ExpressionNode.MINUS, 
                                    new ExpressionNode(f.kernel, 
                                        new MyDouble(f.kernel, -coeffX),
                                        ExpressionNode.MULTIPLY, 
                                        f.fVars[0])
                                    );
        }
        
        
        // f(x) + a/b * x + c/b
        if (coeffConst > 0) {
            temp = new ExpressionNode(f.kernel, 
                            temp, 
                            ExpressionNode.PLUS, 
                            new MyDouble(f.kernel, coeffConst)
                        );          
        } else {
            temp = new ExpressionNode(f.kernel, 
                            temp, 
                            ExpressionNode.MINUS, 
                            new MyDouble(f.kernel, -coeffConst)
                        );
        }
                
        c.setExpression(temp);
        c.fVars[0] = f.fVars[0];
    }   
    
    
    /**
     * Tries to build a RealRootDerivFunction out of this
     * function and its derivative. This can be used for root finding.
     * Note: changes to the function will not affect the returned RealRootDerivFunction.
     * @return real root function
     */
    final public RealRootDerivFunction getRealRootDerivFunction() {
        Function deriv = getDerivativeNoFractions(1);
        if (deriv == null) 
            return null;
        else 
            return new DerivFunction(this, deriv);
    }
    
    /*
     * for root finding
     */
    private class DerivFunction implements RealRootDerivFunction {
        
        private Function fun, derivative;
        private double [] ret = new double[2];
        
        DerivFunction(Function fun, Function derivative) {
            this.fun = fun;
            this.derivative = derivative;
        }
        
        public double[] evaluateDerivFunc(double x) {
            ret[0] = fun.evaluate(x);
            ret[1] = derivative.evaluate(x);
            return ret;
        }

		public double evaluate(double x) { 
			return fun.evaluate(x);
		}

		public double evaluateDerivative(double x) {
			return derivative.evaluate(x);
		}
    }
	
	/**
	 * Decides whether function includes division by expression containing function variable
	 * @return true if function includes division by variable
	 */
	public final boolean includesDivisionByVariable() {
		if (expression == null)
			return false;
		else
			return expression.includesDivisionBy(fVars[0]);
	}

	public boolean isVector3DValue() {
		return false;
	}
	
}
