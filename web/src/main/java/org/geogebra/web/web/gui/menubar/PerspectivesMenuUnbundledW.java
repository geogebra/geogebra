package org.geogebra.web.web.gui.menubar;

import org.geogebra.common.main.Feature;
import org.geogebra.web.html5.gui.util.ImgResourceHelper;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.css.MaterialDesignResources;

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
		super(true, "apps", app);
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
		addGraphingGeometryMenuItems("graphing", "GraphingCalculator",
				MaterialDesignResources.INSTANCE.graphing());
		addGraphingGeometryMenuItems("geometry", "Geometry",
				MaterialDesignResources.INSTANCE.geometry());
		addGraphingGeometryMenuItems("classic", "math_apps",
				MaterialDesignResources.INSTANCE.geogebra_color());
	}

	private void addGraphingGeometryMenuItems(String appId,
			String translationKey,
			ResourcePrototype icon) {
		StringBuilder link = new StringBuilder("https://www.geogebra.org/");
		if (app.has(Feature.TUBE_BETA)) {
			link = new StringBuilder("https://beta.geogebra.org/");
		}
		link.append(appId);
		addItem(getHTMLwithLink(ImgResourceHelper.safeURI(icon),
				app.getLocalization()
						.getMenu(translationKey),
				link.toString()),
				true, new MenuCommand(app) {

					@Override
					public void doExecute() {
						// do nothing
					}
				});
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
		return "<a href=\" " + link + "\" target=\"_blank\">"
				+ imgHTML + "</a>";
	}
}

