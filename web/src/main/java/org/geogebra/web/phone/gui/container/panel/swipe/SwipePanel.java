package org.geogebra.web.phone.gui.container.panel.swipe;

import static com.google.gwt.query.client.GQuery.$;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.query.client.plugins.effects.PropertiesAnimation.EasingCurve;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * A horizontal swipe panel;
 */
public class SwipePanel extends HorizontalPanel {

	/**
	 * Constructor
	 */
	public SwipePanel() {
		getElement().getStyle().setLeft(0, Unit.PX);
	}

	/**
	 * Swipes to the child panel with the specified index.
	 * @param index the index of the child panel.
	 */
	public void swipeTo(int index) {
		int n = getNumberOfPanels();
		if (index < 0 || index >= n) {
			return;
		}
		int scrollOffset = getScrollOffset();
		int selectedPanelLeft = index * scrollOffset;
		$(this).animate("{left:'-" + selectedPanelLeft + "px'}", 300,
				EasingCurve.swing);
	}

	@SuppressWarnings("static-method")
	private int getScrollOffset() {
		// TODO: the offset should be the width of the parent container
		return Window.getClientWidth();
	}

	private int getNumberOfPanels() {
		return getChildren().size();
	}
}
