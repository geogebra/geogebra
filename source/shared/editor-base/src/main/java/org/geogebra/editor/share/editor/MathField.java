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

package org.geogebra.editor.share.editor;

import org.geogebra.editor.share.catalog.TemplateCatalog;
import org.geogebra.editor.share.event.ClickListener;
import org.geogebra.editor.share.event.FocusListener;
import org.geogebra.editor.share.event.KeyListener;

import com.himamis.retex.renderer.share.TeXIcon;

/**
 * Formula input field.
 */
public interface MathField {

	/**
	 * Update the image of the currently edited formula.
	 * @param icon icon
	 */
	void setTeXIcon(TeXIcon icon);

	/**
	 * show keyboard
	 *
	 * @return true if keyboard was hidden previously
	 */
	boolean showKeyboard();

	/**
	 * Show copy and paste buttons (mobile only).
	 */
	void showCopyPasteButtons();

	/**
	 * Focus this input field.
	 */
	void requestViewFocus();

	/**
	 * Set a focus listener.
	 * @param focusListener focus listener
	 */
	void setFocusListener(FocusListener focusListener);

	/**
	 * Set a pointer event listener.
	 * @param clickListener pointer event listener
	 */
	void setClickListener(ClickListener clickListener);

	/**
	 * Set a keyboard event listener.
	 * @param keyListener keyboard event listener
	 */
	void setKeyListener(KeyListener keyListener);

	/**
	 * Mark the component for repaint.
	 */
	void repaint();

	/**
	 * Invalidate layout (mobile, desktop).
	 */
	void requestLayout();

	/**
	 * @return whether this has a parent component
	 */
	boolean hasParent();

	/**
	 * @return whether this input field has focus
	 */
	boolean hasFocus();

	/**
	 * @return catalog describing all available math components (functions, arrays)
	 */
	TemplateCatalog getCatalog();

	/**
	 * Hide copy and paste buttons.
	 */
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

	/**
	 * Fire input change event.
	 */
	void fireInputChangedEvent();

	/**
	 * Paste from system keyboard.
	 */
	void paste();

	/**
	 * Copy to system keyboard.
	 */
	void copy();

	/**
	 * TODO remove this
	 * @return whether an old Firefox hack is needed (always false)
	 */
	boolean useCustomPaste();

	/**
	 * Parse input as math formula in editor format.
	 * @param str formula in editor format
	 */
	default void parse(String str) {
		getInternal().parse(str);
	}

	/**
	 * @return the cross-platform representation of this field
	 */
	MathFieldInternal getInternal();

	/**
	 * Remove focus and call blur handler.
	 */
	default void blur() {
		// implemented in web
	}
}
