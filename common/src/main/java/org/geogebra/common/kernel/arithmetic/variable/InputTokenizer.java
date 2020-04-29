package org.geogebra.common.kernel.arithmetic.variable;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.util.StringUtil;

public class InputTokenizer {
	private String input;

	public InputTokenizer(String input) {
		this.input = input;
	}

	public List<String> getTokens() {
		ArrayList<String> tokens = new ArrayList<>();
		while (!"".equals(input)) {
			tokens.add(next());
		}

		return tokens;
	}

	public String next() {
		String token = getToken();
		input = input.substring(token.length());
		return token;
	}

	private String getToken() {
		if (noInputLeft()) {
			return "";
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

	private boolean isQuoteMarkNext() {
		return input.charAt(1) == '\'';
	}

	private boolean isSingleCharOrLetterNext() {
		return input.length() == 1 || StringUtil.isLetter(input.charAt(1));
	}

	private String getTokenWithIndex() {
		int idxClose = input.indexOf("}");
		return idxClose != -1 ? input.substring(0, idxClose + 1) : "";
	}

	private boolean isIndexNext() {
		return "_{".equals(input.substring(1, 3));
	}

	private boolean noInputLeft() {
		return input == null || input.length() == 0;
	}
}
