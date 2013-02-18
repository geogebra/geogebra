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
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.MyError;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/**
 * An Polynomial is a list of Terms
 */

public class Polynomial extends ValidExpression implements Serializable
		{

	private static final long serialVersionUID = 1L;

	private ArrayList<Term> terms = new ArrayList<Term>();
	private Kernel kernel;

	/**
	 * @param kernel kernel
	 */
	public Polynomial(Kernel kernel) {
		this.kernel = kernel;
	}

	/**
	 * @param kernel kernel
	 * @param t single term
	 */
	public Polynomial(Kernel kernel, Term t) {
		this(kernel);
		terms.add(t);
	}

	/**
	 * @param kernel kernel
	 * @param vars variables string (to create one term)
	 */
	public Polynomial(Kernel kernel, String vars) {
		this(kernel);
		terms.add(new Term(kernel, 1.0d, vars));
	}

	/**
	 * @param kernel kernel
	 * @param poly polynomial to copy
	 */
	public Polynomial(Kernel kernel, Polynomial poly) {
		this(kernel);
		// Application.debug("poly copy constructor input: " + poly);
		for (int i = 0; i < poly.length(); i++) {
			append(new Term(poly.terms.get(i),kernel));
		}
		// Application.debug("poly copy constructor output: " + this);
	}

	public ExpressionValue deepCopy(Kernel kernel1) {
		return new Polynomial(kernel1, this);
	}

	/**
	 * @param i index
	 * @return i-th term
	 */
	public Term getTerm(int i) {
		return terms.get(i);
	}

	/**
	 * @return number of terms
	 */
	public int length() {
		return terms.size();
	}

	/**
	 * @return true if there are no terms
	 */
	boolean isEmpty() {
		return (terms.size() == 0);
	}

	/**
	 * Returns true if this polynomial equals "1 var"
	 * @param var variable name
	 * @return true if this polynomial equals "1 var"
	 */
	boolean isVar(String var) {
		if (length() != 1)
			return false;
		try {
			Term t = terms.get(0);
			return (t.getVars().equals(var) && t.getCoefficient().isConstant() && t
					.getCoefficient().evaluateNum().getDouble() == 1.0);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * append a single term
	 */
	private void append(Term t) {
		terms.add(t);
	}

	/**
	 * add another Polynomial
	 * @param e addend
	 */
	public void add(Polynomial e) {
		for (int i = 0; i < e.length(); i++) {
			append(e.getTerm(i));
		}
		simplify();
	}

	/**
	 * subtract another Polynomial
	 * @param e subtrahend
	 */
	public void sub(Polynomial e) {
		Polynomial temp = new Polynomial(kernel, e);
		temp.multiply(-1.0d);
		add(temp); // append -e
	}

	/**
	 * add a Number
	 * @param number constant addend
	 */
	public void add(ExpressionValue number) {
		append(new Term(number, ""));
		simplify(); // add up parts with same variables
	}

	/**
	 * subtract a Number
	 * @param number constant subtrahend
	 */
	public void sub(ExpressionValue number) {
		Term subTerm = new Term(number, "");
		subTerm.multiply(new MyDouble(kernel,-1.0d));
		append(subTerm);
		simplify(); // add up parts with same variables
	}

	/**
	 * multiply with another Polynomial store result in this Polynomial
	 * @param e factor
	 */
	public void multiply(Polynomial e) {
		ArrayList<Term> temp = new ArrayList<Term>();
		int i, j;
		Term ti, newTerm;

		// multiply every term of this Polynomial
		// with every term of Polynomial e
		for (i = 0; i < length(); i++) {
			ti = getTerm(i);
			for (j = 0; j < e.length(); j++) {
				newTerm = new Term(ti,kernel);
				newTerm.multiply(e.getTerm(j));
				temp.add(newTerm);
			}
		}
		terms = temp;
		simplify();
	}

	/**
	 * multiply every term with a double store result in this Polynomial
	 * @param number constant factor
	 */
	public void multiply(ExpressionValue number) {
		for (int i = 0; i < length(); i++) {
			terms.get(i).multiply(number);
		}
	}

	/**
	 * divide every term with a ExpressionValue store result in this Polynomial
	 * @param number constant divisor
	 */
	public void divide(ExpressionValue number) {
		for (int i = 0; i < length(); i++) {
			getTerm(i).divide(number);
		}
	}

	/**
	 * divides through a polynomial's constant coefficient
	 * @param poly polynomial
	 */
	public void divide(Polynomial poly) {
		divide(poly.getConstantCoefficient());
	}

	/**
	 * multiply every term with a double store result in this Polynomial
	 * @param d constant factor
	 */
	public void multiply(double d) {
		multiply(new MyDouble(kernel, d));
	}

	/**
	 * compute Polynomial^power store result in this Polynomial
	 * @param p exponent
	 */
	public void power(int p) {
		if (p == 0) {
			terms.clear(); // drop everything
			append(new Term(new MyDouble(kernel, 1), ""));
			return;
		}

		Polynomial exp = new Polynomial(kernel, this);
		for (int i = 0; i < p - 1; i++) {
			multiply(exp);
		}
	}

	/**
	 * multiply with another Polynomial return result as Polynomial
	 * 
	 * public Polynomial mult(Polynomial e) { Polynomial exp = (Polynomial)
	 * Util.copy(this); exp.multiply(e); return exp; }
	 **/

	/**
	 * multiply every term with a double store result in this Polynomial
	 * 
	 * public Polynomial mult(double d) { Polynomial exp = new Polynomial(this);
	 * exp.multiply(d); return exp; }
	 **/

	/**
	 * returns true if all terms of this epression are constant
	 * @return true if all terms of this epression are constant
	 */
	public boolean hasOnlyConstantCoeffs() {
		simplify();
		boolean isConst = true;
		Iterator<Term> i = terms.iterator();

		while (i.hasNext()) {
			isConst = isConst && i.next().hasNoVars();
		}
		return isConst;
	}

	/**
	 * @return true iff all coefficients are integer constants
	 */
	public boolean isInteger() {
		boolean isInt = true;
		Term t;
		Iterator<Term> i = terms.iterator();
		while (i.hasNext()) {
			t = i.next();
			isInt = isInt
					&& t.hasNoVars()
					&& Kernel.isInteger(t.getCoefficient()
							.evaluateNum().getDouble());
		}
		return isInt;
	}

	/**
	 * returns the sum of constant numbers in this Polynomial <BR>
	 * returns 0 if there is no constant number
	 * @return the sum of constant numbers in this Polynomial
	 */
	public ExpressionValue getConstantCoefficient() {
		// Constants are coefficients without variables
		return getCoefficient("");
	}

	/**
	 * returns the sum of coefficients of variables in this Polynomial <BR>
	 * returns 0 if variable does not occur <BR>
	 * example: 3x -5y getCoefficient("y") returns -5.0 <BR>
	 * 3x -72zz +5y +3zz getCoefficient("zz") returns -69.0 <BR>
	 * @param variables variables string
	 * @return coefficient
	 */
	public ExpressionValue getCoefficient(String variables) {
		Term t, newTerm = new Term(new MyDouble(kernel, 0.0), variables);

		// add all coefficients of the wanted variables
		for (int i = 0; i < length(); i++) {
			t = getTerm(i);
			if (t.getVars().equals(variables))
				newTerm.addToCoefficient(t.coefficient);
		}
		return newTerm.coefficient;
	}

	/**
	 * @param variables variables string
	 * @return coefficient value
	 */
	public double getCoeffValue(String variables) {
		return getCoefficient(variables).evaluateNum().getDouble();
	}

	/**
	 * @return value of constant coefficient
	 */
	public double getConstantCoeffValue() {
		return getCoeffValue("");
	}

	/**
	 * simplifies this Polynomial so that every variable only occurs once.
	 * example: simplify() on { (4,"xxy"), (7,"xy"), (-84.0,"xx"), (3,"xy") })
	 * changes the Polynomial to { (4,"xxy"), (10,"xy"), (-84.0,"xx") }
	 */
	void simplify() {
		// Application.debug("simplify " + this);
		ArrayList<Term> list;
		Object[] t;
		Term ti, tj;
		String vars;
		int i, j, len;

		list = new ArrayList<Term>(); // for the simplified terms
		t = terms.toArray(); // copy term references to array
		len = t.length;

		// terms may contain terms with same variables
		// example: {3 x, 5 y, -7 x} should be simplified to {-4 x, 5 y}
		for (i = 0; i < len; i++) {
			ti = (Term) t[i];
			if (ti != null) {
				vars = ti.getVars();
				// search for terms with same variable part
				for (j = i + 1; j < len; j++) {
					tj = (Term) t[j];
					if (tj != null && vars.equals(tj.getVars())) {
						ti.addToCoefficient(tj.coefficient);
						t[j] = null;
					}
				}

				if (!ti.coefficient.evaluate(StringTemplate.defaultTemplate).isNumberValue())
					throw new MyError(kernel.getApplication().getLocalization(), ti.coefficient
							.evaluate(StringTemplate.defaultTemplate).toString(StringTemplate.defaultTemplate));

				// add simplified term to list
				if (!ti.coefficient.isConstant()
						|| ti.coefficient.evaluateNum()
								.getDouble() != 0.0) {
					list.add(ti);
				}
			}
		}

		// if nothing is left, keep a term with 0
		if (list.size() == 0) {
			list.add(new Term(new MyDouble(kernel, 0.0), ""));
		}

		// sort the list
		// java.util.Collections.sort( list );
		terms = list;
		// Application.debug("simplified to " + this);
	}

	/**
	 * @param var variable name
	 * @return true iff contains var
	 */
	boolean contains(String var) {
		Iterator<Term> i = terms.iterator();
		while (i.hasNext()) {
			if (i.next().contains(var))
				return true;
		}
		return false;
	}

	/**
	 * @return the degree of the Polynomial (max length of variables in a Term)
	 */
	public int degree() {
		// a quadratic Polynomial may only have terms with one or two variables
		// or constant terms
		int deg = 0;
		int varLen;

		if (terms.size() == 0)
			return -1;

		Iterator<Term> i = terms.iterator();
		while (i.hasNext()) {
			varLen = i.next().degree();
			if (varLen > deg)
				deg = varLen;
		}
		return deg;
	}

	/**
	 * eg isFreeOf('z') to check no terms containing z, z^2 etc
	 * @param var variable name
	 * @return true if does not contain var
	 */
	public boolean isFreeOf(char var) {
		if (terms.size() == 0)
			return true;

		Iterator<Term> i = terms.iterator();
		while (i.hasNext()) {
			Term t = i.next();
			if (t.degree(var) > 0)
				return false;
		}

		return true;

	}

	/**
	 * @return 3 for eg x^3 y^2
	 */
	public int singleDegree() {
		// a quadratic Polynomial may only have terms with one or two variables
		// or constant terms
		int deg = 0;
		int varLen;

		if (terms.size() == 0)
			return -1;

		Iterator<Term> i = terms.iterator();
		while (i.hasNext()) {
			Term t = i.next();
			varLen = t.degree('x');
			if (varLen > deg)
				deg = varLen;

			varLen = t.degree('y');
			if (varLen > deg)
				deg = varLen;

			varLen = t.degree('z');
			if (varLen > deg)
				deg = varLen;
		}
		return deg;
	}

	@Override
	public String toString(StringTemplate tpl) {
		int size = terms.size();
		if (size == 0)
			return null;

		StringBuilder sb = new StringBuilder();
		String termStr;
		boolean first = true;

		for (int i = 0; i < size; i++) {
			termStr = terms.get(i).toString(tpl);
			if (termStr != null && termStr.length() > 0) {
				if (first) {
					sb.append(termStr);
					first = false;
				} else {
					if (termStr.charAt(0) == '-') {
						sb.append(" - ");
						sb.append(termStr.substring(1));
					} else {
						sb.append(" + ");
						sb.append(termStr);
					}
				}
			}
		}

		return sb.toString();
	}

	@Override
	public String toValueString(StringTemplate tpl) {
		return toString(tpl);
	}

	final public String toLaTeXString(boolean symbolic,StringTemplate tpl) {
		return toString(tpl);
	}

	/**
	 * 
	 * @return Coefficient matrix of this polynomial (in x and y)
	 */
	public ExpressionValue[][] getCoeff() {
		simplify();
		Iterator<Term> it = terms.iterator();
		// TODO implement support for z as var
		int degX = 0;
		int degY = 0;
		while (it.hasNext()) {
			Term t = it.next();
			degX = Math.max(degX, t.degree('x'));
			degY = Math.max(degY, t.degree('y'));
		}
		ExpressionValue[][] coeff = new ExpressionValue[degX + 1][degY + 1];
		it = terms.iterator();
		while (it.hasNext()) {
			Term t = it.next();
			coeff[t.degree('x')][t.degree('y')] = t.getCoefficient();
		}
		return coeff;
	}

	public HashSet<GeoElement> getVariables() {
		HashSet<GeoElement> temp, vars = new HashSet<GeoElement>();
		Iterator<Term> i = terms.iterator();
		while (i.hasNext()) {
			temp = i.next().getCoefficient().getVariables();
			if (temp != null) {
				vars.addAll(temp);
			}
		}
		return vars;
	}

	public void resolveVariables(boolean forEquation) {
		Iterator<Term> i = terms.iterator();
		while (i.hasNext()) {
			i.next().getCoefficient().resolveVariables(forEquation);
		}
	}

	public boolean isConstant() {
		HashSet<GeoElement> vars = getVariables();
		return (vars == null || vars.size() == 0);
	}

	public boolean isLeaf() {
		return true;
	}

	final public boolean isNumberValue() {
		return false;
	}

	public boolean isBooleanValue() {
		return false;
	}

	final public boolean isVectorValue() {
		return false;
	}

	final public boolean isPolynomialInstance() {
		return true;
	}

	final public boolean isTextValue() {
		return false;
	}

	final public boolean isExpressionNode() {
		return false;
	}

	public boolean isListValue() {
		return false;
	}

	final public boolean contains(ExpressionValue ev) {
		return ev == this;
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
	public ExpressionValue derivative(FunctionVariable fv) {
		throw new MyError(kernel.getApplication().getLocalization(), "derivative called on Polynomial");
	}

} // end of class Polynomial
