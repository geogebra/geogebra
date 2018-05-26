package com.himamis.retex.editor.share.meta;

import com.himamis.retex.editor.share.input.Character;

/**
 * Group of custom functions not described in the .xml file.
 *
 * @author Balazs Bencze
 */
public class FunctionGroup implements MetaGroup {

	private static MetaParameter[] defaultParameters = new MetaParameter[] {
			new MetaParameter("f", 0), new MetaParameter("x", 1) };
	private MetaFunction apply = new MetaFunction(Tag.APPLY, null, (char) 0,
			defaultParameters);
	private MetaFunction applySquare = new MetaFunction(Tag.APPLY_SQUARE, null,
			(char) 0, defaultParameters);

	@Override
	public MetaComponent getComponent(Tag tag) {
		return getComponent(tag.toString(), false);
	}

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
		return !"".equals(functionName)
				&& Character.areLettersOrDigits(functionName)
				&& containsLetter(functionName);
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
