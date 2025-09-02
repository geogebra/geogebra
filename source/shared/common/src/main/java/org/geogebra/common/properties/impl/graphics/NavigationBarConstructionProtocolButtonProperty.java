package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.gui.view.consprotocol.ConstructionProtocolNavigation;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;

public class NavigationBarConstructionProtocolButtonProperty extends AbstractValuedProperty<Boolean>
	implements BooleanProperty {
	private final App app;
	private final int viewID;

	/**
	 * Creates a property enabling/disabling the construction protocol button in the navigation bar
	 * @param localization localization
	 * @param app application
	 * @param viewID euclidian view ID
	 */
	public NavigationBarConstructionProtocolButtonProperty(Localization localization, App app,
			int viewID) {
		super(localization, "ConstructionProtocolButton");
		this.app = app;
		this.viewID = viewID;
	}

	@Override
	protected void doSetValue(Boolean value) {
		ConstructionProtocolNavigation consNav = app.getGuiManager()
				.getConstructionProtocolNavigation(viewID);
		consNav.setConsProtButtonVisible(!consNav.isConsProtButtonVisible());
		app.setUnsaved();
	}

	@Override
	public Boolean getValue() {
		ConstructionProtocolNavigation cpn = app.getGuiManager()
				.getConstructionProtocolNavigation(viewID);
		return cpn == null || cpn.isConsProtButtonVisible();
	}

	@Override
	public boolean isEnabled() {
		return app.showConsProtNavigation(viewID);
	}
}
