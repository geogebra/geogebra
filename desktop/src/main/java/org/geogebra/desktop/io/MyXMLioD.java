/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * XMLFileReader.java
 *
 * Created on 09. Mai 2003, 16:05
 */

package org.geogebra.desktop.io;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;

import org.geogebra.common.io.DocHandler;
import org.geogebra.common.io.MyXMLHandler;
import org.geogebra.common.io.QDParser;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Macro;
import org.geogebra.common.kernel.algos.AlgoBarChart;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.gui.GuiManagerD;
import org.geogebra.desktop.gui.MyImageD;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.main.GuiManagerInterfaceD;
import org.geogebra.desktop.util.Util;

/**
 * 
 * @author Markus Hohenwarter
 */
public class MyXMLioD extends org.geogebra.common.io.MyXMLio {

	// Use the default (non-validating) parser
	// private static XMLReaderFactory factory;

	private DocHandler handler;
	private MyXMLHandler ggbDocHandler;
	private QDParser xmlParser;

	public MyXMLioD(Kernel kernel, Construction cons) {
		this.kernel = kernel;
		this.cons = cons;
		app = kernel.getApplication();

		xmlParser = new QDParser();
		handler = getGGBHandler();
	}

	private MyXMLHandler getGGBHandler() {
		if (ggbDocHandler == null)
			// ggb3D : to create also a MyXMLHandler3D
			// ggbDocHandler = new MyXMLHandler(kernel, cons);
			ggbDocHandler = kernel.newMyXMLHandler(cons);
		return ggbDocHandler;
	}

	/**
	 * Reads zipped file from input stream that includes the construction saved
	 * in xml format and maybe image files.
	 */
	public final void readZipFromInputStream(InputStream is, boolean isGGTfile)
			throws Exception {

		ZipInputStream zip = new ZipInputStream(is);

		readZip(zip, isGGTfile);

	}

	/**
	 * Reads zipped file from String that includes the construction saved in xml
	 * format and maybe image files.
	 */
	public final void readZipFromString(byte[] zipFile) throws Exception {

		ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(
				zipFile));

		readZip(zip, false);
	}

	/**
	 * Reads zipped file from zip input stream that includes the construction
	 * saved in xml format and maybe image files.
	 */
	private final void readZip(ZipInputStream zip, boolean isGGTfile)
			throws Exception {

		// we have to read everything (i.e. all images)
		// before we process the XML file, that's why we
		// read the XML file into a buffer first
		byte[] xmlFileBuffer = null;
		byte[] macroXmlFileBuffer = null;
		byte[] defaults2dXmlFileBuffer = null;
		byte[] defaults3dXmlFileBuffer = null;
		boolean xmlFound = false;
		boolean macroXMLfound = false;
		boolean javaScriptFound = false;
		boolean ggbHandler = false;

		// get all entries from the zip archive
		while (true) {
			ZipEntry entry = zip.getNextEntry();
			if (entry == null)
				break;

			String name = entry.getName();
			if (name.equals(XML_FILE)) {
				// load xml file into memory first
				xmlFileBuffer = Util.loadIntoMemory(zip);
				xmlFound = true;
				ggbHandler = true;
				handler = getGGBHandler();
			} else if (name.equals(XML_FILE_DEFAULTS_2D)) {
				// load defaults xml file into memory first
				defaults2dXmlFileBuffer = Util.loadIntoMemory(zip);
				ggbHandler = true;
				handler = getGGBHandler();
			} else if (app.is3D() && name.equals(XML_FILE_DEFAULTS_3D)) {
				// load defaults xml file into memory first
				defaults3dXmlFileBuffer = Util.loadIntoMemory(zip);
				ggbHandler = true;
				handler = getGGBHandler();
			} else if (name.equals(XML_FILE_MACRO)) {
				// load macro xml file into memory first
				macroXmlFileBuffer = Util.loadIntoMemory(zip);
				macroXMLfound = true;
				ggbHandler = true;
				handler = getGGBHandler();
			} else if (name.equals(JAVASCRIPT_FILE)) {
				// load JavaScript
				kernel.setLibraryJavaScript(Util.loadIntoString(zip));
				javaScriptFound = true;
			} else if (name.toLowerCase(Locale.US).endsWith("svg")) {
				String svg = Util.loadIntoString(zip);

				MyImageD img = new MyImageD(svg, name);

				((AppD) app).addExternalImage(name, img);

			} else {
				// try to load image
				try {
					BufferedImage img = ImageIO.read(zip);
					if ("".equals(name)) {
						Log.warn("image in zip file with empty name");
					} else {
						((AppD) app).addExternalImage(name, new MyImageD(img));
					}
				} catch (IOException e) {
					Log.debug("readZipFromURL: image could not be loaded: "
							+ name);
					e.printStackTrace();
				}
			}

			// get next entry
			zip.closeEntry();
		}
		zip.close();

		if (!isGGTfile) {
			// ggb file: remove all macros from kernel before processing
			kernel.removeAllMacros();
		}

		// process macros
		if (macroXmlFileBuffer != null) {
			// don't clear kernel for macro files
			kernel.getConstruction().setFileLoading(true);
			processXMLBuffer(macroXmlFileBuffer, !isGGTfile, isGGTfile);
			kernel.getConstruction().setFileLoading(false);
		}

		// process construction
		if (!isGGTfile && xmlFileBuffer != null) {
			kernel.getConstruction().setFileLoading(true);
			app.getCompanion().resetEuclidianViewForPlaneIds();
			processXMLBuffer(xmlFileBuffer, !macroXMLfound, isGGTfile);
			kernel.getConstruction().setFileLoading(false);
		}
		
		
		// process defaults (after construction for labeling styles)
		if (defaults2dXmlFileBuffer != null) {
			kernel.getConstruction().setFileLoading(true);
			processXMLBuffer(defaults2dXmlFileBuffer, false, true);
			kernel.getConstruction().setFileLoading(false);
		}
		if (defaults3dXmlFileBuffer != null) {
			kernel.getConstruction().setFileLoading(true);
			processXMLBuffer(defaults3dXmlFileBuffer, false, true);
			kernel.getConstruction().setFileLoading(false);
		}

		
		if (!javaScriptFound && !isGGTfile)
			kernel.resetLibraryJavaScript();
		if (!(macroXMLfound || xmlFound))
			throw new Exception("No XML data found in file.");
	}

	/**
	 * Get the preview image of a ggb file.
	 * 
	 * @param file
	 * @throws IOException
	 * @return
	 */
	public final static BufferedImage getPreviewImage(File file)
			throws IOException {
		// just allow preview images for ggb files
		if (!file.getName().endsWith(".ggb")) {
			throw new IllegalArgumentException(
					"Preview image source file has to be of the type .ggb");
		}

		FileInputStream fis = new FileInputStream(file);
		ZipInputStream zip = new ZipInputStream(fis);
		BufferedImage result = null;

		// get all entries from the zip archive
		while (true) {
			ZipEntry entry = zip.getNextEntry();
			if (entry == null)
				break;

			if (entry.getName().equals(XML_FILE_THUMBNAIL)) {
				result = ImageIO.read(zip);
				break;
			}

			// get next entry
			zip.closeEntry();
		}

		zip.close();
		fis.close();

		return result;
	}

	/**
	 * Handles the XML file stored in buffer.
	 * 
	 * @param buffer
	 */
	private void processXMLBuffer(byte[] buffer, boolean clearConstruction,
			boolean isGGTOrDefaults) throws Exception {
		// handle the data in the memory buffer
		ByteArrayInputStream bs = new ByteArrayInputStream(buffer);
		InputStreamReader ir = new InputStreamReader(bs, "UTF8");

		// process xml file
		doParseXML(ir, clearConstruction, isGGTOrDefaults, true, true);

		ir.close();
		bs.close();
	}

	public void parsePerspectiveXML(String perspectiveXML) {
		StringReader ir = new StringReader(perspectiveXML);
		try {
			MyXMLHandler h = getGGBHandler();

			xmlParser.parse(h, ir);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	private void doParseXML(Reader ir, boolean clearConstruction,
			boolean isGGTOrDefaults, boolean mayZoom, boolean settingsBatch)
			throws Exception {
		boolean oldVal = kernel.isNotifyViewsActive();
		boolean oldVal2 = kernel.isUsingInternalCommandNames();
		kernel.setUseInternalCommandNames(true);

		if (!isGGTOrDefaults && mayZoom) {
			kernel.setNotifyViewsActive(false);
		}

		if (clearConstruction) {
			// clear construction
			kernel.clearConstruction(false);
		}

		try {
			kernel.setLoadingMode(true);
			if (settingsBatch && !isGGTOrDefaults) {
				app.getSettings().beginBatch();
				xmlParser.parse(handler, ir);
				app.getSettings().endBatch();
			} else
				xmlParser.parse(handler, ir);
			xmlParser.reset();
			kernel.setLoadingMode(false);
		} catch (Error e) {
			Log.error(e.getMessage());
			if (!isGGTOrDefaults) {
				throw e;
			}
		} catch (Exception e) {
			Log.error(e.getMessage());
			if (!isGGTOrDefaults) {
				throw e;
			}
		} finally {
			kernel.setUseInternalCommandNames(oldVal2);
			if (!isGGTOrDefaults && mayZoom) {
				kernel.updateConstruction();
				kernel.setNotifyViewsActive(oldVal);
			}

			// #2153
			if (!isGGTOrDefaults && cons.hasSpreadsheetTracingGeos()) {
				// needs to be done after call to updateConstruction() to avoid
				// spurious traces
				app.getTraceManager().loadTraceGeoCollection();
			}
		}

		// handle construction step stored in XMLhandler
		// do this only if the construction protocol navigation is showing
		if (!isGGTOrDefaults && oldVal && ((AppD) app).showConsProtNavigation()) {
			// ((GuiManagerD)app.getGuiManager()).setConstructionStep(handler.getConsStep());

			if (((GuiManagerInterfaceD) app.getGuiManager()) != null)
				// if there is a ConstructionProtocolView, then update its
				// navigation bars
				((GuiManagerD) app.getGuiManager())
						.getConstructionProtocolView().setConstructionStep(
								handler.getConsStep());
			else
				// otherwise this is not needed
				app.getKernel().getConstruction()
						.setStep(handler.getConsStep());

		}

	}

	/**
	 * Reads from a zipped input stream that includes only the construction
	 * saved in xml format.
	 */
	public final void readZipFromMemory(InputStream is) throws Exception {
		ZipInputStream zip = new ZipInputStream(is);

		// get all entries from the zip archive
		ZipEntry entry = zip.getNextEntry();
		if (entry != null && entry.getName().equals(XML_FILE)) {
			// process xml file
			kernel.getConstruction().setFileLoading(true);
			doParseXML(new InputStreamReader(zip, "UTF8"), true, false, true,
					true);
			kernel.getConstruction().setFileLoading(false);
			zip.close();
		} else {
			zip.close();
			throw new Exception(XML_FILE + " not found");
		}

	}

	@Override
	public void processXMLString(String str, boolean clearAll,
			boolean isGGTOrDefaults, boolean settingsBatch) throws Exception {
		StringReader rs = new StringReader(str);
		doParseXML(rs, clearAll, isGGTOrDefaults, clearAll, settingsBatch);
		rs.close();
	}

	/**
	 * Creates a zipped file containing the construction and all settings saved
	 * in xml format plus all external images.
	 */
	public void writeGeoGebraFile(File file) throws IOException {
		// create file
		FileOutputStream f = new FileOutputStream(file);
		BufferedOutputStream b = new BufferedOutputStream(f);

		// File Extension for GeoGebra: GGB or GGT
		writeGeoGebraFile(b, true);

		b.close();
		f.close();
	}

	/**
	 * Creates a zipped file containing the construction and all settings saved
	 * in xml format plus all external images. GeoGebra File Format.
	 */
	public void writeGeoGebraFile(OutputStream os, boolean includeThumbail)
			throws IOException {
		boolean isSaving = kernel.isSaving();
		kernel.setSaving(true);

		try {
			// zip stream
			ZipOutputStream zip = new ZipOutputStream(os);
			OutputStreamWriter osw = new OutputStreamWriter(zip, "UTF8");

			// write construction images
			writeConstructionImages(kernel.getConstruction(), zip);

			// write construction thumbnails
			if (includeThumbail)
				writeThumbnail(kernel.getConstruction(), zip,
						XML_FILE_THUMBNAIL);

			// save macros
			if (kernel.hasMacros()) {
				// get all registered macros from kernel
				ArrayList<Macro> macros = kernel.getAllMacros();

				// write all images used by macros
				writeMacroImages(macros, zip);

				// write all macros to one special XML file in zip
				zip.putNextEntry(new ZipEntry(XML_FILE_MACRO));
				osw.write(getFullMacroXML(macros));
				osw.flush();
				zip.closeEntry();
			}

			// write library JavaScript to one special file in zip
			zip.putNextEntry(new ZipEntry(JAVASCRIPT_FILE));
			osw.write(kernel.getLibraryJavaScript());
			osw.flush();
			zip.closeEntry();
			
			// write XML file for defaults
			StringBuilder sb2d = new StringBuilder();
			StringBuilder sb3d = null;
			if (app.is3D()) {
				sb3d = new StringBuilder();
			}
			cons.getConstructionDefaults().getDefaultsXML(sb2d, sb3d);

			zip.putNextEntry(new ZipEntry(XML_FILE_DEFAULTS_2D));
			osw.write(sb2d.toString());
			osw.flush();
			zip.closeEntry();
			if (app.is3D()) {
				zip.putNextEntry(new ZipEntry(XML_FILE_DEFAULTS_3D));
				osw.write(sb3d.toString());
				osw.flush();
				zip.closeEntry();
			}


			// write XML file for construction
			zip.putNextEntry(new ZipEntry(XML_FILE));
			osw.write(getFullXML());
			osw.flush();
			zip.closeEntry();

			osw.close();
			zip.close();
		} catch (IOException e) {
			throw e;
		} finally {
			kernel.setSaving(isSaving);
		}
	}

	/**
	 * Creates a zipped file containing the given macros in xml format plus all
	 * their external images (e.g. icons).
	 */
	public void writeMacroFile(File file, ArrayList<Macro> macros)
			throws IOException {
		if (macros == null)
			return;

		// create file
		FileOutputStream f = new FileOutputStream(file);
		BufferedOutputStream b = new BufferedOutputStream(f);
		writeMacroStream(b, macros);
		b.close();
		f.close();
	}

	/**
	 * Writes a zipped file containing the given macros in xml format plus all
	 * their external images (e.g. icons) to the specified output stream.
	 */
	public void writeMacroStream(OutputStream os, ArrayList<Macro> macros)
			throws IOException {
		// zip stream
		ZipOutputStream zip = new ZipOutputStream(os);
		OutputStreamWriter osw = new OutputStreamWriter(zip, "UTF8");

		// write images
		writeMacroImages(macros, zip);

		// write macro XML file
		zip.putNextEntry(new ZipEntry(XML_FILE_MACRO));
		osw.write(getFullMacroXML(macros));
		osw.flush();
		zip.closeEntry();

		osw.close();
		zip.close();
	}

	/**
	 * Writes all images used in construction to zip.
	 */
	private void writeConstructionImages(Construction cons, ZipOutputStream zip) {
		writeConstructionImages(cons, zip, "");
	}

	private void writeConstructionImages(Construction cons,
			ZipOutputStream zip, String filePath) {
		// save all GeoImage images
		// TreeSet images =
		// cons.getGeoSetLabelOrder(GeoElement.GEO_CLASS_IMAGE);
		TreeSet<GeoElement> geos = cons.getGeoSetLabelOrder();
		if (geos == null)
			return;

		Iterator<GeoElement> it = geos.iterator();
		while (it.hasNext()) {
			GeoElement geo = it.next();
			// Michael Borcherds 2007-12-10 this line put back (not needed now
			// MD5 code put in the correct place!)
			String fileName = geo.getImageFileName();
			MyImageD image = (MyImageD) geo.getFillImage();
			if (fileName != null && image != null) {

				if (image.isSVG()) {
					// SVG
					try {
						zip.putNextEntry(new ZipEntry(filePath + fileName));
						OutputStreamWriter osw = new OutputStreamWriter(zip,
								"UTF8");
						osw.write(image.getSVG());
						osw.flush();
						zip.closeEntry();
					} catch (IOException e) {
						e.printStackTrace();
					}

				} else {
					// BITMAP
					BufferedImage img = (BufferedImage) image.getImage();
					if (img != null)
						writeImageToZip(zip, filePath + fileName, img);

				}

			}
			// Save images used in single bars
			AlgoElement algo = geo.getParentAlgorithm();
			if (algo instanceof AlgoBarChart) {
				int num = ((AlgoBarChart) algo).getIntervals();
				int k;
				for (int i = 0; i < num; i++) {
					k = i + 1;
					if (((AlgoBarChart) algo).getBarImage(k) != null) {
						geo.setImageFileName(((AlgoBarChart) algo)
								.getBarImage(k));
						BufferedImage img = (BufferedImage) ((MyImageD) geo
								.getFillImage()).getImage();
						writeImageToZip(zip,
								((AlgoBarChart) algo).getBarImage(k), img);
					}
				}
			}
		}
	}

	/**
	 * Writes thumbnail to zip. Michael Borcherds 2008-04-18
	 */
	// private void writeThumbnail(Construction cons, ZipOutputStream zip)
	// throws IOException {
	private void writeThumbnail(Construction cons, ZipOutputStream zip,
			String fileName) {

		// max 128 pixels either way
		/*
		 * double exportScale = Math.min(THUMBNAIL_PIXELS_X /
		 * ev.getSelectedWidth(), THUMBNAIL_PIXELS_X / ev.getSelectedHeight());
		 */

		try {
			// BufferedImage img = app.getExportImage(exportScale);
			BufferedImage img = ((AppD) app).getExportImage(THUMBNAIL_PIXELS_X,
					THUMBNAIL_PIXELS_Y);
			if (img != null)
				writeImageToZip(zip, fileName, img);
		} catch (Exception e) {
		} // catch error if size is zero

	}

	/**
	 * Writes all images used in the given macros to zip.
	 */
	private void writeMacroImages(ArrayList<Macro> macros, ZipOutputStream zip) {
		writeMacroImages(macros, zip, "");
	}

	private void writeMacroImages(ArrayList<Macro> macros, ZipOutputStream zip,
			String filePath) {
		if (macros == null)
			return;

		for (int i = 0; i < macros.size(); i++) {
			// save all images in macro construction
			Macro macro = macros.get(i);
			writeConstructionImages(macro.getMacroConstruction(), zip, filePath);

			// save macro icon
			String fileName = macro.getIconFileName();
			MyImageD img = ((AppD) app).getExternalImage(fileName);
			if (img != null && img.getImage() != null) {
				writeImageToZip(zip, filePath + fileName,
						(BufferedImage) img.getImage());
			}
		}
	}

	private void writeImageToZip(ZipOutputStream zip, String fileName,
			BufferedImage img) {
		// create new entry in zip archive
		try {
			zip.putNextEntry(new ZipEntry(fileName));
		} catch (Exception e) {
			// if the same image file is used more than once in the construction
			// we get a duplicate entry exception: ignore this
			return;
		}

		writeImageToStream(zip, fileName, img);
	}

	public void writeImageToStream(OutputStream os, String fileName,
			BufferedImage img) {
		// if we get here we need to save the image from the memory
		try {
			// try to write image using the format of the filename extension
			int pos = fileName.lastIndexOf('.');
			String ext = StringUtil.toLowerCase(fileName.substring(pos + 1));
			if (ext.equals("jpg") || ext.equals("jpeg"))
				ext = "JPG";
			else
				ext = "PNG";

			boolean useCache = true;
			// circumvent security issues by disabling disk-based caching
			if (app.isApplet()) {
				useCache = ImageIO.getUseCache();
				javax.imageio.ImageIO.setUseCache(false);
			}

			ImageIO.write(img, ext, os);

			// restore caching to prevent side-effects
			if (app.isApplet()) {
				javax.imageio.ImageIO.setUseCache(useCache);
			}
		} catch (Exception e) {
			App.debug(e.getMessage());
			try {
				// if this did not work save image as png
				ImageIO.write(img, "png", os);
			} catch (Exception ex) {
				App.debug(ex.getMessage());
				return;
			}
		}
	}

	/**
	 * Compresses xml String and writes result to os.
	 */
	public static void writeZipped(OutputStream os, StringBuilder xmlString)
			throws IOException {
		ZipOutputStream z = new ZipOutputStream(os);
		z.putNextEntry(new ZipEntry(XML_FILE));
		BufferedWriter w = new BufferedWriter(new OutputStreamWriter(z, "UTF8"));
		for (int i = 0; i < xmlString.length(); i++) {
			w.write(xmlString.charAt(i));
		}
		w.close();
		z.close();
	}
}
