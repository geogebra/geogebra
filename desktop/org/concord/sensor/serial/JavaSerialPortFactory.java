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

package org.concord.sensor.serial;



public class JavaSerialPortFactory
	implements SerialPortFactory
{

	public SensorSerialPort getSerialPort(String name, SensorSerialPort oldPort)
	{
		String portClassName = null;
		
		if(name.equals("ftdi")) {
			portClassName = "org.concord.sensor.dataharvest.SensorSerialPortFTDI";
		} else if(name.equals("os")){			
			portClassName = "org.concord.sensor.serial.SensorSerialPortRXTX";
		}
		
		try {		    
			Class portClass = getClass().getClassLoader().loadClass(portClassName);
			
			if(!portClass.isInstance(oldPort)){
				return(SensorSerialPort) portClass.newInstance();
			} else {
				return oldPort;
			}
		} catch (Exception e) {
			System.err.println("Can't load serial port driver class: " +
					portClassName);
		}
		
		return null;
	}

	public int getOSType()
	{
		if(System.getProperty("os.name").startsWith("Windows")) {
			return WINDOWS;
		}
			
		return UNKNOWN;		
	}
}
