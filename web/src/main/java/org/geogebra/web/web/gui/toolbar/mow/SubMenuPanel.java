package org.geogebra.web.web.gui.toolbar.mow;

import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.FlowPanel;

public class SubMenuPanel extends FlowPanel {
	private AppW app;
	private boolean info;
	FlowPanel contentPanel;
	FlowPanel infoPanel;

	public SubMenuPanel(AppW app, boolean info) {
		this.app=app;
		this.info = info;
		createGUI();
	}

	protected void createGUI() {
		addStyleName("mowSubMenu");
		createContentPanel();
		if (hasInfo()) {
			createInfoPanel();
			add(LayoutUtilW.panelRow(contentPanel, infoPanel));
		} else {
			add(contentPanel);
		}
	}

	protected void createContentPanel() {
		contentPanel = new FlowPanel();
		contentPanel.addStyleName("mowSubMenuContent");

	}

	protected void createInfoPanel() {
		infoPanel = new FlowPanel();
		contentPanel.addStyleName("mowSubMenuInfo");
	}

	public boolean hasInfo() {
		return info;
	}

	public void setInfo(boolean info) {
		this.info = info;
	}

}
