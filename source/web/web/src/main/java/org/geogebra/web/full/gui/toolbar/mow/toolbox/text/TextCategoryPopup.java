package org.geogebra.web.full.gui.toolbar.mow.toolbox.text;

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_EQUATION;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_MEDIA_TEXT;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.web.full.gui.menubar.MainMenu;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.components.IconButton;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;
import org.geogebra.web.html5.gui.view.IconSpec;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.toolbox.ToolboxIcon;
import org.geogebra.web.html5.main.toolbox.ToolboxIconResource;

public class TextCategoryPopup extends GPopupMenuW implements SetLabels {
	private final IconButton textButton;
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
		ToolboxIconResource res = getApp().getToolboxIconResource();
		addItem(MODE_MEDIA_TEXT, res.getImageResource(ToolboxIcon.TEXT));
		addItem(MODE_EQUATION, res.getImageResource(ToolboxIcon.EQUATION));

		popupMenu.selectItem(0);
	}

	private void addItem(int mode, IconSpec icon) {
		String text = getApp().getToolName(mode);

		AriaMenuItem item = MainMenu.getMenuBarItem(icon, text, () -> {});
		item.setScheduledCommand(() -> {
			updateMode(mode);
			updateButton(icon, mode);
			updateSelection(item);
			hide();
		});
		addItem(item);
	}

	private void updateMode(int mode) {
		getApp().setMode(mode);
		lastSelectedMode = mode;
	}

	private void updateButton(IconSpec image, int mode) {
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
