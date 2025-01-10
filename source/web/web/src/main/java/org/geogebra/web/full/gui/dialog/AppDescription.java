package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.images.SvgPerspectiveResources;
import org.geogebra.web.resources.SVGResource;
import org.gwtproject.resources.client.ResourcePrototype;

public final class AppDescription {

	private final SVGResource icon;
	private final String title;

	private AppDescription(SVGResource icon, String title) {
		this.icon = icon;
		this.title = title;
	}

	/**
	 * @param code (sub)app code
	 * @return image and translation key for the app
	 */
	public static AppDescription get(SuiteSubApp code) {
		SvgPerspectiveResources res = SvgPerspectiveResources.INSTANCE;
		switch (code) {
		case G3D:
			return new AppDescription(res.menu_icon_graphics3D_transparent(),
					"GeoGebra3DGrapher.short");
		case GEOMETRY:
			return new AppDescription(res.menu_icon_geometry_transparent(),
					"Geometry");
		case CAS:
			return new AppDescription(res.cas_white_bg(),
					"CAS");
		case PROBABILITY:
			return new AppDescription(res.menu_icon_probability_transparent(),
					"Probability");
		case SCIENTIFIC:
			return new AppDescription(MaterialDesignResources.INSTANCE.scientific(),
					"Scientific");
		case GRAPHING:
		default:
			return new AppDescription(res.menu_icon_algebra_transparent(),
					"GraphingCalculator.short");
		}
	}

	/**
	 * @return SVG icon
	 */
	public ResourcePrototype getIcon() {
		return icon;
	}

	/**
	 * @return app name translation key
	 */
	public String getNameKey() {
		return title;
	}
}
