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
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;
import javax.swing.text.View;

import org.geogebra.common.util.debug.Log;

/**
 * Useful to match opening and closing keywords from left to right or from right
 * to left
 * 
 * @author Calixte DENIZET
 */
public class MatchingBlockManager {

	private Document doc;
	private GeoGebraEditorPane pane;
	private MatchingBlockScanner scanner;
	private MatchingBlockScanner.MatchingPositions smpos;
	private Highlighter.HighlightPainter ocPainter;
	private Object first;
	private Object second;
	private boolean insideOc;
	private boolean insideKw;
	private boolean ocIncluded;
	private boolean kwIncluded;
	private boolean lr;
	private MouseOverMatcher mouseover;

	/**
	 * Constructor
	 * 
	 * @param doc
	 *            the doc to highlight
	 * @param pane
	 *            the GeoGebraEditorPane associated with this Manager
	 * @param lr
	 *            if true the matching is from left to right
	 */
	public MatchingBlockManager(Document doc, GeoGebraEditorPane pane,
			boolean lr) {
		this.doc = doc;
		this.pane = pane;
		this.scanner = new MatchingBlockScanner(doc);
		this.lr = lr;
	}

	/**
	 * @return the scanner used by thhis manager
	 */
	public MatchingBlockScanner getScanner() {
		return scanner;
	}

	/**
	 * Set the painter for the matching open/close keywords (such as '('/')' or
	 * '['/']'). The contents between the matchings is highlighted.
	 * 
	 * @param filled
	 *            true if a filled rectangle must be used to highlight
	 * @param color
	 *            the color of the painter
	 **/
	public void setPainterForOpenClose(boolean filled, boolean included,
			Color color) {
		this.insideOc = true;
		this.ocIncluded = included;
		update();
		ocPainter = new InsideLinePainter(filled, false, color);
	}

	/**
	 * Set the painter for the matching open/close keywords (such as '('/')' or
	 * '['/']'). The matchings are highlighted.
	 * 
	 * @param type
	 *            one of the three values : GeoGebraKeywordsPainter.FILLED
	 *            GeoGebraKeywordsPainter.UNDERLINED
	 *            GeoGebraKeywordsPainter.FRAMED
	 * @param color
	 *            the color of the painter
	 **/
	public void setPainterForOpenClose(int type, Color color) {
		this.insideOc = false;
		update();
		ocPainter = new GeoGebraKeywordsPainter(color, type);
	}

	/**
	 * Set the painter for the matching open/close keywords (such as '('/')' or
	 * '['/']'). Properties are found in the file scinotesConfiguration.xml The
	 * contents between the matchings is highlighted.
	 **/
	public void setPainterForOpenClose() {
		Parameters param = new Parameters(Color.decode("#40e0d0"), false, false,
				true, GeoGebraKeywordsPainter.FILLED, true);
		if (param.inside) {
			boolean b = param.type == GeoGebraKeywordsPainter.FILLED;
			setPainterForOpenClose(b, param.included, param.color);
		} else {
			setPainterForOpenClose(param.type, param.color);
		}
		if (param.onmouseover) {
			activateMouseOver();
		}
	}

	/**
	 * Set the defaults from scinotesConfiguration.xml
	 */
	public void setDefaults() {
		setPainterForOpenClose();
	}

	/**
	 * Activate this MatchingBlockManager to listen to the KeywordEvent generate
	 * by a MouseOver.
	 */
	public void activateMouseOver() {
		if (mouseover == null) {
			mouseover = new MouseOverMatcher();
		}
		pane.addKeywordListener(mouseover);
	}

	/**
	 * Desactivate this MatchingBlockManager to listen to the KeywordEvent
	 * generate by a MouseOver.
	 */
	public void desactivateMouseOver() {
		if (mouseover != null) {
			pane.removeKeywordListener(mouseover);
			mouseover = null;
		}
	}

	/**
	 * Remove the highlights if they exist.
	 */
	private void update() {
		if (first != null) {
			pane.getHighlighter().removeHighlight(first);
			first = null;
		}
		if (second != null) {
			pane.getHighlighter().removeHighlight(second);
			second = null;
		}
	}

	/**
	 * Search the matching keywords
	 * 
	 * @param tok
	 *            the type of the token at the position pos in the document
	 * @param pos
	 *            the position in the doc
	 */
	public void searchMatchingBlock(int tok, int pos) {
		MatchingBlockScanner.MatchingPositions mpos = null;
		if (tok == GeoGebraLexerConstants.OPENCLOSE) {
			mpos = scanner.getMatchingBlock(pos, lr);
		}
		if (mpos != this.smpos) {
			this.smpos = mpos;
			if (first != null) {
				pane.getHighlighter().removeHighlight(first);
				if (second != null) {
					pane.getHighlighter().removeHighlight(second);
				}
			}
			if (mpos != null && tok == GeoGebraLexerConstants.OPENCLOSE) {
				createHighlights(mpos, insideOc, ocIncluded, ocPainter);
			} else if (mpos != null) {
				createHighlights(mpos, insideKw, kwIncluded, null);
			}
		}
	}

	/**
	 * Create the highlights
	 * 
	 * @param mpos
	 *            the position of the matching keywords
	 * @param inside
	 *            true if we look at the contents between the keywords
	 * @param hp
	 *            the painter to use
	 */
	private void createHighlights(MatchingBlockScanner.MatchingPositions mpos,
			boolean inside, boolean included, Highlighter.HighlightPainter hp) {
		try {
			if (!inside) {
				first = pane.getHighlighter().addHighlight(mpos.firstB, mpos.firstE, hp);
				second = pane.getHighlighter().addHighlight(mpos.secondB, mpos.secondE,
						hp);
			} else {
				if (lr) {
					if (included) {
						first = pane.getHighlighter().addHighlight(mpos.firstB,
								mpos.secondE, hp);
					} else {
						first = pane.getHighlighter().addHighlight(mpos.firstE,
								mpos.secondB, hp);
					}
				} else {
					if (included) {
						first = pane.getHighlighter().addHighlight(mpos.secondB,
								mpos.firstE, hp);
					} else {
						first = pane.getHighlighter().addHighlight(mpos.secondE,
								mpos.firstB, hp);
					}
				}
			}
		} catch (Exception e) {
			Log.error(e.getMessage());
		}
	}

	/**
	 * Inner class used to retriev infos from scinotesConfiguration.xml
	 */
	public static class Parameters {

		/**
		 * The color
		 */
		public Color color;

		/**
		 * Inside or not
		 */
		public boolean inside;

		/**
		 * Strict or not
		 */
		public boolean strict;

		/**
		 * Included or not
		 */
		public boolean included;

		/**
		 * The type
		 */
		public int type;

		/**
		 * The onmouseover
		 */
		public boolean onmouseover;

		/**
		 * Constructor
		 * 
		 * @param color
		 *            the color
		 * @param inside
		 *            inside or not
		 * @param type
		 *            the type
		 * @param onmouseover
		 *            a boolean
		 */
		public Parameters(Color color, boolean inside, boolean strict,
				boolean included, int type, boolean onmouseover) {
			this.color = color;
			this.inside = inside;
			this.type = type;
			this.strict = strict;
			this.included = included;
			this.onmouseover = onmouseover;
		}
	}

	/**
	 * Inner class to highlight matching keywords
	 */
	public class GeoGebraKeywordsPainter
			extends DefaultHighlighter.DefaultHighlightPainter {

		/**
		 * FILLED
		 */
		public static final int FILLED = 0;

		/**
		 * UNDERLINED
		 */
		public static final int UNDERLINED = 1;

		/**
		 * FRAMED
		 */
		public static final int FRAMED = 2;

		private Color color;
		private int type;

		/**
		 * Constructor
		 * 
		 * @param color
		 *            the color to paint
		 * @param type
		 *            must be FILLED, UNDERLINED or FRAMED
		 */
		public GeoGebraKeywordsPainter(Color color, int type) {
			super(color);
			this.color = color;
			this.type = type;
		}

		/**
		 * paintLayer
		 * 
		 * @param g
		 *            Graphics
		 * @param offs0
		 *            the beginning
		 * @param offs1
		 *            the end
		 * @param bounds
		 *            the bounds
		 * @param c
		 *            the text component where to paint
		 * @param view
		 *            the view to use
		 * @return the shape containg the highlighted text
		 */
		@Override
		public Shape paintLayer(Graphics g, int offs0, int offs1, Shape bounds,
				JTextComponent c, View view) {
			try {
				Rectangle r = (Rectangle) view.modelToView(offs0,
						Position.Bias.Forward, offs1, Position.Bias.Backward,
						bounds);
				g.setColor(color);

				switch (type) {
				case UNDERLINED:
					g.drawLine(r.x, r.y + r.height - 1, r.x + r.width - 1,
							r.y + r.height - 1);
					return r;
				case FRAMED:
					g.drawRect(r.x, r.y, r.width - 1, r.height - 1);
					return r;
				case FILLED:
				default:
					g.fillRect(r.x, r.y, r.width, r.height);
					return r;
				}
			} catch (BadLocationException e) {
				return null;
			}
		}
	}

	/**
	 * Inner class to highlight the content inside two keywords. The highlight
	 * depends on the position of the content.
	 */
	class InsideLinePainter implements Highlighter.HighlightPainter {

		private boolean filled;
		private boolean strict;
		private Color color;

		/**
		 * Constructor
		 * 
		 * @param filled
		 *            if the highlighted rectangle must be filled
		 * @param color
		 *            the color to paint
		 */
		protected InsideLinePainter(boolean filled, boolean strict,
				Color color) {
			this.filled = filled;
			this.strict = strict;
			this.color = color;
		}

		/**
		 * Implements a strategy to render contents depending on the position of
		 * these
		 * 
		 * @param g
		 *            Graphics
		 * @param pos0
		 *            the beginning
		 * @param pos1
		 *            the end
		 * @param bounds
		 *            the bounds
		 * @param c
		 *            the text component where to paint
		 */
		@Override
		public void paint(Graphics g, int pos0, int pos1, Shape bounds,
				JTextComponent c) {
			try {
				Rectangle alloc = bounds.getBounds();
				Rectangle p0 = c.modelToView(pos0);
				Rectangle p1 = c.modelToView(pos1);
				g.setColor(color);

				if (p0.y == p1.y) {
					Rectangle r = p0.union(p1);
					if (filled) {
						g.fillRect(r.x, r.y, r.width, r.height);
					} else {
						g.drawRect(r.x, r.y, r.width - 1, r.height - 1);
					}
				} else {
					Element root = doc.getDefaultRootElement();
					int line0 = root.getElementIndex(pos0);
					int line1 = root.getElementIndex(pos1);
					Rectangle r0 = c
							.modelToView(root.getElement(line0).getEndOffset());
					Rectangle r1 = c.modelToView(
							root.getElement(line1).getStartOffset());
					if (line0 != line1) {
						if (!strict) {
							if (filled) {
								g.fillRect(p0.x, p0.y, alloc.width, p0.height);
								g.fillRect(alloc.x, p0.y + p0.height,
										alloc.width, r0.y - p0.y - p0.height);

								if (r1.y != p1.y) {
									g.fillRect(r1.x, r1.y, alloc.width,
											r1.height);
								}
								g.fillRect(r1.x, p1.y, p1.x, r1.height);
							} else {
								g.drawRect(p0.x, p0.y, alloc.width - 1,
										p0.height - 1);
								g.drawRect(alloc.x, p0.y + p0.height,
										alloc.width - 1,
										r0.y - p0.y - p0.height - 1);

								if (r1.y != p1.y) {
									g.drawRect(r1.x, r1.y, alloc.width,
											r1.height - 1);
								}
								g.drawRect(r1.x, p1.y, p1.x, r1.height - 1);
							}
						}

						if (filled) {
							g.fillRect(alloc.x, r0.y, alloc.width, r1.y - r0.y);
						} else {
							g.drawRect(alloc.x, r0.y, alloc.width - 1,
									r1.y - r0.y - 1);
						}
					} else {
						/*
						 * This part of the code has been copied (for the
						 * filling) from DefaultHighlighter.java
						 */
						int w = alloc.x + alloc.width - p0.x;
						if (filled) {
							g.fillRect(p0.x, p0.y, w, p0.height);
							if ((p0.y + p0.height) != p1.y) {
								g.fillRect(alloc.x, p0.y + p0.height,
										alloc.width, p1.y - (p0.y + p0.height));
							}
							g.fillRect(alloc.x, p1.y, p1.x - alloc.x,
									p1.height);
						} else {
							g.drawRect(p0.x, p0.y, w - 1, p0.height - 1);
							if ((p0.y + p0.height) != p1.y) {
								g.drawRect(alloc.x, p0.y + p0.height,
										alloc.width - 1,
										p1.y - (p0.y + p0.height) - 1);
							}
							g.drawRect(alloc.x, p1.y, (p1.x - alloc.x) - 1,
									p1.height - 1);
						}
					}
				}
			} catch (BadLocationException e) {
			}
		}
	}

	/**
	 * Inner class to highlight on a KeywordEvent generated by a ONMOUSEOVER
	 */
	class MouseOverMatcher extends KeywordAdapter.MouseOverAdapter {

		/**
		 * What to do when the event occured
		 * 
		 * @param e
		 *            the event
		 */
		@Override
		public void caughtKeyword(KeywordEvent e) {
			if (lr) {
				searchMatchingBlock(e.getType(), e.getStart());
			} else {
				searchMatchingBlock(e.getType(), e.getStart() + e.getLength());
			}
		}
	}
}
