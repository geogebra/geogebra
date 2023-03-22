package org.geogebra.web.shared.components.tab;

import org.gwtproject.user.client.ui.Widget;

public class TabData {
	private String tabTitle;
	private Widget tabPanel;

	/**
	 * tab settings constructor
	 * @param tabTitle - ggb trans key of the tab title
	 * @param tabPanel - panel holding the content of the tab
	 */
	public TabData(String tabTitle, Widget tabPanel) {
		setTabTitle(tabTitle);
		setTabPanel(tabPanel);
	}

	private void setTabTitle(String tabTitle) {
		this.tabTitle = tabTitle;
	}

	private void setTabPanel(Widget tabPanel) {
		this.tabPanel = tabPanel;
	}

	public String getTabTitle() {
		return tabTitle;
	}

	public Widget getTabPanel() {
		return tabPanel;
	}
}
