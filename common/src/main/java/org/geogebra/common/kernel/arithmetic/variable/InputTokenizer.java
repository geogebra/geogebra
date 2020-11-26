package org.geogebra.common.kernel.arithmetic.variable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.parser.function.ParserFunctions;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.StringUtil;

import com.himamis.retex.editor.share.util.Unicode;

/**
 * Tokenizer for algebra input string
 *
 * @author Laszlo
 */
public class InputTokenizer {

	public static final String IMAGINARY_STRING = Unicode.IMAGINARY + "";

	private final Kernel kernel;
	private final ParserFunctions parserFunctions;

	private final List<String> varStrings;
	private String input;

	/**
	 *
	 * @param kernel the kernel.
	 * @param input to split to tokens
	 */
	public InputTokenizer(Kernel kernel, ParserFunctions parserFunctions, String input) {
		this.kernel = kernel;
		this.parserFunctions = parserFunctions;
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
	 * @return the next token processed from input
	 */
	public String next() {
		String token = getToken();
		input = !StringUtil.empty(token) ? input.substring(token.length()) : "";
		return token;
	}

	private String getToken() {
		String opPrefix = getOperationPrefix();
		if (opPrefix != null) {
			return opPrefix;
		}

		if (isDigitAt(0)) {
			return getNumberToken();
		}

		if (input.length() == 1) {
			return input.substring(0, 1);
		}

		int minLength = getTokenWithIndexLength(1);
		if (input.startsWith("deg")) {
			return "deg";
		}

		String geoLabel = getGeoLabelOrVariable(minLength);
		if (!StringUtil.empty(geoLabel)) {
			return geoLabel;
		}

		if (isPiNext()) {
			return "pi";
		}

		if (isImaginaryNext()) {
			return IMAGINARY_STRING;
		}

		if (minLength <= input.length()) {
			return input.substring(0, minLength);
		}

		return input;
	}

	private String getOperationPrefix() {
		if (input.startsWith("log_")) {
			int end = input.startsWith("log_{") ? input.indexOf('}') : "log_1".length();
			return input.substring(0, end);
		}
		for (int prefixLength = input.length(); prefixLength > 0; prefixLength--) {
			String prefix = input.substring(0, prefixLength);
			Operation op = parserFunctions.getSingleArgumentOp(prefix);
			if (op != null) {
				return prefix;
			}
		}
		return null;
	}

	private boolean isImaginaryNext() {
		return input.charAt(0) == 'i';
	}

	private String getGeoLabelOrVariable(int minLength) {
		for (int i = minLength; i <= input.length();
				i = getTokenWithIndexLength(i + 1)) {
			String label = input.substring(0, i);
			if (varStrings.contains(label)) {
				return label;
			}
			GeoElement geo = kernel.lookupLabel(label);
			if (geo != null) {
				return label;
			}
		}
		return null;
	}

	private boolean isPiNext() {
		if (input.length() < 2) {
			return false;
		}

		return "pi".equals(input.substring(0, 2).toLowerCase());
	}

	private String getNumberToken() {
		if (!hasToken()) {
			return "";
		}

		StringBuilder result = new StringBuilder();

		for (int i = 0; isDigitAt(i); i++) {
			result.append(input.charAt(i));
		}
		return result.toString();
	}

	private boolean isDigitAt(int i) {
		return input.length() > i && StringUtil.isDigitOrDot(input.charAt(i));
	}

	private int getTokenWithIndexLength(int offset) {
		if (offset >= input.length()) {
			return offset;
		}
		if (input.charAt(offset) == '_') {
			if (input.charAt(offset + 1) == '{') {
				return getTokenWithIndexLength(input.indexOf('}', offset) + 1);
			}
			return getTokenWithIndexLength(offset + 2);
		} else if (input.charAt(offset) == '\'') {
			return getTokenWithIndexLength(offset + 1);
		}
		return offset;
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
		return !StringUtil.empty(input);

	}

	public static boolean isImaginaryUnit(String input) {
		return input.length() == 1 && input.charAt(0) == Unicode.IMAGINARY;
	}
}
