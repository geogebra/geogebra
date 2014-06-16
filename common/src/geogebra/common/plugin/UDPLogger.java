package geogebra.common.plugin;

import geogebra.common.kernel.geos.GeoNumeric;

public interface UDPLogger {

	void stopLogging();

	boolean startLogging();

	void registerGeo(String text, GeoNumeric number);

}
