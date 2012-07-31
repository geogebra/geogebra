package geogebra.usb;

import org.concord.framework.data.stream.DataListener;
import org.concord.framework.text.UserMessageHandler;
import org.concord.sensor.DeviceConfig;
import org.concord.sensor.ExperimentConfig;
import org.concord.sensor.SensorConfig;
import org.concord.sensor.SensorDataManager;
import org.concord.sensor.SensorDataProducer;
import org.concord.sensor.SensorRequest;
import org.concord.sensor.device.SensorDevice;
import org.concord.sensor.device.impl.DeviceConfigImpl;
import org.concord.sensor.device.impl.DeviceID;
import org.concord.sensor.device.impl.InterfaceManager;
import org.concord.sensor.impl.ExperimentRequestImpl;
import org.concord.sensor.impl.SensorRequestImpl;
import org.concord.sensor.state.PrintUserMessageHandler;

public class USBLogger {
	
	public SensorDataProducer sDataProducer = null;
	
	public USBLogger(DataListener dataListener) {
		UserMessageHandler messenger = new PrintUserMessageHandler();
		SensorDataManager  sdManager = new InterfaceManager(messenger);
		
		// This should be loaded from the OTrunk.  Each computer
		// might have a different set of devices configured.
		DeviceConfig [] dConfigs = new DeviceConfig[1];
		dConfigs[0] = new DeviceConfigImpl(DeviceID.VERNIER_GO_LINK, null);		
		((InterfaceManager)sdManager).setDeviceConfigs(dConfigs);
				
		// Check what is attached, this isn't necessary if you know what you want
		// to be attached.  But sometimes you want the user to see what is attached
		SensorDevice sensorDevice = sdManager.getSensorDevice();
		ExperimentConfig currentConfig = sensorDevice.getCurrentConfig();
		//SensorUtilJava.printExperimentConfig(currentConfig);
		
		
		ExperimentRequestImpl request = new ExperimentRequestImpl();
		request.setPeriod(0.1f);
		request.setNumberOfSamples(-1);
		
		SensorRequestImpl sensor = new SensorRequestImpl();
		sensor.setDisplayPrecision(-2);
		sensor.setRequiredMax(Float.NaN);
		sensor.setRequiredMin(Float.NaN);
		sensor.setPort(0);
		sensor.setStepSize(0.1f);
		sensor.setType(SensorConfig.QUANTITY_DISTANCE);

		request.setSensorRequests(new SensorRequest [] {sensor});
				
		sDataProducer = 
		    sdManager.createDataProducer();
		sDataProducer.configure(request);
		sDataProducer.addDataListener(dataListener);
		
		/*
		sDataProducer.start();
		
		System.out.println("started device");
		try {
			Thread.sleep(1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		sDataProducer.stop();
		
		sDataProducer.close();
		
		System.exit(0);*/
	}


}
