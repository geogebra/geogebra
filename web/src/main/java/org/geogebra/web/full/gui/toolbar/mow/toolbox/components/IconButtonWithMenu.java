package org.geogebra.web.full.gui.toolbar.mow.toolbox.components;

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_SELECT_MOW;
import static org.geogebra.web.full.gui.toolbar.mow.toolbox.ToolboxMow.TOOLBOX_PADDING;

import java.util.List;

import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;

public class IconButtonWithMenu extends IconButton {
	private final AppW appW;
	private final List<Integer> tools;

	/**
	 * Constructor
	 * @param appW - application
	 * @param icon - image
	 * @param ariaLabel - aria label
	 * @param tools - list of tools showing in the popup
	 * @param deselectButtons - deselect button callback
	 */
	public IconButtonWithMenu(AppW appW, SVGResource icon, String ariaLabel,
			List<Integer> tools, Runnable deselectButtons) {
		super(appW, icon, ariaLabel, ariaLabel, "", () -> {}, null);
		this.appW = appW;
		this.tools = tools;

		AriaHelper.setAriaHasPopup(this);
		addFastClickHandler((event) -> {
			deselectButtons.run();
			initPopupAndShow();
			setActive(true);
		});
	}

	private void initPopupAndShow() {
		CategoryMenuPopup iconButtonPopup = new CategoryMenuPopup(appW, tools);
		iconButtonPopup.show(getAbsoluteLeft() + getOffsetWidth() + TOOLBOX_PADDING,
				(int) (getAbsoluteTop() - appW.getAbsTop()));
		AriaHelper.setAriaExpanded(this, true);

		iconButtonPopup.getPopupPanel().addCloseHandler(e -> {
			deactivate();
			AriaHelper.setAriaExpanded(this, false);
			appW.setMode(MODE_SELECT_MOW);
		});
	}
}
