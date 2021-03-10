package org.geogebra.common.kernel.parser;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.VarString;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.Evaluatable;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.ListValue;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.MyList;
import org.geogebra.common.kernel.arithmetic.MyNumberPair;
import org.geogebra.common.kernel.arithmetic.MySpecialDouble;
import org.geogebra.common.kernel.arithmetic.MyVecNode;
import org.geogebra.common.kernel.arithmetic.SymbolicMode;
import org.geogebra.common.kernel.arithmetic.Traversing;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.arithmetic.variable.Variable;
import org.geogebra.common.kernel.arithmetic.variable.VariableReplacerAlgorithm;
import org.geogebra.common.kernel.arithmetic3D.MyVec3DNode;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.geos.GeoSymbolic;
import org.geogebra.common.kernel.geos.ParametricCurve;
import org.geogebra.common.kernel.parser.cashandlers.CommandDispatcherGiac;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.MyError.Errors;
import org.geogebra.common.main.MyParseError;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.StringUtil;

import com.google.j2objc.annotations.Weak;
import com.himamis.retex.editor.share.util.Unicode;

/**
 * Class for building function nodes from parser
 *
 */
public class FunctionParser {
	@Weak
	private final Kernel kernel;
	@Weak
	private final App app;
	private boolean inputBoxParsing = false;
	private ArrayList<ExpressionNode> multiplyOrFunctionNodes;

	/**
	 * @param kernel
	 *            kernel
	 */
	public FunctionParser(Kernel kernel, ArrayList<ExpressionNode> multiplyOrFunctionNodes) {
		this.kernel = kernel;
		this.app = kernel.getApplication();
		this.multiplyOrFunctionNodes = multiplyOrFunctionNodes;
	}

	public void setInputBoxParsing(boolean inputBoxParsing) {
		this.inputBoxParsing = inputBoxParsing;
	}

	/**
	 * @param cimage
	 *            function name+bracket, e.g. "f("
	 * @param myList
	 *            list of arguments
	 * @param undecided
	 *            list of nodes that may be either fns or multiplications
	 * @param giacParsing
	 *            whether this is for Giac
	 * @return function node
	 */
	public ExpressionNode makeFunctionNode(String cimage, MyList myList,
			ArrayList<ExpressionNode> undecided, boolean giacParsing, boolean geoGebraCASParsing) {
		String funcName = cimage.substring(0, cimage.length() - 1);
		ExpressionNode en;
		if (giacParsing) {
			// check for special Giac functions, e.g. diff, Psi etc.
			en = CommandDispatcherGiac.processCommand(funcName, myList, kernel);
			if (en != null) {
				return en.wrap();
			}
		}
		boolean forceCommand = cimage.charAt(cimage.length() - 1) == '[';
		GeoElement geo = null;
		GeoCasCell cell = null;
		// check for derivative using f'' notation
		int order = 0;

		String label = funcName;
		if (!forceCommand) {

			// f(t)=t(t+1)
			if (kernel.getConstruction().isRegisteredFunctionVariable(funcName)) {
				ExpressionNode expr = new ExpressionNode(kernel, new Variable(kernel, funcName),
						Operation.MULTIPLY_OR_FUNCTION, myList.getListElement(0));
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
			Operation op = getOperation(funcName, myList.size());
			if (op != null) {
				return buildOpNode(op, myList);
			}
			// pi(1.3)
			if (Unicode.PI_STRING.equals(funcName) || "pi".equals(funcName)) {
				MyDouble pi = new MySpecialDouble(kernel, Math.PI, Unicode.PI_STRING);
				return multiplication(pi, undecided, myList, funcName);
			}
			if (myList.size() == 1) {
				ExpressionNode splitCommand = makeSplitCommand(funcName,
						myList.getListElement(0), giacParsing || geoGebraCASParsing);
				if (splitCommand != null) {
					return splitCommand;
				}
			}
			Localization loc = kernel.getLocalization();
			if (!inputBoxParsing || "If".equals(loc.getReverseCommand(funcName))) {
				// function name does not exist: return command
				Command cmd = new Command(kernel, funcName, true, !giacParsing);
				for (int i = 0; i < myList.size(); i++) {
					cmd.addArgument(myList.getListElement(i).wrap());
				}
				return new ExpressionNode(kernel, cmd);
			}
		}
		// make sure we don't send 0th derivative to CAS
		if (cell != null && order > 0) {

			return derivativeNode(kernel, cell, order, false, myList.getItem(0));

		}
		boolean list = geo != null && geo.isGeoList();
		// f(t):=(t,t) produces line, we do not want CAS to use that line
		// Perhaps we should prefer cell over geo in all cases ?
		if (cell != null && (geo == null || geo.isGeoLine() || geo.isGeoConic()
				|| geo.isGeoSurfaceCartesian() || list)) {
			if (cell.getFunctionVariables().length < 2) {

				return new ExpressionNode(kernel, cell,
						list ? Operation.ELEMENT_OF : Operation.FUNCTION,
						list ? myList : myList.getListElement(0));
			}
			return new ExpressionNode(kernel, cell,
					list ? Operation.ELEMENT_OF : Operation.FUNCTION_NVAR, myList);

		}
		// create variable object for label to make sure
		// to handle lables like $A$1 correctly and keep the expression
		Variable geoVar = new Variable(kernel, label);
		ExpressionValue geoExp = geoVar.resolveAsExpressionValue(SymbolicMode.NONE,
				false, !inputBoxParsing);
		// numer of arguments

		if (order > 0) { // derivative
							// n-th derivative of geo
			if (hasDerivative(geo)) {// function

				kernel.getConstruction()
						.registerFunctionVariable(((VarString) geo).getFunctionVariables()[0]
								.toString(StringTemplate.defaultTemplate));

				return derivativeNode(kernel, geoExp, order, geo.isGeoCurveCartesian(),
						myList.getListElement(0));
			}
			throw new MyParseError(kernel.getLocalization(), Errors.FunctionExpected, funcName);

		}
		if (geo instanceof GeoFunctionNVar || geo instanceof GeoSymbolic) {
			return new ExpressionNode(kernel, geoExp, Operation.FUNCTION_NVAR, myList);
		} else if (geo instanceof Evaluatable) {// function
			if (geo instanceof ParametricCurve
					&& ((ParametricCurve) geo).getFunctionVariables() != null) {
				kernel.getConstruction()
						.registerFunctionVariable(((ParametricCurve) geo).getFunctionVariables()[0]
								.toString(StringTemplate.defaultTemplate));
			}
			return new ExpressionNode(kernel, geoExp, Operation.FUNCTION, myList.getListElement(0));
		} else if (geo != null
				&& (geo.isGeoCurveCartesian() || (geo.isGeoLine() && geo.isGeoElement3D()))) {
			// vector function
			// at this point we have eg myList={{1,2}}, so we need first element
			// of myList
			return new ExpressionNode(kernel, geoExp, Operation.VEC_FUNCTION,
					myList.getListElement(0));
		} else if (geo != null && geo.isGeoSurfaceCartesian()) {
			ExpressionValue vecArg = myList;
			if (myList.size() == 1 && !(myList.getItem(0) instanceof ListValue)) {
				vecArg = myList.getItem(0);
			}
			return new ExpressionNode(kernel, geoExp, Operation.VEC_FUNCTION, vecArg);
		} else if (list) {
			// list1(1) to get first element of list1 #1115
			return new ExpressionNode(kernel, geoExp, Operation.ELEMENT_OF, myList);
		}

		if (inputBoxParsing && geoExp.wrap().getRight() instanceof Evaluatable) {
			ExpressionValue left = geoExp.wrap().getLeft();

			return new ExpressionNode(kernel, geoExp.wrap().getRight(),
					Operation.FUNCTION, toFunctionArgument(myList, funcName)).multiply(left);
		}

		// a(b) becomes a*b because a is not a function, no list, and no curve
		// e.g. a(1+x) = a*(1+x) when a is a number
		return multiplication(geoExp, undecided, myList, funcName);
	}

	private ExpressionNode makeSplitCommand(String funcName, ExpressionValue arg,
			boolean casParsing) {
		if (!casParsing
				&& !kernel.getLoadingMode()
				&& !isCommand(funcName)) {
			VariableReplacerAlgorithm replacer = new VariableReplacerAlgorithm(kernel);
			replacer.setMultipleUnassignedAllowed(inputBoxParsing);
			ExpressionNode exprWithDummyArg = replacer.replace(funcName + "$").wrap();
			if (exprWithDummyArg.getOperation() == Operation.MULTIPLY
					&& (Operation.isSimpleFunction(exprWithDummyArg.getRightTree().getOperation())
					|| exprWithDummyArg.getRightTree().getOperation() == Operation.LOGB)) {
				if (exprWithDummyArg.getOperation() == Operation.MULTIPLY) {
					// MULTIPLY_OR_FUNCTION is handled correctly when followed by power
					exprWithDummyArg.setOperation(Operation.MULTIPLY_OR_FUNCTION);
					multiplyOrFunctionNodes.add(exprWithDummyArg);
				}
				Traversing.VariableReplacer dummyArgReplacer = Traversing.VariableReplacer
						.getReplacer("$", arg, kernel);
				return exprWithDummyArg.traverse(dummyArgReplacer).wrap();
			}
		}
		return null;
	}

	private boolean isCommand(String funcName) {
		if (kernel.getApplication().getInternalCommand(funcName) != null) {
			return true;
		}
		try {
			Commands.valueOf(funcName);
			return true;
		} catch(Exception notFound){
			// not a command
		}
		return false;
	}

	private Operation getOperation(String funcName, int size) {
		// Compatibility mode for file opening, see https://jira.geogebra.org/browse/WLY-13
		// Files saved after that ticket was implemented contain ln and lg explicitly.
		if (size == 1 && "log".equals(funcName) && kernel.getLoadingMode()) {
			return Operation.LOG;
		}

		return app.getParserFunctions(inputBoxParsing).get(funcName, size);
	}

	private static boolean hasDerivative(GeoElement geo) {
		return geo.isGeoFunction() || geo.isGeoCurveCartesian() || (geo instanceof GeoSymbolic);
	}

	private ExpressionNode multiplication(ExpressionValue geoExp,
			ArrayList<ExpressionNode> undecided, MyList myList, String funcName) {
		ExpressionNode expr = new ExpressionNode(kernel, geoExp, Operation.MULTIPLY_OR_FUNCTION,
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
			return new MyVecNode(kernel, list.getListElement(0), list.getListElement(1));
		case 3:
			return new MyVec3DNode(kernel, list.getListElement(0), list.getListElement(1),
					list.getListElement(2));

		}
		throw new MyParseError(kernel.getLocalization(), Errors.FunctionExpected, funcName);

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
			return new ExpressionNode(kernel, list.getListElement(0), op, list.getListElement(1));
		// for beta regularized
		case 3:
			return new ExpressionNode(kernel,
					new MyNumberPair(kernel, list.getListElement(0), list.getListElement(1)), op,
					list.getListElement(2));
		// for sum (from CAS)
		case 4:
			return new ExpressionNode(kernel,
					new MyNumberPair(kernel, list.getListElement(0), list.getListElement(1)), op,
					new MyNumberPair(kernel, list.getListElement(2), list.getListElement(3)));
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
	public static ExpressionNode derivativeNode(Kernel kernel, ExpressionValue geo, int order,
			boolean curve, ExpressionValue functionArgument) {

		ExpressionValue left = new ExpressionNode(kernel, geo, Operation.DERIVATIVE,
				new MyDouble(kernel, order));
		Operation operation = curve ? Operation.VEC_FUNCTION : Operation.FUNCTION;
		return new ExpressionNode(kernel, left, operation, functionArgument);
	}

	public ExpressionNode assignment(ExpressionNode rhs0, String funLabel, List<String> localVars,
			ExpressionValue cond) {
		ExpressionNode rhs = rhs0;
		if (cond != null) {
			rhs = new ExpressionNode(kernel, cond, Operation.IF_SHORT, rhs);
		}

		// command without variables: return expressionnode
		// only check for function variables outside of command, eg
		// Derivative[f(x)]+x #4533
		if (rhs.getLeft() instanceof Command && !rhs.containsFreeFunctionVariable(null)) {
			rhs.setLabel(funLabel);
			return rhs;
		}

		// function: wrap function in ExpressionNode
		// number of vars
		int n = localVars.size();
		Operation op = getOperation(funLabel, n);
		if (op != null) {
			if (n == 1) {
				return new Equation(kernel,
						new FunctionVariable(kernel, localVars.get(0)).wrap().apply(op), rhs)
								.wrap();
			}
			MyList vars = new MyList(kernel, n);
			for (String localVar : localVars) {
				FunctionVariable funVar = new FunctionVariable(kernel, localVar);
				vars.addListElement(funVar);
			}
			return new Equation(kernel, buildOpNode(op, vars), rhs).wrap();
		}
		GeoElement existing = kernel.lookupLabel(funLabel);
		if (existing instanceof GeoSymbolic) {
			ExpressionNode lhs = new ExpressionNode(kernel, existing, Operation.FUNCTION,
					new FunctionVariable(kernel, localVars.get(0)));
			return new Equation(kernel, lhs, rhs).wrap();
		}
		FunctionVariable[] funVar = new FunctionVariable[n];
		for (int i = 0; i < n; i++) {
			funVar[i] = new FunctionVariable(kernel, localVars.get(i));
		}
		FunctionNVar fun = kernel.getArithmeticFactory().newFunction(rhs, funVar);
		fun.setLabel(funLabel);
		rhs = new ExpressionNode(kernel, fun);
		rhs.setLabel(funLabel);
		return rhs;
	}

	/**
	 * Parse expression image(x) where image ends with supersript digits.
	 *
	 * @param image
	 *            function
	 * @param en
	 *            argument
	 * @return sin^2(x) or x^2*(x+1)
	 */
	final public ExpressionNode handleTrigPower(String image, ValidExpression en) {
		int pos = image.length() - 2;
		while (pos >= 0 && (Unicode.isSuperscriptDigit(image.charAt(pos))
				|| Unicode.SUPERSCRIPT_MINUS == image.charAt(pos))) {
			pos--;
		}
		String operation = image.substring(0, pos + 1);
		String power = image.substring(pos + 1, image.length() - 1);
		if ("x".equals(operation) || "y".equals(operation)
				|| "z".equals(operation)) {
			return new ExpressionNode(kernel,
					new ExpressionNode(kernel,
							new FunctionVariable(kernel, operation),
							Operation.POWER, convertIndexToNumber(power)),
					Operation.MULTIPLY_OR_FUNCTION, en);
		}
		GeoElement ge = kernel.lookupLabel(operation);
		Operation type = getOperation(operation, 1);
		if (ge != null || type == null) {
			return new ExpressionNode(kernel,
					new ExpressionNode(kernel, new Variable(kernel, operation),
							Operation.POWER, convertIndexToNumber(power)),
					Operation.MULTIPLY_OR_FUNCTION, en);
		}

		// sin^(-1)(x) -> arcsin(x), log^(-1)(x) -> (log(x))^(-1)
		if (Unicode.SUPERSCRIPT_MINUS_ONE_STRING.equals(power)) {
			return minusFirstPower(type, en);
		}

		return new ExpressionNode(kernel,
				new ExpressionNode(kernel, en, type, null), Operation.POWER,
				convertIndexToNumber(power));
	}

	/**
	 * Take 42 from "<sup>42</sup>(".
	 *
	 * @param str
	 *            superscript text
	 * @return number
	 */
	final public MyDouble convertIndexToNumber(String str) {
		return new MyDouble(kernel, StringUtil.indexToNumber(str));
	}

	/**
	 * Builds product of two expressions
	 *
	 * @param left
	 *            left factor
	 * @param right
	 *            right factor
	 * @param giacParsing
	 *            whether this is from GIAC
	 * @return product of factors
	 */
	public ExpressionValue multiplySpecial(ExpressionValue left,
			ExpressionValue right, boolean giacParsing, boolean geogebraCasParsing) {

		String leftImg;
		App app = kernel.getApplication();

		// sin x in GGB is function application if "sin" is not a variable
		if (left instanceof Variable) {
			leftImg = left.toString(StringTemplate.defaultTemplate);
			Operation op = app.getParserFunctions(inputBoxParsing).getSingleArgumentOp(leftImg);

			if (op != null) {
				return new ExpressionNode(kernel, right, op, null);
			}
			if (leftImg.startsWith("log_")
					&& kernel.lookupLabel(leftImg) == null) {
				ExpressionValue index = FunctionParser.getLogIndex(leftImg,
						kernel);

				if (index != null) {
					return new ExpressionNode(kernel, index, Operation.LOGB,
							right);
				}
			}
			ExpressionNode splitFunctionExp = makeSplitCommand(leftImg, right, giacParsing);
			if (splitFunctionExp != null) {
				return splitFunctionExp;
			}
			// sin^2 x
		} else if (left instanceof ExpressionNode
				&& ((ExpressionNode) left).getOperation() == Operation.POWER
				&& ((ExpressionNode) left).getLeft() instanceof Variable) {
			leftImg = ((ExpressionNode) left).getLeft()
					.toString(StringTemplate.defaultTemplate);
			Operation op = app.getParserFunctions(inputBoxParsing).getSingleArgumentOp(leftImg);
			if (op != null) {
				ExpressionValue exponent = ((ExpressionNode) left).getRight()
						.unwrap();
				return inverseOrPower(op, right, exponent);

			} else {
				ExpressionNode splitCommand = makeSplitCommand(leftImg, right,
						giacParsing || geogebraCasParsing);
				if (splitCommand != null) {
					ExpressionValue exponent = ((ExpressionNode) left).getRight()
							.unwrap();
					return buildTrigPower(splitCommand, exponent);
				}
			}
			// x * sin x in GGB is function applied on the right if "sin" is not
			// a variable
			// a * b * f -- check if b*f needs special handling
		} else if (left instanceof ExpressionNode && (((ExpressionNode) left)
				.getOperation() == Operation.MULTIPLY)) {
			ExpressionValue bf = multiplySpecial(
					((ExpressionNode) left).getRight(), right,
					giacParsing, geogebraCasParsing);
			return bf == null ? null
					: new ExpressionNode(kernel,
					((ExpressionNode) left).getLeft(),
					Operation.MULTIPLY, bf);
			// +-b * f is parsed as (b +- ()) *f
		} else if (left instanceof ExpressionNode
				&& (((ExpressionNode) left).getOperation() == Operation.PLUSMINUS)
				&& (((ExpressionNode) left).getRight() instanceof MyNumberPair)) {
			ExpressionValue bf = multiplySpecial(((ExpressionNode) left).getLeft(), right,
					giacParsing, geogebraCasParsing);
			return bf == null ? null
					: new ExpressionNode(kernel, bf, Operation.PLUSMINUS,
					((ExpressionNode) left).getRight());
		}

		if (giacParsing) {
			// (a)(b) in Giac is function application
			if (left instanceof Variable) {
				Command ret = new Command(kernel,
						left.toString(StringTemplate.defaultTemplate), true,
						true);
				ret.addArgument(right.wrap());
				return ret;
				// c*(a)(b) in Giac: function applied on right subtree
			}
		}
		return null;
	}

	private ExpressionValue inverseOrPower(Operation op, ExpressionValue right,
			ExpressionValue exponent) {
		if (right.isOperation(Operation.POWER)
				&& !right.wrap().hasBrackets()
				&& right.wrap().getLeftTree().hasBrackets()) {
			ExpressionValue base = inverseOrPower(op, right.wrap().getLeft(), exponent);
			return new ExpressionNode(kernel, base, Operation.POWER, right.wrap().getRight());
		}
		if (exponent.isConstant()
				&& DoubleUtil.isEqual(-1, exponent.evaluateDouble())) {
			return minusFirstPower(op, right);
		}
		return new ExpressionNode(kernel, right, op, null)
				.power(exponent);
	}

	private ExpressionValue buildTrigPower(ExpressionNode splitCommand,
			ExpressionValue exponent) {
		ExpressionValue coefficient = splitCommand.getLeft();
		ExpressionNode trigExpression = splitCommand.getRight().wrap();
		ExpressionValue power = inverseOrPower(trigExpression.getOperation(),
				trigExpression.getLeft(), exponent);
		return coefficient.wrap().multiplyR(power);
	}

	/**
	 * @param type
	 *            operation
	 * @param en
	 *            argument
	 * @return inverse function for trig operations, reciprocal orherwise
	 */
	private ExpressionNode minusFirstPower(Operation type, ExpressionValue en) {
		switch (type) {
		case SIN:
		case COS:
		case TAN:
		case SINH:
		case COSH:
		case TANH:
			return new ExpressionNode(kernel, en, Operation.inverse(type), null);

		// asec(x) = acos(1/x)
		case SEC:
			return reciprocal(en).apply(Operation.ARCCOS);
		case CSC:
			return reciprocal(en).apply(Operation.ARCSIN);
		case SECH:
			return reciprocal(en).apply(Operation.ACOSH);
		case CSCH:
			return reciprocal(en).apply(Operation.ASINH);
		case COTH:
			return reciprocal(en).apply(Operation.ATANH);

		// acot(x) = pi/2 - atan(x)
		case COT:

			ExpressionNode halfPi = new ExpressionNode(kernel,
					new MyDouble(kernel, Math.PI), Operation.DIVIDE,
					new MyDouble(kernel, 2));
			return new ExpressionNode(kernel, halfPi, Operation.MINUS,
					new ExpressionNode(kernel, en, Operation.ARCTAN, null));

		default:
			ExpressionNode base = new ExpressionNode(kernel, en, type, null);
			return new ExpressionNode(kernel, base, Operation.POWER,
					new MyDouble(kernel, -1));
		}
	}

	private ExpressionNode reciprocal(ExpressionValue en) {
		return new ExpressionNode(kernel,
				new MyDouble(kernel, 1), Operation.DIVIDE, en);
	}
}
