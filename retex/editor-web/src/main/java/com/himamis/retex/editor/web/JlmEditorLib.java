package com.himamis.retex.editor.web;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.RootPanel;
import com.himamis.retex.editor.share.event.MathFieldListener;
import com.himamis.retex.editor.share.model.MathSequence;
import com.himamis.retex.renderer.web.JlmLib;

public class JlmEditorLib extends JlmLib {
	/**
	 * @param el
	 *            element
	 */
	public void edit(Element el) {
		Canvas canvas = Canvas.createIfSupported();
		el.appendChild(canvas.getCanvasElement());
		MathFieldW fld = new MathFieldW(RootPanel.get(), canvas,
				new MathFieldListener() {

					public void onEnter() {
						// TODO Auto-generated method stub

					}

					public void onKeyTyped() {
						// TODO Auto-generated method stub

					}

					public void onCursorMove() {
						// TODO Auto-generated method stub

					}

					public String alt(int unicodeKeyChar, boolean shift) {
						return unicodeKeyChar + "";
					}

					public void onDownKeyPressed() {
						// TODO Auto-generated method stub

					}

					public void onUpKeyPressed() {
						// TODO Auto-generated method stub

					}

					public String serialize(MathSequence selectionText) {
						return selectionText + "";
					}

					public void onInsertString() {
						// TODO Auto-generated method stub

					}
				});
		fld.requestViewFocus();
	}

}
