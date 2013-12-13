package geogebra.web.gui.view.data;

import geogebra.common.main.App;
import geogebra.common.main.Localization;
import geogebra.html5.awt.GPointW;

import java.util.HashMap;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;

/**
 * @author gabor
 * 
 * Stattable for web
 *
 */
public class StatTableW extends FlowPanel {

	private App app;
	private Localization loc;
	private MyTable myTable;
	private ScheduledCommand al;
	private HashMap<GPointW, MyComboBoxEditor> comboBoxEditorMap;
	private HashMap<GPointW, MyComBoxRenderer> comboBoxRendererMap;

	/**
	 * @param app App
	 */
	public StatTableW(App app) {
	    this.app = app;
	    this.loc = app.getLocalization();
	    
	    initTable();
	}
	    
    

	private void initTable() {
	    myTable = new MyTable();
	    
	    //coloring and things here with css....
    }
	
	/**
	 * Sets the dimensions and header values for the table. This should only be
	 * called once.
	 * 
	 * @param rows
	 *            number of rows
	 * @param rowNames
	 *            array of row header strings, if null then a row header is not
	 *            drawn
	 * @param columns
	 *            number of columns
	 * @param columnNames
	 *            array of column header strings, if null then a column header
	 *            is not drawn
	 */
	public void setStatTable(int rows, String[] rowNames, int columns,
			String[] columnNames) {

		myTable.resize(rows + 1, columns + 1);
		// set column names
		if (columnNames == null) {
			//myTable.setTableHeader(null);
			//this.setColumnHeaderView(null);
		} else {
			//tableModel.setColumnCount(0);
			for (int i = 0; i < columnNames.length; i++) {
				myTable.setWidget(0, i + 1, new Label(columnNames[i]));
			}
		}

		// create row header
		if (rowNames != null) {
			//rowHeader = new MyRowHeader(myTable, rowNames, this);
			// rowHeaderModel = new DefaultListModel();
			// .setModel(rowHeaderModel);
			for (int i = 0; i < rowNames.length; i++) {
				myTable.setWidget(i+1, 0, new Label(rowNames[i]));
			}
		} else {
			//setRowHeaderView(null);
		}

		//myTable.setPreferredScrollableViewportSize(myTable.getPreferredSize());
		// statTable.setMinimumSize(statTable.getPreferredSize());

		//this.revalidate();

		//repaint();

	}
	
	/**
	 * Sets all cells values to the blank string " ". Does not change table
	 * dimensions.
	 * 
	 * AG: Why does this needed?
	 */
	@Override
    public void clear() {
		for (int r = 0; r < myTable.getRowCount(); r++)
			for (int c = 0; c < myTable.getColumnCount(); c++)
				myTable.setWidget(r, c, new Label(" "));
	}
	
	/**
	 * Sets the table cells that will use a ComboBox
	 * 
	 * @param cellMap
	 */
	public void setComboBoxCells(HashMap<GPointW, String[]> cell, ScheduledCommand al) {

		this.al = al;

		/*TODO: if (comboBoxEditorMap == null)
			comboBoxEditorMap = new HashMap<Point, MyComboBoxEditor>();
		comboBoxEditorMap.clear();
		if (comboBoxRendererMap == null)
			comboBoxRendererMap = new HashMap<Point, MyComboBoxRenderer>();
		comboBoxRendererMap.clear();

		for (Point cell : cellMap.keySet()) {

			// get the String data for this combo box
			String[] items = cellMap.get(cell);

			// extract the menu items and the combo box label
			String comboBoxLabel = items[items.length - 1];
			String[] comboBoxItems = new String[items.length - 1];
			System.arraycopy(items, 0, comboBoxItems, 0, comboBoxItems.length);

			// create the comboBox editors/renderers and map them
			comboBoxEditorMap.put(cell, new MyComboBoxEditor(comboBoxItems));
			comboBoxRendererMap.put(cell, new MyComboBoxRenderer(comboBoxLabel,
					comboBoxItems));

		}*/
	}
	
	/**
	 * Gets the selected index for a cell given cell comboBox
	 * 
	 * @param row
	 * @param column
	 * @return
	 */
	public Integer getComboCellEditorSelectedIndex(int row, int column) {
		if (comboBoxEditorMap == null)
			return null;

		int modelColumn = myTable.convertColumnIndexToModel(column);
		GPointW cell = new GPointW(row, modelColumn);
		if (comboBoxEditorMap.keySet().contains(cell)) {
			return comboBoxEditorMap.get(cell).getSelectedIndex();
		}
		return null;
	}
	
	/**
	 * Sets the selected index for a cell given cell comboBox
	 * 
	 * @param index
	 * @param row
	 * @param column
	 * @return
	 */
	public boolean setComboCellSelectedIndex(int index, int row, int column) {

		if (comboBoxRendererMap == null)
			return false;

		int modelColumn = myTable.convertColumnIndexToModel(column);
		GPointW cell = new GPointW(row, modelColumn);

		if (comboBoxEditorMap.keySet().contains(cell)) {
			comboBoxEditorMap.get(cell).setSelectedIndex(index);
			return true;
		}
		return false;
	}
	
	public void setLabels(String[] rowNames, String[] columnNames) {

		// set column names
		if (columnNames != null && rowNames != null) {
			myTable.resize(columnNames.length, rowNames.length);
		}
		if (columnNames != null) {
			for (int i = 0; i < columnNames.length; i++)
				myTable.setWidget(0, i + 1, new Label(columnNames[i]));
		}

		if (rowNames != null) {
			for (int i = 0; i < rowNames.length; i++) {
				myTable.setWidget(i + 1, 0, new Label(rowNames[i]));
			}
		}
	}

	
	private class MyTable extends Grid /*do it with CellTable later*/ {

		private boolean allowCellEdith;
		private Object comboBoxEditorMap;

		public MyTable() {
	        super();
        }

		public int convertColumnIndexToModel(int column) {
	        // TODO Auto-generated method stub
	        return 0;
        }

		public void setAllowCellEdith(boolean allowCellEdit) {
			this.allowCellEdith = allowCellEdit;
		}
		
		public boolean isCellEditable(int rowIndex, int colIndex) {
			if (allowCellEdith == true) {
				return true;
			}
			
			if (comboBoxEditorMap == null) {
				//TODO: finish this GWT style
				return false;
			}
			
			//TODO!
			//int modelColumn = convertColumnIndexToModel(colIndex);
			//Point cell = new Point(rowIndex, modelColumn);
			//return comboBoxEditorMap.keySet().contains(cell);
			return false;
		}
		
	}
	
	private class MyComboBoxEditor /*use CellTable editor things here*/ {

		public Integer getSelectedIndex() {
	        // TODO Auto-generated method stub
	        return null;
        }

		public void setSelectedIndex(int index) {
	        // TODO Auto-generated method stub
	        
        }
		
	}
	
	private class MyComBoxRenderer /* use CellTable things here*/ {
		
	}

	public void setValueAt(String value, int row, int column) {
	   myTable.setWidget(row, column, new Label(value));
    }



	public Grid getTable() {
	    return myTable;
    }

}
