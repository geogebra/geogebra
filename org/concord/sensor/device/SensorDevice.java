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
 * $Revision: 1.9 $
 * $Date: 2007-04-23 16:55:44 $
 * $Author: scytacki $
 *
 * Licence Information
 * Copyright 2004 The Concord Consortium 
*/
package org.concord.sensor.device;

import org.concord.sensor.ExperimentConfig;
import org.concord.sensor.ExperimentRequest;


/**
 * SensorDevice
 * Class name and description
 *
 * Date created: Dec 24, 2004
 *
 * @author scott<p>
 *
 */
public interface SensorDevice
{
	/**
	 * Return the name of the vendor of this device.  The goal is to provide a useful
	 * name to the user.
	 * 
	 * This can be called at any time.  If the implementation can handle
	 * multiple vendors, then this should return the most specific name at the 
	 * time it is called.
	 * 
	 * The open call might narrow this down based on the config string
	 * If the device is DeviceIdAware then the passed in id might narrow it down.
	 * If the physical device has been polled that might narrow it down. 
	 * @return
	 */
	public String getVendorName();
	
	/**
	 * Return the name of this device.  The goal is to provide a userful name
	 * to the user.
	 * 
	 * This can be called at any time.  If the implementation can handle
	 * multiple devices, then this should return the most specific name at the 
	 * time it is called. 
	 * 
	 * The open call might narrow this down based on the config string
	 * If the device is DeviceIdAware then the passed in id might narrow it down.
	 * If the physical device has been polled that might narrow it down. 
	 * @return
	 */
	public String getDeviceName();

	
	/**
	 * This is the first method called in the life cycle of this object.
	 * The passed in string can contain whatever configuration information
	 * this device needs.  For a serial port device it probably needs to 
	 * contain the port name.  For a usb device this might not be necessary.
     * 
     * For the serial devices a speical string _auto_ is supported.  This will
     * cycle through the avaiable serial ports and try each one.  This testing
     * might not happen until isAttached is called.
     * 
	 * @param openString
	 */
	public void open(String openString);
	
	/**
	 * This is the last method called in the life cycle of this object.  
	 * The object should release any resources it is holding.  It should
	 * close serial ports, usb ports, or expansion card handles.   
	 * The object will not be used again after this is called,  A new 
	 * object will be created instead.
	 */
	public void close();
	
	/**
	 * This method is called after open but before start.  The request is 
	 * a general sensor configuration request.  The device should try to
	 * meet this request.  If the device cannot id sensors then it should just
	 * assume the appropriate sensors are attached based on the request.  
	 * 
	 * If the device can id sensors then it should verify the appropriate
	 * sensors are attached.  If not then it should return a ExperimentConfig
	 * with an isValid that returns false.  The sensors should be set to the 
	 * actual sensors even if they are wrong.  If the device returns an invalid
	 * config with sensors that don't match then a message will be shown to the
	 * user asking them to plug in the appropriate sensors.  The getInvalidReason
	 * might be used in this case.  If getSensorConfig() returns null then 
	 * getInvalidReason will be displayed to the user.
	 * 
	 * This method might be called more than once, especially if it reports
	 * the wrong sensors are attached.
	 * @param request
	 * @return
	 */
	public ExperimentConfig configure(ExperimentRequest request);
	
	/**
	 * This method is called after configure returns a valid ExperimentConfig
	 * If the external device has its own timer and needs to be started this
	 * is the method where that should be done.
	 * 
	 * After stop has been called this method might be called again.
	 * @return
	 */
	public boolean start();

	/**
	 * After the device is started this method is called again and again
	 * it should be called within the value returned by getDataReadPeriod()
	 * If it takes longer than getDataReadPeriod() to execute then it will
	 * be called again as soon as possible.
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
	 * If the numberOfSamples property in the Experiment request has already
	 * been reached then this should return -2.    
	 * 
	 * @param values
	 * @param offset
	 * @param nextSampleOffset
	 * @param reader
	 * @return
	 */
	public int read(float [] values, int offset, int nextSampleOffset,
			DeviceReader reader);

	/**
	 * This method is called after start.  It might be called more than once.
	 * the wasRunning param should be mostly true.  The timer will be stoped
	 * before this method is called, the read method will not be called after
	 * or while this method is executing.
	 *   
	 * If for some reason the timer was stopped long before this method is called
	 * then wasRunning will be false.
	 * @param wasRunning
	 */
	public void stop(boolean wasRunning);

	/**
	 * Called after an error in start or read.
	 * @param error
	 * @return
	 */
	public String getErrorMessage(int error);
	
	/**
	 * Is the device attached?
	 * @return
	 */
	public boolean isAttached();
	
	/**
	 * Can the device determine if sensors are attached.  Even if the device
	 * can only tell some sensor is attached, but it doesn't know which sensor
	 * it should still return true here.
	 * @return
	 */
	public boolean canDetectSensors();
	
	/**
	 * This will be called to figure out the setup of the current device
	 * If the device cannot detect sensors then it should just fill
	 * out the ExperimentConfig with the device name and null for the 
	 * sensorConfigs.  Waba cannot handle zero length arrays this why the 
	 * sensorConfigs needs to be null.
	 * 
	 * @return
	 */
	public ExperimentConfig getCurrentConfig();
}
