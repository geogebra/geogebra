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

package org.geogebra.web.full.gui.toolbarpanel.tableview;

import java.util.List;
import java.util.function.Function;

import org.geogebra.common.gui.view.probcalculator.ProbabilityCalculatorTableValues;
import org.geogebra.common.gui.view.probcalculator.ProbabilityCalculatorTableValues.Row;
import org.geogebra.common.gui.view.probcalculator.ProbabilityCalculatorTableValuesViewModel;
import org.geogebra.web.full.util.StickyTable;
import org.geogebra.web.html5.gui.util.Dom;
import org.gwtproject.cell.client.SafeHtmlCell;
import org.gwtproject.safehtml.shared.SafeHtml;
import org.gwtproject.user.cellview.client.Column;

import elemental2.dom.HTMLElement;

public final class StickyProbabilityTable extends StickyTable<Row> {

	private ProbabilityCalculatorTableValuesViewModel model;
	private ProbabilityCalculatorTableValues values;

	/**
	 * New table for prob calc
	 */
	public StickyProbabilityTable() {
		getTable().addStyleName("fullWidth");
		getTable().setRowStyles(
				(row, rowIndex) -> row.highlighted() ? "highlighted" : "");
	}

	private void addColumn(final Function<Row, String> projection) {
		getTable().addColumn(new Column<>(new SafeHtmlCell()) {
			@Override
			public SafeHtml getValue(Row row) {
				return new TableCell(projection.apply(row), false).getHTML();
			}
		}, getHeaderHTML(projection));
	}

	private SafeHtml getHeaderHTML(Function<Row, String> projection) {
		HTMLElement content = Dom.createDiv("content");
		HTMLElement label = Dom.createDiv("gwt-Label noMenu");
		content.appendChild(label);
		return () -> {
			label.innerHTML = values == null ? "" : projection.apply(values.header());
			return content.outerHTML;
		};
	}

	@Override
	protected void addCells() {
		// not needed: always the same number of cells
	}

	@Override
	protected void fillValues(List<Row> data) {
		values = model.getContent();
		data.clear();
		if (values != null) {
			data.addAll(values.rows());
		}
	}

	/**
	 * Sets model and initializes GUI.
	 * @param model model to probability data
	 */
	public void setModel(ProbabilityCalculatorTableValuesViewModel model) {
		this.model = model;
		model.setDelegate(this::refresh);
		addColumn(Row::k);
		addColumn(Row::probability);
	}
}
