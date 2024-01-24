package org.geogebra.common.properties;

public final class PropertiesRegistry {

	// see comment below
//	private final App app;

	// the app may change (when switching calculators), and a new property
	// of the same type as a previously registered property may get registered,
	// so we need to register the (app, property) tuple really
	public void register(Property property) {
	}
}
