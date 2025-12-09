/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.full.gui.menu.icons;

import org.geogebra.web.resources.SVGResource;
import org.gwtproject.resources.client.ClientBundle;
import org.gwtproject.resources.client.Resource;

/**
 * Default menu icon bundle.
 */
@Resource
public interface DefaultMenuIconResources extends ClientBundle {

	DefaultMenuIconResources INSTANCE = new DefaultMenuIconResourcesImpl();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/av/baseline-clear-24px.svg")
	SVGResource clear();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/ic_file_download_black_24px.svg")
	SVGResource download();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/ic_save_black_24px.svg")
	SVGResource save();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/ic_save_online_black_24px.svg")
	SVGResource saveOnline();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/hourglass_empty-24px.svg")
	SVGResource hourglassEmpty();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/geogebra.svg")
	SVGResource geogebra();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/ev/ic_settings_black_24px.svg")
	SVGResource settings();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/help-24px.svg")
	SVGResource help();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/ic_print_black_24px.svg")
	SVGResource print();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/ic_search_black_24px.svg")
	SVGResource search();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/ic_share_black_24px.svg")
	SVGResource exportFile();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/ic_collections_black_24px.svg")
	SVGResource exportImage();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/assignment-24px.svg")
	SVGResource assignment();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/school-24px.svg")
	SVGResource school();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/ic_bug_report_black_24px.svg")
	SVGResource bugReport();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/info-24px.svg")
	SVGResource info();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/privacy_tip.svg")
	SVGResource privacyPolicy();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/exit_to_app-24px.svg")
	SVGResource signIn();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/burgerMenu/logout.svg")
	SVGResource signOut();

	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/mow/baseline-folder_open-24px.svg")
	SVGResource folder();
}
