package com.himamis.retex.editor.web;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.RootPanel;
import com.himamis.retex.editor.share.event.MathFieldListener;
import com.himamis.retex.editor.share.io.latex.ParseException;
import com.himamis.retex.editor.share.io.latex.Parser;
import com.himamis.retex.editor.share.meta.MetaModel;
import com.himamis.retex.editor.share.model.MathFormula;
import com.himamis.retex.editor.share.serializer.TeXBuilder;
import com.himamis.retex.renderer.share.TeXFormula;
import com.himamis.retex.renderer.share.platform.FactoryProvider;
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
					public boolean onArrowKeyPressed(int keyCode) {
						return false;
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

	@Override
	protected TeXFormula fromAsciiMath(String ascii) {
		TeXFormula texFormula = new TeXFormula();
		try {
			MathFormula formula = new Parser(new MetaModel()).parse(ascii);
			texFormula.root = new TeXBuilder().build(formula.getRootComponent(),
					null, false);
		} catch (ParseException e) {
			FactoryProvider.debugS("Invalid input " + ascii);
		}
		return texFormula;
	}

}
