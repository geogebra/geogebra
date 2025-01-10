// Copyright 2002, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.awt.Color;
import java.io.IOException;

import org.freehep.graphicsio.emf.EMFConstants;
import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;

/**
 * EMF LogPen
 * 
 * @author Mark Donszelmann
 * @version $Id: LogPen.java,v 1.4 2009-08-17 21:44:44 murkle Exp $
 */
public class LogPen implements EMFConstants {

	private int penStyle;

	private int width;

	private Color color;

	public LogPen(int penStyle, int width, Color color) {
		this.penStyle = penStyle;
		this.width = width;
		this.color = color;
	}

	public LogPen(EMFInputStream emf) throws IOException {
		penStyle = emf.readDWORD();
		width = emf.readDWORD();
		/* int y = */ emf.readDWORD();
		color = emf.readCOLORREF();
	}

	public void write(EMFOutputStream emf) throws IOException {
		emf.writeDWORD(penStyle);
		emf.writeDWORD(width);
		emf.writeDWORD(0);
		emf.writeCOLORREF(color);
	}

	@Override
	public String toString() {
		return "  LogPen\n" + "    penstyle: " + penStyle + "\n" + "    width: "
				+ width + "\n" + "    color: " + color;
	}

	public int getPenStyle() {
		return penStyle;
	}

	public int getWidth() {
		return width;
	}

	public Color getColor() {
		return color;
	}
}
