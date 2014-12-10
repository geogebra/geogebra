package geogebra.phone.gui.view.material;

import geogebra.html5.main.AppW;
import geogebra.phone.gui.view.AbstractViewPanel;
import geogebra.phone.gui.view.material.browser.MaterialListPanelP;

import com.google.gwt.user.client.ui.ScrollPanel;

public class MaterialViewPanel extends AbstractViewPanel {

	private MaterialListPanelP materialListPanel;

	public MaterialViewPanel(final AppW app) {
		super(app);
		materialListPanel = new MaterialListPanelP(app);
		ScrollPanel scrollPanel = new ScrollPanel(materialListPanel);
		add(scrollPanel);
	}

	public MaterialListPanelP getMaterialListPanelP() {
		return materialListPanel;
	}

	@Override
	protected String getViewPanelStyleName() {
		return "materialViewPanel";
	}
}
