package geogebra.web.gui.tooltip;

import geogebra.common.main.App;
import geogebra.web.main.AppW;

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

	private AppW app;
	protected SimplePanel tipPanel;
	private HTML tipHTML = new HTML();

	protected int mouseX = 0;
	protected int mouseY = 0;

	protected int scrollLeft = 0;
	protected int scrollTop = 0;

	private Timer timer;

	// Java ToolTipManager defaults
	private int initialDelay = 750;
	private int dismissDelay = 4000;
	private int reshowDelay = 100;

	private boolean enableDelay = true;
	protected boolean reshow = false;
	private String oldText = "";

	protected Element tipElement;

	/******************************************
	 * Construct a ToolTipManager
	 */
	public ToolTipManagerW(AppW app) {

		this.app = app;
		tipPanel = new SimplePanel();
		// tipPanel.setVisible(false);
		tipPanel.getElement().getStyle().setProperty("visibility", "hidden");
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
	 * Shows toolTip relative to a given element or at mouse position if element
	 * is null
	 * 
	 * @param element
	 *            element associated with tooltip
	 * @param toolTipText
	 *            text to be displayed
	 */
	public void showToolTip(Element element, String toolTipText) {

		tipElement = element;

		if (element == null || toolTipText == null) {
			return;
		}

		if (oldText.equals(toolTipText)) {
			return;
		}

		oldText = toolTipText;
		tipHTML.setHTML(toolTipText);

		showToolTipWithDelay();

	}

	/**
	 * Shows toolTip at mouse position
	 * 
	 * @param toolTipText
	 *            text to be displayed
	 */
	public void showToolTip(String toolTipText) {

		tipElement = null;

		if (toolTipText == null) {
			hideToolTip();
			return;
		}

		if (oldText.equals(toolTipText)) {
			return;
		}

		oldText = toolTipText;
		tipHTML.setHTML(toolTipText);

		showToolTipWithDelay();

	}

	protected void setToolTipLocation() {
		int left, top;

		if (tipElement == null) {
			left = scrollLeft + mouseX;
			top = scrollTop + mouseY + 18;

		} else {
			left = tipElement.getAbsoluteLeft();
			top = tipElement.getAbsoluteBottom();
		}

		// handle tooltip overflow at left and bottom edge
		int w = tipPanel.getOffsetWidth();
		int windowLeft = AppW.getRootComponent(app).getAbsoluteLeft()
		        + AppW.getRootComponent(app).getOffsetWidth();
		if (left + w > windowLeft) {
			left = left - w;
		}
		int h = tipPanel.getOffsetHeight();
		int windowBottom = AppW.getRootComponent(app).getAbsoluteTop()
		        + AppW.getRootComponent(app).getOffsetHeight();
		if (top + h > windowBottom) {
			top = windowBottom - h;
		}
		
		// set the tooltip location
		RootPanel.get().setWidgetPosition(tipPanel, left, top);
	}

	private void showToolTipWithDelay() {

		cancelTimer();

		if (enableDelay) {
			timer = new Timer() {
				@Override
				public void run() {
					show();
				}
			};

			if (reshow) {
				timer.schedule(reshowDelay);
			} else {
				timer.schedule(initialDelay);
			}

		} else {
			show();
		}
	}

	protected void show() {

		setToolTipLocation();
		// tipPanel.setVisible(true);
		tipPanel.getElement().getStyle().setProperty("visibility", "visible");
		reshow = true;

		if (enableDelay) {
			timer = new Timer() {
				@Override
				public void run() {
					hideToolTip();
				}
			};
			timer.schedule(dismissDelay);
		}
	}

	// ======================================
	// Cancel/Hide ToolTip
	// ======================================

	public void hideToolTip() {
		// App.printStacktrace("hide tooltip");
		hide();
		reshow = false;
		oldText = "";

	}

	protected void hide() {

		cancelTimer();
		// tipPanel.setVisible(false);
		tipPanel.getElement().getStyle().setProperty("visibility", "hidden");

		/*
		 * if (enableDelay) { timer = new Timer() {
		 * 
		 * @Override public void run() { tipPanel.setVisible(false); refreshing
		 * = false; } }; timer.schedule(exitDelay); }
		 */

	}

	private void cancelTimer() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

}
