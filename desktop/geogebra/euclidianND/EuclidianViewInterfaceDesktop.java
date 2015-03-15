package geogebra.euclidianND;

import geogebra.common.euclidian.EuclidianController;

import java.awt.Cursor;
import java.awt.image.BufferedImage;
import java.io.File;

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
	 * @param border
	 *            new border
	 */
	public void setBorder(Border border);

	/**
	 * @param cursor
	 *            new cursor
	 */
	public void setCursor(Cursor cursor);

	/**
	 * 
	 * @return euclidian controller
	 */
	public EuclidianController getEuclidianController();

	public BufferedImage getExportImage(double scale);
	
	/**
	 * @param scale
	 *            ratio of desired size and current size of the graphics
	 * @param transparency
	 *            true for transparent image
	 * @return image
	 * @throws OutOfMemoryError
	 *             if the requested image is too big
	 */
	public BufferedImage getExportImage(double scale, boolean transparency)
			throws OutOfMemoryError;
	
	/**
	 * export the current image
	 * @param scale scale factor
	 * @param transparency transparent or not
	 * @param dpi resolution
	 * @param file file
	 * @param exportToClipboard if we want to export to clipboard
	 */
	public void exportImagePNG(double scale, boolean transparency, int dpi,
			File file, boolean exportToClipboard);

}
