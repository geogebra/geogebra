package com.himamis.retex.editor.web;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.HTML;
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
		MathFieldW fld = new MathFieldW(HTML.wrap(el), canvas.getContext2d(),
				new MathFieldListener() {

					public void onEnter() {
						// TODO Auto-generated method stub

					}
				});
		fld.requestViewFocus();
	}

}
