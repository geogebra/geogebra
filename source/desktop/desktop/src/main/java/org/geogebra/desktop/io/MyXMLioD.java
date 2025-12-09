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
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.gui.MyImageD;

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
	protected void loadSVG(String svg, String name) {
		MyImageD img = new MyImageD(svg);
		((AppDI) app).addExternalImage(name, img);
	}

	@Override
	protected void loadBitmap(ZipInputStream zip, String name) {
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
			Log.debug(e);
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