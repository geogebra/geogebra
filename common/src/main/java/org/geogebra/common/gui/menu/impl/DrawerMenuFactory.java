package org.geogebra.common.gui.menu.impl;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.gui.menu.Action;
import org.geogebra.common.gui.menu.ActionableItem;
import org.geogebra.common.gui.menu.DrawerMenu;
import org.geogebra.common.gui.menu.Icon;
import org.geogebra.common.gui.menu.MenuItem;
import org.geogebra.common.gui.menu.MenuItemGroup;

import java.util.ArrayList;
import java.util.Arrays;

public class DrawerMenuFactory {

	private GeoGebraConstants.Platform platform;

	public DrawerMenuFactory(GeoGebraConstants.Platform platform) {
		this.platform = platform;
	}

	public DrawerMenu createScientificCalculatorMenu() {
		MenuItemGroup main = new MenuItemGroupImpl(clearConstruction());
		MenuItemGroup secondary = createSecondaryGroup();
		switch (platform) {
			case ANDROID:
			case IOS:
				return new DrawerMenuImpl("GeoGebraScientificCalculator", main, secondary);
			default:
				MenuItemGroup apps = new MenuItemGroupImpl("GeoGebraApps", startAppItems());
				return new DrawerMenuImpl("GeoGebraScientificCalculator", main, apps, secondary);
		}
	}

	public DrawerMenu createGraphingCalculatorMenu() {
		MenuItemGroup main = new MenuItemGroupImpl(clearConstruction(), openFile(), share(), exportImage(), startExamMode());
		return new DrawerMenuImpl("GeoGebraGraphingCalculator", main, createSecondaryGroup(), createSignInGroup());
	}

	public DrawerMenu createGraphingCalculatorExamMenu() {
		return createExamMenu("GeoGebraGraphingCalculator");
	}

	private static DrawerMenu createExamMenu(String title) {
		MenuItemGroup group = new MenuItemGroupImpl(clearConstruction(), showExamLog(), exitExamMode());
		return new DrawerMenuImpl(title, group);
	}

	private MenuItemGroup createSecondaryGroup() {
		switch (platform) {
			case ANDROID:
			case IOS:
				return new MenuItemGroupImpl(showAppPicker(), showSettings(), showHelpAndFeedback());
			default:
				return new MenuItemGroupImpl(showSettings(), showHelpAndFeedback());
		}
	}

	private static MenuItemGroup createSignInGroup() {
		return new MenuItemGroupImpl(signIn());
	}

	private static MenuItem clearConstruction() {
		return new ActionableItemImpl(Icon.CLEAR, "Clear", Action.CLEAR_CONSTRUCTION);
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

	private static MenuItem showHelpAndFeedback() {
		ActionableItem tutorials = new ActionableItemImpl(Icon.SCHOOL, "Tutorial", Action.SHOW_TUTORIALS);
		ActionableItem askQuestion = new ActionableItemImpl(Icon.QUESTION_ANSWER, "AskAQuestion", Action.SHOW_FORUM);
		ActionableItem reportProblem = new ActionableItemImpl(Icon.BUG_REPORT, "ReportBug", Action.REPORT_PROBLEM);
		ActionableItem license = new ActionableItemImpl(Icon.INFO, "AboutLicense", Action.SHOW_LICENSE);
		return new SubmenuItemImpl(Icon.HELP, "HelpAndFeedback",
				tutorials, askQuestion, reportProblem, license);
	}

	private static MenuItem showExamLog() {
		return new ActionableItemImpl(Icon.ASSIGNMENT, "exam_log_header", Action.SHOW_EXAM_LOG);
	}

	private static MenuItem exitExamMode() {
		return new ActionableItemImpl(Icon.HOURGLASS_EMPTY, "exam_menu_exit", Action.EXIT_EXAM_MODE);
	}

	private static MenuItem signIn() {
		return new ActionableItemImpl(Icon.EXIT_TO_APP, "SignIn", Action.SIGN_IN);
	}
}
