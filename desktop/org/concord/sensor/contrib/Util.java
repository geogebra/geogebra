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
 * Created on Feb 22, 2005
 *
 */
 
package org.concord.sensor.contrib;
 
import org.concord.framework.text.UserMessageHandler;
import org.concord.sensor.DeviceConfig;
import org.concord.sensor.ExperimentConfig;
import org.concord.sensor.SensorDataManager;
import org.concord.sensor.SensorDataProducer;
import org.concord.sensor.SensorRequest;
import org.concord.sensor.device.impl.DeviceConfigImpl;
import org.concord.sensor.device.impl.InterfaceManager;
import org.concord.sensor.impl.ExperimentRequestImpl;
import org.concord.sensor.impl.SensorRequestImpl;
/**
 * some static helper functions
 * 
 * @author Dmitry Markman
 *
 */

public class Util
{
	public static SensorDataProducer getSensorDataProducer(int deviceId,int sensorType){
	    return getSensorDataProducer(deviceId,sensorType, null,null);
	}

	public static SensorDataProducer getSensorDataProducer(int deviceId, int sensorType, String configString,UserMessageHandler messenger){
		SensorDataManager  sdManager = new InterfaceManager(messenger);
		DeviceConfig [] dConfigs = new DeviceConfig[1];
		dConfigs[0] = new DeviceConfigImpl(deviceId, null);		
		((InterfaceManager)sdManager).setDeviceConfigs(dConfigs);
		
		ExperimentRequestImpl expRequest = new ExperimentRequestImpl();
        SensorRequestImpl sensRequest = new SensorRequestImpl();
        sensRequest.setType(sensorType);
        sensRequest.setRequiredMax(Float.NaN);
        sensRequest.setRequiredMin(Float.NaN);

        SensorRequest [] sensorRequests = new SensorRequest[1];
	    sensorRequests[0] = sensRequest;
	    expRequest.setSensorRequests(sensorRequests);	    
	    expRequest.setPeriod(0.1f);
		
		SensorDataProducer producer = sdManager.createDataProducer();
		if(producer != null){
		    ExperimentConfig ec = producer.configure(expRequest);
	    }
		return producer;
	}
}
