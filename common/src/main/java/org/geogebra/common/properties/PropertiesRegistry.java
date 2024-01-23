package org.geogebra.common.properties;

import org.geogebra.common.main.App;

public final class PropertiesRegistry {

	// the app may change (when switching calculators), and a new property
	// of the same type as a previously registered property may register itself,
	// so we need to use register the (app, property) tuple reeally
	public void register(Property property, App app) {
	}
}
