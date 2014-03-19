package geogebra.web.gui.dialog.options;

import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.gui.dialog.options.OptionsEuclidian;
import geogebra.common.main.App;
import geogebra.html5.euclidian.EuclidianViewWeb;
import geogebra.html5.gui.inputfield.AutoCompleteTextFieldW;
import geogebra.html5.gui.util.LayoutUtil;
import geogebra.web.gui.images.AppResources;
import geogebra.web.gui.view.algebra.InputPanelW;
import geogebra.web.main.AppW;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabBar;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

public class OptionsEuclidianW extends OptionsEuclidian implements OptionPanelW {

	private AppW app;
	private TabPanel tabPanel;
	private EuclidianView view;
	private BasicTab basicTab;
	private class EuclidianTab extends FlowPanel{};
	private class BasicTab extends EuclidianTab {
		private Label[] dimLabel;
		private AutoCompleteTextFieldW tfMinX;
		private AutoCompleteTextFieldW tfMaxX;
		private AutoCompleteTextFieldW tfMinY;
		private AutoCompleteTextFieldW tfMaxY;
		private AutoCompleteTextFieldW tfAxesRatioX;
		private AutoCompleteTextFieldW tfAxesRatioY;
		
		private Label axesRatioLabel;
		private FlowPanel dimPanel;
		private ToggleButton cbLockRatio;

		public BasicTab() {
			addDimensionPanel();
		}

		
		private void addDimensionPanel() {
			dimLabel = new Label[4]; // "Xmin", "Xmax" etc.
			for (int i = 0; i < 4; i++) {
				dimLabel[i] = new Label("");
			}

			
			tfMinX = getTextField();
			tfMaxX = getTextField();
			
			tfMinY = getTextField();
			tfMaxY = getTextField();
			
			tfAxesRatioX = getTextField();
			tfAxesRatioY = getTextField();
			//tfAxesRatioX.setEnabled(view.isZoomable() && !view.isLockedAxesRatio());
		//	tfAxesRatioY.setEnabled(view.isZoomable() && !view.isLockedAxesRatio());
			
			cbLockRatio = new ToggleButton(new Image(AppResources.INSTANCE.lock().getSafeUri().toString()));
			cbLockRatio.setValue(view.isLockedAxesRatio());
			cbLockRatio.setEnabled(view.isZoomable());
		
			axesRatioLabel = new Label("");

			dimPanel = new FlowPanel();

			dimPanel.add(LayoutUtil.panelRow(dimLabel[0], tfMinX, dimLabel[1],
					tfMaxX));
			
		
			dimPanel.add(LayoutUtil.panelRow(dimLabel[2], tfMinY, dimLabel[3],
					tfMaxY));
			
			dimPanel.add(LayoutUtil.panelRow(axesRatioLabel));
			dimPanel.add(LayoutUtil.panelRow(tfAxesRatioX, new Label(" : "),
					tfAxesRatioY, cbLockRatio));
			
			add(dimPanel);
        }
		
		public void setLabels() {
			dimLabel[0].setText(app.getPlain("xmin") + ":");
			dimLabel[1].setText(app.getPlain("xmax") + ":");
			dimLabel[2].setText(app.getPlain("ymin") + ":");
			dimLabel[3].setText(app.getPlain("ymax") + ":");
			axesRatioLabel.setText(app.getPlain("xAxis") + " : "
					+ app.getPlain("yAxis"));
		}
	}
	public OptionsEuclidianW(AppW app,
            EuclidianViewInterfaceCommon activeEuclidianView) {
		this.app = app;
		this.view = (EuclidianView) activeEuclidianView;
		initGUI();
    }

	private void initGUI() {
		tabPanel = new TabPanel();
		addBasicTab();
		addXAxisTab();
		addYAxisTab();
		addGridTab();
		setLabels();
	    tabPanel.selectTab(0);
		app.setDefaultCursor();
    }

	private void addBasicTab() {
		basicTab = new BasicTab();
		basicTab.setStyleName("propertiesTab");
		tabPanel.add(basicTab, "basic");
	}
	
	private void addXAxisTab() {
		EuclidianTab tab = new EuclidianTab();
		tab.setStyleName("propertiesTab");
		tab.add(new AxisPanel(app, view,0));
		tabPanel.add(tab, "x");
	}
	
	private void addYAxisTab() {
		EuclidianTab tab = new EuclidianTab();
		tab.setStyleName("propertiesTab");
		tab.add(new AxisPanel(app, view, 1));
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
	    basicTab.setLabels();    
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
	
	private AutoCompleteTextFieldW getTextField() {
		InputPanelW input = new InputPanelW(null, (AppW) app, 1, -1, true);
		AutoCompleteTextFieldW tf = (AutoCompleteTextFieldW)input.getTextComponent();
		tf.setStyleName("numberInput");
		return tf;
	}
}
