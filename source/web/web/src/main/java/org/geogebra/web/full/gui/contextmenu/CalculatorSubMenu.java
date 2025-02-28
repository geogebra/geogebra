package org.geogebra.web.full.gui.contextmenu;

import static org.geogebra.common.GeoGebraConstants.REALSCHULE_APPCODE;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.SuiteSubApp;
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
			addItem(SuiteSubApp.GRAPHING);
			addItem(SuiteSubApp.G3D);
			addItem(SuiteSubApp.GEOMETRY);
			addItem(SuiteSubApp.CAS);
			addItem(SuiteSubApp.PROBABILITY);
			addItem(SuiteSubApp.SCIENTIFIC);
			if (app.isMebis()) {
				addItem(factory.newAriaMenuItem(null,
						"Grafikrechner (Bayern) (Beta)",
						() -> embedManager.addCalcWithPreselectedApp(REALSCHULE_APPCODE,
								GeoGebraConstants.GRAPHING_APPCODE)));
			}
		}
	}

	private void addItem(SuiteSubApp subApp) {
		AppDescription description = AppDescription.get(subApp);
		addItem(factory.newAriaMenuItem(null,
				app.getLocalization().getMenu(description.getNameKey()),
				() -> embedManager.addCalcWithPreselectedApp(GeoGebraConstants.SUITE_APPCODE,
						subApp.appCode)));
	}
}
