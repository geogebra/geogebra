package geogebra.web.gui.view.functioninspector;

import geogebra.common.awt.GColor;
import geogebra.common.euclidian.event.KeyEvent;
import geogebra.common.euclidian.event.KeyHandler;
import geogebra.common.gui.util.SelectionTable;
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
import geogebra.web.gui.images.AppResources;
import geogebra.web.gui.util.ImageOrText;
import geogebra.web.gui.util.MyToggleButton2;
import geogebra.web.gui.util.PopupMenuButton;
import geogebra.web.gui.view.algebra.InputPanelW;
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
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
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
	
	private FlowPanel mainPanel;
	private TabPanel tabPanel;
	private FlowPanel intervalTab;
	private FlowPanel pointsTab;
	private MyToggleButton2 btnTable;
    private MyToggleButton2 btnXYSegments;
	private MyToggleButton2 btnTangent;
	private MyToggleButton2 btnOscCircle;

	private MyToggleButton2 btnHelp;
    private PopupMenuButton btnOptions;
    
    private Label lblGeoName, lblStep, lblInterval;
	private AutoCompleteTextFieldW fldStep, fldLow, fldHigh;
	private InspectorTableW tableXY, tableInterval;
	private GridModel modelXY, modelInterval;
//	private Button btnRemoveColumn, btnHelp;
	//private PopupMenuButton btnAddColumn, btnOptions;
//	private JPanel intervalTabPanel, pointTabPanel, headerPanel, helpPanel;

	private boolean isChangingValue;
	private int pointCount = 9;

	private GeoElementSelectionListener sl;
    public FunctionInspectorW(AppW app, GeoFunction selectedGeo) {
	    super(app, selectedGeo);
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

	public void setLabels() {
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
//		btnAddColumn.setToolTipText(loc
//				.getPlainTooltip("fncInspector.addColumn"));
//		btnRemoveColumn.setToolTipText(loc
//				.getPlainTooltip("fncInspector.removeColumn"));
//		fldStep.setToolTipText(loc.getPlainTooltip("fncInspector.step"));
//		lblStep.setToolTipText(loc.getPlainTooltip("fncInspector.step"));
//
//		// add/remove extra column buttons
//		btnRemoveColumn.setText("\u2718");
//		// btnAddColumn.setText("\u271A");
//
//		Container c = btnAddColumn.getParent();
//		c.removeAll();
//		createBtnAddColumn();
//		c.add(btnAddColumn);
//		c.add(btnRemoveColumn);
//
	    modelInterval.setHeaders(getModel().getIntervalColumnNames());

	}

	public void updateXYTable(boolean isTable) {
		// reset table model and update the XYtable
		// tableXY.setCellEditable(-1, -1);

		if (isTable) {
			modelXY.setRowCount(pointCount);
			//tableXY.setCellEditable((pointCount - 1) / 2, 0);
			// tableXY.setRowSelectionAllowed(true);
			//tableXY.changeSelection((pointCount - 1) / 2, 0, false, false);

		} else {

			modelXY.setRowCount(1);
			//tableXY.setCellEditable(0, 0);
			//tableXY.changeSelection(0, 0, false, false);
			// tableXY.setRowSelectionAllowed(false);
		}

		updateXYTable();
		updateTestPoint();
	}

	public String format(double value) {
		return ""+value;
	}

	public void updateInterval(ArrayList<String> property,
	        ArrayList<String> value) {
		modelInterval.removeAll();
		modelInterval.setHeaders(getModel().getIntervalColumnNames());
		for (int i = 0; i < property.size(); i++) {
			modelInterval.addRow(Arrays.asList(property.get(i), value.get(i)));

		}
	}
	

	public void setXYValueAt(Double value, int row, int col) {
		modelXY.setData(col, row, value.toString());
	}

	public Object getXYValueAt(int row, int col) {
		String value = modelXY.getData(col, row + 1);
		try {
			double x = Double.parseDouble(value);
		} catch (NullPointerException e) {
			value = "0.0";
		}
		catch (NumberFormatException e) {
			value = "0.0";
		}
		App.debug("GRIDMODEL " + value);
		return value;
	}

	public void addTableColumn(String name) {
		// TODO Auto-generated method stub

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
		return tableXY.getSelectedRow();
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
		buttons.setStyleName("functionInspectorToolButtons");
		header.setStyleName("panelRow");
		buildHelpPanel();
		createOptionsButton();
		mainPanel.add(header);
		
	}

	@Override
	protected void createTabIntervalPanel() {
		intervalTab = new FlowPanel();
		
		tableInterval = new InspectorTableW(2);
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
		pointsTab = new FlowPanel();
		pointsTab.setStyleName("propertiesTab");
		FlowPanel header = new FlowPanel();
		header.setStyleName("panelRow");
		pointsTab.add(header);
	
		tableXY = new InspectorTableW(2);
		modelXY = tableXY.getModel();
		modelXY.setHeaders(DEFAULT_XY_HEADERS);
		modelXY.setRowCount(pointCount);
		pointsTab.add(tableXY);
		
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
		
		pointsTab.add(btnPanel);
		
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
		// TODO Auto-generated method stub

	}

	@Override
	protected void changeStart(double x) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void createOptionsButton() {
		AppW appW = getAppW();
		GDimensionW dim = new GDimensionW(-1, -1);
		ImageOrText[] data = ImageOrText.convert(appW.getLocalization().getRoundingMenu());
		
		btnOptions = new PopupMenuButton(appW, data,-1, 1, dim, SelectionTable.MODE_TEXT){
			@Override
			public void handlePopupActionEvent(){
				super.handlePopupActionEvent();
				getModel().applyDecimalPlaces(getSelectedIndex());
			}
		};
		
		ImageResource[] res = {AppResources.INSTANCE.tool()};
	
		btnOptions.setFixedIcon(ImageOrText.convert(res)[0]);
		
	}

	@Override
	protected void doCopyToSpreadsheet() {
		// TODO Auto-generated method stub

	}
	
	public Widget getWrappedPanel() {
		return mainPanel;
	}

}
