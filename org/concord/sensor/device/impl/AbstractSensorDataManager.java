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
 * Last modification information:
 * $Revision: 1.3 $
 * $Date: 2007-03-28 21:57:15 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.sensor.device.impl;

import java.util.ArrayList;

import org.concord.framework.text.UserMessageHandler;
import org.concord.framework.text.UserMessageHandlerExt1;
import org.concord.sensor.DeviceConfig;
import org.concord.sensor.SensorDataManager;
import org.concord.sensor.device.DeviceFactory;
import org.concord.sensor.device.DeviceService;
import org.concord.sensor.device.SensorDevice;
import org.concord.sensor.impl.Ticker;

public abstract class AbstractSensorDataManager
    implements SensorDataManager
{
    protected static DeviceFactory deviceFactory = null;

    protected UserMessageHandler messageHandler;
    protected Ticker ticker;
        
    private DeviceConfig[] deviceConfigs;
        
    private SensorDevice currentDevice = null; 

    public AbstractSensorDataManager(UserMessageHandler msgHandler,
            Ticker ticker)
    {
        this.messageHandler = msgHandler;
        this.ticker = ticker;
    }
    
    class OpenAttempt {
    	String vendorName;
    	String deviceName;
    	String error;
    	DeviceConfig config;
    	String className;
    }
    
    public SensorDevice getSensorDevice()
    {
    	DeviceConfig [] localDeviceConfigs = getDeviceConfigs();
    	
    	if(localDeviceConfigs == null) {
            log("Searching all possible devices isn't supported yet");
    		return null;
    	}
     
        if(currentDevice != null) {
            // check if it is attached.
            // if not then it should be closed.
            // this means we only support one device at a time
            if(ticker.isTicking()) {
                ticker.stopTicking(null);
            }
            
            if(currentDevice.isAttached()) {
                return currentDevice;
            } else {
                deviceFactory.destroyDevice(currentDevice);
                currentDevice = null;                
            }
        }

        ArrayList<OpenAttempt> openAttempts = new ArrayList<OpenAttempt>();
        for(int i=0; i<localDeviceConfigs.length; i++) {
            SensorDevice device = 
                deviceFactory.createDevice(localDeviceConfigs[i]);
            if(device.isAttached()){
                currentDevice = device;
                return currentDevice;
            }
            
            OpenAttempt attempt = new OpenAttempt();
            attempt.vendorName = device.getVendorName();
            attempt.deviceName = device.getDeviceName();
            attempt.error = device.getErrorMessage(0);
            attempt.config = localDeviceConfigs[i];
            attempt.className = device.getClass().getCanonicalName();
            openAttempts.add(attempt);
            
            deviceFactory.destroyDevice(device);
        }

        // If we got here then it means there was no device
        // found, we'll print the error from the last device
        // searched for.
        if(messageHandler != null) {
        	// Construct the error message
        	String title = "Sensor Interface Not Found";
        	String body = "";
        	String details = null;
    		ArrayList<String> deviceNames = new ArrayList<String>();
    		
    		// collect a unique list of device names
    		for(OpenAttempt attempt: openAttempts){
        		String name = attempt.vendorName + " " + attempt.deviceName;
        		if(deviceNames.contains(name)){
        			continue;
        		}
        		deviceNames.add(name);
    		}

    		if(deviceNames.size() == 1){
        		OpenAttempt attempt = openAttempts.get(0);
        		body += attempt.vendorName + " " + attempt.deviceName + " could not be found.";
        		if(attempt.error != null){
        			body += "\n" + attempt.error;
        		}
        	} else {
        		body += "None of the following devices could be found:\n";
        		for(String deviceName: deviceNames){
        			body += "  " + deviceName;
        		}
        	}

        	details = "";
        	int index = 0;
        	for(OpenAttempt attempt: openAttempts){
        		if(openAttempts.size() > 1){
        			details += "Attempt: " + index + "\n";
        		}
        		details += "Error: " + attempt.error + "\n";
        		details += "Device Id: " + attempt.config.getDeviceId() + "\n";
        		details += "Class Name: " + attempt.className + "\n";        		
        		details += "Config: " + attempt.config.getConfigString() + "\n";        		
        		index++;
        	}
        	
        	if(messageHandler instanceof UserMessageHandlerExt1){
        		((UserMessageHandlerExt1) messageHandler).showMessage(body, title, details);
        	} else {
        		messageHandler.showMessage(body, title);
        	}
        }

        return currentDevice;        
    }
    
    /**
     * This returns all the device configs that have been set.  
     * These configs are used to determine the available devices.
     * @return
     */
    public DeviceConfig [] getDeviceConfigs()
    {
        return deviceConfigs; 
    }
    
    /**
     * Set the list of device configs.  This list is used when
     * prepareDataProducer is called.  The list is traversed and the 
     * first available device is used.  If the list is null then all 
     * devices available from the DeviceFactory are checked for.  
     * 
     * @param configs
     */
    public void setDeviceConfigs(DeviceConfig [] configs)
    {
        deviceConfigs = configs;
    }
    
    /**
     * This factory is used to actually create the SensorDataProducers.
     * 
     * 
     * @param factory
     */
    public static void setDeviceFactory(DeviceFactory factory)
    {
        deviceFactory = factory;
    }
    
    protected void log(String message)
    {
        if(deviceFactory instanceof DeviceService) {
            ((DeviceService)deviceFactory).log(message);
        }
    }
}
