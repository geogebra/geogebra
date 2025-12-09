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

package org.geogebra.web.full.gui.toolbarpanel.tableview;

import org.geogebra.web.html5.gui.util.Dom;
import org.gwtproject.safehtml.shared.SafeHtml;

import elemental2.dom.HTMLElement;

public class TableCell {

	private final HTMLElement main;
	private final String errorHTML;

	/**
	 * constructor for cell
	 * @param content content of the cell
	 * @param hasError true if is erroneous cell
	 */
	public TableCell(String content, boolean hasError) {
		main = Dom.createDiv("content");
		main.textContent = content;
		errorHTML = hasError ? Dom.createDiv("errorStyle").outerHTML : "";
	}

	/**
	 * @return SafeHtml of the cell.
	 */
	SafeHtml getHTML() {
		return () -> main.outerHTML + errorHTML;
	}
}
