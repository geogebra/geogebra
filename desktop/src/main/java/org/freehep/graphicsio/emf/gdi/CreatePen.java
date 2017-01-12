// Copyright 2001, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.io.IOException;

import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;
import org.freehep.graphicsio.emf.EMFTag;

/**
 * CreatePen TAG.
 * 
 * @author Mark Donszelmann
 * @version $Id: CreatePen.java,v 1.5 2009-08-17 21:44:44 murkle Exp $
 */
public class CreatePen extends EMFTag {

	private int index;

	private LogPen pen;

	public CreatePen() {
		super(38, 1);
	}

	public CreatePen(int index, LogPen pen) {
		this();
		this.index = index;
		this.pen = pen;
	}

	@Override
	public EMFTag read(int tagID, EMFInputStream emf, int len)
			throws IOException {

		CreatePen tag = new CreatePen(emf.readDWORD(), new LogPen(emf));
		return tag;
	}

	@Override
	public void write(int tagID, EMFOutputStream emf) throws IOException {
		emf.writeDWORD(index);
		pen.write(emf);
	}

	@Override
	public String toString() {
		return super.toString() + "\n" + "  index: 0x"
				+ Integer.toHexString(index) + "\n" + pen.toString();
	}

	public int getIndex() {
		return index;
	}

	public LogPen getPen() {
		return pen;
	}

}
