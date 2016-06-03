package org.geogebra.web.html5.util.tabpanel;

import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;

public interface TabPanelInterface extends HasSelectionHandlers<Integer> {

	HandlerRegistration addSelectionHandler(
			SelectionHandler<Integer> selectionHandler);

	void selectTab(int i);
	
	void add(Widget w, String tabText);
	
	int getWidgetIndex(Widget widget);

}
