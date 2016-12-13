package org.geogebra.web.phone.gui.view.menu;

import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.phone.gui.view.AbstractViewPanel;
import org.geogebra.web.web.gui.menubar.MainMenu;

/**
 * Phone panel for menu
 */
public class MenuViewPanel extends AbstractViewPanel {


	/**
	 * @param app
	 *            {@link AppW}
	 */
	public MenuViewPanel(AppW app) {
		super(app);
		MainMenu menu = (MainMenu) app.getLAF().getMenuBar(app);
		menu.addStyleName("phoneMenu");
		add(menu);
		onResize();
	}

	@Override
	protected String getViewPanelStyleName() {
		return "menuViewPanel";
	}

}
