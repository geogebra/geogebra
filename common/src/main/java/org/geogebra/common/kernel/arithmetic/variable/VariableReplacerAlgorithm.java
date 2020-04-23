package org.geogebra.common.kernel.arithmetic.variable;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.ArcTrigReplacer;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.MySpecialDouble;
import org.geogebra.common.kernel.arithmetic.variable.power.Exponents;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.parser.FunctionParser;
import org.geogebra.common.kernel.parser.ParseException;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.StringUtil;

import com.himamis.retex.editor.share.util.Unicode;

/**
 * Finds the variables that need to be replaced.
 */
public class VariableReplacerAlgorithm {

	private Kernel kernel;
	private DerivativeCreator derivativeCreator;
	private ProductCreator productCreator;

	private String expressionString;
	private String nameNoX;
	private Exponents exponents;
	private ExpressionValue geo;
	private int charIndex;

	/**
	 * @param kernel The kernel.
	 */
	public VariableReplacerAlgorithm(Kernel kernel) {
		this.kernel = kernel;
		derivativeCreator = new DerivativeCreator(kernel);
		productCreator = new ProductCreator(kernel);
		exponents = new Exponents();
	}

	/**
	 * @param expressionString The expression string.
	 * @return The variable that needs to be replaced,
	 * or the expression in which some parts are already replaced.
	 */
	@SuppressWarnings("hiding")
	public ExpressionValue replace(String expressionString) {
		this.expressionString = expressionString;

		// holds powers of x,y,z: eg {"xxx","y","zzzzz"}
		if (expressionString.endsWith("'")
				&& kernel.getAlgebraProcessor().enableStructures()) {

			ExpressionValue ret = derivativeCreator.getDerivative(expressionString);
			if (ret != null) {
				return ret;
			}
		}

		exponents.initWithZero();

		geo = lookupOrProduct(expressionString);
		if (geo != null) {
			return geo;
		}
		nameNoX = expressionString;
		int degPower = 0;
		while (nameNoX.length() > 0 && (geo == null)
				&& nameNoX.endsWith("deg")) {
			int length = nameNoX.length();
			degPower++;
			nameNoX = nameNoX.substring(0, length - 3);
			if (length > 3) {
				geo = kernel.lookupLabel(nameNoX);
			}

		}

		ExpressionValue logExpression = getLogExpression();
		if (logExpression != null) {
			return logExpression;
		}

		ExpressionValue resultOfReverseProcessing = processInReverse();
		if (resultOfReverseProcessing != null) {
			return resultOfReverseProcessing;
		}

		processPi();
		MySpecialDouble mult = consumeConstant(nameNoX);

		if (nameNoX.length() > 0 && geo == null) {
			return new Variable(kernel, nameNoX);
		}
		ExpressionNode ret = productCreator.getFunctionVariablePowers(exponents).wrap();
		if (geo != null) {
			ret = ret.multiply(geo);
		}
		ret = productCreator.piDegPowers(ret, exponents.get(Unicode.PI_STRING), degPower);

		if (mult != null) {
			ret = ret.multiply(mult);
		}

		return ret;
	}

	private ExpressionValue processInReverse() {
		for (charIndex = nameNoX.length() - 1; charIndex >= 0; charIndex--) {

			Operation op = kernel.getApplication().getParserFunctions()
					.getSingleArgumentOp(nameNoX.substring(0, charIndex));
			op = ArcTrigReplacer.getDegreeInverseTrigOp(op);
			if (op != null) {
				ExpressionValue arg = new VariableReplacerAlgorithm(kernel)
						.replace(expressionString.substring(charIndex));
				if (arg instanceof Variable) {
					return arg;
				}
				if (arg != null) {
					return arg.wrap().apply(op).traverse(
							ArcTrigReplacer.getReplacer());
				}
			}
		}

		return processProductReverse();
	}

	private MySpecialDouble consumeConstant(String expressionString) {
		int numberLength = 0;
		while (numberLength < expressionString.length()
				&& StringUtil.isDigit(expressionString.charAt(numberLength))) {
			numberLength++;
		}
		if (numberLength != 0) {
			String num = nameNoX.substring(0, numberLength);
			double value = MyDouble.parseDouble(kernel.getLocalization(), num);
			nameNoX = nameNoX.substring(numberLength);
			geo = lookupOrProduct(nameNoX);
			return new MySpecialDouble(kernel, value, num);
		}
		return null;
	}

	private void processPi() {
		while (nameNoX.length() > 0 && geo == null && (nameNoX.startsWith("pi")
				|| nameNoX.charAt(0) == Unicode.pi)) {
			int chop = nameNoX.charAt(0) == Unicode.pi ? 1 : 2;
			exponents.increase(Unicode.PI_STRING);
			nameNoX = nameNoX.substring(chop);
			if (charIndex + 1 >= chop) {
				geo = lookupOrProduct(nameNoX);
			}
			if (geo != null) {
				break;
			}
		}
	}

	private ExpressionValue lookupOrProduct(String nameNoX) {
		if (kernel.getConstruction().isRegistredFunctionVariable(nameNoX)
				&& !isCharVariableOrConstantName(nameNoX)) {
			return new FunctionVariable(kernel, nameNoX);
		}
		ExpressionValue ret = kernel.lookupLabel(nameNoX);

		if (ret == null && "i".equals(nameNoX)) {
			ret = kernel.getImaginaryUnit();
		}
		if (ret == null && "e".equals(nameNoX)) {
			ret = kernel.getEulerNumber();
		}
		if (ret == null) {
			ret = productCreator.getProduct(nameNoX);
		}
		return ret;
	}

	private ExpressionValue processProductReverse() {
		for (charIndex = nameNoX.length() - 1; charIndex >= 0; charIndex--) {

			String lastChar = expressionString.substring(charIndex, charIndex + 1);
			if (!isCharVariableOrConstantName(lastChar)) {
				break;
			}

			exponents.increase(lastChar);

			nameNoX = expressionString.substring(0, charIndex);
			geo = lookupOrProduct(nameNoX);

			if (geo != null) {
				break;
			}
		}

		return null;
	}

	private boolean isCharVariableOrConstantName(String token) {
		if (token.length() != 1) {
			return false;
		}
		char charAtIndex = token.charAt(0);
		boolean isPi = charAtIndex == Unicode.pi;
		boolean isTheta = charAtIndex == Unicode.theta;
		boolean isT = charAtIndex == 't';
		boolean isXYZ = charAtIndex >= 'x' && charAtIndex <= 'z';
		return isPi || isTheta || isXYZ || isT;
	}

	private ExpressionNode getLogExpression() {
		if (!expressionString.startsWith("log_")) {
			return null;
		}
		ExpressionValue logIndex = FunctionParser.getLogIndex(expressionString, kernel);
		if (logIndex != null) {
			ExpressionValue logArg = getLogArg(expressionString);
			return new ExpressionNode(kernel, logIndex, Operation.LOGB, logArg);
		}
		return null;
	}

	private ExpressionValue getLogArg(String logString) {
		int indexOfArg = getIndexOfArg(logString);
		if (indexOfArg == -1) {
			return null;
		}

		String arg = logString.substring(indexOfArg);
		if (!arg.isEmpty()) {
			try {
				return parseAndReplace(arg);
			} catch (ParseException ignored) {
				// just return null bellow
			}
		}
		return null;
	}

	private ExpressionValue parseAndReplace(String arg) throws ParseException {
		ExpressionValue parsedArg = kernel.getParser()
				.parseGeoGebraExpression(arg);
		parsedArg.resolveVariables(new EvalInfo(false, false)
				.withSymbolicMode(kernel.getSymbolicMode()));
		return parsedArg;
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

	// For tests only.
	Exponents getExponents() {
		return exponents;
	}
}
