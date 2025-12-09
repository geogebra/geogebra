/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.editor.desktop;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import org.geogebra.editor.desktop.event.ClickListenerAdapter;
import org.geogebra.editor.desktop.event.FocusListenerAdapter;
import org.geogebra.editor.desktop.event.KeyListenerAdapter;
import org.geogebra.editor.share.catalog.TemplateCatalog;
import org.geogebra.editor.share.editor.MathField;
import org.geogebra.editor.share.editor.MathFieldInternal;
import org.geogebra.editor.share.editor.SyntaxAdapter;
import org.geogebra.editor.share.event.ClickListener;
import org.geogebra.editor.share.event.FocusListener;
import org.geogebra.editor.share.event.KeyListener;
import org.geogebra.editor.share.tree.Formula;

import com.himamis.retex.renderer.desktop.IconHelper;
import com.himamis.retex.renderer.share.CursorBox;
import com.himamis.retex.renderer.share.SelectionBox;
import com.himamis.retex.renderer.share.TeXIcon;

public class MathFieldD extends JLabel implements MathField {
	
	private static final TemplateCatalog CATALOG;
	
	static {
		CATALOG = new TemplateCatalog();
		CATALOG.enableSubstitutions();
		CATALOG.setForceBracketAfterFunction(true);
	}

	private static final long serialVersionUID = 1L;
	
	private final MathFieldInternal mathFieldInternal;
	private int cursorX;
	private int scrollX = 0;

	/**
	 * @param syntaxAdapter syntax adapter
	 * @param repaint cursor blink listener
	 */
	public MathFieldD(SyntaxAdapter syntaxAdapter, Runnable repaint) {
		SelectionBox.setTouchSelection(false);
		setBackground(Color.white);
		setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
		mathFieldInternal = new MathFieldInternal(this);
		mathFieldInternal.setFormula(new Formula(CATALOG));
		mathFieldInternal.setSyntaxAdapter(syntaxAdapter);
		mathFieldInternal.setSelectionMode(true);
		this.setVerticalAlignment(SwingConstants.TOP);
		Timer t = new Timer(500, e -> {
			CursorBox.toggleBlink();
			repaint.run();
		});
		t.setRepeats(true);
		t.start();
	}

	@Override
	public void setTeXIcon(TeXIcon icon) {
		setIcon(IconHelper.createIcon(icon));
		setFocusTraversalKeysEnabled(true);
		setFocusable(true);
		setPreferredSize(new Dimension(icon.getIconWidth(), icon.getIconHeight()));
		this.cursorX = icon.getCursorX();
	}

	@Override
	public void setFocusListener(FocusListener focusListener) {
		addFocusListener(new FocusListenerAdapter(focusListener));
	}

	@Override
	public void setClickListener(ClickListener clickListener) {
		ClickListenerAdapter adapter = new ClickListenerAdapter(this, clickListener);
		addMouseListener(adapter);
		addMouseMotionListener(adapter);
	}

	@Override
	public void setKeyListener(KeyListener keyListener) {
		addKeyListener(new KeyListenerAdapter(keyListener));
	}

	@Override
	public boolean hasParent() {
		return getParent() != null;
	}

	@Override
	public void requestViewFocus() {
		requestFocus();
	}

	@Override
	public void requestLayout() {
		invalidate();
	}

	@Override
	public TemplateCatalog getCatalog() {
		return CATALOG;
	}

	@Override
	public void hideCopyPasteButtons() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean showKeyboard() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void showCopyPasteButtons() {
		// TODO Auto-generated method stub

	}

	@Override
	public void scroll(int dx, int dy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void fireInputChangedEvent() {
		// TODO Auto-generated method stub

	}

	/**
	 * @param text string to insert
	 */
	public void insertString(String text) {
		mathFieldInternal.insertString(text);
		mathFieldInternal.update();
	}

	@Override
	public void paste() {
		Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
		try {
			insertString(clip.getData(DataFlavor.stringFlavor).toString());
		} catch (UnsupportedFlavorException | IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void copy() {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Clipboard clipboard = toolkit.getSystemClipboard();
		StringSelection stringSelection = new StringSelection(
				mathFieldInternal.copy());
		clipboard.setContents(stringSelection, null);

	}

	@Override
	public boolean useCustomPaste() {
		return false;
	}

	public MathFieldInternal getInternal() {
		return this.mathFieldInternal;
	}

	public int getScrollX() {
		return scrollX;
	}

	/**
	 * Scroll to get caret into view
	 * @param width parent width
	 */
	public void scrollHorizontally(int width) {
		scrollX = MathFieldInternal.getHorizontalScroll(scrollX, width, cursorX);
	}
}
