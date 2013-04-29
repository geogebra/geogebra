package geogebra.web.gui.tooltip;

import geogebra.common.main.App;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ScrollEvent;
import com.google.gwt.user.client.Window.ScrollHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * Class to manage tool tips. Maintains a gwt panel to display tooltips and
 * controls the visibility of this panel.
 * 
 * @author G. Sturr
 */
public class ToolTipManagerW {

	private SimplePanel tipPanel;
	private HTML tipHTML = new HTML();

	protected int mouseX = 0;
	protected int mouseY = 0;

	protected int scrollLeft = 0;
	protected int scrollTop = 0;

	private Timer initialDelayTimer, dismissTimer;

	// Java ToolTipManager defaults
	private int initialDelay = 750;
	private int dismissDelay = 4000;

	private boolean enableDelay = true;

	/******************************************
	 * Construct a ToolTipManager
	 */
	public ToolTipManagerW() {

		tipPanel = new SimplePanel();
		tipPanel.setVisible(false);
		tipPanel.add(tipHTML);
		RootPanel.get().add(tipPanel);
		tipPanel.setStyleName("ToolTip");

		registerMouseListeners();

	}

	// =====================================
	// Getters/Setters
	// =====================================

	public int getInitialDelay() {
		return initialDelay;
	}

	public void setInitialDelay(int initialDelay) {
		this.initialDelay = initialDelay;
	}

	public int getDismissDelay() {
		return dismissDelay;
	}

	public void setDismissDelay(int dismissDelay) {
		this.dismissDelay = dismissDelay;
	}

	public void setEnableDelay(boolean enableDelay) {
		this.enableDelay = enableDelay;
	}

	// =====================================
	// Mouse Listeners
	// =====================================

	/**
	 * Register mouse listeners to keep track of the mouse position and hide the
	 * tooltip on a mouseDown event.
	 */
	private void registerMouseListeners() {

		Event.addNativePreviewHandler(new NativePreviewHandler() {
			public void onPreviewNativeEvent(NativePreviewEvent event) {
				NativeEvent e = event.getNativeEvent();

				if (event.getTypeInt() == Event.ONMOUSEDOWN) {
					hideToolTip();
				}

				mouseX = e.getClientX();
				mouseY = e.getClientY();

			}
		});

		// observe scroll event, so we can offset x and y for tooltip
		Window.addWindowScrollHandler(new ScrollHandler() {
			public void onWindowScroll(ScrollEvent event) {

				scrollLeft = event.getScrollLeft();
				scrollTop = event.getScrollTop();

				App.debug("scrollLeft: " + scrollLeft + " scrollTop: "
				 + scrollTop);

			}
		});
	}

	// ======================================
	// Show ToolTip
	// ======================================

	/**
	 * Shows toolTip at given absolute position
	 * 
	 * @param x
	 * @param y
	 * @param toolTipText
	 */
	public void showToolTipAbsolute(int x, int y, String toolTipText) {

		if (toolTipText == null) {
			return;
		}

		tipHTML.setHTML(toolTipText);
		RootPanel.get().setWidgetPosition(tipPanel, x, y);
		showToolTipWithDelay();
	}

	/**
	 * Shows toolTip relative to a given element
	 * 
	 * @param elem
	 *            - element associated with tooltip
	 * @param toolTipText
	 */
	public void showToolTipForElement(Element elem, String toolTipText) {

		if (toolTipText == null || elem == null) {
			return;
		}

		if (tipPanel.isVisible()) {
			return;
		}

		tipHTML.setHTML(toolTipText);

		// TODO handle window edge overflow case
		int left = elem.getAbsoluteLeft();
		int top = elem.getAbsoluteBottom();
		RootPanel.get().setWidgetPosition(tipPanel, left, top);

		if (enableDelay) {
			showToolTipWithDelay();
		} else {
			show();
		}
	}

	/**
	 * Shows toolTip at current mouse position, just below cursor.
	 * 
	 * @param toolTipText
	 */
	public void showToolTipAtMousePosition(String toolTipText) {

		if (toolTipText == null) {
			hideToolTip();
			return;
		}

		if (tipPanel.isVisible()) {
			return;
		}

		tipHTML.setHTML(toolTipText);

		// TODO handle window edge overflow case
		int x = scrollLeft + mouseX;
		int y = scrollTop + mouseY + 18;
		RootPanel.get().setWidgetPosition(tipPanel, x, y);

		showToolTipWithDelay();
	}

	private void showToolTipWithDelay() {

		cancelDelayTimer();
		if (enableDelay) {
			initialDelayTimer = new Timer() {
				public void run() {
					show();
				}
			};
			initialDelayTimer.schedule(initialDelay);

		} else {
			show();
		}
	}

	protected void show() {

		tipPanel.setVisible(true);

		if (enableDelay) {
			dismissTimer = new Timer() {
				public void run() {
					tipPanel.setVisible(false);
				}
			};
			dismissTimer.schedule(dismissDelay);
		}
	}

	// ======================================
	// Cancel/Hide ToolTip
	// ======================================

	public void hideToolTip() {
		hide();
	}

	protected void hide() {
		cancelDelayTimer();
		cancelDismissTimer();
		tipPanel.setVisible(false);
	}

	private void cancelDelayTimer() {
		if (initialDelayTimer != null) {
			initialDelayTimer.cancel();
			initialDelayTimer = null;
		}
	}

	private void cancelDismissTimer() {
		if (dismissTimer != null) {
			dismissTimer.cancel();
			dismissTimer = null;
		}
	}

}
