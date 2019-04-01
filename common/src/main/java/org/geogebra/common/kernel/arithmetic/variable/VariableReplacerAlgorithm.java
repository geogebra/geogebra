package org.geogebra.common.kernel.arithmetic.variable;

import com.himamis.retex.editor.share.util.Unicode;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.parser.FunctionParser;
import org.geogebra.common.plugin.Operation;

public class VariableReplacerAlgorithm {

	private Kernel kernel;
	private DerivativeCreator derivativeCreator;
	private ProductCreator productCreator;

	private String expressionString;
	private String nameNoX;
	private int[] exponents;
	private ExpressionValue geo;
	private int degPower;
	private int charIndex;

	public VariableReplacerAlgorithm(Kernel kernel) {
		this.kernel = kernel;
		derivativeCreator = new DerivativeCreator(kernel);
		productCreator = new ProductCreator(kernel);
	}

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
		exponents = new int[] { 0, 0, 0, 0, 0 };
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

		ExpressionValue resultOfReverseProcessing = processInReverse();
		if (resultOfReverseProcessing != null) {
			return resultOfReverseProcessing;
		}

		while (nameNoX.length() > 0 && geo == null && (nameNoX.startsWith("pi")
				|| nameNoX.charAt(0) == Unicode.pi)) {
			int chop = nameNoX.charAt(0) == Unicode.pi ? 1 : 2;
			exponents[4]++;
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
		if (nameNoX.length() > 0 && geo == null) {
			return new Variable(kernel, nameNoX);
		}
		ExpressionNode powers = productCreator.getXyzPowers(exponents);
		if (geo == null) {
			return exponents[4] == 0 && degPower == 0 ? powers
					: powers.multiply(productCreator.piDegTo(exponents[4], degPower));
		}
		return exponents[4] == 0 && degPower == 0 ? powers.multiply(geo)
				: powers.multiply(geo)
				.multiply(productCreator.piDegTo(exponents[4], degPower));
	}

	private ExpressionValue processInReverse() {
		for (charIndex = nameNoX.length() - 1; charIndex >= 0; charIndex--) {
			char c = expressionString.charAt(charIndex);
			if ((c < 'x' || c > 'z') && c != Unicode.theta && c != Unicode.pi) {
				break;
			}
			exponents[c == Unicode.pi ? 4
					: (c == Unicode.theta ? 3 : c - 'x')]++;
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
			} else if (nameNoX.startsWith("log_")) {
				ExpressionValue logExpression = getLogExpression();
				if (logExpression != null) {
					return logExpression;
				}
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

	private ExpressionNode getLogExpression() {
		ExpressionValue index = FunctionParser.getLogIndex(nameNoX, kernel);
		if (index != null) {
			ExpressionValue arg = productCreator.getXyzPiDegPower(exponents, degPower);
			return new ExpressionNode(kernel, index, Operation.LOGB, arg);
		}
		return null;
	}

}
