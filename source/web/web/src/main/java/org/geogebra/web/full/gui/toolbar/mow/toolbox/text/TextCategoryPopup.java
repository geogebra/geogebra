package org.geogebra.web.full.gui.toolbar.mow.toolbox.text;

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_MEDIA_TEXT;

import java.util.List;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.web.full.gui.menubar.MainMenu;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.ToolModeIconSpecAdapter;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.components.IconButton;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;
import org.geogebra.web.html5.gui.view.IconSpec;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.toolbox.ToolboxIcon;

public class TextCategoryPopup extends GPopupMenuW implements SetLabels {
	private final IconButton textButton;
	private int lastSelectedMode = -1;
	private final List<Integer> tools;

	/**
	 * Constructor
	 * @param app - application
	 * @param textButton - text icon button
	 */
	public TextCategoryPopup(AppW app, IconButton textButton, List<Integer> tools) {
		super(app);
		this.textButton = textButton;
		this.tools = tools;
		buildGui(tools);
	}

	private void buildGui(List<Integer> tools) {
		for (int mode: tools) {
			addItem(mode);
		}

		popupMenu.selectItem(0);
	}

	private void addItem(int mode) {
		String text = getApp().getToolName(mode);
		ToolboxIcon toolboxIcon = ToolModeIconSpecAdapter.getToolboxIcon(mode);
		IconSpec iconSpec = getApp().getToolboxIconResource().getImageResource(toolboxIcon);

		AriaMenuItem item = MainMenu.getMenuBarItem(iconSpec, text, () -> {});
		item.setScheduledCommand(() -> {
			updateMode(mode);
			updateButton(iconSpec, mode);
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
		buildGui(tools);
	}

	public int getLastSelectedMode() {
		return lastSelectedMode == -1 ? MODE_MEDIA_TEXT : lastSelectedMode;
	}
}
