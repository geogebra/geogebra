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
 * $Revision: 1.25 $
 * $Date: 2007-06-06 13:02:38 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.sensor.device.impl;

import java.util.Hashtable;
import java.util.logging.Logger;

import org.concord.framework.text.UserMessageHandler;
import org.concord.sensor.DeviceConfig;
import org.concord.sensor.device.DeviceFactory;
import org.concord.sensor.device.DeviceIdAware;
import org.concord.sensor.device.DeviceService;
import org.concord.sensor.device.DeviceServiceAware;
import org.concord.sensor.device.SensorDevice;
import org.concord.sensor.impl.Ticker;
import org.concord.sensor.serial.SensorSerialPort;


/**
 * JavaDeviceFactory
 * Class name and description
 *
 * Date created: Dec 1, 2004
 *
 * @author scott<p>
 *
 */
public class JavaDeviceFactory
	implements DeviceFactory, DeviceID, DeviceService
{	
	private static final Logger logger = Logger.getLogger(JavaDeviceFactory.class.getCanonicalName());
	Ticker ticker = null;
		
	Hashtable deviceTable = new Hashtable();
	Hashtable configTable = new Hashtable();
	
	/*
	 *
		new DeviceID( 0, "Pseudo Device"           ,null),
		new DeviceID(10, "Vernier GoLink"         ,null),
		new DeviceID(11, "Vernier LabPro"         ,null),
		new DeviceID(12, "Vernier LabQuest"       ,null),
		new DeviceID(20, "TI Connect"              ,null),
		new DeviceID(30, "Fourier"                 ,null),
		new DeviceID(40, "Data Harvest USB"        ,null),
		new DeviceID(45, "Data Harvest CF"         ,null),
    	new DeviceID(41, "Data Harvest Advanced"   ,null),
    	new DeviceID(42, "Data Harvest QAdvanced"  ,null),
		new DeviceID(50, "ImagiWorks Serial"       ,null),
		new DeviceID(55, "ImagiWorks SD"           ,null),
		new DeviceID(60, "Pasco Serial"            ,null),
		new DeviceID(61, "Pasco Airlink"           ,null),
		new DeviceID(70, "CCProbe Version 0"       ,null),
		new DeviceID(71, "CCProbe Version 1"       ,null),
		new DeviceID(72, "CCProbe Version 2"       ,null),
		new DeviceID(80, "Coach"                   ,null),

	*/
	/**
	 * 
	 */
	public JavaDeviceFactory()
	{
		ticker = new JavaTicker();
	}

	/* (non-Javadoc)
	 * @see org.concord.sensor.DeviceFactory#createDevice(org.concord.sensor.DeviceConfig)
	 */
	public SensorDevice createDevice(DeviceConfig config)
	{
		int id = config.getDeviceId();
		String configStr = config.getConfigString();
		String deviceConfigId = "" + id + ":" + configStr;
		SensorDevice existingDevice = 
			(SensorDevice)deviceTable.get(deviceConfigId);
		if(existingDevice != null) {
			return existingDevice;
		}
		
		String className = null;
		SensorDevice device = null;
		
		switch(id) {
			case PSEUDO_DEVICE:
				className = "org.concord.sensor.pseudo.PseudoSensorDevice";
				break;
			case VERNIER_GO_LINK:
				className = "org.concord.sensor.nativelib.NativeVernierSensorDevice";
				break;
			case VERNIER_LAB_PRO:
				className = "org.concord.sensor.vernier.labpro.LabProSensorDevice";
				break;
			case VERNIER_LAB_QUEST:
				className = "org.concord.sensor.vernier.labquest.LabQuestSensorDevice";
				break;
			case TI_CONNECT:
				className = "org.concord.sensor.nativelib.NativeTISensorDevice";
				break;				
			case FOURIER:
			case DATA_HARVEST_USB:
            case DATA_HARVEST_ADVANCED:
            case DATA_HARVEST_QADVANCED:
			    className = "org.concord.sensor.dataharvest.DataHarvestSensorDevice";
			    break;			    
			case PASCO_SERIAL:
			    className = "org.concord.sensor.pasco.SW500SensorDevice";
			    break;
			case PASCO_AIRLINK:
			    className = "org.concord.sensor.pasco.AirLinkSensorDevice";
			    break;
			case PASCO_USB:
				className = "org.concord.sensor.pasco.PascoUsbSensorDevice";
				break;
			case DATA_HARVEST_CF:
			case IMAGIWORKS_SERIAL:
			case IMAGIWORKS_SD:
			case COACH:
				device = null;
				break;
				
			// TODO: need to handle config string so
			// the serial port can be specified
			case CCPROBE_VERSION_0:
			    className = "org.concord.sensor.cc.CCInterface0";
			    break;			    
			case CCPROBE_VERSION_1:
				className = "org.concord.sensor.cc.CCInterface1";
				break;
			case CCPROBE_VERSION_2:
			    className = "org.concord.sensor.cc.CCInterface2";
			    break;
		}

		if(className == null) {
			// We didn't get a class for this device so warn the user 
			// at least in the console
			System.err.println("Unknown Sensor Interface type: " + id);
			return null;
		}
		
		try {
			System.out.println("Loading sensor device: " + className);
			Class sensDeviceClass = 
				getClass().getClassLoader().loadClass(className);

			device = (SensorDevice) sensDeviceClass.newInstance();

			if(device instanceof DeviceIdAware) {
				((DeviceIdAware)device).setDeviceId(id);
			}

			if(device instanceof DeviceServiceAware) {
				((DeviceServiceAware)device).setDeviceService(this);
			}
			device.open(config.getConfigString());

		} catch (Exception e) {
			e.printStackTrace();

		}

		deviceTable.put(deviceConfigId, device);
		configTable.put(device, deviceConfigId);
		return device;		
	}

	public void destroyDevice(SensorDevice device)
	{
		device.close();
		
		String configStr = (String)configTable.get(device);		
		deviceTable.remove(configStr);
		configTable.remove(device);
		
	}
    
    public int getOSType()
    {
        String osName = System.getProperty("os.name");
        if(osName.startsWith("Windows")){
            return OS_WINDOWS;
        }
        if(osName.startsWith("Linux")){
            return OS_LINUX;
        }
        if(osName.startsWith("Mac OS X")){
            return OS_OSX;
        }
        
        return OS_UNKNOWN;
    }
    
    public SensorSerialPort getSerialPort(String name, SensorSerialPort oldPort)
    {
        String portClassName = null;
        
        if(FTDI_SERIAL_PORT.equals(name)){
            portClassName = "org.concord.sensor.dataharvest.SensorSerialPortFTDI";
        } else if(OS_SERIAL_PORT.equals(name)) {
            portClassName = "org.concord.sensor.serial.SensorSerialPortRXTX";
        } else if(LABPROUSB_SERIAL_PORT.equals(name)) {
            portClassName = "org.concord.sensor.vernier.labpro.SensorSerialPortLabProUSB";        	
        }
        		
            
        try {           
            Class portClass = getClass().getClassLoader().loadClass(portClassName);

            if(!portClass.isInstance(oldPort)){
                return(SensorSerialPort) portClass.newInstance();
            } else {
                return oldPort;
            }
        } catch (Exception e) {
            System.err.println("Can't load serial port driver class: " +
                    portClassName);
        }
        
        return null;
    }
    
    public void log(String message)
    {
        logger.info(message);        
    }
    
    public void sleep(int millis)
    {
        try{
            Thread.sleep(millis);
        } catch (InterruptedException e){
            e.printStackTrace();
        }
        
    }
    
    public long currentTimeMillis()
    {
        return System.currentTimeMillis();
    }
    
    public float intBitsToFloat(int valueInt)
    {
        return Float.intBitsToFloat(valueInt);
    }
    
    public boolean isValidFloat(float val)
    {
        return !Float.isNaN(val);
    }

    public UserMessageHandler getMessageHandler()
    {
        // TODO Auto-generated method stub
        return null;
    }
}
