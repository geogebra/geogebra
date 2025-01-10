// Copyright 2002, FreeHEP.
package org.freehep.graphicsio.emf.gdi;

import java.awt.Font;
import java.io.IOException;

import org.freehep.graphicsio.emf.EMFConstants;
import org.freehep.graphicsio.emf.EMFInputStream;
import org.freehep.graphicsio.emf.EMFOutputStream;

/**
 * EMF ExtLogFontW
 * 
 * @author Mark Donszelmann
 * @version $Id: ExtLogFontW.java,v 1.4 2009-08-17 21:44:44 murkle Exp $
 */
public class ExtLogFontW implements EMFConstants {

	private LogFontW font;

	private String fullName;

	private String style;

	private int version;

	private int styleSize;

	private int match;

	private byte[] vendorID;

	private int culture;

	private Panose panose;

	public ExtLogFontW(LogFontW font, String fullName, String style,
			int version, int styleSize, int match, byte[] vendorID, int culture,
			Panose panose) {
		this.font = font;
		this.fullName = fullName;
		this.style = style;
		this.version = version;
		this.styleSize = styleSize;
		this.match = match;
		this.vendorID = vendorID;
		this.culture = culture;
		this.panose = panose;
	}

	public ExtLogFontW(Font font) {
		this.font = new LogFontW(font);
		this.fullName = "";
		this.style = "";
		this.version = 0;
		this.styleSize = 0;
		this.match = 0;
		this.vendorID = new byte[] { 0, 0, 0, 0 };
		this.culture = 0;
		this.panose = new Panose();
	}

	public ExtLogFontW(EMFInputStream emf) throws IOException {
		font = new LogFontW(emf);
		fullName = emf.readWCHAR(64);
		style = emf.readWCHAR(32);
		version = emf.readDWORD();
		styleSize = emf.readDWORD();
		match = emf.readDWORD();
		emf.readDWORD();
		vendorID = emf.readBYTE(4);
		culture = emf.readDWORD();
		panose = new Panose(emf);
		emf.readWORD(); // Pad to 4-byte boundary
	}

	public void write(EMFOutputStream emf) throws IOException {
		font.write(emf);
		emf.writeWCHAR(fullName, 64);
		emf.writeWCHAR(style, 32);
		emf.writeDWORD(version);
		emf.writeDWORD(styleSize);
		emf.writeDWORD(match);
		emf.writeDWORD(0); // reserved
		emf.writeBYTE(vendorID);
		emf.writeDWORD(culture);
		panose.write(emf);
		emf.writeWORD(0);
	}

	@Override
	public String toString() {
		return super.toString() + "\n" + "  LogFontW\n" + font.toString() + "\n"
				+ "    fullname: " + fullName + "\n" + "    style: " + style
				+ "\n" + "    version: " + version + "\n" + "    stylesize: "
				+ styleSize + "\n" + "    match: " + match + "\n"
				+ "    culture: " + culture + "\n" + panose.toString();
	}
}
