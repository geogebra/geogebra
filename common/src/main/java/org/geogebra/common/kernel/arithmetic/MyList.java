/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.
 
 */

/*
 * Command.java
 *
 * Created on 05. September 2001, 12:05
 */

package org.geogebra.common.kernel.arithmetic;

import java.util.ArrayList;
import java.util.HashSet;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.GgbMat;

/**
 * MyList is used to store a list of ExpressionNode objects read by the parser
 * and to evaluate them. So a MyList object is used when a list is entered (e.g.
 * {2, 3, 7, 9}) and also when a list is used for arithmetic operations.
 * 
 * @see ExpressionNode#evaluate(StringTemplate)
 * 
 * @author Markus Hohenwarter
 */
public class MyList extends ValidExpression implements ListValue,
		ReplaceChildrenByValues, GetItem {

	private Kernel kernel;
	private int matrixRows = -1; // -1 means not calculated, 0 means not a
									// matrix
	private int matrixCols = -1; //

	// list for list elements
	private ArrayList<ExpressionValue> listElements;

	/**
	 * Creates new MyList
	 * 
	 * @param kernel
	 *            kernel
	 */
	public MyList(Kernel kernel) {
		this(kernel, 20);
	}

	/**
	 * Creates new MyList of given length
	 * 
	 * @param kernel
	 *            kernel
	 * @param size
	 *            length of the list
	 */
	public MyList(Kernel kernel, int size) {
		this.kernel = kernel;
		listElements = new ArrayList<ExpressionValue>(size);
	}

	/**
	 * Create new MyList
	 * 
	 * @param kernel
	 *            kernel
	 * @param isFlatList
	 *            true for flat lists, false for matrices etc.
	 */
	public MyList(Kernel kernel, boolean isFlatList) {
		this(kernel);

		if (isFlatList) {
			// make sure isMatrix() returns false (fast)
			// see #1384
			matrixRows = matrixCols = 0;
		}
	}

	/**
	 * Adds expression value to the list
	 * 
	 * @param arg
	 *            element to add
	 */
	public void addListElement(ExpressionValue arg) {
		listElements.add(arg);
		matrixRows = -1; // reset
		matrixCols = -1;
	}

	/**
	 * Tries to return this list as an array of double values
	 * 
	 * @return array of double values from this list
	 */
	public double[] toDouble() {
		try {
			double[] valueArray = new double[listElements.size()];
			for (int i = 0; i < valueArray.length; i++) {
				valueArray[i] = listElements.get(i).evaluateDouble();
			}
			return valueArray;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Replaces all Variable objects with the given varName in this list by the
	 * given FunctionVariable object.
	 * 
	 * @param varName
	 *            variable name
	 * @param fVar
	 *            replacement variable
	 * @return number of replacements done
	 */
	public int replaceVariables(String varName, FunctionVariable fVar) {
		int replacements = 0;

		for (int i = 0; i < listElements.size(); i++) {
			ExpressionValue element = listElements.get(i);
			if (element instanceof ExpressionNode) {
				replacements += ((ExpressionNode) element).replaceVariables(
						varName, fVar);
			} else if (element instanceof Variable) {
				if (varName.equals(((Variable) element)
						.getName(StringTemplate.defaultTemplate))) {
					listElements.set(i, fVar);
					replacements++;
				}
			}
		}

		return replacements;
	}

	/**
	 * Applies an operation to this list using the given value: <this>
	 * <operation> <value>.
	 * 
	 * @param operation
	 *            int value like ExpressionNode.MULTIPLY
	 * @param value
	 *            value that should be applied to this list using the given
	 *            operation
	 * @author Markus Hohenwarter
	 * @param tpl
	 *            string template in case of concatenation
	 */
	final public void applyRight(Operation operation, ExpressionValue value,
			StringTemplate tpl) {
		apply(operation, value, true, tpl);
	}

	/**
	 * Applies an operation to this list using the given value: <value>
	 * <operation> <this>.
	 * 
	 * @param operation
	 *            int value like ExpressionNode.MULTIPLY
	 * @param value
	 *            value that should be applied to this list using the given
	 *            operation
	 * @author Markus Hohenwarter
	 * @param tpl
	 *            string template in case of string concatenation
	 */
	final public void applyLeft(Operation operation, ExpressionValue value,
			StringTemplate tpl) {
		apply(operation, value, false, tpl);
	}

	private boolean isDefined = true;

	final private void matrixMultiply(MyList LHlist, MyList RHlist) {
		int LHcols = LHlist.getMatrixCols(), LHrows = LHlist.getMatrixRows();
		int RHcols = RHlist.getMatrixCols(); // RHlist.getMatrixRows();

		ExpressionNode totalNode;
		ExpressionNode tempNode;
		listElements.clear();

		if (LHcols != RHlist.getMatrixRows()) {
			isDefined = false;
			return;
		}

		isDefined = true;

		for (int row = 0; row < LHrows; row++) {
			MyList col1 = new MyList(kernel);
			for (int col = 0; col < RHcols; col++) {
				ExpressionValue totalVal = new ExpressionNode(kernel,
						new MyDouble(kernel, 0.0d));
				for (int i = 0; i < LHcols; i++) {
					ExpressionValue leftV = getCell(LHlist, i, row);
					ExpressionValue rightV = getCell(RHlist, col, i);
					tempNode = new ExpressionNode(kernel, leftV,
							Operation.MULTIPLY, rightV);

					// multiply two cells...
					ExpressionValue operationResult = tempNode
							.evaluate(StringTemplate.defaultTemplate);

					totalNode = new ExpressionNode(kernel, totalVal,
							Operation.PLUS, operationResult);
					// totalNode.setLeft(operationResult);
					// totalNode.setRight(totalVal);
					// totalNode.setOperation(ExpressionNode.PLUS);

					// ...then add the result to a running total
					totalVal = totalNode
							.evaluate(StringTemplate.defaultTemplate);

				}
				tempNode = new ExpressionNode(kernel, totalVal);
				col1.addListElement(tempNode);
			}
			ExpressionNode col1a = new ExpressionNode(kernel, col1);
			listElements.add(col1a);

		}
		matrixRows = -1; // reset
		matrixCols = -1;

	}

	/**
	 * Applies an operation to this list using the given value.
	 * 
	 * @param operation
	 *            int value like ExpressionNode.MULTIPLY
	 * @param value
	 *            value that should be applied to this list using the given
	 *            operation
	 * @param right
	 *            true for <this> <operation> <value>, false for <value>
	 *            <operation> <this>
	 * @param tpl
	 *            string template in case we do string concatenation here
	 * @author Markus Hohenwarter
	 */
	private void apply(Operation operation, ExpressionValue value,
			boolean right, StringTemplate tpl) {
		int size = size();

		// if (!right)
		// Application.debug("apply: " + value + " < op: " + operation + " > " +
		// this);
		// else
		// Application.debug("apply: " + this + " < op: " + operation + " > " +
		// value);

		// matrix ^ integer
		if (right && operation == Operation.POWER
				&& value instanceof NumberValue && isMatrix()) {

			double power = ((NumberValue) value).getDouble();
			// Application.debug("matrix ^ "+power);

			if (!Kernel.isInteger(power)) {
				listElements.clear();
				return;
			}

			power = Math.round(power);

			if (power == 0) {
				setIdentityMatrix();
			}
			if (power < 0) {
				listElements = this.invert().listElements;
				power *= -1;
				if (power == 1) {
					MyList RHlist = this.deepCopy(kernel);
					RHlist.setIdentityMatrix();
					matrixMultiply(this.deepCopy(kernel), RHlist);
					return;
				}
			}
			if (power != 1) {

				MyList LHlist, RHlist;
				RHlist = this.deepCopy(kernel);
				while (power > 1.0) {
					LHlist = this.deepCopy(kernel);

					matrixMultiply(LHlist, RHlist);
					power--;
				}
				return; // finished matrix multiplication successfully
			}
			// else power = 1, so drop through to standard list code below

		}

		// expression value is list
		MyList valueList = value instanceof ListValue ? ((ListValue) value)
				.getMyList() : null;

		// Michael Borcherds 2008-04-14 BEGIN
		// check for matrix multiplication eg
		// {{1,3,5},{2,4,6}}*{{11,14},{12,15},{13,16}}
		// try{
		if (operation == Operation.MULTIPLY && valueList != null) {
			MyList LHlist, RHlist;

			if (!right) {
				LHlist = valueList;
				RHlist = (MyList) this.deepCopy(kernel);
			} else {
				RHlist = valueList;
				LHlist = (MyList) this.deepCopy(kernel);
			}

			boolean isMatrix = (LHlist.isMatrix() && RHlist.isMatrix());

			if (isMatrix) {
				matrixMultiply(LHlist, RHlist);
				return; // finished matrix multiplication successfully
			}
		}
		// }
		// catch (Exception e) { } // not valid matrices
		// Michael Borcherds 2008-04-14 END

		matrixRows = -1; // reset
		matrixCols = -1;

		// return empty list if sizes don't match

		if (needsExpand()) {
			expand();
			size = size();
		}
		if (size == 0 || (valueList != null && valueList.size() > size)) {
			listElements.clear();
			return;
		}



		// temp ExpressionNode to do evaluation of single elements
		ExpressionNode tempNode = new ExpressionNode(kernel,
				listElements.get(0));
		tempNode.setOperation(operation);

		boolean b = kernel.getConstruction().isSuppressLabelsActive();
		kernel.getConstruction().setSuppressLabelCreation(true);

		if (valueList != null && valueList.needsExpand()) {
			valueList = valueList.deepCopy(kernel);
			valueList.expand();
		}
		if (valueList != null && valueList.size() != size) {
			listElements.clear();
			return;

		}

		for (int i = 0; i < size; i++) {
			// try {
			// singleValue to apply to i-th element of this list
			// since evaluate() might change the value of left operand, we need
			// a deep copy here
			// see #460
			ExpressionValue singleValue = valueList == null ? value
					.deepCopy(kernel) : valueList.getListElement(i);
			addResult(listElements.get(i), i, tempNode, singleValue, right,
						tpl);


			// }
			// catch (MyError err) {
			// err.printStackTrace();
			// Application.debug(err.getLocalizedMessage());

			// return empty list if any of the elements aren't numbers
			// listElements.clear();
			// return;
			// }
		}
		kernel.getConstruction().setSuppressLabelCreation(b);

		// Application.debug("   gives : " + this);

	}

	private boolean needsExpand() {
		int size = size();
		for (int i = 0; i < size; i++) {
			if (getListElement(i).unwrap().isGeoElement()) {
				AlgoElement algo = ((GeoElement) getListElement(i)
						.unwrap()).getParentAlgorithm();

				if (algo != null && algo.getOutputLength() > 1
						&& algo.hasSingleOutputType()) {
					return true;
				}
			}
		}
		return false;
	}

	private void expand() {
		ArrayList<ExpressionValue> expElements = new ArrayList<ExpressionValue>();

		for (int i = 0; i < listElements.size(); i++) {
			AlgoElement algo = null;
			ExpressionValue ev = getListElement(i).unwrap();
			if (ev.isGeoElement()) {
				algo = ((GeoElement) ev)
						.getParentAlgorithm();

			}
			if (algo != null && algo.getOutputLength() > 1
					&& algo.hasSingleOutputType()) {
				for (int k = 0; k < algo.getOutputLength(); k++) {
					if ((algo.getOutput(k).isDefined() || algo.getOutput(k) == ev)
							&& !expElements.contains(algo.getOutput(k)))
					expElements.add(algo.getOutput(k));
				}
			} else {
				expElements.add(ev);
			}
		}
		this.listElements = expElements;
	}
	private void addResult(ExpressionValue myValue, int j,
			ExpressionNode tempNode,
			ExpressionValue singleValue, boolean right, StringTemplate tpl) {
		// apply operation using singleValue
		if (right) {
			// this operation value
			tempNode.setLeft(myValue);
			tempNode.setRight(singleValue);
		} else {
			// value operation this
			tempNode.setLeft(singleValue);
			tempNode.setRight(myValue);
		}

		// evaluate operation

		ExpressionValue operationResult = tempNode.evaluate(tpl);

		// Application.debug("        tempNode : " + tempNode + ", result: "
		// + operationResult);

		// set listElement to operation result
		if (!operationResult.isExpressionNode()) {
			operationResult = new ExpressionNode(kernel, operationResult);
		}

		listElements.set(j, operationResult);

	}

	private void setIdentityMatrix() {
		isMatrix();
		listElements.clear();
		if (matrixRows == matrixCols)
			for (int row = 0; row < matrixRows; row++) {
				MyList col1 = new MyList(kernel);
				for (int col = 0; col < matrixCols; col++) {
					ExpressionNode md = new ExpressionNode(kernel,
							new MyDouble(kernel, row == col ? 1 : 0));
					col1.addListElement(md);
				}
				ExpressionNode col1a = new ExpressionNode(kernel, col1);
				listElements.add(col1a);

			}

	}

	/**
	 * @return 0 if not a matrix
	 * 
	 * @author Michael Borcherds
	 */
	public int getMatrixRows() {
		// check if already calculated
		if (matrixRows != -1 && matrixCols != -1)
			return matrixRows;

		isMatrix(); // do calculation

		return matrixRows;

	}

	/**
	 * @return 0 if not a matrix
	 * 
	 * @author Michael Borcherds
	 */
	public int getMatrixCols() {
		// check if already calculated
		if (matrixRows != -1 && matrixCols != -1)
			return matrixCols;

		isMatrix(); // do calculation

		return matrixCols;

	}

	/**
	 * Inverts matrix (doesn't do any internal changes)
	 * 
	 * @return inversion of this
	 */
	public MyList invert() {
		GgbMat g = new GgbMat(this);
		g.inverseImmediate();
		MyList gl = new MyList(kernel);
		g.getMyList(gl, kernel);
		return gl.getMyList();
	}

	/**
	 * @return true if this list is a matrix
	 */
	public boolean isMatrix() {
		return isMatrix(this);
	}

	/**
	 * Removes all elements from this list
	 */
	public void clear() {
		listElements.clear();
	}

	private static boolean isEquation(ExpressionValue ex) {
		if (ex instanceof Equation
				|| (ex instanceof ExpressionNode && ((ExpressionNode) ex)
						.getLeft() instanceof Equation))
			return true;
		return false;
	}

	private boolean isMatrix(MyList LHlist) {
		// check if already calculated
		if (matrixRows > 0 && matrixCols > 0)
			return true;
		if (matrixRows == 0 && matrixCols == 0)
			return false;

		try {
			boolean isMatrix = true;

			int LHrows = LHlist.size(), LHcols = 0;

			// Application.debug("MULT LISTS"+size);

			// check LHlist is a matrix
			if (isEquation(LHlist.getListElement(0)))
				return false;
			ExpressionValue singleValue = LHlist.getListElement(0).evaluate(
					StringTemplate.defaultTemplate);
			if (singleValue == null) {
				matrixRows = matrixCols = 0;
				return false;
			}

			if (singleValue instanceof ListValue) {
				LHcols = ((ListValue) singleValue).getMyList().size();
				if (LHcols > 0
						&& isEquation(((ListValue) singleValue)
								.getListElement(0)))
					return false;
				// Application.debug("LHrows"+LHrows);
				if (LHrows > 1)
					for (int i = 1; i < LHrows; i++) // check all rows same
														// length
					{
						// Application.debug(i);
						if (isEquation(LHlist.getListElement(i)))
							return false;
						singleValue = LHlist.getListElement(i).evaluate(
								StringTemplate.defaultTemplate);
						// Application.debug("size"+((ListValue)singleValue).getMyList().size());
						if (singleValue.evaluatesToList()) {
							MyList list = ((ListValue) singleValue).getMyList();
							if (list.size() != LHcols)
								isMatrix = false;
							else if ((list.size() > 0)
									&& isEquation(list.getListElement(0)))
								isMatrix = false;
						} else
							isMatrix = false;
					}
			} else
				isMatrix = false;

			// Application.debug("isMatrix="+isMatrix);

			if (isMatrix) {
				matrixCols = LHcols;
				matrixRows = LHrows;
			} else {
				matrixCols = 0;
				matrixRows = 0;
			}

			return isMatrix;
		} catch (Throwable e) {
			matrixRows = matrixCols = 0;
			return false;
		}

	}

	// Michael Borcherds 2008-04-15
	/**
	 * @param list
	 *            matrix
	 * @param row
	 *            row number (starts with 0)
	 * @param col
	 *            col number (starts with 0)
	 * @return cell of a list at given position
	 */
	public static ExpressionValue getCell(MyList list, int row, int col) {
		ExpressionValue singleValue = list.getListElement(col).evaluate(
				StringTemplate.defaultTemplate);
		if (singleValue instanceof ListValue) {
			ExpressionValue ret = (((ListValue) singleValue).getMyList()
					.getListElement(row))
					.evaluate(StringTemplate.defaultTemplate);
			// if (ret.isListValue()) Application.debug("isList*********");
			return ret;
		}
		return null;
	}


	@Override
	public String toValueString(StringTemplate tpl) {
		return toString(tpl, true);
		/*
		 * int size = listElements.size(); for (int i=0; i < size; i++) {
		 * ((ExpressionValue) listElements.get(i)).evaluate(); }
		 */
	}

	public String toLaTeXString(boolean symbolic, StringTemplate tpl) {
		StringBuilder toLaTeXString = new StringBuilder();
		if (size() == 0) {
			// in schools the emptyset symbol is typically not used, see #
			// return "\\emptyset";

			// changed from "\\{ \\}" as MathQuillGGB doesn't render that
			// correctly
			return "\\left\\{ \\right\\}";
		} else if (isMatrix()) {
			if (kernel.getApplication().isLatexMathQuillStyle(tpl)) {
				toLaTeXString.append("\\left(\\ggbtable{");
				for (int i = 0; i < size(); i++) {
					ListValue singleValue = (ListValue) getListElement(i)
							.evaluate(StringTemplate.defaultTemplate);
					toLaTeXString.append("\\ggbtr{");
					for (int j = 0; j < singleValue.size(); j++) {
						toLaTeXString.append("\\ggbtd{");
						toLaTeXString.append(singleValue.getListElement(j)
								.toLaTeXString(symbolic, tpl));
						toLaTeXString.append("}");
					}
					toLaTeXString.append("}");
				}
				toLaTeXString.append("}\\right)");
			} else {
				toLaTeXString.append("\\left(\\begin{array}{");
				for (int i = 0; i < matrixCols; i++) {
					// nice alignment for eg {{-1,1},{1,-1}} in CAS
					toLaTeXString.append("r");
				}
				toLaTeXString.append("}");

				for (int i = 0; i < size(); i++) {
					ListValue singleValue = (ListValue) getListElement(i)
							.evaluate(StringTemplate.defaultTemplate);

					if (singleValue.size() > 0) {
						toLaTeXString.append(singleValue.getListElement(0)
								.toLaTeXString(symbolic, tpl));
						for (int j = 1; j < singleValue.size(); j++) {
							toLaTeXString.append("&");
							toLaTeXString.append(singleValue.getListElement(j)
									.toLaTeXString(symbolic, tpl));
						}
					}

					toLaTeXString.append("\\\\");
				}
				toLaTeXString.append("\\end{array}\\right)");
			}
		} else {
			toLaTeXString.append(" \\left\\{ ");

			// first (n-1) elements
			int lastIndex = listElements.size() - 1;
			if (lastIndex > -1) {
				for (int i = 0; i < lastIndex; i++) {
					ExpressionValue exp = listElements.get(i);
					toLaTeXString.append(exp.toLaTeXString(symbolic, tpl));
					toLaTeXString.append(", ");
				}

				// last element
				ExpressionValue exp = listElements.get(lastIndex);
				toLaTeXString.append(exp.toLaTeXString(symbolic, tpl));
			}

			toLaTeXString.append(" \\right\\} ");
		}
		return toLaTeXString.toString();
	}

	@Override
	public String toString(StringTemplate tpl) {
		return toString(tpl, false);
	}

	// Michael Borcherds 2008-02-04
	// adapted from GeoList
	private String toString(StringTemplate tpl, boolean valueMode) {

		StringBuilder sb = new StringBuilder();
		if (tpl.hasType(StringType.LATEX)) {
			sb.append("\\{");
		} else
			sb.append("{");

		// first (n-1) elements
		int lastIndex = listElements.size() - 1;
		if (lastIndex > -1) {
			for (int i = 0; i < lastIndex; i++) {
				ExpressionValue exp = listElements.get(i);
				sb.append(valueMode ? exp.toOutputValueString(tpl) : exp
						.toString(tpl)); // .toOutputValueString());
				sb.append(", ");
			}

			// last element
			ExpressionValue exp = listElements.get(lastIndex);
			sb.append(valueMode ? exp.toOutputValueString(tpl) : exp
					.toString(tpl));
		}

		if (tpl.hasType(StringType.LATEX)) {
			sb.append("\\}");
		} else
			sb.append("}");
		return sb.toString();
	}

	public int size() {
		return listElements.size();
	}

	public void resolveVariables() {
		for (int i = 0; i < listElements.size(); i++) {
			ExpressionValue en = listElements.get(i);
			en.resolveVariables();
		}
	}

	/**
	 * @param i
	 *            index
	 * @return i-th element of the list
	 */
	public ExpressionValue getListElement(int i) {
		return listElements.get(i);
	}

	public ExpressionValue setListElement(int i, ExpressionValue ev) {
		return listElements.set(i, ev);
	}

	/*
	 * public String toString() { }
	 */

	public boolean isConstant() {
		return getVariables().size() == 0;
	}

	public boolean isLeaf() {
		return true;
	}

	public boolean isNumberValue() {
		return false;
	}

	public MyList deepCopy(Kernel kernel1) {
		// copy arguments
		int size = listElements.size();
		MyList c = new MyList(kernel1, size());

		for (int i = 0; i < size; i++) {
			c.addListElement(listElements.get(i).deepCopy(kernel1));
		}
		return c;
	}

	/**
	 * Deep copy, except for geo elements
	 * 
	 * @param kernel1
	 *            kernel for result
	 * @return deep copy, except for geo elements
	 */
	public ExpressionValue deepCopyExGeo(Kernel kernel1) {
		// copy arguments
		int size = listElements.size();
		MyList c = new MyList(kernel1, size());

		for (int i = 0; i < size; i++) {
			c.addListElement(ExpressionNode.copy(listElements.get(i), kernel1));
		}
		return c;
	}

	public HashSet<GeoElement> getVariables() {
		HashSet<GeoElement> varSet = new HashSet<GeoElement>();
		int size = listElements.size();
		for (int i = 0; i < size; i++) {
			HashSet<GeoElement> s = listElements.get(i).getVariables();
			if (s != null)
				varSet.addAll(s);
		}

		return varSet;
	}

	@Override
	public boolean evaluatesToList() {
		return true;
	}

	final public boolean contains(ExpressionValue ev) {
		return ev == this;
	}

	public MyList getMyList() {
		if (isInTree()) {
			// used in expression node tree: be careful
			return deepCopy(kernel);
		}
		// not used anywhere: reuse this object
		return this;
	}

	/**
	 * @param a
	 *            needle
	 * @param myList
	 *            haystack
	 * @return true iff myList contains a
	 */
	public static boolean isElementOf(ExpressionValue a, MyList myList) {
		// Application.debug(a.getClass()+"");

		for (int i = 0; i < myList.size(); i++) {
			ExpressionValue ev = myList.getListElement(i).evaluate(
					StringTemplate.defaultTemplate);

			// g:x=0, g isElementOf {x=0} gives null here
			// see #1535
			if (ev == null) {
				App.debug(myList.getListElement(i).getClass().getName());
				continue;
			}

			if (ExpressionNode.isEqual(a, ev))
				return true;
		}

		return false;
	}

	/**
	 * @param list1
	 *            haystack
	 * @param list2
	 *            list of needles
	 * @param tpl
	 *            string template
	 * @return true iff list2 is subset of list1
	 */
	public static boolean listContains(MyList list1, MyList list2,
			StringTemplate tpl) {
		if (list2.size() == 0) {
			// the empty set is a subset of all sets
			return true;
		}

		// removed, bug: {1, 2, 2, 2} IS_SUBSET_OF {3, 2, 1}
		// if (list1.size() < list2.size())
		// return false;

		for (int i = 0; i < list2.size(); i++) {
			ExpressionValue ev2 = list2.getListElement(i).evaluate(tpl);
			boolean hasEqualMember = false;
			for (int j = 0; j < list1.size(); j++) {
				ExpressionValue ev1 = list1.getListElement(j).evaluate(tpl);

				if (ExpressionNode.isEqual(ev1, ev2)) {
					hasEqualMember = true;
					break;
				}

			}

			if (!hasEqualMember)
				return false;

		}

		return true;
	}

	/**
	 * @param list1
	 *            haystack
	 * @param list2
	 *            list of needles
	 * @param tpl
	 *            template (in case there are string concatenations)
	 * @return true iff list2 is proper subset of list1
	 */
	public static boolean listContainsStrict(MyList list1, MyList list2,
			StringTemplate tpl) {

		// removed, bug: {1, 2, 2, 2} IS_STRICT_SUBSET_OF {3, 2, 1}
		// if (list1.size() <= list2.size())
		// return false;

		// the empty set is a strict subset of everything except itself
		if (list2.size() == 0) {
			return (list1.size() != 0);
		}

		for (int i = 0; i < list2.size(); i++) {
			ExpressionValue ev2 = list2.getListElement(i).evaluate(tpl);
			boolean hasEqualMember = false;
			for (int j = 0; j < list1.size(); j++) {
				ExpressionValue ev1 = list1.getListElement(j).evaluate(tpl);

				if (ExpressionNode.isEqual(ev1, ev2)) {
					hasEqualMember = true;
					break;
				}

			}

			if (!hasEqualMember)
				return false;

		}

		// now must check sets aren't equal
		for (int i = 0; i < list1.size(); i++) {
			ExpressionValue ev1 = list1.getListElement(i).evaluate(
					StringTemplate.defaultTemplate);
			boolean hasEqualMember = false;
			for (int j = 0; j < list2.size(); j++) {
				ExpressionValue ev2 = list2.getListElement(j).evaluate(
						StringTemplate.defaultTemplate);
				if (ExpressionNode.isEqual(ev1, ev2)) {
					hasEqualMember = true;
					break;
				}
			}
			// we've found an element without a match
			// so lists are not equal
			if (!hasEqualMember)
				return true;

		}

		// lists are equal
		return false;
	}

	/**
	 * @param kernel
	 *            kernel
	 * @param list1
	 *            minuend
	 * @param list2
	 *            subtrahend
	 * @return set difference of the lists
	 */
	public static MyList setDifference(Kernel kernel, MyList list1, MyList list2) {

		if (list2.size() == 0)
			return list1;

		MyList ret = new MyList(kernel);
		if (list1.size() == 0)
			return ret;

		for (int i = 0; i < list1.size(); i++) {
			ExpressionValue ev0 = list1.getListElement(i);
			ExpressionValue ev1 = ev0.evaluate(StringTemplate.defaultTemplate);
			boolean addToList = true;
			for (int j = 0; j < list2.size(); j++) {
				ExpressionValue ev2 = list2.getListElement(j).evaluate(
						StringTemplate.defaultTemplate);
				if (ExpressionNode.isEqual(ev1, ev2)) {
					addToList = false;
					break;
				}
			}
			if (addToList)
				ret.addListElement(ev0);
		}

		return ret;

	}

	public String toOutputValueString(StringTemplate tpl) {
		return toValueString(tpl);
	}

	/**
	 * Computes vector product of this and other list, the result is stored in
	 * this list
	 * 
	 * @param list
	 *            other list
	 */
	public void vectorProduct(MyList list) {
		// tempX/Y needed because a and c can be the same variable
		ExpressionValue ax = getListElement(0);
		ExpressionValue ay = getListElement(1);
		ExpressionValue bx = list.getListElement(0);
		ExpressionValue by = list.getListElement(1);

		ExpressionNode en = new ExpressionNode(kernel, ax, Operation.MULTIPLY,
				by);
		ExpressionNode en2 = new ExpressionNode(kernel, ay, Operation.MULTIPLY,
				bx);
		ExpressionNode x, y, z;
		if (list.size() == 2 || size() == 2) {
			listElements.add(2, new ExpressionNode(kernel, en, Operation.MINUS,
					en2));
			listElements.set(0, new ExpressionNode(kernel, new MyDouble(kernel,
					0.0), Operation.NO_OPERATION, null));
			listElements.set(1, new ExpressionNode(kernel, new MyDouble(kernel,
					0.0), Operation.NO_OPERATION, null));
			return;
		}
		// size 3
		z = new ExpressionNode(kernel, en, Operation.MINUS, en2);
		ExpressionValue az = getListElement(2);
		ExpressionValue bz = list.getListElement(2);
		en = new ExpressionNode(kernel, ay, Operation.MULTIPLY, bz);
		en2 = new ExpressionNode(kernel, az, Operation.MULTIPLY, by);
		x = new ExpressionNode(kernel, en, Operation.MINUS, en2);

		en = new ExpressionNode(kernel, az, Operation.MULTIPLY, bx);
		en2 = new ExpressionNode(kernel, ax, Operation.MULTIPLY, bz);
		y = new ExpressionNode(kernel, en, Operation.MINUS, en2);

		listElements.set(0, x);
		listElements.set(1, y);
		listElements.set(2, z);
		// double tempX = a.y * b.z - a.z * b.y;
		// double tempY = - a.x * b.z + a.z * b.x;
		// c.z = a.x * b.y - a.y * b.x;
		// c.x = tempX;
		// c.y = tempY;

	}

	public Kernel getKernel() {
		return kernel;
	}

	/**
	 * @return true iff this list is defined (not result of e.g. singular matrix
	 *         inverse)
	 */
	public boolean isDefined() {
		return isDefined;
	}

	public void replaceChildrenByValues(GeoElement geo) {
		for (int i = 0; i < size(); i++) {
			ExpressionValue insert = getListElement(i);
			if (insert instanceof ReplaceChildrenByValues)
				((ReplaceChildrenByValues) insert).replaceChildrenByValues(geo);
		}

	}

	/**
	 * Same as deep copy, but doesn't deep copy elements
	 * 
	 * @param kernel2
	 *            kernel
	 * @return copy of this list
	 */
	public MyList getCopy(Kernel kernel2) {
		MyList ret = new MyList(kernel, size());
		for (int i = 0; i < size(); i++) {
			ret.listElements.add(ExpressionNode.copy(listElements.get(i),
					kernel2));
		}
		return ret;
	}

	@Override
	public ExpressionValue traverse(Traversing t) {
		ExpressionValue v = t.process(this);
		for (int i = 0; i < size(); i++) {
			ExpressionValue insert = getListElement(i);
			listElements.set(i, insert.traverse(t));
		}
		return v;
	}

	@Override
	public boolean inspect(Inspecting t) {
		if (t.check(this))
			return true;
		for (int i = 0; i < size(); i++) {
			if ((getListElement(i)).inspect(t)) {
				return true;
			}
		}
		return false;
	}

	public ExpressionValue getItem(int i) {
		return listElements.get(i);
	}

	public int getLength() {
		return listElements.size();
	}

	public ExpressionNode wrap() {
		return new ExpressionNode(kernel, this);
	}

	public void addQue(double val, int offset) {
		if(listElements.size() < offset +1){
			return;
		}
		MyDouble removed = (MyDouble) listElements.get(offset);
		for(int read = offset + 1; read < listElements.size(); read++){
			listElements.set(read-1, listElements.get(read));
		}
		removed.add(val - removed.getDouble());
		listElements.set(listElements.size()-1, removed);
		
	}

	@Override
	public boolean evaluatesToMatrix() {
		return isMatrix();
	}

}
