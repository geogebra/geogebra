package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.web.full.gui.images.SvgPerspectiveResources;
import org.geogebra.web.resources.SVGResource;

import com.google.gwt.resources.client.ResourcePrototype;

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
	public static AppDescription get(String code) {
		SvgPerspectiveResources res = SvgPerspectiveResources.INSTANCE;
		switch (code) {
		case GeoGebraConstants.G3D_APPCODE:
			return new AppDescription(res.menu_icon_graphics3D_transparent(),
					"GeoGebra3DGrapher.short");
		case GeoGebraConstants.GEOMETRY_APPCODE:
			return new AppDescription(res.menu_icon_geometry_transparent(),
					"Geometry");
		case GeoGebraConstants.CAS_APPCODE:
			return new AppDescription(res.cas_white_bg(),
					"CAS");
		case GeoGebraConstants.GRAPHING_APPCODE:
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
