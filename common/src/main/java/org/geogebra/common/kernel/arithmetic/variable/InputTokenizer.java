package org.geogebra.common.kernel.arithmetic.variable;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.util.StringUtil;

public class InputTokenizer {
	private Kernel kernel;
	private String input;

	public InputTokenizer(Kernel kernel, String input) {
		this.kernel = kernel;
		this.input = input;
	}

	public List<String> getTokens() {
		ArrayList<String> tokens = new ArrayList<>();
		while (!"".equals(input)) {
			String nextToken = next();
			if (!isBracket(nextToken)) {
				tokens.add(nextToken);
			}
		}

		return tokens;
	}

	private boolean isBracket(String nextToken) {
		return nextToken.length() == 1 && isBracket(nextToken.charAt(0));
	}

	public String next() {
		String token = getToken();
		input = token != null ? input.substring(token.length()) : null;
		return token;
	}

	private String getToken() {
		if (noInputLeft()) {
			return null;
		}
		String variable = getVariable();
		if (!"".equals(variable)) {
			return variable;
		}

		if (isDigitNext()) {
			return getNumberToken();
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

		if (isExponentialNext()) {
			return getTokenWithExponential();
		}

		return "";
	}

	private boolean isPiNext() {
		if (input.length() < 2) {
			return false;
		}

		return "pi".equals(input.substring(0, 2).toLowerCase());
	}

	private String getVariable() {
		if (kernel == null) {
			return "";
		}
		String var="";
		for (int i = 0; i < input.length(); i++) {
			if (StringUtil.isLetterOrDigitOrUnderscore(input.charAt(i))) {
				var += input.charAt(i);
				if (kernel.lookupLabel(var) != null) {
					return var;
				}
			}
		}

		return "";
	}
	private String getNumberToken() {
		if (noInputLeft()) {
			return "";
		}

		StringBuilder result = new StringBuilder();
		int i = 0;
		while (StringUtil.isDigit(input.charAt(i)) && i < input.length()) {
			result.append(input.charAt(i));
			i++;
		}
		return result.toString();

	}

	private boolean isDigitNext() {
		return input.length() > 1 && StringUtil.isDigit(input.charAt(1));
	}

	private boolean isQuoteMarkNext() {
		return input.charAt(1) == '\'';
	}

	private boolean isSingleCharOrLetterNext() {
		return input.length() == 1 || StringUtil.isLetter(input.charAt(1))
				|| isBracket(input.charAt(1));
	}

	private boolean isBracket(char ch) {
		return ch == '(' || ch == ')';
	}

	private String getTokenWithIndex() {
		int idxClose = input.indexOf("}");
		return idxClose != -1 ? input.substring(0, idxClose + 1) : "";
	}

	private boolean isIndexNext() {
		return "_{".equals(input.substring(1, 3));
	}

	private boolean isExponentialNext() {
		return "^(".equals(input.substring(1, 3));
	}

	private String getTokenWithExponential() {
		int idxClose = input.indexOf(")");
		return idxClose != -1 ? input.substring(0, idxClose + 1) : "";
	}

	public boolean noInputLeft() {
		return input == null || input.length() == 0;
	}

	public String getInputRemaining() {
		return input;
	}

	public boolean hasToken() {
		return !(input == null || "".equals(input));

	}
}
