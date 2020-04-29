package org.geogebra.common.kernel.arithmetic.variable;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.util.StringUtil;

public class InputTokenizer {
	private String input;

	public InputTokenizer(String input) {
		this.input = input;
	}

	public String next() {
		String token = getToken();
		input = input.substring(token.length());
		return token;
	}

	public String getToken() {
		if (input == null || input.length() == 0) {
			return "";
		}

		char ch = input.charAt(0);
		if (input.length() == 1) {
			return String.valueOf(ch);
		}

		char next = input.charAt(1);
		if (StringUtil.isLetter(next)) {
			return String.valueOf(ch);
		}

		if (next == '\'') {
			return ch + "'";
		}
		if ("_{".equals(input.substring(1, 3))) {
			int idxClose = input.indexOf("}");
			if (idxClose != -1) {
				return input.substring(0, idxClose + 1);
			}
		}
		return "";
	}

	public List<String> getTokens() {
		ArrayList<String> tokens = new ArrayList<>();
		while (!"".equals(input)) {
			tokens.add(next());
		}

		return tokens;
	}
}
