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

package org.geogebra.common.jre.io;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.geogebra.common.io.MyXMLHandler;
import org.geogebra.common.io.MyXMLio;
import org.geogebra.common.io.QDParser;
import org.geogebra.common.io.XMLParseException;
import org.geogebra.common.io.file.ByteArrayZipFile;
import org.geogebra.common.io.file.ZipFile;
import org.geogebra.common.jre.gui.MyImageJre;
import org.geogebra.common.jre.io.file.InputStreamZipFile;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Macro;
import org.geogebra.common.kernel.algos.ChartStyle;
import org.geogebra.common.kernel.geos.ChartStyleGeo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;

/**
 * 
 * @author Markus Hohenwarter
 */
public abstract class MyXMLioJre extends MyXMLio {

	// Use the default (non-validating) parser
	// private static XMLReaderFactory factory;

	private QDParser xmlParser;

	/**
	 * @param kernel
	 *            kernel
	 * @param cons
	 *            construction
	 */
	public MyXMLioJre(Kernel kernel, Construction cons) {
		super(kernel, cons);
	}

	@Override
	final protected void createXMLParser() {
		xmlParser = new QDParser();
	}

	/**
	 * Reads zipped file from input stream that includes the construction saved
	 * in xml format and maybe image files.
	 * 
	 * @param is
	 *            input stream
	 * @param isGGTfile
	 *            true for ggt files
	 * @throws XMLParseException if XML is not valid
	 * @throws IOException if stream cannot be read
	 */
	public final void readZipFromInputStream(InputStream is, boolean isGGTfile)
			throws IOException, XMLParseException {

		ZipInputStream zip = new ZipInputStream(is);

		readZip(zip, isGGTfile);

	}

	@Override
	public void readZipFromString(ZipFile zipFile) throws IOException, XMLParseException {
		if (zipFile instanceof ByteArrayZipFile) {
			ByteArrayZipFile byteArrayZipFile = (ByteArrayZipFile) zipFile;
			ZipInputStream zip = new ZipInputStream(
					new ByteArrayInputStream(byteArrayZipFile.getByteArray()));

			readZip(zip, false);
		} else if (zipFile instanceof InputStreamZipFile) {
			InputStreamZipFile inputStreamZipFile = (InputStreamZipFile) zipFile;
			readZipFromInputStream(inputStreamZipFile.getInputStream(), false);
		}
	}

	/**
	 * Reads zipped file from zip input stream that includes the construction
	 * saved in xml format and maybe image files.
	 * 
	 * @param zip
	 *            zip input stream
	 * @param isGGTfile
	 *            true for ggt files
	 * @throws XMLParseException if XML is not valid
	 * @throws IOException if stream cannot be read
	 */
	protected void readZip(ZipInputStream zip, boolean isGGTfile)
			throws IOException, XMLParseException {
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
				xmlFileBuffer = StreamUtil.loadIntoMemory(zip);
				xmlFound = true;
				handler = getGGBHandler();
			} else if (name.equals(XML_FILE_DEFAULTS_2D)) {
				// load defaults xml file into memory first
				defaults2dXmlFileBuffer = StreamUtil.loadIntoMemory(zip);
				handler = getGGBHandler();
			} else if (app.is3D() && name.equals(XML_FILE_DEFAULTS_3D)) {
				// load defaults xml file into memory first
				defaults3dXmlFileBuffer = StreamUtil.loadIntoMemory(zip);
				handler = getGGBHandler();
			} else if (name.equals(XML_FILE_MACRO)) {
				// load macro xml file into memory first
				macroXmlFileBuffer = StreamUtil.loadIntoMemory(zip);
				macroXMLfound = true;
				handler = getGGBHandler();
			} else if (name.equals(JAVASCRIPT_FILE)) {
				// load JavaScript
				kernel.setLibraryJavaScript(StreamUtil.loadIntoString(zip));
				javaScriptFound = true;
			} else if (StringUtil.toLowerCaseUS(name).endsWith("svg")) {
				String svg = StreamUtil.loadIntoString(zip);
				loadSVG(svg, name);
			} else {
				loadBitmap(zip, name);
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
			throw new IOException("No XML data found in file.");
		}
	}

	protected abstract void loadSVG(String svg, String name);

	protected abstract void loadBitmap(ZipInputStream zip, String name);

	/**
	 * Handles the XML file stored in buffer.
	 * 
	 * @param buffer
	 *            input buffer
	 * @param clearConstruction
	 *            whether to clear construction
	 * @param isGGTOrDefaults
	 *            whether this is just ggt/defaults (no construction)
	 * @throws XMLParseException if XML is not valid
	 * @throws IOException if stream cannot be read
	 */
	protected void processXMLBuffer(byte[] buffer, boolean clearConstruction,
			boolean isGGTOrDefaults) throws XMLParseException, IOException {
		// handle the data in the memory buffer
		try (ByteArrayInputStream bs = new ByteArrayInputStream(buffer)) {
			XMLStreamInputStream ir = new XMLStreamInputStream(bs);
			// process xml file
			doParseXML(ir, clearConstruction, isGGTOrDefaults, true, true, true);
		}
	}

	/**
	 * Reads from a zipped input stream that includes only the construction
	 * saved in xml format.
	 * 
	 * @param is
	 *            input stream
	 * @throws XMLParseException if XML is not valid
	 * @throws IOException if stream cannot be read
	 */
	public final void readZipFromMemory(InputStream is) throws IOException, XMLParseException {
		ZipInputStream zip = new ZipInputStream(is);

		// get all entries from the zip archive
		ZipEntry entry = zip.getNextEntry();
		if (entry != null && entry.getName().equals(XML_FILE)) {
			// process xml file
			kernel.getConstruction().setFileLoading(true);
			doParseXML(new XMLStreamInputStream(zip), true, false, true, true,
					false);
			kernel.getConstruction().setFileLoading(false);
			zip.close();
		} else {
			zip.close();
			throw new IOException(XML_FILE + " not found");
		}

	}

	/**
	 * Creates a zipped file containing the construction and all settings saved
	 * in xml format plus all external images.
	 * 
	 * @param file
	 *            output file
	 * @throws IOException
	 *             on write error
	 */
	final public void writeGeoGebraFile(File file) throws IOException {
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
	 * 
	 * @param os
	 *            output stream
	 * @param includeThumbnail
	 *            whether to include thumbnail
	 * @throws IOException
	 *             on write error
	 */
	final public void writeGeoGebraFile(OutputStream os,
			boolean includeThumbnail) throws IOException {
		boolean isSaving = kernel.isSaving();
		kernel.setSaving(true);

		try {
			// zip stream
			ZipOutputStream zip = new ZipOutputStream(os);
			OutputStreamWriter osw = new OutputStreamWriter(zip,
					StandardCharsets.UTF_8);

			// write construction images
			writeConstructionImages(kernel.getConstruction(), zip);

			// write construction thumbnails
			if (includeThumbnail) {
				writeThumbnail(zip, XML_FILE_THUMBNAIL);
			}

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
		} finally {
			kernel.setSaving(isSaving);
		}
	}

	/**
	 * Creates a zipped file containing the given macros in xml format plus all
	 * their external images (e.g. icons).
	 * 
	 * @param file
	 *            output file
	 * @param macros
	 *            list of macros
	 * @throws IOException
	 *             write error
	 */
	final public void writeMacroFile(File file, ArrayList<Macro> macros)
			throws IOException {
		if (macros == null) {
			return;
		}

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
	 * 
	 * @param os
	 *            output stream
	 * @param macros
	 *            list of macros
	 * @throws IOException
	 *             write error
	 */
	final public void writeMacroStream(OutputStream os, ArrayList<Macro> macros)
			throws IOException {
		writeMacroStream(os, macros, macros);
	}

	/**
	 * Writes a zipped file containing the given macros in xml format plus all
	 * their external images (e.g. icons) to the specified output stream.
	 *
	 * @param os
	 *            output stream
	 * @param macros
	 *            list of macros
	 * @param macrosWithImages macros with images
	 * @throws IOException
	 *             write error
	 */
	final public void writeMacroStream(OutputStream os, ArrayList<Macro> macros,
				ArrayList<Macro> macrosWithImages)
			throws IOException {
		// zip stream
		ZipOutputStream zip = new ZipOutputStream(os);
		OutputStreamWriter osw = new OutputStreamWriter(zip,
				StandardCharsets.UTF_8);

		// write images
		writeMacroImages(macrosWithImages, zip);

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
	private void writeConstructionImages(Construction cons1,
			ZipOutputStream zip) {
		writeConstructionImages(cons1, zip, "");
	}

	private void writeConstructionImages(Construction cons1,
			ZipOutputStream zip,
			String filePath) {
		// save all GeoImage images
		// TreeSet images =
		// cons.getGeoSetLabelOrder(GeoElement.GEO_CLASS_IMAGE);
		TreeSet<GeoElement> geos = cons1.getGeoSetLabelOrder();
		if (geos == null) {
			return;
		}

		Iterator<GeoElement> it = geos.iterator();
		while (it.hasNext()) {
			GeoElement geo = it.next();
			String fileName = geo.getImageFileName();
			MyImageJre image = (MyImageJre) geo.getFillImage();
			if (fileName != null && image != null) {

				if (image.isSVG()) {
					// SVG
					try {
						zip.putNextEntry(new ZipEntry(filePath + fileName));
						OutputStreamWriter osw = new OutputStreamWriter(zip,
								StandardCharsets.UTF_8);
						osw.write(image.getSVG());
						osw.flush();
						zip.closeEntry();
					} catch (IOException e) {
						Log.debug(e);
					}

				} else {
					// BITMAP
					if (image.hasNonNullImplementation()) {
						writeImageToZip(zip, filePath + fileName, image);
					}

				}

			}
			// Save images used in single bars
			if (geo instanceof ChartStyleGeo) {
				int num = ((ChartStyleGeo) geo).getIntervals();
				int k;
				for (int i = 0; i < num; i++) {
					k = i + 1;
					ChartStyle algo1 = ((ChartStyleGeo) geo).getStyle();
					if (algo1.getBarImage(k) != null) {
						geo.setImageFileName(
								algo1.getBarImage(k));
						writeImageToZip(zip,
								algo1.getBarImage(k),
								(MyImageJre) geo.getFillImage());
					}
				}
			}
		}
	}

	/**
	 * Writes thumbnail to zip
	 */
	private void writeThumbnail(ZipOutputStream zip, String fileName) {

		// max 128 pixels either way
		/*
		 * double exportScale = Math.min(THUMBNAIL_PIXELS_X /
		 * ev.getSelectedWidth(), THUMBNAIL_PIXELS_X / ev.getSelectedHeight());
		 */

		try {
			// BufferedImage img = app.getExportImage(exportScale);
			MyImageJre img = getExportImage(THUMBNAIL_PIXELS_X,
					THUMBNAIL_PIXELS_Y);
			if (img != null) {
				writeImageToZip(zip, fileName, img);
			}
		} catch (Exception e) {
			// catch error if size is zero
		}

	}

	/**
	 * @param width
	 *            width
	 * @param height
	 *            height
	 * @return image
	 */
	abstract protected MyImageJre getExportImage(double width, double height);

	/**
	 * Writes all images used in the given macros to zip.
	 */
	private void writeMacroImages(ArrayList<Macro> macros,
			ZipOutputStream zip) {
		writeMacroImages(macros, zip, "");
	}

	private void writeMacroImages(ArrayList<Macro> macros, ZipOutputStream zip,
			String filePath) {
		if (macros == null) {
			return;
		}

		for (int i = 0; i < macros.size(); i++) {
			// save all images in macro construction
			Macro macro = macros.get(i);
			writeConstructionImages(macro.getMacroConstruction(), zip,
					filePath);

			// save macro icon
			String fileName = macro.getIconFileName();
			MyImageJre img = getExternalImage(fileName);
			if (img != null && img.hasNonNullImplementation()) {
				writeImageToZip(zip, filePath + fileName, img);
			}
		}
	}

	/**
	 * @param fileName
	 *            file name
	 * @return image
	 */
	abstract protected MyImageJre getExternalImage(String fileName);

	private void writeImageToZip(ZipOutputStream zip, String fileName,
			MyImageJre img) {
		// create new entry in zip archive
		try {
			ZipEntry zipEntry = new ZipEntry(fileName);
			zip.putNextEntry(zipEntry);
		} catch (Exception e) {
			// if the same image file is used more than once in the construction
			// we get a duplicate entry exception: ignore this
			return;
		}

		writeImageToStream(zip, fileName, img);
	}

	/**
	 * Writes an image to stream
	 * 
	 * @param os
	 *            output stream
	 * @param fileName
	 *            filename
	 * @param img
	 *            image
	 */
	final public void writeImageToStream(OutputStream os, String fileName,
			MyImageJre img) {
		// if we get here we need to save the image from the memory
		try {
			// try to write image using the format of the filename extension
			int pos = fileName.lastIndexOf('.');
			String ext = StringUtil.toLowerCaseUS(fileName.substring(pos + 1));
			if ("jpg".equals(ext) || "jpeg".equals(ext)) {
				ext = "JPG";
			} else {
				ext = "PNG";
			}

			writeImage(img, ext, os);
		} catch (Exception e) {
			Log.debug(e.getMessage());
			try {
				// if this did not work save image as png
				writeImage(img, "png", os);
			} catch (Exception ex) {
				Log.debug(ex.getMessage());
				return;
			}
		}
	}

	/**
	 * @param img
	 *            image
	 * @param ext
	 *            file extension
	 * @param os
	 *            output stream
	 * @throws IOException
	 *             write error
	 */
	abstract protected void writeImage(MyImageJre img, String ext,
			OutputStream os) throws IOException;

	/**
	 * Compresses xml String and writes result to os.
	 * 
	 * @param os
	 *            output stream
	 * @param xmlString
	 *            xml as string
	 * @throws IOException
	 *             write error
	 */
	public static void writeZipped(OutputStream os, StringBuilder xmlString)
			throws IOException {
		ZipOutputStream z = new ZipOutputStream(os);
		z.putNextEntry(new ZipEntry(XML_FILE));
		BufferedWriter w = new BufferedWriter(
				new OutputStreamWriter(z, StandardCharsets.UTF_8));
		for (int i = 0; i < xmlString.length(); i++) {
			w.write(xmlString.charAt(i));
		}
		w.close();
		z.close();
	}

	@Override
	final protected void resetXMLParser() {
		xmlParser.reset();
	}

	@Override
	final protected void parseXML(MyXMLHandler xmlHandler, XMLStream stream)
			throws XMLParseException, IOException {
		XMLStreamJre streamJre = (XMLStreamJre) stream;
		xmlParser.parse(xmlHandler, streamJre.getReader());
		streamJre.closeReader();
	}

	/**
	 *
	 */
	public interface XMLStreamJre extends XMLStream {
		/**
		 * @return reader
		 * @throws IOException when reader creation fails
		 */
		public Reader getReader() throws IOException;

		/**
		 * @throws IOException
		 *             when closing goes wrong
		 */
		public void closeReader() throws IOException;
	}

	/**
	 * XML stream from a string.
	 */
	public static class XMLStreamStringJre implements XMLStreamJre {

		private final String str;
		private StringReader rs;

		/**
		 * @param str
		 *            wrapped string
		 */
		public XMLStreamStringJre(String str) {
			this.str = str;
		}

		@Override
		public Reader getReader() {
			rs = new StringReader(str);
			return rs;
		}

		@Override
		public void closeReader() {
			rs.close();
		}
	}

	@Override
	final protected XMLStream createXMLStreamString(String str) {
		return new XMLStreamStringJre(str);
	}

	/**
	 *
	 */
	public static class XMLStreamInputStream implements XMLStreamJre {

		private InputStream is;
		private InputStreamReader reader;

		/**
		 * @param is
		 *            input stream
		 */
		public XMLStreamInputStream(InputStream is) {
			this.is = is;
		}

		@Override
		public Reader getReader() {
			reader = new InputStreamReader(is, StandardCharsets.UTF_8);
			return reader;
		}

		@Override
		public void closeReader() throws IOException {
			reader.close();
		}
	}
}
