// Copyright 2001, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.io.IOException;

import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;
import org.freehep.graphicsio.emf.EMFTag;

/**
 * ExtCreateFontIndirectW TAG.
 * 
 * @author Mark Donszelmann
 * @version $Id: ExtCreateFontIndirectW.java,v 1.5 2009-08-17 21:44:44 murkle
 *          Exp $
 */
public class ExtCreateFontIndirectW extends EMFTag {

	private int index;

	private ExtLogFontW font;

	public ExtCreateFontIndirectW() {
		super(82, 1);
	}

	public ExtCreateFontIndirectW(int index, ExtLogFontW font) {
		this();
		this.index = index;
		this.font = font;
	}

	@Override
	public EMFTag read(int tagID, EMFInputStream emf, int len)
			throws IOException {

		ExtCreateFontIndirectW tag = new ExtCreateFontIndirectW(emf.readDWORD(),
				new ExtLogFontW(emf));
		return tag;
	}

	@Override
	public void write(int tagID, EMFOutputStream emf) throws IOException {
		emf.writeDWORD(index);
		font.write(emf);
	}

	@Override
	public String toString() {
		return super.toString() + "\n" + "  index: 0x"
				+ Integer.toHexString(index) + "\n" + font.toString();
	}
}
