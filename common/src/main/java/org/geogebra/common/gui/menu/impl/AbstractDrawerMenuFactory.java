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
	private final boolean enableSwitchCalculator;

	/**
	 * Default constructor.
	 * @param version version
	 * @param enableSwitchCalculator enable switch calculator item
	 */
	AbstractDrawerMenuFactory(GeoGebraConstants.Version version, boolean enableSwitchCalculator) {
		this.version = version;
		this.enableSwitchCalculator = enableSwitchCalculator;
	}

	String getMenuTitle() {
		switch (version) {
		case GRAPHING:
			return "GeoGebraGraphingCalculator";
		case GRAPHING_3D:
			return "GeoGebra3DGrapher";
		case SCIENTIFIC:
			return "GeoGebraScientificCalculator";
		case CAS:
			return "GeoGebraCASCalculator";
		case GEOMETRY:
			return "GeoGebraGeometry";
		case NOTES:
			return "GeoGebraNotes";
		case SUITE:
			return "GeoGebraCalculatorSuite";
		default:
			return null;
		}
	}

	static MenuItem clearConstruction() {
		return new ActionableItemImpl(Icon.CLEAR, "Clear", Action.CLEAR_CONSTRUCTION);
	}

	@Nullable
	MenuItem showSwitchCalculator() {
		return enableSwitchCalculator
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
}
