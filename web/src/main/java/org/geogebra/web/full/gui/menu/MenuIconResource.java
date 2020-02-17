package org.geogebra.web.full.gui.menu;

import org.geogebra.common.gui.menu.Icon;
import org.geogebra.web.full.gui.menu.icons.MenuIconProvider;
import org.geogebra.web.resources.SVGResource;

public class MenuIconResource {

	private MenuIconProvider menuIconProvider;

	public MenuIconResource(MenuIconProvider menuIconProvider) {
		this.menuIconProvider = menuIconProvider;
	}

	public SVGResource getImageResource(Icon icon) {
		switch (icon) {
			case APP_CLASSIC:
				return menuIconProvider.appClassic();
			case APP_GRAPHING:
				return menuIconProvider.appGraphing();
			case APP_SCIENTIFIC:
				return menuIconProvider.appScientific();
			case APP_GEOMETRY:
				return menuIconProvider.appGeometry();
			case APP_CAS_CALCULATOR:
				return menuIconProvider.appCasCalculator();
			case APP_GRAPHING3D:
				return menuIconProvider.appGraphing3d();
			case CLEAR:
				return menuIconProvider.clear();
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
			case USER_ICON:
			default:
				return null;

		}
	}
}
