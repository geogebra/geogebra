package geogebra.html5.gui.tooltip;

import geogebra.common.euclidian.event.PointerEventType;
import geogebra.common.util.AsyncOperation;
import geogebra.common.util.StringUtil;
import geogebra.html5.css.GuiResourcesSimple;
import geogebra.html5.gui.util.CancelEventTimer;
import geogebra.html5.gui.util.ClickEndHandler;
import geogebra.html5.main.AppW;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ScrollEvent;
import com.google.gwt.user.client.Window.ScrollHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

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

	int mouseX = 0;
	int mouseY = 0;
	int scrollLeft = 0;
	int scrollTop = 0;

	private Timer timer;
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
	}

	public boolean isToolTipBlocked() {
		return blockToolTip;
	}

	public void setBlockToolTip(boolean blockToolTip) {
		this.blockToolTip = blockToolTip;
	}

	// =====================================
	// BottomInfoToolTip
	// =====================================
	/**
	 * @param text
	 *            String
	 * @param helpURL
	 *            String
	 * @param link
	 *            {@link ToolTipLinkType}
	 */
	public void showBottomInfoToolTip(String text, final String helpURL,
	        ToolTipLinkType link, AppW app) {
		if (blockToolTip) {
			return;
		}

		bottomInfoTipHTML.setHTML(text);

		if (helpLabel != null) {
			bottomInfoTipPanel.remove(helpLabel);
		}

		boolean online = app == null || app.getNetworkOperation() == null
		        || app.getNetworkOperation().isOnline();
		if (helpURL != null && helpURL.length() > 0 && link != null && online) {
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
				ClickEndHandler.init(helpLabel, new ClickEndHandler() {
					@Override
					public void onClickEnd(int x, int y, PointerEventType type) {
						openWindow(helpURL);
					}
				});

			helpLabel.addStyleName("manualLink");

			/*
			 * In "exam" mode the question mark is not shown
			 */
			if (!(app.isExam())) {
				bottomInfoTipPanel.add(helpLabel);
			}
		}

		bottomInfoTipPanel.setVisible(true);

		// Helps to align the InfoTooltip in the center of the screen:
		bottomInfoTipPanel
		        .getElement()
		        .getStyle()
		        .setMarginLeft(-(bottomInfoTipPanel.getOffsetWidth() / 2),
		                Unit.PX);
	}

	/**
	 * displays the given message
	 * 
	 * @param text
	 *            String
	 * @param closeAutomatic
	 *            whether the message should be closed automatically after
	 *            dismissDelay milliseconds
	 */
	public void showBottomMessage(String text, boolean closeAutomatic) {
		if (text == null || "".equals(text)) {
			hideBottomInfoToolTip();
			return;
		}
		blockToolTip = false;
		showBottomInfoToolTip("<html>" + StringUtil.toHTMLString(text)
		        + "</html>", "", null, null);

		blockToolTip = true;
		if (closeAutomatic) {
			timer = new Timer() {
				@Override
				public void run() {
					hideBottomInfoToolTip();
				}
			};
			timer.schedule(dismissDelay);
		}
	}

	/**
	 * Opens Link in a new window
	 * 
	 * @param url
	 *            that should be opened
	 */
	native void openWindow(String url)/*-{
		$wnd.open(url);
	}-*/;

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

		Event.addNativePreviewHandler(new NativePreviewHandler() {
			public void onPreviewNativeEvent(NativePreviewEvent event) {
				NativeEvent e = event.getNativeEvent();

				if (event.getTypeInt() == Event.ONTOUCHSTART) {
					CancelEventTimer.touchEventOccured();
				}

				if ((event.getTypeInt() == Event.ONMOUSEDOWN && !CancelEventTimer
				        .cancelMouseEvent())
				        || event.getTypeInt() == Event.ONTOUCHSTART) {
					showImmediately = false;
					hideToolTip();
					hideBottomInfoToolTip();
				}

				mouseX = e.getClientX();
				mouseY = e.getClientY();

			}
		});

		// TODO: is this needed?
		// observe scroll event, so we can offset x and y for toolTip location
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
	// ToolTip location
	// ======================================

	/**
	 * Set the toolTip widget location using the tipElement location or, if this
	 * is null, use current mouse coordinates.
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
		// locate and show the toolTip
		setToolTipLocation();
		// tipPanel.getElement().getStyle().setProperty("visibility",
		// "visible");
		tipPanel.setVisible(true);

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
		if (!tipPanel.isVisible()
		        && oldText.equals("")) {
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

	public static void setEnabled(boolean allowToolTips) {
		enabled = allowToolTips;
	}

	public void registerWidget(final Widget widget,
	        final AsyncOperation toolTipHandler, final boolean alignToElement,
	        final boolean showImmediately1) {

		MouseOverHandler mouseOverHandler = new MouseOverHandler() {
			@Override
			public void onMouseOver(MouseOverEvent event) {
				boolean oldDelay = false;
				if (showImmediately1) {
					oldDelay = sharedInstance().enableDelay;
					sharedInstance().setEnableDelay(false);
				}
				toolTipHandler.callback(null);
				if (alignToElement) {
					sharedInstance().showToolTip(widget.getElement(),
					        (String) toolTipHandler.getData());
				} else {
					sharedInstance().showToolTip(
					        (String) toolTipHandler.getData());
				}

				if (showImmediately1) {
					sharedInstance().setEnableDelay(oldDelay);
				}

			}
		};

		MouseOutHandler mouseOutHandler = new MouseOutHandler() {
			@Override
			public void onMouseOut(MouseOutEvent event) {
				ToolTipManagerW.sharedInstance().showToolTip(null);
			}
		};

		widget.addDomHandler(mouseOverHandler, MouseOverEvent.getType());
		widget.addDomHandler(mouseOutHandler, MouseOutEvent.getType());
	}
}
