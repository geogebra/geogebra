package org.geogebra.web.web.gui.menubar;

import org.geogebra.common.gui.Layout;
import org.geogebra.web.html5.gui.util.ImgResourceHelper;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.css.MaterialDesignResources;
import org.geogebra.web.web.gui.ImageFactory;
import org.geogebra.web.web.gui.images.PerspectiveResources;

import com.google.gwt.core.shared.GWT;
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
		if (app.isUnbundled()) {
			addStyleName("matStackPanelNoOpacity");
		} else {
			addStyleName("GeoGebraMenuBar");
		}
		initActions();
		update();
	}

	private void update() {
		// TODO Auto-generated method stub

	}

	private void initActions() {

		PerspectiveResources pr = ((ImageFactory) GWT
				.create(ImageFactory.class)).getPerspectiveResources();

		addGraphingGeometryMenuItems(0, pr.menu_icon_algebra24());
		addGraphingGeometryMenuItems(1, pr.menu_icon_geometry24());
		addClassic(MaterialDesignResources.INSTANCE.geogebra_color()
				.getSafeUri().asString());
	}

	private void addGraphingGeometryMenuItems(final int index,
			ResourcePrototype icon) {
		if (Layout.getDefaultPerspectives(index) == null) {
			return;
		}
		StringBuilder link = new StringBuilder("https://beta.geogebra.org/");
		switch (index) {
		default:
			break;
		case 0:
			link.append("graphing");
			break;
		case 1:
			link.append("geometry");
			break;
		}
		addItem(getHTMLwithLink(ImgResourceHelper.safeURI(icon),
				app.getLocalization()
						.getMenu(index == 0 ? "GraphingCalculator"
								: "GeometryCalculator"),
				link.toString()),
				true, new MenuCommand(app) {

					@Override
					public void doExecute() {
						// do nothing
					}
				});
	}

	private void addClassic(String icon) {
		StringBuilder link = new StringBuilder("https://www.geogebra.org/graphing");
		addItem(getHTMLwithLink(icon,
				app.getLocalization().getMenu("Classic"),
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

