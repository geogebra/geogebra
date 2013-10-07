package geogebra.html5.gui.inputfield;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Grid;

public class SymbolTableW extends Grid implements ClickHandler {

	// private AppWeb app;

	private String[] symbolStrings;
	private String[] symbolToolTips;
	private int selectedCellIndex = 0;
	private int selectedColumnIndex;
	private int selectedRowIndex;

	/**
	 * Constructs a SymbolTable
	 * 
	 * @param app
	 * @param symbolStrings
	 * @param symbolToolTips
	 */
	public SymbolTableW(String[] symbolStrings, String[] symbolToolTips) {
		super();
		resize((int) Math.ceil(symbolStrings.length / 10), 10);
		this.symbolStrings = symbolStrings;
		this.symbolToolTips = symbolToolTips;

		buildSymbolTable();
		addClickHandler(this);
		addStyleName("SymbolTable");
	}

	private void buildSymbolTable() {

		for (int i = 0; i < symbolStrings.length; i++) {
			int col = (int) Math.floor(i % 10);
			int row = (int) Math.floor(i / 10);
			setText(row, col, symbolStrings[i]);
		}
	}

	public void onClick(ClickEvent event) {

		Cell clickCell = getCellForEvent(event);
		clickCell.getRowIndex();
		int cellIndex = getColumnCount() * clickCell.getRowIndex()
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

	public String getSelectedSymbolString() {
		return getWidget(selectedRowIndex, selectedColumnIndex).getElement()
		        .getInnerText();
	}

}
