package org.geogebra.desktop.io;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.FileImageOutputStream;

import org.w3c.dom.NodeList;

/**
 * Extends ImageIO.write() in order to specify the DPI (dots per inch) of the
 * image.
 * 
 * @author Markus Hohenwarter
 */
public class MyImageIO {

	public static void write(BufferedImage img, String format, float DPI,
			File outFile) throws IOException {

		Iterator<ImageWriter> it = ImageIO.getImageWritersByFormatName(format);
		ImageWriter writer = it.next();
		FileImageOutputStream fios = new FileImageOutputStream(outFile);
		writer.setOutput(fios);

		writeImage(writer, img, DPI);

		fios.close();
	}

	public static void writeImage(ImageWriter writer, BufferedImage img,
			double DPI) throws IOException {
		float xDPI = (float) DPI;
		float yDPI = (float) DPI;

		ImageWriteParam writeParam = writer.getDefaultWriteParam();
		RenderedImage ri = img;
		// set the DPI
		IIOMetadata destMeta = writer.getDefaultImageMetadata(
				new ImageTypeSpecifier(ri), writeParam);
		IIOMetadataNode destNodes = (IIOMetadataNode) destMeta
				.getAsTree("javax_imageio_1.0");
		NodeList nl = destNodes.getElementsByTagName("Dimension");
		IIOMetadataNode dim;
		if ((nl != null) && (nl.getLength() > 0))
			dim = (IIOMetadataNode) nl.item(0);
		else {
			dim = new IIOMetadataNode("Dimension");
			destNodes.appendChild(dim);
		}
		nl = destNodes.getElementsByTagName("HorizontalPixelSize");
		if ((nl == null) || (nl.getLength() == 0)) {
			IIOMetadataNode horz = new IIOMetadataNode("HorizontalPixelSize");
			dim.appendChild(horz);
			horz.setAttribute("value", Float.toString(xDPI / 25.4f));
		}
		nl = destNodes.getElementsByTagName("VerticalPixelSize");
		if ((nl == null) || (nl.getLength() == 0)) {
			IIOMetadataNode horz = new IIOMetadataNode("VerticalPixelSize");
			dim.appendChild(horz);
			horz.setAttribute("value", Float.toString(yDPI / 25.4f));
		}

		destMeta.setFromTree("javax_imageio_1.0", destNodes);
		writer.write(null, new IIOImage(ri, null, destMeta), writeParam);

		// close everything
		writer.dispose();

	}

	public static BufferedImage read(InputStream input, boolean useCache)
			throws IOException {

		boolean oldUseCache = ImageIO.getUseCache();
		if (oldUseCache != useCache)
			ImageIO.setUseCache(useCache);

		BufferedImage ret = ImageIO.read(input);

		// restore caching to prevent side-effects
		if (oldUseCache != useCache)
			ImageIO.setUseCache(oldUseCache);

		return ret;

	}

}
