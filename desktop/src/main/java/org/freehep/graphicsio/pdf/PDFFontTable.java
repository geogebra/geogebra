// Copyright 2001-2003, FreeHEP.
package org.freehep.graphicsio.pdf;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;

import org.freehep.graphics2d.font.CharTable;
import org.freehep.graphics2d.font.Lookup;
import org.freehep.graphicsio.FontConstants;
import org.freehep.graphicsio.font.FontIncluder;
import org.freehep.graphicsio.font.FontTable;

/**
 * A table to remember which fonts were used while writing a pdf document.
 * Entries to resource dictionaries and embedding of fonts can be done when the
 * drawing is finished by calling <tt>addAll()</tt>.
 * 
 * @author Simon Fischer
 * @version $Id: PDFFontTable.java,v 1.4 2009-08-17 21:44:44 murkle Exp $
 */
public class PDFFontTable extends FontTable {

	private int currentFontIndex = 1;

	private PDFWriter pdf;

	private PDFRedundanceTracker tracker;

	public PDFFontTable(PDFWriter pdf) {
		super();
		this.pdf = pdf;
		this.tracker = new PDFRedundanceTracker(pdf);
	}

	/** Adds all fonts to a dictionary named "FontList". */
	public int addFontDictionary() throws IOException {
		Collection fonts = getEntries();
		if (fonts.size() > 0) {
			PDFDictionary fontList = pdf.openDictionary("FontList");
			for (Iterator i = fonts.iterator(); i.hasNext();) {
				Entry e = (Entry) i.next();
				fontList.entry(e.getReference(), pdf.ref(e.getReference()));
			}
			pdf.close(fontList);
		}
		return fonts.size();
	}

	/** Embeds all not yet embedded fonts to the file. */
	public void embedAll(FontRenderContext context, boolean embed,
			String embedAs) throws IOException {
		Collection col = getEntries();
		Iterator i = col.iterator();
		while (i.hasNext()) {
			Entry e = (Entry) i.next();
			if (!e.isWritten()) {
				e.setWritten(true);

				FontIncluder fontIncluder = null;
				if (PDFFontIncluder.isStandardFont(e.getFont())) {
					embed = false;
				}

				if (embed) {
					if (embedAs.equals(FontConstants.EMBED_FONTS_TYPE3)) {
						fontIncluder = new PDFFontEmbedderType3(context, pdf,
								e.getReference(), tracker);
					} else if (embedAs
							.equals(FontConstants.EMBED_FONTS_TYPE1)) {
						fontIncluder = PDFFontEmbedderType1.create(context, pdf,
								e.getReference(), tracker);
					} else {
						System.out.println(
								"PDFFontTable: invalid value for embedAs: "
										+ embedAs);
					}
				} else {
					fontIncluder = new PDFFontIncluder(context, pdf,
							e.getReference(), tracker);
				}

				if (fontIncluder != null) {
					fontIncluder.includeFont(e.getFont(), e.getEncoding(),
							e.getReference());
				}
			}
		}
		tracker.writeAll();
	}

	@Override
	public CharTable getEncodingTable() {
		return Lookup.getInstance().getTable("PDFLatin");
	}

	@Override
	public void firstRequest(Entry e, boolean embed, String embedAs) {
	}

	private static final Properties replaceFonts = new Properties();
	static {
		replaceFonts.setProperty("Dialog", "Helvetica");
		replaceFonts.setProperty("DialogInput", "Courier");
		replaceFonts.setProperty("Serif", "TimesRoman");
		replaceFonts.setProperty("SansSerif", "Helvetica");
		replaceFonts.setProperty("Monospaced", "Courier");
	}

	@Override
	protected Font substituteFont(Font font) {
		String fontName = replaceFonts.getProperty(font.getName(), null);
		if (fontName != null) {
			Font newFont = new Font(fontName, font.getSize(), font.getStyle());
			font = newFont.deriveFont(font.getSize2D());
		}
		return font;
	}

	/**
	 * Creates the reference by numbering them.
	 * 
	 * @return "F"+currentFontIndex
	 */
	@Override
	protected String createFontReference(Font f) {
		return "F" + (currentFontIndex++);
	}
}
