package com.himamis.retex.editor.web;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.RootPanel;
import com.himamis.retex.editor.share.event.MathFieldListener;
import com.himamis.retex.renderer.web.JlmLib;

public class JlmEditorLib extends JlmLib {
	/**
	 * @param el
	 *            element
	 */
	public void edit(Element el) {
		Canvas canvas = Canvas.createIfSupported();
		el.appendChild(canvas.getCanvasElement());
		MathFieldW fld = new MathFieldW(null, RootPanel.get(), canvas,
				new MathFieldListener() {

					@Override
					public void onEnter() {
						// TODO Auto-generated method stub

					}

					@Override
					public void onKeyTyped(String key) {
						// TODO Auto-generated method stub
					}

					@Override
					public void onCursorMove() {
						// TODO Auto-generated method stub
					}

					@Override
					public void onDownKeyPressed() {
						// TODO Auto-generated method stub
					}

					@Override
					public void onUpKeyPressed() {
						// TODO Auto-generated method stub
					}

					@Override
					public void onInsertString() {
						// TODO Auto-generated method stub
					}

					@Override
					public boolean onEscape() {
						// TODO Auto-generated method stub
						return false;
					}

					@Override
					public void onTab(boolean shiftDown) {
						// TODO Auto-generated method stub
					}
				});
		fld.requestViewFocus();
	}

}
