package org.geogebra.web.full.gui.menubar;

import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;

/**
 * Help menu
 */
public class DownloadMenuW extends Submenu implements MenuBarI {
	/**
	 * @param app
	 *            application
	 */
	public DownloadMenuW(final AppW app) {
		super("DownloadAs", app);
		addExpandableStyleWithColor(false);
		ExportMenuW.initActions(this, app);
	}

	@Override
	public void hide() {
		// no hiding needed
	}

	@Override
	public SVGResource getImage() {
        return ((AppWFull) getApp()).getActivity().getResourceIconProvider().downloadMenu();
	}

	@Override
	protected String getTitleTranslationKey() {
		return "DownloadAs";
	}
}

