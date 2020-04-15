package org.geogebra.common.gui.menu.impl;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.gui.menu.Action;
import org.geogebra.common.gui.menu.ActionableItem;
import org.geogebra.common.gui.menu.DrawerMenu;
import org.geogebra.common.gui.menu.Icon;
import org.geogebra.common.gui.menu.MenuItem;
import org.geogebra.common.gui.menu.MenuItemGroup;
import org.geogebra.common.move.ggtapi.models.GeoGebraTubeUser;
import org.geogebra.common.move.ggtapi.operations.LogInOperation;

import java.util.ArrayList;
import java.util.List;

/**
 * Menu factory creating the default menus for all app versions and platforms.
 */
public class DefaultDrawerMenuFactory extends AbstractDrawerMenuFactory {

	private GeoGebraConstants.Platform platform;
	private LogInOperation logInOperation;
	private boolean createExamEntry;

	/**
	 * Create a new DrawerMenuFactory.
	 *
	 * @param platform platform
	 * @param version version
	 */
	public DefaultDrawerMenuFactory(GeoGebraConstants.Platform platform,
									GeoGebraConstants.Version version) {
		this(platform, version, null);
	}

	/**
	 * Create a new DrawerMenuFactory.
	 *
	 * @param platform platform
	 * @param version version
	 * @param logInOperation if loginOperation is not null, it creates menu options that require
	 *                       login based on the {@link LogInOperation#isLoggedIn()} method.
	 */
	public DefaultDrawerMenuFactory(GeoGebraConstants.Platform platform,
									GeoGebraConstants.Version version,
									LogInOperation logInOperation) {
		this(platform, version, logInOperation, false);
	}

	/**
	 * Create a new DrawerMenuFactory.
	 *
	 * @param platform platform
	 * @param version version
	 * @param logInOperation if loginOperation is not null, it creates menu options that require
	 *                       login based on the {@link LogInOperation#isLoggedIn()} method.
	 * @param createExamEntry whether the factory should create the start exam button
	 */
	public DefaultDrawerMenuFactory(GeoGebraConstants.Platform platform,
									GeoGebraConstants.Version version,
									LogInOperation logInOperation,
									boolean createExamEntry) {
		super(version);
		this.platform = platform;
		this.logInOperation = logInOperation;
		this.createExamEntry = createExamEntry;
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
	protected final <T> List<T> removeNulls(T... groups) {
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
		MenuItem downloadAs = isDesktop() ? showDownloadAs() : null;
		MenuItem printPreview = isDesktop() ? previewPrint() : null;
		MenuItem startExamMode = createExamEntry ? startExamMode() : null;
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
			return createUserGroup(logInOperation);
		}
		return null;
	}

	private static MenuItemGroup createUserGroup(LogInOperation logInOperation) {
		if (logInOperation.isLoggedIn()) {
			GeoGebraTubeUser user = logInOperation.getModel().getLoggedInUser();
			MenuItem userItem = new ActionableItemImpl(Icon.USER_ICON,
					user.getUserName(), Action.OPEN_PROFILE_PAGE);
			MenuItem signOut = new ActionableItemImpl(Icon.SIGN_OUT,
					"SignOut", Action.SIGN_OUT);
			return new MenuItemGroupImpl(userItem, signOut);
		} else {
			MenuItem signIn = new ActionableItemImpl(Icon.SIGN_IN, "SignIn", Action.SIGN_IN);
			return new MenuItemGroupImpl(signIn);
		}
	}

	private boolean isMobile() {
		return platform == GeoGebraConstants.Platform.ANDROID
				|| platform == GeoGebraConstants.Platform.IOS;
	}

	private boolean isDesktop() {
		return !isMobile();
	}

	private static MenuItem startExamMode() {
		return new ActionableItemImpl(Icon.HOURGLASS_EMPTY,
				"exam_menu_entry", Action.START_EXAM_MODE);
	}

	private MenuItem showAppPicker() {
		return new SubmenuItemImpl(Icon.GEOGEBRA, "GeoGebraApps", startAppItems());
	}

	private ActionableItem[] startAppItems() {
		ActionableItem graphing = new ActionableItemImpl(Icon.APP_GRAPHING,
				"GraphingCalculator", Action.START_GRAPHING);
		ActionableItem geometry = new ActionableItemImpl(Icon.APP_GEOMETRY,
				"Geometry", Action.START_GEOMETRY);
		ActionableItem graphing3d = new ActionableItemImpl(Icon.APP_GRAPHING3D,
				"GeoGebra3DGrapher.short", Action.START_GRAPHING_3D);
		ActionableItem cas = platform == GeoGebraConstants.Platform.WEB ? null
				: new ActionableItemImpl(Icon.APP_CAS_CALCULATOR,
						"CASCalculator", Action.START_CAS_CALCULATOR);
		ActionableItem scientific = new ActionableItemImpl(Icon.APP_SCIENTIFIC,
				"ScientificCalculator", Action.START_SCIENTIFIC);
		ActionableItem classic = isMobile() ? null : new ActionableItemImpl(Icon.APP_CLASSIC,
				"Classic", Action.START_CLASSIC);
		List<ActionableItem> retVal = removeNulls(graphing, geometry,
				graphing3d, cas, scientific, classic);
		return retVal.toArray(new ActionableItem[0]);
	}

	private static MenuItem openFile() {
		return new ActionableItemImpl(Icon.SEARCH, "Load", Action.SHOW_SEARCH_VIEW);
	}

	protected static MenuItem share() {
		return new ActionableItemImpl(Icon.EXPORT_FILE, "Share", Action.SHARE_FILE);
	}

	private static MenuItem exportImage() {
		return new ActionableItemImpl(Icon.EXPORT_IMAGE, "exportImage", Action.EXPORT_IMAGE);
	}

	protected static MenuItem showSettings() {
		return new ActionableItemImpl(Icon.SETTINGS, "Settings", Action.SHOW_SETTINGS);
	}

	protected static MenuItem saveFile() {
		return new ActionableItemImpl(Icon.SAVE, "Save", Action.SAVE_FILE);
	}

	protected static MenuItem previewPrint() {
		return new ActionableItemImpl(Icon.PRINT, "PrintPreview", Action.PREVIEW_PRINT);
	}

	private static MenuItem showHelpAndFeedback() {
		ActionableItem tutorials = new ActionableItemImpl(Icon.SCHOOL,
				"Tutorial", Action.SHOW_TUTORIALS);
		ActionableItem askQuestion = new ActionableItemImpl(Icon.QUESTION_ANSWER,
				"AskAQuestion", Action.SHOW_FORUM);
		ActionableItem reportProblem = new ActionableItemImpl(Icon.BUG_REPORT,
				"ReportProblem", Action.REPORT_PROBLEM);
		ActionableItem license = new ActionableItemImpl(Icon.INFO,
				"AboutLicense", Action.SHOW_LICENSE);
		return new SubmenuItemImpl(Icon.HELP, "HelpAndFeedback",
				tutorials, askQuestion, reportProblem, license);
	}

	protected MenuItem showDownloadAs() {
		ActionableItem png = new ActionableItemImpl(null, "Download.PNGImage", Action.DOWNLOAD_PNG);
		ActionableItem svg = new ActionableItemImpl(null, "Download.SVGImage", Action.DOWNLOAD_SVG);
		ActionableItem pdf = new ActionableItemImpl(null,
				"Download.PDFDocument", Action.DOWNLOAD_PDF);
		switch (version) {
			case NOTES:
				return new SubmenuItemImpl(Icon.DOWNLOAD, "DownloadAs",
						createDownloadSlides(), png, svg, pdf);
			case GRAPHING_3D:
				ActionableItem dae = new ActionableItemImpl(
						"Download.ColladaDae", Action.DOWNLOAD_COLLADA_DAE);
				ActionableItem html = new ActionableItemImpl(
						"Download.ColladaHtml", Action.DOWNLOAD_COLLADA_HTML);
				return new SubmenuItemImpl(Icon.DOWNLOAD, "DownloadAs", createDownloadGgb(),
						png, createDownloadStl(), dae, html);
			default:
				return new SubmenuItemImpl(Icon.DOWNLOAD, "DownloadAs", createDownloadGgb(),
						png, svg, pdf, createDownloadStl());
		}
	}

	private static ActionableItem createDownloadGgb() {
		return new ActionableItemImpl("Download.GeoGebraFile", Action.DOWNLOAD_GGB);
	}

	private static ActionableItem createDownloadSlides() {
		return new ActionableItemImpl("Download.SlidesGgs", Action.DOWNLOAD_GGS);
	}

	private static ActionableItem createDownloadStl() {
		return new ActionableItemImpl("Download.3DPrint", Action.DOWNLOAD_STL);
	}

	public LogInOperation getLogInOperation() {
		return logInOperation;
	}
}
