// Copyright 2001-2005 freehep
package org.freehep.graphicsio.font;

import java.awt.Font;
import java.awt.font.TextAttribute;
import java.io.IOException;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;

import org.freehep.graphics2d.font.CharTable;
import org.freehep.graphics2d.font.Lookup;

/**
 * A table to remember which fonts were used while writing a document.
 * 
 * @author Simon Fischer
 * @version $Id: FontTable.java,v 1.5 2009-08-17 21:44:45 murkle Exp $
 */
public abstract class FontTable {

	protected class Entry {
		private Font font;

		private String ref;

		private CharTable encoding;

		private boolean written;

		private Entry(Font f, CharTable encoding) {
			// get attributes of font for the stored default font
			Map/* <TextAttribute,?> */ attributes = f.getAttributes();

			// set default font size
			attributes.put(TextAttribute.SIZE,
					new Float(FontEmbedder.FONT_SIZE));

			// remove font transformations
			attributes.remove(TextAttribute.TRANSFORM);
			attributes.remove(TextAttribute.SUPERSCRIPT);

			this.font = new Font(attributes);

			this.ref = createFontReference(this.font);
			this.encoding = encoding;
			this.written = false;
		}

		public Font getFont() {
			return font;
		}

		public String getReference() {
			return ref;
		}

		protected void setReference(String ref) {
			this.ref = ref;
		}

		public CharTable getEncoding() {
			return encoding;
		}

		public void setWritten(boolean written) {
			this.written = written;
		}

		public boolean isWritten() {
			return written;
		}

		@Override
		public String toString() {
			return ref + "=" + font;
		}
	}

	private Hashtable table;

	public FontTable() {
		this.table = new Hashtable();
	}

	/**
	 * Returns a default CharTable to be used for normal text (not Symbol or
	 * Dingbats).
	 */
	public abstract CharTable getEncodingTable();

	/**
	 * Called whenever a specific font is used for the first time. Subclasses
	 * may use this method to include the font instantly. This method may change
	 * the value of the reference by calling <tt>e.setReference(String)</tt>
	 * e.g. if it wants to substitute the font by a standard font that can be
	 * addressed under a name different from the generated one.
	 */
	protected abstract void firstRequest(Entry e, boolean embed, String embedAs)
			throws IOException;

	/** Creates a unique reference to address this font. */
	protected abstract String createFontReference(Font f);

	protected abstract Font substituteFont(Font font);

	/**
	 * Returns a name for this font that can be used in the document. A new name
	 * is generated if the font was not used yet. For different fontsizes the
	 * same name is returned.
	 */
	public String fontReference(Font font, boolean embed, String embedAs) {
		// look for stored font
		font = substituteFont(font);
		String key = getKey(font);
		Entry e = (Entry) table.get(key);

		// create new one
		if (e == null) {
			e = new Entry(font, getEncodingTable(font));
			try {
				firstRequest(e, embed, embedAs);
			} catch (IOException exc) {
				exc.printStackTrace();
			}
			table.put(key, e);
		}

		return e.ref;
	}

	/**
	 * To embed all derivations of a font too (with underline, strikethrough
	 * etc.) the key consists all these attributes.
	 *
	 * @param font
	 *            ist attributes are used
	 * @return something like Helvetica[BOLD:1][ITALIC:0][UNDERLINE:1]
	 */
	private static String getKey(Font font) {
		Map/* <TextAttribute,?> */ attributes = font.getAttributes();

		StringBuffer result = new StringBuffer(font.getName());

		// bold
		result.append("[WEIGHT:");
		result.append(attributes.get(TextAttribute.WEIGHT));
		result.append("]");

		// italic
		result.append("[POSTURE:");
		result.append(attributes.get(TextAttribute.POSTURE));
		result.append("]");

		// underline is not handled as an font property
		// result.append("[UNDERLINE:");
		// result.append(attributes.get(TextAttribute.UNDERLINE));
		// result.append("]");

		// strike through is not handled as an font property
		// result.append("[STRIKETHROUGH:");
		// result.append(attributes.get(TextAttribute.STRIKETHROUGH));
		// result.append("]");

		// SUPERSCRIPT is apllied by font.getTransformation()
		// leave this as a reminder!
		// result.append("[");
		// result.append(attributes.get(TextAttribute.SUPERSCRIPT));
		// result.append("]");

		// width is not handled as an font property
		// result.append("[WIDTH:");
		// result.append(attributes.get(TextAttribute.WIDTH));
		// result.append("]");

		return result.toString();
	}

	/**
	 * creates a normalized attribute map, e.g.
	 * java.awt.Font[family=Dialog,name=dialog.bold,style=plain,size=20] becomes
	 * java.awt.Font[family=Dialog,name=Dialog,style=bold,size=20]
	 *
	 * @param attributes
	 */
	public static void normalize(Map/* <TextAttribute,?> */ attributes) {
		// get name
		String family = (String) attributes.get(TextAttribute.FAMILY);

		// Java font names could end with ".plain" ".bold"
		// and ".italic". We have to convert this to an
		// attribute first
		if (family.toLowerCase().endsWith(".bold")) {
			attributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
			// cut the ".bold"
			int pos = family.toLowerCase().indexOf(".bold");
			family = family.substring(0, pos);
		} else if (family.toLowerCase().endsWith(".italic")) {
			attributes.put(TextAttribute.POSTURE,
					TextAttribute.POSTURE_OBLIQUE);
			// cut the ".italic"
			int pos = family.toLowerCase().indexOf(".bold");
			family = family.substring(0, pos);
		} else if (family.toLowerCase().endsWith(".plain")) {
			// cut the ".plain"
			int pos = family.toLowerCase().indexOf(".plain");
			family = family.substring(0, pos);
		}

		// first character up
		family = family.substring(0, 1).toUpperCase()
				+ family.substring(1, family.length());
		attributes.put(TextAttribute.FAMILY, family);
	}

	/**
	 * Returns a Collection view of all fonts. The elements of the collection
	 * are <tt>Entrie</tt>s.
	 */
	public Collection getEntries() {
		return table.values();
	}

	private CharTable getEncodingTable(Font font) {
		String fontname = font.getName().toLowerCase();
		if (fontname.indexOf("symbol") >= 0) {
			return Lookup.getInstance().getTable("Symbol");
		}
		if (fontname.indexOf("zapfdingbats") >= 0) {
			return Lookup.getInstance().getTable("Zapfdingbats");
		}
		return getEncodingTable();
	}

}
