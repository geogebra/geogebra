/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.gui.view.probcalculator;

import java.util.function.Predicate;

import org.geogebra.common.gui.view.table.TableValues;
import org.geogebra.common.gui.view.table.TableValuesModel;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.App;
import org.geogebra.common.main.settings.ProbabilityCalculatorSettings;

public class ProbabilityTableValues extends ProbabilityTable {

	protected final TableValues view;
	private final TableValuesModel model;
	private final Kernel kernel;
	private final Construction construction;

	private GeoList xValues;
	private GeoList probabilityValues;
	private Predicate<Integer> highlighted = row -> false;

	/**
	 * @param app application
	 * @param probCalc probability calculator
	 * @param view view
	 */
	public ProbabilityTableValues(App app, ProbabilityCalculatorView probCalc, TableValues view) {
		super(app, probCalc);
		this.view = view;
		model = view.getTableValuesModel();
		kernel = probCalc.kernel;
		construction = kernel.getConstruction();
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

		if (probabilityValues != null) {
			probabilityValues.remove();
		}
		view.clearView();

		setTableModel(dist, params, xMin, xMax);
		if (getProbManager().isDiscrete(dist)) {
			String[] columnNames = getColumnNames();
			model.setValuesHeader(columnNames[0]);

			xValues = view.getValues();
			probabilityValues = new GeoList(view.getValues().cons);
			probabilityValues.setLabel(columnNames[1]);

			view.addAndShow(probabilityValues);
			kernel.getConstruction().removeFromConstructionList(probabilityValues);

			int rows = xMax - xMin + 1;
			xValues.ensureCapacity(rows);
			probabilityValues.ensureCapacity(rows);

			// Batch update will notify that the dataset has changed.
			view.startBatchUpdate();
			fillRows(dist, params, xMin, xMax);
			view.endBatchUpdate();
		}

		kernel.setUndoActive(isUndoActive);
	}

	@Override
	protected void setRowValues(int row, String k, String prob) {
		assert row == xValues.size() : "The assumption that row values are set in increasing"
				+ " order is broken.";
		GeoNumeric kNumeric = new GeoNumeric(construction, Double.parseDouble(k));
		GeoNumeric probNumeric = new GeoNumeric(construction, Double.parseDouble(prob));
		xValues.add(kNumeric);
		probabilityValues.add(probNumeric);
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
