package org.geogebra.web.html5.gui.tooltip;

import org.geogebra.web.html5.main.AppW;
import org.gwtproject.dom.client.Style;
import org.gwtproject.dom.style.shared.Unit;

public final class ToolTipManagerW {
	private ComponentSnackbar snackbar;
	private boolean blockToolTip = true;

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
	 * @param title -title of snackbar
	 * @param helpText - text of snackbar
	 * @param buttonText - text of button
	 * @param appW - app for positioning
	 * @param showDuration - how long should the tooltip be visible
	 */
	public void showBottomInfoToolTip(String title, final String helpText,
			String buttonText, String url, final AppW appW, int showDuration) {
		if (blockToolTip || appW == null) {
			return;
		}

		createSnackbar(appW, title, helpText, buttonText, showDuration, url);

		Style style = snackbar.getElement().getStyle();
		if (appW.isWhiteboardActive()) {
			style.setLeft((appW.getWidth() - snackbar.getOffsetWidth()) / 2, Unit.PX);
		} else {
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

	private void createSnackbar(AppW appW, String title, String helpText, String buttonText,
			int showDuration, String url) {
		if (snackbar != null) {
			appW.getAppletFrame().remove(snackbar);
		}
		snackbar = new ComponentSnackbar(appW, title, helpText, buttonText);
		snackbar.setShowDuration(showDuration);
		snackbar.setButtonAction(() -> {
			if ("Share".equals(buttonText)) {
				appW.share();
			} else {
				appW.getFileManager().open(url);
			}
		});
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
		showBottomInfoToolTip(title, helpText, buttonText, url, appW,
				ComponentSnackbar.DEFAULT_TOOLTIP_DURATION);
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
	 * hide currently shown tooltip without delay
	 */
	public void hideTooltip() {
		if (snackbar != null) {
			snackbar.hide();
		}
	}
}
