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

package org.concord.sensor;

/**
 * This is an interface that SensorConfig implementations should implement
 * to indicate they support zeroing in some way.
 *
 * @author scott
 *
 */
public interface ZeroingSensor {
	/**
	 * Check if this sensor supports zeroing.  
	 * 
	 * This duplicates the purpose
	 * of this interface, but it is easier for implementors to provide a single
	 * sensor implementation that determines the zeroing support based on its 
	 * content.
	 */
	public boolean getSupportsZeroing();
	
	
	/**
	 * This will be called while the sensor is running.  It is expected to 
	 * send a message to the device telling it to zero the sensor.  This will 
	 * probably have to be refined based on the requirements of the different
	 * sensors. 
	 * If a sensor supports zeroing with a hardware button only.  Then this
	 * method might want to send a message to the user explaining how to do 
	 * that.
	 *
	 */
	public void zeroSensor();
}
