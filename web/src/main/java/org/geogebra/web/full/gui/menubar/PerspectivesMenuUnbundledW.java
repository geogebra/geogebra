package org.geogebra.web.full.gui.menubar;

import org.geogebra.common.main.Feature;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.util.AriaMenuItem;
import org.geogebra.web.html5.gui.util.ImgResourceHelper;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.resources.client.ResourcePrototype;

/**
 * Web implementation of PerspectivesMenu
 */
public class PerspectivesMenuUnbundledW extends GMenuBar {

	/** Application */
	AppW app;

	/**
		 * @param app application
		 */
	public PerspectivesMenuUnbundledW(AppW app) {
		super("apps", app);
		this.app = app;
		if (app.isUnbundledOrWhiteboard()) {
			addStyleName("matStackPanelNoOpacity");
		} else {
			addStyleName("GeoGebraMenuBar");
		}
		initActions();
		update();
	}

	private void update() {
		// do nothing
	}

	private void initActions() {
		addMenuItem("graphing", "GraphingCalculator",
				MaterialDesignResources.INSTANCE.graphing());
		addMenuItem("geometry", "Geometry",
				MaterialDesignResources.INSTANCE.geometry());
		addMenuItem("3d", "Graphing3D",
				MaterialDesignResources.INSTANCE.graphing3D());
		addMenuItem("classic", "math_apps",
				MaterialDesignResources.INSTANCE.geogebra_color());
	}

	private void addMenuItem(String appId,
			String translationKey,
			ResourcePrototype icon) {
		StringBuilder link = new StringBuilder("https://www.geogebra.org/");
		if (app.has(Feature.TUBE_BETA)) {
			link = new StringBuilder("https://beta.geogebra.org/");
		}
		link.append(appId);
		AriaMenuItem item = addItem(
				getHTMLwithLink(ImgResourceHelper.safeURI(icon),
				app.getLocalization()
						.getMenu(translationKey),
				link.toString()),
				true, new MenuCommand(app) {

					@Override
					public void doExecute() {
						// do nothing
					}
				});
		AriaHelper.setLabel(item,
				app.getLocalization().getMenu(translationKey));
	}

	/**
	 * @param img
	 *            - image of the menu item
	 * @param s
	 *            - title of the menu item
	 * @param link
	 *            -
	 * @return html code
	 */
	private String getHTMLwithLink(String img, String s, String link) {
		String imgHTML = "<img src=\"" + img
				+ "\" draggable=\"false\"><span>"
				+ app.getLocalization().getMenu(s) + "</span>";
		return "<a class=\"menuLink\" href=\" " + link + "\" target=\"_blank\">"
				+ imgHTML + "</a>";
	}
}

