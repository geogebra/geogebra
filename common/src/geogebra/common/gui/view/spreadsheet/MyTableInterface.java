package geogebra.common.gui.view.spreadsheet;


public interface MyTableInterface {

	public int getSelectionType();

	public boolean editCellAt(int selectedRow, int selectedColumn);

	public CellFormatInterface getCellFormatHandler();

	public boolean isSelectNone();

	public void repaint();

	public void selectionChanged();

	public boolean setSelection(int i, int j);

	public void updateEditor(String text);

}
