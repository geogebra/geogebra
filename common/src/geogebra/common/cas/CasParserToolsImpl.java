package geogebra.common.cas;

import com.google.gwt.regexp.shared.RegExp;

class CasParserToolsImpl implements CasParserTools {
	
	private static final char DEFAULT_EXP_CHAR = 'E';
	
	private final RegExp pattern;
	
	public CasParserToolsImpl(char foreignExpChar) {
		pattern = RegExp.compile("([0-9])" + foreignExpChar,"g");
	}
	
	// convert MathPiper's scientific notation from e.g. 3.24e-4 to 3.2E-4
	public String convertScientificFloatNotation(String input) {
		return pattern.replace(input,"$1"+DEFAULT_EXP_CHAR);
	}
	
}
