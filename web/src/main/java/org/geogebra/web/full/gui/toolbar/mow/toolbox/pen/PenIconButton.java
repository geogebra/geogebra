package org.geogebra.web.full.gui.toolbar.mow.toolbox.pen;

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_ERASER;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_HIGHLIGHTER;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_PEN;

import java.util.Arrays;
import java.util.List;
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
	private static final List<Integer> modes = Arrays.asList(MODE_PEN, MODE_HIGHLIGHTER,
			MODE_ERASER);

	/**
	 * Constructor
	 * @param appW - application
	 * @param deselectButtons - deselect other button callback
	 */
	public PenIconButton(AppW appW, Runnable deselectButtons) {
		super(MODE_PEN, appW);
		this.appW = appW;

		AriaHelper.setAriaHasPopup(this);
		addFastClickHandler((event) -> {
			deselectButtons.run();
			showPopup();
			setActive(true);

			AriaHelper.setAriaExpanded(this, true);
		});
	}

	private void showPopup() {
		appW.setMode(getMode());
		if (penPopup == null) {
			penPopup = new PenCategoryPopup(appW, modes, getUpdateButtonCallback());
			penPopup.setAutoHideEnabled(false);
			penPopup.addCloseHandler((e) -> AriaHelper.setAriaExpanded(this, false));
		}
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
			if (penPopup != null) {
				penPopup.update();
			}
		});
	}

	@Override
	public int getMode() {
		return penPopup == null || penPopup.getLastSelectedMode() == -1
				? MODE_PEN : penPopup.getLastSelectedMode();
	}

	@Override
	public boolean containsMode(int mode) {
		return modes.contains(mode);
	}

	@Override
	public void setLabels() {
		if (penPopup != null) {
			penPopup.setLabels();
		}
	}
}
