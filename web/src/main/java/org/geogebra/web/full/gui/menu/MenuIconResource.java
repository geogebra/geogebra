package org.geogebra.web.full.gui.menu;

import org.geogebra.common.gui.menu.Icon;
import org.geogebra.web.full.gui.menu.icons.MenuIconProvider;
import org.geogebra.web.resources.SVGResource;

class MenuIconResource {

	private static final String FILL_COLOR = "rgba(0, 0, 0, 0.54)";

	private MenuIconProvider menuIconProvider;

	MenuIconResource(MenuIconProvider menuIconProvider) {
		this.menuIconProvider = menuIconProvider;
	}

	SVGResource getImageResource(Icon icon) {
		SVGResource resource = matchIconWithResource(icon);
		return applyTint(resource);
	}

	private SVGResource matchIconWithResource(Icon icon) {
		switch (icon) {
			case CLEAR:
				return menuIconProvider.clear();
			case DOWNLOAD:
				return menuIconProvider.download();
			case SAVE:
				return menuIconProvider.save();
			case HOURGLASS_EMPTY:
				return menuIconProvider.hourglassEmpty();
			case GEOGEBRA:
				return menuIconProvider.geogebra();
			case SETTINGS:
				return menuIconProvider.settings();
			case HELP:
				return menuIconProvider.help();
			case PRINT:
				return menuIconProvider.print();
			case SEARCH:
				return menuIconProvider.search();
			case EXPORT_FILE:
				return menuIconProvider.exportFile();
			case EXPORT_IMAGE:
				return menuIconProvider.exportImage();
			case ASSIGNMENT:
				return menuIconProvider.assignment();
			case SCHOOL:
				return menuIconProvider.school();
			case QUESTION_ANSWER:
				return menuIconProvider.questionAnswer();
			case BUG_REPORT:
				return menuIconProvider.bugReport();
			case INFO:
				return menuIconProvider.info();
			case SIGN_IN:
				return menuIconProvider.signIn();
			case SIGN_OUT:
				return menuIconProvider.signOut();
			case FOLDER:
				return menuIconProvider.folder();
			case USER_ICON:
			default:
				return null;
		}
	}

	private SVGResource applyTint(SVGResource resource) {
		return resource == null ? null : resource.withFill(FILL_COLOR);
	}
}
