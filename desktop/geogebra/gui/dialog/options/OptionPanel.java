package geogebra.gui.dialog.options;

import geogebra.common.kernel.geos.GeoElement;

import javax.swing.border.Border;


/**
 * Interface for option panels
 * @author mathieu
 *
 */
public interface OptionPanel {
	/**
	 * Update the GUI to take care of new settings which were applied.
	 */
	public void updateGUI();

	/**
	 * JPanel method
	 */
	public void revalidate();

	/**
	 * JPanel method
	 * @param border border 
	 */
	public void setBorder(Border border);
	
}
