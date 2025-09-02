package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.gui.view.consprotocol.ConstructionProtocolNavigation;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;

public class NavigationBarPlayButtonProperty extends AbstractValuedProperty<Boolean>
	implements BooleanProperty {
	private final App app;
	private final int viewID;

	/**
	 * Creates a property enabling/disabling the play button in the navigation bar
	 * @param localization localization
	 * @param app application
	 * @param viewID euclidian view ID
	 */
	public NavigationBarPlayButtonProperty(Localization localization, App app, int viewID) {
		super(localization, "PlayButton");
		this.app = app;
		this.viewID = viewID;
	}

	@Override
	protected void doSetValue(Boolean value) {
		ConstructionProtocolNavigation cpn = app.getGuiManager()
				.getConstructionProtocolNavigation(viewID);
		cpn.setPlayButtonVisible(!cpn.isPlayButtonVisible());
		app.setUnsaved();
	}

	@Override
	public Boolean getValue() {
		ConstructionProtocolNavigation cpn = app.getGuiManager()
				.getConstructionProtocolNavigation(viewID);
		return cpn == null || cpn.isPlayButtonVisible();
	}

	@Override
	public boolean isEnabled() {
		return app.showConsProtNavigation(viewID);
	}
}
