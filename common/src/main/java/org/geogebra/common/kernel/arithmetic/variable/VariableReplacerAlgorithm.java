package org.geogebra.common.kernel.arithmetic.variable;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.ArcTrigReplacer;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.MySpecialDouble;
import org.geogebra.common.kernel.parser.FunctionParser;
import org.geogebra.common.kernel.parser.function.ParserFunctions;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.StringUtil;

import com.himamis.retex.editor.share.util.Unicode;

/**
 * Finds the variables that need to be replaced.
 */
public class VariableReplacerAlgorithm {

	private Kernel kernel;
	private DerivativeCreator derivativeCreator;

	private boolean multipleUnassignedAllowed = false;

	/**
	 * @param kernel The kernel.
	 */
	public VariableReplacerAlgorithm(Kernel kernel) {
		this.kernel = kernel;
		derivativeCreator = new DerivativeCreator(kernel);
	}

	/**
	 * @param expressionString The expression string.
	 * @return The variable that needs to be replaced,
	 * or the expression in which some parts are already replaced.
	 */
	@SuppressWarnings("hiding")
	public ExpressionValue replace(String expressionString) {
		return tokenize(expressionString);
	}

	private ExpressionValue tokenize(String expressionString) {
		ParserFunctions parserFunctions = kernel.getApplication()
			.getParserFunctions(multipleUnassignedAllowed);
		InputTokenizer tokenizer = new InputTokenizer(kernel, parserFunctions, expressionString);
		String next = expressionString;
		if (tokenizer.hasToken()) {
			next = tokenizer.next();
			if (next.startsWith("log_")) {
				ExpressionValue logIndex = FunctionParser.getLogIndex(expressionString, kernel);
				if (logIndex != null) {
					ExpressionValue logArg = tokenize(getLogArg(expressionString));
					return new ExpressionNode(kernel, logIndex, Operation.LOGB, logArg);
				}
			}
			Operation op = parserFunctions.getSingleArgumentOp(next);
			op = ArcTrigReplacer.getDegreeInverseTrigOp(op);
			if (op != null) {
				ExpressionValue arg = tokenize(tokenizer.getInputRemaining());
				return arg.wrap().apply(op);
			}
			ExpressionValue v1 = replaceToken(next);

			if (!multipleUnassignedAllowed && v1 instanceof Variable) {
				return parseReverse(expressionString);
			}

			if (tokenizer.hasToken()) {
				ExpressionValue v2 = tokenize(tokenizer.getInputRemaining());
				return leftProduct(v1, v2);
			}
			return v1;
		}

		return replaceToken(next);
	}

	private ExpressionValue parseReverse(String expressionString) {
		if (expressionString.endsWith("deg")) {
			return buildReverseProduct(expressionString, 3);
		}
		String lastChar = expressionString.substring(expressionString.length() - 1);
		if (isCharVariableName(lastChar) || Unicode.PI_STRING.equals(lastChar)) {
			return buildReverseProduct(expressionString, 1);
		}
		return replaceToken(expressionString);
	}

	private ExpressionValue buildReverseProduct(String expressionString, int suffixLength) {
		int length = expressionString.length() - suffixLength;
		ExpressionValue left = parseReverse(expressionString.substring(0, length));
		return left.wrap().multiplyR(replaceToken(expressionString.substring(length)));
	}

	private ExpressionNode leftProduct(ExpressionValue v1, ExpressionValue v2) {
		if (isProduct(v2)) {
			return leftProduct(v1, v2.wrap().getLeft()).multiplyR(v2.wrap().getRight());
		}
		return v1.wrap().multiplyR(v2);
	}

	private boolean isProduct(ExpressionValue value) {
		return value.wrap().getOperation() == Operation.MULTIPLY;
	}

	private ExpressionValue replaceToken(String expressionString) {
		ExpressionValue derivative = getDerivative(expressionString);
		if (derivative != null) {
			return derivative;
		}

		ExpressionValue geo = lookupOrProduct(expressionString);
		if (geo != null) {
			return geo;
		}

		if ("deg".equals(expressionString)) {
			return new MySpecialDouble(kernel, Kernel.PI_180, Unicode.DEGREE_STRING);
		}

		if ("pi".equals(expressionString) || Unicode.PI_STRING.equals(expressionString)) {
			return new MySpecialDouble(kernel, Math.PI, Unicode.PI_STRING);
		}
		MySpecialDouble mult = consumeConstant(expressionString);
		if (mult != null) {
			return mult;
		}

		if (InputTokenizer.isImaginaryUnit(expressionString)) {
			return kernel.getImaginaryUnit();
		}

		if (isCharVariableName(expressionString)) {
			return new FunctionVariable(kernel, expressionString);
		}

		return new Variable(kernel, expressionString);
	}

	private ExpressionValue getDerivative(String expressionString) {
		// holds powers of x,y,z: eg {"xxx","y","zzzzz"}
		return expressionString.endsWith("'")
				&& kernel.getAlgebraProcessor().enableStructures()
				? derivativeCreator.getDerivative(expressionString)
				: null;
	}

	private MySpecialDouble consumeConstant(String expressionString) {
		int numberLength = 0;
		while (numberLength < expressionString.length()
				&& StringUtil.isDigitOrDot(expressionString.charAt(numberLength))) {
			numberLength++;
		}
		if (numberLength != 0) {
			String num = expressionString.substring(0, numberLength);
			double value = MyDouble.parseDouble(kernel.getLocalization(), num);
			return new MySpecialDouble(kernel, value, num);
		}
		return null;
	}

	private ExpressionValue lookupOrProduct(String nameNoX) {
		if (kernel.getConstruction().isRegisteredFunctionVariable(nameNoX)
				&& !isCharVariableName(nameNoX)) {
			return new FunctionVariable(kernel, nameNoX);
		}

		ExpressionValue ret = kernel.lookupLabel(nameNoX);

		if (ret == null && "i".equals(nameNoX)) {
			ret = kernel.getImaginaryUnit();
		}
		if (ret == null && "e".equals(nameNoX)) {
			ret = kernel.getEulerNumber();
		}

		return ret;
	}

	private boolean isCharVariableName(String token) {
		if (token.length() != 1) {
			return false;
		}
		char charAtIndex = token.charAt(0);
		boolean isTheta = charAtIndex == Unicode.theta;
		boolean isT = charAtIndex == 't';
		boolean isXYZ = charAtIndex >= 'x' && charAtIndex <= 'z';
		return isTheta || isXYZ || isT;
	}

	private String getLogArg(String logString) {
		int indexOfArg = getIndexOfArg(logString);
		if (indexOfArg == -1) {
			return null;
		}

		return logString.substring(indexOfArg);
	}

	private static int getIndexOfArg(String logString) {
		int indexOfClosingBracket = logString.indexOf('}');
		if (indexOfClosingBracket != -1) {
			 return indexOfClosingBracket + 1;
		}

		int indexOfUnderline = logString.indexOf('_');
		if (indexOfUnderline != -1) {
			return indexOfUnderline + 2;
		}

		return -1;
	}

	/**
	 * We always allow splitting pp to p*p if p is a function variable, but if p
	 * is a generic variable (=unassigned) we only want to split it in input boxes.
	 * @param value whether to allow splitting with more than one Variable instance
	 */
	public void setMultipleUnassignedAllowed(boolean value) {
		multipleUnassignedAllowed = value;
	}
}
