package org.geogebra.web.plugin;

import org.geogebra.common.plugin.SensorLogger.Types;

public interface WebSocketListener {

	void onIDchecked(boolean correctID);

	void onSensorActive(Types sensor, boolean flag);

	void onFrequency(int parseInt);

	void onDataReceived(Types sensor, double timestamp, int dataCount);
}
