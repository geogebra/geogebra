// Copyright 2006, FreeHEP.
package org.freehep.graphicsio.emf.gdiplus;

import java.awt.image.RenderedImage;
import java.io.IOException;

import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;

/**
 * The DrawImage metafile record represents a call to Graphics.DrawImage, which
 * draws a bitmap or other image to the drawing surface.
 * 
 * FIXME no 16 bit handling
 * 
 * @author Mark Donszelmann
 * @version $Id: DrawImage.java,v 1.1 2009-08-17 21:44:44 murkle Exp $
 */
public class DrawImage extends EMFPlusTag {

	private RenderedImage image;

	public DrawImage() {
		super(26, 1);
	}

	public DrawImage(int imageIndex, RenderedImage image) {
		this();
		flags = imageIndex;
		this.image = image;
	}

	@Override
	public EMFPlusTag read(int tagID, int flags, EMFInputStream emf, int len)
			throws IOException {
		DrawImage tag = new DrawImage();
		tag.flags = flags;
		emf.readInt(); // image attributes
		emf.readUINT(); // source unit
		emf.readFLOAT(); // X, Y, W, H (src)
		emf.readFLOAT();
		emf.readFLOAT();
		emf.readFLOAT();
		emf.readFLOAT(); // X, Y, W, H (dst)
		emf.readFLOAT();
		emf.readFLOAT();
		emf.readFLOAT();
		return tag;
	}

	@Override
	public void write(int tagID, int flags, EMFOutputStream emf)
			throws IOException {
		emf.writeInt(-1); // image attributes
		emf.writeUINT(0x02); // source unit: pixel
		emf.writeFLOAT(0); // X, Y, W, H (src)
		emf.writeFLOAT(0);
		emf.writeFLOAT(image.getWidth());
		emf.writeFLOAT(image.getHeight());
		emf.writeFLOAT(0); // X, Y, W, H (dst)
		emf.writeFLOAT(0);
		emf.writeFLOAT(image.getWidth());
		emf.writeFLOAT(image.getHeight());
	}
}
