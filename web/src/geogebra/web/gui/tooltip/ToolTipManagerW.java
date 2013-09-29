package geogebra.web.gui.tooltip;

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
 * Singleton class that maintains a GWT panel for displaying toolTips.
 * 
 * Design is adapted from Java's ToolTipManager. ToolTip behavior should follow
 * this description found in the Java source code:
 * <p>
 * </p>
 * "ToolTipManager contains numerous properties for configuring how long it will
 * take for the tooltips to become visible, and how long till they hide.
 * Consider a component that has a different tooltip based on where the mouse
 * is, such as JTree. When the mouse moves into the JTree and over a region that
 * has a valid tooltip, the tooltip will become visible after initialDelay
 * milliseconds. After dismissDelay milliseconds the tooltip will be hidden. If
 * the mouse is over a region that has a valid tooltip, and the tooltip is
 * currently visible, when the mouse moves to a region that doesn't have a valid
 * tooltip the tooltip will be hidden. If the mouse then moves back into a
 * region that has a valid tooltip within reshowDelay milliseconds, the tooltip
 * will immediately be shown, otherwise the tooltip will be shown again after
 * initialDelay milliseconds."
 * 
 * @author G. Sturr
 */
public class ToolTipManagerW {

	private SimplePanel tipPanel;
	private HTML tipHTML = new HTML();

	private String oldText = "";

	private int mouseX = 0;
	private int mouseY = 0;
	private int scrollLeft = 0;
	private int scrollTop = 0;

	private Timer timer;

	/**
	 * Time, in milliseconds, to delay showing a toolTip triggered.
	 * 
	 * Java default = 1750
	 */
	private int initialDelay = 3750;

	/**
	 * Time, in milliseconds, to allow the toolTip to remain visible.
	 * 
	 * Java default = 4000.
	 */
	private int dismissDelay = 4000;

	/**
	 * Time, in milliseconds, to allow showing of the toolTip without delay.
	 * After this delay has expired, toolTips are shown with an initial delay.
	 * 
	 * Java default = 500;
	 */
	private int reshowDelay = 500;

	/**
	 * Flag to enable/disable a delay time before showing a toolTip.
	 * */
	private boolean enableDelay = true;

	/**
	 * Flag to prevent a toolTip delay, even if an initial delay has been
	 * enabled. This is helpful when the mouse is moved around nearby objects
	 * and the initial delay is annoying to the user.
	 */
	private boolean showImmediately = false;

	/**
	 * HTML element associated with the toolTip. The toolTip will be positioned
	 * relative to this element.
	 */
	private Element tipElement;

	/** Singleton instance of ToolTipManager. */
	final static ToolTipManagerW sharedInstance = new ToolTipManagerW();

	/*****************************************************
	 * Constructor
	 */
	private ToolTipManagerW() {
		initTooltipManagerW();
	}

	/**
	 * All methods are accessed from this instance.
	 * 
	 * @return Singleton instance of this class
	 */
	public static ToolTipManagerW sharedInstance() {
		return sharedInstance;
	}

	public void initTooltipManagerW() {

		if (tipPanel != null) {
			return;
		}

		tipPanel = new SimplePanel();
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
					hideToolTip(true);
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

				// App.debug("scrollLeft: " + scrollLeft + " scrollTop: "
				// + scrollTop);

			}
		});
	}

	// ======================================
	// Show ToolTip
	// ======================================

	/**
	 * Shows toolTip relative to a given element
	 * 
	 * @param element
	 *            element associated with tooltip
	 * @param toolTipText
	 *            text to be displayed
	 */
	public void showToolTip(Element element, String toolTipText) {

		tipElement = element;

		if (element == null || toolTipText == null) {
			hideToolTip(false);
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
			hideToolTip(false);
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
	 * Sets the toolTip widget location using the tipElement location or, if
	 * this is null, using current mouse coordinates.
	 */
	protected void setToolTipLocation() {
		int left, top;

		// get initial position from associated tip element or,
		// if this is null, from mouse coordinates
		if (tipElement == null) {
			left = scrollLeft + mouseX;
			top = scrollTop + mouseY + 18;

		} else {
			left = tipElement.getAbsoluteLeft();
			top = tipElement.getAbsoluteBottom();
		}

		// handle toolTip overflow at left and bottom edge
		int w = tipPanel.getOffsetWidth();
		int windowLeft = RootPanel.get().getAbsoluteLeft()
		        + RootPanel.get().getOffsetWidth();
		if (left + w > windowLeft) {
			left = left - w;
		}

		int h = tipPanel.getOffsetHeight();
		int windowBottom = RootPanel.get().getAbsoluteTop()
		        + RootPanel.get().getOffsetHeight();
		if (top + h > windowBottom) {
			top = windowBottom - h;
		}

		// set the toolTip location
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
			
			if (showImmediately) {
				timer.schedule(0);

			} else {
				timer.schedule(initialDelay);

			}

		} else {
			show();
		}
	}

	/**
	 * Show the tooltip.
	 */
	protected void show() {

		cancelTimer();

		setToolTipLocation();
		tipPanel.getElement().getStyle().setProperty("visibility", "visible");
		showImmediately = true;

		if (enableDelay) {
			timer = new Timer() {
				@Override
				public void run() {
					hideToolTip(true);
				}
			};
			timer.schedule(dismissDelay);
		}
	}

	// ======================================
	// Cancel/Hide ToolTip
	// ======================================

	/**
	 * TODO temporary fix --- do we need the reshow param?
	 */
	public void hideToolTip() {
		hideToolTip(false);
	}

	public void hideToolTip(boolean doReshow) {
		if (tipPanel.getElement().getPropertyBoolean("visibility")) {
			// TODO: do nothing ?
		}
		oldText = "";
		tipPanel.getElement().getStyle().setProperty("visibility", "hidden");
		setReshowTimer();
	}

	private void setReshowTimer() {
		
		if (showImmediately) {
			cancelTimer();
			timer = new Timer() {
				@Override
				public void run() {
					showImmediately = false;
				}
			};
		
			timer.schedule(reshowDelay);
		}
	}

	private void cancelTimer() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

}
