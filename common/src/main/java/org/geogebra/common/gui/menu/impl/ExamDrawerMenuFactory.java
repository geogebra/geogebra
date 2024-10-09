package org.geogebra.common.gui.menu.impl;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.gui.menu.Action;
import org.geogebra.common.gui.menu.DrawerMenu;
import org.geogebra.common.gui.menu.Icon;
import org.geogebra.common.gui.menu.MenuItem;
import org.geogebra.common.gui.menu.MenuItemGroup;

/**
 * Creates drawer menus for apps when in exam mode.
 */
public class ExamDrawerMenuFactory extends AbstractDrawerMenuFactory {

	private boolean createsExitExam = true;

	/**
	 * Create a new ExamDrawerMenuFactory.
	 * @param version version of the app
	 */
	public ExamDrawerMenuFactory(GeoGebraConstants.Version version) {
		this(version, version.equals(GeoGebraConstants.Version.SUITE));
	}

	/**
	 * Create a new ExamDrawerMenuFactory.
	 * @param version version of the app
	 * @param isSuiteApp whether it is the Suite app
	 */
	public ExamDrawerMenuFactory(GeoGebraConstants.Version version,
			boolean isSuiteApp) {
		super(version, isSuiteApp);
	}

	/**
	 * Set whether it should create exit exam menu item.
	 *
	 * @param createsExitExam true to create menu item
	 */
	public void setCreatesExitExam(boolean createsExitExam) {
		this.createsExitExam = createsExitExam;
	}

	@Override
	public DrawerMenu createDrawerMenu() {
		boolean isScientific = version == GeoGebraConstants.Version.SCIENTIFIC;
		MenuItem clearConstruction = clearConstruction();
		MenuItem openFile = isScientific && !isSuiteApp() ? null : openFile();
		MenuItem saveFile = isScientific && !isSuiteApp() ? null : saveFile();
		MenuItem switchCalculator = showSwitchCalculator();
		MenuItem examLog = showExamLog();
		MenuItem exitExam = createsExitExam ? exitExamMode() : null;
		MenuItemGroup group = new MenuItemGroupImpl(removeNulls(clearConstruction, openFile,
				saveFile, switchCalculator, examLog, exitExam));
		String title = getMenuTitle();
		return new DrawerMenuImpl(title, group);
	}

	private static MenuItem exitExamMode() {
		return new ActionableItemImpl(Icon.HOURGLASS_EMPTY,
				"exam_menu_exit", Action.EXIT_EXAM_MODE);
	}

	private static MenuItem showExamLog() {
		return new ActionableItemImpl(Icon.ASSIGNMENT,
				"exam_log_header", Action.SHOW_EXAM_LOG);
	}
}
