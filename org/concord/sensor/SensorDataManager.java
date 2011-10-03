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
 * $Date: 2006-05-17 19:56:43 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.sensor;

import org.concord.sensor.device.SensorDevice;



/**
 * SensorDataManager
 * 
 * This Manager is used to create SensorDataProducers.  
 * Usually a class that implements this interface will be retrieved from 
 * an underlying configuration system.  The only current implementation is
 * org.concord.sensor.device.impl.InterfaceManager
 *
 * Date created: Jan 4, 2005
 *
 * @author scott<p>
 *
 */
public interface SensorDataManager
{
    /**
     * This might be temporary.  The design before was that users
     * of the sensor data manager shouldn't know about the SensorDevice
     * However right now using it is the easiest way for this to work.
     */
    public SensorDevice getSensorDevice();
    
	/**
	 * Creates a data producer that can then be configured with a
	 * particular experiment request.
	 * 
	 */
	public SensorDataProducer createDataProducer();
	
	/**
	 * This should return a sensordataproducer for all the currently 
	 * attached devices.  This method currently is not implemented.  When it 
	 * is implement it might take a long time to return. 
	 * @return
	 */
	public SensorDataProducer [] getAttachedDevices();
}
