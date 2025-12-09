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
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.FileImageOutputStream;

import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.desktop.awt.GBufferedImageD;
import org.w3c.dom.NodeList;

/**
 * Extends ImageIO.write() in order to specify the DPI (dots per inch) of the
 * image.
 * 
 * @author Markus Hohenwarter
 */
public class MyImageIO {

	/**
	 * @param img image
	 * @param DPI scale
	 * @param outFile output file
	 * @throws IOException if I/O error happened
	 */
	public static void write(BufferedImage img, String format, float DPI,
			File outFile) throws IOException {

		Iterator<ImageWriter> it = ImageIO.getImageWritersByFormatName(format);
		ImageWriter writer = it.next();
		FileImageOutputStream fios = new FileImageOutputStream(outFile);
		writer.setOutput(fios);

		GBufferedImageD.writeImage(writer, img, DPI);

		fios.close();
	}
}
