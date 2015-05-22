package org.geogebra.web.plugin;

import org.geogebra.common.plugin.SensorLogger.Types;

public interface WebSocketListener {
	public void onIDchecked(boolean correctID);
	public void onSensorActive(Types sensor, boolean flag);
	public void onFrequency(int parseInt);
}
