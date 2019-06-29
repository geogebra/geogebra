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
import org.geogebra.common.jre.io.MyXMLioJre;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;

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

	}

	@Override
	final protected MyImageJre getExportImage(double width, double height) {
		return null;
	}

	@Override
	final protected MyImageJre getExternalImage(String fileName) {
		return null;
	}

	@Override
	final protected void writeImage(MyImageJre img, String ext, OutputStream os)
			throws IOException {

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
