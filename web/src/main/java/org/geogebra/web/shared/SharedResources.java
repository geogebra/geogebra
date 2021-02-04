package org.geogebra.web.shared;

import org.geogebra.web.resources.SVGResource;
import org.geogebra.web.resources.SassResource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface SharedResources extends ClientBundle {

    SharedResources INSTANCE = GWT.create(SharedResources.class);

    @Source("org/geogebra/common/icons/png/web/algebra-view-tree-open.png")
    ImageResource algebra_tree_open();

    @Source("org/geogebra/common/icons/png/web/algebra-view-tree-closed.png")
    ImageResource algebra_tree_closed();

    @Source("org/geogebra/web/resources/scss/solver.scss")
    SassResource solverStyleScss();

    @Source("org/geogebra/web/resources/scss/step-tree.scss")
    SassResource stepTreeStyleScss();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/av/plusMenu/ic_help_outline_black_24px.svg")
	SVGResource icon_help_black();

	@Source("org/geogebra/common/icons/png/web/button_cancel.png")
	ImageResource dialog_cancel();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/ic_file_download_white_24px.svg")
	SVGResource file_download_white();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/ic_print_white_24px.svg")
	SVGResource print_white();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/code_white_24px.svg")
	SVGResource code_white();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/ic_info_outline_black_24px.svg")
	SVGResource info_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/mow/baseline-link-white-24px.svg")
	SVGResource mow_link_white();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/mow/baseline-link-black-24px.svg")
	SVGResource mow_link_black();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/mow/baseline-group-24px.svg")
	SVGResource groups();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/mow/check_mark_white.svg")
	SVGResource check_mark_white();

	@Source("org/geogebra/web/resources/scss/dialog-styles.scss")
	SassResource dialogStylesScss();
}
