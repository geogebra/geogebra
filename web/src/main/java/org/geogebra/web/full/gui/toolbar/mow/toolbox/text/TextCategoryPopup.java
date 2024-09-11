package org.geogebra.web.full.gui.toolbar.mow.toolbox.text;

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_EQUATION;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_MEDIA_TEXT;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.web.full.gui.app.GGWToolBar;
import org.geogebra.web.full.gui.menubar.MainMenu;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.components.IconButton;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;
import org.geogebra.web.resources.SVGResourcePrototype;

public class TextCategoryPopup extends GPopupMenuW implements SetLabels {
	private IconButton textButton;
	private int lastSelectedMode = -1;

	/**
	 * Constructor
	 * @param app - application
	 * @param textButton - text icon button
	 */
	public TextCategoryPopup(AppW app, IconButton textButton) {
		super(app);
		this.textButton = textButton;
		buildGui();
	}

	private void buildGui() {
		addItem(MODE_MEDIA_TEXT);
		addItem(MODE_EQUATION);

		popupMenu.selectItem(0);
	}

	private void addItem(int mode) {
		String text = getApp().getToolName(mode);

		AriaMenuItem item = MainMenu.getMenuBarItem(
				SVGResourcePrototype.EMPTY, text, () -> {});
		GGWToolBar.getImageResource(mode, getApp(), image -> {
			item.setResource(image);
			item.setScheduledCommand(() -> {
				updateMode(mode);
				updateButton((SVGResource) image, mode);
				updateSelection(item);
			});
		});
		addItem(item);
	}

	private void updateMode(int mode) {
		getApp().setMode(mode);
		lastSelectedMode = mode;
	}

	private void updateButton(SVGResource image, int mode) {
		String fillColor = textButton.isActive()
				? getApp().getGeoGebraElement().getDarkColor(getApp().getFrameElement())
				: GColor.BLACK.toString();
		textButton.updateImgAndTxt(image.withFill(fillColor), mode, getApp());
	}

	private void updateSelection(AriaMenuItem item) {
		popupMenu.unselect();
		popupMenu.selectItem(item);
	}

	@Override
	public void setLabels() {
		clearItems();
		buildGui();
	}

	public int getLastSelectedMode() {
		return lastSelectedMode == -1 ? MODE_MEDIA_TEXT : lastSelectedMode;
	}
}
