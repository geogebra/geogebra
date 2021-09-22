package org.geogebra.web.full.gui.toolbarpanel.tableview;

import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public class TableCell extends FlowPanel {

	public TableCell(String content, boolean hasError, AppW app) {
		SimplePanel contentPanel = new SimplePanel();
		contentPanel.addStyleName("content");
		contentPanel.getElement().setInnerText(content);
		this.add(contentPanel);

		if (hasError) {
			SimplePanel errorTriangle = new SimplePanel();
			errorTriangle.addHandler(event -> {
						ToolTipManagerW.sharedInstance().showBottomMessage("Use numbers only", app);
			}, MouseOverEvent.getType());
			errorTriangle.addStyleName("errorStyle");
			this.add(errorTriangle);
		}
	}
}
