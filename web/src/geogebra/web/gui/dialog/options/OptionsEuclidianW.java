package geogebra.web.gui.dialog.options;

import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.gui.dialog.options.OptionsEuclidian;
import geogebra.common.main.App;
import geogebra.html5.euclidian.EuclidianViewWeb;
import geogebra.web.main.AppW;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabBar;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;

public class OptionsEuclidianW extends OptionsEuclidian implements OptionPanelW {

	private AppW app;
	private TabPanel tabPanel;
	private class EuclidianTab extends FlowPanel{};
	public OptionsEuclidianW(AppW app,
            EuclidianViewInterfaceCommon activeEuclidianView) {
		this.app = app;
		initGUI();
    }

	private void initGUI() {
		tabPanel = new TabPanel();
		addBasicTab();
		addXAxisTab();
		addYAxisTab();
		addGridTab();
		setLabels();
	    app.setDefaultCursor();
    }

	private void addBasicTab() {
		EuclidianTab tab = new EuclidianTab();
		tab.setStyleName("propertiesTab");
		tab.add(new Label("basic"));
		tabPanel.add(tab, "basic");
	}
	
	private void addXAxisTab() {
		EuclidianTab tab = new EuclidianTab();
		tab.setStyleName("propertiesTab");
		tab.add(new Label("X axis"));
		tabPanel.add(tab, "x");
	}
	
	private void addYAxisTab() {
		EuclidianTab tab = new EuclidianTab();
		tab.setStyleName("propertiesTab");
		tab.add(new Label("Y axis"));
		tabPanel.add(tab, "y");
	}
	
	private void addGridTab() {
		EuclidianTab tab = new EuclidianTab();
		tab.setStyleName("propertiesTab");
		tab.add(new Label("grid"));
		tabPanel.add(tab, "grid");
	}
	
	public void setLabels() {
	    TabBar tabBar = tabPanel.getTabBar();
	    tabBar.setTabText(0, app.getMenu("Properties.Basic"));
	    tabBar.setTabText(1, app.getPlain("xAxis"));
	    tabBar.setTabText(2, app.getPlain("yAxis"));
	    tabBar.setTabText(3, app.getPlain("Grid"));
		    
    }

	public void setView(EuclidianViewWeb euclidianView1) {
	    // TODO Auto-generated method stub
	    App.debug("setView");
    }

	public void showCbView(boolean b) {
	    App.debug("showCbView");
	        // TODO Auto-generated method stub
	    
    }

	public void updateGUI() {
	    App.debug("updateGUI");
	    
	    setLabels();
    }

	@Override
    public void updateBounds() {
		App.debug("updateBounds");
	    
    }

	public Widget getWrappedPanel() {
	    return tabPanel;
    }

}
