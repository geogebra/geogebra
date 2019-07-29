package org.geogebra.common.kernel.parser;

import java.util.ArrayList;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.VarString;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.Evaluatable;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.ListValue;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.MyList;
import org.geogebra.common.kernel.arithmetic.MyNumberPair;
import org.geogebra.common.kernel.arithmetic.MySpecialDouble;
import org.geogebra.common.kernel.arithmetic.MyVecNode;
import org.geogebra.common.kernel.arithmetic.SymbolicMode;
import org.geogebra.common.kernel.arithmetic.variable.Variable;
import org.geogebra.common.kernel.arithmetic3D.MyVec3DNode;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.geos.GeoSymbolic;
import org.geogebra.common.kernel.geos.ParametricCurve;
import org.geogebra.common.kernel.parser.cashandlers.CommandDispatcherGiac;
import org.geogebra.common.main.App;
import org.geogebra.common.main.MyError.Errors;
import org.geogebra.common.main.MyParseError;
import org.geogebra.common.plugin.Operation;

import com.himamis.retex.editor.share.util.Unicode;

/**
 * Class for building function nodes from parser
 *
 */
public class FunctionParser {
	private final Kernel kernel;
	private final App app;

	/**
	 * @param kernel
	 *            kernel
	 */
	public FunctionParser(Kernel kernel) {
		this.kernel = kernel;
		this.app = kernel.getApplication();
	}

	/**
	 * @param cimage
	 *            function name+bracket, e.g. "f("
	 * @param myList
	 *            list of arguments
	 * @param undecided
	 *            list of nodes that may be either fns or multiplications
	 * @param GiacParsing
	 *            whether this is for Giac
	 * @return function node
	 */
	public ExpressionNode makeFunctionNode(String cimage, MyList myList,
			ArrayList<ExpressionNode> undecided, boolean GiacParsing) {
		String funcName = cimage.substring(0, cimage.length() - 1);
		ExpressionNode en;
		if (GiacParsing) {
			// check for special Giac functions, e.g. diff, Psi etc.
			en = CommandDispatcherGiac.processCommand(funcName, myList, kernel);
			if (en != null) {
				return en.wrap();
			}
		}
		boolean forceCommand = cimage.charAt(cimage.length() - 1) == '[';
		GeoElement geo = null;
		GeoElement cell = null;
		// check for derivative using f'' notation
		int order = 0;

		String label = funcName;
		if (!forceCommand) {

			// f(t)=t(t+1)
			if (kernel.getConstruction()
					.isRegistredFunctionVariable(funcName)) {
				ExpressionNode expr = new ExpressionNode(kernel,
						new Variable(kernel, funcName),
						Operation.MULTIPLY_OR_FUNCTION,
						myList.getListElement(0));
				undecided.add(expr);
				return expr;
			}

			geo = kernel.lookupLabel(funcName);
			cell = kernel.lookupCasCellLabel(funcName);

			if (cell == null && geo == null && label.startsWith("log_")) {
				ExpressionValue indexVal = getLogIndex(label, kernel);
				return new ExpressionNode(kernel, indexVal, Operation.LOGB,
						myList.getListElement(0));
			}

			if (cell == null && (geo == null || !hasDerivative(geo))) {

				int index = funcName.length() - 1;
				while (index >= 0 && cimage.charAt(index) == '\''
						&& kernel.getAlgebraProcessor().enableStructures()) {
					order++;
					index--;
				}

				while (index < funcName.length()) {
					label = funcName.substring(0, index + 1);
					geo = kernel.lookupLabel(label);
					cell = kernel.lookupCasCellLabel(label);
					// stop if f' is defined but f is not defined, see #1444
					if (cell != null || (geo != null && (hasDerivative(geo)))) {
						break;
					}

					order--;
					index++;
				}
			}
		}

		if (forceCommand || (geo == null && cell == null)) {
			Operation op = app.getParserFunctions().get(funcName,
					myList.size());
			if (op != null) {
				return buildOpNode(op, myList);
			}

			// pi(1.3)
			if (Unicode.PI_STRING.equals(funcName) || "pi".equals(funcName)) {
				MyDouble pi = new MySpecialDouble(kernel, Math.PI,
						Unicode.PI_STRING);
				return multiplication(pi, undecided, myList, funcName);
			}
			// function name does not exist: return command
			Command cmd = new Command(kernel, funcName, true, !GiacParsing);
			for (int i = 0; i < myList.size(); i++) {
				cmd.addArgument(myList.getListElement(i).wrap());
			}
			return new ExpressionNode(kernel, cmd);

		}
		// make sure we don't send 0th derivative to CAS
		if (cell != null && order > 0) {

			return derivativeNode(kernel, cell, order, false,
					myList.getItem(0));

		}
		boolean list = geo != null && geo.isGeoList();
		// f(t):=(t,t) produces line, we do not want CAS to use that line
		// Perhaps we should prefer cell over geo in all cases ?
		if (cell != null && (geo == null || geo.isGeoLine() || geo.isGeoConic()
				|| geo.isGeoSurfaceCartesian() || list)) {
			if (((GeoCasCell) cell).getFunctionVariables().length < 2) {

				return new ExpressionNode(kernel, cell,
						list ? Operation.ELEMENT_OF : Operation.FUNCTION,
						list ? myList : myList.getListElement(0));
			}
			return new ExpressionNode(kernel, cell,
					list ? Operation.ELEMENT_OF : Operation.FUNCTION_NVAR,
					myList);

		}
		// create variable object for label to make sure
		// to handle lables like $A$1 correctly and keep the expression
		Variable geoVar = new Variable(kernel, label);
		ExpressionValue geoExp = geoVar
				.resolveAsExpressionValue(SymbolicMode.NONE);
		// numer of arguments

		if (order > 0) { // derivative
							// n-th derivative of geo
			if (hasDerivative(geo)) {// function

				kernel.getConstruction()
						.registerFunctionVariable(((VarString) geo)
								.getFunctionVariables()[0].toString(
										StringTemplate.defaultTemplate));

				return derivativeNode(kernel, geoExp, order,
						geo.isGeoCurveCartesian(), myList.getListElement(0));
			}
			throw new MyParseError(kernel.getLocalization(), Errors.FunctionExpected,
					funcName);

		}
		if (geo instanceof GeoFunctionNVar || geo instanceof GeoSymbolic) {
			return new ExpressionNode(kernel, geoExp, Operation.FUNCTION_NVAR,
					myList);
		} else if (geo instanceof Evaluatable) {// function
			if (geo instanceof ParametricCurve
					&& ((ParametricCurve) geo).getFunctionVariables() != null) {
				kernel.getConstruction()
						.registerFunctionVariable(((ParametricCurve) geo)
								.getFunctionVariables()[0].toString(
										StringTemplate.defaultTemplate));
			}
			return new ExpressionNode(kernel, geoExp, Operation.FUNCTION,
					myList.getListElement(0));
		} else if (geo.isGeoCurveCartesian()
				|| (geo.isGeoLine() && geo.isGeoElement3D())) {
			// vector function
			// at this point we have eg myList={{1,2}}, so we need first element
			// of myList
			return new ExpressionNode(kernel, geoExp, Operation.VEC_FUNCTION,
					myList.getListElement(0));
		} else if (geo.isGeoSurfaceCartesian()) {
			ExpressionValue vecArg = myList;
			if (myList.size() == 1
					&& !(myList.getItem(0) instanceof ListValue)) {
				vecArg = myList.getItem(0);
			}
			return new ExpressionNode(kernel, geoExp, Operation.VEC_FUNCTION,
					vecArg);
		}
		// list1(1) to get first element of list1 #1115
		else if (list) {
			return new ExpressionNode(kernel, geoExp, Operation.ELEMENT_OF,
					myList);
			// String [] str = { "FunctionExpected", funcName };
			// throw new MyParseError(loc, str);
		}
		// a(b) becomes a*b because a is not a function, no list, and no curve
		// e.g. a(1+x) = a*(1+x) when a is a number

		return multiplication(geoExp, undecided, myList, funcName);
	}

	private static boolean hasDerivative(GeoElement geo) {
		return geo.isGeoFunction() || geo.isGeoCurveCartesian()
				|| (geo instanceof GeoSymbolic);
	}

	private ExpressionNode multiplication(ExpressionValue geoExp,
			ArrayList<ExpressionNode> undecided, MyList myList,
			String funcName) {
		ExpressionNode expr = new ExpressionNode(kernel, geoExp,
				Operation.MULTIPLY_OR_FUNCTION,
				toFunctionArgument(myList, funcName));
		undecided.add(expr);
		return expr;
	}

	/**
	 * @param label
	 *            label starting with log_
	 * @param kernel
	 *            kernel
	 * @return MyDouble if numeric index is present, null otherwise
	 */
	public static ExpressionValue getLogIndex(String label, Kernel kernel) {
		String afterUnderline = label.substring(4);
		String logIndex;
		if (afterUnderline.startsWith("{")) {
			int closingBracketIndex = afterUnderline.indexOf('}');
			logIndex = afterUnderline.substring(1, closingBracketIndex);
		} else {
			logIndex = afterUnderline.substring(0, 1);
		}
		try {
			return new GParser(kernel, kernel.getConstruction()).parseGeoGebraExpression(logIndex);
		} catch (Throwable e1) {
			return null;
		}
	}

	private ExpressionValue toFunctionArgument(MyList list, String funcName) {
		switch (list.size()) {
		case 1:
			return list.getListElement(0);
		case 2:
			return new MyVecNode(kernel, list.getListElement(0),
					list.getListElement(1));
		case 3:
			return new MyVec3DNode(kernel, list.getListElement(0),
					list.getListElement(1), list.getListElement(2));

		}
		throw new MyParseError(kernel.getLocalization(), Errors.FunctionExpected,
				funcName);

	}

	/**
	 * @param op
	 *            operation
	 * @param list
	 *            argument list
	 * @return expression
	 */
	public ExpressionNode buildOpNode(Operation op, MyList list) {
		switch (list.size()) {
		case 1:
			return new ExpressionNode(kernel, list.getListElement(0), op, null);
		case 2:
			return new ExpressionNode(kernel, list.getListElement(0), op,
					list.getListElement(1));
		// for beta regularized
		case 3:
			return new ExpressionNode(kernel,
					new MyNumberPair(kernel, list.getListElement(0),
							list.getListElement(1)),
					op, list.getListElement(2));
		// for sum (from CAS)
		case 4:
			return new ExpressionNode(kernel,
					new MyNumberPair(kernel, list.getListElement(0),
							list.getListElement(1)),
					op, new MyNumberPair(kernel, list.getListElement(2),
							list.getListElement(3)));
		default:
			return null;
		}
	}

	/**
	 * @param kernel
	 *            kernel
	 * @param geo
	 *            function
	 * @param order
	 *            derivative order
	 * @param curve
	 *            whether geo is a curve
	 * @param functionArgument
	 *            function argument
	 * @return expression for geo'''(functionArgument)
	 */
	public static ExpressionNode derivativeNode(
			Kernel kernel,
			ExpressionValue geo,
			int order,
			boolean curve,
			ExpressionValue functionArgument) {

		ExpressionValue left =
				new ExpressionNode(
						kernel,
						geo, Operation.DERIVATIVE, new MyDouble(kernel, order));
		Operation operation = curve ? Operation.VEC_FUNCTION : Operation.FUNCTION;
		return new ExpressionNode(kernel, left, operation, functionArgument);
	}
}
