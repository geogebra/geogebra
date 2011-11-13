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

package org.concord.sensor.impl;

import java.util.logging.Logger;

import org.concord.framework.data.stream.DataStreamDescription;
import org.concord.framework.data.stream.DataStreamEvent;
import org.concord.framework.data.stream.DefaultDataProducer;
import org.concord.framework.text.UserMessageHandler;
import org.concord.framework.text.UserMessageHandlerExt1;
import org.concord.sensor.ExperimentConfig;
import org.concord.sensor.ExperimentRequest;
import org.concord.sensor.SensorConfig;
import org.concord.sensor.SensorDataProducer;
import org.concord.sensor.SensorRequest;
import org.concord.sensor.device.DeviceReader;
import org.concord.sensor.device.SensorDevice;
import org.concord.sensor.device.impl.SensorConfigImpl;


public class SensorDataProducerImpl extends DefaultDataProducer
	implements SensorDataProducer, DeviceReader, TickListener
{
	private static final Logger logger = Logger
			.getLogger(SensorDataProducerImpl.class.getCanonicalName());
	
	public int		startTimer =  0;
	protected Ticker ticker = null;
	protected UserMessageHandler messageHandler;
	
	protected float [] processedData;
	private static final int DEFAULT_BUFFERED_SAMPLE_NUM = 1000;
	
	int timeWithoutData = 0;
	protected String [] okOptions;
	protected String [] continueOptions;	
	public final static int DATA_TIME_OUT = 20;
	private boolean inDeviceRead;
	private int totalDataRead;
	private SensorDevice device;
	private ExperimentConfig experimentConfig = null;
    protected float dataTimeOffset;
    
	
	public SensorDataProducerImpl(SensorDevice device, Ticker t, UserMessageHandler h)
	{
		this.device = device;
		continueOptions = new String [] {"Continue"};
		okOptions = new String [] {"Ok"};
		
		ticker = t;
		
		
		messageHandler = h;
		
		processedData = new float[DEFAULT_BUFFERED_SAMPLE_NUM];
		dataEvent.setData(processedData);
		dataTimeOffset = 0;
	}

	public void tick()
	{
	    int ret;

	    /*
		if(messageHandler != null) messageHandler.showOptionMessage(null, "Message test",			
				continueOptions, continueOptions[0]);
	    */
	    
	    // reset the total data read so we can track data coming from
	    // flushes
	    totalDataRead = 0;

		dataDesc.setDataOffset(0);

	    // track when we are in the device read so if flush
	    // is called outside of this we can complain
	    inDeviceRead = true;
	    ret = device.read(processedData, 0, 
	    			dataDesc.getChannelsPerSample(), this);
	    inDeviceRead = false;
	    
	    if(ret < 0) {
			stop();
			String devError = device.getErrorMessage(ret);
			if(devError == null) {
				devError = "unknown";
			}
			String message = "Data Read Error: " + devError;
			if(messageHandler != null) {
				messageHandler.showOptionMessage(message, "Interface Error",
						continueOptions, continueOptions[0]);
			}
			return;
	    }
	    
	    totalDataRead += ret;
	    if(totalDataRead == 0) {
			// we didn't get any data. 
	    	// keep track of this so we can report there is
	    	// is a problem.  If this persists too long
			timeWithoutData++;
			if(timeWithoutData > DATA_TIME_OUT){
				stop();
				if(messageHandler != null) {
					messageHandler.showOptionMessage("Data Read Error: " +
							 "possibly no interface " +
							 "connected", "Interface Error",
							 continueOptions, continueOptions[0]);					
				}
			}
			return;
	    }
	    
	    // We either got data or there was an error
		timeWithoutData = 0;

		if(ret > 0){
			// There was some data that didn't get flushed during the read
			// so send this out to our listeners.
			dataEvent.setNumSamples(ret);
			notifyDataReceived();
		} 	
	}
	
	public void tickStopped()
	{
	    deviceStop(false);
	}
	
	/*
	 * This is a helper method for slow devices.  It be called within deviceRead.
	 * If the data should be written into the values array passed to deviceRead
	 * the values read from the offset passed in until offset+numSamples will 
	 * be attempted to be flushed.
	 * the method returns the new offset into the data. 
	 * 
	 * You don't need to call this, but if your device is going to work on a slow
	 * computer (for example an older palm) then you will probably have to use
	 * this method.  Otherwise you will build up too much data to be processed later
	 * and then while all that data is being processed the serial buffer will overflow.
	 * 
	 * Instead this method will partially process the data.  This will give the device
	 * a better chance to "get ahead" of the serial buffer.  Once the device has gotten
	 * far enough ahead of the serial buffer it can return from deviceRead the
	 * data will be fully processed.
	 */
	public int flushData(int numSamples)
	{
		if(!inDeviceRead) {
			// flush should only be called inside of a device read

			// error we need an assert here but we are in waba land 
			// so no exceptions or asserts instead we force
			// a null pointer exception.  Superwaba supports
			// some exceptions now so we should refactor this 
			// to use exceptions
			Object test = null;
			test.equals(test);
		}
		
		dataEvent.setNumSamples(numSamples);
		notifyDataReceived();
		dataDesc.setDataOffset(dataDesc.getDataOffset()+numSamples);
		
		totalDataRead += numSamples;
		
		return 0;
	}
	
	protected int getBufferedSampleNum()
	{
		return DEFAULT_BUFFERED_SAMPLE_NUM;
	}
	
	/**
	 * This method is called by users of the sensor
	 * device.  After the producer is created this method
	 * is called.  In some cases it is called before every
	 * start().
	 * 
	 * It might take a while to return.  It might also fail
	 * in which case it will return null, or it will return
	 * a config for which getValid() return false.
	 */
	public ExperimentConfig configure(ExperimentRequest request)
	{
	    if(ticker.isTicking()) {
	        ticker.stopTicking(this);
	    }
	    
		ExperimentConfig actualConfig = device.configure(request);
		
		// prompt the user because the attached sensors do not
		// match the requested sensors.
		// It is in this case that we need more error information
		// from the device.  I suppose one solution is to get a 
		// listing of the actual sensors and then do the comparison
		// here in a general way.
		// That will work if the interface can auto identify sensors
		// if it can't then how would it know they are incorrect???
		// I guess in case it would have to check if the returned values
		// are valid.  Otherwise it will just have to trust the student and
		// the experiments will have to be designed (technical hints) to help
		// the student figure out what is wrong.
		// So we will try to tackle the general error cases here :S
		// But there is now a way for the device to explain why the configuration
		// is invalid.
		if(actualConfig == null) {
			// we don't have any config.  this should mean there was
			// a more serious error talking to the device.  Either it
			// isn't there, our communication channel is messed up, or
			// the device is messed up.
			if(messageHandler != null) {

				// get the error message from the device
				String devErrStr = device.getErrorMessage(0);

				String title = "Sensor Device Error";
				String body = "Error getting sensors description from " + getFullDeviceName();
				if(devErrStr != null) {
					body += "\n" + devErrStr;
				}

				if(messageHandler instanceof UserMessageHandlerExt1){
					String detail = "DeviceClassName: " + device.getClass().getCanonicalName() + "\n";
					detail += SensorUtilJava.experimentRequestToString(request); 

					((UserMessageHandlerExt1)messageHandler).showMessage(
							body, 
							title,
							detail);
				} else {
					messageHandler.showMessage(body, title);						
				}
			}
		} else if (!actualConfig.isValid()){
			// we have a valid config so that should mean the device
			// can detect the sensors, but the ones it found didn't 
			// match the request so it set the config to invalid
			if(messageHandler != null) {
				sendWrongSensorAttachedMessage(request, actualConfig);
			}				

		}
						
		// Maybe should be a policy decision somewhere
		// because maybe you would want to just return the
		// currently attached setup
		
		// It is not clear if the experiment config should be null if the 
		// actualConfig is not valid
	    experimentConfig = actualConfig;
	    
	    if(actualConfig != null && !actualConfig.isValid()){
	    	actualConfig = null;
	    }
	    
	    if(actualConfig != null &&  
	    		(actualConfig.getSensorConfigs() == null ||
	    				actualConfig.getSensorConfigs().length != 
	    					request.getSensorRequests().length)){
	    	logger.warning("Device returned a 'valid' config but it has a different number of sensors");
	    	actualConfig = null;
	    }
	    
		DataStreamDescUtil.setupDescription(dataDesc, request, actualConfig);

		notifyDataStreamEvent(DataStreamEvent.DATA_DESC_CHANGED);
		
		return actualConfig;
	}

	protected String getFullDeviceName() {
		return device.getVendorName() + " " + device.getDeviceName();
	}
	
	/**
	 * At this point the SensorDevice has rejected the request and returned
	 * a config which is marked invalid.
	 * This method assumes userMessageHandler is not null
	 * 
	 * @param request
	 * @param actualConfig
	 * @return
	 */
	protected void sendWrongSensorAttachedMessage(
			ExperimentRequest request, ExperimentConfig actualConfig)
	{
		// So far requests are only rejected if the sensorRequests don't match the 
		// attached sensors.  Any properties on ExperimentRequest itself don't cause
		// rejection.
		
		SensorRequest[] sensorRequests = request.getSensorRequests();
		SensorConfig[] sensorConfigs = actualConfig.getSensorConfigs();

		if(!device.canDetectSensors()){
			// this device can't detect sensors so it is an error if we get to this point
			messageHandler.showMessage(
					"Device Error: The " + getFullDeviceName() + 
					" can't identify sensors and rejected the requested sensors", 
					"Device Error");
			return;
		}
		
		if(sensorRequests == null || sensorRequests.length == 0){
			// the request is invalid
			String body = "Configuration Error: No sensors types were specified.";
			String deviceError = device.getErrorMessage(0); 
			if(deviceError != null){
				body += "\n" + deviceError; 
			}
			String invalidReason = experimentConfig.getInvalidReason();
			if(invalidReason != null){
				body += "\n" + invalidReason;
			}
			
			messageHandler.showMessage(body, "Configuration Error");
			return;
		}
		
		if(sensorConfigs == null || sensorConfigs.length == 0) {
			// the device can auto detect sensors, but it returned an empty sensorConfigs.
			// this could be that the attached sensor doesn't auto id, or that no sensor
			// is attached.  If we are here then it means the device rejected the request
			// so even if the attached sensors don't auto id, then it decided the request 
			// wouldn't match 
			String body = "The " + getFullDeviceName() + " reported no valid sensors are attached\n";
			body += "Check if the sensors attached correctly";
			String title = "No sensors attached";
			if (messageHandler instanceof UserMessageHandlerExt1){
				((UserMessageHandlerExt1)messageHandler).showMessage(body, title, 
						SensorUtilJava.experimentRequestToString(request));
			} else {
				messageHandler.showMessage(body, title);
			}
			return;
		}
		
		// handle the simple case
		if(sensorConfigs.length == 1 && sensorRequests.length == 1){
			SensorConfig sensorConfig = sensorConfigs[0];
			SensorRequest sensorRequest = sensorRequests[0];
			float sensorTypeScore = SensorUtilJava.scoreSensorType(sensorConfig, sensorRequest);
			float valueRangeScore = SensorUtilJava.scoreValueRange(sensorConfig, sensorRequest);
			float stepSizeScore = SensorUtilJava.scoreStepSize(sensorConfig, sensorRequest);
			String title = null;
			String body = null;
			if(sensorTypeScore < 0.1f){
				// the request and config don't have a compatible type
				title = "Wrong Sensor Attached";
				
				// FIXME there should be a better string representation of the sensor names.
				body = "This data collection requires a " + 
					SensorUtilJava.getTypeConstantName(sensorRequest.getType()) + " sensor.\n";
				body += "However a " + sensorConfig.getName() + " is attached";				
			} else if(valueRangeScore < 0.6f) {
				// Some sensor devices will reject a request if the value range doesn't match
				// This is because not all devices use general scoring system
				// So far the only part of the range that really matters is the maximum.
				title = "Wrong Sensor Range";
				body = "This data collection requires a sensor that can measure up to: " + sensorRequest.getRequiredMax() + "\n"; 
				if(sensorConfig instanceof SensorConfigImpl){
					body += " However the attached sensor can only measure up to: " + 
					   ((SensorConfigImpl)sensorConfig).getValueRange().maximum + "\n";
				}
				body += "\n";
				body += "If the sensor has ranges switch, try another setting.";
			} else if(stepSizeScore < 0.1f){
				title = "Wrong Sensor Accuracy";
				body = "This data collection requires a more accurate sensor.\n";
				body += "\n";
				body += "If the sensor has a range switch, try another setting.";
			} else {
				title = "Wrong Sensor Attached";
				body = "The attached sensor cannot be used by this data collection.";				
			}
			
			if(messageHandler instanceof UserMessageHandlerExt1){
				String details = SensorUtilJava.experimentRequestToString(request);
				details += "\n\n";
				details += SensorUtilJava.experimentConfigToString(actualConfig);
				((UserMessageHandlerExt1)messageHandler).showMessage(body, title, details);
			} else {
				messageHandler.showMessage(body, title);
			}
			return;
		}

		String title = "Wrong Sensor(s) Attached";
		String body = "The data collection requires a different set of sensors.\n";
		body += " see the details for more information";
		if(messageHandler instanceof UserMessageHandlerExt1){
			String details = SensorUtilJava.experimentRequestToString(request);
			details += "\n\n";
			details += SensorUtilJava.experimentConfigToString(actualConfig);
			((UserMessageHandlerExt1)messageHandler).showMessage(body, title, details);
		} else {
			messageHandler.showMessage(body, title);
		}
		
	}
	
	public final void start()
	{
	    if(ticker == null) {
	        throw new RuntimeException("Null ticker object in start");
	    }
	    
	    if(ticker.isTicking()) {
	        // this is an error some other object is using
	        // this ticker, or we are trying to start it twice
	        throw new RuntimeException("Trying to start device twice");
	    }
	    
	    if(device == null) {
	        throw new RuntimeException("Null device in start");
	    }
	    
		if(!device.start()) {
			// cannot start device
			if(messageHandler != null) {
				String devMessage = device.getErrorMessage(0);
				if(devMessage == null) {
					devMessage = "unknown";
				}
				
				messageHandler.showMessage("Can't start device: " + devMessage,
						"Device Start Error");
			}
			return;
		}
		
		timeWithoutData = 0;

		startTimer = ticker.currentTimeMillis();
		int dataReadMillis = (int)(experimentConfig.getDataReadPeriod()*1000.0);
		// Check if the data read millis is way below the experiment period
		// if it is then tick code will time out incorrectly.  So 
		// we try to correct it so that the read time is no less than
		// 1/5th of the period.
		int autoDataReadMillis = (int)(experimentConfig.getPeriod()*1000/5);
		if(dataReadMillis < autoDataReadMillis){
		    dataReadMillis = autoDataReadMillis;
		}
		ticker.startTicking(dataReadMillis, this);
		super.start();

	}
	
	/**
	 *  This doesn't really need to do anything if
	 * the sensor isn't storing any cache.
	 * however for sensors that need to put timestamps
	 * on the data this method should be used to 
	 * reset the timestamp
	 */
	public final void reset()
	{	
		stop();
	    dataTimeOffset = 0;
	    super.reset();
	}
	
	public final void stop()
	{
		boolean ticking = ticker.isTicking();

		// just to make sure
		// even if we are not ticking just incase
		ticker.stopTicking(this);

		deviceStop(ticking);
		super.stop();
	}

	
	protected void deviceStop(boolean ticking)
	{
		device.stop(ticking);

		// FIXME we should get the time the device sends back
		// instead of using our own time.
		dataTimeOffset += (ticker.currentTimeMillis() - startTimer) / 1000f;	    
	}
	
	/* (non-Javadoc)
	 * @see org.concord.sensor.SensorDataProducer#isAttached()
	 */
	public boolean isAttached()
	{
	    if(ticker != null && ticker.isTicking()) {
	        // this will have the ticker send a tickStopped event 
	        // which should cause us to stop the device
	        ticker.stopTicking(null);
	    }
	    
		return device.isAttached();
	}
	
	@Override
	public boolean isRunning()
	{
		if(ticker == null) {
			return false;
		}
		
		return ticker.isTicking();
	}
	
	@Override
	public boolean isInInitialState() {
		if(dataTimeOffset == 0){
			return true;
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.concord.sensor.SensorDataProducer#canDetectSensors()
	 */
	public boolean canDetectSensors()
	{
		// TODO Auto-generated method stub
		return device.canDetectSensors();
	}
	
	public ExperimentConfig getCurrentConfig()
	{
		return device.getCurrentConfig();
	}
	
	public void close()
	{
		// make sure we are stopped.
		stop();
		
		device.close();
	}
	
	@Override
	protected void notifyDataReceived() {
		// if the data has timestamps they should be adjusted
		// the contract for sensor devices is that time starts 
		// at 0 when start is called, however for data producers
		// time starts at 0 when reset is called.  The stop method
		// is more like a pause for data producers.
		if(dataDesc.getDataType() == DataStreamDescription.DATA_SERIES){
		    // the first channel will be time.
		    for(int i=dataDesc.getDataOffset(); 
		    	i < dataEvent.getNumSamples(); 
		    	i+= dataDesc.getNextSampleOffset()){
		        processedData[i] += dataTimeOffset;
		    }
		}

		super.notifyDataReceived();
	}
}
