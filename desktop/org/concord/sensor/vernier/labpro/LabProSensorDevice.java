/**
 * 
 */
package org.concord.sensor.vernier.labpro;

import java.util.StringTokenizer;

import org.concord.sensor.ExperimentConfig;
import org.concord.sensor.ExperimentRequest;
import org.concord.sensor.SensorConfig;
import org.concord.sensor.device.DeviceReader;
import org.concord.sensor.device.DeviceService;
import org.concord.sensor.device.impl.AbstractStreamingSensorDevice;
import org.concord.sensor.device.impl.SensorConfigImpl;
import org.concord.sensor.device.impl.SerialPortParams;
import org.concord.sensor.device.impl.StreamingBuffer;
import org.concord.sensor.impl.ExperimentConfigImpl;
import org.concord.sensor.impl.Vector;
import org.concord.sensor.serial.SensorSerialPort;
import org.concord.sensor.serial.SerialException;
import org.concord.sensor.vernier.VernierSensor;
import org.concord.sensor.vernier.VernierSensorDevice;

/**
 * @author scott
 *
 */
public class LabProSensorDevice extends AbstractStreamingSensorDevice
	implements VernierSensorDevice
{
    final static SerialPortParams serialPortParams = 
    	new SerialPortParams(SensorSerialPort.FLOWCONTROL_NONE,
    			38400, 8, 1, 0);
	
    public final static int [] CHANNELS = {1,2,3,4,11,12};

    public final static String ERR_DEVICE_NOT_ATTACHED = "LabPro is not attached";
    
	protected final byte [] buf = new byte [1024];
	
	protected LabProProtocol protocol;
	
	protected String currentErrorMessage;
	
    public LabProSensorDevice()
    {
    	deviceLabel = "LP";
    	protocol = new LabProProtocol(this);

    	Runtime.getRuntime().addShutdownHook(new Thread(){
			public void run() {
				System.err.println("Closing LabProSensorDevice.");
				
				// Try to make sure the labpro is closed
				// If this not done then it cause a bad crash.
				// The crash happens when the usb is used and the program is closed while
				// the labpro is still open, 
				close();
			}
		});    	
    }
    
	/* (non-Javadoc)
	 * @see org.concord.sensor.device.impl.AbstractSensorDevice#getSerialPortParams()
	 */
	protected SerialPortParams getSerialPortParams()
	{
		return serialPortParams;
	}

	/**
	 * @see org.concord.sensor.device.impl.AbstractSensorDevice#initializeOpenPort(java.lang.String)
	 */
	protected boolean initializeOpenPort(String portName)
	{
		try {
			protocol.wakeUp();
			protocol.reset();
		} catch (SerialException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

	/**
	 * @see org.concord.sensor.device.impl.AbstractSensorDevice#isAttachedInternal(java.lang.String)
	 */
	protected boolean isAttachedInternal(String portLabel)
	{
		try {
			if(port == null || !port.isOpen()){
				return false;
			}
			
			// wakeup again incase this is called directly			
			protocol.wakeUp();
			protocol.requestSystemStatus();
			
			// to work with rxtx and the keyspan USA19QW on osx
			// some pause is needed before reading from the device
			// otherwise it doesn't get any bytes.  
			// this is a hack so we'll take it out, if we really
			// need to do this it should be a change to the rxtx
			// getDeviceService().sleep(5);

			//read result
			float [] values = new float[18];
			int count = readValues(values);
			
			// with rxtx-19QW-osx, sometimes the count returned is 16
			if(count != 17){
				if(count >= 0){
					log("wrong number of values returned for system status ret: " +
							count);
				}
				return false;
			}

			if(round(values[LabProProtocol.SYS_STATUS.CONST_8888]) != 8888){
				log("system status has wrong constent vlaue: " + 
						values[LabProProtocol.SYS_STATUS.CONST_8888]);
				return false;
			}
						
		} catch (SerialException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	/**
	 * This takes away any floating point drift that would mess up a basic
	 * cast to int.  For example a float could be turn out to be 0.99999
	 * and a basic cast to int will return 0 instead of the desired 1.
	 * @param value
	 * @return
	 */
	public final static int round(float value)
	{
		if(value >= 0){
			return (int)(value+0.5f);
		} else {
			return (int)(value-0.5f);
		}
	}
	
    /**
     * @see org.concord.sensor.device.SensorDevice#configure(org.concord.sensor.ExperimentRequest)
     */
    public ExperimentConfig configure(ExperimentRequest request) 
    {
    	return autoIdConfigure(request);
    }


	/**
	 * @see org.concord.sensor.device.SensorDevice#getCurrentConfig()
	 */
	public ExperimentConfig getCurrentConfig()
	{
	    ExperimentConfigImpl expConfig = new ExperimentConfigImpl();
	    expConfig.setDeviceName("LabPro");
	    
		// read the sensor ids of each of the ports to see what is
		// attached.  
	    Vector sensorConfigVect = new Vector();
	    
		try {
			protocol.wakeUp();

			// Turning on the power might be necessary before reading
			// from sensors that require power
			protocol.portPowerControl(-2);

			// send the read command
			float channelStatus [] = new float[3]; 
			
			for(int i=0; i<CHANNELS.length; i++){
				int channelNumber = CHANNELS[i];
				protocol.requestChannelStatus(channelNumber, 0);

				int count = readValues(channelStatus);
				if(count < 3){
					// there was an error
					log("error reading channel status from device chan: " + 
							channelNumber);
					continue;
				} 
				
				int sensorId = round(channelStatus[0]);
				log("chan: " + channelNumber + " sens id: " + sensorId);
				
				// only add the sensor if we can identify that it is really 
				// there.  There might be some trick we can use to see if 
				// something is attached, by reading a value and seeing if it
				// is different than the baseline value.
				if(sensorId <= 0){
					continue;
				}
						
				int channelType = VernierSensor.CHANNEL_TYPE_ANALOG;
				if(channelNumber > 10){
					channelType = VernierSensor.CHANNEL_TYPE_DIGITAL;
				}
				
				VernierSensor sensorConfig = 
					new VernierSensor(this, devService, channelNumber,
							channelType);
				
				// translate the vernier id to the SenorConfig id
				sensorConfig.setupSensor(sensorId, null);
				sensorConfigVect.add(sensorConfig);
			}
		} catch (SerialException e) {
			currentErrorMessage = ERR_DEVICE_NOT_ATTACHED;
			System.err.println(e.toString());
			return null;
		}
		
		int numSensors = sensorConfigVect.size();
		if(numSensors == 0){
			expConfig.setSensorConfigs(null);			
		} else {
			SensorConfig [] sensorConfigArr = 
				new SensorConfig[sensorConfigVect.size()];
			Vector.copyArray(sensorConfigVect.items, 0, sensorConfigArr, 
					0, sensorConfigVect.size());
			expConfig.setSensorConfigs(sensorConfigArr);
		}
		
	    expConfig.setExactPeriod(true);

	    return expConfig;
	}

	/* (non-Javadoc)
	 * @see org.concord.sensor.device.SensorDevice#getErrorMessage(int)
	 */
	public String getErrorMessage(int error)
	{
		return currentErrorMessage;
	}

	float [] dataValues = new float[2];

	private SensorSerialPort usbSerialPort;

	private SensorSerialPort osSerialPort;
	
	protected int streamRead(float[] values, int offset, int nextSampleOffset, 
			DeviceReader reader, StreamingBuffer streamingBuffer)
	{
		// read all the bytes from the port and look for the 
		// {} blocks
		try {
			int count;
			int numSamples = 0;
			while(true ){
				count = readValues(dataValues, streamingBuffer);
				if(count <= 0){
					break;
				}
				for(int i=0; i<count; i++){
					values[offset + nextSampleOffset * numSamples + i] = dataValues[i];
				}
				numSamples++;
			}

			// The readValues method should have updated the 
			// processedBytes field of the streamingBuffer
			
			return numSamples;
			// the problem with this approach is that we are goint to eat
			// up bytes from the serial port before the whole thing is 
			// ready.
			
			// so we need to store the read bytes into a buffer
			// that we then reuse.
			
		} catch (SerialException e) {
			e.printStackTrace();
		}
		
		return 0;
	}

	/**
	 * @see org.concord.sensor.device.SensorDevice#start()
	 */
	public boolean start()
	{
		try {
			protocol.wakeUp();
			protocol.reset();

			SensorConfig[] sensorConfigs = currentConfig.getSensorConfigs();
			for (SensorConfig sensor : sensorConfigs) {
				// get the current channel from the currentConfig
				int channelNumber = sensor.getPort();
				
				// collected data from channel one using the 
				// auto id operation
				
				if(sensor.getType() == SensorConfig.QUANTITY_RAW_VOLTAGE_1 ||
						sensor.getType() == SensorConfig.QUANTITY_RAW_DATA_1){
					// setup sensor to report 0-5V
					// I have not found a way to access the raw data on the LabPro
					// so 0-5 seems like the best default here.
					protocol.channelSetup(channelNumber, 14);
				} else if(sensor.getType() == SensorConfig.QUANTITY_RAW_VOLTAGE_2 ||
						sensor.getType() == SensorConfig.QUANTITY_RAW_DATA_2){
					// setup sensor to report +/-10V
					protocol.channelSetup(channelNumber, 2);				
				} else {
					protocol.channelSetup(channelNumber, 1);
				}				
			}			
			
			// Turning on the power seems necessary before reading
			// from the gomotion in real time.  It might also be
			// necessary for something like the relative humidity sensor
			// The power is not turned off immediately when the device is reset.
			// however it does time out after a reset  
			protocol.portPowerControl(-2);

			// start the collection
			// sample once every 0.5 seconds
			// use realtime mode (-1)
			// start sampling now (0)			
			protocol.dataCollectionSetup(currentConfig.getPeriod(),-1,0);
			
			// If the power is not turned on the following command will make
			// the gomotion collect data, but it is not real time so the 
			// read method would have to be modfied. 
			//protocol.dataCollectionSetup(0.5f,100,0);
			
			return super.start();
		} catch (SerialException e) {
			e.printStackTrace();
		}
		
		return false;
	}

	/**
	 * @see org.concord.sensor.device.SensorDevice#stop(boolean)
	 */
	public void stop(boolean wasRunning)
	{
		try {
			// send a wakeup just in case
			protocol.wakeUp();
			
			// send the reset
			protocol.reset();
						
			// The port should be closed if it opens quickly, that way
			// if the program crashes or another one is opened then will
			// be less of a chance of conflict.
			if(port.isOpenFast()){
				port.close();
			}
			
		} catch (SerialException e) {
			e.printStackTrace();
		}
		
	}

	protected int readValues(float [] values, StreamingBuffer sb)
    	throws SerialException
	{
		// read one byte at a time until we get to the closing bracket
		// this is not the best way to do this, but it work without blocking
		// regardless about how the readBytes on the port is implemented
		// The timeout here should be set depending on how long it takes
		// the LabPro to get back to us with the first byte
		// check that the first byte is a }		
		if((sb.totalBytes - sb.processedBytes) < 2){
			return 0;
		}
		
		byte currentByte = sb.buf[sb.processedBytes];
		if(currentByte != '{'){
			log("First byte isn't { instead it is: " + (char)currentByte);

			// we should look ahead and find the first byte that is {
			int off = sb.processedBytes + 1;
			while(off < sb.totalBytes && currentByte != '{'){
				currentByte = sb.buf[off++];
			}
			
			if(currentByte != '{'){
				// we didn't find the beginning char, so we only read part 
				// of a packet
				// increment processedBytes to the end and return that we didn't find any values
				sb.processedBytes = off;
				return 0;				
			} else {
				// start the processed bytes at the '{'
				sb.processedBytes = off - 1;
			}			
		}

		int off = sb.processedBytes + 1;
			
		while(off < sb.totalBytes && currentByte != '\n'){
			currentByte = sb.buf[off++];
		}

		if(currentByte != '\n'){
			// we didn't find the ending char, so we only read part 
			// of a packet
			// leave the processedBytes alone, so we can read them all again
			// the next time
			return 0;
		}
		
		// we should now have a buffer with a string in it from 0-off
		// I don't know if this will work in waba
		String result = 
			new String(sb.buf, sb.processedBytes, off-sb.processedBytes);

		// now we have to use basic string parsing because waba and java don't 
		// share the tokenizer
		// but sense we are in a crunch lets just use the java conventions
		// and deal with the waba stuff when we need it.
		int count = 0;
		StringTokenizer toks = new StringTokenizer(result, "{},");
		while(toks.hasMoreTokens() && count < values.length){
			String numberStr = toks.nextToken();
			try {
				float number = Float.parseFloat(numberStr);
				values[count] = number;
			} catch (NumberFormatException e){
				System.err.println("error parsing: " + numberStr);
				e.printStackTrace();
			}
			count++;
		}
		
		sb.processedBytes = off;
		return count;
	}
	
	/**
	 * This read the string return values of the LabPro
	 * These values look like:
	 * {  +2.40000E+01, -9.99900E+02, -9.99900E+02 }
	 * @param values
	 * @return -1 if there is an error, otherwise returns the number of values
	 * @throws SerialException 
	 */	
	protected int readValues(float [] values)
		throws SerialException
	{
		// read one byte at a time until we get to the closing bracket
		// this is not the best way to do this, but it work without blocking
		// regardless about how the readBytes on the port is implemented
		// The timeout here should be set depending on how long it takes
		// the LabPro to get back to us with the first byte
		// check that the first byte is a }		

		// It ought to be faster to read a whole chunk of bytes until the last
		// byte read is }
		int ret = 0;
		int off = 0;
		int numBytes = 0;
		int attempts = 0;
		while(attempts < 5){
			// give it 100ms to send the reponse
			ret = port.readBytes(buf, off, buf.length-off, 100);		
			if(ret < 0){
				log("error reading values err: " + ret);
				return -1;
			}
			
			if(ret == 0){
				// should at least get something
				log("Didn't get any bytes from device attempt: " + attempts);
				attempts++;
				continue;
			}
			
			numBytes += ret;
			
			if(buf[numBytes-1] == '\n'){
				break;
			}
			
			off += ret;
			attempts++;
		}

		if(numBytes < 1){
			// didn't find any bytes at all probably no device is attached
			log("error reading values: no data returned " + attempts + " attempts");
			return -1;
		}
		
		if(buf[numBytes-1] != '\n'){
			// we got an error 
			String lastByteStr = "";
			if(ret>0){
				lastByteStr = " lastB: " + (char)buf[ret-1];				
			}
			log("error reading values ret: " + ret + lastByteStr);
			return -1;
		}
		
		// we should now have a buffer with a string in it from 0-off
		// I don't know if this will work in waba
		String result = new String(buf, 0, numBytes);

		// optional logging should have an option in the logging
		// system so this can be turned on and off
		log("read: \"" + result + "\"");
		
		// We should use basic string parsing because waba and java don't 
		// share a common tokenizer class
		// but since we are in a time crunch lets just use the java conventions
		// and deal with the waba stuff when we need it.
		
		// first find the last occurance of { that way we can 
		// skip any junk that came with this. 
		int startingIndex = result.lastIndexOf("{");
		
		if(startingIndex == -1){
			// invalid return format
			log("readValues got invalid return: " + result);
			return -1;
		}
		
		result = result.substring(startingIndex);
		
		int count = 0;
		StringTokenizer toks = new StringTokenizer(result, "{},");
		while(toks.hasMoreTokens() && count < values.length){
			String numberStr = toks.nextToken();
			numberStr = numberStr.trim();
			if(numberStr.length() == 0) {
				continue;
			}
			float number = Float.parseFloat(numberStr);
			values[count] = number;
			count++;
		}
		
		return count;
	}

	/* (non-Javadoc)
	 * @see org.concord.sensor.device.impl.AbstractStreamingSensorDevice#getStreamBufferSize()
	 */
	protected int getStreamBufferSize()
	{
		return 1024;
	}

	SensorSerialPort getPort()
	{
		return port;
	}
	
	DeviceService getDeviceService()
	{
		return devService;
	}
	

	protected SensorConfig createSensorConfig(int type, int requestPort) 
	{
    	SensorConfigImpl config = new SensorConfigImpl();
    	config.setType(type);
    	config.setPort(requestPort+1);
    	return config;
	}
	
	protected SensorSerialPort getSensorSerialPort()
	{
		if("usb".equals(portName)){
			return devService.getSerialPort(DeviceService.LABPROUSB_SERIAL_PORT, port);
		}
		
		return super.getSensorSerialPort();
	}

	protected boolean openAutoPort()
	{
		// first try the usb port.
		if(usbSerialPort == null){
			usbSerialPort = devService.getSerialPort(DeviceService.LABPROUSB_SERIAL_PORT, usbSerialPort);
		}
		
		// try opening the usb
		try {
			usbSerialPort.open(null);
			if(port != usbSerialPort && port != null){
				port.close();
			}
			port = usbSerialPort;
			portName = "usb";
			return true;
		} catch (SerialException e) {
			// The port could not be opened so lets move on to the rxtxPort
			System.err.println("Can't open LabPro USB");
			System.err.println("  " + e.toString());
		}

		if(osSerialPort == null){
			osSerialPort = devService.getSerialPort(DeviceService.OS_SERIAL_PORT, osSerialPort);
		}

		if(port != osSerialPort && port != null){
			try {
				port.close();
				port = osSerialPort;
			} catch (SerialException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// Do the default auto which asks the port for this ports availables ports and 
		// checks if each one works.
		return super.openAutoPort();		
	}

	/**
	 * Increase the visibility of the log by overriding it here
	 * that  
	 * 
	 * @see org.concord.sensor.device.impl.AbstractSensorDevice#log(java.lang.String)
	 */
	public void log(String message)
	{
	    // TODO Auto-generated method stub
	    super.log(message);
	}
	
	protected boolean hasNonAutoIdSensors() 
	{
		return true;
	}	

	/**
	 * @see org.concord.sensor.device.SensorDevice#canDetectSensors()
	 */
	public boolean canDetectSensors()
	{
		return true;
	}
	
	public String getVendorName() 
	{
		return "Vernier";
	}

	public String getDeviceName() 
	{
		return "LabPro";
	}
}