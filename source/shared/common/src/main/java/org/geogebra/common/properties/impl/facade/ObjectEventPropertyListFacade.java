/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
