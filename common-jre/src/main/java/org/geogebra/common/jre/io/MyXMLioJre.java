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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.geogebra.common.io.MyXMLHandler;
import org.geogebra.common.io.MyXMLio;
import org.geogebra.common.io.QDParser;
import org.geogebra.common.io.file.ByteArrayZipFile;
import org.geogebra.common.io.file.ZipFile;
import org.geogebra.common.jre.gui.MyImageJre;
import org.geogebra.common.jre.io.file.InputStreamZipFile;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Macro;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.ChartStyle;
import org.geogebra.common.kernel.algos.ChartStyleAlgo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.util.Charsets;
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
	 * @throws Exception
	 *             when file is not accessible / is not valid ggb
	 */
	public final void readZipFromInputStream(InputStream is, boolean isGGTfile)
			throws Exception {

		ZipInputStream zip = new ZipInputStream(is);

		readZip(zip, isGGTfile);

	}

	@Override
	public void readZipFromString(ZipFile zipFile) throws Exception {
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
	 * @throws Exception
	 *             when file is not accessible / is not valid ggb
	 */
	protected abstract void readZip(ZipInputStream zip, boolean isGGTfile)
			throws Exception;

	/**
	 * Handles the XML file stored in buffer.
	 * 
	 * @param buffer
	 *            input buffer
	 * @param clearConstruction
	 *            whether to clear construction
	 * @param isGGTOrDefaults
	 *            whether this is just ggt/defaults (no construction)
	 * @throws Exception
	 *             on parsing error
	 */
	protected void processXMLBuffer(byte[] buffer, boolean clearConstruction,
			boolean isGGTOrDefaults) throws Exception {
		// handle the data in the memory buffer
		ByteArrayInputStream bs = new ByteArrayInputStream(buffer);
		XMLStreamInputStream ir = new XMLStreamInputStream(bs);

		// process xml file
		doParseXML(ir, clearConstruction, isGGTOrDefaults, true, true, true);

		bs.close();
	}

	/**
	 * Reads from a zipped input stream that includes only the construction
	 * saved in xml format.
	 * 
	 * @param is
	 *            input stream
	 * @throws Exception
	 *             on parsing error
	 */
	public final void readZipFromMemory(InputStream is) throws Exception {
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
			throw new Exception(XML_FILE + " not found");
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
	 * @param includeThumbail
	 *            whether to include thumbnail
	 * @throws IOException
	 *             on write error
	 */
	final public void writeGeoGebraFile(OutputStream os,
			boolean includeThumbail) throws IOException {
		boolean isSaving = kernel.isSaving();
		kernel.setSaving(true);

		try {
			// zip stream
			ZipOutputStream zip = new ZipOutputStream(os);
			OutputStreamWriter osw = new OutputStreamWriter(zip,
					Charsets.getUtf8());

			// write construction images
			writeConstructionImages(kernel.getConstruction(), zip);

			// write construction thumbnails
			if (includeThumbail) {
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
		} catch (IOException e) {
			throw e;
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
		// zip stream
		ZipOutputStream zip = new ZipOutputStream(os);
		OutputStreamWriter osw = new OutputStreamWriter(zip,
				Charsets.getUtf8());

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
								Charsets.getUtf8());
						osw.write(image.getSVG());
						osw.flush();
						zip.closeEntry();
					} catch (IOException e) {
						e.printStackTrace();
					}

				} else {
					// BITMAP
					if (image.hasNonNullImplementation()) {
						writeImageToZip(zip, filePath + fileName, image);
					}

				}

			}
			// Save images used in single bars
			AlgoElement algo = geo.getParentAlgorithm();
			if (algo instanceof ChartStyleAlgo) {
				int num = ((ChartStyleAlgo) algo).getIntervals();
				int k;
				for (int i = 0; i < num; i++) {
					k = i + 1;
					ChartStyle algo1 = ((ChartStyleAlgo) algo).getStyle();
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

	final private void writeImageToZip(ZipOutputStream zip, String fileName,
			MyImageJre img) {
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
				new OutputStreamWriter(z, Charsets.getUtf8()));
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
			throws Exception {
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
		 * @throws Exception
		 *             e
		 */
		public Reader getReader() throws Exception;

		/**
		 * @throws Exception
		 *             when closing goes wrong
		 */
		public void closeReader() throws Exception;
	}

	/**
	 *
	 */
	protected static class XMLStreamStringJre implements XMLStreamJre {

		private String str;
		private StringReader rs;

		/**
		 * @param str
		 *            wrapped string
		 */
		public XMLStreamStringJre(String str) {
			this.str = str;
		}

		@Override
		public Reader getReader() throws Exception {
			rs = new StringReader(str);
			return rs;
		}

		@Override
		public void closeReader() throws Exception {
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
		public Reader getReader() throws Exception {
			reader = new InputStreamReader(is, Charsets.getUtf8());
			return reader;
		}

		@Override
		public void closeReader() throws Exception {
			reader.close();
		}
	}
}
