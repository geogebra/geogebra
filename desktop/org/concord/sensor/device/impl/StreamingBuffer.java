/**
 * 
 */
package org.concord.sensor.device.impl;

/**
 * @author scott
 *
 */
public class StreamingBuffer
{
	public byte [] buf;
	public int totalBytes;

	// this is the offset into the buffer where our current
	// streaming bytes are stored.  This buffere can persist
	// over reads so we need to keep track of how bytes are already
	// in the buffer.
	public int processedBytes;
	
	/**
	 * Move all the remaining unprocessed data from the end of the buffer 
	 * to the beginning.
	 *
	 */
	public void shift()
	{
		int j;
		if(totalBytes > processedBytes){
			int difference = totalBytes - processedBytes;
			// Move all remaing data to the beginning of the array
			for(j=0; j<difference; j++){
				buf[j] = buf[processedBytes + j];
			}
			
			totalBytes = difference;
		} else {
		    totalBytes = 0;
		}
		
		processedBytes = 0;
	}
	
	public void clear()
	{
		processedBytes = 0;
		totalBytes = 0;
	}
}
