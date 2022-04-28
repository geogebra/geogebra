package org.geogebra.web.full.gui.menu.icons;

import org.geogebra.web.resources.SVGResource;
import org.gwtproject.resources.client.Resource;

/**
 * Gives access to Mebis menu icons.
 */
@Resource
public interface MebisMenuIconProvider extends DefaultMenuIconProvider {

	MebisMenuIconProvider INSTANCE = new MebisMenuIconProviderImpl();

	@Override
	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/mow/file_plus.svg")
	SVGResource clear();

	@Override
	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/mow/folder-open.svg")
	SVGResource search();
}