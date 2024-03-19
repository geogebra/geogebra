package org.geogebra.web.full.gui.toolbar.mow.toolbox.components;

import static org.geogebra.web.full.gui.toolbar.mow.toolbox.ToolboxMow.TOOLBOX_PADDING;

import java.util.List;

import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;

public class IconButtonWithPopup extends IconButton {

	public IconButtonWithPopup(AppW appW, SVGResource icon, String ariaLabel,
			List<Integer> tools) {
		super(appW, icon, ariaLabel, ariaLabel, "", () -> {}, () -> {});
		addFastClickHandler((event) -> {
			CategoryPopup iconButtonPopup = new CategoryPopup(appW, tools);
			iconButtonPopup.show(getAbsoluteLeft() + getOffsetWidth() + TOOLBOX_PADDING,
					(int) (getAbsoluteTop() - appW.getAbsTop()));
			iconButtonPopup.getPopupPanel().addCloseHandler(event1 -> setActive(false,
					appW.getGeoGebraElement().getDarkColor(appW.getFrameElement())));
		});
	}
}
