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
 * $Revision: 1.2 $
 * $Date: 2006-05-05 15:46:09 $
 * $Author: maven $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.sensor.device;

import org.concord.sensor.DeviceTime;
import org.concord.sensor.LoggingRequest;

public interface SensorLogger
    extends SensorDevice
{
    /**
     * Get the records avaiable on this device
     * @return
     */
    public SensorLoggedRecord [] getAvailableRecords();
        
    /**
     * Send a request to setup a log to this device.
     * @param request
     */
    public void sendLoggingRequest(LoggingRequest request);
        
    /**
     * This reads the current time from the device.  This is important for logging
     * because triggered and delayed start logs rely on the clock of the device.
     * 
     * If the device doesn't support this feature it should return null here.
     * @return
     */
    public DeviceTime getLoggerCurrentTime();
    
    /**
     * This sets the current time in the device.
     * @param time
     */
    public void setLoggerCurrentTime(DeviceTime time);
}
