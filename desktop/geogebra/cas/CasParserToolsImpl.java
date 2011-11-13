package geogebra.cas;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class CasParserToolsImpl implements CasParserTools {
	
	private static final char DEFAULT_EXP_CHAR = 'E';
	
	private final Pattern pattern;
	
	public CasParserToolsImpl(char foreignExpChar) {
		pattern = Pattern.compile("[0-9]" + foreignExpChar);
	}
	
	// convert MathPiper's scientific notation from e.g. 3.24e-4 to 3.2E-4
	public String convertScientificFloatNotation(String input) {
		Matcher matcher = pattern.matcher(input);
		char[] chars = input.toCharArray();
		
		while(matcher.find()) {
			int expCharIndex = matcher.end() - 1;
			chars[expCharIndex] = DEFAULT_EXP_CHAR;
		}
		
		return new String(chars);
	}
	
}
