package org.geogebra.common.gui.view.probcalculator;

import org.geogebra.common.gui.view.table.TableValues;
import org.geogebra.common.gui.view.table.TableValuesModel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;
import org.geogebra.common.main.App;
import org.geogebra.common.main.settings.ProbabilityCalculatorSettings;

public class ProbabilityTableValues extends ProbabilityTable {

	private final TableValues view;
	private final TableValuesModel model;
	private GeoList probabilityList;

	/**
	 * @param app application
	 * @param probCalc probability calculator
	 * @param view view
	 */
	public ProbabilityTableValues(App app,
			ProbabilityCalculatorView probCalc, TableValues view) {
		super(app, probCalc);
		this.view = view;
		this.model = view.getTableValuesModel();
	}

	@Override
	public void setSelectionByRowValue(int lowValue, int highValue) {
		// ToDo
	}

	@Override
	public void setTwoTailedSelection(int lowValue, int highValue) {
		// ToDo
	}

	@Override
	public void setTable(ProbabilityCalculatorSettings.Dist dist, GeoNumberValue[] params,
			int xMin, int xMax) {
		view.clearView();
		setTableModel(dist, params, xMin, xMax);
		if (getProbManager().isDiscrete(dist)) {
			String[] columnNames = getColumnNames();
			model.setValuesHeader(columnNames[0]);

			probabilityList = new GeoList(view.getValues().cons);
			probabilityList.setLabel(columnNames[1]);
			view.addAndShow(probabilityList);

			fillRows(dist, params, xMin, xMax);
		}
	}

	@Override
	protected void setRowValues(int row, String k, String prob) {
		view.getProcessor().processInput(k, view.getValues(), row);
		view.getProcessor().processInput(prob, probabilityList, row);
	}
}
