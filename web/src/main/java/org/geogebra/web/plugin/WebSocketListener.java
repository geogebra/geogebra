package org.geogebra.web.plugin;

import org.geogebra.common.plugin.SensorLogger.Types;

public interface WebSocketListener {
	public void onWrongID();
	public void onSensorActive(Types sensor, boolean flag);
}
