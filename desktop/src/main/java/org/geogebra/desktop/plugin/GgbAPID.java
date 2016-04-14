package org.geogebra.desktop.plugin;

import java.awt.Toolkit;
/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.

 */
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.AccessController;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedAction;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.JOptionPane;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.main.App;
import org.geogebra.common.util.FileExtensions;
import org.geogebra.common.util.StringUtil;
import org.geogebra.desktop.euclidianND.EuclidianViewInterfaceD;
import org.geogebra.desktop.export.GraphicExportDialog;
import org.geogebra.desktop.gui.util.ImageSelection;
import org.geogebra.desktop.io.MyImageIO;
import org.geogebra.desktop.main.AppD;

/**
 * <h3>GgbAPI - API for PlugLets</h3>
 * 
 * <pre>
 *    The Api the plugin program can use.
 * </pre>
 * <ul>
 * <h4>Interface:</h4>
 * <li>GgbAPI(Application) //Application owns it
 * <li>getApplication()
 * <li>getKernel()
 * <li>getConstruction()
 * <li>getAlgebraProcessor()
 * <li>getPluginManager()
 * <li>evalCommand(String)
 * <li>and the rest of the methods from the Applet JavaScript/Java interface
 * <li>...
 * </ul>
 * 
 * @author H-P Ulven
 * @version 31.10.08 29.05.08: Tranferred applet interface methods (the relevant
 *          ones) from GeoGebraAppletBase
 */

public class GgbAPID extends org.geogebra.common.plugin.GgbAPI {

	/**
	 * Constructor: Makes the api with a reference to the GeoGebra program.
	 * Called from GeoGebra.
	 * 
	 * @param app
	 *            Application
	 */
	public GgbAPID(AppD app) {
		this.app = app;
		kernel = app.getKernel();
		algebraprocessor = kernel.getAlgebraProcessor();
		construction = kernel.getConstruction();
		// pluginmanager=app.getPluginManager();
	}

	// /* JAVA SCRIPT INTERFACE */

	/**
	 * Returns current construction as a ggb file in form of a byte array.
	 * 
	 * @return null if something went wrong
	 */
	public synchronized byte[] getGGBfile() {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			((AppD) app).getXMLio().writeGeoGebraFile(bos, true);
			bos.flush();
			return bos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Returns current construction in Base64 format. May be used for saving.
	 */
	@Override
	public synchronized String getBase64(boolean includeThumbnail) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			((AppD) app).getXMLio().writeGeoGebraFile(baos, includeThumbnail);
			return org.geogebra.common.util.Base64.encode(baos.toByteArray(), 0);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Opens construction given in XML format. May be used for loading
	 * constructions.
	 */
	public synchronized void setBase64(String base64) {
		byte[] zipFile;
		try {
			zipFile = org.geogebra.common.util.Base64.decode(base64);
		} catch (IOException e) {

			e.printStackTrace();
			return;
		}
		((AppD) app).loadXML(zipFile);
	}

	/**
	 * Turns showing of error dialogs on (true) or (off). Note: this is
	 * especially useful together with evalCommand().
	 */
	public synchronized void setErrorDialogsActive(boolean flag) {
		((AppD) app).setErrorDialogsActive(flag);
	}

	/**
	 * Clears the construction and resets all views.
	 */
	public synchronized void fileNew() {
		((AppD) app).fileNew();
	}

	/**
	 * Refreshs all views. Note: clears traces in geometry window.
	 */
	public synchronized void refreshViews() {
		app.refreshViews();
	}

	/**
	 * Loads a construction from a file (given URL). Note that this method does
	 * NOT refresh the user interface.
	 */
	public synchronized void openFile(String strURL) {
		try {
			String lowerCase = StringUtil.toLowerCase(strURL);
			URL url = new URL(strURL);
			((AppD) app).loadXML(url,
					lowerCase.endsWith(FileExtensions.GEOGEBRA_TOOL.ext));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static MessageDigest messageDigestMD5 = null;

	/**
	 * @return reference to MD5 algorithm
	 * @throws NoSuchAlgorithmException
	 *             if algorithm is not supported
	 */
	public static MessageDigest getMessageDigestMD5()
			throws NoSuchAlgorithmException {
		if (messageDigestMD5 == null)
			messageDigestMD5 = MessageDigest.getInstance("MD5");

		return messageDigestMD5;
	}

	/*
	 * saves a PNG file signed applets only
	 */
	public synchronized boolean writePNGtoFile(String filename,
			final double exportScale, final boolean transparent,
			final double DPI) {

		File file1 = null;
		try {
			file1 = new File(filename);
		} catch (Throwable t) {
			t.printStackTrace();
		}
		if (file1 == null)
			return false;
		final File file = file1;
		return (Boolean) AccessController
				.doPrivileged(new PrivilegedAction<Object>() {
					public Boolean run() {

						try {
							// draw graphics view into image
							BufferedImage img = ((AppD) getApplication())
									.getEuclidianView1().getExportImage(
											exportScale, transparent);

							// write image to file
							MyImageIO.write(img, "png", (float) DPI, file);

							return true;
						} catch (Exception ex) {
							App.debug(ex.toString());
							return false;
						} catch (Error ex) {
							App.debug(ex.toString());
							return false;
						}

					}
				});

	}

	public synchronized String getPNGBase64(double exportScale,
			boolean transparent, double DPI) {
		return getPNGBase64(exportScale, transparent, DPI, false);
	}

	/*
	 * returns a String (base-64 encoded PNG file of the Graphics View)
	 */
	public synchronized String getPNGBase64(double exportScale,
			boolean transparent, double DPI, boolean copyToClipboard) {

		EuclidianViewInterfaceD ev = (EuclidianViewInterfaceD) ((AppD) app)
				.getActiveEuclidianView();
		
		if (copyToClipboard) {

			if (DPI == 0 || Double.isNaN(DPI)) {
				// pastes into more programs
				BufferedImage img = ev.getExportImage(exportScale, transparent);

				ImageSelection imgSel = new ImageSelection(img);
				Toolkit.getDefaultToolkit().getSystemClipboard()
						.setContents(imgSel, null);
			} else {

				if (exportScale == 0 || Double.isNaN(exportScale)) {
					// calculate so that we get 1:1 scale
					exportScale = (ev.getPrintingScale() * DPI) / 2.54
							/ ev.getXscale();

				}

				// more control but doesn't paste into eg Paint, Google Docs
				GraphicExportDialog.exportPNG(true, transparent, (int) DPI,
						exportScale, (AppD) app, (EuclidianViewInterfaceD) ev);
			}
			return "";

		}

		BufferedImage img = ev.getExportImage(exportScale, transparent);

		return base64encode(img, DPI);
	}

	/**
	 * @param img
	 *            image
	 * @return encoded image
	 */
	public static String base64encode(BufferedImage img, double DPI) {
		if (img == null) {
			return null;
		}
		try {
			Iterator<ImageWriter> it = ImageIO
					.getImageWritersByFormatName("png");
			ImageWriter writer = it.next();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			ImageOutputStream ios = ImageIO.createImageOutputStream(baos);

			writer.setOutput(ios);

			MyImageIO.writeImage(writer, img, DPI);

			String ret = org.geogebra.common.util.Base64.encode(baos.toByteArray(),
					0);

			baos.close();

			return ret;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void drawToImage(String label, double[] x, double[] y) {
		GeoElement ge = kernel.lookupLabel(label);

		if (ge == null) {
			ge = new GeoImage(kernel.getConstruction());
			if (label == null || label.length() == 0) {
				ge.setLabel(null);
			} else {
				ge.setLabel(label);
			}
		}
		if (!ge.isGeoImage()) {
			debug("Bad drawToImage arguments");
			return;
		}

		((AppD) app).getEuclidianView1().drawPoints((GeoImage) ge, x, y);

	}

	public void clearImage(String label) {
		GeoElement ge = kernel.lookupLabel(label);

		if (!ge.isGeoImage()) {
			debug("Bad drawToImage arguments");
			return;
		}
		((GeoImage) ge).clearFillImage();

	}

	/**
	 * JavaScript-like prompt
	 * 
	 * @param value0
	 *            prompt text
	 * @param value1
	 *            default value
	 * @return user's response
	 */
	public String prompt(Object value0, Object value1) {
		return (String) JOptionPane.showInputDialog(((AppD) app).getFrame(),
				value0, GeoGebraConstants.APPLICATION_NAME,
				JOptionPane.PLAIN_MESSAGE, null, null, value1);
	}

	/**
	 * pops up message dialog with "OK" and "Stop Script"
	 * 
	 * @param message
	 *            to display
	 */
	public void alert(String message) {
		Object[] options = { app.getPlain("StopScript"), app.getPlain("OK") };
		int n = JOptionPane.showOptionDialog(((AppD) app).getFrame(), message,
				GeoGebraConstants.APPLICATION_NAME, JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, // do not use a custom Icon
				options, // the titles of buttons
				options[0]); // default button title

		if (n == 0)
			throw new Error("Script stopped by user");

	}


	/**
	 * Returns the dimensions of the real world coordinate system in the
	 * graphics view as [xmin, ymin, width, height]
	 * 
	 * @return dimensions of the real world coordinate system
	 */
	public synchronized Rectangle2D.Double getCoordSystemRectangle() {
		EuclidianView ev = app.getEuclidianView1();
		return new Rectangle2D.Double(ev.getXmin(), ev.getYmin(), ev.getXmax()
				- ev.getXmin(), ev.getYmax() - ev.getYmin());
	}

}// class GgbAPI

