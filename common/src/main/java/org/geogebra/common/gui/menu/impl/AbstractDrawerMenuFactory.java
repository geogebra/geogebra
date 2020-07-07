package org.geogebra.common.gui.menu.impl;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.gui.menu.Action;
import org.geogebra.common.gui.menu.DrawerMenuFactory;
import org.geogebra.common.gui.menu.Icon;
import org.geogebra.common.gui.menu.MenuItem;

abstract class AbstractDrawerMenuFactory implements DrawerMenuFactory {

	protected GeoGebraConstants.Version version;

	AbstractDrawerMenuFactory(GeoGebraConstants.Version version) {
		this.version = version;
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
}
