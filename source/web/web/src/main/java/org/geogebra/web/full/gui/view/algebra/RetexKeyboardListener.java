package org.geogebra.web.full.gui.view.algebra;

import org.geogebra.web.html5.gui.util.MathKeyboardListener;
import org.gwtproject.canvas.client.Canvas;
import org.gwtproject.user.client.ui.Widget;

import com.himamis.retex.editor.share.util.AltKeys;
import com.himamis.retex.editor.web.MathFieldW;

/**
 * ReTeX connector for keyboard
 */
public class RetexKeyboardListener implements MathKeyboardListener {

	private Canvas canvas;
	private MathFieldW mf;
	private boolean acceptsCommandInserts;

	/**
	 * @param canvas
	 *            canvas
	 * @param mf
	 *            math input field
	 */
	public RetexKeyboardListener(Canvas canvas, MathFieldW mf) {
		this.canvas = canvas;
		this.mf = mf;
	}

	@Override
	public void setFocus(boolean focus) {
		// canvas.setFocus(focus);
		mf.setFocus(focus);
	}

	@Override
	public void ensureEditing() {
		mf.requestViewFocus();

	}

	@Override
	public Widget asWidget() {
		return canvas;
	}

	/**
	 * @return math input field
	 */
	public MathFieldW getMathField() {
		return mf;
	}

	@Override
	public boolean needsAutofocus() {
		return true;
	}

	@Override
	public boolean hasFocus() {
		return getMathField().hasFocus();
	}

	/**
	 * @param unicodeKeyChar
	 *            code
	 * @param shift
	 *            whether shift is pressed also
	 * @return sequence for alt+code
	 */
	public String alt(int unicodeKeyChar, boolean shift) {
		return AltKeys.getAltSymbols(unicodeKeyChar, shift, true);
	}

	/**
	 * @return Whether the commands can be inserted into this text field
	 * from the keyboard's More button.
	 */
	@Override
	public boolean acceptsCommandInserts() {
		return acceptsCommandInserts;
	}

	/**
	 * @param acceptsCommandInserts Whether the commands can be inserted into this text field
	 *                              from the keyboard's More button.
	 */
	public void setAcceptsCommandInserts(boolean acceptsCommandInserts) {
		this.acceptsCommandInserts = acceptsCommandInserts;
	}
}
