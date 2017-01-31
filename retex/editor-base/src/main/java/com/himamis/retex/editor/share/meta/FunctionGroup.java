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

	private Map<String, MetaComponent> components = new HashMap<String, MetaComponent>();
    private MetaParameter[] defaultParameters = new MetaParameter[]{new MetaParameter("x", 0)};

    @Override
    public String getName() {
        return MetaModel.FUNCTIONS;
    }

    @Override
    public String getGroup() {
        return getName();
    }

    @Override
    public MetaComponent getComponent(String componentName) {
        if (acceptedFunction(componentName)) {
            return getMathComponent(componentName);
        }
        return null;
    }

	private static boolean acceptedFunction(String functionName) {
        // Accept only functions that consist of no special characters
		return !"".equals(functionName) && Character.areLetters(functionName);
    }

    private MetaComponent getMathComponent(String componentName) {
        MetaComponent component = null;
        if ((component = components.get(componentName)) == null) {
            component = createComponent(componentName);
            components.put(componentName, component);
        }
        return component;
    }

    private MetaComponent createComponent(String componentName) {
        return new MetaFunction(componentName, componentName, componentName, '\0', defaultParameters);
    }
}
