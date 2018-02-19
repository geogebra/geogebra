package org.geogebra.web.full.gui.dialog.options;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.dialog.handler.ColorChangeHandler;
import org.geogebra.common.gui.dialog.options.OptionsEuclidian;
import org.geogebra.common.gui.dialog.options.model.EuclidianOptionsModel;
import org.geogebra.common.gui.dialog.options.model.EuclidianOptionsModel.IEuclidianOptionsListener;
import org.geogebra.common.main.Localization;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.gui.dialog.DialogManagerW;
import org.geogebra.web.full.gui.util.ComboBoxW;
import org.geogebra.web.full.gui.util.LineStylePopup;
import org.geogebra.web.full.gui.util.MyCJButton;
import org.geogebra.web.full.gui.util.NumberListBox;
import org.geogebra.web.full.gui.util.PopupMenuButtonW;
import org.geogebra.web.full.gui.util.PopupMenuHandler;
import org.geogebra.web.full.gui.view.algebra.InputPanelW;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.util.FormLabel;
import org.geogebra.web.html5.gui.util.ImageOrText;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.tabpanel.MultiRowsTabBar;
import org.geogebra.web.html5.util.tabpanel.MultiRowsTabPanel;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.himamis.retex.editor.share.util.Unicode;

public class OptionsEuclidianW extends OptionsEuclidian implements OptionPanelW,
	IEuclidianOptionsListener {

	protected AppW app;
	protected MultiRowsTabPanel tabPanel;
	protected EuclidianView view;
	public EuclidianOptionsModel model;
	protected BasicTab basicTab;
	AxisTab xAxisTab;
	AxisTab yAxisTab;
	private GridTab gridTab;
	private boolean isIniting;
	protected Localization loc;
	
	protected static abstract class EuclidianTab extends FlowPanel
			implements SetLabels {
		
		protected EuclidianTab(AppW app) {
			if (app.isUnbundledOrWhiteboard()) {
				setStyleName("propMaterialTab");
			} else {
				setStyleName("propertiesTab");
			}
		}
		
		public void onResize(int height, int width) {
			this.setHeight(height + "px");
			this.setWidth(width + "px");
		}
	}
	
	protected class AxisTab extends EuclidianTab {
		private AxisPanel axisPanel;
			
		public AxisTab(int axis, boolean view3D) {
			super(app);
			axisPanel = new AxisPanel(app, view, axis, view3D);
			add(axisPanel);
		}
		
		public void updateView(EuclidianView view) {
			axisPanel.updateView(view);
		}

		public void setShowAxis(boolean value) {
			axisPanel.setShowAxis(value);
		}

		@Override
		public void setLabels() {
			axisPanel.setLabels();
		}
	}
		
	protected class GridTab extends EuclidianTab {
		private static final int ICON_HEIGHT = 24;
		CheckBox cbShowGrid;
		private FormLabel lbPointCapturing;
		private ListBox pointCapturingStyleList;
		ListBox lbGridType;
		CheckBox cbGridManualTick;
		NumberListBox ncbGridTickX;
		NumberListBox ncbGridTickY;
		ComboBoxW cbGridTickAngle;
		private FormLabel gridLabel1;
		private FormLabel gridLabel2;
		private FormLabel gridLabel3;
		protected FormLabel lblGridType;
		private Label lblGridStyle;
		LineStylePopup btnGridStyle;
		private Label lblColor;
		CheckBox cbBoldGrid;
		private MyCJButton btGridColor;
		private FlowPanel mainPanel;

		public GridTab() {
			super(app);
			cbShowGrid = new CheckBox();
			cbShowGrid.addClickHandler(new ClickHandler(){

				@Override
				public void onClick(ClickEvent event) {
					enableGrid(cbShowGrid.getValue());
					app.storeUndoInfo();
				}
			});
			mainPanel = new FlowPanel();

			add(cbShowGrid);
			addPointCapturingStyle();
			add(mainPanel);
			initGridTypePanel();
			initGridStylePanel();
		}
		
		/**
		 * update gui of grid tab
		 */
		public void updateGUI() {
			updatePointCapturingStyleList();
		}

		private void addPointCapturingStyle() {
			pointCapturingStyleList = new ListBox();
			lbPointCapturing = new FormLabel(
					loc.getMenu("PointCapturing") + ":")
							.setFor(pointCapturingStyleList);
			updatePointCapturingStyleList();
			mainPanel.add(LayoutUtilW.panelRowIndent(lbPointCapturing,
					pointCapturingStyleList));
			pointCapturingStyleList.addChangeHandler(new ChangeHandler() {

				@Override
				public void onChange(ChangeEvent event) {
					int index = getPointCapturingStyleList().getSelectedIndex();
					app.getEuclidianView1().setPointCapturing(
							getPointCapturingModeList(index));
					if (app.hasEuclidianView2EitherShowingOrNot(1)) {
						app.getEuclidianView2(1).setPointCapturing(index);
					}
					app.setUnsaved();
					app.storeUndoInfo();
				}
			});
		}

		/**
		 * @param index
		 *            selected index in list
		 * @return point capturing mode
		 */
		public int getPointCapturingModeList(int index) {
			switch (index) {
			case 0:
				return EuclidianStyleConstants.POINT_CAPTURING_AUTOMATIC;
			case 1:
				return EuclidianStyleConstants.POINT_CAPTURING_STICKY_POINTS;
			case 2:
				return EuclidianStyleConstants.POINT_CAPTURING_ON_GRID;
			case 3:
				return EuclidianStyleConstants.POINT_CAPTURING_OFF;
			default:
				return EuclidianStyleConstants.POINT_CAPTURING_AUTOMATIC;
			}
		}

		/**
		 * @return list of point capturing style
		 */
		public ListBox getPointCapturingStyleList() {
			return pointCapturingStyleList;
		}

		private void updatePointCapturingStyleList() {
			pointCapturingStyleList.clear();
			String[] strPointCapturing = new String[] {
					loc.getMenu("Labeling.automatic"),
					loc.getMenu("SnapToGrid"), loc.getMenu("FixedToGrid"),
					loc.getMenu("off") };
			for (String str : strPointCapturing) {
				pointCapturingStyleList.addItem(str);
			}
			pointCapturingStyleList.setSelectedIndex(getPointCapturingModeEV());
		}

		private int getPointCapturingModeEV() {
			int mode = app.getActiveEuclidianView().getPointCapturingMode();
			switch (mode) {
			case EuclidianStyleConstants.POINT_CAPTURING_AUTOMATIC:
				return 0;
			case EuclidianStyleConstants.POINT_CAPTURING_STICKY_POINTS:
				return 1;
			case EuclidianStyleConstants.POINT_CAPTURING_ON_GRID:
				return 2;
			case EuclidianStyleConstants.POINT_CAPTURING_OFF:
				return 3;
			default:
				return 0;
			}
		}

		void enableGrid(boolean value) {
			model.showGrid(value);
			if (value) {
				mainPanel.removeStyleName("disabled");
			} else {
				mainPanel.setStyleName("disabled");
			}
			lbGridType.setEnabled(value);
			cbGridManualTick.setEnabled(value);
			btnGridStyle.setEnabled(value);
			cbBoldGrid.setEnabled(value);
			btGridColor.setEnabled(value);
		}

		private void initGridTypePanel() {
			// grid type combo box

			lbGridType = new ListBox();
			lblGridType = new FormLabel("").setFor(lbGridType);
			mainPanel.add(lblGridType);
			lblGridType.setStyleName("panelTitle");
			
			lbGridType.addChangeHandler(new ChangeHandler(){

				@Override
				public void onChange(ChangeEvent event) {
					model.appyGridType(lbGridType.getSelectedIndex());
					updateView();
					app.storeUndoInfo();
				}
			});
			// tick intervals

			cbGridManualTick = new CheckBox();
			cbGridManualTick.addClickHandler(new ClickHandler(){

				@Override
				public void onClick(ClickEvent event) {
					model.appyGridManualTick(cbGridManualTick.getValue());
					updateView();
				}
			});
			cbGridManualTick.setStyleName("checkBoxPanel");
			ncbGridTickX = new NumberListBox(app){

				@Override
				protected void onValueChange(String value) {
					model.applyGridTicks(ncbGridTickX.getValue(), 0);
					updateView();
				}
			};
	
			ncbGridTickY = new NumberListBox(app) {

				@Override
				protected void onValueChange(String value) {
					model.applyGridTicks(ncbGridTickY.getValue(), 1);
					updateView();
				}
			};

			// checkbox for grid labels
			cbGridTickAngle = new ComboBoxW(app) {

				@Override
				protected void onValueChange(String value) {
					model.applyGridTickAngle(
							cbGridTickAngle.getValue());
					updateView();
				}
			};
			
			FlowPanel gridTickAnglePanel = new FlowPanel();
			gridTickAnglePanel.setStyleName("panelRow");
			addGridType(gridTickAnglePanel);
		
			
			// grid labels
			gridLabel1 = new FormLabel("x:").setFor(this.ncbGridTickX);
			gridLabel2 = new FormLabel("y:").setFor(this.ncbGridTickY);
			gridLabel3 = new FormLabel(Unicode.theta + ":")
					.setFor(cbGridTickAngle);
			
			FlowPanel ncbGridTickXPanel = new FlowPanel();
			FlowPanel ncbGridTickYPanel = new FlowPanel();
			FlowPanel ncbGridTickAnglePanel = new FlowPanel();
			ncbGridTickXPanel.setStyleName("panelRowCell");
			ncbGridTickYPanel.setStyleName("panelRowCell");
			ncbGridTickAnglePanel.setStyleName("panelRowCell");
			ncbGridTickXPanel.add(gridLabel1);
			ncbGridTickXPanel.add(ncbGridTickX);
			ncbGridTickYPanel.add(gridLabel2);
			ncbGridTickYPanel.add(ncbGridTickY);
			ncbGridTickAnglePanel.add(gridLabel3);
			ncbGridTickAnglePanel.add(cbGridTickAngle);
		
			FlowPanel tickPanel = LayoutUtilW.panelRow(cbGridManualTick, ncbGridTickXPanel, 
					ncbGridTickYPanel, ncbGridTickAnglePanel);
			mainPanel.add(tickPanel);
			
			FlowPanel typePanel = new FlowPanel();
			typePanel.add(gridTickAnglePanel);
			typePanel.add(cbGridManualTick);
			typePanel.add(LayoutUtilW.panelRowIndent(
					ncbGridTickXPanel, ncbGridTickYPanel, ncbGridTickAnglePanel));

			
			lbGridType.addChangeHandler(new ChangeHandler(){
				@Override
				public void onChange(ChangeEvent event) {
					model.appyGridType(lbGridType.getSelectedIndex());
					updateView();					
				}
			});
			typePanel.setStyleName("panelIndent");
			mainPanel.add(typePanel);
		}
		
		protected void addGridType(FlowPanel gridTickAnglePanel){
			gridTickAnglePanel.add(lbGridType);
		}

		private void initGridStylePanel() {
			// line style
			btnGridStyle = LineStylePopup.create(app, -1, false,
					app.isUnbundledOrWhiteboard());
			
			lblGridStyle = new Label();
			addOnlyFor2D(lblGridStyle);
			lblGridStyle.setStyleName("panelTitle");
			btnGridStyle.addPopupHandler(new PopupMenuHandler() {

				@Override
				public void fireActionPerformed(PopupMenuButtonW actionButton) {
					model.appyGridStyle(EuclidianView
							.getLineType(btnGridStyle.getSelectedIndex()));
				}});
			btnGridStyle.setKeepVisible(false);

			// color
			lblColor = new Label();
			btGridColor = new MyCJButton(app);
			btGridColor.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					if (!cbShowGrid.getValue()) {
						return;
					}
					getDialogManager().showColorChooserDialog(
							model.getGridColor(),
							new ColorChangeHandler() {
						
						@Override
						public void onForegroundSelected() {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void onColorChange(GColor color) {
							model.applyGridColor(color);
							updateGridColorButton(color);
						}
						
						@Override
						public void onClearBackground() {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void onBackgroundSelected() {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void onAlphaChange() {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void onBarSelected() {
							// TODO Auto-generated method stub

						}
					});
				// Just for dummy.
//				
				}
			});
			// bold
			cbBoldGrid = new CheckBox();
			cbBoldGrid.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					model.applyBoldGrid(cbBoldGrid.getValue());
					updateView();
				}
			});

			// style panel
			FlowPanel stylePanel = new FlowPanel();

			stylePanel.add(LayoutUtilW.panelRowIndent(btnGridStyle));
			stylePanel.add(LayoutUtilW.panelRowIndent(lblColor, btGridColor, cbBoldGrid));
			
			addOnlyFor2D(stylePanel);
		}
		
		
		protected void addOnlyFor2D(Widget w){
			mainPanel.add(w);
		}


		@Override
		public void setLabels() {
			cbShowGrid.setText(loc.getMenu("ShowGrid"));
			setTextColon(lbPointCapturing, "PointCapturing");
			updatePointCapturingStyleList();
			int idx = lbGridType.getSelectedIndex();
			setGridTypeLabel();
			lbGridType.clear();
			model.fillGridTypeCombo();
			lbGridType.setSelectedIndex(idx);

			idx = cbGridTickAngle.getSelectedIndex();
			cbGridTickAngle.cleanSelection();
			model.fillAngleOptions();
			cbGridTickAngle.setSelectedIndex(idx);
			cbGridManualTick.setText(loc.getMenu("TickDistance") + ":");
			lblGridStyle.setText(loc.getMenu("LineStyle"));
			lblColor.setText(loc.getMenu("Color") + ":");
			cbBoldGrid.setText(loc.getMenu("Bold"));
		}
		
		protected void setGridTypeLabel(){
			lblGridType.setText(loc.getMenu("GridType"));
		}

		public void addGridTypeItem(String item) {
			lbGridType.addItem(item);
		}

		public void addAngleOptionItem(String item) {
			cbGridTickAngle.addItem(item);
		}

		public void update(GColor color, boolean isShown, boolean isBold,
				int gridType) {

			enableGrid(isShown);
			cbShowGrid.setValue(isShown);
			cbBoldGrid.setValue(isBold);
			lbGridType.setSelectedIndex(gridType);
			btGridColor.getElement().getStyle().setColor(StringUtil.toHtmlColor(color));
			updateGridColorButton(color);
		}
	
		public void updateTicks(boolean isAutoGrid, double[] gridTicks,
				int gridType) {
	
			if (gridType != EuclidianView.GRID_POLAR) {

				ncbGridTickY.setVisible(true);
				gridLabel2.setVisible(true);
				cbGridTickAngle.setVisible(false);
				gridLabel3.setVisible(false);

				ncbGridTickX.setDoubleValue(gridTicks[0]);
				ncbGridTickY.setDoubleValue(gridTicks[1]);
				gridLabel1.setText("x:");

			} else {
				ncbGridTickY.setVisible(false);
				gridLabel2.setVisible(false);
				cbGridTickAngle.setVisible(true);
				gridLabel3.setVisible(true);

				ncbGridTickX.setDoubleValue(gridTicks[0]);
				cbGridTickAngle.setValue(model.gridAngleToString());
				gridLabel1.setText("r:");
			}

			ncbGridTickX.setEnabled(!isAutoGrid);
			ncbGridTickY.setEnabled(!isAutoGrid);
			cbGridTickAngle.setEnabled(!isAutoGrid);
		}

		public void selectGridStyle(int style) {
			btnGridStyle.selectLineType(style);
		}
		
		public void updateGridColorButton(GColor color) {
			ImageOrText content = new ImageOrText();
			content.setBgColor(color);
			btGridColor.setIcon(content);
		}
	
	}
	
	public OptionsEuclidianW(AppW app,
			EuclidianViewInterfaceCommon activeEuclidianView) {
		isIniting = true;
		this.app = app;
		this.loc = app.getLocalization();
		this.view = (EuclidianView) activeEuclidianView;
		model = new EuclidianOptionsModel(app, view, this);
		initGUI();
		isIniting = false;
	}

	/**
	 * update the view (also for model)
	 * 
	 * @param view
	 *            view
	 */
	public void updateView(EuclidianView view) {
		setView(view);
		view.setOptionPanel(this);
		model.setView(view);
		xAxisTab.updateView(view);
		yAxisTab.updateView(view);
	}

	private void initGUI() {
		tabPanel = new MultiRowsTabPanel();
		addTabs();
		updateGUI();
		tabPanel.selectTab(0);
		app.setDefaultCursor();
		tabPanel.addSelectionHandler(new SelectionHandler<Integer>() {

			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				updateGUI();
			}
		});
	}
	
	/**
	 * add tabs
	 */
	protected void addTabs(){
		addBasicTab();
		addAxesTabs();
		addGridTab();
	}
	
	/**
	 * add tabs for axes
	 */
	protected void addAxesTabs(){
		addXAxisTab();
		addYAxisTab();
	}

	private void addBasicTab() {
		basicTab = newBasicTab();
		tabPanel.add(basicTab, "basic");
	}
	
	protected BasicTab newBasicTab(){
		return new BasicTab(this);
	}
	
	private void addXAxisTab() {
		xAxisTab = newAxisTab(EuclidianOptionsModel.X_AXIS);
		tabPanel.add(xAxisTab, "x");
	}
	
	private void addYAxisTab() {
		yAxisTab = newAxisTab(EuclidianOptionsModel.Y_AXIS);
		tabPanel.add(yAxisTab, "y");
	}
	
	/**
	 * 
	 * @param axis axis id
	 * @return axis tab
	 */
	protected AxisTab newAxisTab(int axis){
		return new AxisTab(axis, false);
	}
	
	private void addGridTab() {
		gridTab = newGridTab();
		tabPanel.add(gridTab, "grid");
	}
	
	/**
	 * 
	 * @return new grid tab
	 */
	protected GridTab newGridTab(){
		return new GridTab();
	}

	/**
	 * set labels
	 * @param tabBar tab bar
	 * @param gridIndex index for grid tab
	 */
	protected void setLabels(Widget tabBar2, int gridIndex) {

		MultiRowsTabBar tabBar = (MultiRowsTabBar) tabBar2;

		tabBar.setTabText(0, loc.getMenu("Properties.Basic"));
		tabBar.setTabText(1, loc.getMenu("xAxis"));
		tabBar.setTabText(2, loc.getMenu("yAxis"));
		tabBar.setTabText(gridIndex, loc.getMenu("Grid"));



		basicTab.setLabels();
		xAxisTab.setLabels();
		yAxisTab.setLabels();
		gridTab.setLabels();
	}

	/**
	 * set labels
	 */
	public void setLabels() {
			setLabels(tabPanel.getTabBar(), 3);

	}

	public void setView(EuclidianView euclidianView1) {
		this.view = euclidianView1;
		if (!isIniting) {
			updateGUI();
		}
	}

	public void showCbView(boolean b) {
		Log.warn("showCbView");
	}

	@Override
	public void updateGUI() {
		setLabels(); // resets all comboboxes: call *before* properties update
		model.updateProperties();
		getGridTab().updateGUI();
	}

	@Override
	public void updateBounds() {
		basicTab.updateBounds();
	}

	@Override
	public Widget getWrappedPanel() {
		return tabPanel;
	}
	
	protected AutoCompleteTextFieldW getTextField() {
		InputPanelW input = new InputPanelW(null, app, 1, -1, true);
		AutoCompleteTextFieldW tf = input.getTextComponent();
		tf.setStyleName("numberInput");
		return tf;
	}
	
	@Override
	public GColor getEuclidianBackground(int viewNumber) {
		return app.getSettings().getEuclidian(viewNumber).getBackground();
	}

	/**
	 * @return grid tab
	 */
	public GridTab getGridTab() {
		return gridTab;
	}

	@Override
	public void enableAxesRatio(boolean value) {
		basicTab.enableAxesRatio(value);
	}		

	@Override
	public void setMinMaxText(String minX, String maxX, String minY, String maxY) {
		basicTab.setMinMaxText(minX, maxX, minY, maxY);
	}

	@Override
	public void updateAxes(GColor color, boolean isShown, boolean isBold) {
		basicTab.updateAxes(color, isShown, isBold);
	}

	@Override
	public void updateBackgroundColor(GColor color) {
		basicTab.updateBackgroundColorButton(color);
	}
	
	@Override
	public void selectTooltipType(int index) {
		basicTab.lbTooltips.setSelectedIndex(index);
	}

	@Override
	public void updateConsProtocolPanel(boolean isVisible) {
		basicTab.updateConsProtocolPanel(isVisible);
	}

	@Override
	public void updateGrid(GColor color, boolean isShown, boolean isBold,
			int gridType) {
		gridTab.update(color, isShown, isBold, gridType);
	}

	@Override
	public void showMouseCoords(boolean value) {
		basicTab.showMouseCoords(value);
	}

	@Override
	public void selectAxesStyle(int index) {
	    basicTab.selectAxesStyle(index);
	}

	@Override
	public void updateGridTicks(boolean isAutoGrid, double[] gridTicks,
			int gridType) {
		gridTab.updateTicks(isAutoGrid, gridTicks, gridType);
	}

	@Override
	public void enableLock(boolean value) {
		basicTab.enabeLock(value);
	}

	@Override
	public void selectGridStyle(int style) {
		if (gridTab == null) {
			return;
		}
		gridTab.selectGridStyle(style);
	}

	@Override
	public void addGridTypeItem(String item) {
		if (gridTab == null) {
			return;
		}
		
		gridTab.addGridTypeItem(item);
	}

	@Override
	public void addAngleOptionItem(String item) {
		if (gridTab == null) {
			return;
		}
		gridTab.addAngleOptionItem(item);

	}

	protected void updateView() {
		view.updateBackground();
		updateGUI();
	}

	@Override
	public void onResize(int height, int width) {
		for(int i = 0; i < tabPanel.getWidgetCount(); i++) {
			EuclidianTab tab = (EuclidianTab) tabPanel.getWidget(i);
			if (tab != null) {
				tab.onResize(height, width);
			}
		}
	}

	/**
	 * select the correct tab
	 * 
	 * @param index
	 *            index
	 */
	public void setSelectedTab(int index) {
		// tabbedPane.setSelectedIndex(index);
		Log.warn("======== OptionsEuclidianW.setSelectedTab() : TODO");
	}

	@Override
	public void updateAxisFontStyle(boolean serif, boolean isBold,
			boolean isItalic) {
		basicTab.cbAxisLabelSerif.setValue(serif);
		basicTab.cbAxisLabelBold.setValue(isBold);
		basicTab.cbAxisLabelItalic.setValue(isItalic);
	}

	@Override
	public MultiRowsTabPanel getTabPanel() {
		return tabPanel;
	}

	protected DialogManagerW getDialogManager() {
		return (DialogManagerW) app.getDialogManager();
	}

	public void setTextColon(FormLabel cb, String string) {
		cb.setText(loc.getMenu(string) + ":");
	}
}

