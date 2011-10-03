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
 * Created on Dec 13, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.concord.sensor;

import org.concord.framework.data.DataDimension;

/**
 * This is sent to a SensorDataProducer and then to a SensorDevice
 * to request a particular sensor.  The ExperimentRequest contains 
 * a collection of these SensorRequests.
 * 
 * @author Scott Cytacki
 *
 * 
 */
public interface SensorRequest 
{
	/**
	 * This is the type of quantity.  The types are defined in the 
	 * @see org.concord.sensor.SensorConfig.
	 * @return type of quantity
	 */
	public int getType();
	
	/**
	 * This is the maximum step size between values.  This
	 * is dependent on the units returned by this sensor.  There
	 * will be implicit units for each quantity, and this step
	 * size will be in those units.  The implict unit will also
	 * be available by getUnit(), however that can be ignored because
	 * the units will always be the same for each quantity.  These
	 * implicity units can be found here 
	 * FIXME add url to implicit unit mapping.
	 * 
	 * @return
	 */
	public float getStepSize();
	
	/**
	 * This is the required maxium for this experiment.
	 * the Units are the standard units for this type
	 * of sensor.
	 * This should be used to determine if the correct
	 * sensor is attached.  Or if the device cannot auto
	 * id its sensors, then this should be used to deduce
	 * which sensor is attached.
	 * 
	 * If this returns Float.NaN then there is no required
	 * maximum.
	 * 
	 * @return
	 */
	public float getRequiredMax();

	/**
	 * This is the required minimum for this experiment.
	 * the Units are the standard units for this type
	 * of sensor.
	 * This should be used to determine if the correct
	 * sensor is attached.  Or if the device cannot auto
	 * id its sensors, then this should be used to deduce
	 * which sensor is attached.
     *
	 * If this returns Float.NaN then there is no required
	 * minimum.
	 * 
	 * @return
	 */
	public float getRequiredMin();
	
	/**
	 * This should be ignored by sensor devices.  This value is only used
	 * by display wigets.  
	 * 
	 * This is used by the author to set the precision as a power of
	 * 10 that they wish to be displayed in the graph, table, or other
	 * display of this data.  For example:
	 * setting this to -1 will give a 0.1 precision
	 * setting this to 0 will give integer precision.
	 * 
	 * @return display precision as a power of 10
	 */
	public int getDisplayPrecision();
	
	/**
	 * This is the port the sensor is or should be plugged into.
	 * This value ranges from 0 on up.  This value might be ignored
	 * if the ports can figure out which sensor is attached.  
	 * 
	 * Also there could be more than one "sensor config" for a single
	 * port.  If the author wants distance and velocity from the same
	 * sensor.
	 * 
	 * The ports in a experiment should be continuous starting at 0. 
	 * The SensorDevice implementation should assign these ports to the 
	 * first available physical port that can handle this type of of
	 * sensor. 
	 *        
	 * @return
	 */
	public int getPort();
	
	/**
	 * The unit of the requested sensor.
	 * 
	 * This will be set to the implicit unit of this type
	 * of sensor.  Most implementations will probably ignore this
	 * and just hard code the implicit units in the implementation.
	 * 
	 * @return
	 */
	public DataDimension getUnit();
		
	/**
	 * These parameters can be used to customize a sensor.  If a parameter
	 * is device specific then the key should start with a device specific
	 * id.  
	 * @param key
	 * @return
	 */
	public String getSensorParam(String key);
	
	/**
	 * This is the list of params that have been set.  This is 
	 * necessary in order to serialize instances of this interface
	 * 
	 * @return
	 */
	public String [] getSensorParamKeys();
}
