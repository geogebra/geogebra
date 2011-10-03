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
 * $Revision: 1.12 $
 * $Date: 2007-04-23 16:55:44 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.sensor;


/**
 * ExperimentConfig
 * 
 * This interface is returned by a SensorDevice.  It provides the current
 * configuration of the device and the sensors.
 *
 * Date created: Nov 30, 2004
 *
 * @author scott<p>
 *
 */
public interface ExperimentConfig
{
	/**
	 * If the set of sensors attached does not match the requested sensors then
	 * this should be false.
	 * If at least one the requested sensors does not have an auto id,
	 * then this should be true.  In this case isConfirmed should be
	 * false for that sensor.
	 * If the device cannot auto id sensors then this should always be
	 * true.    
	 * @return
	 */
	public boolean isValid();
	
	/**
	 * If isValid is false then this should return a String that can be presented
	 * to the user.  This should be the reason the request can not be handled.
	 * The user will be given an option to try again, in which case the request
	 * will be sent again.  So the message might take this into account.
	 * @return
	 */
	public String getInvalidReason();
	
	/**
	 * The time in seconds between the returned samples.  If this isn't exact
	 * because of how the device is implemented this should return the approximate
	 * time.
	 * @return
	 */
	public float getPeriod();
	
	/**
	 * If this returns true then the period is an exact
	 * period.  If it is false then it is an approximate
	 * period and the real time will be in the time channel
	 * returned by the SensorDataProducer. 
	 * 
	 */
	public boolean getExactPeriod();
	
	/**
	 * This is the time between read calls that the device
	 * prefers.  The units are seconds per read.  It can't be
	 * guarunteed but the caller will do its best to call 
	 * read at these times. Implementors should return 
	 * smallest reasonable value.  Most likely the data will be displayed
	 * in realtime, so a faster response time is better.
	 * 
	 * @return
	 */
	public float getDataReadPeriod();
		
	/**
	 * An array of SensorConfig, each SensorConfig contains configuration
	 * information about the sensor.  This will return null if there are no
	 * sensor configs.  Zero length arrays are not handled by waba which is 
	 * one target for this code.
	 */
	public SensorConfig [] getSensorConfigs();	
	
	/**
	 * The name of the device that is handling this experiment.
	 * It could be a collection of devices.  For example the Venier
	 * GoLinks could be working together to do an experiment.  In this
	 * case the name should reflect that it is a collection of devices.
	 * The name might be presented to the user show it should be human 
	 * readable.
	 * @param name
	 */
	public String getDeviceName();
}

