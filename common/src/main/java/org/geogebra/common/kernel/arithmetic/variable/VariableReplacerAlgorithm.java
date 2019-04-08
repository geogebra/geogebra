package org.geogebra.common.kernel.arithmetic.variable;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.variable.power.Base;
import org.geogebra.common.kernel.arithmetic.variable.power.Exponents;
import org.geogebra.common.kernel.parser.FunctionParser;
import org.geogebra.common.kernel.parser.ParseException;
import org.geogebra.common.plugin.Operation;

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
	private int degPower;
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

		geo = productCreator.getProduct(expressionString);
		if (geo != null) {
			return geo;
		}
		nameNoX = expressionString;
		degPower = 0;
		while (nameNoX.length() > 0 && (geo == null)
				&& nameNoX.endsWith("deg")) {
			int length = nameNoX.length();
			degPower++;
			nameNoX = nameNoX.substring(0, length - 3);
			if (length > 3) {
				geo = kernel.lookupLabel(nameNoX);
			}

		}

		ExpressionValue logExpression = getLogExpression(expressionString);
		if (logExpression != null) {
			return logExpression;
		}

		ExpressionValue resultOfReverseProcessing = processInReverse();
		if (resultOfReverseProcessing != null) {
			return resultOfReverseProcessing;
		}

		processPi();

		if (nameNoX.length() > 0 && geo == null) {
			return new Variable(kernel, nameNoX);
		}
		ExpressionNode powers = productCreator.getXyzPowers(exponents);
		if (geo == null) {
			return exponents.get(Base.pi) == 0 && degPower == 0 ? powers
					: powers.multiply(productCreator.piDegTo(exponents.get(Base.pi), degPower));
		}
		return exponents.get(Base.pi) == 0 && degPower == 0 ? powers.multiply(geo)
				: powers.multiply(geo)
				.multiply(productCreator.piDegTo(exponents.get(Base.pi), degPower));
	}

	private ExpressionValue processInReverse() {
		for (charIndex = nameNoX.length() - 1; charIndex >= 0; charIndex--) {

			if (!isCharVariableOrConstantName()) {
				break;
			}

			increaseExponents();

			nameNoX = expressionString.substring(0, charIndex);
			geo = kernel.lookupLabel(nameNoX);
			if (geo == null && "i".equals(nameNoX)) {
				geo = kernel.getImaginaryUnit();
			}
			Operation op = kernel.getApplication().getParserFunctions()
					.get(nameNoX, 1);
			if (op != null && op != Operation.XCOORD && op != Operation.YCOORD
					&& op != Operation.ZCOORD) {
				return productCreator.getXyzPiDegPower(exponents, degPower).apply(op);
			}

			if (geo == null) {
				geo = productCreator.getProduct(nameNoX);
			}
			if (geo != null) {
				break;
			}
		}

		return null;
	}

	private void processPi() {
		while (nameNoX.length() > 0 && geo == null && (nameNoX.startsWith("pi")
				|| nameNoX.charAt(0) == Unicode.pi)) {
			int chop = nameNoX.charAt(0) == Unicode.pi ? 1 : 2;
			exponents.increase(Base.pi);
			nameNoX = nameNoX.substring(chop);
			if (charIndex + 1 >= chop) {
				geo = kernel.lookupLabel(nameNoX);
				if (geo == null) {
					geo = productCreator.getProduct(nameNoX);
				}
			}
			if (geo != null) {
				break;
			}
		}
	}

	private void increaseExponents() {
		char charAtIndex = expressionString.charAt(charIndex);

		if (charAtIndex == Unicode.pi) {
			exponents.increase(Base.pi);
		}
		else if (charAtIndex == Unicode.theta) {
			exponents.increase(Base.theta);
		}
		else if (charAtIndex == 'x') {
			exponents.increase(Base.x);
		}
		else if (charAtIndex == 'y') {
			exponents.increase(Base.y);
		}
		else if (charAtIndex == 'z') {
			exponents.increase(Base.z);
		}
	}

	private boolean isCharVariableOrConstantName() {
		char charAtIndex = expressionString.charAt(charIndex);
		boolean isPi = charAtIndex == Unicode.pi;
		boolean isTheta = charAtIndex == Unicode.theta;
		boolean isXYZ = charAtIndex >= 'x' && charAtIndex <= 'z';
		return isPi || isTheta || isXYZ;
	}

	private ExpressionNode getLogExpression(String expressionString) {
		if (!expressionString.startsWith("log_")) {
			return null;
		}
		ExpressionValue index = FunctionParser.getLogIndex(expressionString, kernel);
		if (index != null) {
			ExpressionValue logArg = getLogArg(expressionString);
			if (logArg == null) {
				logArg = productCreator.getXyzPiDegPower(exponents, degPower);
			}
			return new ExpressionNode(kernel, index, Operation.LOGB, logArg);
		}
		return null;
	}

	private ExpressionValue getLogArg(String logString) {
		int indexOfClosingBracket = logString.indexOf('}');
		String argString = logString.substring(indexOfClosingBracket + 1);
		try {
			return kernel.getParser().parseGeoGebraExpression(argString);
		} catch (ParseException ignored) {
		}
		return null;
	}

	// For tests only.
	Exponents getExponents() {
		return exponents;
	}
}
