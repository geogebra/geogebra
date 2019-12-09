package org.geogebra.common.gui.menu.impl;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.gui.menu.Action;
import org.geogebra.common.gui.menu.ActionableItem;
import org.geogebra.common.gui.menu.DrawerMenu;
import org.geogebra.common.gui.menu.Icon;
import org.geogebra.common.gui.menu.MenuItem;
import org.geogebra.common.gui.menu.MenuItemGroup;
import org.geogebra.common.move.ggtapi.operations.LogInOperation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DefaultDrawerMenuFactory extends AbstractDrawerMenuFactory {

	private GeoGebraConstants.Platform platform;
	private LogInOperation logInOperation;

	public DefaultDrawerMenuFactory(GeoGebraConstants.Platform platform, GeoGebraConstants.Version version) {
		this(platform, version, null);
	}

	public DefaultDrawerMenuFactory(GeoGebraConstants.Platform platform, GeoGebraConstants.Version version, LogInOperation logInOperation) {
		super(version);
		this.platform = platform;
		this.logInOperation = logInOperation;
	}

	@Override
	public DrawerMenu createDrawerMenu() {
		MenuItemGroup main = createMainMenuItemGroup();
		MenuItemGroup secondary = createSecondaryMenuItemGroup();
		MenuItemGroup appsGroup = createAppsGroup();
		MenuItemGroup userGroup = createUserGroup();
		String title = getMenuTitle();
		return new DrawerMenuImpl(title, removeNulls(main, appsGroup, secondary, userGroup));
	}

	@SafeVarargs
	private final <T> List<T> removeNulls(T... groups) {
		ArrayList<T> list = new ArrayList<>();
		for (T group: groups) {
			if (group != null) {
				list.add(group);
			}
		}
		return list;
	}

	private MenuItemGroup createMainMenuItemGroup() {
		MenuItem clearConstruction = clearConstruction();
		MenuItem save = logInOperation == null ? null : saveFile();
		MenuItem downloadAs = platform == GeoGebraConstants.Platform.DESKTOP ? showDownloadAs() : null;
		MenuItem printPreview = platform == GeoGebraConstants.Platform.DESKTOP ? previewPrint() : null;
		MenuItem startExamMode = platform == GeoGebraConstants.Platform.DESKTOP ? null : startExamMode();
		if (version == GeoGebraConstants.Version.SCIENTIFIC) {
			return new MenuItemGroupImpl(removeNulls(clearConstruction, startExamMode));
		}
		return new MenuItemGroupImpl(removeNulls(clearConstruction, openFile(), save, share(),
				exportImage(), downloadAs, printPreview, startExamMode));
	}

	private MenuItemGroup createSecondaryMenuItemGroup() {
		if (isMobile()) {
			return new MenuItemGroupImpl(showAppPicker(), showSettings(), showHelpAndFeedback());
		}
		return new MenuItemGroupImpl(showSettings(), showHelpAndFeedback());
	}

	private MenuItemGroup createAppsGroup() {
		if (!isMobile()) {
			return new MenuItemGroupImpl("GeoGebraApps", startAppItems());
		}
		return null;
	}

	private MenuItemGroup createUserGroup() {
		if (logInOperation != null) {
			return new UserMenuItemGroup(logInOperation);
		}
		return null;
	}

	private boolean isMobile() {
		return platform == GeoGebraConstants.Platform.ANDROID || platform == GeoGebraConstants.Platform.IOS;
	}

	private static MenuItem startExamMode() {
		return new ActionableItemImpl(Icon.HOURGLASS_EMPTY, "exam_menu_entry", Action.START_EXAM_MODE);
	}

	private MenuItem showAppPicker() {
		return new SubmenuItemImpl(Icon.GEOGEBRA, "GeoGebraApps", startAppItems());
	}

	private ActionableItem[] startAppItems() {
		ArrayList<ActionableItem> items = new ArrayList<>(Arrays.<ActionableItem>asList(
				new ActionableItemImpl(Icon.APP_GRAPHING, "GraphingCalculator", Action.START_GRAPHING),
				new ActionableItemImpl(Icon.APP_GEOMETRY, "Geometry", Action.START_GEOMETRY),
				new ActionableItemImpl(Icon.APP_GRAPHING3D, "GeoGebra3DGrapher.short", Action.START_GRAPHING_3D),
				new ActionableItemImpl(Icon.APP_SCIENTIFIC, "ScientificCalculator", Action.START_SCIENTIFIC),
				new ActionableItemImpl(Icon.APP_CAS_CALCULATOR, "CASCalculator", Action.START_CAS_CALCULATOR),
				new ActionableItemImpl(Icon.APP_CLASSIC, "Classic", Action.START_CLASSIC)));
		switch (platform) {
			case WEB:
				items.remove(5); // cas
				break;
			case ANDROID:
			case IOS:
				items.remove(6); // classic
		}
		return items.toArray(new ActionableItem[0]);
	}


	private static MenuItem openFile() {
		return new ActionableItemImpl(Icon.SEARCH, "Load", Action.SHOW_SEARCH_VIEW);
	}

	private static MenuItem share() {
		return new ActionableItemImpl(Icon.EXPORT_FILE, "Share", Action.SHARE_FILE);
	}

	private static MenuItem exportImage() {
		return new ActionableItemImpl(Icon.EXPORT_IMAGE, "exportImage", Action.EXPORT_IMAGE);
	}

	private static MenuItem showSettings() {
		return new ActionableItemImpl(Icon.SETTINGS, "Settings", Action.SHOW_SETTINGS);
	}

	private static MenuItem saveFile() {
		return new ActionableItemImpl(Icon.SAVE, "Save", Action.SAVE_FILE);
	}

	private static MenuItem previewPrint() {
		return new ActionableItemImpl(Icon.PRINT, "PrintPreview", Action.PREVIEW_PRINT);
	}

	private static MenuItem showHelpAndFeedback() {
		ActionableItem tutorials = new ActionableItemImpl(Icon.SCHOOL, "Tutorial", Action.SHOW_TUTORIALS);
		ActionableItem askQuestion = new ActionableItemImpl(Icon.QUESTION_ANSWER, "AskAQuestion", Action.SHOW_FORUM);
		ActionableItem reportProblem = new ActionableItemImpl(Icon.BUG_REPORT, "ReportBug", Action.REPORT_PROBLEM);
		ActionableItem license = new ActionableItemImpl(Icon.INFO, "AboutLicense", Action.SHOW_LICENSE);
		return new SubmenuItemImpl(Icon.HELP, "HelpAndFeedback",
				tutorials, askQuestion, reportProblem, license);
	}

	private MenuItem showDownloadAs() {
		ActionableItem png = new ActionableItemImpl(Icon.NONE, "PNGImage", Action.DOWNLOAD_PNG);
		ActionableItem svg = new ActionableItemImpl(Icon.NONE, "SVGImage", Action.DOWNLOAD_SVG);
		ActionableItem pdf = new ActionableItemImpl(Icon.NONE, "PDFDocument", Action.DOWNLOAD_PDF);
		switch (version) {
			case NOTES:
				return new SubmenuItemImpl(Icon.SAVE, "DownloadAs", createDownloadSlides(), png, svg, pdf);
			case GRAPHING_3D:
				ActionableItem dae = new ActionableItemImpl(Icon.NONE, "ColladaDae", Action.DOWNLOAD_COLLADA_DAE);
				ActionableItem html = new ActionableItemImpl(Icon.NONE, "ColladaHtml", Action.DOWNLOAD_COLLADA_HTML);
				return new SubmenuItemImpl(Icon.SAVE, "DownloadAs", createDownloadGgb(), png, svg, pdf, createStl(), dae, html);
			default:
				return new SubmenuItemImpl(Icon.SAVE, "DownloadAs", createDownloadGgb(), png, svg, pdf, createStl());
		}
	}

	private static ActionableItem createDownloadGgb() {
		return new ActionableItemImpl(Icon.NONE, "GeoGebraFile", Action.DOWNLOAD_GGB);
	}

	private static ActionableItem createDownloadSlides() {
		return new ActionableItemImpl(Icon.NONE, "SlidesGgs", Action.DOWNLOAD_GGS);
	}

	private static ActionableItem createStl() {
		return new ActionableItemImpl(Icon.NONE, "3DPrint", Action.DOWNLOAD_STL);
	}
}
