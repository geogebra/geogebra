package org.geogebra.web.web.gui.view.data;

import java.util.HashMap;

import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.web.html5.awt.GPointW;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

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
	    this.add(myTable);
	    
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

 		myTable.resize(rows, columns);
		// set column names
		myTable.setHeaderCells(columnNames);

		// create row header
		if (rowNames != null) {
			//rowHeader = new MyRowHeader(myTable, rowNames, this);
			// rowHeaderModel = new DefaultListModel();
			// .setModel(rowHeaderModel);
			for (int i = 0; i < rowNames.length; i++) {
				myTable.setWidget(i, 0, new Label(rowNames[i]));
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
		setLabels(rowNames, columnNames, true);
	}
	
	public void setLabels(String[] rowNames, String[] columnNames, boolean hasHeader) {

		// set column names
		if (columnNames != null && rowNames != null) {
			myTable.resize(rowNames.length + 1, columnNames.length + 1);
		}
		if (columnNames != null) {
			for (int i = 0; i < columnNames.length; i++)
				myTable.setWidget(0, i + 1, new Label(columnNames[i]));
		}

		int startRow = hasHeader ? 1: 0;
		if (rowNames != null) {
			for (int i = 0; i < rowNames.length; i++) {
				myTable.setWidget(startRow + i , 0, new Label(rowNames[i]));
			}
		}
	}

	
	/**
	 * @author gabor
	 * 
	 * Table for StatTable
	 *
	 */
	public class MyTable extends Grid /*do it with CellTable later*/ {

		private boolean allowCellEdith;
		private Object comboBoxEditorMap;
		private int firstRow = 0;

		public MyTable() {
	        super();
        }

		public void setHeaderCells(String[] columnNames) {
	        if (columnNames != null) {
	        	firstRow = 0;
	        	resizeRows(getRowCount() + 1);
	        	for (int i = 0; i < columnNames.length; i++) {
	        		this.setWidget(0, i, new Label(columnNames[i]));
	        		this.getCellFormatter().getElement(0, i).addClassName("headercell");
	        	}
	        	firstRow = 1;
	        }
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
		
		

		public void handleSelection(ClickEvent event) {
	       Cell c = this.getCellForEvent(event);
	       Element parentRow = c.getElement().getParentElement();
	       if (c.getElement().hasClassName("headercell")) {
	    	   parentRow.removeClassName("selected");
	    	   return;
	       }
	       
	       if (!event.isShiftKeyDown()) {
	    	   toggleSelection(parentRow);
		       if (parentRow.hasClassName("selected")) {
		    	   if (!(parentRow.getPreviousSiblingElement() != null &&
		    		   		parentRow.getPreviousSiblingElement().hasClassName("selected") ||
		    		   		parentRow.getNextSiblingElement() != null &&
		    		   		parentRow.getNextSiblingElement().hasClassName("selected"))) {
		    			   		clearSelection(c);
		    	   }
				} else {
					clearSelectionFrom(c);
				}
	       } else {
	    	   selectTableRows(getFirstSelectedRow(-1), c.getRowIndex());
	       }
		}

		private void toggleSelection(Element parentRow) {
	        if (parentRow.hasClassName("selected")) {
		    	   parentRow.removeClassName("selected");
		       } else {
		    	   parentRow.addClassName("selected");
		       }
        }
			

		private void clearSelectionFrom(Cell c) {
			if (c != null) {
				for (int i = c.getRowIndex(); i < this.getRowCount(); i++) {
		        	getRowFormatter().getElement(i).removeClassName("selected");
		        }
			}
	        
        }

		private void clearSelection(Cell c) {
	        for (int i = firstRow; i < this.getRowCount(); i++) {
	        	if (c != null) {
	        		if (c.getRowIndex() != i) {
	        			getRowFormatter().getElement(i).removeClassName("selected");
	        		}
	        	} else {
        			getRowFormatter().getElement(i).removeClassName("selected");
	        	}
	        }
        }

		public int[] getSelectedRows() {
			int start = firstRow;
			int end = 0;
			int [] result;
	        for (int i = firstRow; i < this.getRowCount(); i++) {
	        	if (this.getRowFormatter().getElement(i).hasClassName("selected")) {
	        		if (end == 0) {
	        			start = i;
	        		}
	        		end++;
	        	}
	        }
	        result = new int [end];
	        for (int i = 0; i < end; i++) {
	        	result[i] = start + i;
	        }
	        
	       return result;
        }

		public String getValueAt(int i, int j) {
	        return this.getText(i, j);
        }
		
		private int getFirstSelectedRow(int to) {
			int t = to > -1 ? to : this.getRowCount();
			 for (int i = firstRow; i < this.getRowCount(); i++) {
	    		   if (this.getRowFormatter().getElement(i).hasClassName("selected") && i <= t) {
	    			   return i;
	    		   }
	    	   }
			 return -1;
			 
		}
		
		private void selectTableRows(int from, int to) {
			int fr = from > -1 ? from : firstRow;
			int t = to > this.getRowCount() ? this.getRowCount() : to;
			if (fr > this.getRowCount()) {
				fr = this.getRowCount();
			}
			if (t < 0) {
				t = 0;
			}
			if (fr <= t) {
				for (int i = fr; i <= t; i++) {
					this.getRowFormatter().getElement(i).addClassName("selected");
				}
			} else {
				for (int i = fr; i >= t; i--) {
					this.getRowFormatter().getElement(i).addClassName("selected");
				}
			}
		}

		public void changeSelection(int row, boolean toggle, boolean extend) {
		   int start;
		   int r = row + firstRow;
		   if (r < getRowCount()) {
		       if (!toggle && !extend) {
		    	   clearSelection(null);
		    	   this.getRowFormatter().getElement(r).addClassName("selected");
		       } else if (!toggle && extend) {
		    	   start = getFirstSelectedRow(r);
		    	   if (start > -1) {
		    		   selectTableRows(start, r);
		    	   }
		       }
		   }
        }

		/**
		 * @param row row
		 * @param column column
		 * @param widget widget
		 */
		@Override
        public void setWidget(int row, int column, Widget widget) {
			super.setWidget(row + firstRow, column, widget);
		}

		public void setValueAt(String str, int row, int col) {
	        setWidget(row, col, new Label(str));
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



	public MyTable getTable() {
	    return myTable;
    }

}
