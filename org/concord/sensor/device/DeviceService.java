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
 * $Revision: 1.4 $
 * $Date: 2007-06-06 06:54:38 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.sensor.device;

import org.concord.framework.text.UserMessageHandler;
import org.concord.sensor.serial.SensorSerialPort;

public interface DeviceService
{
    public final static int OS_UNKNOWN = 0;
    public final static int OS_LINUX = 1;
    public final static int OS_OSX = 2;
    public final static int OS_WINDOWS = 3;
    public final static int OS_PALMOS = 4;
    public final static int OS_WINCE = 4;  
    
    public final static String OS_SERIAL_PORT = "os";
    public final static String FTDI_SERIAL_PORT = "ftdi";
    public final static String LABPROUSB_SERIAL_PORT = "labprousb";
    
    public int getOSType();

    public void log(String message);

    public UserMessageHandler getMessageHandler();
    
    public void sleep(int millis);
    
    public long currentTimeMillis();
    
    /**
     * This is needed because Waba doesn't have the Float class 
     * 
     * @param valueInt
     * @return
     */
    public float intBitsToFloat(int valueInt);
    
    /**
     * This is need because Waba doesn't have the Float class
     * so this makes it possible to check if a float is valid
     * in a platform independent way
     * 
     * @param value
     * @return
     */
    public boolean isValidFloat(float value);
    
    public SensorSerialPort getSerialPort(String name, SensorSerialPort oldPort);
}
