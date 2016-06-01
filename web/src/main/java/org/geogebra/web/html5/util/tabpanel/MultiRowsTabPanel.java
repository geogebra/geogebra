package org.geogebra.web.html5.util.tabpanel;

import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabBar;
import com.google.gwt.user.client.ui.Widget;


public class MultiRowsTabPanel extends Composite implements TabPanelInterface {

	FlowPanel tabBar;

	public MultiRowsTabPanel() {
		Label temp = new Label("tabpanel will be here");
		initWidget(temp);
	}

	public HandlerRegistration addSelectionHandler(
			SelectionHandler<Integer> selectionHandler) {
		// TODO Auto-generated method stub
		return null;
	}

	public void selectTab(int i) {
		// TODO Auto-generated method stub

	}

	public TabBar getTabBar() {
		// TODO Auto-generated method stub
		return null;
	}

	public void add(Widget w, String tabText) {
		// TODO Auto-generated method stub

	}

	public int getWidgetIndex(Widget widget) {
		// TODO Auto-generated method stub
		return 0;
	}

}
