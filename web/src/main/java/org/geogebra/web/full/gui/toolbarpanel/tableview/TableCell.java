package org.geogebra.web.full.gui.toolbarpanel.tableview;

import org.geogebra.web.html5.gui.util.Dom;

import com.google.gwt.safehtml.shared.SafeHtml;

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
		main.innerHTML = content;
		errorHTML = hasError ? Dom.createDiv("errorStyle").outerHTML : "";
	}

	/**
	 * @return SafeHtml of the cell.
	 */
	SafeHtml getHTML() {
		return () -> main.outerHTML + errorHTML;
	}
}
