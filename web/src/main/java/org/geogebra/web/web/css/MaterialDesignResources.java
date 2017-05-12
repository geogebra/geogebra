package org.geogebra.web.web.css;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

@SuppressWarnings("javadoc")
public interface MaterialDesignResources extends ClientBundle {

	MaterialDesignResources INSTANCE = GWT.create(MaterialDesignResources.class);
	/** NEW MATERIAL DESIGN ICONS */

	// context menu icon resources
	@Source("org/geogebra/common/icons/png/matDesignIcons/contextMenu/more_vert_black.png")
	ImageResource more_vert_black();

	@Source("org/geogebra/common/icons/png/matDesignIcons/contextMenu/more_vert_purple.png")
	ImageResource more_vert_purple();

	@Source("org/geogebra/common/icons/png/matDesignIcons/contextMenu/duplicate_black.png")
	ImageResource duplicate_black();

	@Source("org/geogebra/common/icons/png/matDesignIcons/contextMenu/delete_black.png")
	ImageResource delete_black();

	@Source("org/geogebra/common/icons/png/matDesignIcons/contextMenu/settings_black.png")
	ImageResource settings_black();

	@Source("org/geogebra/common/icons/png/matDesignIcons/contextMenu/zoom_in_black.png")
	ImageResource zoom_in_black();

	@Source("org/geogebra/common/icons/png/matDesignIcons/contextMenu/home_black.png")
	ImageResource home_black();

	@Source("org/geogebra/common/icons/png/matDesignIcons/contextMenu/show_all_objects_black.png")
	ImageResource show_all_objects_black();

	@Source("org/geogebra/common/icons/png/matDesignIcons/contextMenu/arrow_drop_right_black.png")
	ImageResource arrow_drop_right_black();

	@Source("org/geogebra/common/icons/png/matDesignIcons/contextMenu/arrow_drop_left_black.png")
	ImageResource arrow_drop_left_black();

	// @Source("org/geogebra/common/icons/png/matDesignIcons/contextMenu/content_copy_black.png")
	// ImageResource copy_content_black();


	// plus menu icon resources
	@Source("org/geogebra/common/icons/png/matDesignIcons/av/plusMenu/menu_help_black.png")
	ImageResource icon_help_black();

	@Source("org/geogebra/common/icons/png/matDesignIcons/av/plusMenu/format_quote_black.png")
	ImageResource icon_quote_black();

	@Source("org/geogebra/common/icons/png/matDesignIcons/av/plusMenu/insert_photo_black.png")
	ImageResource insert_photo_black();

	// @Source("org/geogebra/common/icons/svg/web/app_store.svg")
	// SVGResource app_store();

	// Toolbar resoures
	@Source("org/geogebra/common/icons/png/matDesignIcons/toolBar/open_white.png")
	ImageResource toolbar_open_white();

	@Source("org/geogebra/common/icons/png/matDesignIcons/toolBar/close_white.png")
	ImageResource toolbar_close_white();

	@Source("org/geogebra/common/icons/png/matDesignIcons/toolBar/menu_white.png")
	ImageResource toolbar_menu_white();

}
