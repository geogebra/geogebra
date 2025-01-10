package org.geogebra.web.html5.main.topbar;

import org.geogebra.web.resources.SVGResource;
import org.gwtproject.resources.client.ClientBundle;
import org.gwtproject.resources.client.Resource;

@Resource
public interface DefaultTopBarIconResources extends ClientBundle {

	DefaultTopBarIconResources INSTANCE = new DefaultTopBarIconResourcesImpl();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/ev/baseline-menu-24px.svg")
	SVGResource menu();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/ev/undo.svg")
	SVGResource undo_border();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/ev/redo.svg")
	SVGResource redo_border();

	@Source("org/geogebra/common/icons/svg/common/zoom_in.svg")
	SVGResource zoom_in();

	@Source("org/geogebra/common/icons/svg/common/zoom_out.svg")
	SVGResource zoom_out();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/contextMenu/ic_home_black_24px.svg")
	SVGResource home();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/ev/drag_pad.svg")
	SVGResource pan_view();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/ev/ic_fullscreen_black_18px.svg")
	SVGResource fullscreen();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/ev/ic_fullscreen_exit_black_18px.svg")
	SVGResource fullscreen_exit();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/ev/ic_settings_black_24px.svg")
	SVGResource settings();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/ev/grid_black.svg")
	SVGResource ruling();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/dynStylebar/color_black.svg")
	SVGResource color();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/mow/ic_view_module_24px.svg")
	SVGResource page_overview();
}
