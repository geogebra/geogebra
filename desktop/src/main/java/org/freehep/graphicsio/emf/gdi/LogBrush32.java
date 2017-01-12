// Copyright 2002, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.awt.Color;
import java.io.IOException;

import org.freehep.graphicsio.emf.EMFConstants;
import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;

/**
 * EMF LogBrush32
 * 
 * @author Mark Donszelmann
 * @version $Id: LogBrush32.java,v 1.4 2009-08-17 21:44:44 murkle Exp $ see
 *          http://msdn.microsoft.com/library/default.asp?url=/library/en-us/gdi
 *          /brushes_8yk2.asp
 */
public class LogBrush32 implements EMFConstants {

	private int style;

	private Color color;

	private int hatch;

	public LogBrush32(int style, Color color, int hatch) {
		this.style = style;
		this.color = color;
		this.hatch = hatch;
	}

	public LogBrush32(EMFInputStream emf) throws IOException {
		style = emf.readUINT();
		color = emf.readCOLORREF();
		hatch = emf.readULONG();
	}

	public void write(EMFOutputStream emf) throws IOException {
		emf.writeUINT(style);
		emf.writeCOLORREF(color);
		emf.writeULONG(hatch);
	}

	@Override
	public String toString() {
		return "  LogBrush32\n" + "    style: " + style + "\n" + "    color: "
				+ color + "\n" + "    hatch: " + hatch;
	}

	public int getStyle() {
		return style;
	}

	public Color getColor() {
		return color;
	}

	public int getHatch() {
		return hatch;
	}
}
