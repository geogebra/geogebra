package org.geogebra.web.html5.gui.inputfield;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.App;
import org.geogebra.web.html5.main.DrawEquationW;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Widget;

public class SymbolTableW extends FlexTable implements ClickHandler {

	private String[] symbolStrings;
	private int selectedCellIndex = 0;
	private boolean isLatex;

	private int rowLength = 10;
	private GeoNumeric sample;

	/**
	 * Constructs a SymbolTable
	 * 
	 * @param app
	 *            application
	 * @param symbolStrings
	 *            symbols
	 * @param isLatex
	 *            for latex ?
	 * @param rowLength
	 *            row length
	 * @param colors
	 *            colors
	 */
	public SymbolTableW(String[] symbolStrings,
			boolean isLatex, int rowLength, App app, GColor[] colors) {
		super();
		this.symbolStrings = symbolStrings;
		this.isLatex = isLatex;
		this.rowLength = rowLength;
		if (app != null) {
			this.sample = new GeoNumeric(app.getKernel().getConstruction());
		}
		buildSymbolTable(app, colors);
		addClickHandler(this);
		addStyleName("SymbolTable");
	}

	/**
	 * Constructs a SymbolTable
	 * 
	 * @param symbolStrings
	 *            symbols
	 */
	public SymbolTableW(String[] symbolStrings) {
		this(symbolStrings, false, 10, null, null);
	}

	private void buildSymbolTable(App app, GColor[] colors) {

		for (int i = 0; i < symbolStrings.length; i++) {
			int col = i % rowLength;
			int row = i / rowLength;

			if (isLatex) {
				this.setWidget(row, col, getLatexHTML(symbolStrings[i], app));
			} else {
				setText(row, col, symbolStrings[i]);
				if (colors != null && colors[i] != null) {
					this.getCellFormatter()
							.getElement(row, col)
							.getStyle()
							.setColor(GColor.getColorString(colors[i]));
				}
			}

			// getCellFormatter().setHeight(row, col, "12px");
			// getCellFormatter().setWidth(row, col, "12px");
			// getCellFormatter().setHorizontalAlignment(row, col,
			// HasHorizontalAlignment.ALIGN_CENTER);
		}
	}

	private Widget getLatexHTML(String text, App app) {
		Canvas c = DrawEquationW.paintOnCanvas(sample, text, null,
					app.getFontSize());
		return c;
	}

	@Override
	public void onClick(ClickEvent event) {

		Cell clickCell = getCellForEvent(event);

		if (clickCell == null) {
			// click on the gap between two buttons
			return;
		}

		int cellIndex = rowLength * clickCell.getRowIndex()
		        + clickCell.getCellIndex();

		setSelectedCellIndex(cellIndex);
	}

	public int getSelectedCellIndex() {
		return selectedCellIndex;
	}

	public void setSelectedCellIndex(int selectedCellIndex) {
		this.selectedCellIndex = selectedCellIndex;
	}

	/**
	 * @param row
	 *            row
	 * @param col
	 *            column
	 * @return symbol
	 */
	public String getSymbolText(int row, int col) {
		int index = row * rowLength + col;
		return this.symbolStrings[index];
	}

	@Override
	public com.google.gwt.user.client.Element getEventTargetCell(Event event) {
		return super.getEventTargetCell(event);
	}

}
