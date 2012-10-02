/*
 *  Copyright (C) 2004  The Concord Consortium, Inc.,
 *  10 Concord Crossing, Concord, MA 01742
 *
 *  Web Site: http://www.concord.org
 *  Email: info@concord.org
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * END LICENSE */

/*
 * Created on Dec 10, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.concord.sensor.nativelib;


import org.concord.framework.data.stream.DataListener;
import org.concord.framework.data.stream.DataStreamEvent;
import org.concord.framework.text.UserMessageHandler;
import org.concord.sensor.DeviceConfig;
import org.concord.sensor.ExperimentConfig;
import org.concord.sensor.SensorConfig;
import org.concord.sensor.SensorDataManager;
import org.concord.sensor.SensorDataProducer;
import org.concord.sensor.SensorRequest;
import org.concord.sensor.device.SensorDevice;
import org.concord.sensor.device.impl.DeviceConfigImpl;
import org.concord.sensor.device.impl.InterfaceManager;
import org.concord.sensor.device.impl.JavaDeviceFactory;
import org.concord.sensor.impl.ExperimentRequestImpl;
import org.concord.sensor.impl.SensorRequestImpl;
import org.concord.sensor.state.PrintUserMessageHandler;

/**
 * @author Informaiton Services
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TestNative 
{
	public static void main(String[] args) 
	{
		UserMessageHandler messenger = new PrintUserMessageHandler();
		SensorDataManager  sdManager = new InterfaceManager(messenger);
		
		// This should be loaded from the OTrunk.  Each computer
		// might have a different set of devices configured.
		DeviceConfig [] dConfigs = new DeviceConfig[1];
		dConfigs[0] = new DeviceConfigImpl(JavaDeviceFactory.VERNIER_GO_LINK, null);		
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
		sensor.setType(SensorConfig.QUANTITY_TEMPERATURE);

		request.setSensorRequests(new SensorRequest [] {sensor});
				
		SensorDataProducer sDataProducer = 
		    sdManager.createDataProducer();
		sDataProducer.configure(request);
		sDataProducer.addDataListener(new DataListener(){
			public void dataReceived(DataStreamEvent dataEvent)
			{
				int numSamples = dataEvent.getNumSamples();
				float [] data = dataEvent.getData();
				if(numSamples > 0) {
					System.out.println("" + numSamples + " " +
								data[0]);
					System.out.flush();
				} 
				else {
					System.out.println("" + numSamples);
				}
			}

			public void dataStreamEvent(DataStreamEvent dataEvent)
			{				
				String eventString;
				int eventType = dataEvent.getType();
				
				if(eventType == 1001) return;
				
				switch(eventType) {
					case DataStreamEvent.DATA_DESC_CHANGED:
						eventString = "Description changed";
					break;
					default:
						eventString = "Unknown event type";					
				}
				
				System.out.println("Data Event: " + eventString); 
			}
		});
		
		sDataProducer.start();
		
		System.out.println("started device");
		try {
			Thread.sleep(1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		sDataProducer.stop();
		
		sDataProducer.close();
		
		System.exit(0);
	}
}
