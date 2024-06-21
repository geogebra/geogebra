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

import java.awt.Dimension;
import java.awt.Font;
import java.awt.IllegalComponentStateException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import javax.swing.JEditorPane;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;

import org.geogebra.common.plugin.ScriptType;
import org.geogebra.desktop.gui.GeoGebraKeys;
import org.geogebra.desktop.main.AppD;

/**
 *
 * @author Calixte DENIZET
 *
 */
public class GeoGebraEditorPane extends JEditorPane implements CaretListener,
		MouseListener, MouseMotionListener, FocusListener {

	private static final long serialVersionUID = 1L;

	private Popup helpPopup;

	private final AppD app;
	private final int rows;
	private final int cols;
	private int rowHeight;
	private int columnWidth;
	private Lexer lexer;
	private boolean matchingEnable;
	private MatchingBlockManager matchLR;
	private MatchingBlockManager matchRL;
	private final List<KeywordListener> kwListeners = new ArrayList<>();
	private ScriptType type;

	/**
	 * Default Constructor
	 * 
	 * @param app
	 *            app
	 *
	 * @param rows
	 *            the number of rows to use
	 * @param cols
	 *            the number of columns to use
	 */
	public GeoGebraEditorPane(AppD app, int rows, int cols) {
		super();
		this.app = app;
		this.rows = rows;
		this.cols = cols;

		// TODO temporarily remove the caretListener
		// It causes a np exception with the PropertiesView.... why?
		// addCaretListener(this);

		addMouseMotionListener(this);
		addMouseListener(this);
		addFocusListener(this);
		addKeyListener(new GeoGebraKeys());
	}

	/**
	 * @param kitString "geogebra", "javascript" or "latex"
	 */
	public void setEditorKit(ScriptType kitString) {
		String str = getText();
		type = kitString;
		if (kitString == ScriptType.GGBSCRIPT) {
			GeoGebraEditorKit ggbKit = new GeoGebraEditorKit(app);
			super.setEditorKit(ggbKit);
			setFont(ggbKit.getStylePreferences().tokenFont);
			lexer = new GeoGebraLexer(getDocument(), app);

		} else if (kitString == ScriptType.JAVASCRIPT) {
			JavascriptEditorKit javascriptKit = new JavascriptEditorKit(app);
			super.setEditorKit(javascriptKit);
			setFont(javascriptKit.getStylePreferences().tokenFont);
			lexer = new JavascriptLexer(getDocument());
			((JavascriptEditorKit.JavascriptDocument) getDocument())
					.setTextComponent(this);
		}

		matchLR = new MatchingBlockManager(getDocument(), this, true);
		matchLR.setDefaults();
		matchRL = new MatchingBlockManager(getDocument(), this, false);
		matchRL.setDefaults();
		enableMatchingKeywords(true);
		setText(str);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFont(Font f) {
		super.setFont(f);
		this.columnWidth = getFontMetrics(f).charWidth('m');
		this.rowHeight = getFontMetrics(getFont()).getHeight();
	}

	/**
	 * @param f UI font
	 */
	public void updateFont(Font f) {
		((ViewContext) getEditorKit().getViewFactory()).setTokenFont(f);
		super.setFont(f);
		revalidate();
	}

	/**
	 * Returns preferred dimension for the given number of rows and columns when
	 * using the current font.
	 * 
	 * @param row row
	 * @param column column
	 * @return size
	 */
	public Dimension getPreferredSizeFromRowColumn(int row, int column) {
		Dimension dim = new Dimension(400, 400);
		if (column != 0) {
			dim.width = cols * columnWidth;
		}
		if (row != 0) {
			dim.height = rows * rowHeight;
		}

		return dim;
	}

	/**
	 * Add a new KeywordListener
	 *
	 * @param kw
	 *            a KeywordListener
	 */
	public void addKeywordListener(KeywordListener kw) {
		if (!kwListeners.contains(kw)) {
			kwListeners.add(kw);
		}
	}

	/**
	 * Remove a new KeywordListener
	 *
	 * @param kw
	 *            a KeywordListener
	 */
	public void removeKeywordListener(KeywordListener kw) {
		kwListeners.remove(kw);
	}

	/**
	 * Enable (active true) or disable (active false) the matching keywords.
	 *
	 * @param active
	 *            true or false
	 */
	public void enableMatchingKeywords(boolean active) {
		matchingEnable = active;
	}

	/**
	 * This class listens to the caret event
	 *
	 * @param e
	 *            event
	 */
	@Override
	public void caretUpdate(CaretEvent e) {
		if (lexer != null) {
			int pos = getCaretPosition();
			int ltok = lexer.getKeyword(pos, false);
			int start = lexer.start + lexer.yychar();
			int rtok = lexer.getKeyword(pos, true);
			if (matchingEnable) {
				matchLR.searchMatchingBlock(ltok, start);
				matchRL.searchMatchingBlock(rtok,
						lexer.start + lexer.yychar() + lexer.yylength());
			}

			if (type == ScriptType.GGBSCRIPT && rtok == GeoGebraLexerConstants.OPENCLOSE) {
				pos = getCaretPosition() - 1;
				rtok = lexer.getKeyword(pos, true);
				if (rtok == GeoGebraLexerConstants.COMMAND) {
					try {
						HelpOnKeywordPanel panel = HelpOnKeywordPanel
								.getInstance(app,
										getDocument().getText(
												lexer.start + lexer.yychar(),
												lexer.yylength()));
						Point p = this.getLocationOnScreen();
						Rectangle r = modelToView(pos);
						if (helpPopup != null) {
							helpPopup.hide();
						}
						helpPopup = PopupFactory.getSharedInstance().getPopup(
								this, panel, p.x + r.x,
								p.y + r.y + 2 + r.height);
						helpPopup.show();
					} catch (BadLocationException | IllegalComponentStateException ex) {
						//
					}
				}
			} else {
				if (helpPopup != null) {
					helpPopup.hide();
					helpPopup = null;
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void focusGained(FocusEvent e) {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void focusLost(FocusEvent e) {
		if (helpPopup != null) {
			helpPopup.hide();
			helpPopup = null;
		}
	}

	/**
	 * Get a keyword at a position in the document.
	 *
	 * @param position
	 *            in the document
	 * @return the KeywordEvent containing infos about keyword.
	 */
	public KeywordEvent getKeywordEvent(int position) {
		int tok = lexer.getKeyword(position, true);
		return new KeywordEvent(this, null, tok, lexer.start + lexer.yychar(),
				lexer.yylength());
	}

	/**
	 * Prevents the different KeywordListener that a MouseEvent occured
	 *
	 * @param position
	 *            of the mouse
	 * @param ev
	 *            the event which occured
	 * @param type1
	 *            of the event : KeywordListener.ONMOUSECLICKED or
	 *            KeywordListener.ONMOUSEOVER
	 */
	protected void preventConcernedKeywordListener(int position, EventObject ev,
			int type1) {
		int tok = lexer.getKeyword(position, true);
		KeywordEvent kev = new KeywordEvent(this, ev, tok,
				lexer.start + lexer.yychar(), lexer.yylength());
		for (KeywordListener listener : kwListeners) {
			if (type1 == listener.getType()) {
				listener.caughtKeyword(kev);
			}
		}
	}

	/**
	 * Implements mouseClicked in MouseListener
	 *
	 * @param e
	 *            event
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		preventConcernedKeywordListener(getCaretPosition(), e,
				KeywordListener.ONMOUSECLICKED);
	}

	/**
	 * Implements mouseEntered in MouseListener
	 *
	 * @param e
	 *            event
	 */
	@Override
	public void mouseEntered(MouseEvent e) {
		//
	}

	/**
	 * Implements mouseExited in MouseListener
	 *
	 * @param e
	 *            event
	 */
	@Override
	public void mouseExited(MouseEvent e) {
		//
	}

	/**
	 * Implements mousePressed in MouseListener
	 *
	 * @param e
	 *            event
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		//
	}

	/**
	 * Implements mouseReleseaed in MouseListener
	 *
	 * @param e
	 *            event
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		//
	}

	/**
	 * Implements mouseMoved in MouseMotionListener
	 *
	 * @param e
	 *            event
	 */
	@Override
	public void mouseMoved(MouseEvent e) {
		preventConcernedKeywordListener(viewToModel(e.getPoint()), e,
				KeywordListener.ONMOUSEOVER);
	}

	/**
	 * Implements mouseDragged in MouseMotionListener
	 *
	 * @param e
	 *            event
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
		//
	}
}
