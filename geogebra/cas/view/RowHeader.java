package geogebra.cas.view;

import geogebra.main.GeoGebraColorConstants;

import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.ListSelectionModel;

public class RowHeader extends JList {
	
	public static final int ROW_HEADER_WIDTH = 30;
	
	public RowHeader(CASTable table){
		this(table, false);
	}
	
	
	public RowHeader(CASTable table, boolean multipleIntervalSelection) {
		setModel(new RowHeaderListModel(table));
		
		if (multipleIntervalSelection)
			setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		else {
			setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		}
		setFixedCellWidth(ROW_HEADER_WIDTH);
		setFocusable(true);
		setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, GeoGebraColorConstants.TABLE_GRID_COLOR));
		// renderer
		setCellRenderer(new RowHeaderRenderer(table));
			
		// listener 
		RowHeaderListener rhl = new RowHeaderListener(table, this);						
		addMouseListener(rhl);
		addMouseMotionListener(rhl);	
		addKeyListener(rhl);
		
//		table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
//		table.getSelectionModel().addListSelectionListener(this);
//		table.setRowSelectionAllowed(true);	
	}

}
