/*
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.
This code has been written initially for Scilab (http://www.scilab.org/).

This program is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by
the Free Software Foundation.

 */

package org.geogebra.desktop.gui.editor;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.Map;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Position;
import javax.swing.text.Segment;
import javax.swing.text.Utilities;
import javax.swing.text.WrappedPlainView;

import org.geogebra.common.util.debug.Log;

/**
 *
 * @author Calixte DENIZET
 *
 */
public class GeoGebraView extends WrappedPlainView {

	/**
	 * A tabulation can be rendered with a vertical line.
	 */
	public static final int TABVERTICAL = 0;

	/**
	 * A tabulation can be rendered with a double-chevrons.
	 */
	public static final int TABDOUBLECHEVRONS = 1;

	/**
	 * A tabulation can be rendered with an horizontal line.
	 */
	public static final int TABHORIZONTAL = 2;

	/**
	 * A tabulation can be rendered with a character.
	 */
	public static final int TABCHARACTER = 3;

	private static final String DESKTOPHINTS = "awt.font.desktophints";

	private final ViewContext context;
	private final Lexer lexer;
	private final Document doc;
	private final Segment text = new Segment();

	private int tabType;

	private Map<?, ?> desktopFontHints;
	private boolean enableDesktopFontHints = true;

	private int whiteHeight;

	private boolean unselected = true;

	/**
	 * The constructor to set this view for an element with a context
	 * (containing infos such as colors or fonts of the keywords).
	 * 
	 * @param elem
	 *            the element to view
	 * @param lexer
	 *            the lexer to use
	 * @param context
	 *            used to view the element
	 */
	GeoGebraView(Element elem, Lexer lexer, ViewContext context) {
		super(elem);
		this.context = context;
		this.lexer = lexer;
		this.doc = getDocument();
		lexer.setDocument(doc);
		setTabRepresentation(TABVERTICAL);
	}

	/**
	 * This method can be used to draw anything you want in the editor (such as
	 * the line of maximum recommended chars).
	 * 
	 * @param g
	 *            the graphics where to draw
	 * @param a
	 *            the shape bounding the visible area
	 *            
	 * (overload paint method in WrappedPlainView)
	 */
	@Override
	public void paint(Graphics g, Shape a) {
		super.paint(g, a);
	}

	/**
	 * A trick to be sure that all the line is covered by an highlight
	 * {@inheritDoc}
	 */
	@Override
	public Shape modelToView(int p0, Position.Bias b0, int p1, Position.Bias b1,
			Shape a) throws BadLocationException {
		Rectangle r = (Rectangle) super.modelToView(p0, b0, p1, b1, a);
		r.width = ((Rectangle) a).width;
		return r;
	}

	/**
	 * Very important method since we draw the text in this method !!
	 * 
	 * @param g
	 *            the graphics where to draw
	 * @param sx
	 *            the x-coordinate where to draw
	 * @param sy
	 *            the y-coordinate ... (guess the end of the sentence)
	 * @param p0
	 *            the start of the text in the doc
	 * @param p1
	 *            the end of the text in the doc
	 * @return the x-coordinate where to draw the next piece of text
	 * @throws BadLocationException
	 *             if p0 and p1 are bad positions in the text
	 */
	@Override
	protected float drawUnselectedText(Graphics2D g, float sx, float sy, int p0, int p1)
			throws BadLocationException {
		if (enableDesktopFontHints && desktopFontHints == null) {
			/*
			 * This hint is used to have antialiased fonts in the view in using
			 * the same method (differents way to antialias with LCD screen) as
			 * the desktop.
			 */
			desktopFontHints = (Map<?, ?>) Toolkit.getDefaultToolkit()
					.getDesktopProperty(DESKTOPHINTS);
			calculateHeight(g.getFontRenderContext(),
					context.tokenFont);
			enableDesktopFontHints = desktopFontHints != null;
		}

		if (enableDesktopFontHints) {
			g.addRenderingHints(desktopFontHints);
		}

		g.setFont(context.tokenFont);

		/*
		 * The lexer returns all tokens between the pos p0 and p1. The value of
		 * the returned token determinates the color and the font. The lines can
		 * be broken by the Pane so we must look at previous and next chars to
		 * know if p0 or p1 is "inside" a token.
		 */

		Element elem = doc.getDefaultRootElement();
		Element line = elem.getElement(elem.getElementIndex(p0));

		int prevTok = -1;
		int tok = -1;
		int mark = p0;
		int start = p0;
		float x = sx;
		float y = sy;
		boolean isBroken = false;

		int startL = line.getStartOffset();
		int endL = line.getEndOffset();

		if (startL != start) {
			// we are drawing a broken line
			try {
				lexer.setRange(startL, endL);
				while (startL < start) {
					tok = lexer.scan();
					startL = lexer.start + lexer.yychar() + lexer.yylength();
				}
				isBroken = true;
			} catch (IOException e) {
				Log.debug(e.getMessage());
			}
		}

		if (!isBroken) {
			lexer.setRange(start, endL);
		}

		while (start < p1) {
			try {
				if (!isBroken) {
					tok = lexer.scan();
				} else {
					isBroken = false;
				}
			} catch (IOException e) {
				Log.debug(e.getMessage());
			}

			start = lexer.start + lexer.yychar();
			int end = Math.min(p1, start + lexer.yylength());

			if (end != mark) {
				if (tok != prevTok) {
					if (unselected) {
						g.setColor(context.tokenColors[tok]);
					} else {
						g.setColor(Color.WHITE);
					}
					prevTok = tok;
				}

				doc.getText(mark, end - mark, text);

				float w;

				if ((context.tokenAttrib[tok] & 1) != 0) {
					w = Utilities.getTabbedTextWidth(text, g.getFontMetrics(),
							x, this, mark);
					drawLine(g, x, y + 1, x + w, y + 1);
				}

				if ((context.tokenAttrib[tok] & 2) != 0) {
					w = Utilities.getTabbedTextWidth(text, g.getFontMetrics(),
							x, this, mark);
					drawLine(g, x, y - whiteHeight, x + w, y - whiteHeight);
				}

				/*
				 * disabled background filling if ((context.tokenAttrib[tok] &
				 * 4) != 0) { w = Utilities.getTabbedTextWidth(text,
				 * g.getFontMetrics(), x, this, mark); FontMetrics fm =
				 * g.getFontMetrics(); int hgt = fm.getHeight(); int desc =
				 * fm.getDescent(); g.fillRect(x, y - hgt, w, hgt + desc);
				 * g.setColor(context.tokenColors[LexerConstants.DEFAULT]); }
				 */

				switch (tok) {
				case LexerConstants.WHITE:
					w = Utilities.getTabbedTextWidth(text,
							g.getFontMetrics(), x, this, mark);
					drawLine(g, x + (w - 1) / 2, y - whiteHeight,
							x + (w + 1) / 2, y - whiteHeight);
					break;
				case LexerConstants.TAB:
					paintTab(text, x, y, g, mark);
					break;
				case LexerConstants.UNKNOWN:
					w = Utilities.getTabbedTextWidth(text, g.getFontMetrics(),
							x, this, mark);
					for (int i = 0; i < w; i += 4) {
						drawLine(g, x + i, y + 2, x + i + 1, y + 2);
					}
					for (int i = 2; i < w; i += 4) {
						drawLine(g, x + i, y + 1, x + i + 1, y + 1);
					}
					break;
				default:
					break;
				}

				x = Utilities.drawTabbedText(text, x, y, g, this, mark);
				mark = end;
			}

			start = end;
		}

		return x;
	}

	private void drawLine(Graphics2D g, float x1, float y1, float x2, float y2) {
		g.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
	}

	/**
	 * Draw the selected text.
	 * 
	 * @param g
	 *            the graphics where to draw
	 * @param x
	 *            the x-coordinate where to draw
	 * @param y
	 *            the y-coordinate ... (guess the end pf the sentence)
	 * @param p0
	 *            the start of the text in the doc
	 * @param p1
	 *            the end of the text in the doc
	 * @return the x-coordinate where to draw the next piece of text
	 * @throws BadLocationException
	 *             if p0 and p1 are bad positions in the text
	 */
	@Override
	protected float drawSelectedText(Graphics2D g, float x, float y, int p0, int p1)
			throws BadLocationException {
		unselected = false;
		float z = drawUnselectedText(g, x, y, p0, p1);
		unselected = true;
		return z;
	}

	/**
	 * Used to give the way to represent a tabulation. By default TABVERTICAL is
	 * used.
	 * 
	 * @param type
	 *            must be TABVERTICAL or TABDOUBLECHEVRONS or TABHORIZONTAL If a
	 *            bad value is given, then nothing will be drawn
	 */
	public void setTabRepresentation(int type) {
		this.tabType = type;
	}

	/**
	 * Method to paint a tabulation according to the setTabRepresentation.
	 * 
	 * @param text
	 *            the segment of text representing a tabulation
	 * @param x
	 *            the x-coordinate where to draw
	 * @param y
	 *            the y-coordinate where to draw
	 * @param g
	 *            the graphics ... (yeah ! once again)
	 * @param start
	 *            the position in the document
	 */
	protected void paintTab(Segment text, float x, float y, Graphics2D g, int start) {
		FontMetrics fm = g.getFontMetrics();
		float w = Utilities.getTabbedTextWidth(text, fm, x, this, start);
		String tabCharacter = " ";
		switch (tabType) {
		case TABVERTICAL:
			drawLine(g, x, y + 4, x, y + 4 - fm.getHeight());
			break;
		case TABDOUBLECHEVRONS:
			g.drawString("\u00BB", x, y);
			break;
		case TABHORIZONTAL:
			drawLine(g, x, y - whiteHeight, x + w - 1, y - whiteHeight);
			break;
		case TABCHARACTER:
			g.drawString(tabCharacter, x, y);
			break;
		default:
		}
	}

	/**
	 * Determines the height of a '+' to have the vertical shift to draw a
	 * line which strokes the text or to draw the mark let by a white.
	 * 
	 * @param frc
	 *            a font context
	 * @param f
	 *            the font where to take the '+'
	 */
	private void calculateHeight(FontRenderContext frc, Font f) {
		TextLayout layout = new TextLayout("+", f, frc);
		Rectangle2D rectangle = layout.getBounds();
		whiteHeight = (int) Math.round(-rectangle.getY() / 2);
	}
}
