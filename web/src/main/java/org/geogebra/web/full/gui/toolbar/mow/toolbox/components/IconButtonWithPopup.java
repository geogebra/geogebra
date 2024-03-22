package org.geogebra.web.full.gui.toolbar.mow.toolbox.components;

import static org.geogebra.web.full.gui.toolbar.mow.toolbox.ToolboxMow.TOOLBOX_PADDING;

import java.util.List;
import java.util.function.Consumer;

import org.geogebra.web.full.css.ToolbarSvgResources;
import org.geogebra.web.full.gui.app.GGWToolBar;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;
import org.gwtproject.resources.client.ResourcePrototype;

public class IconButtonWithPopup extends IconButton {
	private final AppW appW;
	private CategoryPopup categoryPopup;

	/**
	 * Constructor
	 * @param appW - application
	 * @param icon - image
	 * @param ariaLabel - aria label
	 * @param tools - list of tools
	 * @param deselectButtons - deselect button callback
	 */
	public IconButtonWithPopup(AppW appW, SVGResource icon, String ariaLabel, List<Integer> tools,
			Runnable deselectButtons) {
		super(appW, icon, ariaLabel, ariaLabel, () -> {}, null);
		this.appW = appW;
		AriaHelper.setAriaHasPopup(this);

		addFastClickHandler(source -> {
			deselectButtons.run();
			setActive(true, appW.getGeoGebraElement().getDarkColor(appW.getFrameElement()));

			if (categoryPopup == null) {
				categoryPopup = new CategoryPopup(appW, tools, getUpdateButtonCallback());
			}

			AriaHelper.setAriaExpanded(this, true);
			appW.setMode(categoryPopup.getLastSelectedMode());
			categoryPopup.show();
			categoryPopup.setPopupPosition(getAbsoluteLeft() + getOffsetWidth() + TOOLBOX_PADDING,
					(int) (getAbsoluteTop() - appW.getAbsTop()));

			categoryPopup.addCloseHandler((event) -> AriaHelper.setAriaExpanded(this, false));
		});
	}

	private Consumer<Integer> getUpdateButtonCallback() {
		return mode -> {
			ResourcePrototype image = GGWToolBar.getImageURLNotMacro(ToolbarSvgResources.INSTANCE,
					mode, appW);
			updateImgAndTxt((SVGResource) image, mode, appW);
		};
	}
}
