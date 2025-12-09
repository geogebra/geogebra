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

import org.geogebra.web.full.gui.toolbarpanel.ProbabilityTableAdapter;
import org.geogebra.web.full.util.StickyTable;
import org.geogebra.web.html5.gui.util.Dom;
import org.gwtproject.cell.client.SafeHtmlCell;
import org.gwtproject.safehtml.shared.SafeHtml;
import org.gwtproject.user.cellview.client.Column;

import elemental2.dom.HTMLElement;

public class StickyProbabilityTable extends StickyTable<List<String>> {

	private ProbabilityTableAdapter adapter;

	/**
	 * New table for prob calc
	 */
	public StickyProbabilityTable() {
		getTable().addStyleName("fullWidth");
		getTable().setRowStyles(
				(row, rowIndex) -> adapter.isHighlighted(rowIndex) ? "highlighted" : "");
	}

	private void addColumn(final int col) {
		getTable().addColumn(new Column<>(new SafeHtmlCell()) {
			@Override
			public SafeHtml getValue(List<String> row) {
				return new TableCell(row.get(col), false).getHTML();
			}
		}, getHeaderHTML(col));
	}

	private SafeHtml getHeaderHTML(int col) {
		HTMLElement content = Dom.createDiv("content");
		HTMLElement label = Dom.createDiv("gwt-Label noMenu");
		content.appendChild(label);
		return () -> {
			label.innerHTML = adapter.getColumnName(col);
			return content.outerHTML;
		};
	}

	@Override
	protected void addCells() {
		// not needed: always the same number of cells
	}

	@Override
	protected void fillValues(List<List<String>> data) {
		adapter.fillValues(data);
	}

	/**
	 * Sets adapter and initialized GUI
	 * @param adapter adapter to probability data
	 */
	public void setAdapter(ProbabilityTableAdapter adapter) {
		this.adapter = adapter;
		addColumn(0);
		addColumn(1);
	}
}
