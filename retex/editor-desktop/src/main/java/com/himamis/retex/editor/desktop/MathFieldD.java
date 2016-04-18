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

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import com.himamis.retex.editor.desktop.event.ClickListenerAdapter;
import com.himamis.retex.editor.desktop.event.FocusListenerAdapter;
import com.himamis.retex.editor.desktop.event.KeyListenerAdapter;
import com.himamis.retex.editor.share.editor.MathField;
import com.himamis.retex.editor.share.editor.MathFieldInternal;
import com.himamis.retex.editor.share.event.ClickListener;
import com.himamis.retex.editor.share.event.FocusListener;
import com.himamis.retex.editor.share.event.KeyListener;
import com.himamis.retex.editor.share.meta.MetaModel;
import com.himamis.retex.editor.share.meta.MetaModelParser;
import com.himamis.retex.editor.share.model.MathFormula;
import com.himamis.retex.renderer.desktop.IconHelper;
import com.himamis.retex.renderer.share.TeXIcon;
import com.himamis.retex.renderer.share.platform.Resource;

public class MathFieldD extends JLabel implements MathField {
	
	private static final MetaModel metaModel;
	
	static {
		metaModel = new MetaModelParser()
				.parse(new Resource().loadResource(
						"/com/himamis/retex/editor/desktop/meta/Octave.xml"));
	}

	private static final long serialVersionUID = 1L;
	
	private MathFieldInternal mathFieldInternal;

	public MathFieldD() {
		setBackground(Color.white);
		setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
		mathFieldInternal = new MathFieldInternal(this);
		mathFieldInternal.setFormula(MathFormula.newFormula(metaModel));
		this.setVerticalAlignment(SwingConstants.TOP);
	}
	
	public MathFieldD(String latex) {
		
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

	public MetaModel getMetaModel() {
		return metaModel;
	}

	public void hideCopyPasteButtons() {
		// TODO Auto-generated method stub

	}
}
