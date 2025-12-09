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

package org.geogebra.web.full.gui.view.algebra;

import org.geogebra.common.main.App;
import org.geogebra.common.util.IndexHTMLBuilder;
import org.gwtproject.dom.client.Document;
import org.gwtproject.dom.client.Element;
import org.gwtproject.dom.style.shared.Unit;
import org.gwtproject.user.client.ui.Widget;

/**
 * Index builder that creates SUB elements in DOM directly
 * 
 * @author Zbynek
 *
 */
public final class DOMIndexHTMLBuilder extends IndexHTMLBuilder {
	private final Widget w;
	private final App app;
	private Element sub = null;

	/**
	 * @param w
	 *            parent widget
	 * @param app
	 *            application (for font size)
	 */
	public DOMIndexHTMLBuilder(Widget w, App app) {
		super(false);
		this.w = w;
		this.app = app;
	}

	@Override
	public void append(String s) {

		if (sub == null) {
			w.getElement()
					.appendChild(Document.get().createTextNode(s));
		} else {
			sub.appendChild(Document.get().createTextNode(s));
		}
	}

	@Override
	public void startIndex() {
		sub = Document.get().createElement("sub");
		sub.getStyle().setFontSize((int) (app.getFontSize() * 0.8),
				Unit.PX);
	}

	@Override
	public void endIndex() {
		if (sub != null) {
			w.getElement().appendChild(sub);
		}
		sub = null;
	}

	@Override
	public String toString() {
		if (sub != null) {
			endIndex();
		}
		return w.getElement().getInnerHTML();
	}

	@Override
	public void clear() {
		w.getElement().removeAllChildren();
		sub = null;
	}

	@Override
	public boolean canAppendRawHtml() {
		return false;
	}

	@Override
	public void appendHTML(String str) {
		append(str);
	}
}