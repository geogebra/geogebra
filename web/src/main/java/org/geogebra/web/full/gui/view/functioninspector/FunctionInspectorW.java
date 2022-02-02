package org.geogebra.web.full.gui.view.functioninspector;

import java.util.ArrayList;
import java.util.Arrays;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.gui.view.functioninspector.FunctionInspector;
import org.geogebra.common.gui.view.functioninspector.FunctionInspectorModel.Colors;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.css.GuiResources;
import org.geogebra.web.full.gui.images.AppResources;
import org.geogebra.web.full.gui.util.MyCJButton;
import org.geogebra.web.full.gui.util.MyToggleButtonW;
import org.geogebra.web.full.gui.util.PopupMenuButtonW;
import org.geogebra.web.full.gui.view.algebra.InputPanelW;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;
import org.geogebra.web.html5.gui.util.ImageOrText;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.SharedResources;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabBar;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;

public class FunctionInspectorW extends FunctionInspector {

	private static final int PADDING_RIGHT = 45;
	// color constants
	private static final GColor DISPLAY_GEO_COLOR = GColor.RED;
	private static final GColor DISPLAY_GEO2_COLOR = GColor.RED;
	private static final GColor EVEN_ROW_COLOR = GColor.newColor(241, 245, 250);
	private static final GColor TABLE_GRID_COLOR = GColor.GRAY;
	private static final int TAB_INTERVAL_IDX = 0;
	private static final String[] DEFAULT_XY_HEADERS = { "x", "y(x)" };
	private static final String PREFIX = "[FUNC_ISPECTOR]";
	private static final int HEADER_PADDING = 44;

	private FlowPanel mainPanel;
	private TabPanel tabPanel;
	private FlowPanel intervalTab;
	private FlowPanel pointsTab;
	private MyToggleButtonW btnTable;
	private MyToggleButtonW btnXYSegments;
	private MyToggleButtonW btnTangent;
	private MyToggleButtonW btnOscCircle;

	private StandardButton btnHelp;
	PopupMenuButtonW btnOptions;
	// private MenuBar btnOptions;

	private Label lblGeoName;
	private Label lblStep;
	private Label lblInterval;
	AutoCompleteTextFieldW fldStep;
	AutoCompleteTextFieldW fldLow;
	AutoCompleteTextFieldW fldHigh;
	private InspectorTableW tableXY;
	private GridModel modelXY;
	private GridModel modelInterval;

	PopupMenuButtonW btnAddColumn;
	private MyCJButton btnRemoveColumn;

	private int pointCount = 9;

	/**
	 * @param app
	 *            application
	 * @param selectedGeo
	 *            function
	 */
	public FunctionInspectorW(AppW app, GeoFunction selectedGeo) {
		super(app, selectedGeo);
		Window.addResizeHandler(event -> onResize());
		Scheduler.get().scheduleDeferred(() -> onResize());
	}

	@Override
	public void createGUI() {
		super.createGUI();
		setInspectorVisible(true);
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean hasFocus() {
		return false;
	}

	@Override
	public void updateFonts() {
		// TODO Auto-generated method stub
	}

	private static void debug(String msg) {
		Log.debug(PREFIX + " " + msg);
	}

	@Override
	public void updateXYTable(boolean isTable) {
		// reset table model and update the XYtable
		tableXY.setCellEditable(-1, -1);

		if (isTable) {
			int row = pointCount / 2;
			modelXY.setRowCount(pointCount);
			Log.debug("[updateXYTable] pointCount: " + pointCount + " row: "
			        + row);
			tableXY.setCellEditable(row, 0);
			tableXY.setSelectedRow(row);
		} else {
			modelXY.setRowCount(1);
			tableXY.setSelectedRow(0);
			tableXY.setCellEditable(0, 0);
		}

		updateXYTable();
		Log.debug(modelXY.toString());
		updateTestPoint();
	}

	@Override
	public void updateInterval(ArrayList<String> property,
	        ArrayList<String> value) {
		debug("updateInterval");
		modelInterval.removeAll();
		modelInterval.setHeaders(getModel().getIntervalColumnNames());
		for (int i = 0; i < property.size(); i++) {
			modelInterval
			        .addAsRow(Arrays.asList(property.get(i), value.get(i)));
		}
		debug("updateInterval ended");
	}

	@Override
	public void setXYValueAt(Double value, int row, int col) {
		debug("[XY] setData");
		modelXY.setData(row, col, getModel().format(value));
		debug("setData ended");
	}

	@Override
	public Object getXYValueAt(int row, int col) {
		Log.debug("GETDATA row: " + row + " col: " + col);
		return modelXY.getData(row, col);
	}

	@Override
	public void addTableColumn(String name) {
		modelXY.addColumn(name);
		updateXYTable();
	}

	@Override
	public void setGeoName(String name) {
		lblGeoName.setText(name);
	}

	@Override
	public void changeTableSelection() {
		updateXYTable();
		updateTestPoint();
	}

	@Override
	public void updateHighAndLow(boolean isAscending, boolean isLowSelected) {
		if (isAscending) {
			if (isLowSelected) {
				doTextFieldActionPerformed(fldLow);
			} else {
				doTextFieldActionPerformed(fldHigh);
			}
		}

		updateIntervalFields();
	}

	@Override
	public void setStepText(String text) {
		fldStep.setText(text);
	}

	@Override
	public void setStepVisible(boolean isVisible) {
		lblStep.setVisible(isVisible);
		fldStep.setVisible(isVisible);
	}

	@Override
	public GColor getColor(Colors id) {
		GColor color;
		switch (id) {
		case EVEN_ROW:
			color = EVEN_ROW_COLOR;
			break;
		case GEO:
			color = DISPLAY_GEO_COLOR;
			break;
		case GEO2:
			color = DISPLAY_GEO2_COLOR;
			break;
		case GRID:
			color = TABLE_GRID_COLOR;
			break;
		default:
			color = GColor.BLACK;
			break;
		}
		return color;
	}

	@Override
	public int getSelectedXYRow() {
		int row = tableXY.getSelectedRow() - 1;
		return row;
	}

	@Override
	protected void buildTabPanel() {
		tabPanel = new TabPanel();
		tabPanel.add(intervalTab, "Interval");
		tabPanel.add(pointsTab, "Points");
		tabPanel.selectTab(TAB_INTERVAL_IDX);
		tabPanel.addSelectionHandler(event -> updateTabPanels());
		mainPanel.add(tabPanel);
	}

	@Override
	protected void buildHelpPanel() {
		btnHelp = new StandardButton(SharedResources.INSTANCE.icon_help_black(),
				null, 24);
		btnHelp.addStyleName("MyCanvasButton");
		btnHelp.addStyleName("fiButton");
		btnHelp.addFastClickHandler(
				source -> app.getGuiManager().openHelp("Function_Inspector_Tool"));
	}

	@Override
	protected void buildHeaderPanel() {
		FlowPanel header = new FlowPanel();
		header.add(lblGeoName);
		header.add(btnHelp);
		header.add(btnOptions);
		header.setStyleName("panelRow");
		buildHelpPanel();
		mainPanel.add(header);
	}

	@Override
	protected void createTabIntervalPanel() {
		intervalTab = new FlowPanel();
		InspectorTableW tableInterval = new InspectorTableW(app, 2);
		modelInterval = tableInterval.getModel();
		intervalTab.add(tableInterval);
		FlowPanel toolBar = new FlowPanel();
		toolBar.setStyleName("panelRow");
		toolBar.add(fldLow);
		toolBar.add(lblInterval);
		toolBar.add(fldHigh);
		intervalTab.add(toolBar);
		intervalTab.setStyleName("propertiesTab");
	}

	@Override
	protected void createTabPointPanel() {
		debug("createTabPointPanel()");
		pointsTab = new FlowPanel();
		pointsTab.setStyleName("propertiesTab");

		FlowPanel header = new FlowPanel();
		header.setStyleName("panelRow");

		createStep();
		createBtnAddColumn();
		createBtnRemoveColumn();

		header.add(lblStep);
		header.add(fldStep);
		header.add(btnAddColumn);
		header.add(btnRemoveColumn);

		pointsTab.add(header);

		createXYtable();
		pointsTab.add(tableXY);

		FlowPanel btnPanel = createBtnPanel();
		pointsTab.add(btnPanel);

		debug("createTabPointPanel() ENDED");
	}

	/**
	 * @return button panel
	 */
	private FlowPanel createBtnPanel() {
		FlowPanel btnPanel = new FlowPanel();
		btnPanel.setStyleName("panelRowIndent");
		btnTable = new MyToggleButtonW(new Image(AppResources.INSTANCE.table()
		        .getSafeUri().asString()));
		btnXYSegments = new MyToggleButtonW(new Image(AppResources.INSTANCE
		        .xy_segments().getSafeUri().asString()));
		btnTangent = new MyToggleButtonW(new Image(AppResources.INSTANCE
		        .tangent_line().getSafeUri().asString()));
		btnOscCircle = new MyToggleButtonW(new Image(AppResources.INSTANCE
		        .osculating_circle().getSafeUri().asString()));

		btnPanel.add(btnTable);
		btnPanel.add(btnXYSegments);
		btnPanel.add(btnTangent);
		btnPanel.add(btnOscCircle);

		ClickHandler btnClick = event -> updateGUI();

		btnTable.addClickHandler(btnClick);
		btnXYSegments.addClickHandler(btnClick);
		btnTangent.addClickHandler(btnClick);
		btnOscCircle.addClickHandler(btnClick);
		btnXYSegments.setDown(true);
		return btnPanel;
	}

	private void createBtnRemoveColumn() {
		btnRemoveColumn = new MyCJButton();
		btnRemoveColumn.addClickHandler(event -> removeColumn());
	}

	private void createXYtable() {
		tableXY = new InspectorTableW(app, 2);
		modelXY = tableXY.getModel();
		modelXY.setHeaders(DEFAULT_XY_HEADERS);

		tableXY.setKeyHandler(e -> {
			if (e.isEnterKey()) {
				changeXYStart();
			}
		});

		tableXY.setBlurHandler(event -> changeXYStart());
	}

    private void createStep() {
	    lblStep = new Label();
		InputPanelW stepPanel = new InputPanelW(app, -1, false);
		fldStep = stepPanel.getTextComponent();

		fldStep.addKeyHandler(e -> {
			if (e.isEnterKey()) {
				doTextFieldActionPerformed(fldStep);
			}
		});

		fldStep.addBlurHandler(event -> doTextFieldActionPerformed(fldStep));

		fldStep.setWidthInEm(6);
    }

	void changeXYStart() {
		Double value = tableXY.getDoubleEdited();
		Log.debug("[TESTPOINT] edited value is: " + value);
		if (value != null) {
			changeStart(value);
		}
	}

	private void createBtnAddColumn() {
		btnAddColumn = new PopupMenuButtonW((AppW) app,
		        ImageOrText.convert(getModel().getColumnNames()), -1, 1,
				org.geogebra.common.gui.util.SelectionTable.MODE_TEXT) {
			@Override
			public void handlePopupActionEvent() {
				super.handlePopupActionEvent();
				getModel().addColumn(getSelectedIndex());
				btnAddColumn.setSelectedIndex(-1);
			}
		};
		btnAddColumn.setKeepVisible(false);
		btnAddColumn.setText("\u271A");
		btnAddColumn.setSelectedIndex(-1);
	}

	@Override
	protected void createGUIElements() {

		mainPanel = new FlowPanel();
		mainPanel.addStyleName("functionInspectorMainPanel");
		lblGeoName = new Label(getModel().getTitleString());

		lblInterval = new Label();
		InputPanelW lowPanel = new InputPanelW(app, -1, false);
		fldLow = lowPanel.getTextComponent();
		fldLow.setWidthInEm(6);

		fldLow.addKeyHandler(e -> {
			if (e.isEnterKey()) {
				doTextFieldActionPerformed(fldLow);
			}
		});

		fldLow.addBlurHandler(event -> doTextFieldActionPerformed(fldLow));

		InputPanelW highPanel = new InputPanelW(app, -1, false);
		fldHigh = highPanel.getTextComponent();
		fldHigh.setWidthInEm(6);

		fldHigh.addKeyHandler(e -> {
			if (e.isEnterKey()) {
				doTextFieldActionPerformed(fldHigh);
			}
		});

		fldHigh.addBlurHandler(event -> doTextFieldActionPerformed(fldHigh));
	}

	void doTextFieldActionPerformed(AutoCompleteTextFieldW source) {
		try {

			String inputText = source.getText().trim();

			// allow input such as sqrt(2)
			NumberValue nv;
			nv = getKernel().getAlgebraProcessor().evaluateToNumeric(inputText,
			        false);
			double value = nv.getDouble();

			if (source == fldStep) {
				getModel().applyStep(value);
				updateXYTable();
			} else if (source == fldLow) {

				getModel().applyLow(value);

				updateIntervalTable();
			} else if (source == fldHigh) {

				getModel().applyHigh(value);

				updateIntervalTable();
			}

		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void updatePointsTab() {
		Log.debug("UPDATE POINTS TAB");
		getModel().updatePoints(btnTangent.isSelected(),
		        btnOscCircle.isSelected(), btnXYSegments.isSelected(),
		        btnTable.isSelected());
	}

	@Override
	protected boolean isIntervalTabSelected() {
		return tabPanel.getTabBar().getSelectedTab() == TAB_INTERVAL_IDX;
	}

	@Override
	protected void updateIntervalFields() {

		if (isIntervalTabSelected()) {

			double[] coords = new double[3];
			getModel().getLowPoint().getCoords(coords);
			fldLow.setText(coords[0] + "");
			getModel().getHighPoint().getCoords(coords);
			fldHigh.setText(coords[0] + "");
			getModel().updateIntervalTable();
		}
	}

	@Override
	protected void updateXYTable() {
		getModel().updateXYTable(modelXY.getRowCount(), btnTable.isSelected());
	}

	@Override
	protected void removeColumn() {
		if (modelXY.getColumnCount() == 2) {
			return;
		}

		Log.debug("Removing column");
		getModel().removeColumn();
		modelXY.removeColumn();

		updateXYTable();
	}

	@Override
	protected void changeStart(double x) {
		setStart(x);
	}

	@Override
	protected void createOptionsButton() {
		ImageOrText[] strOptions = new ImageOrText[] { new ImageOrText(
				app.getLocalization().getMenu("CopyToSpreadsheet")) };
		btnOptions = new PopupMenuButtonW((AppW) app, strOptions,
				strOptions.length, 1,
				org.geogebra.common.gui.util.SelectionTable.MODE_TEXT);

		ImageOrText icon = new ImageOrText(
				GuiResources.INSTANCE.menu_icon_tools());
		btnOptions.setFixedIcon(icon);
		btnOptions.setSelectedIndex(-1);
		btnOptions.addPopupHandler(actionButton -> {
			doCopyToSpreadsheet();
			btnOptions.setSelectedIndex(-1);
		});
	}

	@Override
	protected void doCopyToSpreadsheet() {
		if (isIntervalTabSelected()) {
			getModel().copyIntervalsToSpreadsheet(2, 9); // modelInterval.getColumnCount(),
		} else {
			getModel().copyPointsToSpreadsheet(modelXY.getColumnCount(),
			        modelXY.getRowCount());
		}
	}

	public Widget getWrappedPanel() {
		return mainPanel;
	}

	@Override
	public boolean suggestRepaint() {
		return false;
	}

	@Override
	public void setLabels() {
		debug("setLabels");
		Localization loc = app.getLocalization();
		lblStep.setText(loc.getMenu("Step") + ":");
		lblInterval.setText(" \u2264 x \u2264 "); // <= x <=

		TabBar tabBar = tabPanel.getTabBar();
		tabBar.setTabText(0, loc.getMenu("fncInspector.Interval"));
		tabBar.setTabText(1, loc.getMenu("fncInspector.Points"));

		lblGeoName.setText(getModel().getTitleString());
		//
		// // tool tips
		btnHelp.setTitle(loc.getMenu("ShowOnlineHelp"));
		btnOscCircle.setToolTipText(loc
		        .getPlainTooltip("fncInspector.showOscCircle"));
		btnXYSegments.setToolTipText(loc
		        .getPlainTooltip("fncInspector.showXYLines"));
		btnTable.setToolTipText(loc.getPlainTooltip("fncInspector.showTable"));
		btnTangent.setToolTipText(loc
		        .getPlainTooltip("fncInspector.showTangent"));
		btnAddColumn.setToolTipText(loc
		        .getPlainTooltip("fncInspector.addColumn"));
		btnRemoveColumn.setTitle(loc
		        .getPlainTooltip("fncInspector.removeColumn"));
		// // add/remove extra column buttons
		btnRemoveColumn.setText("\u2718");
		btnAddColumn.setText("\u271A");

		btnOptions.getMyTable().updateText(
				new ImageOrText[] { new ImageOrText(
						app.getLocalization()
		                .getMenu("CopyToSpreadsheet")) });
		btnAddColumn.getMyTable().updateText(
		        ImageOrText.convert(getModel().getColumnNames()));

		modelInterval.setHeaders(getModel().getIntervalColumnNames());
	}

	void onResize() {
		if (mainPanel.getOffsetHeight() != 0) {
			tabPanel.setHeight(mainPanel.getOffsetHeight()
			        - HEADER_PADDING + "px");
			intervalTab.setWidth(mainPanel.getOffsetWidth() - PADDING_RIGHT
			        + "px");
			pointsTab.setWidth(mainPanel.getOffsetWidth()
			        - PADDING_RIGHT + "px");
		}
	}
}
