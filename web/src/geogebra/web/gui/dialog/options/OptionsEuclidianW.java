package geogebra.web.gui.dialog.options;

import geogebra.common.awt.GColor;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.euclidian.event.KeyEvent;
import geogebra.common.euclidian.event.KeyHandler;
import geogebra.common.gui.dialog.options.OptionsEuclidian;
import geogebra.common.gui.dialog.options.model.EuclidianOptionsModel;
import geogebra.common.gui.dialog.options.model.EuclidianOptionsModel.IEuclidianOptionsListener;
import geogebra.common.gui.dialog.options.model.EuclidianOptionsModel.MinMaxType;
import geogebra.common.main.App;
import geogebra.html5.euclidian.EuclidianViewWeb;
import geogebra.html5.event.FocusListener;
import geogebra.html5.gui.inputfield.AutoCompleteTextFieldW;
import geogebra.html5.gui.util.LayoutUtil;
import geogebra.web.gui.images.AppResources;
import geogebra.web.gui.view.algebra.InputPanelW;
import geogebra.web.main.AppW;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabBar;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

public class OptionsEuclidianW extends OptionsEuclidian implements OptionPanelW,
	IEuclidianOptionsListener {

	private AppW app;
	private TabPanel tabPanel;
	private EuclidianView view;
	private EuclidianOptionsModel model;
	private BasicTab basicTab;
	private class EuclidianTab extends FlowPanel{};
	private class BasicTab extends EuclidianTab {
		private Label dimTitle;
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
		private CheckBox cbShowAxes;
		private CheckBox cbBoldAxes;
		private Label colorLabel;
		private Button btAxesColor;
		private Label lineStyle;
		private FlowPanel axesOptionsPanel;
		private AutoCompleteTextFieldW axesOptionTitle;
		private Label axesOptionsTitle;

		public BasicTab() {
			addDimensionPanel();
			addAxesOptionsPanel();
			addConsProtocolPanel();
			addMiscPanel();
		}

		private void addMinMaxHandler(final AutoCompleteTextFieldW tf, final MinMaxType type) {

			tf.addKeyHandler(new KeyHandler() {

				public void keyReleased(KeyEvent e) {
					if (e.isEnterKey()) {
						model.applyMinMax(tf.getText(), type);
					}
				}});

			tf.addFocusListener(new FocusListener(this){
				@Override
				protected void wrapFocusLost(){
					model.applyMinMax(tf.getText(), type);
				}	
			});

		}
		protected double parseDouble(String text) {
			if (text == null || text.equals(""))
				return Double.NaN;
			return app.getKernel().getAlgebraProcessor().evaluateToDouble(text);
		}
		
		private void addAxesRatioHandler(final AutoCompleteTextFieldW tf) {
	
			tf.addKeyHandler(new KeyHandler() {

				public void keyReleased(KeyEvent e) {
					if (e.isEnterKey()) {
						final double xval = parseDouble(tfAxesRatioX.getText());
						final double yval = parseDouble(tfAxesRatioY.getText());
						model.applyAxesRatio(xval, yval);
					}
				}});

			tf.addFocusListener(new FocusListener(this){
				@Override
				protected void wrapFocusLost(){
					final double xval = parseDouble(tfAxesRatioX.getText());
					final double yval = parseDouble(tfAxesRatioY.getText());
					model.applyAxesRatio(xval, yval);
				}	
			});

		}
		
		private void addDimensionPanel() {
			dimTitle = new Label("");
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
			
			cbLockRatio = new ToggleButton(new Image(AppResources.INSTANCE.lock()));
			cbLockRatio.setValue(view.isLockedAxesRatio());
			cbLockRatio.setEnabled(view.isZoomable());
		
			axesRatioLabel = new Label("");

			dimPanel = new FlowPanel();
			add(dimTitle);
			dimPanel.add(LayoutUtil.panelRow(dimLabel[0], tfMinX, dimLabel[1],
					tfMaxX));
			
		
			dimPanel.add(LayoutUtil.panelRow(dimLabel[2], tfMinY, dimLabel[3],
					tfMaxY));
			
			dimPanel.add(LayoutUtil.panelRow(axesRatioLabel));
			dimPanel.add(LayoutUtil.panelRow(tfAxesRatioX, new Label(" : "),
					tfAxesRatioY, cbLockRatio));
			
			add(dimPanel);
			
			addMinMaxHandler(tfMinX, MinMaxType.minX);
			addMinMaxHandler(tfMaxX, MinMaxType.maxX);

			addMinMaxHandler(tfMinY, MinMaxType.minY);
			addMinMaxHandler(tfMaxY, MinMaxType.maxY);
			
			addAxesRatioHandler(tfAxesRatioX);
			addAxesRatioHandler(tfAxesRatioY);
			
		}
		
		private void addAxesOptionsPanel() {

			axesOptionsTitle = new Label();
			// show axes checkbox
			cbShowAxes = new CheckBox(app.getPlain("ShowAxes"));

			// show bold checkbox
			cbBoldAxes = new CheckBox(app.getPlain("Bold"));

			// axes color
			colorLabel = new Label(app.getPlain("Color") + ":");

			btAxesColor = new Button("\u2588");
			
			// axes style
			lineStyle = new Label(app.getPlain("LineStyle") + ":");
		
//			AxesStyleListRenderer renderer = new AxesStyleListRenderer();
//			cbAxesStyle = new JComboBox(EuclidianStyleConstants.lineStyleOptions);
//			cbAxesStyle.setRenderer(renderer);
//			cbAxesStyle.setMaximumRowCount(AxesStyleListRenderer.MAX_ROW_COUNT);
//			cbAxesStyle.setEditable(false);

			// axes options panel
			axesOptionsPanel = new FlowPanel();
			axesOptionsPanel.add(axesOptionsTitle);
			axesOptionsPanel.add(LayoutUtil.panelRow(cbShowAxes,
					 cbBoldAxes));
			axesOptionsPanel.add(LayoutUtil.panelRow(colorLabel, btAxesColor,
					 lineStyle));
			cbShowAxes.addClickHandler(new ClickHandler(){

				public void onClick(ClickEvent event) {
					model.showAxes(cbShowAxes.getValue());

                }});
			
			cbBoldAxes.addClickHandler(new ClickHandler(){

				public void onClick(ClickEvent event) {
					model.applyBoldAxes(cbBoldAxes.getValue(), cbShowAxes.getValue());

                }});
			
			cbLockRatio.addClickHandler(new ClickHandler() {

				public void onClick(ClickEvent event) {
					if (cbLockRatio.getValue()) {
						model.applyLockRatio(parseDouble(tfAxesRatioX.getText())
								/ parseDouble(tfAxesRatioY.getText()));
					} else {
						model.applyLockRatio(null);
					}

                }});
			
			add(axesOptionsPanel);
		}

		private void addConsProtocolPanel() {
	        // TODO Auto-generated method stub
	        
        }

		private void addMiscPanel() {
	        // TODO Auto-generated method s0tub
	        
        }

		public void setLabels() {
			dimTitle.setText(app.getPlain("Dimensions"));
			
			dimLabel[0].setText(app.getPlain("xmin") + ":");
			dimLabel[1].setText(app.getPlain("xmax") + ":");
			dimLabel[2].setText(app.getPlain("ymin") + ":");
			dimLabel[3].setText(app.getPlain("ymax") + ":");
			axesRatioLabel.setText(app.getPlain("xAxis") + " : "
					+ app.getPlain("yAxis"));
			
			axesOptionsTitle.setText(app.getPlain("Axes"));
			cbShowAxes.setText(app.getPlain("ShowAxes"));
			cbBoldAxes.setText(app.getPlain("Bold"));
			colorLabel.setText(app.getPlain("Color") + ":");
			lineStyle.setText(app.getPlain("LineStyle") + ":");

		}

		public void enableAxesRatio(boolean value) {
			tfAxesRatioX.getTextBox().setEnabled(value);
			tfAxesRatioY.getTextBox().setEnabled(value);
		}

		public void setMinMaxText(String minX, String maxX, String minY, String maxY) {
			tfMinX.setText(minX);
			tfMaxX.setText(maxX);
			tfMinY.setText(minY);
			tfMaxY.setText(maxY);

		}
	}
	public OptionsEuclidianW(AppW app,
            EuclidianViewInterfaceCommon activeEuclidianView) {
		this.app = app;
		this.view = (EuclidianView) activeEuclidianView;
		model = new EuclidianOptionsModel(app, view, this);
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
	
	public GColor getEuclidianBackground(int viewNumber) {
		return null;
//		return new GColorW(
//				((GuiManagerW) (app.getGuiManager()))
//						.showColorChooser(app.getSettings()
//								.getEuclidian(viewNumber).getBackground()));
	}

	public void enableAxesRatio(boolean value) {
		basicTab.enableAxesRatio(value);
	}		

	public void setMinMaxText(String minX, String maxX, String minY, String maxY) {
		basicTab.setMinMaxText(minX, maxX, minY, maxY);
	}
}

