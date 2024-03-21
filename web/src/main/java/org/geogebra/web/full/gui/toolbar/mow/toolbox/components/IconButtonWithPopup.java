package org.geogebra.web.full.gui.toolbar.mow.toolbox.components;

import static org.geogebra.web.full.gui.toolbar.mow.toolbox.ToolboxMow.TOOLBOX_PADDING;

import java.util.List;
import java.util.function.Consumer;

import org.geogebra.web.full.css.ToolbarSvgResources;
import org.geogebra.web.full.gui.app.GGWToolBar;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;
import org.gwtproject.resources.client.ResourcePrototype;

public class IconButtonWithPopup extends IconButton {
	private final AppW appW;
	private CategoryPopup categoryPopup;

	public IconButtonWithPopup(AppW appW, SVGResource icon, String ariaLabel, List<Integer> tools) {
		super(appW, icon, ariaLabel, ariaLabel, () -> {}, null);
		this.appW = appW;
		addFastClickHandler(source -> {
			if (categoryPopup == null) {
				categoryPopup = new CategoryPopup(appW, tools, getUpdateButtonCallback());
			}

			categoryPopup.show();
			categoryPopup.setPopupPosition(getAbsoluteLeft() + getOffsetWidth() + TOOLBOX_PADDING,
					(int) (getAbsoluteTop() - appW.getAbsTop()));
		});
	}

	private Consumer<Integer> getUpdateButtonCallback() {
		return mode -> {
			ResourcePrototype image = GGWToolBar.getImageURLNotMacro(ToolbarSvgResources.INSTANCE,
					mode, appW);
			updateImgAndTxt((SVGResource) image, mode, appW);
			deactivate();
		};
	}
}
