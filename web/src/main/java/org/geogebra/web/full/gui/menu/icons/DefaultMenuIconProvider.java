package org.geogebra.web.full.gui.menu.icons;

import org.geogebra.web.resources.SVGResource;
import org.gwtproject.resources.client.ClientBundle;
import org.gwtproject.resources.client.Resource;

/**
 * Gives default access to menu icons.
 */
@Resource
public interface DefaultMenuIconProvider extends ClientBundle, MenuIconProvider {

	DefaultMenuIconProvider INSTANCE = new DefaultMenuIconProviderImpl();

	@Override
	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/av/baseline-clear-24px.svg")
	SVGResource clear();

	@Override
	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/ic_file_download_black_24px.svg")
	SVGResource download();

	@Override
	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/ic_save_black_24px.svg")
	SVGResource save();

	@Override
	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/hourglass_empty-24px.svg")
	SVGResource hourglassEmpty();

	@Override
	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/geogebra.svg")
	SVGResource geogebra();

	@Override
	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/ev/ic_settings_black_24px.svg")
	SVGResource settings();

	@Override
	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/help-24px.svg")
	SVGResource help();

	@Override
	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/ic_print_black_24px.svg")
	SVGResource print();

	@Override
	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/ic_search_black_24px.svg")
	SVGResource search();

	@Override
	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/ic_share_black_24px.svg")
	SVGResource exportFile();

	@Override
	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/ic_collections_black_24px.svg")
	SVGResource exportImage();

	@Override
	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/assignment-24px.svg")
	SVGResource assignment();

	@Override
	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/school-24px.svg")
	SVGResource school();

	@Override
	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/ic_forum_black_24px.svg")
	SVGResource questionAnswer();

	@Override
	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/ic_bug_report_black_24px.svg")
	SVGResource bugReport();

	@Override
	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/info-24px.svg")
	SVGResource info();

	@Override
	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/exit_to_app-24px.svg")
	SVGResource signIn();

	@Override
	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/logout.svg")
	SVGResource signOut();

	@Override
	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/mow/baseline-folder_open-24px.svg")
	SVGResource folder();
}
