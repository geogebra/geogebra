package org.geogebra.common.gui.menu.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.gui.menu.Action;
import org.geogebra.common.gui.menu.DrawerMenuFactory;
import org.geogebra.common.gui.menu.Icon;
import org.geogebra.common.gui.menu.MenuItem;

abstract class AbstractDrawerMenuFactory implements DrawerMenuFactory {

	protected final GeoGebraConstants.Version version;
	private final boolean isSuiteApp;

	/**
	 * Default constructor.
	 * @param version version
	 * @param isSuiteApp whether it is the Suite app
	 */
	AbstractDrawerMenuFactory(GeoGebraConstants.Version version, boolean isSuiteApp) {
		this.version = version;
		this.isSuiteApp = isSuiteApp;
	}

	String getMenuTitle() {
		if (isSuiteApp) {
			return "GeoGebraCalculatorSuite";
		}

		return version.getTransKey();
	}

	static MenuItem clearConstruction() {
		return new ActionableItemImpl(Icon.CLEAR, "Clear", Action.CLEAR_CONSTRUCTION);
	}

	protected static MenuItem openFile() {
		return new ActionableItemImpl(Icon.SEARCH, "Load", Action.SHOW_SEARCH_VIEW);
	}

	protected static MenuItem saveFile() {
		return new ActionableItemImpl(Icon.SAVE, "Save", Action.SAVE_FILE);
	}

	@Nullable
	MenuItem showSwitchCalculator() {
		return isSuiteApp
				? new ActionableItemImpl(Icon.GEOGEBRA,
				"SwitchCalculator", Action.SWITCH_CALCULATOR)
				: null;
	}

	@SafeVarargs
	protected final <T> List<T> removeNulls(T... groups) {
		ArrayList<T> list = new ArrayList<>();
		for (T group : groups) {
			if (group != null) {
				list.add(group);
			}
		}
		return list;
	}

	public boolean isSuiteApp() {
		return isSuiteApp;
	}
}
