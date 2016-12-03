package org.geogebra.web.html5.gui.tooltip;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.javax.swing.SwingConstants;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.geogebra.web.html5.gui.util.ClickEndHandler;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * <p>
 * Singleton class that maintains a GWT panel for displaying toolTips.
 * </p>
 * 
 * <p>
 * Design is adapted from Java's ToolTipManager. ToolTip behavior should follow
 * this description from the Java source code:
 * </p>
 * 
 * <p>
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
 * </p>
 * 
 * @author G. Sturr
 */
public class ToolTipManagerW {

	/**
	 * The toolTip can include a link. depending on the type of the link,
	 * another picture has to be added.
	 */
	public enum ToolTipLinkType {
		/**
		 * question mark
		 */
		Help,
		/**
		 * TODO another picture is needed
		 */
		ViewSavedFile;
	}

	private SimplePanel tipPanel;
	private HTML tipHTML;

	private HorizontalPanel bottomInfoTipPanel;
	private HTML bottomInfoTipHTML;
	private String questionMark;
	private String viewSavedFile;
	private Label helpLabel;

	private String oldText = "";
	/** last mouse x coord */
	int mouseX = 0;
	/** last mouse y coord */
	int mouseY = 0;

	private Timer timer, bottomTimer;
	private boolean blockToolTip = true;

	/**
	 * Time, in milliseconds, to delay showing a toolTip.
	 * 
	 * Java default = 1750, // maybe we use a quicker 1000?
	 */
	private int initialDelay = 1750;

	/**
	 * Time, in milliseconds, to allow the toolTip to remain visible.
	 * 
	 * Java default = 4000.
	 */
	private int dismissDelay = 4000;

	/**
	 * Time, in milliseconds, to allow showing a new toolTip immediately, with
	 * no delay. After this delay has expired, toolTips are shown with an
	 * initial delay.
	 * 
	 * Java default = 500;
	 */
	private int reshowDelay = 500;

	/**
	 * Flag to enable/disable a delay time before showing a toolTip.
	 * */
	boolean enableDelay = true;

	/**
	 * Flag to prevent a toolTip delay, even if an initial delay has been
	 * enabled. This is helpful when the mouse is moved around nearby objects
	 * and the initial delay is annoying to the user.
	 */
	boolean showImmediately = false;

	/**
	 * HTML element associated with the toolTip. The toolTip will be positioned
	 * relative to this element.
	 */
	private Element tipElement;
	private static boolean enabled = true;
	private String helpURL;

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

	private void initTooltipManagerW() {
		if (tipPanel != null || !enabled) {
			return;
		}

		createTipElements();
		createBottomInfoTipElements();
		registerMouseListeners();

	}

	private void createTipElements() {
		tipHTML = new HTML();
		tipHTML.setStyleName("toolTipHTML");

		tipPanel = new SimplePanel();
		tipPanel.setStyleName("ToolTip");
		tipPanel.add(tipHTML);

		tipPanel.setVisible(false);
		RootPanel.get().add(tipPanel);
	}

	private void createBottomInfoTipElements() {
		bottomInfoTipHTML = new HTML();
		bottomInfoTipHTML.setStyleName("infoText");

		questionMark = GuiResourcesSimple.INSTANCE.questionMark().getSafeUri()
				.asString();
		viewSavedFile = GuiResourcesSimple.INSTANCE.viewSaved().getSafeUri()
				.asString();

		bottomInfoTipPanel = new HorizontalPanel();
		bottomInfoTipPanel.setStyleName("infoTooltip");
		bottomInfoTipPanel.add(bottomInfoTipHTML);

		bottomInfoTipPanel.setVisible(false);
		RootPanel.get().add(bottomInfoTipPanel);
		ClickEndHandler.init(bottomInfoTipPanel, new ClickEndHandler() {

			@Override
			public void onClickEnd(int x, int y, PointerEventType type) {
				openHelp();
			}
		});
	}

	/**
	 * Open current help URL in browser / webview
	 */
	protected void openHelp() {
		if (helpURL != null) {
			openWindow(helpURL);
			hideAllToolTips();
		}

	}

	/**
	 * @return whether tooltips are blocked
	 */
	public boolean isToolTipBlocked() {
		return blockToolTip;
	}

	/**
	 * @param blockToolTip
	 *            whether to block tooltips
	 */
	public void setBlockToolTip(boolean blockToolTip) {
		this.blockToolTip = blockToolTip;
	}

	// =====================================
	// BottomInfoToolTip
	// =====================================
	/**
	 * @param text
	 *            String
	 * @param helpLinkURL
	 *            String
	 * @param link
	 *            {@link ToolTipLinkType}
	 * @param app
	 *            app for positioning
	 * @param kb
	 *            whether keyboard is open
	 */
	public void showBottomInfoToolTip(String text, final String helpLinkURL,
			ToolTipLinkType link, AppW app, boolean kb) {
		if (blockToolTip || app == null) {
			return;
		}
		bottomInfoTipPanel.removeFromParent();
		app.getPanel().add(bottomInfoTipPanel);
		bottomInfoTipHTML.setHTML(text);

		if (helpLabel != null) {
			bottomInfoTipPanel.remove(helpLabel);
		}

		boolean online = app.getNetworkOperation() == null
				|| app.getNetworkOperation().isOnline();
		this.helpURL = helpLinkURL;
		if (app.isExam() && app.getExam().getStart() >= 0) {
			this.helpURL = null;
		}
		if (helpURL != null && helpURL.length() > 0 && link != null
				&& online) {

			helpLabel = new Label();

			if (link.equals(ToolTipLinkType.Help)) {
				helpLabel.getElement().getStyle()
						.setBackgroundImage("url(" + this.questionMark + ")");
			} else if (link.equals(ToolTipLinkType.ViewSavedFile)) {
				helpLabel.getElement().getStyle()
						.setBackgroundImage("url(" + this.viewSavedFile + ")");
			}
			// IE and FF block popups if they are comming from mousedown, so use
			// mouseup instead

			helpLabel.addStyleName("manualLink");

			/*
			 * In "exam" mode the question mark is not shown
			 */
			if (!(app.isExam() && app.getExam().getStart() >= 0)) {
				bottomInfoTipPanel.add(helpLabel);
			}
		}

		bottomInfoTipPanel.setVisible(true);


			// Helps to align the InfoTooltip in the center of the screen:

		Style style = bottomInfoTipPanel.getElement().getStyle();
		style.setLeft(0, Unit.PX);

		double left = (app.getWidth() - bottomInfoTipPanel.getOffsetWidth()) / 2;
		if (left < 0) {
			left = 0;
		}
			// Toolbar on bottom - tooltip needs to be positioned higher so it
			// doesn't overlap with the toolbar
		if (app.getToolbarPosition() == SwingConstants.SOUTH) {
			style.setLeft(left * 1.5, Unit.PX);
			style.setTop((app.getHeight() - (kb ? 250 : 70) - 50), Unit.PX);
			// Toolbar on top
		} else {
			style.setLeft(left, Unit.PX);
			style.setTop((app.getHeight() - (kb ? 250 : 70)), Unit.PX);
		}

		if (link == ToolTipLinkType.Help && helpURL != null
				&& helpURL.length() > 0) {
			scheduleHideBottom();
		}
	}

	/**
	 * displays the given message
	 * 
	 * @param text
	 *            String
	 * @param closeAutomatic
	 *            whether the message should be closed automatically after
	 *            dismissDelay milliseconds
	 * @param app
	 *            application
	 */
	public void showBottomMessage(String text, boolean closeAutomatic, AppW app) {
		if (text == null || "".equals(text)) {
			hideBottomInfoToolTip();
			return;
		}
		blockToolTip = false;
		showBottomInfoToolTip(StringUtil.toHTMLString(text), "", null, app, app
				.getAppletFrame()
				.isKeyboardShowing());

		blockToolTip = true;
		if (closeAutomatic) {
			scheduleHideBottom();
		}
	}

	private void scheduleHideBottom() {
		cancelTimer();
		timer = new Timer() {
			@Override
			public void run() {
				hideBottomInfoToolTip();
			}
		};

		timer.schedule(dismissDelay);

	}

	/**
	 * Opens Link in a new window
	 * 
	 * @param url
	 *            that should be opened
	 */
	native public static void openWindow(String url)/*-{
		$wnd.open(url);
	}-*/;

	/**
	 * Hide the bottom tooltip
	 */
	public void hideBottomInfoToolTip() {
		cancelTimer();
		bottomInfoTipPanel.setVisible(false);
	}

	// =====================================
	// Getters/Setters
	// =====================================

	/**
	 * @return time, in milliseconds, to wait before showing toolTip
	 */
	public int getInitialDelay() {
		return initialDelay;
	}

	/**
	 * Set initial delay time.
	 * 
	 * @param initialDelay
	 *            time, in milliseconds, to wait before showing toolTip
	 */
	public void setInitialDelay(int initialDelay) {
		this.initialDelay = initialDelay;
	}

	/**
	 * @return time, in milliseconds, to wait before hiding toolTip
	 * */
	public int getDismissDelay() {
		return dismissDelay;
	}

	/**
	 * Set dismissDelay time
	 * 
	 * @param dismissDelay
	 *            time, in milliseconds, to wait before hiding toolTip
	 */
	public void setDismissDelay(int dismissDelay) {
		this.dismissDelay = dismissDelay;
	}

	/**
	 * Set flag to enable/disable delay timers
	 * 
	 * @param enableDelay
	 *            If true, timers manage toolTip visibility. If false, the
	 *            toolTip is shown immediately without automatic hiding.
	 */
	public void setEnableDelay(boolean enableDelay) {
		this.enableDelay = enableDelay;
	}

	// =====================================
	// Mouse Listeners
	// =====================================

	/**
	 * Register mouse listeners to keep track of the mouse position and hide the
	 * toolTip on a mouseDown event.
	 */
	private void registerMouseListeners() {
		if (!enabled) {
			return;
		}

		// Closing tooltips is done in AppW.closePopups
		Event.addNativePreviewHandler(new NativePreviewHandler() {
			public void onPreviewNativeEvent(NativePreviewEvent event) {
				NativeEvent e = event.getNativeEvent();

				if (event.getTypeInt() == Event.ONTOUCHSTART) {
					CancelEventTimer.touchEventOccured();
				}

				mouseX = e.getClientX();
				mouseY = e.getClientY();

			}
		});


	}

	// ======================================
	// ToolTip location
	// ======================================

	/**
	 * Set the toolTip widget location using the tipElement location or, if this
	 * is null, use current mouse coordinates.
	 */
	protected void setToolTipLocation() {
		int left, top, topAbove;

		// get initial position from associated tip element or,
		// if this is null, from mouse coordinates
		if (tipElement == null) {
			left = Window.getScrollLeft() + mouseX;
			topAbove = top = Window.getScrollTop() + mouseY + 18;

		} else {
			left = tipElement.getAbsoluteLeft();
			top = tipElement.getAbsoluteBottom();
			topAbove = tipElement.getAbsoluteTop();
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
			top = topAbove - h;
		}

		// set the toolTip location
		RootPanel.get().setWidgetPosition(tipPanel, left, top);
	}

	// ======================================
	// Show/Hide ToolTip
	// ======================================

	/**
	 * Show toolTip relative to a given element
	 * 
	 * @param element
	 *            element associated with tooltip
	 * @param toolTipText
	 *            text to be displayed
	 */
	public void showToolTip(Element element, String toolTipText) {
		if (!enabled) {
			return;
		}
		tipElement = element;
		if (tipElement == null) {
			hideToolTip();
			return;
		}
		showToolTipWithDelay(toolTipText);
	}

	/**
	 * Show toolTip using mouse coordinates.
	 * 
	 * @param toolTipText
	 *            text to be displayed
	 */
	public void showToolTip(String toolTipText) {
		if (!enabled) {
			return;
		}
		tipElement = null;
		showToolTipWithDelay(toolTipText);
	}

	private void showToolTipWithDelay(String toolTipText) {

		if (oldText.equals(toolTipText)) {
			return;
		}

		if (toolTipText == null) {
			hideToolTip();
			return;
		}

		oldText = toolTipText;
		tipHTML.setHTML(toolTipText);

		if (enableDelay && !showImmediately) {
			setInitialDelayTimer();
		} else {
			show();
		}
	}

	/**
	 * Show the toolTip.
	 */
	protected void show() {
		if (!enabled) {
			return;
		}

		// tipPanel.getElement().getStyle().setProperty("visibility",
		// "visible");
		tipPanel.setVisible(true);
		// locate and show the toolTip
		setToolTipLocation();

		// set to immediate mode so that toolTips for nearby elements will not
		// be delayed
		showImmediately = true;

		// set the dismiss timer
		if (enableDelay) {
			setDismissDelayTimer();
		}
	}

	/**
	 * Hide the toolTip.
	 */
	public void hideToolTip() {
		if (!enabled) {
			return;
		}
		// exit if toolTip is already hidden
		if (!tipPanel.isVisible() && oldText.equals("")) {
			return;
		}

		tipHTML.setHTML("");
		oldText = "";
		// tipPanel.getElement().getStyle().setProperty("visibility", "hidden");

		tipPanel.setVisible(false);
		// cancel the timer in case of a delayed call to show()
		cancelTimer();

		// but, if in immediate mode, reset the reshow timer
		if (showImmediately) {
			setReshowTimer();
		}

	}

	// ======================================
	// Timers
	// ======================================

	private void setInitialDelayTimer() {

		cancelTimer();
		timer = new Timer() {
			@Override
			public void run() {
				show();
				// App.debug("initialDelay timer done, toolTip shown");
			}
		};
		// App.debug("start initialDelay timer");
		timer.schedule(initialDelay);
	}

	private void setDismissDelayTimer() {

		cancelTimer();
		timer = new Timer() {
			@Override
			public void run() {
				hideToolTip();
				// App.debug("dismissDelay timer done, toolTip hidden");
			}
		};
		// App.debug("start dismissDelay timer");
		timer.schedule(dismissDelay);
	}

	private void setReshowTimer() {

		cancelTimer();
		timer = new Timer() {
			@Override
			public void run() {
				showImmediately = false;
				// App.debug("reshow timer done, showImmediately = false");
			}
		};
		// App.debug("start reshowDelay timer");
		timer.schedule(reshowDelay);
	}

	private void cancelTimer() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

	/**
	 * @param allowToolTips
	 *            global tooltips flag
	 */
	public static void setEnabled(boolean allowToolTips) {
		enabled = allowToolTips;
	}

	/**
	 * Hide all tooltips
	 */
	public static void hideAllToolTips() {

		sharedInstance().showImmediately = false;
		sharedInstance().hideToolTip();
		sharedInstance().hideBottomInfoToolTip();
	}
}
