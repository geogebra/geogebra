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
import org.concord.sensor.ExperimentConfig;
import org.concord.sensor.ExperimentRequest;

public interface SensorLoggedRecord
{
    /**
     * A short description describing this record in the device
     * @return
     */
    public String getDescription();
    
    /**
     * The configuration for this particular record.  
     * 
     * Some devices don't know what sensor configuration was actually 
     * logged.  In those cases this will still return a config but
     * the sensors config will be empty.
     * 
     * @return
     */
    public ExperimentConfig getLoggedConfig();

    /**
     * Return the number samples that were logged in this record.
     * @return
     */
    public int getNumSamples();
    
    /**
     * Get the start time of this record.  Then each sample is
     * offset by the period returned by the config.
     * 
     * @return
     */
    public DeviceTime getStartTime();

    /**
     * This method needs to be called before the read is called.
     * If the device uses the attached sensors to determine the callibration
     * of the logged data then the attached sensors will be queried here
     * 
     * In that case the ExperimentConfig returned by 
     * getLoggedConfig will not have sensors
     * until after this method is called.
     * 
     * The request can be null.  In some cases the request is required
     * because the device cannot auto detect sensors.  So the types of sensors
     * in the request will be used to determine the callibrations of the data
     * stored in the log.
     * 
     * If the device can detect sensors and or the type of sensor is actually
     * saved in the log, then this method will validate that request with the 
     * information from the device.  If the device has stored data for more
     * sensors then are requested, then only the requested sensor data will be
     * read. 
     * 
     * @return
     */
    public ExperimentConfig initializeRead(ExperimentRequest request);
    
    /**
     * This method is called again and again.  It will be called approximately
     * around the value getDataReadPeriod on the experimentconfig.
     * 
     * It should not take too long (~150ms) to return.  
     * If it is going to take a while to read the data out the device, 
     * then should just read a small amount at a time.
     * 
     * The startSample value will always be incremental.  So as the method
     * is called again and again it will be incremented by the return value 
     * of this method.  So it can be ignored if the implementor keeps track
     * of this value itself.
     * 
     * It should place values read from the device in the values buffer, starting
     * at the offset.  The data should be in "samples" each sample might consist
     * of multiple values.  For example if a temperature and pressure sensor is
     * attached then each sample should have 2 values (t,p).
     * The number and order of the values in each sample should match the
     * SensorConfigs in the ExperimentConfig returned by configure. 
     * 
     * If the sensor device returned false for getExactPeriod then
     * an addition value should be returned before the others for each
     * sample.  This value is the time the sample was taken.  It should
     * be in seconds since the call to start().
     * 
     * After writing a sample to the values buffer the method should advance 
     * by nextSampleOffset.   This is sometimes referred to as a "stride".
     * So if the nextSampleOffset == 5 and there are 2 values in each sample
     * then the method should skip 3 values after each sample. 
     * 
     * It should return the number of samples read.  0 means no samples were
     * read.  -1 means there was an error. getErrorMessage() will be called 
     * to find out what the error was.
     * 
     * If the getNumberSamples has already
     * been reached then this should return -2.    
     * 
     * @param values
     * @param offset
     * @param nextSampleOffset
     * @param reader
     * @return
     */
    public int read(int startSample, float [] values, int offset, int nextSampleOffset);

}
