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

package org.geogebra.editor.web;

import org.geogebra.editor.share.catalog.TemplateCatalog;
import org.geogebra.editor.share.editor.EditorFeatures;
import org.geogebra.editor.share.event.MathFieldListener;
import org.geogebra.editor.share.io.latex.ParseException;
import org.geogebra.editor.share.io.latex.Parser;
import org.geogebra.editor.share.serializer.TeXBuilder;
import org.geogebra.editor.share.tree.Formula;
import org.gwtproject.canvas.client.Canvas;
import org.gwtproject.dom.client.Element;
import org.gwtproject.user.client.ui.RootPanel;

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
				}, new EditorFeatures());
		fld.requestViewFocus();
	}

	@Override
	protected TeXFormula fromAsciiMath(String ascii) {
		TeXFormula texFormula = new TeXFormula();
		try {
			Formula formula = new Parser(new TemplateCatalog()).parse(ascii);
			texFormula.root = new TeXBuilder().build(formula.getRootNode(),
					null, -1, false);
		} catch (ParseException e) {
			FactoryProvider.debugS("Invalid input " + ascii);
		}
		return texFormula;
	}

}
