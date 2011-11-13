package geogebra.cas.view;

import javax.swing.AbstractListModel;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

public class RowHeaderListModel extends AbstractListModel implements TableModelListener {					 
		 
	private static final long serialVersionUID = 1L;
	private JTable table;
	private int size;
	
	  public RowHeaderListModel(JTable table) {
		  this.table = table;
		  table.getModel().addTableModelListener(this);
		  size = table.getRowCount();
	  }
	  
	  public int getSize() {
	    return size;
	  }
	
	  public Object getElementAt(int index) {
		  return Integer.toString(index + 1);
	  }
	
	public void tableChanged(TableModelEvent e) {
		int firstRow = e.getFirstRow();
		int lastRow = e.getLastRow();
				
		int oldSize = size;
		int rowCount = table.getRowCount();
		size = rowCount;		
		
		if (rowCount > oldSize) {
//			Application.printStacktrace("RowHeaderListModel.fireIntervalAdded " + firstRow + " " + lastRow);	
			fireIntervalAdded(this, firstRow, lastRow);								
		}
		else if (rowCount < oldSize) {
//			Application.printStacktrace("RowHeaderListModel.fireIntervalRemoved " + firstRow + " " + lastRow);
			fireIntervalRemoved(this, firstRow, lastRow);	
		}
		else {
//			Application.printStacktrace("RowHeaderListModel.fireContentsChanged " + firstRow + " " + lastRow);
			fireContentsChanged(this, firstRow, lastRow);	
		}					
	}
}