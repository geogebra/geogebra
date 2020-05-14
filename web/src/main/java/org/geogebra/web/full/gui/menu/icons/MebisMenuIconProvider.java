package org.geogebra.web.full.gui.menu.icons;

import org.geogebra.web.resources.SVGResource;

import com.google.gwt.core.client.GWT;

/**
 * Gives access to Mebis menu icons.
 */
public interface MebisMenuIconProvider extends DefaultMenuIconProvider {

	MebisMenuIconProvider INSTANCE = GWT.create(MebisMenuIconProvider.class);

	@Override
	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/mow/file_plus.svg")
	SVGResource clear();

	@Override
	@Source("org/geogebra/common/icons/svg/web/matDesignIcons/mow/folder-open.svg")
	SVGResource search();
}