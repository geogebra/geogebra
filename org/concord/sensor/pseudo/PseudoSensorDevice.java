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
 * Created on Jan 25, 2005
 *
 */
package org.concord.sensor.pseudo;

import org.concord.sensor.ExperimentConfig;
import org.concord.sensor.ExperimentRequest;
import org.concord.sensor.SensorConfig;
import org.concord.sensor.SensorRequest;
import org.concord.sensor.device.DeviceReader;
import org.concord.sensor.device.impl.AbstractSensorDevice;
import org.concord.sensor.device.impl.SerialPortParams;
import org.concord.sensor.impl.ExperimentConfigImpl;
import org.concord.sensor.impl.Range;
import org.concord.sensor.impl.SensorUnit;

/**
 * @author scott
 *
 */
public class PseudoSensorDevice extends AbstractSensorDevice 
{   
    PseudoSensorConfig [] sensConfigs; 
	float time = 0;
	
	public String getVendorName()
	{
		return "Simulated";
	}
	
	public String getDeviceName()
	{
		return "Simulated Data";
	}
	
	/* (non-Javadoc)
	 * @see org.concord.sensor.device.SensorDevice#open(java.lang.String)
	 */
	public void open(String openString) 
	{
		// we have no real sensors so we dont' need to open anything
	}

	/* (non-Javadoc)
	 * @see org.concord.sensor.device.SensorDevice#configure(org.concord.sensor.ExperimentRequest)
	 */
	public ExperimentConfig configure(ExperimentRequest request) 
	{
		// We need to return a valid config that matches the request
		// because we are going to pretend to handle anything.
		currentConfig = new ExperimentConfigImpl();
		currentConfig.setValid(true);
		currentConfig.setDeviceName("Psuedo Device");
		currentConfig.setExactPeriod(true);
		currentConfig.setPeriod(0.1f);
		currentConfig.setDataReadPeriod(0.1f);
		
		SensorRequest [] sensRequests = request.getSensorRequests();
		sensConfigs = new PseudoSensorConfig [sensRequests.length]; 
		for(int i =0; i<sensRequests.length; i++) {
			PseudoSensorConfig sensConfig = new PseudoSensorConfig();
			sensConfigs[i] = sensConfig;
			sensConfig.setPort(sensRequests[i].getPort());
			sensConfig.setStepSize(sensRequests[i].getStepSize());
			sensConfig.setType(sensRequests[i].getType());
			sensConfig.setUnit(sensRequests[i].getUnit());
			float max = sensRequests[i].getRequiredMax();
			float min = sensRequests[i].getRequiredMin();
			sensConfig.setValueRange(new Range(min, max));
			if(devService.isValidFloat(max) && devService.isValidFloat(min)){
			    sensConfig.setSinOffset((max + min) / 2);
			    sensConfig.setSinMagnitude((max - min) / 2);
			}
		}
		currentConfig.setSensorConfigs(sensConfigs);
		return currentConfig;
	}

	/* (non-Javadoc)
	 * @see org.concord.sensor.device.SensorDevice#start()
	 */
	public boolean start() 
	{
		time = 0;
		// We have no real sensors so no problem starting 
		return true;
	}

	/* (non-Javadoc)
	 * @see org.concord.sensor.device.SensorDevice#read(float[], int, int, org.concord.sensor.device.DeviceReader)
	 */
	public int read(float[] values, int offset, int nextSampleOffset,
			DeviceReader reader) 
	{
		// for a test just return a single value each time
		// this could make it seem out of sync because we said the period 
		// was X but we are only return a single value every time, so if the 
		// read takes longer than X then we will be behind.  But for testing
		// purposes this should be ok
	    for(int i=0; i<sensConfigs.length; i++) {
	        float sinOffset = sensConfigs[i].getSinOffset();
	        float sinMagnitude = sensConfigs[i].getSinMagnitude();
	        values[offset++] = (float)(sinOffset + sinMagnitude*Math.sin(time));
	    }
	    time += 0.1;
		return 1;		
	}

	/* (non-Javadoc)
	 * @see org.concord.sensor.device.SensorDevice#stop(boolean)
	 */
	public void stop(boolean wasRunning) 
	{
		time = 0;
	}

	/* (non-Javadoc)
	 * @see org.concord.sensor.device.SensorDevice#getErrorMessage(int)
	 */
	public String getErrorMessage(int error) 
	{
		return null;
	}

	/* (non-Javadoc)
	 * @see org.concord.sensor.device.SensorDevice#isAttached()
	 */
	public boolean isAttached() 
	{
		return true;
	}

	/* (non-Javadoc)
	 * @see org.concord.sensor.device.SensorDevice#canDetectSensors()
	 */
	public boolean canDetectSensors() 
	{
		return true;
	}

	/* (non-Javadoc)
	 * @see org.concord.sensor.device.SensorDevice#getCurrentConfig()
	 */
	public ExperimentConfig getCurrentConfig() 
	{
		// If we have been configured before return that configuration
		if(currentConfig != null) {
			return currentConfig;
		}

		// Otherwise return a new configuration that says we 
		// have a temperature sensor
		
		// We need to return a valid config that matches the request
		// because we are going to pretend to handle anything.
		currentConfig = new ExperimentConfigImpl();
		SensorConfig [] sensConfigs = new SensorConfig [1]; 
		PseudoSensorConfig sensConfig = new PseudoSensorConfig();
		sensConfigs[0] = sensConfig;
		
		sensConfig.setPort(0);
		sensConfig.setStepSize(0.1f);
		sensConfig.setType(SensorConfig.QUANTITY_TEMPERATURE);
		sensConfig.setUnit(new SensorUnit("degC"));
		currentConfig.setSensorConfigs(sensConfigs);
		return currentConfig;
	}

	/* (non-Javadoc)
	 * @see org.concord.sensor.device.impl.AbstractSensorDevice#getSerialPortParams()
	 */
	protected SerialPortParams getSerialPortParams()
	{
		return null;
	}

	/* (non-Javadoc)
	 * @see org.concord.sensor.device.impl.AbstractSensorDevice#initializeOpenPort(java.lang.String)
	 */
	protected boolean initializeOpenPort(String portName)
	{
		return true;
	}
}
