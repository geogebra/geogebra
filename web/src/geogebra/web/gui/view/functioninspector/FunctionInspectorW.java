package geogebra.web.gui.view.functioninspector;

import geogebra.common.awt.GColor;
import geogebra.common.euclidian.event.KeyEvent;
import geogebra.common.euclidian.event.KeyHandler;
import geogebra.common.gui.view.functioninspector.FunctionInspector;
import geogebra.common.gui.view.functioninspector.FunctionInspectorModel.Colors;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.main.App;
import geogebra.common.main.GeoElementSelectionListener;
import geogebra.common.main.Localization;
import geogebra.html5.awt.GColorW;
import geogebra.html5.awt.GDimensionW;
import geogebra.html5.gui.inputfield.AutoCompleteTextFieldW;
import geogebra.web.gui.GuiManagerW;
import geogebra.web.gui.images.AppResources;
import geogebra.web.gui.util.ImageOrText;
import geogebra.web.gui.util.MyToggleButton2;
import geogebra.web.gui.util.PopupMenuButton;
import geogebra.web.gui.view.algebra.InputPanelW;
import geogebra.web.gui.view.functioninspector.GridModel.DataCell;
import geogebra.web.gui.view.spreadsheet.SpreadsheetViewW;
import geogebra.web.main.AppW;

import java.util.ArrayList;
import java.util.Arrays;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.TabBar;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;

public class FunctionInspectorW extends FunctionInspector {

	// color constants
	private static final GColor DISPLAY_GEO_COLOR = GColorW.RED;
	private static final GColor DISPLAY_GEO2_COLOR = GColorW.RED;
	private static final GColor EVEN_ROW_COLOR = new GColorW(241, 245, 250);
	private static final GColor TABLE_GRID_COLOR = GColorW.gray;
	private static final int TAB_INTERVAL_IDX = 0;
	private static final String[] DEFAULT_XY_HEADERS = {"x", "y(x)"};
	private static final String PREFIX = "[FUNC_ISPECTOR]";  
	
	private FlowPanel mainPanel;
	private TabPanel tabPanel;
	private FlowPanel intervalTab;
	private FlowPanel pointsTab;
	private MyToggleButton2 btnTable;
    private MyToggleButton2 btnXYSegments;
	private MyToggleButton2 btnTangent;
	private MyToggleButton2 btnOscCircle;

	private MyToggleButton2 btnHelp;
//    private PopupMenuButton btnOptions;
    private MenuBar btnOptions;
    
    private Label lblGeoName, lblStep, lblInterval;
	private AutoCompleteTextFieldW fldStep, fldLow, fldHigh;
	private InspectorTableW tableXY, tableInterval;
	private GridModel modelXY, modelInterval;

    private PopupMenuButton btnAddColumn;
    private MyToggleButton2 btnRemoveColumn;

	private boolean isChangingValue;
	private int pointCount = 9;

	private GeoElementSelectionListener sl;
	
	private class RoundingCommand implements Command {
		private int index;
		public RoundingCommand(int idx) {
			index = idx;
		}
		public void execute() {
	        getModel().applyDecimalPlaces(index);
        }
		
	}
    public FunctionInspectorW(AppW app, GeoFunction selectedGeo) {
	    super(app, selectedGeo);
    }

	@Override
	public void createGUI() {
		super.createGUI();
		setInspectorVisible(true);
	}
    public void reset() {
		// TODO Auto-generated method stub

	}

	public boolean hasFocus() {
		// TODO Auto-generated method stub
		return false;
	}

	public void updateFonts() {
		// TODO Auto-generated method stub

	}

	private void debug(String msg) {
		App.debug(PREFIX + " " + msg);
		
	}
	public void setLabels() {
		debug("setLabels");
		Localization loc = getAppW().getLocalization();
//		wrappedDialog.setTitle(loc.getMenu("FunctionInspector"));
		lblStep.setText(loc.getMenu("Step") + ":");
		lblInterval.setText(" \u2264 x \u2264 "); // <= x <=
//
//		// header text
		//
		TabBar tabBar = tabPanel.getTabBar();
		tabBar.setTabText(0, loc.getPlain("fncInspector.Interval"));
		tabBar.setTabText(1, loc.getPlain("fncInspector.Points"));

		lblGeoName.setText(getModel().getTitleString());
//
//		// tool tips
		btnHelp.setToolTipText(loc.getPlain("ShowOnlineHelp"));
		btnOscCircle.setToolTipText(loc
				.getPlainTooltip("fncInspector.showOscCircle"));
		btnXYSegments.setToolTipText(loc
				.getPlainTooltip("fncInspector.showXYLines"));
		btnTable.setToolTipText(loc.getPlainTooltip("fncInspector.showTable"));
		btnTangent.setToolTipText(loc
				.getPlainTooltip("fncInspector.showTangent"));
		btnAddColumn.setToolTipText(loc
				.getPlainTooltip("fncInspector.addColumn"));
		btnRemoveColumn.setToolTipText(loc
				.getPlainTooltip("fncInspector.removeColumn"));
//		fldStep.setToolTipText(loc.getPlainTooltip("fncInspector.step"));
//		lblStep.setToolTipText(loc.getPlainTooltip("fncInspector.step"));
//
//		// add/remove extra column buttons
		btnRemoveColumn.setText("\u2718");
		btnAddColumn.setText("\u271A");

		FlowPanel p = (FlowPanel) btnAddColumn.getParent();
		p.clear();
		p.add(lblStep);
		p.add(fldStep);
		createBtnAddColumn();
		p.add(btnAddColumn);
		p.add(btnRemoveColumn);

	    modelInterval.setHeaders(getModel().getIntervalColumnNames());
		debug("setLabels ended");
		
	}

	public void updateXYTable(boolean isTable) {
		// reset table model and update the XYtable
		tableXY.setCellEditable(-1, -1);

		if (isTable) {
			int row = (pointCount) / 2;
			modelXY.setRowCount(pointCount);
			App.debug("[updateXYTable] pointCount: " + pointCount +
					" row: " + row);
			tableXY.setCellEditable(row, 0);
			tableXY.setSelectedRow(row);
			} else {

			modelXY.setRowCount(1);
			tableXY.setSelectedRow(0);
			tableXY.setCellEditable(0, 0);
			
					//tableXY.changeSelection(0, 0, false, false);
			// tableXY.setRowSelectionAllowed(false);
		}

		updateXYTable();
		App.debug(modelXY.toString());
		updateTestPoint();
	}

	public void updateInterval(ArrayList<String> property,
	        ArrayList<String> value) {
		debug("updateInterval");
		modelInterval.removeAll();
		modelInterval.setHeaders(getModel().getIntervalColumnNames());
		for (int i = 0; i < property.size(); i++) {
			modelInterval.addAsRow(Arrays.asList(property.get(i), value.get(i)));

		}
		debug("updateInterval ended");
	}
	

	public void setXYValueAt(Double value, int row, int col) {
		debug("[XY] setData");
		modelXY.setData(row, col, getModel().format(value));
		debug("setData ended");
	}

	public Object getXYValueAt(int row, int col) {
		App.debug("GETDATA row: " + row + " col: " + col);
		DataCell value = modelXY.getData(row, col );
		
		return value != null ? value.toString() : "";
	}

	public void addTableColumn(String name) {
		modelXY.addColumn(name);
		updateXYTable();
	}

	public void setGeoName(String name) {
		lblGeoName.setText(name);
	}

	public void changeTableSelection() {
		updateXYTable();
		updateTestPoint();
	}

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

	public void setStepText(String text) {
		fldStep.setText(text);
	}

	public void setStepVisible(boolean isVisible) {
		lblStep.setVisible(isVisible);
		fldStep.setVisible(isVisible);

	}

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
			color = GColorW.black;
			break;

		}
		return color;
	}

	public int getSelectedXYRow() {
		int row =  tableXY.getSelectedRow() - 1;
		return row;
	}

	@Override
	protected void buildTabPanel() {
		tabPanel = new TabPanel(); 
		tabPanel.add(intervalTab, "Interval");
		tabPanel.add(pointsTab, "Points");
		tabPanel.selectTab(TAB_INTERVAL_IDX);
		tabPanel.addSelectionHandler(new SelectionHandler<Integer>() {
			
			public void onSelection(SelectionEvent<Integer> event) {
				updateTabPanels();
			}
		});
		mainPanel.add(tabPanel);
		setLabels();
	}

	@Override
	protected void buildHelpPanel() {
	    btnHelp = new MyToggleButton2(AppResources.INSTANCE.help());
	    btnHelp.addClickHandler(new ClickHandler() {
			
			public void onClick(ClickEvent event) {
			    getAppW().getGuiManager().openHelp("Function_Inspector_Tool");	
			}
		});
	}

	@Override
	protected void buildHeaderPanel() {
		FlowPanel header = new FlowPanel();
		FlowPanel buttons = new FlowPanel();
		header.add(lblGeoName);
		buttons.add(btnHelp);
		buttons.add(btnOptions);
		header.add(buttons);
//		buttons.setStyleName("functionInspectorToolButtons");
		buttons.setStyleName("panelRow");
		header.setStyleName("panelRow");
		buildHelpPanel();
		createOptionsButton();
		mainPanel.add(header);
		
	}

	@Override
	protected void createTabIntervalPanel() {
		intervalTab = new FlowPanel();
		
		tableInterval = new InspectorTableW(getAppW(), 2);
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
		
		lblStep = new Label();
		InputPanelW stepPanel = new InputPanelW(null, getAppW(), -1, false);
		fldStep = stepPanel.getTextComponent();

		fldStep.addKeyHandler(new KeyHandler(){

			public void keyReleased(KeyEvent e) {
				if (e.isEnterKey()) {
					doTextFieldActionPerformed(fldStep);	    
				}
			}

		});
		
		fldStep.addBlurHandler(new BlurHandler() {
			
			public void onBlur(BlurEvent event) {
				doTextFieldActionPerformed(fldStep);	    
			}
		});
		
		fldStep.setColumns(6);
		
		header.add(lblStep);
		header.add(fldStep);
		createBtnAddColumn();
		header.add(btnAddColumn);
		btnRemoveColumn = new MyToggleButton2(AppResources.INSTANCE.empty());
		header.setStyleName("panelRow");
		header.add(btnRemoveColumn);
		
		pointsTab.add(header);
	
		tableXY = new InspectorTableW(getAppW(), 2);
		modelXY = tableXY.getModel();
		modelXY.setHeaders(DEFAULT_XY_HEADERS);
//		modelXY.setRowCount(pointCount);
		pointsTab.add(tableXY);
		
		tableXY.addKeyHandler(new KeyHandler() {
			
			public void keyReleased(KeyEvent e) {
				if (e.isEnterKey()) {
					changeXYStart();
				}
					
			}
		});
		
		tableXY.addBlurHandler(new BlurHandler() {
			
			public void onBlur(BlurEvent event) {
				changeXYStart();
			}
		});
		FlowPanel btnPanel = new FlowPanel();
		btnPanel.setStyleName("panelRowIndent");
		btnTable = new MyToggleButton2(new Image(AppResources.INSTANCE.table().getSafeUri()));
		btnXYSegments = new MyToggleButton2(new Image(AppResources.INSTANCE.xy_segments().getSafeUri()));
		btnTangent = new MyToggleButton2(new Image(AppResources.INSTANCE.tangent_line().getSafeUri()));
		btnOscCircle = new MyToggleButton2(new Image(AppResources.INSTANCE.osculating_circle().getSafeUri()));
		
		btnPanel.add(btnTable);
		btnPanel.add(btnXYSegments);
		btnPanel.add(btnTangent);
		btnPanel.add(btnOscCircle);
		
		ClickHandler btnClick = new ClickHandler() {
			
			public void onClick(ClickEvent event) {
				updateGUI();
			}
		};
	
		btnTable.addClickHandler(btnClick);
		btnXYSegments.addClickHandler(btnClick);
		btnTangent.addClickHandler(btnClick);
		btnOscCircle.addClickHandler(btnClick);
		btnXYSegments.setSelected(true);
		
		btnRemoveColumn.addClickHandler(new ClickHandler() {
			
			public void onClick(ClickEvent event) {
				removeColumn();
			}
		});
		
		pointsTab.add(btnPanel);
		debug("createTabPointPanel() ENDED");
	}

	private void changeXYStart() {
		Double value = tableXY.getDoubleEdited();
		App.debug("[TESTPOINT] edited value is: " + value); 
		if (value != null) {
			changeStart(value);
		}
	}

	private void createBtnAddColumn() {


		btnAddColumn = new PopupMenuButton(getAppW(),
				ImageOrText.convert(getModel().getColumnNames()), 
				-1, 1, new GDimensionW(0, 18),
				geogebra.common.gui.util.SelectionTable.MODE_TEXT){
			@Override
			public void handlePopupActionEvent(){
				super.handlePopupActionEvent();
				getModel().addColumn(getSelectedIndex());
			}
		};
		btnAddColumn.setKeepVisible(false);
		btnAddColumn.setStandardButton(true);
		btnAddColumn.setText("\u271A");
		ImageResource[] res = {AppResources.INSTANCE.empty()};
	//	btnAddColumn.setFixedIcon(ImageOrText.convert(res)[0]);
	}
	@Override
	protected void createGUIElements() {


//		tableInterval.getSelectionModel().addListSelectionListener(
//				new ListSelectionListener() {
//					public void valueChanged(ListSelectionEvent e) {
//						getModel().updateIntervalGeoVisiblity();
//					}
//				});

		mainPanel = new FlowPanel();
		
		lblGeoName = new Label(getModel().getTitleString());

		
		lblInterval = new Label();
		InputPanelW lowPanel = new InputPanelW(null, getAppW(), -1, false);
		fldLow = lowPanel.getTextComponent();
		fldLow.setColumns(6);
		
		fldLow.addKeyHandler(new KeyHandler(){

			public void keyReleased(KeyEvent e) {
				if (e.isEnterKey()) {
					doTextFieldActionPerformed(fldLow);	    
				}
			}

		});
		
		fldLow.addBlurHandler(new BlurHandler() {
			
			public void onBlur(BlurEvent event) {
				doTextFieldActionPerformed(fldLow);	    
			}
		});
	
		
		InputPanelW highPanel = new InputPanelW(null, getAppW(), -1, false);
		fldHigh = highPanel.getTextComponent();
		fldHigh.setColumns(6);
		
		fldHigh.addKeyHandler(new KeyHandler(){

			public void keyReleased(KeyEvent e) {
				if (e.isEnterKey()) {
					doTextFieldActionPerformed(fldHigh);	    
				}
			}

		});
		
		fldHigh.addBlurHandler(new BlurHandler() {
			
			public void onBlur(BlurEvent event) {
				doTextFieldActionPerformed(fldHigh);	    
			}
		});
		
//		btnRemoveColumn = new JButton();
//		btnRemoveColumn.addActionListener(this);
//		createBtnAddColumn();
//	
	}

	private void doTextFieldActionPerformed(AutoCompleteTextFieldW source) {
		try {

			String inputText = source.getText().trim();
			if (inputText == null)
				return;

			// allow input such as sqrt(2)
			NumberValue nv;
			nv = getKernel().getAlgebraProcessor().evaluateToNumeric(inputText,
					false);
			double value = nv.getDouble();

			if (source == fldStep) {
				getModel().applyStep(value);
				updateXYTable();
			} else if (source == fldLow) {
				isChangingValue = true;

				getModel().applyLow(value);

				isChangingValue = false;
				updateIntervalTable();
			} else if (source == fldHigh) {
				isChangingValue = true;

				getModel().applyHigh(value);

				isChangingValue = false;
				updateIntervalTable();
			}

		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

	}

	private AppW getAppW() {
	    return (AppW)getApp();
    }

	@Override
	protected void updatePointsTab() {
		App.debug("UPDATE POINTS TAB");
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
			fldLow.setText(coords[0] +"");
				getModel().getHighPoint().getCoords(coords);
			fldHigh.setText(coords[0]+"");
			getModel().updateIntervalTable();
		}
	}

	@Override
	protected void updateXYTable() {
		isChangingValue = true;
		getModel().updateXYTable(modelXY.getRowCount(), btnTable.isSelected());
		isChangingValue = false;
	}

	@Override
	protected void removeColumn() {
		if (modelXY.getColumnCount()  == 2) {
			return;
		}

		App.debug("Removing column");
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
		AppW appW = getAppW();

		Localization loc = appW.getLocalization();
		btnOptions = new MenuBar();
		MenuBar options = new MenuBar(true);
//		MenuBar rounding = new RoundingMenu(appW, new IRoundingMenuListener()  {
//			
//			public void onChange(int index) {
//				getModel().applyDecimalPlaces(index);
//			}
//		});
//		
//		options.addItem("Roundings", rounding);
//		// copy to spreadsheet
		MenuItem mi = new MenuItem(loc.getMenu("CopyToSpreadsheet"), 
				new Command() {
					
					public void execute() {
						doCopyToSpreadsheet();
					}
				});

		options.addItem(mi);
		
		String image = "<img src=\""+ AppResources.INSTANCE.tool().getSafeUri().asString() 
				+ "\" >";
		btnOptions.addItem(image, true, options);
				
	}

	@Override
	protected void doCopyToSpreadsheet() {
		SpreadsheetViewW sp = ((GuiManagerW) getAppW().getGuiManager())
				.getSpreadsheetView();

		if (sp == null) {
			return;
		}

		if (isIntervalTabSelected()) {
			getModel()
			.copyIntervalsToSpreadsheet(2, 9);//modelInterval.getColumnCount(),
			//		modelInterval.getRowCount());
		} else {
			getModel().copyPointsToSpreadsheet(modelXY.getColumnCount(),
					modelXY.getRowCount());
		}
	}
	
	public Widget getWrappedPanel() {
		return mainPanel;
	}
	
	public void suggestRepaint(){
		// not used for this view
	}

	public void attachView() {
		getKernel().attach(this);
	}

	public void detachView() {
		getKernel().detach(this);
	}
	
}
