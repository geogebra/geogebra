// Copyright 2002, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.awt.Font;
import java.io.IOException;

import org.freehep.graphicsio.emf.EMFConstants;
import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;

/**
 * EMF LogFontW
 * 
 * @author Mark Donszelmann
 * @version $Id: LogFontW.java,v 1.4 2009-08-17 21:44:44 murkle Exp $
 */
public class LogFontW implements EMFConstants {

	private int height;

	private int width;

	private int escapement;

	private int orientation;

	private int weight;

	private boolean italic;

	private boolean underline;

	private boolean strikeout;

	private int charSet;

	private int outPrecision;

	private int clipPrecision;

	private int quality;

	private int pitchAndFamily;

	private String faceFamily;

	public LogFontW(int height, int width, int escapement, int orientation,
			int weight, boolean italic, boolean underline, boolean strikeout,
			int charSet, int outPrecision, int clipPrecision, int quality,
			int pitchAndFamily, String faceFamily) {
		this.height = height;
		this.width = width;
		this.escapement = escapement;
		this.orientation = orientation;
		this.weight = weight;
		this.italic = italic;
		this.underline = underline;
		this.strikeout = strikeout;
		this.charSet = charSet;
		this.outPrecision = outPrecision;
		this.clipPrecision = clipPrecision;
		this.quality = quality;
		this.pitchAndFamily = pitchAndFamily;
		this.faceFamily = faceFamily;
	}

	public LogFontW(Font font) {
		this.height = -font.getSize();
		this.width = 0;
		this.escapement = 0;
		this.orientation = 0;
		this.weight = font.isBold() ? FW_BOLD : FW_NORMAL;
		this.italic = font.isItalic();
		this.underline = false;
		this.strikeout = false;
		this.charSet = 0; // ANSI_CHARSET;
		this.outPrecision = 0; // OUT_DEFAULT_PRECIS;
		this.clipPrecision = 0; // CLIP_DEFAULT_PRECIS;
		this.quality = 5; // CLEARTYPE_QUALITY;
		this.pitchAndFamily = 0;
		this.faceFamily = font.getName();
	}

	public LogFontW(EMFInputStream emf) throws IOException {
		height = emf.readLONG();
		width = emf.readLONG();
		escapement = emf.readLONG();
		orientation = emf.readLONG();
		weight = emf.readLONG();
		italic = emf.readBOOLEAN();
		underline = emf.readBOOLEAN();
		strikeout = emf.readBOOLEAN();
		charSet = emf.readBYTE();
		outPrecision = emf.readBYTE();
		clipPrecision = emf.readBYTE();
		quality = emf.readBYTE();
		pitchAndFamily = emf.readBYTE();
		faceFamily = emf.readWCHAR(32);
	}

	public void write(EMFOutputStream emf) throws IOException {
		emf.writeLONG(height);
		emf.writeLONG(width);
		emf.writeLONG(escapement);
		emf.writeLONG(orientation);
		emf.writeLONG(weight);
		emf.writeBYTE(italic);
		emf.writeBYTE(underline);
		emf.writeBYTE(strikeout);
		emf.writeBYTE(charSet);
		emf.writeBYTE(outPrecision);
		emf.writeBYTE(clipPrecision);
		emf.writeBYTE(quality);
		emf.writeBYTE(pitchAndFamily);
		emf.writeWCHAR(faceFamily, 32);
	}

	@Override
	public String toString() {
		return "  LogFontW\n" + "    height: " + height + "\n" + "    width: "
				+ width + "\n" + "    orientation: " + orientation + "\n"
				+ "    weight: " + weight + "\n" + "    italic: " + italic
				+ "\n" + "    underline: " + underline + "\n"
				+ "    strikeout: " + strikeout + "\n" + "    charSet: "
				+ charSet + "\n" + "    outPrecision: " + outPrecision + "\n"
				+ "    clipPrecision: " + clipPrecision + "\n" + "    quality: "
				+ quality + "\n" + "    pitchAndFamily: " + pitchAndFamily
				+ "\n" + "    faceFamily: " + faceFamily;
	}
}
