package geogebra.cas.view;

import geogebra.main.Application;
import geogebra.main.GeoGebraColorConstants;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class RowHeaderRenderer extends JLabel implements ListCellRenderer {
		
	private static final long serialVersionUID = 1L;    	    	
	private CASTable casTable;	
	private Application app;
	
	public RowHeaderRenderer(CASTable casTable) {
		super("", JLabel.CENTER);		
		this.casTable = casTable;
		this.app = casTable.app;
				
		setOpaque(true);
		setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, GeoGebraColorConstants.TABLE_GRID_COLOR));
	}

	public Component getListCellRendererComponent(JList list, Object value,	int index, boolean  isSelected, boolean cellHasFocus) {
		setText ((value == null) ? ""  : value.toString());
		setFont(casTable.getFont());
		
		if (isSelected) {
			setBackground(GeoGebraColorConstants.TABLE_SELECTED_BACKGROUND_COLOR_HEADER);
		}
		else {								
			setBackground(GeoGebraColorConstants.TABLE_BACKGROUND_COLOR_HEADER);
		}
	
		// update height		
		Dimension prefSize = getPreferredSize();
		// go through all columns of this row to get the max height
		int height = casTable.getPreferredRowHeight(index);			
		if (height != prefSize.height) {
			prefSize.height = height;
			setSize(prefSize);
			setPreferredSize(prefSize);
		}
			
		return this;
	}


}
