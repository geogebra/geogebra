package org.concord.sensor.vernier.goio;

import org.concord.sensor.ExperimentConfig;
import org.concord.sensor.ExperimentRequest;
import org.concord.sensor.device.DeviceReader;
import org.concord.sensor.device.impl.AbstractSensorDevice;
import org.concord.sensor.device.impl.SerialPortParams;
import org.concord.sensor.vernier.VernierSensorDevice;

//JVDH API
//     Start here, look at LabQuestSensorDevice.java
//     and                 VernierSensor, SensorID
public class GoIOSensorDevice extends AbstractSensorDevice implements
		VernierSensorDevice {

	@Override
	public void log(String message) {
		super.log(message);
	}
	
	@Override
	protected SerialPortParams getSerialPortParams() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean initializeOpenPort(String portName) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean canDetectSensors() {
		// TODO Auto-generated method stub
		return false;
	}

	public ExperimentConfig configure(ExperimentRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	public ExperimentConfig getCurrentConfig() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getDeviceName() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getErrorMessage(int error) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getVendorName() {
		// TODO Auto-generated method stub
		return null;
	}

	public int read(float[] values, int offset, int nextSampleOffset,
			DeviceReader reader) {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean start() {
		// TODO Auto-generated method stub
		return false;
	}

	public void stop(boolean wasRunning) {
		// TODO Auto-generated method stub

	}

}
