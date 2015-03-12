package geogebra.html5.gui.inputfield;

import geogebra.html5.main.DrawEquationWeb;

import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlexTable;

public class SymbolTableW extends FlexTable implements ClickHandler {

	private String[] symbolStrings;
	private String[] symbolToolTips;
	private int selectedCellIndex = 0;
	private int selectedColumnIndex;
	private int selectedRowIndex;
	private boolean isLatex;

	private int rowLength = 10;

	/**
	 * Constructs a SymbolTable
	 * 
	 * @param app
	 * @param symbolStrings
	 * @param symbolToolTips
	 */
	public SymbolTableW(String[] symbolStrings, String[] symbolToolTips,
	        boolean isLatex, int rowLength) {
		super();
		this.symbolStrings = symbolStrings;
		this.symbolToolTips = symbolToolTips;
		this.isLatex = isLatex;
		this.rowLength = rowLength;

		buildSymbolTable();
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
		this(symbolStrings, symbolToolTips, false, 10);
	}

	private void buildSymbolTable() {

		for (int i = 0; i < symbolStrings.length; i++) {
			int col = (int) Math.floor(i % rowLength);
			int row = (int) Math.floor(i / rowLength);

			if (isLatex) {
				setHTML(row, col, getLatexHTML(symbolStrings[i]));
			} else {
				setText(row, col, symbolStrings[i]);
			}

			// getCellFormatter().setHeight(row, col, "12px");
			// getCellFormatter().setWidth(row, col, "12px");
			// getCellFormatter().setHorizontalAlignment(row, col,
			// HasHorizontalAlignment.ALIGN_CENTER);
		}
	}

	private String getLatexHTML(String text) {

		SpanElement se = DOM.createSpan().cast();
		DrawEquationWeb.drawEquationAlgebraView(se, "\\mathrm {" + text + "}",
		        true);

		return se.getInnerHTML();
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

}
