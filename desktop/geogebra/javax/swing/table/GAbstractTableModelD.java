package geogebra.javax.swing.table;

import javax.swing.event.EventListenerList;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;

/**
 * Wrappper class for javax.swing.table.AbstractTableModel 
 * @author judit
 *
 */
public abstract class GAbstractTableModelD {
	
	MyAbstractTableModel impl;
	GAbstractTableModelD wrapper = this;
	
	public GAbstractTableModelD(){
		impl = new MyAbstractTableModel();
	}
	
	public AbstractTableModel getImpl(){
		return impl;
	}

	
	private class MyAbstractTableModel extends AbstractTableModel{

		public int getRowCount() {
			return wrapper.getRowCount();
		}

		public int getColumnCount() {
			return wrapper.getColumnCount();
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			return wrapper.getValueAt(rowIndex, columnIndex);
		}
		
		public void setValueAt(Object aValue, int rowIndex, int columnIndex){
			wrapper.setValueAt(aValue, rowIndex, columnIndex);
		}
		
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return wrapper.isCellEditable(rowIndex, columnIndex);
		}
		
	}

	public abstract int getRowCount();
	public abstract Object getValueAt(int rowIndex, int columnIndex);
	public abstract int getColumnCount();

    /**
     *  This empty implementation is provided so users don't have to implement
     *  this method if their data model is not editable.
     *
     *  @param  aValue   value to assign to cell
     *  @param  rowIndex   row of cell
     *  @param  columnIndex  column of cell
     */
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    }
	
    /**
     *  Returns false.  This is the default implementation for all cells.
     *
     *  @param  rowIndex  the row being queried
     *  @param  columnIndex the column being queried
     *  @return false
     */
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
    
    /**
     * Notifies all listeners that rows in the range
     * <code>[firstRow, lastRow]</code>, inclusive, have been inserted.
     *
     * @param  firstRow  the first row
     * @param  lastRow   the last row
     *
     * @see TableModelEvent
     * @see EventListenerList
     *
     */
    public void fireTableRowsInserted(int firstRow, int lastRow) {
    	impl.fireTableRowsInserted(firstRow, lastRow);
    }

    /**
     * Notifies all listeners that rows in the range
     * <code>[firstRow, lastRow]</code>, inclusive, have been updated.
     *
     * @param firstRow  the first row
     * @param lastRow   the last row
     *
     * @see TableModelEvent
     * @see EventListenerList
     */
    public void fireTableRowsUpdated(int firstRow, int lastRow) {
        impl.fireTableRowsUpdated(firstRow, lastRow);
    }

    /**
     * Notifies all listeners that rows in the range
     * <code>[firstRow, lastRow]</code>, inclusive, have been deleted.
     *
     * @param firstRow  the first row
     * @param lastRow   the last row
     *
     * @see TableModelEvent
     * @see EventListenerList
     */
    public void fireTableRowsDeleted(int firstRow, int lastRow) {
        impl.fireTableRowsDeleted(firstRow, lastRow);
    }
	
}
