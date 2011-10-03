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
 * Created on Feb 15, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.concord.sensor.device.impl;

import java.util.ArrayList;

import org.concord.sensor.ExperimentConfig;
import org.concord.sensor.ExperimentRequest;
import org.concord.sensor.SensorConfig;
import org.concord.sensor.SensorRequest;
import org.concord.sensor.device.DeviceService;
import org.concord.sensor.device.DeviceServiceAware;
import org.concord.sensor.device.SensorDevice;
import org.concord.sensor.impl.ExperimentConfigImpl;
import org.concord.sensor.impl.Range;
import org.concord.sensor.impl.SensorUtilJava;
import org.concord.sensor.impl.Vector;
import org.concord.sensor.serial.SensorSerialPort;
import org.concord.sensor.serial.SerialException;

/**
 * @author scott
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class AbstractSensorDevice 
	implements SensorDevice, DeviceServiceAware
{
	public final static int ERROR_NONE			= 0;
	public final static int ERROR_GENERAL			= -1;
	public final static int ERROR_PORT				= -2;
	public final static int ERROR_DEV_VERSION		= -3;	

	// Default is SD for sensor device;
	protected String deviceLabel = "SD";
	
	protected ExperimentConfigImpl currentConfig = null;
    protected boolean attached = false;

    protected String portName;
	protected SensorSerialPort port = null;
	
	protected int error = 0;
	protected int portError = 0;


    protected DeviceService devService;
    
    public final static boolean isRawType(int type)    
    {    	
    	return type == SensorConfig.QUANTITY_RAW_VOLTAGE_1
		        || type == SensorConfig.QUANTITY_RAW_VOLTAGE_2
		        || type == SensorConfig.QUANTITY_RAW_DATA_1
		        || type == SensorConfig.QUANTITY_RAW_DATA_2;
    }
    
	/* (non-Javadoc)
	 * @see org.concord.sensor.device.SensorDevice#isAttached()
	 */
	public boolean isAttached()
	{
		if(port == null || !port.isOpen()){
			return false;
		}
		return isAttachedInternal(portName);
	}
	
    public void setDeviceService(DeviceService provider)
    {
        this.devService = provider;
    }
 
	public void open(String openString)
	{
        portName = openString;
		if(!openPort()){
		    return;
		}			
	}

	public void close()
	{
		System.err.println("Closing device: " + getClass());
		closePort();
		
		// make sure the port is set to null
		port = null;
	}

	protected void closePort()
	{
	    if(port == null || !port.isOpen()) {
	        // port should already be closed
	        return;
	    }
	    
	    log("closing port: " + portName);
	    
	    try {
	    	port.close();
	    } catch (SerialException e){
	    	e.printStackTrace();
	    }
	    
	    // This had to be removed for the SW500 to
	    // work butit seems like a ligitimate thing to do.  
	    // so we should instead fix the SW500 
	    port = null;	    
	}
	
	protected boolean openPort()
	{
		// The port is already open
		if(port != null && port.isOpen()) {
			return true;
		}
		
		// This comes from the airlink implementation these error codes
		// are not used by every device implementation
    	error = ERROR_NONE;

	    // This is for backward compatibility with older LabBooks
	    // which have empty port names.
        if(portName == null || portName.trim().length() == 0) {
            portName = "bluetooth";
        }
        
        if("_auto_".equals(portName)) {
        	return openAutoPort();
        } 
        
        return openPortName(portName);        
	}

	/**
	 * Override this if you device wants to use a different technique for automatically
	 * identify where the device is attached.  One case is if the the software supports
	 * multiple types of "serialPorts" for example the LabPro can work with its one USB
	 * port, or with the rxtx serial port.
	 * 
	 * @return
	 */
	protected boolean openAutoPort()
	{
    	// In another version of the code we called closePort
    	// here.  It doesn't seem like that should be necessary
    
    	if(port == null) {
	    	port = getSensorSerialPort();
	    }

        Vector availablePorts = getAvailablePortNames();
        for(int i=0; i<availablePorts.size(); i++) {  
            String possiblePort = (String)availablePorts.get(i);
            
            // Try opening the port
            if(openPortName(possiblePort)){
            	// replace _auto_ with the opened port name
                portName = possiblePort;
                return true;
            }
        }
        
        return false;
	}
	
	protected boolean openPortName(String possiblePort)
	{
    	log("looking for device on port: " + possiblePort);
        if(!attemptToOpenPort(possiblePort)){
            // we couldn't even open this port
        	log("could not open port: " + possiblePort);
        	// close the port just to be safe
        	closePort();
            return false;
        }
        
        // we could at least open the port but we don't know if
        // the device is attached.
        if(!isAttachedInternal(possiblePort)) {
            // we opened the port but nothing was there
        	log("could not find device on port: " + possiblePort);
        	closePort();
            return false;
        }
        
        // if we got this far then we found a device
        // change the portname.
        // That way we won't check every port each time this
        // method is called.
        // Currently this does mean they 
        // will have to restart the program if the device changes
        // ports for whatever reason
        log("found device on port: " + possiblePort);
        return true;
	}
	
	protected void log(String message)
	{
		devService.log(deviceLabel + ": " + message);
	}
	
	/**
	 * By default this return the operating system serial port
	 * this is a bit confusing because it is really an object
	 * which can open the operating system serial ports not a 
	 * particular serial port.
	 * 
	 * It should be overriden if different type of
	 * SensorSerialPort is needed by the device.
	 * 
	 * @return
	 */
	protected SensorSerialPort getSensorSerialPort()
	{
		return devService.getSerialPort("os", port);
	}
	
	/**
	 * By default this returns the list of strings provided by the port object
	 * returned from getSensorSerialPort.
	 * It can be overriden if the device needs to filter out or add some special ports. 
	 * 
	 * @return
	 */
	protected Vector getAvailablePortNames()
	{
        return port.getAvailablePorts();
	}
	
    protected boolean isAttachedInternal(String portLabel)
    {
		return attached;
    }

    public boolean attemptToOpenPort(String portName)
    {
	    // Make sure the port is closed before opening it
	    closePort();
	    
    	if(port == null) {
	    	port = getSensorSerialPort();
	    }

	    if(port == null) {
            log("Cannot open serial driver");
		    return false;				        
	    }

	    SerialPortParams serialPortParams = getSerialPortParams();
	    
        // set the basic serial params
        try {
        	if(serialPortParams != null) {
        		serialPortParams.setupPort(port);
        	}
            port.open(portName);
            log("opened port: " + portName);
		} catch (SerialException e) {
            portError = e.getPortError();
            port = null;
            error = ERROR_PORT;
            log("Cannot open port " + portName + " err: " + portError);
            log("  msg: " + e.getMessage());
            return false;
		}
	    
        return initializeOpenPort(portName);        
    }
    
	protected void portError(int portError)
	{
		error = ERROR_PORT;
        log("ser port err: " + portError);
        closePort();
	}

	protected void deviceError(int code, String tag)
	{
		log("dev err: " + code + " (" + tag + ")");
		
		// if the port is already null then there will already
		// be an error logged and the error code should not be 
		// changed
		if(port != null) {
			error = code;
		}
		
		closePort();		
	}
   
	/**
	 * This is a utility method to deal with a common problem of matching
	 * up sensor requests with the sensor determined by probing the hardware
	 * return a score 0 to 100
	 * 
	 * @param request
	 * @return
	 */
	protected int compareSensorConfigAndRequest(SensorConfig config, SensorRequest request)
	{
		float score = 100f;
		
		float typeFactor = SensorUtilJava.scoreSensorType(config, request);

		score = score * typeFactor;
		
        float rangeFactor = SensorUtilJava.scoreValueRange(config, request);
        
        score = score * rangeFactor;
        
        float stepSizeFactor = SensorUtilJava.scoreStepSize(config, request);
        
        score = score * stepSizeFactor;
        
        return (int)score;
	}
	
    /**
	 * override this if you need change it
	 * @return
	 */
	protected boolean hasExactPeriod()
	{	
		return true;
	}
	
    /**
     * 
     * An internal method used by the autoIdConfigure.  This method could also 
     * used by logging code.  Given a request it will check the current config
     * and return a config that matches the request.  
     * 
     * If that is not possible then if there was an error, it will return null.  
     * If it is not possible for some other reason like wrong sensors are attached
     * it will return a config marked invalid. 
     * 
     * @param request
     * @param record record is logged record being configured against 0xFF is the 
     *    currently attached sensors
     * @return
     */
	protected void autoIdConfigureInternal(ExperimentConfigImpl expConfig, 
	                                 ExperimentRequest request)
	{
		Range periodRange = expConfig.getPeriodRange();
		if(periodRange != null){
			if(request.getPeriod() > periodRange.maximum) {
				expConfig.setPeriod(periodRange.maximum);
			} else if(request.getPeriod() < periodRange.minimum) {
				expConfig.setPeriod(periodRange.minimum);
			} else {
				expConfig.setPeriod(request.getPeriod());
			}
		} else {
			expConfig.setPeriod(request.getPeriod());			
		}
		expConfig.setDataReadPeriod(expConfig.getPeriod());
		expConfig.setExactPeriod(hasExactPeriod());
	    
		SensorRequest [] sensorRequests = request.getSensorRequests();
		SensorConfig [] sensorConfigs = expConfig.getSensorConfigs();
		ArrayList<SensorConfig> matchingConfigs = new ArrayList<SensorConfig>();
		expConfig.setValid(true);

		// compare these objects to the request.  
		boolean sensorTypesAvailable = true;
		
		// There is one score array for each sensor request
		// the array contains the scores for each sensor config
		ArrayList<int []> scores = new ArrayList<int []>();
		for(int i=0; i<sensorRequests.length; i++) {
			// we need to handle the case where a probe sensor
			// is requested but there is also an internal sensor
			// we could do this with a score or 2 passes
			// also we have the problem in this case that we don't
			// know if the we should accept probes when a non probe
			// is requested.  I think in this device it can't tell
			// if the sensor is attached
			
		    int [] scoreArray = new int [sensorConfigs.length]; 
		    
	        for(int j=0; j<sensorConfigs.length; j++) {
	        	scoreArray[j] = compareSensorConfigAndRequest(sensorConfigs[j], sensorRequests[i]);
	        }
	        scores.add(scoreArray);
		}
		
		for(int i=0; i<sensorRequests.length; i++) {

			// start out with 0 for the high score that way things won't
			// get counted that doen't match anything
		    int highScore = 0;
		    int highScoreIndex = -1;

			int [] scoreArray = scores.get(i);
			for(int j=0; j<scoreArray.length; j++){
				// once a sensor is found then in the future matches should ignore it.
				SensorConfig sensorConfig = sensorConfigs[j];
				if(matchingConfigs.contains(sensorConfig)){
					continue;
				}
				
				int score = scoreArray[j];
				
				if(score > highScore) {
					highScoreIndex = j;
					highScore = score;
				}				
			}
			
			
		    if(highScoreIndex == -1 ) {
		        sensorTypesAvailable = false;
		        break;
		    }
		 
            // devService.log("selected sensor: " + highScoreIndex);
            matchingConfigs.add(sensorConfigs[highScoreIndex]);            
		}
		
		if(!sensorTypesAvailable){
		    expConfig.setValid(false);
		    // leave the detected sensor configs 
		    return;
		}
		
		// assemble the correct objects into a ExperimentConfig
		int numConfigs = matchingConfigs.size();
		SensorConfig [] matchingConfigArray = 
		    new SensorConfig[numConfigs];
		for(int i=0; i<numConfigs; i++) {
		    matchingConfigArray[i] = (SensorConfig)matchingConfigs.get(i);
		}
		
		expConfig.setSensorConfigs(matchingConfigArray);
		expConfig.setValid(true);		
	}
	
	protected ExperimentConfig autoIdConfigure(ExperimentRequest request)
    {		
		if(!openPort()) {
			return null;
		}

		ExperimentConfigImpl deviceConfig = 
			(ExperimentConfigImpl)getCurrentConfig();
		
		if(deviceConfig == null){
			// There was an error possibly the device isn't attached
			return null;
		}
		
		// We need to check if there is a raw voltage or raw data in the request
		// if so then a sensor might be attached that doesn't id itself, so we 
		// should ignore the deviceConfig
		
		// FIXME this is a hack for the Olathe workshop this should be handled
		// more generally.
		SensorRequest[] sensorRequests = request.getSensorRequests();
		if(sensorRequests != null && sensorRequests.length > 0 &&
				(isRawType(sensorRequests[0].getType()))){
			SensorConfig rawSensorConfig = 
				createSensorConfig(sensorRequests[0].getType(), 0);
			SensorConfig [] sensorConfigs = new SensorConfig [] {rawSensorConfig};
			deviceConfig.setSensorConfigs(sensorConfigs);
			deviceConfig.setValid(true);
		}
		
		if(deviceConfig.getSensorConfigs() == null){
			// If some of the sensors don't auto id
			// then it should assume the sensors are configured the way they 
			// are requested.
			// If the device doesn't support non auto id sensors then there is no point 
			// in trying auto configure with no sensors attached.  			
			if(hasNonAutoIdSensors()){
				nonAutoIdConfigureInteral(deviceConfig, request);
			} else {				
				deviceConfig.setValid(false);
			}
		} else if(deviceConfig.getSensorConfigs().length == 0){
			devService.log("warning getCurrentConfig returned 0 length " + 
					"sensorConfigs waba cannot handle that");
			deviceConfig.setValid(false);
		} else {
			autoIdConfigureInternal(deviceConfig, request);			
		}
				
        if(deviceConfig.isValid()){
            currentConfig = deviceConfig;            
        }

		return deviceConfig;
	}


    protected void nonAutoIdConfigureInteral(ExperimentConfigImpl deviceConfig , ExperimentRequest request) 
    {
    	SensorRequest[] sensorRequests = request.getSensorRequests();
    	
    	if(sensorRequests == null || sensorRequests.length == 0){
    		deviceConfig.setSensorConfigs(null);
    		deviceConfig.setValid(false);
    		return;
    	}
    	
    	SensorConfig [] sensorConfigs = new SensorConfig [sensorRequests.length];
    	for(int i=0; i<sensorRequests.length; i++){
    		// I'm not sure if the request port here will be accurate.
    		sensorConfigs[i] = 
    			createSensorConfig(sensorRequests[i].getType(), sensorRequests[i].getPort());    		
    	}
		deviceConfig.setSensorConfigs(sensorConfigs);
		deviceConfig.setValid(true);
		
		deviceConfig.setPeriod(request.getPeriod());			
		deviceConfig.setDataReadPeriod(deviceConfig.getPeriod());
		deviceConfig.setExactPeriod(hasExactPeriod());

	}

	/**
     * This method is used to handle raw voltage and data configs.  If a device
     * can only handle its internal SensorConfig implementations this should be overriden
     * to create on of those.
     *  
     * @param type
     * @return
     */
    protected SensorConfig createSensorConfig(int type, int requestPort)
    {
    	SensorConfigImpl config = new SensorConfigImpl();
    	config.setType(type);
    	config.setPort(requestPort);
    	return config;
    }

    /**
     * Subclasses should override this method if they support sensors that don't provide
     * autoId.  
     * If this returns true, then the autoIdConfigure method check if the getCurrentConfig
     * returns a device config that returns null on getSensorConfigs().  Then it will 
     * assume the device is has the requested sensors attached.  It will call 
     * createSensorConfig with the type and port of the sensor requested.
     * 
     * TODO Currently this won't handled mixed sensors where some are autoid and some are not.
     * 
     * @return
     */
    protected boolean hasNonAutoIdSensors()
    {
    	return false;
    }
    
	/**
     * This method is called after the port has been setup with the 
     * serialPortParams returned by getSerialPortParams and then opened
     * with this portName.  
     * 
     * @param portName
     * @return true if the device is attached to this port false otherwise
     */
    protected abstract boolean initializeOpenPort(String portName);
    
    /**
     * This should return serial port need for this particular device setup
     * 
     * @return
     */
    protected abstract SerialPortParams getSerialPortParams();
}
