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

package org.geogebra.web.full.gui.toolbarpanel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import org.geogebra.common.gui.view.probcalculator.ProbabilityCalculatorView;
import org.geogebra.common.gui.view.probcalculator.ProbabilityTable;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.main.App;
import org.geogebra.common.main.settings.ProbabilityCalculatorSettings;
import org.geogebra.web.full.gui.toolbarpanel.tableview.StickyProbabilityTable;

public class ProbabilityTableAdapter extends ProbabilityTable {
	private final StickyProbabilityTable table;
	private final ArrayList<List<String>> rows = new ArrayList<>();
	private Predicate<Integer> highlighted = row -> false;

	/**
	 * @param table table
	 * @param app app
	 * @param probCalc probability calc
	 */
	public ProbabilityTableAdapter(StickyProbabilityTable table, App app,
			ProbabilityCalculatorView probCalc) {
		super(app, probCalc);
		this.table = table;
		setColumnNames();
		table.setAdapter(this);
	}

	@Override
	public void setSelectionByRowValue(int lowValue, int highValue) {
		this.highlighted = row -> row >= lowValue && row <= highValue;
		table.getTable().redraw();
	}

	@Override
	public void setTwoTailedSelection(int lowValue, int highValue) {
		this.highlighted = row -> row <= lowValue || row >= highValue;
		table.getTable().redraw();
	}

	@Override
	public void setTable(ProbabilityCalculatorSettings.Dist distType, GeoNumberValue[] params,
			int xMin, int xMax) {
		rows.clear();
		setTableModel(distType, params, xMin, xMax);
		fillRows(distType, params, xMin, xMax);
		table.refresh();
	}

	@Override
	protected void setRowValues(int row, String k, String prob) {
		rows.add(row, Arrays.asList(k, prob));
	}

	/**
	 * Clears data and fills it from currently selected distribution
	 * @param data output list
	 */
	public void fillValues(List<List<String>> data) {
		data.clear();
		data.addAll(rows);
	}

	/**
	 * @param rowIndex row index
	 * @return whether the row is highlighted
	 */
	public boolean isHighlighted(int rowIndex) {
		return highlighted.test(rowIndex);
	}

	/**
	 * @param col column index
	 * @return column name
	 */
	public String getColumnName(int col) {
		return getColumnNames()[col];
	}
}
