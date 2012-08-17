/*
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.
This code has been written initially for Scilab (http://www.scilab.org/).

This program is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by
the Free Software Foundation.

 */

package geogebra.gui.editor;

import geogebra.gui.GeoGebraKeys;
import geogebra.main.AppD;

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

/**
 *
 * @author Calixte DENIZET
 *
 */
public class GeoGebraEditorPane extends JEditorPane implements CaretListener,
		MouseListener, MouseMotionListener, FocusListener {

	private static final long serialVersionUID = 1L;
	
	private static final int GEOGEBRA = 0;
	private static final int LATEX = 1;
	private static final int JAVASCRIPT = 2;

	private Popup helpPopup;

	private AppD app;
	private int rows;
	private int cols;
	private int rowHeight;
	private int columnWidth;
	private Lexer lexer;
	private boolean matchingEnable;
	private MatchingBlockManager matchLR;
	private MatchingBlockManager matchRL;
	private Point mousePoint;
	private List<KeywordListener> kwListeners = new ArrayList<KeywordListener>();
	private int type;

	/**
	 * Default Constructor
	 * @param app app
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
		//	addCaretListener(this);

		addMouseMotionListener(this);
		addMouseListener(this);
		addFocusListener(this);
		addKeyListener(new GeoGebraKeys(app));
	}

	/**
	 * @param kitString
	 */
	public void setEditorKit(String kitString) {
		String str = getText();
		if ("geogebra".equalsIgnoreCase(kitString)) {
			GeoGebraEditorKit ggbKit = new GeoGebraEditorKit(app);
			super.setEditorKit(ggbKit);
			setFont(ggbKit.getStylePreferences().tokenFont);
			lexer = new GeoGebraLexer(getDocument(), app);
			type = GEOGEBRA;
		} else if ("latex".equalsIgnoreCase(kitString)) {
			LaTeXEditorKit ltxKit = new LaTeXEditorKit(app);
			super.setEditorKit(ltxKit);
			setFont(ltxKit.getStylePreferences().tokenFont);
			lexer = new LaTeXLexer(getDocument());
			type = LATEX;
		} else if ("javascript".equalsIgnoreCase(kitString)) {
			JavascriptEditorKit javascriptKit = new JavascriptEditorKit(app);
			super.setEditorKit(javascriptKit);
			setFont(javascriptKit.getStylePreferences().tokenFont);
			lexer = new JavascriptLexer(getDocument());
			((JavascriptEditorKit.JavascriptDocument) getDocument())
					.setTextComponent(this);
			type = JAVASCRIPT;
		}

		matchLR = new MatchingBlockManager(getDocument(), this, true,
				getHighlighter());
		matchLR.setDefaults();
		matchRL = new MatchingBlockManager(getDocument(), this, false,
				getHighlighter());
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

	public void updateFont(Font f){
		super.setFont(f);
		((ViewContext)getEditorKit().getViewFactory()).setTokenFont(f);
	}
	
	// =================================================================
	//
	// TODO These two methods are preventing the script editor from scrolling. Do we
	// need them, can they be removed?

	/**
	 * {@inheritDoc}
	 */
	
	/*
	@Override
	public Dimension getPreferredSize() {
		Dimension dim = super.getPreferredSize();
		dim = (dim == null) ? new Dimension(400, 400) : dim;
		if (cols != 0) {
			dim.width = cols * columnWidth;
		}
		if (rows != 0) {
			dim.height = rows * rowHeight;
		}

		return dim;
	}
	*/
	

	/**
	 * {@inheritDoc}
	 */
	
	/*
	@Override
	public Dimension getPreferredScrollableViewportSize() {
		return new Dimension(cols * columnWidth, rows * rowHeight);
	}
	 */
	
	
	// =================================================================
	
	
	
	
	
	/**
	 * Returns preferred dimension for the given number of rows and columns
	 * when using the current font.
	 * 
	 * @param row
	 * @param column
	 * @return
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
		if (kwListeners.contains(kw)) {
			kwListeners.remove(kw);
		}
	}

	/**
	 * @return an array of KeywordListener
	 */
	public KeywordListener[] getKeywordListeners() {
		return kwListeners.toArray(new KeywordListener[0]);
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
	 * Get a matching manager
	 *
	 * @param lr
	 *            true if the LR matcher must be returned
	 * @return the MatchingBlockManager
	 */
	public MatchingBlockManager getMatchingBlockManager(boolean lr) {
		if (lr) {
			return matchLR;
		}
		return matchRL;
	}

	/**
	 * This class listens to the caret event
	 *
	 * @param e
	 *            event
	 */
	public void caretUpdate(CaretEvent e) {
		if (lexer != null) {
			int pos = getCaretPosition();
			int ltok = lexer.getKeyword(pos, false);
			int start = lexer.start + lexer.yychar();
			int rtok = lexer.getKeyword(pos, true);
			if (matchingEnable) {
				matchLR.searchMatchingBlock(ltok, start);
				matchRL.searchMatchingBlock(rtok, lexer.start + lexer.yychar()
						+ lexer.yylength());
			}

			if (type == GEOGEBRA && rtok == GeoGebraLexerConstants.OPENCLOSE) {
				pos = getCaretPosition() - 1;
				rtok = lexer.getKeyword(pos, true);
				if (rtok == GeoGebraLexerConstants.COMMAND) {
					try {
						HelpOnKeywordPanel panel = HelpOnKeywordPanel
								.getInstance(app, getDocument().getText(
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
					} catch (BadLocationException ex) {
						//
					}
					catch (IllegalComponentStateException ex) {			
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
	public void focusGained(FocusEvent e) {
		//
	}

	/**
	 * {@inheritDoc}
	 */
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
	 * Get a keyword at the current position in the document.
	 *
	 * @return the KeywordEvent containing infos about keyword.
	 */
	public KeywordEvent getKeywordEvent() {
		return getKeywordEvent(getCaretPosition());
	}

	/**
	 * Get a keyword at the current position in the document.
	 *
	 * @param caret
	 *            if true the position is the current caret position in the doc
	 *            else the position is the mouse pointer position projected in
	 *            the document.
	 * @param strict
	 *            if true the char just after the caret is ignored
	 * @return the KeywordEvent containing infos about keyword.
	 */
	public KeywordEvent getKeywordEvent(boolean caret, boolean strict) {
		int tok;
		if (caret) {
			tok = lexer.getKeyword(getCaretPosition(), strict);
		} else {
			tok = lexer.getKeyword(viewToModel(mousePoint), strict);
		}
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
	protected void preventConcernedKeywordListener(int position,
			EventObject ev, int type1) {
		int tok = lexer.getKeyword(position, true);
		KeywordEvent kev = new KeywordEvent(this, ev, tok, lexer.start
				+ lexer.yychar(), lexer.yylength());
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
	public void mouseEntered(MouseEvent e) {
		this.mousePoint = e.getPoint();
	}

	/**
	 * Implements mouseExited in MouseListener
	 *
	 * @param e
	 *            event
	 */
	public void mouseExited(MouseEvent e) {
		//
	}

	/**
	 * Implements mousePressed in MouseListener
	 *
	 * @param e
	 *            event
	 */
	public void mousePressed(MouseEvent e) {
		//
	}

	/**
	 * Implements mouseReleseaed in MouseListener
	 *
	 * @param e
	 *            event
	 */
	public void mouseReleased(MouseEvent e) {
		//
	}

	/**
	 * Implements mouseMoved in MouseMotionListener
	 *
	 * @param e
	 *            event
	 */
	public void mouseMoved(MouseEvent e) {
		this.mousePoint = e.getPoint();
		preventConcernedKeywordListener(viewToModel(mousePoint), e,
				KeywordListener.ONMOUSEOVER);
	}

	/**
	 * Implements mouseDragged in MouseMotionListener
	 *
	 * @param e
	 *            event
	 */
	public void mouseDragged(MouseEvent e) {
		//
	}

	/**
	 * @return the current mouse position in this pane
	 */
	public Point getMousePoint() {
		return mousePoint;
	}
	
	

}
