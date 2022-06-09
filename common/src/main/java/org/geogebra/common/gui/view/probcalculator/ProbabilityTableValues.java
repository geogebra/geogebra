package org.geogebra.common.gui.view.probcalculator;

import java.util.function.Predicate;

import org.geogebra.common.gui.view.table.TableValues;
import org.geogebra.common.gui.view.table.TableValuesModel;
import org.geogebra.common.gui.view.table.TableValuesProcessor;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.main.App;
import org.geogebra.common.main.settings.ProbabilityCalculatorSettings;

public class ProbabilityTableValues extends ProbabilityTable {

	private final TableValues view;
	private final TableValuesModel model;
	private final TableValuesProcessor processor;
	private final Kernel kernel;

	private GeoList probabilityList;
	private Predicate<Integer> highlighted = row -> false;

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
		this.processor = view.getProcessor();
		this.kernel = probCalc.kernel;
	}

	@Override
	public void setSelectionByRowValue(int lowValue, int highValue) {
		highlighted = row -> row >= lowValue && row <= highValue;
	}

	@Override
	public void setTwoTailedSelection(int lowValue, int highValue) {
		highlighted = row -> row <= lowValue || row >= highValue;
	}

	@Override
	public void setTable(ProbabilityCalculatorSettings.Dist dist, GeoNumberValue[] params,
			int xMin, int xMax) {
		boolean isUndoActive = kernel.isUndoActive();
		kernel.setUndoActive(false);

		if (probabilityList != null) {
			probabilityList.remove();
		}
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

		kernel.setUndoActive(isUndoActive);
	}

	@Override
	protected void setRowValues(int row, String k, String prob) {
		processor.processInput(k, view.getValues(), row);
		processor.processInput(prob, probabilityList, row);
	}

	/**
	 * Returns true if row should be highlighted
	 * @param row row
	 * @return true to highlight row
	 */
	public boolean isRowHighlighted(int row) {
		return highlighted.test(row);
	}
}
