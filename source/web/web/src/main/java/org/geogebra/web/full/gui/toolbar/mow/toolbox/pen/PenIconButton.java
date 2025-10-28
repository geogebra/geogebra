package org.geogebra.web.full.gui.toolbar.mow.toolbox.pen;

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_PEN;

import java.util.List;

import org.geogebra.web.full.gui.toolbar.mow.toolbox.ToolboxPopupPositioner;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.components.ToolIconButton;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.view.IconSpec;
import org.geogebra.web.html5.main.AppW;

public class PenIconButton extends ToolIconButton {
	private final AppW appW;
	private PenCategoryPopup penPopup;
	private final List<Integer> modes;

	/**
	 * Constructor
	 * @param appW - application
	 * @param deselectButtons - deselect other button callback
	 */
	public PenIconButton(AppW appW, List<Integer> modes, Runnable deselectButtons) {
		super(MODE_PEN, appW);
		this.appW = appW;
		this.modes = modes;

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
			penPopup = new PenCategoryPopup(appW, modes, this::updateButton);
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

	private void updateButton(int mode) {
		IconSpec icon = getIconFromMode(mode, appW.getToolboxIconResource());
		updateImgAndTxt(icon, mode, appW);
		setActive(true);
		if (penPopup != null) {
			penPopup.update();
		}
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
		super.setLabels();
		if (penPopup != null) {
			penPopup.setLabels();
		}
	}
}
