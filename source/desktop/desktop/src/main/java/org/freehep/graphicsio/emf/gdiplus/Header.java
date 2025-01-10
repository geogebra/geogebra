// Copyright 2006, FreeHEP
package org.freehep.graphicsio.emf.gdiplus;

import java.io.IOException;

import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;

/**
 * The Header metafile record marks the beginning of EMF+ data within the
 * metafile, and contains general information about the metafile itself.
 * 
 * @author Mark Donszelmann
 * @version $Id: Header.java,v 1.1 2009-08-17 21:44:44 murkle Exp $
 */
public class Header extends EMFPlusTag {

	public final static int EMF_PLUS_ONLY = 0x00;
	public final static int EMF_PLUS_DUAL = 0x01;

	private int hDpi = 120;
	private int vDpi = 120;

	public Header() {
		super(1, 1);
	}

	public Header(int type) {
		this();
		this.flags = type;
	}

	@Override
	public EMFPlusTag read(int tagID, int flags, EMFInputStream emf, int len)
			throws IOException {
		Header tag = new Header();
		tag.flags = flags;
		emf.readUINT();
		emf.readUINT();
		tag.hDpi = emf.readUINT();
		tag.vDpi = emf.readUINT();
		return tag;
	}

	@Override
	public void write(int tagID, int flags, EMFOutputStream emf)
			throws IOException {
		emf.writeUINT(0xDBC01001);
		emf.writeUINT(0x00000001);
		emf.writeUINT(hDpi);
		emf.writeUINT(vDpi);
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer(super.toString());
		sb.append("\n");
		sb.append("  hDPI: " + hDpi + "\n");
		sb.append("  vDPI: " + vDpi);
		return sb.toString();
	}
}
