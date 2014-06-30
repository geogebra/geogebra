package geogebra.html5.gui.util;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class CardPanel extends FlowPanel {
	private int lastSelectedIndex;
	private int selectedIndex;
	public CardPanel() {
		lastSelectedIndex = -1;
		selectedIndex = 0;
	}
	public int getSelectedIndex() {
	    return selectedIndex;
    }
	
	public void setSelectedIndex(int idx) {
	   
	    if (lastSelectedIndex >= 0 && lastSelectedIndex < getWidgetCount()) {
	    	getWidget(lastSelectedIndex).setVisible(false);
	    }

	    getWidget(selectedIndex).setVisible(true);
	    lastSelectedIndex = selectedIndex;
	    selectedIndex = idx;
	}
	
	@Override
	public void add(Widget w) {
		w.setVisible(false);
		super.add(w);
	}
}

