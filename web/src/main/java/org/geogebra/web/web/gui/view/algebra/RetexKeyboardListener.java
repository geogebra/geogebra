package org.geogebra.web.web.gui.view.algebra;

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

	public void setFocus(boolean focus, boolean scheduled) {
		canvas.setFocus(focus);

	}

	public void ensureEditing() {
		// TODO Auto-generated method stub

	}

	public UIObject asWidget() {
		return canvas;
	}

	public String getText() {
		// TODO Auto-generated method stub
		return null;
	}

	public void onEnter(boolean b) {
		// TODO Auto-generated method stub

	}

	public MathFieldW getMathField() {
		return mf;
	}

	public boolean needsAutofocus() {
		return true;
	}

}
