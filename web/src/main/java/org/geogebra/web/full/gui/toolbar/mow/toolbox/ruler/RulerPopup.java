package org.geogebra.web.full.gui.toolbar.mow.toolbox.ruler;

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_PROTRACTOR;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_RULER;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_TRIANGLE_PROTRACTOR;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.web.full.css.ToolbarSvgResources;
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
		addItem(ToolbarSvgResources.INSTANCE.mode_ruler(),
				getApp().getLocalization().getMenu("Ruler"), MODE_RULER);

		if (getApp().getVendorSettings().hasBothProtractor()) {
			addItem(ToolbarSvgResources.INSTANCE.mode_protractor(),
					getApp().getLocalization().getMenu("Protractor"), MODE_PROTRACTOR);
		}

		addItem(ToolbarSvgResources.INSTANCE.mode_triangle_protractor(),
				getApp().getLocalization().getMenu("TriangleProtractor"),
				MODE_TRIANGLE_PROTRACTOR);

		popupMenu.selectItem(0);
	}

	private void addItem(SVGResource image, String text, int mode) {
		AriaMenuItem item = new AriaMenuItem(MainMenu.getMenuBarHtmlClassic(
				image.getSafeUri().asString(), text), true, () -> {});
		item.setScheduledCommand(() -> {
			rulerButton.removeTool();
			activeRulerMode = mode;
			String fillColor = rulerButton.isActive()
					? getApp().getGeoGebraElement().getDarkColor(getApp().getFrameElement())
					: GColor.BLACK.toString();
			rulerButton.updateImgAndTxt(image.withFill(fillColor), mode, getApp());
			rulerButton.handleRuler();
			setHighlight(item);
		});
		addItem(item);
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
		buildGui();
	}
}