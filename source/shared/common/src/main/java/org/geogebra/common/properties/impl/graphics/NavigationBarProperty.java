package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;

public class NavigationBarProperty extends AbstractValuedProperty<Boolean>
	implements BooleanProperty {
	private final App app;
	private final int viewID;

	/**
	 * Creates a property enabling/disabling the navigation bar
	 * @param localization localization
	 * @param app application
	 * @param viewID view ID
	 */
	public NavigationBarProperty(Localization localization, App app, int viewID) {
		super(localization, "ConstructionProtocolNavigation");
		this.app = app;
		this.viewID = viewID;
	}

	@Override
	protected void doSetValue(Boolean value) {
		app.toggleShowConstructionProtocolNavigation(viewID);
	}

	@Override
	public Boolean getValue() {
		return app.showConsProtNavigation(viewID);
	}
}
