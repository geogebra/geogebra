package org.geogebra.web.full.gui.util;

import org.geogebra.common.main.Localization;
import org.geogebra.common.main.MaterialVisibility;
import org.geogebra.common.properties.impl.AbstractEnumerableProperty;
import org.geogebra.common.util.debug.Log;

public class MaterialVisibilityProperty extends AbstractEnumerableProperty {
	private int index;

	/**
	 * Constructs an AbstractEnumerableProperty
	 * @param localization the localization used
	 */
	public MaterialVisibilityProperty(Localization localization) {
		super(localization, "");
		update(MaterialVisibility.Private);
	}

	@Override
	public int getIndex() {
		return index;
	}

	@Override
	protected void setValueSafe(String value, int index) {
		this.index = index;
	}

	/**
	 * Update property due to visibility
	 * @param visibility to update on.
	 */
	public void update(MaterialVisibility visibility) {
		Log.debug("Property is updated to " + visibility);
		if (visibility == MaterialVisibility.Public) {
			setValues("Private", "Shared", "Public");
		} else {
			setValues("Private", "Shared");
		}
		setValueSafe("", visibility.getIndex());
	}
}
