package org.geogebra.web.full.gui.menu;

import com.google.gwt.resources.client.ImageResource;
import org.geogebra.common.gui.menu.Icon;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.images.SvgPerspectiveResources;
import org.geogebra.web.resources.SVGResource;

public class MenuIconResource {

	public SVGResource getImageResource(Icon icon) {
		switch (icon) {
			case CLEAR:
				return MaterialDesignResources.INSTANCE.clear();
			case SAVE:
				return MaterialDesignResources.INSTANCE.save_black();
			case SEARCH:
				return MaterialDesignResources.INSTANCE.openFileMenu();
			case SCHOOL:
				return MaterialDesignResources.INSTANCE.tutorial_black();
			case BUG_REPORT:
				return MaterialDesignResources.INSTANCE.bug_report_black();
			case APP_CAS_CALCULATOR:
				return SvgPerspectiveResources.INSTANCE.menu_icon_cas();
			case APP_CLASSIC:
				return MaterialDesignResources.INSTANCE.geogebra_color();
			case APP_GEOMETRY:
				return SvgPerspectiveResources.INSTANCE.menu_icon_geometry_transparent();
			case APP_GRAPHING:
				return SvgPerspectiveResources.INSTANCE.menu_icon_algebra_transparent();
			case APP_GRAPHING3D:
				return SvgPerspectiveResources.INSTANCE.menu_icon_graphics3D_transparent();
			case APP_SCIENTIFIC:
				return MaterialDesignResources.INSTANCE.scientific();
			case SETTINGS:
				return MaterialDesignResources.INSTANCE.gear();
			case EXPORT_IMAGE:
				return MaterialDesignResources.INSTANCE.export_image_black();
			case EXPORT_FILE:
				return MaterialDesignResources.INSTANCE.file_download_black();
			case INFO:

			case HELP:
//				MaterialDesignResources.INSTANCE.h
			default:
				return MaterialDesignResources.INSTANCE.share_black();
		}
	}
}
