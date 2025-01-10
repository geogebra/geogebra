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
 * BitBlt TAG. Encoded as plain RGB rather than the not-yet-working PNG format.
 * The BI_code for BI_PNG and BI_JPG seems to be missing from the WINGDI.H file
 * of visual C++.
 * 
 * @author Mark Donszelmann
 * @version $Id: BitBlt.java,v 1.5 2009-08-17 21:44:44 murkle Exp $
 */
public class BitBlt extends EMFTag implements EMFConstants {

	public final static int size = 100;

	private Rectangle bounds;

	private int x, y, width, height;

	private int dwROP;

	private int xSrc, ySrc;

	private AffineTransform transform;

	private Color bkg;

	private int usage;

	private BitmapInfo bmi;

	private RenderedImage image;

	public BitBlt() {
		super(76, 1);
	}

	public BitBlt(Rectangle bounds, int x, int y, int width, int height,
			AffineTransform transform, RenderedImage image, Color bkg) {
		this();
		this.bounds = bounds;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.dwROP = SRCCOPY;
		this.xSrc = 0;
		this.ySrc = 0;
		this.transform = transform;
		this.bkg = bkg;
		this.usage = DIB_RGB_COLORS;
		this.image = image;
		this.bmi = null;
	}

	@Override
	public EMFTag read(int tagID, EMFInputStream emf, int len)
			throws IOException {

		BitBlt tag = new BitBlt();
		tag.bounds = emf.readRECTL(); // 16
		tag.x = emf.readLONG(); // 20
		tag.y = emf.readLONG(); // 24
		tag.width = emf.readLONG(); // 28
		tag.height = emf.readLONG(); // 32
		tag.dwROP = emf.readDWORD(); // 36
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
		emf.writeDWORD(dwROP);
		emf.writeLONG(xSrc);
		emf.writeLONG(ySrc);
		emf.writeXFORM(transform);
		emf.writeCOLORREF(bkg);
		emf.writeDWORD(usage);
		emf.writeDWORD(size); // bmi follows this record immediately
		emf.writeDWORD(BitmapInfoHeader.size);
		emf.writeDWORD(size + BitmapInfoHeader.size); // bitmap follows bmi

		emf.pushBuffer();

		UserProperties properties = new UserProperties();
		properties.setProperty(RawImageWriteParam.BACKGROUND, bkg);
		properties.setProperty(RawImageWriteParam.CODE, "BGR");
		properties.setProperty(RawImageWriteParam.PAD, 4);
		ImageGraphics2D.writeImage(image, "raw", properties,
				new NoCloseOutputStream(emf));

		// emf.writeImage(image, bkg, "BGR", 4);
		int length = emf.popBuffer();

		BitmapInfoHeader header = new BitmapInfoHeader(image.getWidth(),
				image.getHeight(), 24, BI_RGB, length, 0, 0, 0, 0);
		bmi = new BitmapInfo(header);
		bmi.write(emf);

		emf.writeDWORD(length);

		emf.append();
	}

	@Override
	public String toString() {
		return super.toString() + "\n" + "  bounds: " + bounds + "\n"
				+ "  x, y, w, h: " + x + " " + y + " " + width + " " + height
				+ "\n" + "  dwROP: 0x" + Integer.toHexString(dwROP) + "\n"
				+ "  xSrc, ySrc: " + xSrc + " " + ySrc + "\n" + "  transform: "
				+ transform + "\n" + "  bkg: " + bkg + "\n" + "  usage: "
				+ usage + "\n"
				+ ((bmi != null) ? bmi.toString() : "  bitmap: null");
	}
}
