/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/**
 * stores a (coefficient, variables) pair<BR>
 * example: Term("-45yx") stores coefficient -45
 * and variables "xy". Variables are sorted alphabetically.
 */
 
package geogebra.kernel.arithmetic;

import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;

import java.io.Serializable;

/**
 * A term is a pair of coefficient and variables in a Polynomial, 
 * e.g. {4, "x"}, {a, "xy"}
 */
public class Term implements Comparable, Serializable {         
	
	private static final long serialVersionUID = 1L;
    
    ExpressionValue coefficient; // hast to evaluate() to NumberValue
    private StringBuilder variables;
    private Kernel kernel;
    
    public Term(Kernel kernel, ExpressionValue coeff, String vars) {
        this(kernel, coeff, new StringBuilder(vars));
    }
    
    public Term(Kernel kernel, double coeff, String vars) {
        this(kernel, new MyDouble(kernel, coeff), new StringBuilder(vars));
    }
    
    public Term(Kernel kernel, ExpressionValue coeff, StringBuilder vars) {
        this.kernel = kernel;
        coefficient = coeff;
        variables = vars;
    }
             
    public Term(Term t) {                
        kernel = t.kernel;
        variables = new StringBuilder(t.variables.toString());
        coefficient = ExpressionNode.copy(t.coefficient, kernel);               
    }
    
    
    ExpressionValue getCoefficient() {
        return coefficient;
    }

    void setCoefficient(ExpressionValue coeff) {
        coefficient = coeff;
    }

    String getVars() {
        return variables.toString();
    }

    void setVariables(String vars) {
        variables.setLength(0);
        variables.append(vars);
    }

    void setVariables(StringBuilder vars) {
        variables.setLength(0);
        variables.append(vars);
    }

    boolean hasNoVars() {
        return (variables.length() == 0);    
    }             
    
    boolean hasIntegerCoeff() {
        return kernel.isInteger( ((NumberValue)coefficient.evaluate()).getDouble());
    }             
    
    int degree() {
        return variables.length();
    }
    
    /*
     * degree of eg x
     * xxxyy returns 3 for x, 2 for y
     */
    int degree(char var) {
    	int count = 0;
        for (int i = 0 ; i < variables.length() ; i++) {
        	if (variables.charAt(i) == var) count++;
        }
        return count;
    }
    
    /**
     * add a number to this term's coefficient
     */
    void addToCoefficient(ExpressionValue number) {
        coefficient = add(coefficient, number);
    }
    
    // return a + b
    ExpressionValue add(ExpressionValue a, ExpressionValue b) {
        boolean aconst = a.isConstant();
        boolean bconst = b.isConstant();
        double aval, bval;                        
        
        // add constant?                 
        if (aconst && bconst) {            
            aval = ((NumberValue)a.evaluate()).getDouble();
            bval = ((NumberValue)b.evaluate()).getDouble();            
            return new MyDouble(kernel, aval + bval );
        } 
        else if (aconst) {
            aval = ((NumberValue)a.evaluate()).getDouble();
            if (aval == 0.0d)
				return b;
			else {
                if (b.isExpressionNode()) {
                    ExpressionNode ben = (ExpressionNode) b;   
                    if (ben.getLeft().isConstant()) {
                        switch (ben.operation) {
                            // a + (b.left + b.right) = (a + b.left) + b.right
                            case ExpressionNode.PLUS:                             
                                return add(add(a, ben.getLeft()), ben.getRight());        
                            // a + (b.left - b.right) = (a + b.left) - b.right
                            case ExpressionNode.MINUS:                
                                return sub( add(a, ben.getLeft()), ben.getRight() );            
                        }            
                    }
                } // else                
                return new ExpressionNode(kernel, a, ExpressionNode.PLUS, b);                  
            }            
        }
        else if (bconst)
			return add(b, a); // get the constant to the left                        
		else
			return new ExpressionNode(kernel, a, ExpressionNode.PLUS, b);
    }
    
    ExpressionValue sub(ExpressionValue a, ExpressionValue b) {
        return add(a, multiply(new MyDouble(kernel, -1.0d), b));
    }        
    
    /**
     * multiply this term with another term
     */
    void multiply(Term t) {
        coefficient = multiply(coefficient, t.coefficient);
        variables.append(t.variables);
        sort(variables);        
    }        
    
    void multiply(double d) {
        multiply(new MyDouble(kernel, d));
    }
    
    /**
     * multiply this term with another term
     * return a new Term
     *
    Term mult(Term t) {         
        StringBuilder sb = new StringBuilder();
    
        // concatenate and sort (variables + t.variables)
        sb = sb.append(variables);
        sb = sb.append(t.variables);
        sort(sb);
        Term ret = new Term( coefficient, sb );
        ret.multiply(t.coefficient);        
        return ret;
    }
     */
       
    
    /**
     * multiply this term with a number
     */
    void multiply(ExpressionValue number) {                     
        coefficient = multiply(coefficient, number);                
    }      
    
    // c = a * b
    ExpressionValue multiply(ExpressionValue a, ExpressionValue b) {   
         
        // multiply constant? 
        boolean aconst = a.isConstant();
        boolean bconst = b.isConstant();
        double aval, bval;                        
        
        if (aconst && bconst) {
            aval = ((NumberValue)a.evaluate()).getDouble();
            bval = ((NumberValue)b.evaluate()).getDouble(); 
            
            return new MyDouble(kernel, aval * bval );                        
        } 
        else if (aconst) {
            aval = ((NumberValue)a.evaluate()).getDouble();
            if (aval == 0.0d)
				return new MyDouble(kernel, 0.0d);
			else if (aval == 1.0d)
				return b;
			else {
                 if (b instanceof ExpressionNode) {
                    ExpressionNode ben = (ExpressionNode) b;  
                    if (ben.getLeft().isConstant()) {
                        switch (ben.operation) {
                            // a * (b.left * b.right) = (a * b.left) * b.right
                            case ExpressionNode.MULTIPLY:                
                                return multiply(multiply(a, ben.getLeft()), ben.getRight());                                            
                            // a * (b.left / b.right) = (a * b.left) / b.right
                            case ExpressionNode.DIVIDE:                
                                return divide(multiply(a, ben.getLeft()), ben.getRight());                                            
                        }
                    }
                }
                return new ExpressionNode(kernel, a, ExpressionNode.MULTIPLY, b);                  
            }            
        }
        else if (bconst)
			// a * b = b * a
            return multiply(b, a); // get the constant to the left                                    
		else
			return new ExpressionNode(kernel, a, ExpressionNode.MULTIPLY, b);
    }
       
    /**
     * divide this term with a number
     */
    void divide(ExpressionValue number) {        
        coefficient = divide(coefficient, number);                       
    }  
    
    // c = a / b
    ExpressionValue divide(ExpressionValue a, ExpressionValue b) {                
        // divide constants 
        boolean aconst = a.isConstant();
        boolean bconst = b.isConstant();
        double aval, bval;        
        
        if (aconst && bconst) {
            aval = ((NumberValue)a.evaluate()).getDouble();
            bval = ((NumberValue)b.evaluate()).getDouble();            
            return new MyDouble(kernel, aval / bval );
        } 
        else if (aconst) {
            aval = ((NumberValue)a.evaluate()).getDouble();
            if (aval == 0.0d)
				return new MyDouble(kernel, 0.0d);
			else {
                if (b instanceof ExpressionNode) {
                    ExpressionNode ben = (ExpressionNode) b;                      
                    switch (ben.operation) {
                        // a / (b.left / b.right) = (a / b.left) * b.right                            
                        case ExpressionNode.DIVIDE:                
                            return multiply(divide(a, ben.getLeft()), ben.getRight());            
                    }                    
                }                
                return new ExpressionNode(kernel, a, ExpressionNode.DIVIDE, b);  
            }            
        }
        else if (bconst) {
            bval = ((NumberValue)b.evaluate()).getDouble();
            if (bval == 1.0d)
				return a;
			else
				return new ExpressionNode(kernel, a, ExpressionNode.DIVIDE, b);
        } else
			return new ExpressionNode(kernel, a, ExpressionNode.DIVIDE, b);
    }   
             
    // sort single characters: "yx" -> "xy"
    private void sort( StringBuilder sb ) {        
        int len = sb.length();
        char [] chVariables = new char[len];
    
        // to sort, copy characters into a char array        
        sb.getChars(0, len, chVariables, 0);
        java.util.Arrays.sort(chVariables, 0, len);
        sb.setLength(0);
        sb.append(chVariables);         
    }       
    
    public boolean equals(Object o) {
        Term t;        
        if (o instanceof Term) {
            t = (Term) o;
            return (coefficient == t.coefficient && 
                    variables.toString().equals(t.variables.toString()));
        } else
			return false;    
    }
        
    public int hashCode() {
    	assert false : "hashCode not designed";
    return 42; // any arbitrary constant will do 
    }

    boolean contains(String var) {
        return (variables.toString().indexOf(var) >= 0);    
    }
    
    public int compareTo(Object o) {
        // may throw ClassCastException        
        return ((Term) o).variables.toString().compareTo(variables.toString());
    }
    
    public String toString() {                          
        if (ExpressionNode.isEqualString(coefficient, 0, true)) 
        	return "0";
        if (ExpressionNode.isEqualString(coefficient, 1, true)){
        	if (variableString().length()>0)
        		return variableString();
        	else
        		return "1";
        }
        
        StringBuilder sb = new StringBuilder();
        String var = variableString();
        if (ExpressionNode.isEqualString(coefficient, -1, true) && var.length() > 0) { 
            sb.append('-');            
            sb.append(var);                    
        } else {
            sb.append(coeffString(coefficient));                       
            if (var != null) {
                sb.append(' ');
                sb.append(var);
            }
        }        
        return sb.toString();
    }                
        
    private String coeffString(ExpressionValue ev) {                
        if (ev instanceof GeoElement)
			return ((GeoElement)ev).getLabel();
		else if (ev instanceof ExpressionNode) {
            ExpressionNode n = (ExpressionNode) ev;
            if (n.isLeaf() || 
                ExpressionNode.opID(n) >= ExpressionNode.MULTIPLY ||
                variables.length() == 0)
				return n.toString();
			else {
                StringBuilder sb = new StringBuilder();
                sb.append('(');
                sb.append(n);
                sb.append(')');
                return sb.toString();
            }            
        } else
			return ev.toString();                       
    } 
    
    private String variableString() {        
        switch (variables.length()) {            
            case 1: return variables.toString();
            case 2:
                String str = variables.toString();
                if (str.equals("xx")) return "x\u00b2";
                if (str.equals("yy")) return "y\u00b2";
                if (str.equals("xy")) return "xy";        
            default: return "";
        }        
    }
} // end of class Term
