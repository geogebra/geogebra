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

package org.geogebra.common.kernel.arithmetic;

import java.util.Objects;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.editor.share.util.Unicode;

/**
 * A term is a pair of coefficient and variables in a Polynomial, e.g. {4, "x"},
 * {a, "xy"}
 */
public class Term implements Comparable<Term> {

	/** coefficient */
	ExpressionValue coefficient; // has to evaluate() to NumberValue
	private StringBuilder variables;
	private String cachedVariables;

	// private Kernel kernel;

	/**
	 * @param coeff
	 *            coefficient
	 * @param vars
	 *            variables string, eg. "xxy"
	 */
	public Term(ExpressionValue coeff, String vars) {
		this(coeff, new StringBuilder(vars));
	}

	/**
	 * @param kernel
	 *            kernel
	 * @param coeff
	 *            coefficient
	 * @param vars
	 *            variables string, eg. "xxy"
	 */
	public Term(Kernel kernel, double coeff, String vars) {
		this(new MyDouble(kernel, coeff), new StringBuilder(vars));
	}

	/**
	 * @param coeff
	 *            coefficient
	 * @param vars
	 *            variables StringBuilder
	 */
	public Term(ExpressionValue coeff, StringBuilder vars) {
		setCoefficient(coeff);
		variables = vars;
	}

	/**
	 * Copy constructor
	 * 
	 * @param t
	 *            term to copy
	 * @param kernel
	 *            kernel for coefficient
	 */
	public Term(Term t, Kernel kernel) {
		variables = new StringBuilder(t.getVars());
		setCoefficient(ExpressionNode.copy(t.coefficient, kernel));
	}

	/**
	 * @return coefficient
	 */
	public ExpressionValue getCoefficient() {
		return coefficient;
	}

	/**
	 * @param coeff
	 *            changes coefficient
	 */
	void setCoefficient(ExpressionValue coeff) {
		coefficient = coeff;
	}

	/**
	 * @return variables
	 */
	String getVars() {
		if (cachedVariables == null) {
			cachedVariables = variables.toString();
		}
		return cachedVariables;
	}

	/**
	 * @param vars
	 *            new variables string, eg. "xxy"
	 */
	void setVariables(String vars) {
		variables.setLength(0);
		variables.append(vars);
		cachedVariables = null;
	}

	/**
	 * @param vars
	 *            new variables as string
	 */
	void setVariables(StringBuilder vars) {
		variables.setLength(0);
		variables.append(vars);
		cachedVariables = null;
	}

	/**
	 * @return true if this has no variables
	 */
	boolean hasNoVars() {
		return variables.length() == 0;
	}

	/**
	 * @return true if coeff is integer
	 */
	boolean hasIntegerCoeff() {
		return DoubleUtil.isInteger(coefficient.evaluateDouble());
	}

	/**
	 * Total degree of this term
	 * 
	 * @return degree
	 */
	int degree() {
		return variables.length();
	}

	/**
	 * degree of eg x xxxyy returns 3 for x, 2 for y
	 * 
	 * @param var
	 *            term whose degree we want
	 * @return degree
	 */
	public int degree(char var) {
		int count = 0;
		for (int i = 0; i < variables.length(); i++) {
			if (variables.charAt(i) == var) {
				count++;
			}
		}
		return count;
	}

	/**
	 * add a number to this term's coefficient
	 * 
	 * @param number
	 *            number to add
	 * @param kernel
	 *            kernel
	 * @param keepFraction
	 *            whether to use keep coefficients as fractions
	 */
	void addToCoefficient(ExpressionValue number, Kernel kernel,
			boolean keepFraction) {
		setCoefficient(add(coefficient, number, kernel, keepFraction));
	}

	// return a + b
	private ExpressionValue add(ExpressionValue a, ExpressionValue b,
			Kernel kernel, boolean keepFraction) {
		// potentially related to TRAC-1994
		boolean aconst = a.isConstant();
		boolean bconst = b.isConstant();
		double aval, bval;

		// add constant?
		if (aconst && bconst && !keepFraction) {
			aval = a.evaluateDouble();
			bval = b.evaluateDouble();
			return new MyDouble(kernel, aval + bval);
		} else if (aconst) {
			aval = a.evaluateDouble();
			if (aval == 0.0d) {
				return b;
			}
			if (b.isExpressionNode()) {
				ExpressionNode ben = (ExpressionNode) b;
				if (ben.getLeft().isConstant()) {
					switch (ben.getOperation()) {
					// a + (b.left + b.right) = (a + b.left) + b.right
					case PLUS:
						return add(add(a, ben.getLeft(), kernel, keepFraction),
								ben.getRight(), kernel, keepFraction);
					// a + (b.left - b.right) = (a + b.left) - b.right
					case MINUS:
						return sub(add(a, ben.getLeft(), kernel, keepFraction),
								ben.getRight(), kernel, keepFraction);
					default:
						break;
					}
				}
			} // else
			return new ExpressionNode(kernel, a, Operation.PLUS, b);
		} else if (bconst) {
			return add(b, a, kernel, keepFraction); // get the constant to the
													// left
		} else {
			return new ExpressionNode(kernel, a, Operation.PLUS, b);
		}
	}

	private ExpressionValue sub(ExpressionValue a, ExpressionValue b,
			Kernel kernel, boolean keepFraction) {
		return add(a,
				multiply(new MyDouble(kernel, -1.0d), b, kernel, keepFraction),
				kernel, keepFraction);
	}

	/**
	 * multiply this term with another term
	 * 
	 * @param t
	 *            multiplier
	 * @param kernel
	 *            kernel
	 * @param keepFraction
	 *            whether to use keep coefficients as fractions
	 */
	void multiply(Term t, Kernel kernel, boolean keepFraction) {
		setCoefficient(
				multiply(coefficient, t.coefficient, kernel, keepFraction));
		variables.append(t.variables);
		cachedVariables = null;
		sort(variables);
	}

	/**
	 * multiply this term with a number
	 * 
	 * @param number
	 *            multiplier
	 * @param kernel
	 *            kernel
	 * @param keepFraction
	 *            whether to use keep coefficients as fractions
	 */
	void multiply(ExpressionValue number, Kernel kernel, boolean keepFraction) {
		setCoefficient(multiply(coefficient, number, kernel, keepFraction));
	}

	// c = a * b
	private ExpressionValue multiply(ExpressionValue a, ExpressionValue b,
			Kernel kernel, boolean keepFraction) {
		// multiply constant?
		boolean aconst = a.isConstant();
		boolean bconst = b.isConstant();
		double aval, bval;

		if (aconst && bconst && !keepFraction) {
			aval = a.evaluateDouble();
			bval = b.evaluateDouble();

			return new MyDouble(kernel, aval * bval);
		} else if (aconst) {
			aval = a.evaluateDouble();
			if (aval == 0.0d) {
				return new MyDouble(kernel, 0.0d);
			} else if (aval == 1.0d) {
				return b;
			} else {
				if (b instanceof ExpressionNode) {
					ExpressionNode ben = (ExpressionNode) b;
					if (ben.getLeft().isConstant()) {
						switch (ben.getOperation()) {
						// a * (b.left * b.right) = (a * b.left) * b.right
						case MULTIPLY:
							return multiply(
									multiply(a, ben.getLeft(), kernel,
											keepFraction),
									ben.getRight(), kernel, keepFraction);
						// a * (b.left / b.right) = (a * b.left) / b.right
						case DIVIDE:
							return divide(
									multiply(a, ben.getLeft(), kernel,
											keepFraction),
									ben.getRight(), kernel, keepFraction);
						default:
							break;
						}
					}
				}
				return new ExpressionNode(kernel, a, Operation.MULTIPLY, b);
			}
		} else if (bconst) {
			// a * b = b * a
			return multiply(b, a, kernel, keepFraction); // get the constant to
															// the left
		} else {
			return new ExpressionNode(kernel, a, Operation.MULTIPLY, b);
		}
	}

	/**
	 * divide this term with a number
	 * 
	 * @param number
	 *            divisor
	 * @param kernel
	 *            kernel
	 * @param keepFraction
	 *            whether to use keep coefficients as fractions
	 */
	void divide(ExpressionValue number, Kernel kernel, boolean keepFraction) {
		setCoefficient(divide(coefficient, number, kernel, keepFraction));
	}

	// c = a / b
	private ExpressionValue divide(ExpressionValue a, ExpressionValue b,
			Kernel kernel, boolean keepFraction) {
		// divide constants
		boolean aconst = a.isConstant();
		boolean bconst = b.isConstant();
		double aval, bval;

		if (aconst && bconst && !keepFraction) {
			aval = a.evaluateDouble();
			bval = b.evaluateDouble();
			return new MyDouble(kernel, aval / bval);
		} else if (aconst) {
			aval = a.evaluateDouble();
			if (aval == 0.0d) {
				return new MyDouble(kernel, 0.0d);
			}
			if (b instanceof ExpressionNode) {
				ExpressionNode ben = (ExpressionNode) b;
				// a / (b.left / b.right) = (a / b.left) * b.right
				if (ben.getOperation() == Operation.DIVIDE) {
					return multiply(
							divide(a, ben.getLeft(), kernel, keepFraction),
							ben.getRight(), kernel, keepFraction);
				}
				// TODO multiply?
			}
			return new ExpressionNode(kernel, a, Operation.DIVIDE, b);

		} else if (bconst) {
			bval = b.evaluateDouble();
			if (bval == 1.0d) {
				return a;
			}
			return new ExpressionNode(kernel, a, Operation.DIVIDE, b);
		} else {
			return new ExpressionNode(kernel, a, Operation.DIVIDE, b);
		}
	}

	// sort single characters: "yx" -> "xy"
	private static void sort(StringBuilder sb) {
		int len = sb.length();
		char[] chVariables = new char[len];

		// to sort, copy characters into a char array
		sb.getChars(0, len, chVariables, 0);
		java.util.Arrays.sort(chVariables, 0, len);
		sb.setLength(0);
		sb.append(chVariables);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Term) {
			Term t = (Term) o;
			return coefficient == t.coefficient
					&& getVars().equals(t.getVars());
		}
		return false;
	}

	@Override
	public int hashCode() {
		assert false : "hashCode not designed";
		return 42; // any arbitrary constant will do
	}

	/**
	 * @param var
	 *            var name
	 * @return True if contains given variable
	 */
	boolean contains(String var) {
		return getVars().indexOf(var) >= 0;
	}

	@Override
	public int compareTo(Term o) {

			return o.getVars()
					.compareTo(getVars());

	}

	@Override
	@Deprecated
	public String toString() {
		return toString(StringTemplate.defaultTemplate);
	}

	/**
	 * Serialize to string according to given template
	 * 
	 * @param tpl
	 *            template
	 * @return string representation
	 */
	public String toString(StringTemplate tpl) {
		if (ExpressionNode.isEqualString(coefficient, 0, true)) {
			return "0";
		}
		if (ExpressionNode.isEqualString(coefficient, 1, true)) {
			if (variableString(tpl).length() > 0) {
				return variableString(tpl);
			}
			return "1";
		}

		StringBuilder sb = new StringBuilder();
		String var = variableString(tpl);
		if (ExpressionNode.isEqualString(coefficient, -1, true)
				&& var.length() > 0) {
			sb.append('-');
			sb.append(var);
		} else {
			sb.append(coeffString(coefficient, tpl));
			if (var != null) {
				sb.append(' ');
				sb.append(var);
			}
		}
		return sb.toString();
	}

	private String coeffString(ExpressionValue ev, StringTemplate tpl) {
		if (ev instanceof GeoElement) {
			return ((GeoElement) ev).getLabel(tpl);
		} else if (ev instanceof ExpressionNode) {
			ExpressionNode n = (ExpressionNode) ev;
			if (n.isLeaf()
					|| ExpressionNode.opID(n) >= Operation.MULTIPLY.ordinal()
					|| variables.length() == 0) {
				return n.toString(tpl);
			}
			StringBuilder sb = new StringBuilder();
			sb.append('(');
			sb.append(n.toString(tpl));
			sb.append(')');
			return sb.toString();
		} else {
			return ev.toString(tpl);
		}
	}

	private String variableString(StringTemplate tpl) {
		String str = getVars();

		if (tpl.hasCASType() && variables.length() >= 1) {

			StringBuilder sb = new StringBuilder("(");

			for (int i = 0; i < str.length(); i++) {
				if (i > 0) {
					sb.append('*');
				}
				sb.append(tpl.printVariableName(str.charAt(0) + ""));
			}
			sb.append(')');
			return sb.toString();
		}
		switch (variables.length()) {
		case 1:
			return str;
		case 2:

			if ("xx".equals(str)) {
				return "x" + Unicode.SUPERSCRIPT_2;
			}
			if ("yy".equals(str)) {
				return "y" + Unicode.SUPERSCRIPT_2;
			}
			if ("xy".equals(str)) {
				return "xy";
			}
		default:
			return "";
		}
	}
} // end of class Term
