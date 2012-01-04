package geogebra.common.gui.view.spreadsheet;

public interface SpreadsheetTableModelInterface {

	public int getRowCount();
	
	public int getColumnCount();
	
	public Object getValueAt(int row, int column);
}
