package org.geogebra.web.web.gui.view.algebra;

import org.geogebra.common.gui.inputfield.AltKeys;
import org.geogebra.web.html5.gui.view.algebra.MathKeyboardListener;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.user.client.ui.UIObject;
import com.himamis.retex.editor.web.MathFieldW;

public class RetexKeyboardListener implements MathKeyboardListener {

	private Canvas canvas;
	private MathFieldW mf;

	public RetexKeyboardListener(Canvas canvas, MathFieldW mf) {
		this.canvas = canvas;
		this.mf = mf;
	}

	@Override
	public void setFocus(boolean focus, boolean scheduled) {
		canvas.setFocus(focus);

	}

	@Override
	public void ensureEditing() {
		mf.requestViewFocus();

	}

	@Override
	public UIObject asWidget() {
		return canvas;
	}

	@Override
	public String getText() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onEnter(boolean b) {
		// TODO Auto-generated method stub

	}

	public MathFieldW getMathField() {
		return mf;
	}

	@Override
	public boolean needsAutofocus() {
		return true;
	}

	public String alt(int unicodeKeyChar, boolean shift) {
		return AltKeys.getAltSymbols(unicodeKeyChar, shift, true);
	}

}
