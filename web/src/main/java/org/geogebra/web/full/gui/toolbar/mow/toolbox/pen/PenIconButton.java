package org.geogebra.web.full.gui.toolbar.mow.toolbox.pen;

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_ERASER;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_HIGHLIGHTER;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_PEN;

import java.util.Arrays;
import java.util.function.Consumer;

import org.geogebra.web.full.gui.app.GGWToolBar;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.ToolboxPopupPositioner;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.components.IconButton;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;

public class PenIconButton extends IconButton {
	private final AppW appW;
	private PenCategoryPopup penPopup;

	/**
	 * Constructor
	 * @param appW - application
	 * @param deselectButtons - deselect other button callback
	 */
	public PenIconButton(AppW appW, Runnable deselectButtons) {
		super(MODE_PEN, appW);
		this.appW = appW;

		AriaHelper.setAriaHasPopup(this);
		if (penPopup == null) {
			penPopup = new PenCategoryPopup(appW, Arrays.asList(MODE_PEN, MODE_HIGHLIGHTER,
					MODE_ERASER), getUpdateButtonCallback());
			penPopup.setAutoHideEnabled(false);
		}
		addFastClickHandler((event) -> {
			deselectButtons.run();
			showPopup();
			setActive(true);

			AriaHelper.setAriaExpanded(this, true);
		});
		penPopup.addCloseHandler((e) -> AriaHelper.setAriaExpanded(this, false));
	}

	private void showPopup() {
		appW.setMode(getMode());
		penPopup.update();
		if (penPopup.isShowing()) {
			penPopup.hide();
		} else {
			ToolboxPopupPositioner.showRelativeToToolbox(penPopup, this, appW);
		}
	}

	private Consumer<Integer> getUpdateButtonCallback() {
		return mode -> GGWToolBar.getImageResource(mode, appW, image -> {
			updateImgAndTxt((SVGResource) image, mode, appW);
			setActive(true);
			penPopup.update();
		});
	}

	@Override
	public int getMode() {
		return penPopup.getLastSelectedMode() == -1 ? MODE_PEN : penPopup.getLastSelectedMode();
	}

	@Override
	public void setLabels() {
		if (penPopup != null) {
			penPopup.setLabels();
		}
	}
}
