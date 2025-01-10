package org.geogebra.web.full.gui.menu.icons;

import org.geogebra.common.gui.menu.Icon;
import org.geogebra.web.html5.gui.view.IconSpec;
import org.geogebra.web.html5.main.toolbox.FaIconSpec;

/**
 * Gives access to Mebis menu icons.
 */
public class MebisMenuIconProvider extends DefaultMenuIconProvider {

	@Override
	public IconSpec matchIconWithResource(Icon icon) {
		if (icon == null) {
			return null;
		}
		switch (icon) {
		case CLEAR:
			return new FaIconSpec("fa-file");
		case SEARCH:
			return new FaIconSpec("fa-folder-open");
		case FOLDER:
			return new FaIconSpec("fa-folder-arrow-up");
		case SAVE:
			return new FaIconSpec("fa-floppy-disk");
		case EXPORT_FILE:
			return new FaIconSpec("fa-share-nodes");
		case EXPORT_IMAGE:
			return new FaIconSpec("fa-images");
		case DOWNLOAD:
			return new FaIconSpec("fa-arrow-down-to-line");
		case PRINT:
			return new FaIconSpec("fa-print");
		case SETTINGS:
			return new FaIconSpec("fa-gear");
		case INFO:
			return new FaIconSpec("fa-circle-info");
		}
		return super.matchIconWithResource(icon);
	}
}