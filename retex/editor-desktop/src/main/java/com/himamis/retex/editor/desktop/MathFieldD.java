/**
 * This file is part of the ReTeX library - https://github.com/himamis/ReTeX
 *
 * Copyright (C) 2015 Balazs Bencze
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * A copy of the GNU General Public License can be found in the file
 * LICENSE.txt provided with the source distribution of this program (see
 * the META-INF directory in the source jar). This license can also be
 * found on the GNU website at http://www.gnu.org/licenses/gpl.html.
 *
 * If you did not receive a copy of the GNU General Public License along
 * with this program, contact the lead developer, or write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */
package com.himamis.retex.editor.desktop;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import com.himamis.retex.editor.desktop.event.ClickListenerAdapter;
import com.himamis.retex.editor.desktop.event.FocusListenerAdapter;
import com.himamis.retex.editor.desktop.event.KeyListenerAdapter;
import com.himamis.retex.editor.share.controller.CursorController;
import com.himamis.retex.editor.share.editor.MathField;
import com.himamis.retex.editor.share.editor.MathFieldInternal;
import com.himamis.retex.editor.share.event.ClickListener;
import com.himamis.retex.editor.share.event.FocusListener;
import com.himamis.retex.editor.share.event.KeyListener;
import com.himamis.retex.editor.share.event.MathFieldListener;
import com.himamis.retex.editor.share.input.KeyboardInputAdapter;
import com.himamis.retex.editor.share.meta.MetaModel;
import com.himamis.retex.editor.share.model.MathFormula;
import com.himamis.retex.renderer.desktop.IconHelper;
import com.himamis.retex.renderer.share.CursorBox;
import com.himamis.retex.renderer.share.SelectionBox;
import com.himamis.retex.renderer.share.TeXFont;
import com.himamis.retex.renderer.share.TeXIcon;

public class MathFieldD extends JLabel implements MathField {
	
	private static final MetaModel metaModel;
	
	static {
		metaModel = new MetaModel();
	}

	private static final long serialVersionUID = 1L;
	
	private MathFieldInternal mathFieldInternal;

	public MathFieldD() {
		SelectionBox.touchSelection = false;
		setBackground(Color.white);
		setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
		mathFieldInternal = new MathFieldInternal(this, false);
		mathFieldInternal.setFormula(MathFormula.newFormula(metaModel));
		mathFieldInternal.setSelectionMode(true);
		mathFieldInternal.setType(TeXFont.SANSSERIF);
		mathFieldInternal.setSize(36);
		this.setVerticalAlignment(SwingConstants.TOP);
		Timer t = new Timer(500, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				CursorBox.toggleBlink();
				repaint();
			}
		});
		t.setRepeats(true);
		t.start();
	}
	
	/**
	 * @param listener
	 *            listener
	 */
	public void setFieldListener(MathFieldListener listener) {
		mathFieldInternal.setFieldListener(listener);
	}

	@Override
	public void setTeXIcon(TeXIcon icon) {
		setIcon(IconHelper.createIcon(icon));
		setFocusTraversalKeysEnabled(true);
		setFocusable(true);
		setPreferredSize(new Dimension(icon.getIconWidth(), icon.getIconHeight()));
	}

	@Override
	public void setFocusListener(FocusListener focusListener) {
		addFocusListener(new FocusListenerAdapter(focusListener));
	}

	@Override
	public void setClickListener(ClickListener clickListener) {
		ClickListenerAdapter adapter = new ClickListenerAdapter(clickListener);
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
	public MetaModel getMetaModel() {
		return metaModel;
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

	public void hideCopyButton() {
		// TODO Auto-generated method stub

	}

	@Override
	public void fireInputChangedEvent() {
		// TODO Auto-generated method stub

	}

	public void insertString(String text) {
		KeyboardInputAdapter.insertString(mathFieldInternal, text);
		mathFieldInternal.update();
	}

	public void setFormula(MathFormula f) {
		mathFieldInternal.setFormula(f);

	}

	@Override
	public void paste() {
		Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
		try {
			insertString(clip.getData(DataFlavor.stringFlavor).toString());
		} catch (UnsupportedFlavorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
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

	public MathFormula getFormula(){
		return mathFieldInternal.getFormula();
	}

	@Override
	public boolean useCustomPaste() {
		return false;
	}

	public String getCurrentWord() {
		return mathFieldInternal.getCurrentWord();
	}

	public MathFieldInternal getInternal() {
		return this.mathFieldInternal;
	}

	@Override
	public void tab(boolean shiftDown) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @return caret path as indices in the formula tree
	 */
	public ArrayList<Integer> getCaretPath() {
		return CursorController.getPath(mathFieldInternal.getEditorState());
	}

}
