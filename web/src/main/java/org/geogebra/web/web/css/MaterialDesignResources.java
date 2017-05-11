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

	// @Source("org/geogebra/common/icons/png/matDesignIcons/contextMenu/content_copy_black.png")
	// ImageResource copy_content_black();


	// plus menu icon resources
	@Source("org/geogebra/common/icons/png/matDesignIcons/av/plusMenu/menu_help_black.png")
	ImageResource icon_help_black();

	@Source("org/geogebra/common/icons/png/matDesignIcons/av/plusMenu/format_quote_black.png")
	ImageResource icon_quote_black();

	@Source("org/geogebra/common/icons/png/matDesignIcons/av/plusMenu/insert_photo_black.png")
	ImageResource insert_photo_black();

}
