package org.geogebra.common.properties.impl.collections;

import java.util.List;

import org.geogebra.common.main.Localization;
import org.geogebra.common.plugin.ScriptType;
import org.geogebra.common.properties.impl.AbstractProperty;
import org.geogebra.common.properties.impl.objects.ObjectEventProperty;

/**
 * Handles a collection of ScriptTypeProperty objects as a single ScriptTypeProperty.
 */
public class ObjectEventPropertyCollection extends AbstractProperty implements ObjectEventProperty {

	private final List<? extends ObjectEventProperty> properties;

	/**
	 * @param loc localization
	 * @param properties per-element properties
	 */
	public ObjectEventPropertyCollection(Localization loc,
			List<? extends  ObjectEventProperty> properties) {
		super(loc, properties.get(0).getRawName());
		this.properties = properties;
	}

	private ObjectEventProperty getFirstProperty() {
		return properties.get(0);
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

	@Override
	public boolean isEnabled() {
		return getFirstProperty().isEnabled();
	}

}
