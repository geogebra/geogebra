// Copyright 2002-2003, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import java.io.IOException;

import org.freehep.graphicsio.ImageGraphics2D;
import org.freehep.graphicsio.emf.EMFConstants;
import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;
import org.freehep.graphicsio.emf.EMFTag;
import org.freehep.graphicsio.raw.RawImageWriteParam;
import org.freehep.util.UserProperties;
import org.freehep.util.io.NoCloseOutputStream;

/**
 * PNG and JPG seem not to work.
 * 
 * @author Mark Donszelmann
 * @version $Id: AlphaBlend.java,v 1.5 2009-08-17 21:44:44 murkle Exp $
 */
public class AlphaBlend extends EMFTag implements EMFConstants {

	public final static int size = 108;

	private Rectangle bounds;

	private int x, y, width, height;

	private BlendFunction dwROP;

	private int xSrc, ySrc;

	private AffineTransform transform;

	private Color bkg;

	private int usage;

	private BitmapInfo bmi;

	private RenderedImage image;

	public AlphaBlend() {
		super(114, 1);
	}

	public AlphaBlend(Rectangle bounds, int x, int y, int width, int height,
			AffineTransform transform, RenderedImage image, Color bkg) {
		this();
		this.bounds = bounds;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.dwROP = new BlendFunction(AC_SRC_OVER, 0, 0xFF, AC_SRC_ALPHA);
		this.xSrc = 0;
		this.ySrc = 0;
		this.transform = transform;
		this.bkg = (bkg == null) ? new Color(0, 0, 0, 0) : bkg;
		this.usage = DIB_RGB_COLORS;
		this.image = image;
		this.bmi = null;
	}

	@Override
	public EMFTag read(int tagID, EMFInputStream emf, int len)
			throws IOException {

		AlphaBlend tag = new AlphaBlend();
		tag.bounds = emf.readRECTL(); // 16
		tag.x = emf.readLONG(); // 20
		tag.y = emf.readLONG(); // 24
		tag.width = emf.readLONG(); // 28
		tag.height = emf.readLONG(); // 32
		tag.dwROP = new BlendFunction(emf); // 36
		tag.xSrc = emf.readLONG(); // 40
		tag.ySrc = emf.readLONG(); // 44
		tag.transform = emf.readXFORM(); // 68
		tag.bkg = emf.readCOLORREF(); // 72
		tag.usage = emf.readDWORD(); // 76

		// ignored
		/* int bmiOffset = */ emf.readDWORD(); // 80
		int bmiSize = emf.readDWORD();
		// 84
		/* int bitmapOffset = */ emf.readDWORD(); // 88
		int bitmapSize = emf.readDWORD(); // 92

		/* int width = */ emf.readLONG();
		// 96
		/* int height = */ emf.readLONG(); // 100

		// FIXME: this size can differ and can be placed somewhere else
		bmi = (bmiSize > 0) ? new BitmapInfo(emf) : null;

		// FIXME: need to decode image into java Image.
		/* int[] bytes = */ emf.readUnsignedByte(bitmapSize);
		return tag;
	}

	@Override
	public void write(int tagID, EMFOutputStream emf) throws IOException {
		emf.writeRECTL(bounds);
		emf.writeLONG(x);
		emf.writeLONG(y);
		emf.writeLONG(width);
		emf.writeLONG(height);
		dwROP.write(emf);
		emf.writeLONG(xSrc);
		emf.writeLONG(ySrc);
		emf.writeXFORM(transform);
		emf.writeCOLORREF(bkg);
		emf.writeDWORD(usage);
		emf.writeDWORD(size); // bmi follows this record immediately
		emf.writeDWORD(BitmapInfoHeader.size);
		emf.writeDWORD(size + BitmapInfoHeader.size); // bitmap follows bmi

		emf.pushBuffer();

		int encode;
		// plain
		encode = BI_RGB;
		UserProperties properties = new UserProperties();
		properties.setProperty(RawImageWriteParam.BACKGROUND, bkg);
		properties.setProperty(RawImageWriteParam.CODE, "*BGRA");
		properties.setProperty(RawImageWriteParam.PAD, 1);
		ImageGraphics2D.writeImage(image, "raw", properties,
				new NoCloseOutputStream(emf));

		// emf.writeImage(image, bkg, "*BGRA", 1);
		// png
		// encode = BI_PNG;
		// ImageGraphics2D.writeImage(image, "png", new Properties(), new
		// NoCloseOutputStream(emf));
		// jpg
		// encode = BI_JPEG;
		// ImageGraphics2D.writeImage(image, "jpg", new Properties(), new
		// NoCloseOutputStream(emf));
		int length = emf.popBuffer();

		emf.writeDWORD(length);
		emf.writeLONG(image.getWidth());
		emf.writeLONG(image.getHeight());

		BitmapInfoHeader header = new BitmapInfoHeader(image.getWidth(),
				image.getHeight(), 32, encode, length, 0, 0, 0, 0);
		bmi = new BitmapInfo(header);
		bmi.write(emf);

		emf.append();
	}

	@Override
	public String toString() {
		return super.toString() + "\n" + "  bounds: " + bounds + "\n"
				+ "  x, y, w, h: " + x + " " + y + " " + width + " " + height
				+ "\n" + "  dwROP: " + dwROP + "\n" + "  xSrc, ySrc: " + xSrc
				+ " " + ySrc + "\n" + "  transform: " + transform + "\n"
				+ "  bkg: " + bkg + "\n" + "  usage: " + usage + "\n"
				+ ((bmi != null) ? bmi.toString() : "  bitmap: null");
	}
}
