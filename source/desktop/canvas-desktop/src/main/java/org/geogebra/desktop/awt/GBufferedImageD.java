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

package org.geogebra.desktop.awt;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;

import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.common.awt.GGraphics2D;
import org.w3c.dom.NodeList;

public class GBufferedImageD implements GBufferedImage {
	private final BufferedImage impl;

	public GBufferedImageD(int width, int height, int imageType) {
		impl = new BufferedImage(width, height, imageType);
	}

	public GBufferedImageD(BufferedImage image) {
		impl = image;
	}

	@Override
	public int getWidth() {
		return impl.getWidth();
	}

	@Override
	public int getHeight() {
		return impl.getHeight();
	}

	/**
	 * @param im wrapped image
	 * @return native image
	 */
	public static BufferedImage getAwtBufferedImage(GBufferedImage im) {
		if (im == null) {
			return null;
		}
		return ((GBufferedImageD) im).impl;
	}

	@Override
	public GGraphics2D createGraphics() {
		return new GGraphics2DD((Graphics2D) impl.getGraphics());
	}

	@Override
	public GBufferedImage getSubimage(int x, int y, int w, int h) {
		return new GBufferedImageD(impl.getSubimage(x, y, w, h));
	}

	/**
	 * 
	 * @return ARGB pixel data
	 */
	public int[] getData() {
		return ((DataBufferInt) impl.getRaster().getDataBuffer()).getData();
	}

	@Override
	public void flush() {
		impl.flush();
	}

	@Override
	public String getBase64() {
		return "data:image/png;base64," + base64encode(impl, 72);
	}

	/**
	 * convert image to grayscale (monochrome)
	 */
	public void convertToGrayscale() {
		int width = impl.getWidth();
		int height = impl.getHeight();

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int rgb = impl.getRGB(x, y);

				int a = (rgb >> 24) & 0xff;
				int r = (rgb >> 16) & 0xff;
				int g = (rgb >> 8) & 0xff;
				int b = rgb & 0xff;

				int gray = (int) Math.round((r + g + b) / 3d);

				rgb = (a << 24) | (gray << 16) | (gray << 8) | gray;

				impl.setRGB(x, y, rgb);
			}
		}
	}

	/**
	 * @param img
	 *            image
	 * @return encoded image
	 */
	public static String base64encode(BufferedImage img, double DPI) {
		if (img == null) {
			return null;
		}
		try {
			Iterator<ImageWriter> it = ImageIO
					.getImageWritersByFormatName("png");
			ImageWriter writer = it.next();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			ImageOutputStream ios = ImageIO.createImageOutputStream(baos);

			writer.setOutput(ios);

			writeImage(writer, img, DPI);

			String ret = Base64.getEncoder().encodeToString(baos.toByteArray());

			baos.close();
			ios.close();

			return ret;
		} catch (IOException e) {
			Log.debug(e);
			return null;
		}
	}

	/**
	 * @param writer writer
	 * @param img image
	 * @param DPI scale
	 * @throws IOException if I/O error happened
	 */
	public static void writeImage(ImageWriter writer, BufferedImage img,
			double DPI) throws IOException {
		float xDPI = (float) DPI;
		float yDPI = (float) DPI;

		ImageWriteParam writeParam = writer.getDefaultWriteParam();
		// set the DPI
		IIOMetadata destMeta = writer.getDefaultImageMetadata(
				new ImageTypeSpecifier(img), writeParam);
		IIOMetadataNode destNodes = (IIOMetadataNode) destMeta
				.getAsTree("javax_imageio_1.0");
		NodeList nl = destNodes.getElementsByTagName("Dimension");
		IIOMetadataNode dim;
		if ((nl != null) && (nl.getLength() > 0)) {
			dim = (IIOMetadataNode) nl.item(0);
		} else {
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
		writer.write(null, new IIOImage(img, null, destMeta), writeParam);

		// close everything
		writer.dispose();

	}

}
