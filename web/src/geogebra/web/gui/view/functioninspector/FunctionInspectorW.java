package geogebra.web.gui.view.functioninspector;

import geogebra.common.awt.GColor;
import geogebra.common.gui.view.functioninspector.FunctionInspector;
import geogebra.common.gui.view.functioninspector.FunctionInspectorModel.Colors;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.web.main.AppW;

import java.util.ArrayList;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TabPanel;

public class FunctionInspectorW extends FunctionInspector {

	private TabPanel tabPanel;
	private FlowPanel intervalTab;
	private FlowPanel pointsTab;

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
		return null;
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
		// TODO Auto-generated method stub
		return null;
	}

	public int getSelectedXYRow() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void buildTabPanel() {
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

}
