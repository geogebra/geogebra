package org.geogebra.web.full.gui.app;

import org.geogebra.common.main.exam.ExamLogBuilder;

import com.google.gwt.dom.client.DivElement;
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
		addLineEl(sb.toString());
	}

	private DivElement addLineEl(String string) {
		DivElement div = DOM.createDiv().cast();
		div.setInnerText(string);
		html.getElement().appendChild(div);
		return div;
	}

	@Override
	public void addField(String name, String value) {
		DivElement nameEl = addLineEl(name);
		nameEl.getStyle().setColor("rgba(0,0,0,0.54)");
		nameEl.getStyle().setFontSize(75, Unit.PCT);
		addLineEl(value);
	}

	/**
	 * @return HTML content
	 */
	public HTML getHTML() {
		return html;
	}

}
