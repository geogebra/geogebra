package geogebra.gui.view.spreadsheet;

import geogebra.common.gui.view.spreadsheet.SpreadsheetTableModelInterface;

import javax.swing.table.DefaultTableModel;

/**
 * @author G. Sturr
 *
 */
public class SpreadsheetTableModel extends DefaultTableModel
implements SpreadsheetTableModelInterface {
	
	private static final long serialVersionUID = 1L;

	
	/** Constructor
	 * @param rows
	 * @param columns
	 */
	public SpreadsheetTableModel(int rows, int columns){
		super(rows, columns);
	}
	

}
