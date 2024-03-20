package org.geogebra.web.full.gui.toolbar.mow.toolbox.components;

import static org.geogebra.web.full.gui.toolbar.mow.toolbox.ToolboxMow.TOOLBOX_PADDING;

import java.util.List;

import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;

public class IconButtonWithPopup extends IconButton {
	private final AppW appW;
	private final List<Integer> tools;

	public IconButtonWithPopup(AppW appW, SVGResource icon, String ariaLabel,
			List<Integer> tools, Runnable deselectButtons) {
		super(appW, icon, ariaLabel, ariaLabel, "", () -> {}, null);
		this.appW = appW;
		this.tools = tools;

		AriaHelper.setAriaHasPopup(this);
		addFastClickHandler((event) -> {
			deselectButtons.run();
			initPopupAndShow();
			setActive(true, appW.getGeoGebraElement().getDarkColor(appW.getFrameElement()));
		});
	}

	private void initPopupAndShow() {
		CategoryPopup iconButtonPopup = new CategoryPopup(appW, tools);

		if (!iconButtonPopup.isMenuShown()) {
			iconButtonPopup.show(getAbsoluteLeft() + getOffsetWidth() + TOOLBOX_PADDING,
					(int) (getAbsoluteTop() - appW.getAbsTop()));
			AriaHelper.setAriaExpanded(this, true);
		}

		iconButtonPopup.getPopupPanel().addCloseHandler(e -> {
			deactivate();
			AriaHelper.setAriaExpanded(this, false);
		});
	}
}
