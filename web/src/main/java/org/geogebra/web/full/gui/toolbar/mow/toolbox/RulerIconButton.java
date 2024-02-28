package org.geogebra.web.full.gui.toolbar.mow.toolbox;

import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;

public class RulerIconButton extends IconButton {

	public RulerIconButton(AppW appW, SVGResource icon, String ariaLabel, String dataTitle,
			String dataTest) {
		super(appW.getLocalization(), icon, ariaLabel, dataTitle, dataTest, null);
		addFastClickHandler((event) -> {
			RulerPopup rulerPopup = new RulerPopup(appW, this);
			rulerPopup.showAtPoint(getAbsoluteLeft() + getOffsetWidth() + 8,
					(int) (getAbsoluteTop() - appW.getAbsTop()));
		});
	}
}
