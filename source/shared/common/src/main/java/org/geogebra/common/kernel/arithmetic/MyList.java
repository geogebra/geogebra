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
import java.util.Set;
import java.util.stream.Stream;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.arithmetic.variable.Variable;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.MyError;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.ExtendedBoolean;
import org.geogebra.common.util.GgbMat;
import org.geogebra.common.util.debug.Log;

import com.google.j2objc.annotations.Weak;

/**
 * MyList is used to store a list of ExpressionNode objects read by the parser
 * and to evaluate them. So a MyList object is used when a list is entered (e.g.
 * {2, 3, 7, 9}) and also when a list is used for arithmetic operations.
 *
 * @see ExpressionNode#evaluate(StringTemplate)
 *
 * @author Markus Hohenwarter
 */
public class MyList extends ValidExpression
		implements ListValue, ReplaceChildrenByValues, GetItem {

	@Weak
	private final Kernel kernel;
	private int matrixRows = -1; // -1 means not calculated, 0 means not a
									// matrix
	private int matrixCols = -1; //
	private boolean isAllLists; // whether all elements are lists

	// list for list elements
	private ArrayList<ExpressionValue> listElements;
	private boolean isDefined = true;

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
		listElements = new ArrayList<>(size);
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
	@Override
	public double[] toDouble(int offset) {
		int length = listElements.size();
		try {
			double[] valueArray = new double[length - offset];
			for (int i = offset; i < length; i++) {
				valueArray[i - offset] = listElements.get(i).evaluateDouble();
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
				replacements += ((ExpressionNode) element)
						.replaceVariables(varName, fVar);
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
	 * Applies an operation to this list using the given value:
	 * [this] [operation] [value].
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
	 * Applies an operation to this list using the given value:
	 * [value] [operation] [this].
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

	private void matrixMultiply(MyList LHlist, MyList RHlist) {
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
	 *            true for [this] [operation] [value], false for [value]
	 *            [operation] [this]
	 * @param tpl
	 *            string template in case we do string concatenation here
	 */
	public void apply(Operation operation, ExpressionValue value,
			boolean right, StringTemplate tpl) {
		int size = size();

		// matrix ^ integer
		if (right && operation == Operation.POWER
				&& value instanceof NumberValue && isMatrix()) {

			double powerD = value.evaluateDouble();

			if (!DoubleUtil.isInteger(powerD)) {
				listElements.clear();
				return;
			}

			int power = (int) Math.round(powerD);

			if (power == 0) {
				setIdentityMatrix();
			}
			if (power < 0) {
				MyList invert = this.invert();
				listElements = invert.listElements;
				power *= -1;
				if (!invert.isDefined) {
					this.isDefined = false;
					return;
				}
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
		MyList valueList = value instanceof ListValue
				? ((ListValue) value).getMyList() : null;

		// Michael Borcherds 2008-04-14 BEGIN
		// check for matrix multiplication eg
		// {{1,3,5},{2,4,6}}*{{11,14},{12,15},{13,16}}
		// try{
		if (operation == Operation.MULTIPLY && valueList != null) {
			MyList LHlist, RHlist;

			if (!right) {
				LHlist = valueList;
				RHlist = this.deepCopy(kernel);
			} else {
				RHlist = valueList;
				LHlist = this.deepCopy(kernel);
			}

			boolean isMatrix = LHlist.isMatrix() && RHlist.isMatrix();

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

		if (needsExpand()) {
			expand();
			size = size();
		}
		// return empty list if sizes don't match
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
			kernel.getConstruction().setSuppressLabelCreation(b);
			return;

		}
		for (int i = 0; i < size; i++) {
			// try {
			// singleValue to apply to i-th element of this list
			// since evaluate() might change the value of left operand, we need
			// a deep copy here
			// see #460
			ExpressionValue singleValue = valueList == null
					? value.deepCopy(kernel) : valueList.get(i);
			addResult(listElements.get(i), i, tempNode, singleValue, right,
					tpl);
		}
		kernel.getConstruction().setSuppressLabelCreation(b);
	}

	private boolean needsExpand() {
		int size = size();
		for (int i = 0; i < size; i++) {
			if (get(i).unwrap().isGeoElement()) {
				AlgoElement algo = ((GeoElement) get(i).unwrap())
						.getParentAlgorithm();

				if (algo != null && algo.getOutputLength() > 1
						&& algo.hasSingleOutputType()) {
					return true;
				}
			}
		}
		return false;
	}

	private void expand() {
		ArrayList<ExpressionValue> expElements = new ArrayList<>();

		for (int i = 0; i < listElements.size(); i++) {
			AlgoElement algo = null;
			ExpressionValue ev = get(i).unwrap();
			if (ev.isGeoElement()) {
				algo = ((GeoElement) ev).getParentAlgorithm();

			}
			if (algo != null && algo.getOutputLength() > 1
					&& algo.hasSingleOutputType()) {
				for (int k = 0; k < algo.getOutputLength(); k++) {
					if ((algo.getOutput(k).isDefined()
							|| algo.getOutput(k) == ev)
							&& !expElements.contains(algo.getOutput(k))) {
						expElements.add(algo.getOutput(k));
					}
				}
			} else {
				expElements.add(ev);
			}
		}
		this.listElements = expElements;
	}

	private void addResult(ExpressionValue myValue, int j,
			ExpressionNode tempNode, ExpressionValue singleValue, boolean right,
			StringTemplate tpl) {
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
		if (tempNode.containsFreeFunctionVariable(null)) {
			if (myValue.unwrap() instanceof MyList) {
				throw new MyError(kernel.getLocalization(), MyError.Errors.InvalidFunction);
			}
			FunctionNVar toProc = kernel.getAlgebraProcessor()
					.makeFunctionNVar(tempNode.deepCopy(kernel));
			if (toProc instanceof Function) {
				operationResult = kernel.getAlgebraProcessor().processFunction(
						(Function) toProc, new EvalInfo(false))[0];
			} else {
				operationResult = kernel.getAlgebraProcessor()
						.processFunctionNVar(toProc,
								new EvalInfo(false))[0];
			}
		}
		if (operationResult instanceof NumberValue) {
			operationResult = operationResult.isGeoElement()
					? operationResult.deepCopy(kernel)
					: ((NumberValue) operationResult)
							.toGeoElement(kernel.getConstruction());
			((GeoElement) operationResult)
					.setDefinition(tempNode.deepCopy(kernel));
		}
		// set listElement to operation result
		if (!operationResult.isExpressionNode()) {
			operationResult = new ExpressionNode(kernel, operationResult);
		}

		listElements.set(j, operationResult);

	}

	private void setIdentityMatrix() {
		isMatrix();
		listElements.clear();
		if (matrixRows == matrixCols) {
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

	}

	/**
	 * @return 0 if not a matrix
	 *
	 * @author Michael Borcherds
	 */
	public int getMatrixRows() {
		// check if already calculated
		if (matrixRows != -1 && matrixCols != -1) {
			return matrixRows;
		}

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
		if (matrixRows != -1 && matrixCols != -1) {
			return matrixCols;
		}

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
		gl.isDefined = !g.isUndefined();
		return gl;
	}

	/**
	 * Removes all elements from this list
	 */
	public void clear() {
		listElements.clear();
	}

	/**
	 * @return true if this list is a matrix
	 */
	@Override
	public boolean isMatrix() {
		// check if already calculated
		if (matrixRows > 0 && matrixCols > 0) {
			return true;
		}
		if (matrixRows == 0 && matrixCols == 0) {
			return false;
		}

		try {
			boolean isMatrix = true;

			int rows = size(), cols = 0;

			// check LHlist is a matrix
			if (isEquation(get(0))) {
				return false;
			}
			ExpressionValue singleValue = get(0)
					.evaluate(StringTemplate.defaultTemplate);
			if (singleValue == null) {
				matrixRows = matrixCols = 0;
				return false;
			}

			if (singleValue instanceof ListValue) {
				cols = ((ListValue) singleValue).getMyList().size();
				if (cols > 0 && isEquationOrList(
						((ListValue) singleValue).get(0))) {
					return false;
				}
				if (rows > 1) {
					// check all rows same length
					for (int i = 1; i < rows; i++) {
						if (isEquation(get(i))) {
							return false;
						}
						singleValue = get(i)
								.evaluate(StringTemplate.defaultTemplate);
						if (singleValue.evaluatesToList()) {
							MyList list = ((ListValue) singleValue).getMyList();
							if (list.size() != cols) {
								isMatrix = false;
							} else if ((list.size() > 0)
									&& isEquationOrList(list.get(0))) {
								isMatrix = false;
							}
						} else {
							isMatrix = false;
						}
					}
				}
			} else {
				isMatrix = false;
			}

			if (isMatrix) {
				matrixCols = cols;
				matrixRows = rows;
				isAllLists = listElements.stream().map(ExpressionValue::unwrap)
						.allMatch(ex -> ex instanceof ListValue);
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

	/**
	 *
	 * @param ex {@link ExpressionValue}
	 * @return if ex is an Equation
	 */
	static boolean isEquation(ExpressionValue ex) {
		return ex != null && (ex.unwrap() instanceof Equation);
	}

	private static boolean isEquationOrList(ExpressionValue ex) {
		return ex != null && (ex.unwrap() instanceof Equation || ex.unwrap() instanceof ListValue);
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
		ExpressionValue singleValue = list.get(col)
				.evaluate(StringTemplate.defaultTemplate);
		if (singleValue instanceof ListValue) {
			ExpressionValue ev = ((ListValue) singleValue).getMyList().get(row);
			if (ev.any(Inspecting::isFunctionVariable)) {
				return convertToFunction(ev, list.getKernel());
			}
			return ev.evaluate(StringTemplate.defaultTemplate);
		}
		return null;
	}

	/**
	 * @param list
	 *            matrix
	 * @param row
	 *            row number (starts with 0)
	 * @param col
	 *            col number (starts with 0)
	 * @return numeric value of the cell at given position in given list
	 */
	public static double getCellAsDouble(MyList list, int row, int col) {
		ExpressionValue singleValue = list.get(col)
				.evaluate(StringTemplate.defaultTemplate);
		if (singleValue instanceof ListValue) {
			ExpressionValue ev = ((ListValue) singleValue).getMyList().get(row);
			return ev.evaluateDouble();
		}
		return Double.NaN;
	}

	/**
	 * @param ev ExpressionValue
	 * @param kernel Kernel
	 * @return The passed ExpressionValue as either a {@link FunctionNVar} or {@link Function}
	 */
	private static ExpressionValue convertToFunction(ExpressionValue ev, Kernel kernel) {
		FunctionVarCollector fun = FunctionVarCollector.getCollector();
		ev.traverse(fun);
		FunctionVariable[] fVars = fun.buildVariables(kernel);
		return fVars.length == 1
				? new Function(ev.wrap(), fVars) : new FunctionNVar(ev.wrap(), fVars);
	}

	@Override
	public String toValueString(StringTemplate tpl) {
		return toString(tpl, true, true);
	}

	@Override
	public String toLaTeXString(boolean symbolic, StringTemplate tpl) {
		StringBuilder toLaTeXString = new StringBuilder();
		if (size() == 0) {
			// in schools the emptyset symbol is typically not used, see #
			// return "\\emptyset";

			// correctly
			return "\\left\\{ \\right\\}";
		} else if (isMatrix()
				&& !(get(0).unwrap() instanceof ListValue
						&& get(0).getListDepth() > 1)) {

				toLaTeXString.append("\\left(\\begin{array}{");
				for (int i = 0; i < matrixCols; i++) {
					// nice alignment for eg {{-1,1},{1,-1}} in CAS
					toLaTeXString.append("r");
				}
				toLaTeXString.append("}");

				for (int i = 0; i < size(); i++) {
					ListValue singleValue = (ListValue) get(i)
							.evaluate(StringTemplate.defaultTemplate);

					if (singleValue.size() > 0) {
						toLaTeXString.append(singleValue.get(0)
								.toLaTeXString(symbolic, tpl));
						for (int j = 1; j < singleValue.size(); j++) {
							toLaTeXString.append("&");
							toLaTeXString.append(singleValue.get(j)
									.toLaTeXString(symbolic, tpl));
						}
					}

					toLaTeXString.append("\\\\");
				}
				toLaTeXString.append("\\end{array}\\right)");

		} else {
			toLaTeXString.append(" \\left\\{ ");

			toLaTeXString.append(toLaTeXStringNoBrackets(symbolic, tpl));

			toLaTeXString.append(" \\right\\} ");
		}
		return toLaTeXString.toString();
	}

	/**
	 * @param symbolic
	 *            whether to substitute numbers
	 * @param tpl
	 *            output template
	 * @return string representation without brackets
	 */
	public String toLaTeXStringNoBrackets(boolean symbolic,
			StringTemplate tpl) {
		StringBuilder toLaTeXString = new StringBuilder();
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
		return toLaTeXString.toString();
	}

	@Override
	public String toString(StringTemplate tpl) {
		return toString(tpl, false, true);
	}

	/**
	 * Adapted from GeoList
	 *
	 * @param tpl
	 *            output template
	 * @param valueMode
	 *            true to substitute numbers
	 * @param printBrackets
	 *            true to include {} (needs to be false for f(x,y) as {x,y} is
	 *            MyList too
	 * @return string representation of this list
	 */
	public String toString(StringTemplate tpl, boolean valueMode,
			boolean printBrackets) {
		if (tpl.getStringType() == ExpressionNodeConstants.StringType.LATEX
				&& isMatrix() && printBrackets && isAllLists
				&& (valueMode || isExpressions())) {
			return toMatrixString(tpl);
		} else {
			return toFlatString(tpl, valueMode, printBrackets);
		}
	}

	private boolean isExpressions() {
		return !listElements.isEmpty() && !listElements.get(0).unwrap().isGeoElement();
	}

	private String toFlatString(StringTemplate tpl, boolean valueMode, boolean printBrackets) {
		StringBuilder sb = new StringBuilder();

		if (printBrackets) {
			tpl.leftCurlyBracket(sb);
		}
		// first (n-1) elements
		int lastIndex = listElements.size() - 1;
		if (lastIndex > -1) {
			for (int i = 0; i < lastIndex; i++) {
				ExpressionValue exp = listElements.get(i);
				sb.append(valueMode ? exp.toOutputValueString(tpl)
						: exp.toString(tpl)); // .toOutputValueString());
				tpl.getCommaOptionalSpace(sb, kernel.getLocalization());
			}

			// last element
			ExpressionValue exp = listElements.get(lastIndex);
			sb.append(valueMode ? exp.toOutputValueString(tpl)
					: exp.toString(tpl));
		}
		if (printBrackets) {
			tpl.rightCurlyBracket(sb);
		}
		return sb.toString();
	}

	private String toMatrixString(StringTemplate tpl) {
		ExpressionValue e0 = listElements.get(0).unwrap();
		final int cols = ((ListValue) e0).size();

		final StringBuilder sb = new StringBuilder();

		sb.append("\\left(\\begin{array}{");
		// eg rr
		for (int i = 0; i < cols; i++) {
			sb.append('r');
		}
		sb.append("}");
		for (int i = 0; i < size(); i++) {
			// we can assume elements are ListValues because of isMatrix check
			final ListValue row = (ListValue) listElements.get(i).unwrap();
			for (int j = 0; j < row.size(); j++) {
				sb.append(ExpressionNode.toLaTeXString(row.get(j).unwrap(), true, tpl));
				if (j < (row.size() - 1)) {
					sb.append("&");
				}
			}
			sb.append("\\\\");
		}
		sb.append(" \\end{array}\\right)");
		return sb.toString();
	}

	@Override
	public int size() {
		return listElements.size();
	}

	@Override
	public void resolveVariables(EvalInfo info) {
		for (int i = 0; i < listElements.size(); i++) {
			ExpressionValue en = listElements.get(i);
			en.resolveVariables(info);
		}
	}

	/**
	 * @param i
	 *            index
	 * @return i-th element of the list
	 */
	@Override
	public ExpressionValue get(int i) {
		return listElements.get(i);
	}

	/**
	 * Replaces element for given index, index must be within (0, length -1)
	 *
	 * @param i
	 *            index (0 based)
	 * @param ev
	 *            new value
	 * @return old value
	 */
	public ExpressionValue setListElement(int i, ExpressionValue ev) {
		return listElements.set(i, ev);
	}

	@Override
	public boolean isConstant() {
		for (ExpressionValue listElement : listElements) {
			if (!listElement.isConstant()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean isLeaf() {
		return true;
	}

	@Override
	public boolean isNumberValue() {
		return false;
	}

	@Override
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

	@Override
	public void getVariables(Set<GeoElement> variables, SymbolicMode mode) {
		for (ExpressionValue listElement : listElements) {
			listElement.getVariables(variables, mode);
		}
	}

	@Override
	final public boolean contains(ExpressionValue ev) {
		return ev == this;
	}

	@Override
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
	 * @return TRUE iff myList contains a, FALSE if not, UNKNOWN when CAS needed and not loaded
	 */
	public static ExtendedBoolean isElementOf(ExpressionValue a, MyList myList) {
		for (int i = 0; i < myList.size(); i++) {
			ExpressionValue ev = myList.get(i)
					.evaluate(StringTemplate.defaultTemplate);

			// g:x=0, g isElementOf {x=0} gives null here
			// see #1535
			if (ev == null) {
				Log.warn(myList.get(i) + " cannot be evaluated");
				continue;
			}
			ExtendedBoolean found = ExpressionNode.isEqual(a, ev);
			if (found != ExtendedBoolean.FALSE) {
				return found;
			}
		}

		return ExtendedBoolean.FALSE;
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
			ExpressionValue ev2 = list2.get(i).evaluate(tpl);
			boolean hasEqualMember = false;
			for (int j = 0; j < list1.size(); j++) {
				ExpressionValue ev1 = list1.get(j).evaluate(tpl);

				if (ExpressionNode.isEqual(ev1, ev2).boolVal()) {
					hasEqualMember = true;
					break;
				}

			}

			if (!hasEqualMember) {
				return false;
			}

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
			return list1.size() != 0;
		}

		for (int i = 0; i < list2.size(); i++) {
			ExpressionValue ev2 = list2.get(i).evaluate(tpl);
			boolean hasEqualMember = false;
			for (int j = 0; j < list1.size(); j++) {
				ExpressionValue ev1 = list1.get(j).evaluate(tpl);

				if (ExpressionNode.isEqual(ev1, ev2).boolVal()) {
					hasEqualMember = true;
					break;
				}

			}

			if (!hasEqualMember) {
				return false;
			}

		}

		// now must check sets aren't equal
		for (int i = 0; i < list1.size(); i++) {
			ExpressionValue ev1 = list1.get(i)
					.evaluate(StringTemplate.defaultTemplate);
			boolean hasEqualMember = false;
			for (int j = 0; j < list2.size(); j++) {
				ExpressionValue ev2 = list2.get(j)
						.evaluate(StringTemplate.defaultTemplate);
				if (ExpressionNode.isEqual(ev1, ev2).boolVal()) {
					hasEqualMember = true;
					break;
				}
			}
			// we've found an element without a match
			// so lists are not equal
			if (!hasEqualMember) {
				return true;
			}

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
	public static MyList setDifference(Kernel kernel, MyList list1,
			MyList list2) {

		if (list2.size() == 0) {
			return list1;
		}

		MyList ret = new MyList(kernel);
		if (list1.size() == 0) {
			return ret;
		}

		for (int i = 0; i < list1.size(); i++) {
			ExpressionValue ev0 = list1.get(i);
			ExpressionValue ev1 = ev0.evaluate(StringTemplate.defaultTemplate);
			boolean addToList = true;
			for (int j = 0; j < list2.size(); j++) {
				ExpressionValue ev2 = list2.get(j)
						.evaluate(StringTemplate.defaultTemplate);
				if (ExpressionNode.isEqual(ev1, ev2).boolVal()) {
					addToList = false;
					break;
				}
			}
			if (addToList) {
				ret.addListElement(ev0);
			}
		}

		return ret;

	}

	@Override
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
		ExpressionValue ax = get(0);
		ExpressionValue ay = get(1);
		ExpressionValue bx = list.get(0);
		ExpressionValue by = list.get(1);

		ExpressionNode en = new ExpressionNode(kernel, ax, Operation.MULTIPLY,
				by);
		ExpressionNode en2 = new ExpressionNode(kernel, ay, Operation.MULTIPLY,
				bx);
		if (list.size() == 2 || size() == 2) {
			listElements.add(2,
					new ExpressionNode(kernel, en, Operation.MINUS, en2));
			listElements.set(0, new ExpressionNode(kernel,
					new MyDouble(kernel, 0.0), Operation.NO_OPERATION, null));
			listElements.set(1, new ExpressionNode(kernel,
					new MyDouble(kernel, 0.0), Operation.NO_OPERATION, null));
			return;
		}

		ExpressionNode x, y, z;
		// size 3
		z = new ExpressionNode(kernel, en, Operation.MINUS, en2);
		ExpressionValue az = get(2);
		ExpressionValue bz = list.get(2);
		en = new ExpressionNode(kernel, ay, Operation.MULTIPLY, bz);
		en2 = new ExpressionNode(kernel, az, Operation.MULTIPLY, by);
		x = new ExpressionNode(kernel, en, Operation.MINUS, en2);

		en = new ExpressionNode(kernel, az, Operation.MULTIPLY, bx);
		en2 = new ExpressionNode(kernel, ax, Operation.MULTIPLY, bz);
		y = new ExpressionNode(kernel, en, Operation.MINUS, en2);

		listElements.set(0, x);
		listElements.set(1, y);
		listElements.set(2, z);
	}

	/**
	 * @return kernel
	 */
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

	@Override
	public void replaceChildrenByValues(GeoElement geo) {
		for (int i = 0; i < size(); i++) {
			ExpressionValue insert = get(i);
			if (insert instanceof ReplaceChildrenByValues) {
				((ReplaceChildrenByValues) insert).replaceChildrenByValues(geo);
			}
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
			ret.listElements
					.add(ExpressionNode.copy(listElements.get(i), kernel2));
		}
		return ret;
	}

	@Override
	public ExpressionValue traverse(Traversing t) {
		ExpressionValue v = t.process(this);
		for (int i = 0; i < size(); i++) {
			ExpressionValue insert = get(i);
			listElements.set(i, insert.traverse(t));
		}
		return v;
	}

	@Override
	public int getChildCount() {
		return listElements.size();
	}

	@Override
	public ExpressionValue getChild(int index) {
		return listElements.get(index);
	}

	@Override
	public ExpressionValue getItem(int i) {
		return listElements.get(i);
	}

	@Override
	public ExpressionNode wrap() {
		return new ExpressionNode(kernel, this);
	}

	/**
	 * @param val
	 *            value to be added to the end
	 * @param offset
	 *            number of elements to skip
	 */
	public void addQue(double val, int offset) {
		if (listElements.size() < offset + 1) {
			return;
		}
		MyDouble removed = (MyDouble) listElements.get(offset);
		for (int read = offset + 1; read < listElements.size(); read++) {
			listElements.set(read - 1, listElements.get(read));
		}
		removed.add(val - removed.getDouble());
		listElements.set(listElements.size() - 1, removed);

	}

	@Override
	public int getListDepth() {
		return isMatrix() ? 2 : 1;
	}

	@Override
	public ListValueType getValueType() {
		return ListValueType.of(listElements.isEmpty() ? ValueType.UNKNOWN
				: listElements.get(0).getValueType());
	}

	/**
	 * @param xEval
	 *            list or simple value
	 * @param idx
	 *            index
	 * @return value from list at given index or simple value
	 */
	public static ExpressionValue get(ExpressionValue xEval, int idx) {
		return xEval instanceof ListValue
				? ((ListValue) xEval).get(idx) : xEval;
	}

	/**
	 * @param other other list
	 * @return whether this and the other list have equal elements
	 */
	public boolean isEqual(ListValue other) {
		if (size() != other.size()) {
			return false;
		}
		for (int i = 0; i < size(); i++) {
			if (!ExpressionNode.isEqual(get(i).evaluate(StringTemplate.maxDecimals),
					other.get(i).evaluate(StringTemplate.maxDecimals)).boolVal()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @return elements of the list as a stream.
	 */
	public Stream<ExpressionValue> elements() {
		return listElements.stream();
	}
}
