package com.himamis.retex.editor.share.meta;

import java.util.HashMap;
import java.util.Map;

import com.himamis.retex.editor.share.input.Character;

/**
 * Group of custom functions not described in the .xml file.
 *
 * @author Balazs Bencze
 */
public class FunctionGroup implements MetaGroup {

	private Map<String, MetaFunction> components = new HashMap<String, MetaFunction>();
	private static MetaParameter[] defaultParameters = new MetaParameter[] {
			new MetaParameter("x", 0) };

	public MetaFunction getComponent(String componentName) {
        if (acceptedFunction(componentName)) {
            return getMathComponent(componentName);
        }
        return null;
    }

	private static boolean acceptedFunction(String functionName) {
        // Accept only functions that consist of no special characters
		return !"".equals(functionName) && Character.areLetters(functionName);
    }

	private MetaFunction getMathComponent(String componentName) {
		MetaFunction component = null;
        if ((component = components.get(componentName)) == null) {
            component = createComponent(componentName);
            components.put(componentName, component);
        }
        return component;
    }

	private MetaFunction createComponent(String componentName) {
		return new MetaFunction(componentName, componentName, '\0',
				defaultParameters);
    }
}
