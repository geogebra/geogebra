/**
 * 
 */
package org.concord.sensor.device.impl;

import org.concord.sensor.device.DeviceReader;
import org.concord.sensor.serial.SerialException;

/**
 * @author scott
 *
 */
public abstract class AbstractStreamingSensorDevice extends AbstractSensorDevice
{
	private StreamingBuffer streamingBuffer;
	int readSize = 0;
	
	public AbstractStreamingSensorDevice()
	{
		readSize = getStreamBufferSize();

		streamingBuffer = new StreamingBuffer();
		// add a 100 byte cushion		
		streamingBuffer.buf = new byte[readSize + 100];
		streamingBuffer.processedBytes = 0;
		streamingBuffer.totalBytes = 0;
	}
	
	public boolean start()
	{
		error = ERROR_NONE;
		streamingBuffer.clear();

		return true;
	}
	
	/**
	 * @see org.concord.sensor.device.SensorDevice#read(float[], int, int, org.concord.sensor.device.DeviceReader)
	 */
	public int read(float[] values, int offset, int nextSampleOffset,
			DeviceReader reader)
	{
		// assume there is something wrong with the port.
		int ret = -1;

        try {
            ret = port.readBytes(streamingBuffer.buf, 
            		streamingBuffer.totalBytes,
            		readSize - streamingBuffer.totalBytes, 1);
            if(ret<0) {
                log("SerPort r error: " + ret + " (dataRead)");
                portError(0);
                return ret;
            }	    
        } catch (SerialException e) {
            log("SerPort r exp: " + e.getMessage() + " (dataRead)");
            portError(e.getPortError());
            return -1;            
        }
            
	    // the actual available bytes to be processed is the number of 
	    // saved bytes from before plus the new bytes we just read
        streamingBuffer.totalBytes += ret;

        int numSamples = streamRead(values, offset, nextSampleOffset,
				reader, streamingBuffer);        
        
        // FIXME check for the -2 return value mentioned above
        // I don't know if anything actually returns that
        
        if(numSamples < 0){
        	// there was some kind of unrecoverable error
        	// it should have already been logged so lets close the port
        	// and return an error
        	closePort();
        	return -1;
        }
        
        streamingBuffer.shift();

        return numSamples;
	}
	
	/**
	 * This should read the bytes out of the streamingBuffer and return the
	 * number of samples found.  It should update the processedBytes of the
	 * streamingBuffer before returning.  The implementation can assume that 
	 * processedBytes
	 * will be 0 at the beginning of each call.  The remaining bytes
	 * will be shifted down.
	 * 
	 * @return This number of samples found or -1 to indicate an error
	 */
	protected abstract int streamRead(float[] values, int offset, int nextSampleOffset,
			DeviceReader reader, StreamingBuffer streamingBuffer);
	
	/**
	 * This will be called in the constructor to create the buffer 
	 * used to store the steamed bytes
	 * @return
	 */
	protected abstract int getStreamBufferSize();	
}
