package geogebra.euclidianND;

import geogebra.common.euclidian.EuclidianController;

import java.awt.Cursor;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.border.Border;

/**
 * interface for 2D/3D view in desktop
 * 
 * @author matthieu
 *
 */
public interface EuclidianViewInterfaceDesktop {
	

	
	
	/**
	 * @return mouse position
	 */
	public java.awt.Point getMousePosition();
	
	/**
	 * @return underlying component
	 */
	public JPanel getJPanel();
	
	/**
	 * @see JPanel#setBorder(Border)
	 * @param border new border
	 */
	public void setBorder(Border border);
	
	/**
	 * @param cursor new cursor
	 */
	public void setCursor(Cursor cursor);
	
	



	
	

	
	/**
	 * 
	 * @return euclidian controller
	 */
	public EuclidianController getEuclidianController();


	
	
	
	


	public BufferedImage getExportImage(double scale);

}
