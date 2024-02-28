package org.geogebra.web.full.gui.toolbar.mow.toolbox;

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_MOVE;

import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;

public class RulerIconButton extends IconButton {
	private RulerPopup rulerPopup;

	public RulerIconButton(AppW appW, SVGResource icon, String ariaLabel, String dataTitle,
			String dataTest) {
		super(appW.getLocalization(), icon, ariaLabel, dataTitle, dataTest, null);
		addFastClickHandler((event) -> {
			if (rulerPopup == null) {
				rulerPopup = new RulerPopup(appW, this);
			}
			rulerPopup.showAtPoint(getAbsoluteLeft() + getOffsetWidth() + 8,
					(int) (getAbsoluteTop() - appW.getAbsTop()));

			setActive(!isActive(), appW.getDarkColor());
			if (isActive()) {
				appW.setMode(rulerPopup.getActiveRulerType());
			} else {
				appW.setMode(MODE_MOVE);
			}
		});
	}
}
