package org.geogebra.web.html5.gui.tooltip;

import org.geogebra.web.html5.gui.util.CancelEventTimer;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Event;

public final class ToolTipManagerW {
	private ComponentSnackbar snackbar;
	private boolean blockToolTip = true;
	private static boolean enabled = true;

	/** Singleton instance of ToolTipManager. */
	final static ToolTipManagerW SHARED_INSTANCE = new ToolTipManagerW();

	/**
	 * Constructor
	 */
	private ToolTipManagerW() {
		initTooltipManagerW();
	}

	/**
	 * All methods are accessed from this instance.
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

	/**
	 * @param title
	 *            title of snackbar
	 * @param helpText
	 *            text of snackbar
	 * @param buttonText
	 *           text of button
	 * @param appW
	 *            app for positioning
	 */
	public void showBottomInfoToolTip(String title, final String helpText,
			String buttonText, String url, final AppW appW) {
		if (blockToolTip || appW == null) {
			return;
		}

		if (snackbar != null) {
			appW.getPanel().remove(snackbar);
		}
		snackbar = new ComponentSnackbar(appW, title, helpText, buttonText);
		snackbar.setButtonAction(() -> {
			if ("Share".equals(buttonText)) {
				appW.share();
			} else {
				appW.getFileManager().open(url);
			}
		});

		Style style = snackbar.getElement().getStyle();
		if (appW.isWhiteboardActive()) {
			style.setLeft((appW.getWidth() - snackbar.getOffsetWidth()) / 2, Unit.PX);
		}
		else {
			if (appW.getAppletFrame().isKeyboardShowing()) {
				style.setBottom(236, Unit.PX); // 8px higher then keyboard
			} else if (appW.isUnbundled() || appW.isSuite()) {
				boolean portrait = appW.getWidth() < appW.getHeight();
				snackbar.addStyleName(portrait ? "portrait" : "landscape");
				// show snackbar above move FAB
				int snackbarRight = (portrait ? 8 : 80) + snackbar.getOffsetWidth();
				int moveTop = appW.getGuiManager().getMoveTopBelowSnackbar(snackbarRight);
				if (moveTop > 0) {
					style.setBottom(moveTop + 8, Unit.PX);
				}
			}
		}
	}

	/**
	 * displays the given message
	 * @param text
	 *            String
	 * @param appW
	 *            application
	 */
	public void showBottomMessage(String text, AppW appW) {
		blockToolTip = false;
		showBottomInfoToolTip(text, null, null, null, appW);
		blockToolTip = true;
	}

	/**
	 * Register mouse listeners to keep track of the mouse position and hide the
	 * toolTip on a mouseDown event.
	 */
	private static void registerMouseListeners() {
		if (!enabled) {
			return;
		}

		Event.addNativePreviewHandler(event -> {
			if (event.getTypeInt() == Event.ONTOUCHSTART) {
				CancelEventTimer.touchEventOccured();
			}
		});
	}

	/**
	 * @param allowToolTips
	 *            global tooltips flag
	 */
	public static void setEnabled(boolean allowToolTips) {
		enabled = allowToolTips;
	}

	/**
	 * hide currently shown tooltip without delay
	 */
	public void hideTooltip() {
		if (snackbar != null) {
			snackbar.hide();
		}
	}
}
