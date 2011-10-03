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
 * $Date: 2005-08-05 18:26:08 $
 * $Author: maven $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.sensor.device.impl;

import org.concord.sensor.DeviceConfig;


/**
 * DeviceConfig
 * Class name and description
 *
 * Date created: Dec 1, 2004
 *
 * @author scott<p>
 *
 */
public class DeviceConfigImpl implements DeviceConfig
{
	/**
	 * An id assigned by CC for this device
	 * This id will map to a particular SensorDevice class and maybe
	 * some extra config for that device for example if the device has
	 * a native driver then the id will map to the jni device class and
	 * the native driver dll name.
	 */ 
	protected int deviceId;
	
	/**
	 * This is a string that configures this device.  It could be serial
	 * port number.  Or if it is a networked device it could be an
	 * internet address for this device.
	 */
	protected String configString;

	public DeviceConfigImpl(int deviceId, String configString)
	{
		this.deviceId = deviceId;
		this.configString = configString;
	}
	
	public void setDeviceId(int id)
	{
		deviceId = id;
	}
	
	public int getDeviceId()
	{
		return deviceId;
	}
	
	public void setConfigString(String config)
	{
		configString = config;
	}
	
	public String getConfigString()
	{
		return configString;
	}
}
