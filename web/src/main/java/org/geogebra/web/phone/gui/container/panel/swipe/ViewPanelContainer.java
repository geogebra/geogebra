package org.geogebra.web.phone.gui.container.panel.swipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geogebra.web.phone.PhoneLookAndFeel;
import org.geogebra.web.phone.gui.view.View;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * A panel showing one view at a time, with animated view changing.
 */
public class ViewPanelContainer extends SimplePanel implements
        org.geogebra.web.phone.gui.container.panel.Panel {

	private SwipePanel content;

	private List<View> viewOrder;
	private Map<View, ViewPanelWithStylebar> viewPanels;

	private View activeView;

	/**
	 * Constructor
	 */
	public ViewPanelContainer() {
		setStyleName("viewContainer");
		buildGui();

		viewOrder = new ArrayList<View>();
		viewPanels = new HashMap<View, ViewPanelWithStylebar>();
	}

	private void buildGui() {
		content = new SwipePanel();
		add(content);
	}

	public void addView(View view) {
		if (hasView(view)) {
			return;
		}
		ViewPanelWithStylebar viewPanel = createViewPanelWithStylebar(view);

		viewOrder.add(view);
		viewPanels.put(view, viewPanel);
		content.add(viewPanel);

		viewAdded();
	}

	private void viewAdded() {
		if (activeView == null && viewOrder.size() > 0) {
			activeView = viewOrder.get(0);
			content.swipeTo(0);
		}
	}

	public void removeView(View view) {
		if (!hasView(view)) {
			return;
		}
		Panel panel = viewPanels.get(view);

		viewOrder.remove(view);
		viewPanels.remove(view);
		content.remove(panel);

		viewRemoved();
	}

	private void viewRemoved() {
		if (activeView != null && !hasView(activeView)) {
			activeView = null;
			if (viewOrder.size() > 0) {
				int lastViewIndex = viewOrder.size() - 1;
				viewOrder.get(lastViewIndex);
				content.swipeTo(lastViewIndex);
			}
		}
	}

	public void showView(View view) {
		if (view == activeView || !hasView(view)) {
			return;
		}
		int viewIndex = getViewIndex(view);
		content.swipeTo(viewIndex);
		activeView = view;
		updateSize();
	}

	/**
	 * after a resize, the scroll-position of the {@link SwipePanel} has to be
	 * updated. the active view "scrolls" to the right position.
	 * 
	 */
	public void updateAfterResize() {
		content.swipeTo(getViewIndex(activeView));
	}

	private int getViewIndex(View view) {
		return viewOrder.indexOf(view);
	}

	public void onResize() {
		updateSize();
		for (ViewPanelWithStylebar panel : viewPanels.values()) {
			panel.onResize();
		}
	}

	private void updateSize() {
		int height = Window.getClientHeight()
		        - PhoneLookAndFeel.PHONE_HEADER_HEIGHT;
		int width = Window.getClientWidth();
		Style style = getElement().getStyle();
		style.setPosition(Position.RELATIVE);
		style.setPropertyPx("width", width);
		style.setPropertyPx("height", height);
		style.setProperty("zoom", "1");
		style.setOverflowX(Overflow.HIDDEN);
		style.setOverflowY(Overflow.HIDDEN);
	}

	private ViewPanelWithStylebar createViewPanelWithStylebar(View view) {
		ViewPanelWithStylebar viewPanel = new ViewPanelWithStylebar();
		viewPanel.setPanel(view.getViewPanel());
		viewPanel.setStyleBar(view.getStyleBar());
		return viewPanel;
	}

	private boolean hasView(View view) {
		return viewPanels.containsKey(view);
	}

}
