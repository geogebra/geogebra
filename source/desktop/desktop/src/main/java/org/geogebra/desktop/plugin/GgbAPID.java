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

package org.geogebra.desktop.plugin;

import java.awt.Toolkit;
import java.awt.geom.Rectangle2D;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Consumer;

import javax.swing.JOptionPane;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.jre.plugin.GgbAPIJre;
import org.geogebra.common.main.App.ExportType;
import org.geogebra.common.main.Localization;
import org.geogebra.common.plugin.JsObjectWrapper;
import org.geogebra.common.util.FileExtensions;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.awt.GBufferedImageD;
import org.geogebra.desktop.euclidianND.EuclidianViewInterfaceD;
import org.geogebra.desktop.export.GraphicExportDialog;
import org.geogebra.desktop.gui.util.ImageSelection;
import org.geogebra.desktop.io.MyImageIO;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.util.UtilD;

/**
 * <h1>GgbAPI - API for applets</h1>
 * 
 * <pre>
 *    The Api the plugin program can use.
 * </pre>
 * <ul>
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
 */

public class GgbAPID extends GgbAPIJre {

	/**
	 * Constructor: Makes the api with a reference to the GeoGebra program.
	 * Called from GeoGebra.
	 * 
	 * @param app
	 *            Application
	 */
	public GgbAPID(AppD app) {
		super(app);
		// pluginmanager=app.getPluginManager();
	}

	// /* JAVA SCRIPT INTERFACE */

	/**
	 * Returns current construction as a ggb file in form of a byte array.
	 * 
	 * @return null if something went wrong
	 */
	@Override
	public synchronized byte[] getGGBfile() {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			((AppD) app).getXMLio().writeGeoGebraFile(bos, true);
			bos.flush();
			return bos.toByteArray();
		} catch (IOException e) {
			Log.debug(e);
			return null;
		}
	}

	/**
	 * Turns showing of error dialogs on (true) or (off). Note: this is
	 * especially useful together with evalCommand().
	 */
	@Override
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
	 * Refreshes all views. Note: clears traces in geometry window.
	 */
	@Override
	public synchronized void refreshViews() {
		app.refreshViews();
	}

	/**
	 * Loads a construction from a file (given URL). Note that this method does
	 * NOT refresh the user interface.
	 */
	@Override
	public synchronized void openFile(String strURL) {
		try {
			openFileUnsafe(strURL);
		} catch (Exception e) {
			Log.debug(e);
		}
	}

	/**
	 * Open file without exception handling
	 * @param strURL file URL
	 * @throws IOException for invalid URL
	 */
	public void openFileUnsafe(String strURL) throws IOException {
		String lowerCase = StringUtil.toLowerCaseUS(strURL);
		URL url = new URL(strURL);
		((AppD) app).loadXML(url, lowerCase
				.endsWith(FileExtensions.GEOGEBRA_TOOL.toString()));
	}

	/*
	 * saves a PNG file signed applets only
	 */
	@Override
	public synchronized boolean writePNGtoFile(String filename,
			final double exportScale, final boolean transparent,
			final double DPI0, final boolean greyscale) {
		
		final double DPI = DPI0 <= 0 ? 72 : DPI0;

		File file1 = null;
		try {
			file1 = new File(filename);
		} catch (Throwable t) {
			Log.debug(t);
		}
		if (file1 == null) {
			return false;
		}
		final File file = file1;
		try {
			// draw graphics view into image
			GBufferedImage img = getApplication()
					.getActiveEuclidianView()
					.getExportImage(exportScale, transparent,
							ExportType.PNG);

			if (greyscale) {
				((GBufferedImageD) img).convertToGrayscale();
			}

			// write image to file
			MyImageIO.write(
					GBufferedImageD.getAwtBufferedImage(img),
					"png", (float) DPI, file);

			return true;
		} catch (IOException | RuntimeException | Error ex) {
			Log.debug(ex);
			return false;
		}
	}

	@Override
	protected void exportPNGClipboard(boolean transparent, int DPI,
			double exportScale, EuclidianView ev) {
		// more control but doesn't paste into eg Paint, Google Docs
		GraphicExportDialog.exportPNGClipboard(transparent, DPI, exportScale,
				(AppD) app, (EuclidianViewInterfaceD) ev);
	}

	@Override
	protected void exportPNGClipboardDPIisNaN(boolean transparent,
			double exportScale, EuclidianView ev) {
		// pastes into more programs
		GBufferedImage img = ev.getExportImage(exportScale, transparent,
				ExportType.PNG);

		ImageSelection imgSel = new ImageSelection(
				GBufferedImageD.getAwtBufferedImage(img));
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(imgSel,
				null);
	}

	@Override
	protected String base64encodePNG(boolean transparent, double DPI,
			double exportScale, EuclidianView ev) {
		GBufferedImage img = ((EuclidianViewInterfaceD) ev)
				.getExportImage(exportScale, transparent, ExportType.PNG);
		return GBufferedImageD.base64encode(GBufferedImageD.getAwtBufferedImage(img), DPI);
	}

	@Override
	public String prompt(Object promptText, Object initValue) {
		return (String) JOptionPane.showInputDialog(((AppD) app).getFrame(),
				promptText, GeoGebraConstants.APPLICATION_NAME,
				JOptionPane.PLAIN_MESSAGE, null, null, initValue);
	}

	@Override
	public void alert(String message) {
		Localization loc = app.getLocalization();
		Object[] options = { loc.getMenu("StopScript"), loc.getMenu("OK") };
		int n = JOptionPane.showOptionDialog(((AppD) app).getFrame(), message,
				GeoGebraConstants.APPLICATION_NAME, JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, // do not use a custom Icon
				options, // the titles of buttons
				options[0]); // default button title

		if (n == 0) {
			throw new Error("Script stopped by user");
		}

	}

	/**
	 * Returns the dimensions of the real world coordinate system in the
	 * graphics view as [xmin, ymin, width, height]
	 * 
	 * @return dimensions of the real world coordinate system
	 */
	public synchronized Rectangle2D.Double getCoordSystemRectangle() {
		EuclidianView ev = app.getEuclidianView1();
		return new Rectangle2D.Double(ev.getXmin(), ev.getYmin(),
				ev.getXmax() - ev.getXmin(), ev.getYmax() - ev.getYmin());
	}
	
	@Override
	public void exportSVG(String file0, Consumer<String> callback) {
		String filename = file0;

		if (file0 == null) {
			String tempDir = UtilD.getTempDir();
			filename = tempDir + "geogebra.svg";
		}

		File file = new File(filename);

		EuclidianView view = app.getActiveEuclidianView();
		GraphicExportDialog.exportSVG(app, view, file, true,
				view.getExportWidth(),
				view.getExportHeight(), -1, -1, 1, true);
		
		try {
			// read file back as String
			callback.accept(Files.readString(Paths.get(filename)));
		} catch (IOException e) {
			Log.error("problem reading " + filename);
		}
	}

	@Override
	public void exportPDF(double exportScale, String file0, Consumer<String> callback,
			String sliderLabel, double dpi) {

		String filename = file0;

		if (file0 == null) {
			String tempDir = UtilD.getTempDir();
			filename = tempDir + "geogebra.pdf";
		}

		File file = new File(filename);

		EuclidianView view = app.getActiveEuclidianView();
		GraphicExportDialog.exportPDF(view, file, true,
				view.getExportWidth(),
				view.getExportHeight(), exportScale);

		try {
			// read file back as String
			callback.accept(Files.readString(Paths.get(filename)));
		} catch (IOException e) {
			Log.error("problem reading " + filename);
		}
	}

	@Override
	public JsObjectWrapper getWrapper(Object obj) {
		return new JsObjectWrapperD(obj);
	}

}
