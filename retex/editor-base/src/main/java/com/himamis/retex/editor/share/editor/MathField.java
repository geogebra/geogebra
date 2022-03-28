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

package com.himamis.retex.editor.share.editor;

import com.himamis.retex.editor.share.event.ClickListener;
import com.himamis.retex.editor.share.event.FocusListener;
import com.himamis.retex.editor.share.event.KeyListener;
import com.himamis.retex.editor.share.meta.MetaModel;
import com.himamis.retex.renderer.share.TeXIcon;

public interface MathField {

	void setTeXIcon(TeXIcon icon);

	/**
	 * show keyboard
	 *
	 * @return true if keyboard was hidden previously
	 */
	boolean showKeyboard();

	void showCopyPasteButtons();

	void requestViewFocus();

	void setFocusListener(FocusListener focusListener);

	void setClickListener(ClickListener clickListener);

	void setKeyListener(KeyListener keyListener);

	void repaint();

	void requestLayout();

	boolean hasParent();

	boolean hasFocus();

	MetaModel getMetaModel();

	void hideCopyPasteButtons();

	/**
	 * scroll the view
	 *
	 * @param dx
	 *            x distance from current call to last call
	 * @param dy
	 *            y distance from current call to last call
	 */
	void scroll(int dx, int dy);

	void fireInputChangedEvent();

	void paste();

	void copy();

	boolean useCustomPaste();

	void parse(String str);

	/**
	 * @return the cross-platform representation of this field
	 */
	MathFieldInternal getInternal();
}
