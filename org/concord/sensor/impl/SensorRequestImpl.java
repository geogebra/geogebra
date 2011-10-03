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

package org.concord.sensor.impl;

import org.concord.framework.data.DataDimension;
import org.concord.sensor.SensorRequest;


public class SensorRequestImpl
	implements SensorRequest
{
	protected int type = 0;
	protected float stepSize = 0.1f;
	protected float requiredMax;
	protected float requiredMin;
	protected int displayPrecision;
	protected int port;
	protected DataDimension unit;
	
	// we can't use hashtables because we want this class
	// to be portable to waba
	String [] paramKeys;
	String [] paramValues;
	
	public int getType()
	{
		return type;
	}
	
	public void setType(int type)
	{
		this.type = type;
	}
	
	public float getStepSize()
	{
		return stepSize;
	}
	
	public void setStepSize(float stepSize)
	{
		this.stepSize = stepSize;
	}
	
	public float getRequiredMax()
	{
		return requiredMax;
	}
	
	public void setRequiredMax(float requiredMax)
	{
		this.requiredMax = requiredMax;
	}
	
	public float getRequiredMin()
	{
		return requiredMin;
	}
	
	public void setRequiredMin(float requiredMin)
	{
		this.requiredMin = requiredMin;
	}
	
	public int getDisplayPrecision()
	{
		return displayPrecision;
	}
	
	public void setDisplayPrecision(int displayPrecision)
	{
		this.displayPrecision = displayPrecision;
	}
	
	public int getPort()
	{
		return port;
	}
	
	public void setPort(int port)
	{
		this.port = port;
	}
	
	public DataDimension getUnit()
	{
		return unit;
	}
	
	public void setUnit(DataDimension unit)
	{
		this.unit = unit;
	}
	
	public String getSensorParam(String key)
	{
		if(paramKeys == null) {
			return null;
		}
		
		for(int i=0; i<paramKeys.length; i++) {
			if(paramKeys[i].equals(key)) {
				return paramValues[i];
			}
		}

		return null;
	}

	public String [] getSensorParamKeys()
	{
		return paramKeys;
	}
	
	public void setSensorParams(String [] keys, String [] values)
	{
		paramKeys = keys;
		paramValues = values;
	}
}
