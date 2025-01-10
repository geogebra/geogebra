package com.himamis.retex.editor.web;

import org.gwtproject.canvas.client.Canvas;
import org.gwtproject.dom.client.Element;
import org.gwtproject.user.client.ui.RootPanel;

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
					public boolean onEscape() {
						// TODO Auto-generated method stub
						return false;
					}

					@Override
					public boolean onTab(boolean shiftDown) {
						// TODO Auto-generated method stub
						return true;
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
					null, -1, false);
		} catch (ParseException e) {
			FactoryProvider.debugS("Invalid input " + ascii);
		}
		return texFormula;
	}

}
