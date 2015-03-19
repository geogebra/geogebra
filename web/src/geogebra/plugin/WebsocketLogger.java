package geogebra.plugin;

import geogebra.common.GeoGebraConstants;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.plugin.SensorLogger;

/**
 * @author gabor WebSocket logger for external mobile app
 */
public class WebsocketLogger implements SensorLogger {

	private Kernel kernel;
	private WebSocketConnection connection;

	public WebsocketLogger(Kernel kernel) {
		this.kernel = kernel;
		this.connection = WebSocketFactory
		        .create(
		        GeoGebraConstants.DATA_LOGGING_WEBSOCKET_URL);
	}

	public void stopLogging() {
		// TODO Auto-generated method stub

	}

	public boolean startLogging() {
		// TODO Auto-generated method stub
		return false;
	}

	public void registerGeo(String text, GeoNumeric number) {
		// TODO Auto-generated method stub

	}

	public void registerGeoList(String text, GeoList list) {
		// TODO Auto-generated method stub

	}

	public void registerGeoList(String text, GeoList list, double limit) {
		// TODO Auto-generated method stub

	}

}
