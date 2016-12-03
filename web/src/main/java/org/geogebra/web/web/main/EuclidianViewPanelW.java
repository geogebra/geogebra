package org.geogebra.web.web.main;

import java.util.Iterator;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Panel for Euclidian View
 */
public class EuclidianViewPanelW extends Composite implements HasWidgets {

	private AbsolutePanel evPanel;
	private FocusPanel focusPanel;

	/**
	 * Creates new EV panel
	 */
	public EuclidianViewPanelW() {
		evPanel = new AbsolutePanel();
		focusPanel = new FocusPanel();
		focusPanel.setWidget(evPanel);
		initWidget(focusPanel);
	}


	public void add(Widget w) {
		evPanel.add(w);
	}

	public void clear() {
		evPanel.clear();
	}

	public Iterator<Widget> iterator() {
		return evPanel.iterator();
	}

	public boolean remove(Widget w) {
		return evPanel.remove(w);
	}

	/**
	 * @param i
	 *            index
	 * @return child widget of nested panel at specified index
	 */
	public Widget getWidget(int i) {
		return evPanel.getWidget(i);
	}

	/**
	 * @param panel
	 *            panel to be added
	 * @param x
	 *            x coord
	 * @param y
	 *            y coord
	 */
	public void add(HorizontalPanel panel, int x, int y) {
		evPanel.add(panel, x, y);

	}

}
