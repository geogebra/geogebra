package org.geogebra.web.phone.gui.view.menu;

import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.phone.gui.view.AbstractViewPanel;
import org.geogebra.web.web.gui.menubar.MainMenu;

public class MenuViewPanel extends AbstractViewPanel {

	private MainMenu menu;

	/**
	 * @param app
	 *            {@link AppW}
	 */
	public MenuViewPanel(AppW app) {
		super(app);
		this.menu = (MainMenu) app.getLAF().getMenuBar(app);
		this.menu.addStyleName("phoneMenu");
		this.add(menu);
	}

	@Override
	protected String getViewPanelStyleName() {
		return "menuViewPanel";
	}

}
