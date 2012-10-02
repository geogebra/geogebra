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
 * $Revision: 1.7 $
 * $Date: 2005-09-09 14:01:58 $
 * $Author: maven $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.sensor.nativelib;

import geogebra.common.main.App;

import org.concord.sensor.ExperimentRequest;
import org.concord.sensor.SensorRequest;
import org.concord.sensor.device.DeviceReader;
import org.concord.sensor.device.SensorDevice;

import ccsd.vernier.ExperimentConfig;
import ccsd.vernier.NativeBridge;
import ccsd.vernier.SWIGTYPE_p_float;
import ccsd.vernier.SWIGTYPE_p_void;
import ccsd.vernier.SensorConfig;

/**
 * NativeSensorDevice
 * Class name and description
 *
 * Date created: Dec 2, 2004
 *
 * @author scott<p>
 *
 */
public class NativeVernierSensorDevice 
	implements SensorDevice
{
	public static final String VERNIER_NATIVE_LIB_LOADED = "org.concord.sensor.vernier.loaded";
    SWIGTYPE_p_void deviceHandle = null;
	SWIGTYPE_p_float readValuesBuffer = null;
	SWIGTYPE_p_float readTimestampsBuffer = null;
	private boolean open;
	private boolean nativeLibAvailable = false;
	private boolean useTimeStamps = false;
	private int numberOfChannels = 1;
	private String lastErrorMessage = "no error message yet";
	
	/**
	 * 
	 */
	public NativeVernierSensorDevice()
	{
	    if (Boolean.getBoolean(VERNIER_NATIVE_LIB_LOADED)) {
	        nativeLibAvailable = true;
	    } else {
	        try {
	            if(System.getProperty("os.name").startsWith("Windows")) {
	                System.loadLibrary("GoIO_DLL");
	            }else if(System.getProperty("os.name").startsWith("Mac")) {
	                try{//I need try/catch block for not interrupting remaining code
	                    System.loadLibrary("SetDylibPath");
	                }catch(Throwable tt){}
	            }

	            System.loadLibrary("vernier_ccsd");
	            nativeLibAvailable = true;
	            //			System.loadLibrary("blah");
	        } catch (Throwable thr) {
	            thr.printStackTrace();
	        }
	    }
	}
	
	public synchronized void open(String config)
	{
		if(!nativeLibAvailable) {
			open = false;
			lastErrorMessage = "Cannot load vernier GoIO native library";
			return;
		}
		
		open = true;
		deviceHandle = NativeBridge.SensDev_open(config);
		if(readValuesBuffer == null) {
			readValuesBuffer = NativeBridge.new_floatArray(200);
		}
		
		if(readTimestampsBuffer == null) {
			readTimestampsBuffer = NativeBridge.new_floatArray(200);
		}
	}
	
	public synchronized void close()
	{
		open = false;
		if(deviceHandle != null) {
			NativeBridge.SensDev_close(deviceHandle);
		}
	}
		
	/* (non-Javadoc)
	 * @see org.concord.sensor.device.AbstractSensorDevice#getErrorMessage(int)
	 */
	public String getErrorMessage(int error)
	{		
		return lastErrorMessage;
	}
	
	
	/* (non-Javadoc)
	 * @see org.concord.sensor.device.AbstractSensorDevice#getRightMilliseconds()
	 */
	public int getRightMilliseconds()
	{
		// TODO Base this on the time between samples from the device
		return 50;
	}
	
	/**
	 * Send this directly to the native dll.
	 * It will need some configuration string which would be set earlier
	 * so there probably needs to be a cookie that is passed around for
	 * this device to store things.
	 * @see org.concord.sensor.SensorDataProducer#isAttached()
	 */
	public synchronized boolean isAttached()
	{
		if(!open) return false;
		
		App.warn(deviceHandle.toString());

		int result = NativeBridge.SensDev_isAttached(deviceHandle);
		App.warn("result="+result);
		return result == 1;
	}

	/**
	 * convert the experimentconfig to native structures.
	 * this is probably done in the native code???
	 * @see org.concord.sensor.SensorDataProducer#configure(org.concord.sensor.ExperimentConfig)
	 */
	public synchronized org.concord.sensor.ExperimentConfig configure(ExperimentRequest request)
	{
		ExperimentConfig requestConfig = 
			new ExperimentConfig();

		requestConfig.setPeriod(request.getPeriod());
		requestConfig.setNumberOfSamples(request.getNumberOfSamples());
		
		SensorRequest [] sensorRequests = request.getSensorRequests();

		requestConfig.setNumSensorConfigs(sensorRequests.length);
		requestConfig.createSensorConfigArray(sensorRequests.length);
		numberOfChannels = sensorRequests.length;
		for(int i=0; i<sensorRequests.length; i++) {
			SensorRequest sensorRequest = sensorRequests[i];
			SensorConfig sensorConfig = new SensorConfig();
			sensorConfig.setPort(sensorRequest.getPort());
			sensorConfig.setType(sensorRequest.getType());
			sensorConfig.setStepSize(sensorRequest.getStepSize());
			sensorConfig.setRequiredMax(sensorRequest.getRequiredMax());
			sensorConfig.setRequiredMin(sensorRequest.getRequiredMin());
			// TODO this should be based on the default unit
			// for this type 
			sensorConfig.setUnitStr("degC");
			requestConfig.setSensorConfig(sensorConfig, i);
		}
		
		// TODO we need to make sure that java owns this object
		// that is returned by the sensor device.  Otherwise
		// the memory will never get freed.
		org.concord.sensor.ExperimentConfig expConfig = 
			NativeBridge.configureHelper(deviceHandle, requestConfig);
		if(expConfig != null) {
			useTimeStamps = !expConfig.getExactPeriod();
		}
		return expConfig;
	}

	/**
	 * native
	 * 
	 * @see org.concord.sensor.SensorDataProducer#getCurrentConfig()
	 */
	public org.concord.sensor.ExperimentConfig getCurrentConfig()
	{
		return NativeBridge.getCurrentConfigHelper(deviceHandle);
	}

	/**
	 * This could be native, but since the native implementation only works with 
	 * go-link and go-temp we can just return true here.
	 * @see org.concord.sensor.SensorDataProducer#canDetectSensors()
	 */
	public boolean canDetectSensors()
	{
		return true;
	}

	/**
	 * 
	 * native. but with the associated cookie.  unless there is another
	 * perhaps the cookie can be stored in this object so the native code
	 * can look it up and then it won't need to be in each of these methods.
	 * @see org.concord.framework.startable.Startable#stop()
	 */
	public synchronized void stop(boolean wasRunning)
	{
		// TODO check for null device
		NativeBridge.SensDev_stop(deviceHandle);
	}
	
	/**
	 * native.
	 * @see org.concord.framework.startable.Startable#start()
	 */
	public synchronized boolean start()
	{
		// TODO check for null device
		NativeBridge.SensDev_start(deviceHandle);
		
		return true;

	}

	public synchronized int read(float [] values, int offset, 
			int nextSampleOffset, DeviceReader reader)
	{
		// take our existing native float pointer
		// pass it to the native read function so it gets filled in
		// traverse the resutling array (in C) and copy the values 
		// into this array.  This could be more efficient if the
		// the array functions generated by swig included a "copy"
		// function that would copy a section of the array.
		int numberRead = NativeBridge.SensDev_read(deviceHandle, readValuesBuffer, 
				readTimestampsBuffer, 200);
		
		if (numberRead < 0) {
			System.err.println("error reading values from device");
		}
		
		int valPos = offset;
		for(int i=0; i<numberRead; i++) {
			int firstChannelValuePos = valPos;
			if(useTimeStamps) {
				values[valPos] = NativeBridge.floatArray_getitem(readTimestampsBuffer, i);
				firstChannelValuePos = valPos+1;
			}
			for(int j=0; j<numberOfChannels; j++) {
				values[firstChannelValuePos+j] = 
					NativeBridge.floatArray_getitem(readValuesBuffer, i*numberOfChannels+j);
			}
			
			valPos += nextSampleOffset;
		}
		
		return numberRead;
	}
	
	public String getVendorName() 
	{
		return "Vernier";
	}

	public String getDeviceName() 
	{
		// this can be a Go! Link, Go! Temp, or Go! Motion 
		return "Go! Device";
	}
	
}
