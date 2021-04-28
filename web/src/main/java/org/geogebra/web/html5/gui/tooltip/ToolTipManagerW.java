package org.geogebra.web.html5.gui.tooltip;

import org.geogebra.common.javax.swing.SwingConstants;
import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.timer.client.Timer;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Event;

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
	private AppW app;

	private ComponentSnackbar snackbar;
	private Timer timer;
	private boolean blockToolTip = true;
	private boolean keyboardVisible;
	private boolean lastTipVisible = false;
	private boolean isSmall = false;
	private boolean moveBtnMoved = false;

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
		if (!enabled) {
			return;
		}
		registerMouseListeners();
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
	 * @param title
	 *            title of snackbar
	 * @param helpText
	 *            text of snackbar
	 * @param buttonText
	 *           text of button
	 * @param appw
	 *            app for positioning
	 * @param kb
	 *            whether keyboard is open
	 */
	public void showBottomInfoToolTip(String title, final String helpText,
			String buttonText, String url, final AppW appw, boolean kb) {
		if (blockToolTip || appw == null) {
			return;
		}
		
		this.app = appw;
		keyboardVisible = kb;
		isSmall = false;

		if (snackbar != null) {
			appw.getPanel().remove(snackbar);
		}
		snackbar = new ComponentSnackbar(app, app.getLocalization(),
				title, helpText, buttonText);
		snackbar.setButtonAction(() -> {
			if ("Share".equals(buttonText)) {
					app.share();
			} else {
				app.getFileManager().open(url);
			}
		});
		appw.getPanel().add(snackbar);

		Style style = snackbar.getElement().getStyle();
		// Toolbar on bottom - tooltip needs to be positioned higher so it
		// doesn't overlap with the toolbar
		if (appw.getToolbarPosition() == SwingConstants.SOUTH) {
			if (app.isWhiteboardActive()) {
				style.setTop((appw.getHeight() - 220) - 20 * lines(title),
						Unit.PX);
			} else {
				style.setTop((appw.getHeight() - (kb ? 250 : 70) - 50)
						- 20 * lines(title), Unit.PX);
			}
		}
		// Toolbar on top
		else {
			if (app.isUnbundled()) {
				if (appw.getAppletFrame().isKeyboardShowing()) {
					style.setTop((appw.getHeight() - 310), Unit.PX);
				} else {
					snackbar.getElement().getStyle().clearTop();
					if (!lastTipVisible && buttonText != null) {
						//animateIn(appw, snackbar);
					} else {
						snackbar.getElement().getStyle().setBottom(0, Unit.PX);
					}
					moveBtnMoved = appw.getGuiManager()
							.moveMoveFloatingButtonUp(8, snackbar.getOffsetWidth(), isSmall);
				}

			} else {
				style.setTop((appw.getHeight() - (kb ? 250 : 70)) - 20 * lines(title), Unit.PX);
			}
		}
		snackbar.show();
		lastTipVisible = true;
		if ((buttonText == "Share"
				|| (buttonText == "Help" && helpURL != null
						&& helpURL.length() > 0))) {
				scheduleHideBottom();
		}
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
		showBottomInfoToolTip(null, text, null, null, appw,
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
	 * Hide the bottom tooltip
	 */
	public void hideBottomInfoToolTip() {
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
		Event.addNativePreviewHandler(event -> {
			if (event.getTypeInt() == Event.ONTOUCHSTART) {
				CancelEventTimer.touchEventOccured();
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
