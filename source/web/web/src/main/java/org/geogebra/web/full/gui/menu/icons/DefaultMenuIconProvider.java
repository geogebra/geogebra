package org.geogebra.web.full.gui.menu.icons;

import org.geogebra.common.gui.menu.Icon;
import org.geogebra.web.html5.gui.view.IconSpec;
import org.geogebra.web.html5.gui.view.ImageIconSpec;
import org.geogebra.web.resources.SVGResource;

/**
 * Gives default access to menu icons.
 */
public class DefaultMenuIconProvider implements MenuIconProvider {

	private static final DefaultMenuIconResources res = DefaultMenuIconResources.INSTANCE;

	@Override
	public IconSpec matchIconWithResource(Icon icon) {
		return icon != null ? new ImageIconSpec(findImage(icon)) : null;
	}

	private SVGResource findImage(Icon icon) {
		switch (icon) {
		case CLEAR:
			return res.clear();
		case DOWNLOAD:
			return res.download();
		case SAVE:
			return res.save();
		case SAVE_ONLINE:
			return res.saveOnline();
		case HOURGLASS_EMPTY:
			return res.hourglassEmpty();
		case GEOGEBRA:
			return res.geogebra();
		case SETTINGS:
			return res.settings();
		case HELP:
			return res.help();
		case PRINT:
			return res.print();
		case SEARCH:
			return res.search();
		case EXPORT_FILE:
			return res.exportFile();
		case EXPORT_IMAGE:
			return res.exportImage();
		case ASSIGNMENT:
			return res.assignment();
		case SCHOOL:
			return res.school();
		case BUG_REPORT:
			return res.bugReport();
		case INFO:
			return res.info();
		case SIGN_IN:
			return res.signIn();
		case SIGN_OUT:
			return res.signOut();
		case FOLDER:
			return res.folder();
		default:
			return null;
		}
	}
}
