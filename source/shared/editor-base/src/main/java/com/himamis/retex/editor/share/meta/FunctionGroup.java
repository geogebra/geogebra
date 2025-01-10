package com.himamis.retex.editor.share.meta;

import com.himamis.retex.editor.share.input.Character;
import com.himamis.retex.editor.share.util.Unicode;

/**
 * Group of custom functions not described in the .xml file.
 *
 * @author Balazs Bencze
 */
public class FunctionGroup implements MetaGroup {

	private static MetaParameter[] defaultParameters = new MetaParameter[] {
			MetaParameter.BASIC, MetaParameter.BASIC };
	private MetaFunction apply = new MetaFunction(Tag.APPLY, null,
			defaultParameters);
	private MetaFunction applySquare = new MetaFunction(Tag.APPLY_SQUARE, null,
			defaultParameters);

	@Override
	public MetaComponent getComponent(Tag tag) {
		return getComponent(tag.toString(), false);
	}

	/**
	 * @param name
	 *            function name
	 * @param square
	 *            use [ rather than (
	 * @return meta function
	 */
	MetaFunction getComponent(String name, boolean square) {
		if (!isAcceptable(name)) {
			return null;
		}
		return square ? applySquare : apply;
	}

	/**
	 * @param functionName
	 *            potential function name
	 * @return whether the name could be user defined function (has just letters
	 *         + digits, contains letter)
	 */
	public static boolean isAcceptable(String functionName) {
        // Accept only functions that consist of no special characters
		String stem = functionName;
		while (stem.length() > 0
				&& primeOrPower(stem.charAt(stem.length() - 1))) {
			stem = stem.substring(0, stem.length() - 1);
		}
		return !"".equals(stem)
				&& Character.areLettersOrDigits(stem)
				&& containsLetter(stem);
    }

	private static boolean primeOrPower(char c) {
		return c == '\'' || c == Unicode.SUPERSCRIPT_MINUS
				|| Unicode.isSuperscriptDigit(c);
	}

	private static boolean containsLetter(String functionName) {
		for (int i = 0; i < functionName.length(); i++) {
			if (Character.isLetter(functionName.charAt(i))) {
				return true;
			}
		}
		return false;
	}
	
}
