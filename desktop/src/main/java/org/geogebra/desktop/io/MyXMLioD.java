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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.imageio.ImageIO;

import org.geogebra.common.jre.gui.MyImageJre;
import org.geogebra.common.jre.headless.AppDI;
import org.geogebra.common.jre.io.MyXMLioJre;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.gui.MyImageD;
import org.geogebra.desktop.util.UtilD;

/**
 * 
 * @author Markus Hohenwarter
 */
public class MyXMLioD extends MyXMLioJre {
	/**
	 * 
	 * @param kernel
	 *            Kernel
	 * @param cons
	 *            Construction
	 */
	public MyXMLioD(Kernel kernel, Construction cons) {
		super(kernel, cons);
	}

	@Override
	protected final void readZip(ZipInputStream zip, boolean isGGTfile)
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
		boolean structureFound = false;

		// get all entries from the zip archive
		while (true) {
			ZipEntry entry = null;
			try {
				entry = zip.getNextEntry();
			} catch (Exception e) {
				Log.error(e.getMessage());
			}
			if (entry == null) {
				break;
			}
			String name = entry.getName();

			if (name.equals("structure.json")) {
				structureFound = true;
			} else if (name.equals(XML_FILE)) {
				// load xml file into memory first
				xmlFileBuffer = UtilD.loadIntoMemory(zip);
				xmlFound = true;
				handler = getGGBHandler();
			} else if (name.equals(XML_FILE_DEFAULTS_2D)) {
				// load defaults xml file into memory first
				defaults2dXmlFileBuffer = UtilD.loadIntoMemory(zip);
				handler = getGGBHandler();
			} else if (app.is3D() && name.equals(XML_FILE_DEFAULTS_3D)) {
				// load defaults xml file into memory first
				defaults3dXmlFileBuffer = UtilD.loadIntoMemory(zip);
				handler = getGGBHandler();
			} else if (name.equals(XML_FILE_MACRO)) {
				// load macro xml file into memory first
				macroXmlFileBuffer = UtilD.loadIntoMemory(zip);
				macroXMLfound = true;
				handler = getGGBHandler();
			} else if (name.equals(JAVASCRIPT_FILE)) {
				// load JavaScript
				kernel.setLibraryJavaScript(UtilD.loadIntoString(zip));
				javaScriptFound = true;
			} else if (StringUtil.toLowerCaseUS(name).endsWith("svg")) {
				String svg = UtilD.loadIntoString(zip);

				MyImageD img = new MyImageD(svg);

				((AppDI) app).addExternalImage(name, img);

			} else {
				// try to load image
				try {
					BufferedImage img = ImageIO.read(zip);
					if ("".equals(name)) {
						Log.warn("image in zip file with empty name");
					} else {
						((AppDI) app).addExternalImage(name, new MyImageD(img));
					}
				} catch (IOException e) {
					Log.debug("readZipFromURL: image could not be loaded: "
							+ name);
					e.printStackTrace();
				}
			}

			// get next entry
			try {
				zip.closeEntry();
			} catch (Exception e) {
				Log.error(e.getMessage());
			}
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

		if (!javaScriptFound && !isGGTfile) {
			kernel.resetLibraryJavaScript();
		}
		if (!(macroXMLfound || xmlFound || structureFound)) {
			throw new Exception("No XML data found in file.");
		}
	}

	@Override
	final protected MyImageJre getExportImage(double width, double height) {
		return ((AppDI) app).getExportImage(THUMBNAIL_PIXELS_X,
				THUMBNAIL_PIXELS_Y);
	}

	@Override
	final protected MyImageJre getExternalImage(String fileName) {
		return ((AppDI) app).getExternalImage(fileName);
	}

	@Override
	final protected void writeImage(MyImageJre img, String ext, OutputStream os)
			throws IOException {
		ImageIO.write((BufferedImage) ((MyImageD) img).getImage(), ext, os);

	}

	/**
	 * Get the preview image of a ggb file.
	 * 
	 * @param file
	 *            file
	 * @throws IOException
	 *             when file is not valid ggb
	 * @return preview image
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
			if (entry == null) {
				break;
			}

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

}
