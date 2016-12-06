// Copyright 2001-2005 freehep
package org.freehep.graphicsio.font;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

import org.freehep.graphics2d.font.CharTable;

/**
 * Instances of this class write the information into documents (ps or pdf) that
 * is necessary in order to include or embed fonts. In order to guarantee a
 * time-invariant interface the main methods to implement by subclasses
 * <tt>includeFont</tt> takes no arguments. All necessary data should be
 * available by getter methods which can easily be added. <br>
 * The abstract methods are called in the following order:
 * <ul>
 * <li><tt>openIncludeFont</tt>
 * <li><tt>writeEncoding</tt>
 * <li><tt>closeIncludeFont</tt>
 * </ul>
 * 
 * @author Simon Fischer
 * @version $Id: FontIncluder.java,v 1.4 2009-08-17 21:44:45 murkle Exp $
 */
public abstract class FontIncluder {

	public static final double FONT_SIZE = 1000;

	// -------------------- abstract methods --------------------

	/**
	 * Writes the given information about the font into the file. When this
	 * method is called all <tt>getXXX()</tt> are guaranteed to return
	 * reasonable values.
	 */
	protected abstract void openIncludeFont() throws IOException;

	/** Writes the encoding table to the file. */
	protected abstract void writeEncoding(CharTable charTable)
			throws IOException;

	/** Does nothing, but can be implemented by subclasses if necessary. */
	protected void closeIncludeFont() throws IOException {
	}

	// -----------------------------------------------------------

	private FontRenderContext context;

	private Rectangle2D fontBBox;

	private Font font;

	private String fontName;

	private CharTable charTable;

	private char[] unicode;

	private String[] charName;

	private int noDefinedChars;

	public FontIncluder(FontRenderContext context) {
		this.context = context;
		this.noDefinedChars = -1;
	}

	// -----------------------------------------------------------

	protected FontRenderContext getContext() {
		return context;
	}

	protected String getFontName() {
		return fontName;
	}

	protected Font getFont() {
		return font;
	}

	protected CharTable getEncodingTable() {
		return charTable;
	}

	protected Rectangle2D getFontBBox() {
		return fontBBox;
	}

	protected String getCharName(int i) {
		return charName[i];
	}

	protected char getUnicode(int i) {
		return unicode[i];
	}

	protected char[] getUnicode() {
		return unicode;
	}

	protected int getNODefinedChars() {
		return noDefinedChars;
	}

	// -----------------------------------------------------------

	/**
	 * Embed this font to the file.
	 * 
	 * @param font
	 *            The font to include
	 * @param name
	 *            The name under which this font is addressed within the
	 *            document (can be retrieved by <tt>getFontName()</tt>)
	 */
	public void includeFont(Font font, CharTable charTable, String name)
			throws IOException {

		unicode = null;
		charName = null;

		this.font = font;
		this.charTable = charTable;
		this.fontName = name;

		// figure out the maximum bounding box for all characters
		this.fontBBox = font.getMaxCharBounds(context);

		// figure out the unicodes and character names and
		// create a glyph vector containing the 256 glyphs of the font
		this.noDefinedChars = 0;
		this.unicode = new char[256];
		this.charName = new String[256];
		for (int i = 0; i < unicode.length; i++) {
			charName[i] = charTable.toName(i);
			if (charName[i] != null) {
				unicode[i] = charTable.toUnicode(charName[i]);
				noDefinedChars++;
			} else {
				unicode[i] = 0;
			}
		}

		openIncludeFont();
		writeEncoding(charTable);
		closeIncludeFont();
	}

	protected double getUndefinedWidth() {
		return FONT_SIZE;
	}
}
