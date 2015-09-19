package org.geogebra.web.html5.gui.inputfield;

import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.App;
import org.geogebra.web.html5.main.DrawEquationWeb;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Widget;

public class SymbolTableW extends FlexTable implements ClickHandler {

	private String[] symbolStrings;
	private String[] symbolToolTips;
	private int selectedCellIndex = 0;
	private int selectedColumnIndex;
	private int selectedRowIndex;
	private boolean isLatex;

	private int rowLength = 10;
	private GeoNumeric sample;

	/**
	 * Constructs a SymbolTable
	 * 
	 * @param app
	 * @param symbolStrings
	 * @param symbolToolTips
	 */
	public SymbolTableW(String[] symbolStrings, String[] symbolToolTips,
			boolean isLatex, int rowLength, App app) {
		super();
		this.symbolStrings = symbolStrings;
		this.symbolToolTips = symbolToolTips;
		this.isLatex = isLatex;
		this.rowLength = rowLength;
		if (app != null) {
			this.sample = new GeoNumeric(app.getKernel().getConstruction());
		}
		buildSymbolTable(app);
		addClickHandler(this);
		addStyleName("SymbolTable");
	}

	/**
	 * Constructs a SymbolTable
	 * 
	 * @param app
	 * @param symbolStrings
	 * @param symbolToolTips
	 */
	public SymbolTableW(String[] symbolStrings, String[] symbolToolTips) {
		this(symbolStrings, symbolToolTips, false, 10, null);
	}

	private void buildSymbolTable(App app) {

		for (int i = 0; i < symbolStrings.length; i++) {
			int col = (int) Math.floor(i % rowLength);
			int row = (int) Math.floor(i / rowLength);

			if (isLatex) {
				this.setWidget(row, col, getLatexHTML(symbolStrings[i], app));
			} else {
				setText(row, col, symbolStrings[i]);
			}

			// getCellFormatter().setHeight(row, col, "12px");
			// getCellFormatter().setWidth(row, col, "12px");
			// getCellFormatter().setHorizontalAlignment(row, col,
			// HasHorizontalAlignment.ALIGN_CENTER);
		}
	}

	private Widget getLatexHTML(String text, App app) {
		Canvas c = DrawEquationWeb.paintOnCanvas(sample, text, null,
					app.getFontSizeWeb());
		return c;
	}

	public void onClick(ClickEvent event) {

		Cell clickCell = getCellForEvent(event);

		if (clickCell == null) {
			// click on the gap between two buttons
			return;
		}

		int cellIndex = rowLength * clickCell.getRowIndex()
		        + clickCell.getCellIndex();

		setSelectedCellIndex(cellIndex);
		setSelectedRowIndex(clickCell.getRowIndex());
		setSelectedColumnIndex(clickCell.getCellIndex());
	}

	private void setSelectedColumnIndex(int index) {
		selectedColumnIndex = index;
	}

	private void setSelectedRowIndex(int index) {
		selectedRowIndex = index;
	}

	public int getSelectedCellIndex() {
		return selectedCellIndex;
	}

	public void setSelectedCellIndex(int selectedCellIndex) {
		this.selectedCellIndex = selectedCellIndex;
	}

	public String getSymbolText(int row, int col) {
		int index = row * rowLength + col;
		return this.symbolStrings[index];
	}

	public com.google.gwt.user.client.Element getEventTargetCell(Event event) {
		return super.getEventTargetCell(event);
	}

}
