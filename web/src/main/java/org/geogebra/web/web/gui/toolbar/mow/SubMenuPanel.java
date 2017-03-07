package org.geogebra.web.web.gui.toolbar.mow;

import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

public class SubMenuPanel extends FlowPanel {
	AppW app;
	private boolean info;
	ScrollPanel scrollPanel;
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
			add(LayoutUtilW.panelRow(scrollPanel, infoPanel));
		} else {
			add(scrollPanel);
		}
	}

	protected void createContentPanel() {
		scrollPanel = new ScrollPanel();
		scrollPanel.addStyleName("mowSubMenuContent");
		contentPanel = new FlowPanel();
		scrollPanel.add(contentPanel);
	}

	protected void createInfoPanel() {
		infoPanel = new FlowPanel();
		infoPanel.addStyleName("mowSubMenuInfo");
	}

	public boolean hasInfo() {
		return info;
	}

	public void setInfo(boolean info) {
		this.info = info;
	}

	public void onOpen() {

	}
}
