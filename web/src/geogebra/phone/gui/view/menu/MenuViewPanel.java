package geogebra.phone.gui.view.menu;

import geogebra.html5.main.AppW;
import geogebra.phone.gui.view.AbstractViewPanel;

public class MenuViewPanel extends AbstractViewPanel {

	public MenuViewPanel(AppW app) {
		super(app);
	}

	@Override
	protected String getViewPanelStyleName() {
		return "menuViewPanel";
	}

}
