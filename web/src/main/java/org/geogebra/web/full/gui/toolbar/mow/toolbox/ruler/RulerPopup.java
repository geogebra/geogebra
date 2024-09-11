package org.geogebra.web.full.gui.toolbar.mow.toolbox.ruler;

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_PROTRACTOR;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_RULER;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_TRIANGLE_PROTRACTOR;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.web.full.css.ToolbarSvgResources;
import org.geogebra.web.full.gui.app.GGWToolBar;
import org.geogebra.web.full.gui.menubar.MainMenu;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;

public class RulerPopup extends GPopupMenuW implements SetLabels {
	private RulerIconButton rulerButton;
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

		if (!getApp().getVendorSettings().hasTriangleProtractor(
				getApp().getLocalization().getLanguage())) {
			addItem(getApp().getLocalization().getMenu("Protractor"), MODE_PROTRACTOR);
		} else {
			addItem(getApp().getLocalization().getMenu("TriangleProtractor"),
					MODE_TRIANGLE_PROTRACTOR);
		}
		popupMenu.selectItem(activeRulerMode == MODE_RULER ? 0 : 1);
	}

	private void addItem(String text, int mode) {
		SVGResource image = GGWToolBar.getImageURLNotMacro(
				ToolbarSvgResources.INSTANCE, mode, getApp());
		AriaMenuItem item = MainMenu.getMenuBarItem(image, text, () -> {});
		item.setScheduledCommand(() -> {
			activeRulerMode = mode;
			updateRulerButton(mode);
			setHighlight(item);
		});
		addItem(item);
	}

	private void updateRulerButton(int mode) {
		GGWToolBar.getImageResource(mode, getApp(), image -> {
			String fillColor = rulerButton.isActive()
					? getApp().getGeoGebraElement().getDarkColor(getApp().getFrameElement())
					: GColor.BLACK.toString();
			rulerButton.removeTool();
			rulerButton.updateImgAndTxt(((SVGResource) image).withFill(fillColor), mode, getApp());
			rulerButton.handleRuler();
		});
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
		Dom.toggleClass(popupMenu.getSelectedItem(), "selectedItem", rulerButton.isActive());
	}

	/**
	 * Rebuilds the GUI (e.g. language changes)
	 */
	@Override
	public void setLabels() {
		clearItems();
		boolean triangleSupported = getApp().getVendorSettings().hasTriangleProtractor(
				getApp().getLocalization().getLanguage());
		if (activeRulerMode == MODE_TRIANGLE_PROTRACTOR && !triangleSupported
			|| activeRulerMode == MODE_PROTRACTOR && triangleSupported) {
			activeRulerMode = triangleSupported ? MODE_TRIANGLE_PROTRACTOR : MODE_PROTRACTOR;
			updateRulerButton(activeRulerMode);
		}
		buildGui();
	}
}