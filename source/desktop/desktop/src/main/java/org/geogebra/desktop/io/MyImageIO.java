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
