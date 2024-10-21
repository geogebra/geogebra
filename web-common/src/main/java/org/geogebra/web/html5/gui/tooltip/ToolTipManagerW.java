package org.geogebra.web.html5.gui.tooltip;

import org.geogebra.web.html5.main.AppW;
import org.gwtproject.dom.client.Style;
import org.gwtproject.dom.style.shared.Unit;

public final class ToolTipManagerW {
	private ComponentSnackbar snackbar;
	private boolean blockToolTip = true;

	/**
	 * @param blockToolTip
	 *            whether to block tooltips
	 */
	public void setBlockToolTip(boolean blockToolTip) {
		this.blockToolTip = blockToolTip;
	}

	/**
	 * @param toolTip - data for the tooltip
	 * @param appW - app for positioning
	 * @param showDuration - how long should the tooltip be visible
	 */
	public void showBottomInfoToolTip(ToolTip toolTip, final AppW appW, int showDuration) {
		if (blockToolTip || appW == null) {
			return;
		}

		createSnackbar(appW, toolTip, showDuration);

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

	private void createSnackbar(AppW appW, ToolTip toolTip,
			int showDuration) {
		if (snackbar != null) {
			appW.getAppletFrame().remove(snackbar);
		}
		snackbar = new ComponentSnackbar(appW, toolTip);
		snackbar.setShowDuration(showDuration);
		snackbar.setButtonAction(() -> {
			if ("Share".equals(toolTip.buttonTransKey)) {
				appW.share();
			} else {
				appW.getFileManager().open(toolTip.url);
			}
		});
	}

	/**
	 * displays the given message
	 * @param text
	 *            String
	 * @param appW
	 *            application
	 */
	public void showBottomMessage(String text, AppW appW) {
		showBottomMessage(text, appW, ToolTip.Role.INFO);
	}

	/**
	 * displays the given message
	 * @param text
	 *            String
	 * @param appW
	 *            application
	 */
	public void showBottomMessage(String text, AppW appW, ToolTip.Role role) {
		blockToolTip = false;
		showBottomInfoToolTip(new ToolTip(text, role), appW,
				ComponentSnackbar.DEFAULT_TOOLTIP_DURATION);
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
