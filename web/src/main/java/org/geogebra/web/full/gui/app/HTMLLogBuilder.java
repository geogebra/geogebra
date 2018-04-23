package org.geogebra.web.full.gui.app;

import org.geogebra.common.main.ExamLogBuilder;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HTML;

/**
 * HTML builder for exam logs
 * 
 * @author Zbynek
 *
 */
public class HTMLLogBuilder extends ExamLogBuilder {
	private HTML html;

	/**
	 * Default constructor.
	 */
	public HTMLLogBuilder() {
		this.html = new HTML();
	}

	@Override
	public void addLine(StringBuilder sb) {
		DivElement div = DOM.createDiv().cast();
		div.setInnerText(sb.toString());
		html.getElement().appendChild(div);
	}

	@Override
	public void addHR() {
		Element hr = DOM.createElement("HR");
		hr.getStyle().setMarginBottom(10, Unit.PX);
		html.getElement().appendChild(hr);
	}

	/**
	 * @return HTML content
	 */
	public HTML getHTML() {
		return html;
	}

}
