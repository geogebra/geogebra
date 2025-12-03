package org.geogebra.common.properties.impl.facade;

import java.util.List;

import org.geogebra.common.plugin.ScriptType;
import org.geogebra.common.properties.impl.objects.ObjectEventProperty;

/**
 * Handles a collection of ScriptTypeProperty objects as a single ScriptTypeProperty.
 */
public class ObjectEventPropertyListFacade<T extends ObjectEventProperty>
		extends AbstractPropertyListFacade<T> implements ObjectEventProperty {

	/**
	 * @param properties per-element properties
	 */
	public ObjectEventPropertyListFacade(List<T> properties) {
		super(properties);
	}

	@Override
	public ScriptType getScriptType() {
		return getFirstProperty().getScriptType();
	}

	@Override
	public String getScriptText() {
		return getFirstProperty().getScriptText();
	}

	@Override
	public void setScriptText(String text) {
		getFirstProperty().setScriptText(text);
	}

	@Override
	public void setScriptType(ScriptType value) {
		getFirstProperty().setScriptType(value);
	}

	@Override
	public void setJsEnabled(boolean jsEnabled) {
		getFirstProperty().setJsEnabled(jsEnabled);
	}

	@Override
	public boolean isJsEnabled() {
		return getFirstProperty().isJsEnabled();
	}
}
