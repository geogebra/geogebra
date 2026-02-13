/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.gui.menu.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.CheckForNull;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.gui.menu.Action;
import org.geogebra.common.gui.menu.DrawerMenuFactory;
import org.geogebra.common.gui.menu.Icon;
import org.geogebra.common.gui.menu.MenuItem;
import org.geogebra.common.main.App;
import org.geogebra.common.ownership.GlobalScope;
import org.geogebra.common.ownership.SuiteScope;

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

	@CheckForNull MenuItem showSwitchCalculator(App app) {
		if (!isSuiteApp) {
			return null;
		}
		SuiteScope suiteScope = GlobalScope.getSuiteScope(app);
		if (suiteScope != null && suiteScope.getEnabledSubApps().size() == 1) {
			return null;
		}
		return new ActionableItemImpl(Icon.GEOGEBRA, "SwitchCalculator", Action.SWITCH_CALCULATOR);
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

	boolean isSuiteApp() {
		return isSuiteApp;
	}
}
