/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.desktop.euclidianND;

import java.awt.Cursor;
import java.io.File;

import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.border.Border;

import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.main.App.ExportType;

/**
 * interface for 2D/3D view in desktop
 * 
 * @author Mathieu
 *
 */
public interface EuclidianViewInterfaceD extends EuclidianViewInterfaceCommon {

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
	@Override
	public EuclidianController getEuclidianController();

	/**
	 * @param scale
	 *            image scale
	 * @return image
	 */
	public GBufferedImage getExportImage(double scale);

	/**
	 * @param scale
	 *            ratio of desired size and current size of the graphics
	 * @param transparency
	 *            true for transparent image
	 * @return image
	 * @throws OutOfMemoryError
	 *             if the requested image is too big
	 */
	public GBufferedImage getExportImage(double scale, boolean transparency,
			ExportType exportType)
			throws OutOfMemoryError;

	/**
	 * export the current image
	 * 
	 * @param scale
	 *            scale factor
	 * @param transparency
	 *            transparent or not
	 * @param dpi
	 *            resolution
	 * @param file
	 *            file
	 * @param exportToClipboard
	 *            if we want to export to clipboard
	 */
	public void exportImagePNG(double scale, boolean transparency, int dpi,
			File file, boolean exportToClipboard, ExportType exportType);

	/**
	 * @return printing scale
	 */
	public double getPrintingScale();

	/**
	 * Update fonts
	 */
	public void updateFonts();

	/**
	 * @return export width in pixels
	 */
	public int getExportWidth();

	/**
	 * @return export height in pixels
	 */
	public int getExportHeight();

	/**
	 * @param box
	 *            box to be added
	 */
	public void add(Box box);

}
