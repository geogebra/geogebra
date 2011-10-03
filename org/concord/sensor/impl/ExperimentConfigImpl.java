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
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.concord.sensor.impl;

import org.concord.framework.util.Copyable;
import org.concord.sensor.ExperimentConfig;
import org.concord.sensor.SensorConfig;

/**
 * @author scott
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ExperimentConfigImpl 
	implements ExperimentConfig, Copyable
{
	private SensorConfig [] sensorConfigs = null;
	private boolean valid;
	private String invalidReason;
	private float period;
    private boolean exactPeriod;
    private String deviceName;
	private int deviceId;
    private float dataReadPeriod;

    private Range periodRange;
    
	/* (non-Javadoc)
	 * @see org.concord.sensor.ExperimentConfig#isValid()
	 */
	public boolean isValid() 
	{
		return valid;
	}

	public void setValid(boolean valid)
	{
	    this.valid = valid;
	}
	
	/* (non-Javadoc)
	 * @see org.concord.sensor.ExperimentConfig#getInvalidReason()
	 */
	public String getInvalidReason() 
	{
		return invalidReason;
	}

	public void setInvalidReason(String reason)
	{
	    invalidReason = reason;
	}
	/* (non-Javadoc)
	 * @see org.concord.sensor.ExperimentConfig#getPeriod()
	 */
	public float getPeriod() 
	{
		return period;
	}
	
	public void setPeriod(float period)
	{
	    this.period = period; 
	}
	
	/* (non-Javadoc)
	 * @see org.concord.sensor.ExperimentConfig#getExactPeriod()
	 */
	public boolean getExactPeriod() 
	{
		return exactPeriod;
	}
	
	public void setExactPeriod(boolean exact)
	{
	    exactPeriod = exact;
	}
	
	/* (non-Javadoc)
	 * @see org.concord.sensor.ExperimentConfig#getSensorConfigs()
	 */
	public SensorConfig[] getSensorConfigs() 
	{
		return sensorConfigs;
	}

	public void setSensorConfigs(SensorConfig [] sensorConfigs)
	{
		this.sensorConfigs = sensorConfigs;
	}
	
	/* (non-Javadoc)
	 * @see org.concord.sensor.ExperimentConfig#getDeviceName()
	 */
	public String getDeviceName() 
	{
		return deviceName;
	}

	public void setDeviceName(String name)
	{
	    deviceName = name;
	}
	
	/**
     * @return Returns the deviceId.
     */
    public int getDeviceId()
    {
        return deviceId;
    }
    
    /**
     * @param deviceId The deviceId to set.
     */
    public void setDeviceId(int deviceId)
    {
        this.deviceId = deviceId;
    }
    
    /* (non-Javadoc)
     * @see org.concord.sensor.ExperimentConfig#getDataReadPeriod()
     */
    public float getDataReadPeriod()
    {
        return dataReadPeriod;
    }
    
    /**
     * @param dataReadPeriod The dataReadPeriod to set.
     */
    public void setDataReadPeriod(float dataReadPeriod)
    {
        this.dataReadPeriod = dataReadPeriod;
    }

    /**
     * This will not be a deep copy
     */
    public Object getCopy()
    {
        ExperimentConfigImpl copy = new ExperimentConfigImpl();
        copy.dataReadPeriod = dataReadPeriod;
        copy.deviceId = deviceId;
        copy.deviceName = deviceName;
        copy.exactPeriod = exactPeriod;
        copy.invalidReason = invalidReason;
        copy.period = period;
        copy.sensorConfigs = sensorConfigs;
        copy.valid = valid;
        
        return copy;
    }

	public Range getPeriodRange()
    {
    	return periodRange;
    }

	public void setPeriodRange(Range periodRange)
    {
    	this.periodRange = periodRange;
    }    
}
