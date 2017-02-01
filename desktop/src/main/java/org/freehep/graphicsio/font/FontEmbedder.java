// Copyright 2001-2005 freehep
package org.freehep.graphicsio.font;

import java.awt.Font;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphMetrics;
import java.awt.font.GlyphVector;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

import org.freehep.graphics2d.font.CharTable;

/**
 * A FontIncluder that also embeds all glyphs. Subclasses must implement the
 * <tt>writeGlyph</tt> method which is called for all defined (up to 256)
 * characters and the notdefined character. These method calls are bracketed by
 * <tt>openGlyphs()</tt> and <tt>closeGlyph()</tt>. All invocations of methods
 * that are abstract in this class succeed the method calls of the superclass
 * <tt>FontIncluder</tt> (especially <tt>closeIncludeFont()</tt>!) All of these
 * calls are again succeeded by <tt>closeEmbedFont</tt>. <br>
 * The abstract methods are called in the following order:
 * <ul>
 * <li><tt>openIncludeFont</tt>
 * <li><tt>writeEncoding</tt>
 * <li><tt>closeIncludeFont</tt>
 * <li><tt>writeWidths</tt>
 * <li><tt>openGlyphs</tt>
 * <li>loop over all glyphs: <tt>openGlyphs</tt>
 * <li><tt>closeGlyphs</tt>
 * <li><tt>closeEmbedFont</tt>
 * </ul>
 * 
 * @author Simon Fischer
 * @version $Id: FontEmbedder.java,v 1.4 2009-08-17 21:44:45 murkle Exp $
 */
public abstract class FontEmbedder extends FontIncluder {

	public static final String NOTDEF = ".notdef";

	/**
	 * Writes a single glyph to the file. A null value for <tt>glyphMetrics</tt>
	 * indicates the undefined character. In this case the value of
	 * <tt>unicodeName</tt> equals the value of <tt>NOTDEF</TT> (=
	 * <tt>.notdef</tt>).
	 *
	 * @param unicodeName
	 *            the character's name according to the unicode standard
	 * @param glyph
	 *            the shape that represents this glyph
	 * @param glyphMetrics
	 *            the metrics of this glyph
	 */
	protected abstract void writeGlyph(String unicodeName, Shape glyph,
			GlyphMetrics glyphMetrics) throws IOException;

	/** Writes the character widths to the file. */
	protected abstract void writeWidths(double[] widths) throws IOException;

	/**
	 * Called before the glyph loop starts. Does nothing by default but can be
	 * implemented.
	 */
	protected void openGlyphs() throws IOException {
	}

	/**
	 * Called after the glyph loop ends. Does nothing by default but can be
	 * implemented.
	 */
	protected void closeGlyphs() throws IOException {
	}

	protected abstract void closeEmbedFont() throws IOException;

	private double[] widths;

	private GlyphVector glyphs;

	private Font font; // FONTHACK

	public FontEmbedder(FontRenderContext context) {
		super(context);
	}

	protected double[] getAdvanceWidths() {
		if (widths == null) {
			// figure out the widths of the characters if not yet done
			widths = new double[256];
			for (int i = 0; i < widths.length; i++) {
				widths[i] = glyphs.getGlyphMetrics(i).getAdvance();
				// in case of undefined character set to width of undefined
				// symbol
				if (getCharName(i) == null) {
					widths[i] = getUndefinedWidth();
				}
			}
		}
		return widths;
	}

	protected double getAdvanceWidth(int character) {
		return getAdvanceWidths()[character];
	}

	protected Shape getGlyph(int i) {
		// This one-line implementation produces different results under JDK 1.3
		// and 1.4
		// return glyphs.getGlyphOutline(i);

		// The substitute code attempts to work around this by using defensive
		// programming
		// See code marked FONTHACK elsewhere in this file
		// Create a GlyphVector for this single character.
		FontRenderContext orig = getContext();
		FontRenderContext frc = new FontRenderContext(null,
				orig.isAntiAliased(), orig.usesFractionalMetrics());
		Shape shape = font.createGlyphVector(frc, new char[] { getUnicode(i) })
				.getGlyphOutline(0);
		return orig.getTransform().createTransformedShape(shape);
	}

	protected GlyphMetrics getGlyphMetrics(int i) {
		return glyphs.getGlyphMetrics(i);
	}

	@Override
	public void includeFont(Font font, CharTable charTable, String name)
			throws IOException {

		glyphs = null;
		widths = null;
		// FONTHACK: Needed by hacked version of getGlyph()
		this.font = font;

		super.includeFont(font, charTable, name);

		this.glyphs = font.createGlyphVector(getContext(), getUnicode());

		writeWidths(getAdvanceWidths());

		try {

			openGlyphs();

			// write the glyphs
			for (int i = 0; i < 256; i++) {
				if (getCharName(i) != null) {
					writeGlyph(getCharName(i), getGlyph(i), getGlyphMetrics(i));
				}
			}
			writeGlyph(NOTDEF, createUndefined(), null);

			closeGlyphs();
			closeEmbedFont();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static Shape createUndefined() {
		GeneralPath ud = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 10);
		ud.append(new Rectangle2D.Double(0, 0, FONT_SIZE, FONT_SIZE), false);
		ud.append(new Rectangle2D.Double(FONT_SIZE / 20, FONT_SIZE / 20,
				18 * FONT_SIZE / 20, 18 * FONT_SIZE / 20), false);
		return ud;
	}
}
