package org.geogebra.web.html5.gui.tooltip;

import java.util.Locale;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.javax.swing.SwingConstants;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.geogebra.web.html5.gui.util.ClickEndHandler;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.CSSAnimation;
import org.gwtproject.timer.client.Timer;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

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
public final class ToolTipManagerW {

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
	
	private AppW app;

	private TooltipPanel bottomInfoTipPanel;
	private HTML bottomInfoTipHTML;
	private Label helpLabel;

	private Timer timer;
	private boolean blockToolTip = true;
	private boolean keyboardVisible;
	private boolean lastTipVisible = false;
	private boolean isSmall = false;
	private boolean moveBtnMoved = false;
	private ToolTipLinkType linkType;

	/**
	 * Time, in milliseconds, to delay showing a toolTip.
	 * 
	 * Java default = 1750, // maybe we use a quicker 1000?
	 */
	private int initialDelay = 500;

	/**
	 * Time, in milliseconds, to allow the toolTip to remain visible.
	 * 
	 * Java default = 4000.
	 */
	private int dismissDelay = 6000;

	/**
	 * HTML element associated with the toolTip. The toolTip will be positioned
	 * relative to this element.
	 */
	private static boolean enabled = true;
	private String helpURL;

	/** Singleton instance of ToolTipManager. */
	final static ToolTipManagerW SHARED_INSTANCE = new ToolTipManagerW();

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
		return SHARED_INSTANCE;
	}

	private void initTooltipManagerW() {
		if (bottomInfoTipHTML != null || !enabled) {
			return;
		}

		createBottomInfoTipElements();
		registerMouseListeners();
	}

	private void createBottomInfoTipElements() {
		bottomInfoTipHTML = new HTML();
		bottomInfoTipHTML.setStyleName("infoText");

		bottomInfoTipPanel = new TooltipPanel();
		bottomInfoTipPanel.setStyleName("infoTooltip");
		bottomInfoTipPanel.add(bottomInfoTipHTML);
		bottomInfoTipPanel.setVisible(false);
		RootPanel.get().add(bottomInfoTipPanel);
	}

	/**
	 * Open current help URL in browser / webview
	 */
	void openHelp() {
		if (!StringUtil.empty(helpURL) && app != null) {
			if (this.linkType == ToolTipLinkType.ViewSavedFile) {
				app.share();
			} else {
				app.getFileManager().open(helpURL);
			}
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
	 * @param appw
	 *            app for positioning
	 * @param kb
	 *            whether keyboard is open
	 */
	public void showBottomInfoToolTip(String text, final String helpLinkURL,
			ToolTipLinkType link, final AppW appw, boolean kb) {
		if (blockToolTip || appw == null) {
			return;
		}
		
		this.app = appw;
		keyboardVisible = kb;
		linkType = link;
		isSmall = false;
		if (app.isUnbundled()) {
			bottomInfoTipPanel.setStyleName("snackbar");
			if (appw.getWidth() < 400) {
				bottomInfoTipPanel.addStyleName("small");
				isSmall = true;
			}
		} else if (app.isWhiteboardActive()) {
			bottomInfoTipPanel.setStyleName("snackbarMow");
		} else {
			bottomInfoTipPanel.setStyleName("infoTooltip");
		}

		bottomInfoTipPanel.removeFromParent();
		appw.getPanel().add(bottomInfoTipPanel);
		bottomInfoTipHTML.setHTML(text);

		if (helpLabel != null) {
			bottomInfoTipPanel.remove(helpLabel);
		}

		boolean online = appw.getNetworkOperation() == null
				|| appw.getNetworkOperation().isOnline();
		this.helpURL = helpLinkURL;
		if (appw.isExam() && appw.getExam().getStart() >= 0) {
			this.helpURL = null;
		}
		if (helpURL != null && helpURL.length() > 0
				&& link != null
				&& online) {

			helpLabel = new Label();

			if (link.equals(ToolTipLinkType.Help)) {
				helpLabel.setText(app.getLocalization().getMenu("Help")
						.toUpperCase(Locale.ROOT));
			} else if (link.equals(ToolTipLinkType.ViewSavedFile)) {
				helpLabel.setText(app.getLocalization().getMenu("Share")
						.toUpperCase(Locale.ROOT));
			}
			// IE and FF block popups if they are comming from mousedown, so use
			// mouseup instead

			helpLabel.addStyleName("manualLink");

			/*
			 * In "exam" mode the question mark is not shown
			 */
			if (!(appw.isExam() && appw.getExam().getStart() >= 0)
					&& !app.isWhiteboardActive() && helpLinkURL != null
					&& !" ".equals(helpLinkURL)) {
				bottomInfoTipPanel.add(helpLabel);
			}
		} else if (app.isUnbundled()) {
			helpLabel = new Label();
			helpLabel.addStyleName("warning");
			bottomInfoTipPanel.add(helpLabel);
		}

		bottomInfoTipPanel.setVisible(true);
		if (helpLabel != null) {
			ClickEndHandler.init(helpLabel, new ClickEndHandler() {

				@Override
				public void onClickEnd(int x, int y, PointerEventType type) {
					openHelp();
				}
			});

		}

			// Helps to align the InfoTooltip in the center of the screen:

		Style style = bottomInfoTipPanel.getElement().getStyle();
		style.setLeft(0, Unit.PX);

		double left = (appw.getWidth() - bottomInfoTipPanel.getOffsetWidth()) / 2;
		if (left < 0 || app.isUnbundled()) {
			if (left < 0) {
				left = 0;
			} else if (app.isUnbundled()) {
				// is landscape
				if (appw.getWidth() >= appw.getHeight()) {
					left = 0;
				}
			}
		}
			// Toolbar on bottom - tooltip needs to be positioned higher so it
			// doesn't overlap with the toolbar
		if (appw.getToolbarPosition() == SwingConstants.SOUTH) {
			style.setLeft(left, Unit.PX);
			if (app.isWhiteboardActive()) {
				style.setTop((appw.getHeight() - 220) - 20 * lines(text),
						Unit.PX);
			} else {
				style.setTop((appw.getHeight() - (kb ? 250 : 70) - 50)
						- 20 * lines(text), Unit.PX);
			}
		}
		// Toolbar on top
		else {
			style.setLeft(left, Unit.PX);

			if (app.isUnbundled()) {
				if (appw.getAppletFrame().isKeyboardShowing()) {
					style.setTop((appw.getHeight() - 310), Unit.PX);
				} else {
					bottomInfoTipPanel.getElement().getStyle().clearTop();
					if (!lastTipVisible && link != null) {
						animateIn(appw);
					} else {
						bottomInfoTipPanel.getElement().getStyle().setBottom(0, Unit.PX);
					}
					moveBtnMoved = appw.getGuiManager()
							.moveMoveFloatingButtonUp((int) left,
							bottomInfoTipPanel.getOffsetWidth(), isSmall);
				}

			} else {
				style.setTop((appw.getHeight() - (kb ? 250 : 70)) - 20 * lines(text), Unit.PX);
			}
		}
		lastTipVisible = true;
		if ((link == ToolTipLinkType.ViewSavedFile)
				|| (link == ToolTipLinkType.Help && helpURL != null
						&& helpURL.length() > 0)) {
				scheduleHideBottom();
		}
	}

	private void animateIn(final AppW appw) {
		appw.getPanel().getElement().getStyle().setOverflow(Overflow.HIDDEN);
		bottomInfoTipPanel.addStyleName("animateShow");
		CSSAnimation.runOnAnimation(new Runnable() {
			@Override
			public void run() {
				appw.getPanel().getElement().getStyle()
						.setOverflow(Overflow.VISIBLE);

			}
		}, bottomInfoTipPanel.getElement(), "animateShow");

	}

	private static int lines(String text) {
		int lines = 0;
		for (int i = 0; i < text.length(); i++) {
			if ('\n' == text.charAt(i)) {
				lines++;
			}
		}
		return lines;
	}

	/**
	 * displays the given message
	 * 
	 * @param text
	 *            String
	 * @param closeAutomatic
	 *            whether the message should be closed automatically after
	 *            dismissDelay milliseconds
	 * @param appw
	 *            application
	 */
	public void showBottomMessage(String text, boolean closeAutomatic, AppW appw) {
		if (text == null || "".equals(text)) {
			hideBottomInfoToolTip();
			return;
		}
		blockToolTip = false;
		showBottomInfoToolTip(StringUtil.toHTMLString(text), "", null, appw,
				appw != null && appw.getAppletFrame().isKeyboardShowing());

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
	 * @param width
	 *            - update width of tooltip with av resize
	 */
	public void setTooltipWidthOnResize(int width) {
		bottomInfoTipPanel.getElement().getStyle().setWidth(width, Unit.PX);
	}

	/**
	 * Hide the bottom tooltip
	 */
	public void hideBottomInfoToolTip() {
		if (app != null && app.isUnbundled() && !keyboardVisible
				&& linkType != null) {
			app.getPanel().getElement().getStyle().setOverflow(Overflow.HIDDEN);
			bottomInfoTipPanel.addStyleName("animateHide");
			bottomInfoTipPanel.getElement().getStyle().clearBottom();
			timer = new Timer() {
				@Override
				public void run() {
					animateSnackbarOut();
				}
			};
			timer.schedule(400);
		} else {
			cancelTimer();
			bottomInfoTipPanel.removeFromParent();
		}
		if (app != null && app.isUnbundled()) {
			app.getGuiManager().moveMoveFloatingButtonDown(isSmall,
					moveBtnMoved);
			moveBtnMoved = false;
		}
		lastTipVisible = false;
	}

	// =====================================
	// Getters/Setters
	// =====================================

	/**
	 * Move snackbar out
	 */
	void animateSnackbarOut() {
		cancelTimer();
		bottomInfoTipPanel.removeFromParent();
		app.getPanel().getElement().getStyle().setOverflow(Overflow.VISIBLE);
	}

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

	// =====================================
	// Mouse Listeners
	// =====================================

	/**
	 * Register mouse listeners to keep track of the mouse position and hide the
	 * toolTip on a mouseDown event.
	 */
	private static void registerMouseListeners() {
		if (!enabled) {
			return;
		}

		// Closing tooltips is done in AppW.closePopups
		Event.addNativePreviewHandler(new NativePreviewHandler() {
			@Override
			public void onPreviewNativeEvent(NativePreviewEvent event) {
				if (event.getTypeInt() == Event.ONTOUCHSTART) {
					CancelEventTimer.touchEventOccured();
				}
			}
		});
	}

	// ======================================
	// Timers
	// ======================================

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
		sharedInstance().hideBottomInfoToolTip();
	}
}
