package org.geogebra.web.web.gui.dialog.options;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.gui.dialog.options.model.OptionsModel;
import org.geogebra.common.main.Localization;
import org.geogebra.web.web.gui.properties.IOptionPanel;
import org.geogebra.web.web.gui.properties.OptionPanel;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TabBar;
import com.google.gwt.user.client.ui.TabPanel;

class OptionsTab extends FlowPanel {
	/**
	 * 
	 */
	// private final OptionsObjectW optionsObjectW;
	private String titleId;
	private int index;
	private List<OptionsModel> models;
	private TabPanel tabPanel;
	private Localization loc;
	
	public OptionsTab(Localization loc, TabPanel tabPanel,
			final String title) {
		super();
		// this.optionsObjectW = optionsObjectW;
		this.titleId = title;
		this.loc = loc;
		this.tabPanel = tabPanel;
		models = new ArrayList<OptionsModel>();
		setStyleName("propertiesTab");
	}

	public void add(IOptionPanel panel) {
		add(panel.getWidget());
		models.add(panel.getModel());
	}

	public void addPanelList(List<OptionPanel> list) {
		for (OptionPanel panel: list) {
			add(panel);
		}
	}

	public boolean update(Object[] geos) {
		boolean enabled = false;
		for (OptionsModel panel : models) {
			enabled = panel.updatePanel(geos) || enabled;
		}

		TabBar tabBar = this.tabPanel.getTabBar();
		tabBar.setTabText(index, getTabText());
		tabBar.setTabEnabled(index, enabled);	
		if (!enabled && tabBar.getSelectedTab() == index) {
			tabBar.selectTab(0);
		}
		return enabled;
	}

	private String getTabText() {
		return loc.getMenu(titleId);
	}

	public void addToTabPanel() {
		this.tabPanel.add(this, getTabText());
		index = this.tabPanel.getWidgetIndex(this);
	}

	public void onResize(int height, int width) {
         this.setHeight(height + "px");
    }
}