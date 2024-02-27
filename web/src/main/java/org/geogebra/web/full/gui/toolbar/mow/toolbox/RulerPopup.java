package org.geogebra.web.full.gui.toolbar.mow.toolbox;

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_PROTRACTOR;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_RULER;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_TRIANGLE_PROTRACTOR;

import org.geogebra.web.full.css.ToolbarSvgResources;
import org.geogebra.web.full.gui.menubar.MainMenu;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;

public class RulerPopup extends GPopupMenuW {

	public RulerPopup(AppW app) {
		super(app);
		buildGui();
	}

	private void buildGui() {
		addItem(ToolbarSvgResources.INSTANCE.mode_ruler(),
				getApp().getLocalization().getMenu("Ruler"), MODE_RULER);

		addItem(ToolbarSvgResources.INSTANCE.mode_protractor(),
				getApp().getLocalization().getMenu("Protractor"), MODE_PROTRACTOR);

		addItem(ToolbarSvgResources.INSTANCE.mode_protractor(),
				getApp().getLocalization().getMenu("Triangle Protractor"),
				MODE_TRIANGLE_PROTRACTOR);
	}

	private void addItem(SVGResource image, String text, int mode) {
		AriaMenuItem item = new AriaMenuItem(MainMenu.getMenuBarHtmlClassic(
				image.getSafeUri().asString(), text), true, () -> {
			getApp().setMode(mode);
			// TODO update ruler button
		});
		addItem(item);
	}


}