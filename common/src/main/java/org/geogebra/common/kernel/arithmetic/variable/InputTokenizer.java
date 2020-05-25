package org.geogebra.common.kernel.arithmetic.variable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.util.StringUtil;

import com.himamis.retex.editor.share.util.Unicode;

/**
 * Tokenizer for algebra input string
 *
 * @author Laszlo
 */
public class InputTokenizer {
	public static final String IMAGINARY_STRING = Unicode.IMAGINARY + "";
	private final List<String> varStrings;
	private final Kernel kernel;
	private String input;

	/**
	 *
	 * @param kernel the kernel.
	 * @param input to split to tokens
	 */
	public InputTokenizer(Kernel kernel, String input) {
		this.kernel = kernel;
		this.input = input;
		varStrings = getVarStrings();
	}

	private List<String> getVarStrings() {
		if (kernel == null || kernel.getConstruction() == null) {
			return Collections.emptyList();
		}

		return Arrays.asList(kernel.getConstruction().getRegisteredFunctionVariables());
	}

	/**
	 *
	 * @return all the tokens input was split to.
	 */
	public List<String> getTokens() {
		ArrayList<String> tokens = new ArrayList<>();
		while (!StringUtil.empty(input)) {
			String nextToken = next();
			tokens.add(nextToken);
		}

		return tokens;
	}

	/**
	 *
	 * @return the next token processed from input
	 */
	public String next() {
		String token = getToken();
		input = !StringUtil.empty(token) ? input.substring(token.length()) : null;
		return token;
	}

	private String getToken() {
		if (noInputLeft()) {
			return null;
		}

		if (isImaginaryNext()) {
			return IMAGINARY_STRING;
		}

		if (isDigitNext()) {
			if (StringUtil.isLetter(input.charAt(0))) {
				return String.valueOf(input.charAt(0));
			}
			return getNumberToken(0);
		}

		String geoLabel = getGeoLabel();
		if (!StringUtil.empty(geoLabel)) {
			return geoLabel;
		}

		String variable = getVariable();
		if (!"".equals(variable)) {
			return variable;
		}

		if (isPiNext()) {
			return "pi";
		}

		if (isSingleCharOrLetterNext()) {
			return String.valueOf(input.charAt(0));
		}

		if (isQuoteMarkNext()) {
			return input.charAt(0) + "'";
		}

		if (isIndexNext()) {
			return getTokenWithIndex();
		}

		return "";
	}

	private boolean isImaginaryNext() {
		return input.charAt(0) == 'i';
	}

	private String getGeoLabel() {
		if (input.length() == 1) {
			return input;
		}

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < input.length(); i++) {
			String label = sb.toString();
			GeoElement geo = kernel.lookupLabel(label);
			if (geo != null) {
				return label;
			}
			sb.append(input.charAt(i));
		}
		return null;
	}

	private boolean isPiNext() {
		if (input.length() < 2) {
			return false;
		}

		return "pi".equals(input.substring(0, 2).toLowerCase());
	}

	private String getVariable() {
		for (String var : varStrings) {
			if (input.startsWith(var)) {
				return var;
			}
		}
		return "";
	}

	private String getNumberToken(int from) {
		if (noInputLeft()) {
			return "";
		}

		StringBuilder result = new StringBuilder();
		int i = from;
		while (StringUtil.isDigit(input.charAt(i))) {
			result.append(input.charAt(i));
			i++;
		}
		return result.toString();

	}

	private boolean isDigitNext() {
		return input.length() > 1 && StringUtil.isDigit(nextChar());
	}

	private char nextChar() {
		return input.charAt(1);
	}

	private boolean isQuoteMarkNext() {
		return nextChar() == '\'';
	}

	private boolean isSingleCharOrLetterNext() {
		return input.length() == 1 || StringUtil.isLetter(nextChar())
				|| isBracket(nextChar());
	}

	private boolean isBracket(char ch) {
		return ch == '(' || ch == ')';
	}

	private String getTokenWithIndex() {
		if ("_{".equals(input.substring(1, 3))) {
			return tokenWithCurlyBracketIndex();
		}
		return tokenWithUnderscoreIndex();
	}

	private String tokenWithUnderscoreIndex() {
		String token = input.substring(0, 2);
		return token + getNumberToken(2);
	}

	private String tokenWithCurlyBracketIndex() {
		int idxClose = input.indexOf("}");
		return idxClose != -1 ? input.substring(0, idxClose + 1) : "";
	}

	private boolean isIndexNext() {
		return nextChar() == '_';
	}

	public boolean noInputLeft() {
		return input == null || input.length() == 0;
	}

	/**
	 * @return the unprocessed input.
	 */
	public String getInputRemaining() {
		return input;
	}

	/**
	 * @return if there are tokens to process.
	 */
	public boolean hasToken() {
		return !(input == null || "".equals(input));

	}

	public static boolean isImaginaryUnit(String input) {
		return IMAGINARY_STRING.equals(input);
	}
}
