package org.geogebra.web.full.gui.toolbarpanel.tableview;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public class TableCell extends FlowPanel {

	/**
	 * constructor for cell
	 * @param content content of the cell
	 * @param hasError true if is erroneous cell
	 */
	public TableCell(String content, boolean hasError) {
		SimplePanel contentPanel = new SimplePanel();
		contentPanel.addStyleName("content");
		contentPanel.getElement().setInnerText(content);
		this.add(contentPanel);

		if (hasError) {
			SimplePanel errorTriangle = new SimplePanel();
			errorTriangle.addStyleName("errorStyle");
			this.add(errorTriangle);
		}
	}

	/**
	 * constructor for header cell
	 * @param htmlContent HTML content of the cell (including 3dot button)
	 */
	public TableCell(String htmlContent) {
		SimplePanel contentPanel = new SimplePanel();
		contentPanel.addStyleName("content");
		contentPanel.getElement().setInnerHTML(htmlContent);
		this.add(contentPanel);
	}

	/**
	 * @return SafeHtml of the cell.
	 */
	SafeHtml getHTML() {
		return (SafeHtml) () -> this.getElement().getInnerHTML();
	}
}
