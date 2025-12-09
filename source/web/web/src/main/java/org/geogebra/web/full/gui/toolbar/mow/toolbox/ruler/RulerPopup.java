/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.full.gui.toolbar.mow.toolbox.ruler;

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_PROTRACTOR;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_RULER;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_TRIANGLE_PROTRACTOR;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.web.full.gui.app.GGWToolBar;
import org.geogebra.web.full.gui.menubar.MainMenu;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.ToolModeIconSpecAdapter;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.view.IconSpec;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.toolbox.ToolboxIcon;

public class RulerPopup extends GPopupMenuW implements SetLabels {
	private final RulerIconButton rulerButton;
	private int activeRulerMode = MODE_RULER;

	/**
	 * Constructor
	 * @param app - application
	 */
	public RulerPopup(AppW app, RulerIconButton rulerButton) {
		super(app);
		this.rulerButton = rulerButton;
		buildGui();
	}

	private void buildGui() {
		addItem(getApp().getLocalization().getMenu("Ruler"), MODE_RULER);
		for (int mode: getApp().getVendorSettings().getProtractorTools(
				getApp().getLocalization().getLanguage())) {
			addItem(getApp().getLocalization().getMenu(EuclidianConstants.getModeText(mode)),
					mode);
		}
		popupMenu.selectItem(activeRulerMode == MODE_RULER ? 0 : 1);
	}

	private void addItem(String text, int mode) {
		ToolboxIcon toolboxIcon = ToolModeIconSpecAdapter.getToolboxIcon(mode);
		IconSpec iconSpec = getApp().getToolboxIconResource().getImageResource(toolboxIcon);

		AriaMenuItem item = MainMenu.getMenuBarItem(iconSpec, text, () -> {});
		GGWToolBar.getImageResource(mode, getApp(), item);
		item.setScheduledCommand(() -> {
			activeRulerMode = mode;
			updateRulerButton(mode);
			setHighlight(item);
		});

		addItem(item);
	}

	private void updateRulerButton(int mode) {
		ToolboxIcon toolboxIcon = ToolModeIconSpecAdapter.getToolboxIcon(mode);
		IconSpec iconSpec = getApp().getToolboxIconResource().getImageResource(toolboxIcon);

		String fillColor = rulerButton.isActive()
				? getApp().getGeoGebraElement().getDarkColor(getApp().getFrameElement())
				: GColor.BLACK.toString();
		rulerButton.removeTool();
		rulerButton.updateImgAndTxt(iconSpec.withFill(fillColor), mode, getApp());
		rulerButton.handleRuler();
	}

	private void setHighlight(AriaMenuItem highlighted) {
		popupMenu.unselect();
		popupMenu.selectItem(highlighted);
	}

	public int getActiveRulerType() {
		return activeRulerMode;
	}

	/**
	 * Updates selection highlighting in the popup menu
	 */
	public void updatePopupSelection() {
		AriaMenuItem selectedItem = popupMenu.getSelectedItem();
		if (selectedItem != null) {
			Dom.toggleClass(selectedItem, "selectedItem", rulerButton.isActive());
		}
	}

	/**
	 * Rebuilds the GUI (e.g. language changes)
	 */
	@Override
	public void setLabels() {
		clearItems();
		boolean triangleSupported = getApp().getVendorSettings().getProtractorTools(
				getApp().getLocalization().getLanguage()).contains(MODE_TRIANGLE_PROTRACTOR);
		if (activeRulerMode == MODE_TRIANGLE_PROTRACTOR && !triangleSupported) {
			activeRulerMode = MODE_PROTRACTOR;
			updateRulerButton(activeRulerMode);
		}
		buildGui();
	}
}