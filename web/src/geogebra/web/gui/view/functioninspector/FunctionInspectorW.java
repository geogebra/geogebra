package geogebra.web.gui.view.functioninspector;

import geogebra.common.awt.GColor;
import geogebra.common.gui.view.functioninspector.FunctionInspector;
import geogebra.common.gui.view.functioninspector.FunctionInspectorModel.Colors;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.html5.awt.GColorW;
import geogebra.web.gui.images.AppResources;
import geogebra.web.main.AppW;

import java.util.ArrayList;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

public class FunctionInspectorW extends FunctionInspector {

	// color constants
	private static final GColor DISPLAY_GEO_COLOR = GColorW.RED;
	private static final GColor DISPLAY_GEO2_COLOR = GColorW.RED;
	private static final GColor EVEN_ROW_COLOR = new GColorW(241, 245, 250);
	private static final GColor TABLE_GRID_COLOR = GColorW.gray;  
	
	private TabPanel tabPanel;
	private FlowPanel intervalTab;
	private FlowPanel pointsTab;
	private ToggleButton btnTable;
    private ToggleButton btnXYSegments;
	private ToggleButton btnTangent;
    private ToggleButton btnOscCircle;
	
    public FunctionInspectorW(AppW app, GeoFunction selectedGeo) {
	    super(app, selectedGeo);
	    // TODO Auto-generated constructor stub
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
		// TODO Auto-generated method stub

	}

	public void updateXYTable(boolean isTable) {
		// TODO Auto-generated method stub

	}

	public String format(double value) {
		// TODO Auto-generated method stub
		return null;
	}

	public void updateInterval(ArrayList<String> property,
	        ArrayList<String> value) {
		// TODO Auto-generated method stub

	}

	public void setXYValueAt(Double value, int row, int col) {
		// TODO Auto-generated method stub

	}

	public Object getXYValueAt(int row, int col) {
		// TODO Auto-generated method stub
		return "8";
	}

	public void addTableColumn(String name) {
		// TODO Auto-generated method stub

	}

	public void setGeoName(String name) {
		// TODO Auto-generated method stub

	}

	public void changeTableSelection() {
		// TODO Auto-generated method stub

	}

	public void updateHighAndLow(boolean isAscending, boolean isLowSelected) {
		// TODO Auto-generated method stub

	}

	public void setStepText(String text) {
		// TODO Auto-generated method stub

	}

	public void setStepVisible(boolean isVisible) {
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void buildTabPanel() {
		tabPanel = new TabPanel(); 
		tabPanel.add(intervalTab, "Interval");
		tabPanel.add(pointsTab, "Points");
	}

	@Override
	protected void buildHelpPanel() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void buildHeaderPanel() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void createTabIntervalPanel() {
		intervalTab = new FlowPanel();
	}

	@Override
	protected void createTabPointPanel() {
		pointsTab = new FlowPanel();
		FlowPanel btnPanel = new FlowPanel();
		btnTable = new ToggleButton(new Image(AppResources.INSTANCE.table().getSafeUri()));
		btnXYSegments = new ToggleButton(new Image(AppResources.INSTANCE.xy_segments().getSafeUri()));
		btnTangent = new ToggleButton(new Image(AppResources.INSTANCE.tangent_line().getSafeUri()));
		btnOscCircle = new ToggleButton(new Image(AppResources.INSTANCE.osculating_circle().getSafeUri()));
		
		btnPanel.add(btnTable);
		btnPanel.add(btnXYSegments);
		btnPanel.add(btnTangent);
		btnPanel.add(btnOscCircle);
		
	
		pointsTab.add(btnPanel);
		
	}

	@Override
	protected void createGUIElements() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void updatePointsTab() {
		// TODO Auto-generated method stub

	}

	@Override
	protected boolean isIntervalTabSelected() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void updateIntervalFields() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void updateXYTable() {
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub

	}

	@Override
	protected void doCopyToSpreadsheet() {
		// TODO Auto-generated method stub

	}
	
	public Widget getWrappedPanel() {
		return tabPanel;
	}

}
