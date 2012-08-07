package geogebra.cas.view;

import geogebra.common.cas.view.CASTable;
import geogebra.common.kernel.geos.GeoCasCell;
import geogebra.common.main.GeoGebraColorConstants;
import geogebra.main.AppD;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;

/**
 * Renders row headers
 */
public class RowHeaderRenderer extends JPanel implements ListCellRenderer {
		
	private static final long serialVersionUID = 1L;    	    	
	private CASTableD casTable;	
	private JLabel numLabel;
	/** show hide option (also called plot tool) for this cell content*/
	protected JLabel showHideControl;
	private ImageIcon iconShown, iconHidden;
	protected GridBagConstraints c;
	
	/**
	 * Creates new renderer
	 * @param casTable CAS table
	 */
	public RowHeaderRenderer(CASTableD casTable) {
		super(new GridBagLayout());
		c = new GridBagConstraints();
		AppD app = (AppD)casTable.getApplication();
		iconShown = app.getImageIcon("shown.gif");
		iconHidden = app.getImageIcon("hidden.gif");	
		numLabel = new JLabel("", SwingConstants.CENTER);		
		this.casTable = casTable;
		//setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));
		showHideControl = new JLabel(iconHidden);
		showHideControl.setVisible(false);
		add(numLabel,c);
		
		
		// set constraint to place the marble 8 pixels below numLabel 
		c.insets = new Insets(8,0,0,0);
		c.gridy = 1;
		add(showHideControl,c);
		
		setOpaque(true);
		setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, geogebra.awt.GColorD.getAwtColor(GeoGebraColorConstants.TABLE_GRID_COLOR)));
	}

	public Component getListCellRendererComponent(JList list, Object value,	int index, boolean  isSelected, boolean cellHasFocus) {
		numLabel.setText ((value == null) ? ""  : value.toString());
		numLabel.setFont(casTable.getFont());
		GeoCasCell ctr = casTable.getGeoCasCell(index);
		
		if(ctr.showOutput() && !ctr.isError()){
			showHideControl.setIcon(ctr.hasTwinGeo() && ctr.getTwinGeo().isEuclidianVisible() 
					&& ctr.getTwinGeo().isEuclidianShowable()?iconShown:iconHidden);
			showHideControl.setVisible(true);
		}else{
			showHideControl.setVisible(false);
		}
		
		if (isSelected) {
			setBackground(geogebra.awt.GColorD.getAwtColor(GeoGebraColorConstants.TABLE_SELECTED_BACKGROUND_COLOR_HEADER));
		}
		else {								
			setBackground(geogebra.awt.GColorD.getAwtColor(GeoGebraColorConstants.TABLE_BACKGROUND_COLOR_HEADER));
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
