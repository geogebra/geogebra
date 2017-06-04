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

	public MetaFunction getComponent(String componentName) {
		if (!isAcceptable(componentName)) {
			return null;
		}
		return apply;
	}

	public static boolean isAcceptable(String functionName) {
        // Accept only functions that consist of no special characters
		return !"".equals(functionName) && Character.areLetters(functionName);
    }

	
}
