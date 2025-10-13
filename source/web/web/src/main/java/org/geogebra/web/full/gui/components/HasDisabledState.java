package org.geogebra.web.full.gui.components;

public interface HasDisabledState {

	/**
	 * Disable or enable this component.
	 * @param disabled whether this component should be not editable
	 */
	void setDisabled(boolean disabled);
}
