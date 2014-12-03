package geogebra.phone.gui.views;

import geogebra.html5.gui.ResizeListener;
import geogebra.html5.main.AppW;
import geogebra.phone.gui.views.browseView.MaterialListPanelP;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

public class MaterialViewPanel extends FlowPanel implements ResizeListener {

	private MaterialListPanelP materialListPanel;

	public MaterialViewPanel(final AppW app) {
		materialListPanel = new MaterialListPanelP(app);
		ScrollPanel scrollPanel = new ScrollPanel(materialListPanel);
		add(scrollPanel);
	}

	public void onResize() {
		setPixelSize(Window.getClientWidth(), Window.getClientHeight() - 43);
	}

	public MaterialListPanelP getMaterialListPanelP() {
		return materialListPanel;
	}
}
