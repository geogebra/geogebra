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
 * $Date: 2005-11-15 16:07:04 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.sensor;

import org.concord.framework.data.stream.DataProducer;


/**
 * SensorDataProducer
 * 
 * This is a special data producer that represents a sensor device.
 * It can be configured with ExperimentRequests and it can be asked for the
 * current configuration.
 * 
 * Generally the SensorDataManager is used to create these data producers.
 *
 * Date created: Nov 30, 2004
 *
 * @author scott<p>
 *
 */
public interface SensorDataProducer
	extends DataProducer
{
	/**
	 * This determines if the deivice is attached.  This is 
	 * generally called by the InterfaceManager
	 * @return
	 */
	public boolean isAttached();
		
	/**
	 * This returns true if the device is actually running.  
	 * Most like it would have thrown an exception while it was starting.
	 * but in some case it might not.  So it is useful to check this after
	 * calling start() to make sure it really started.
	 * 
	 * @return
	 */
	public boolean isRunning();
	
	/**
	 * This returns the configuration attached to the interface
	 * right now.  (if it is available).  If canDetectSensors() returns
	 * true then this method should return the most acurrate list of 
	 * sensors.  If canDetectSensors is false then this will probably
	 * return the configuration most recently set on the device with the
	 * configure method.
	 * @return
	 */
	public ExperimentConfig getCurrentConfig();

	/**
	 * 
	 * 
	 * @param request
	 * @param result
	 * @return
	 */
	public ExperimentConfig configure(ExperimentRequest request);
	
	/**
	 * This returns true if this device can detect if sensor are attached.
	 * 
	 * @return
	 */
	public boolean canDetectSensors();
	
	/**
	 * Close the underlying device.  This is generally handled by the
	 * InterfaceManager.
	 *
	 */
	public void close();
}
