package org.geogebra.common.gui.view.probcalculator;

import org.geogebra.common.gui.view.table.TableValues;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;
import org.geogebra.common.main.App;
import org.geogebra.common.main.settings.ProbabilityCalculatorSettings;

public class ProbabilityTableValues extends ProbabilityTable {

	private final TableValues view;
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
	public void setTable(ProbabilityCalculatorSettings.Dist distType2, GeoNumberValue[] parms2,
			int xMin2, int xMax2) {
		GeoElement evaluatable = (GeoElement) view.getEvaluatable(1);
		if (evaluatable != null) {
			view.remove(evaluatable);
		}
		probabilityList = new GeoList(view.getValues().cons);
		view.addAndShow(probabilityList);

		setTableModel(distType2, parms2, xMin2, xMax2);
		fillRows(distType2, parms2, xMin2, xMax2);
	}

	@Override
	protected void setRowValues(int row, String k, String prob) {
		view.getProcessor().processInput(k, null, row);
		view.getProcessor().processInput(k, probabilityList, row);
	}
}
