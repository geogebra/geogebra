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
