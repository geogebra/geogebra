package org.geogebra.web.full.gui.contextmenu;

import org.geogebra.common.euclidian.EmbedManager;
import org.geogebra.common.main.App;
import org.geogebra.web.full.gui.ContextMenuItemFactory;
import org.geogebra.web.html5.gui.menu.AriaMenuBar;

public class CalculatorSubMenu extends AriaMenuBar {
	private final App app;
	private final ContextMenuItemFactory factory;
	private final EmbedManager embedManager;

	public CalculatorSubMenu(App app) {
		this.app = app;
		factory = new ContextMenuItemFactory();
		embedManager = app.getEmbedManager();

		if (embedManager != null) {
			addItem("Graphing", "graphing");
			addItem("3d", "3d");
			addItem("Geometry", "geometry");
			addItem("Cas", "cas");
			addItem("Probability", "probability");
		}
	}

	private void addItem(String key, String code) {
		addItem(factory.newAriaMenuItem(null, app.getLocalization().getMenu(key),
				() -> embedManager.addSuiteCalcWithPreselectedAppCode(code)));
	}
}
