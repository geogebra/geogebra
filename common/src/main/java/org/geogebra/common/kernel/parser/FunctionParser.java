package org.geogebra.common.kernel.parser;

import java.util.ArrayList;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.Evaluatable;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.MyList;
import org.geogebra.common.kernel.arithmetic.MyNumberPair;
import org.geogebra.common.kernel.arithmetic.MyVecNode;
import org.geogebra.common.kernel.arithmetic.Variable;
import org.geogebra.common.kernel.arithmetic3D.MyVec3DNode;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.geos.ParametricCurve;
import org.geogebra.common.kernel.parser.cashandlers.CommandDispatcherGiac;
import org.geogebra.common.main.App;
import org.geogebra.common.main.MyParseError;
import org.geogebra.common.plugin.Operation;

public class FunctionParser {
	Kernel kernel;
	App app;

	public FunctionParser(Kernel kernel) {
		this.kernel = kernel;
		this.app = kernel.getApplication();
	}

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

			if (cell == null && (geo == null
					|| !(geo.isGeoFunction() || geo.isGeoCurveCartesian()))) {
				if (label.startsWith("log_")) {
					String logIndex = label.substring(4);
					if (logIndex.startsWith("{")) {
						logIndex = logIndex.substring(1, logIndex.length() - 1);
					}
					double indexVal = MyDouble
							.parseDouble(kernel.getLocalization(), logIndex);

					return new ExpressionNode(kernel,
							new MyDouble(kernel, indexVal), Operation.LOGB,
							myList.getListElement(0));
				}
				int index = funcName.length() - 1;
				while (index >= 0 && cimage.charAt(index) == '\'') {
					order++;
					index--;
				}

				while (index < funcName.length()) {
					label = funcName.substring(0, index + 1);
					geo = kernel.lookupLabel(label);
					cell = kernel.lookupCasCellLabel(label);
					// stop if f' is defined but f is not defined, see #1444
					if (cell != null || (geo != null && (geo.isGeoFunction()
							|| geo.isGeoCurveCartesian()))) {
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
			if (op != null)
				return buildOpNode(op, myList);
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
		ExpressionValue geoExp = geoVar.resolveAsExpressionValue();

		// numer of arguments

		if (order > 0) { // derivative
							// n-th derivative of geo
			if (geo.isGeoFunction() || geo.isGeoCurveCartesian()) {// function

				kernel.getConstruction()
						.registerFunctionVariable(((ParametricCurve) geo)
								.getFunctionVariables()[0].toString(
										StringTemplate.defaultTemplate));

				return derivativeNode(kernel, geoExp, order,
						geo.isGeoCurveCartesian(), myList.getListElement(0));
			}
			String[] str = { "FunctionExpected", funcName };
			throw new MyParseError(kernel.getLocalization(), str);

		}
		if (geo instanceof Evaluatable) {// function
			if (geo instanceof ParametricCurve) {
				kernel.getConstruction()
						.registerFunctionVariable(((ParametricCurve) geo)
								.getFunctionVariables()[0].toString(
										StringTemplate.defaultTemplate));
			}
			return new ExpressionNode(kernel, geoExp, Operation.FUNCTION,
					myList.getListElement(0));
		} else if (geo instanceof GeoFunctionNVar) {
			return new ExpressionNode(kernel, geoExp, Operation.FUNCTION_NVAR,
					myList);
		} else if (geo.isGeoCurveCartesian()) {
			// vector function
			// at this point we have eg myList={{1,2}}, so we need first element
			// of myList
			return new ExpressionNode(kernel, geoExp, Operation.VEC_FUNCTION,
					myList.getListElement(0));
		} else if (geo.isGeoSurfaceCartesian()) {
			// vector function
			// at this point we have eg myList={{1,2}}, so we need first element
			// of myList
			return new ExpressionNode(kernel, geoExp, Operation.VEC_FUNCTION,
					myList);
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
		ExpressionNode expr = new ExpressionNode(kernel, geoExp,
				Operation.MULTIPLY_OR_FUNCTION,
				toFunctionArgument(myList, funcName));
		undecided.add(expr);
		return expr;

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
		String[] str = { "FunctionExpected", funcName };
		throw new MyParseError(kernel.getLocalization(), str);

	}

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

	public static ExpressionNode derivativeNode(Kernel kernel2,
			ExpressionValue geo, int order, boolean curve, ExpressionValue at) {
		return new ExpressionNode(kernel2,
				new ExpressionNode(kernel2, geo, Operation.DERIVATIVE,
						new MyDouble(kernel2, order)),
				curve ? Operation.VEC_FUNCTION : Operation.FUNCTION, at);
	}
}
