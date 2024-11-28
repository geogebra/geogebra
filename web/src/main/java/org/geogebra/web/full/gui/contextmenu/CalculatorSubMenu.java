package org.geogebra.web.full.gui.contextmenu;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.euclidian.EmbedManager;
import org.geogebra.common.main.App;
import org.geogebra.web.full.gui.ContextMenuItemFactory;
import org.geogebra.web.full.gui.dialog.AppDescription;
import org.geogebra.web.html5.gui.menu.AriaMenuBar;

public class CalculatorSubMenu extends AriaMenuBar {
	private final App app;
	private final ContextMenuItemFactory factory;
	private final EmbedManager embedManager;

	/**
	 * Constructor
	 * @param app - application
	 */
	public CalculatorSubMenu(App app) {
		this.app = app;
		factory = new ContextMenuItemFactory();
		embedManager = app.getEmbedManager();

		if (embedManager != null) {
			addItem(GeoGebraConstants.GRAPHING_APPCODE);
			addItem(GeoGebraConstants.G3D_APPCODE);
			addItem(GeoGebraConstants.GEOMETRY_APPCODE);
			addItem(GeoGebraConstants.CAS_APPCODE);
			addItem(GeoGebraConstants.PROBABILITY_APPCODE);
		}
	}

	private void addItem(String subApp) {
		AppDescription description = AppDescription.get(subApp);
		addItem(factory.newAriaMenuItem(null,
				app.getLocalization().getMenu(description.getNameKey()),
				() -> embedManager.addSuiteCalcWithPreselectedApp(subApp)));
	}
}