package org.geogebra.common.kernel.arithmetic.variable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.util.StringUtil;

public class InputTokenizer {
	private final List<String> varStrings;
	private Kernel kernel;
	private String input;

	public InputTokenizer(String input) {
		this.input = input;
		varStrings = Collections.emptyList();
	}

	public InputTokenizer(Kernel kernel, String input) {
		this.kernel = kernel;
		this.input = input;
		varStrings = getVarStrings();
	}

	private List<String> getVarStrings() {
		if (kernel == null || kernel.getConstruction() == null ||
				kernel.getConstruction().getGeoSetConstructionOrder() == null) {
			return Collections.emptyList();
		}

		ArrayList<String> list = new ArrayList<>();
		for (GeoElement geo: kernel.getConstruction().getGeoSetConstructionOrder()) {
			for (FunctionVariable variable: getFunctionVariables(geo)) {
				list.add(variable.getSetVarString());
			}
		}
		return list;
	}

	private FunctionVariable[] getFunctionVariables(GeoElement geo) {
		if (geo.isGeoFunction()) {
			return ((GeoFunction) geo).getFunctionVariables();
		}
		if (geo.isGeoFunctionNVar()) {
			return ((GeoFunctionNVar)geo).getFunctionVariables();
		}
		return new FunctionVariable[0];
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
		for (String var: varStrings) {
			if (input.startsWith(var)) {
				return var;
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
