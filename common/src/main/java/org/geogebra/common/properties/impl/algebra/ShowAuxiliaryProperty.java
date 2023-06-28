package org.geogebra.common.properties.impl.algebra;

import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;

/**
 * "Show auxiliary objects" property.
 */
public class ShowAuxiliaryProperty extends AbstractValuedProperty<Boolean>
		implements BooleanProperty {

	private App app;

	/**
	 * Constructs a property for showing or hiding auxiliary objects.
	 * @param app app
	 * @param localization localization
	 */
	public ShowAuxiliaryProperty(App app, Localization localization) {
		super(localization, "AuxiliaryObjects");
		this.app = app;
	}

	@Override
	public Boolean getValue() {
		return app.showAuxiliaryObjects();
	}

	@Override
	public void doSetValue(Boolean value) {
		app.setShowAuxiliaryObjects(value);
	}
}
