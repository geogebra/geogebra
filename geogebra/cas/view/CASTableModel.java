package geogebra.cas.view;

import javax.swing.table.DefaultTableModel;

public class CASTableModel extends DefaultTableModel {
   
 	
    public CASTableModel() {
        super(1, 1);          
    }
    
    public String getRowLabel (int row)
    {        
        return String.valueOf(row + 1);
    }

    /*
     * Don't need to implement this method unless your table's
     * editable.
     */
    public boolean isCellEditable(int row, int col) {
    	return true;
    }


}
