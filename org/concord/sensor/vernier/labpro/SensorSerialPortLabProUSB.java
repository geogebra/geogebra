package org.concord.sensor.vernier.labpro;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.concord.sensor.impl.Vector;
import org.concord.sensor.labprousb.LabProUSB;
import org.concord.sensor.serial.SensorSerialPort;
import org.concord.sensor.serial.SerialException;

public class SensorSerialPortLabProUSB implements SensorSerialPort 
{
	byte [] tmpBuffer = new byte [2048];

	static {
		Runtime.getRuntime().addShutdownHook(new Thread(){
			public void run() {
				System.err.println("Closing LabProUSB.  Its open state is: " +
						LabProUSB.isOpen());
				
				// Make sure the labpro is closed
				LabProUSB.close();
			}
		});
	}
	
	
	public void close() throws SerialException 
	{
		LabProUSB.close();
	}

	public void disableReceiveTimeout() 
	{
		// there are no timeouts
	}

	public void enableReceiveTimeout(int time) throws SerialException 
	{
		// this isn't supported
	}

	public Vector getAvailablePorts() 
	{		
		// TODO Auto-generated method stub
		return null;
	}

	public int getBaudRate() 
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public int getDataBits() 
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public int getParity() 
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public int getStopBits() 
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean isOpen() 
	{
		short open = LabProUSB.isOpen();
		return open == 1;
	}

	public void open(String portName) throws SerialException 
	{
		boolean hasNewVernierDeviceDriver = false;
		if(System.getProperty("os.name").toLowerCase().startsWith("windows")){
			// check if the new labpro device driver is installed
			String windir = System.getenv("windir");
			if(windir != null){
				File newLabProFile = new File(windir + "\\system32\\LabProCo.dll");
				if(newLabProFile.exists()){
					// this computer has the new device driver.
					hasNewVernierDeviceDriver = true;
					System.out.println("Found new vernier device driver");
				}
			}
		}
				
		try {
			if(hasNewVernierDeviceDriver){
				System.loadLibrary("LabProUSB");
			} else {
				// extract the old driver and do a System.load with that.
				URL labProDLL2Resource = LabProUSB.class.getResource("LabProUSB-2.dll");
				try {
					File tempFile = File.createTempFile("testFile", null);
					File tempDir = tempFile.getParentFile();					
					tempFile.delete();
					File ccSensorLabProDir = new File(tempDir, "cc-sens-labpro");
					if(!ccSensorLabProDir.isDirectory()){
						ccSensorLabProDir.mkdirs();
					} 
					File dllFile = new File(ccSensorLabProDir, "LabProUSB.dll");
					if(dllFile.exists()){
						dllFile.delete();
					}
					dllFile.createNewFile();
					FileOutputStream fileOutputStream = new FileOutputStream(dllFile);
					InputStream openStream = labProDLL2Resource.openStream();
					byte [] buffer= new byte[1024];
					while(true){
						int numRead = openStream.read(buffer);
						if(numRead < 0){
							break;
						}
						fileOutputStream.write(buffer, 0, numRead);						
					}
					fileOutputStream.close();
					System.load(dllFile.getAbsolutePath());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			System.loadLibrary("labprousb_wrapper");			
		} catch (UnsatisfiedLinkError e){
			e.printStackTrace();
			throw new SerialException("Can't load labprousb library", e);
		}
		
		short open = LabProUSB.open();
		if(open < 0){
			// not opened 
			throw new SerialException("LabPro USB driver returned " + open + " on open call");
		}
		
		return;
	}

	/**
	 * This is not thread safe.
	 */
	public int readBytes(byte[] buf, int off, int len, long timeout)
			throws SerialException 
	{
		int size = 0;	    
	    long startTime = System.currentTimeMillis();
	    while(size != -1 && size < len &&
	            (System.currentTimeMillis() - startTime) < timeout){
	    	
	    	int availableBytes = LabProUSB.getAvailableBytes();
	    	if(availableBytes > 0){
	    		int numRead = LabProUSB.readBytes(availableBytes, tmpBuffer);
		        if(numRead < 0) {	      
		            System.err.println();
		            System.err.println("error in readBytes: " + numRead);
		            
		            return numRead;
		        }
	    		
				System.arraycopy(tmpBuffer, 0, buf, size+off, numRead);	    		
	    		size += numRead;
	    	} 
	    	
	    	try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    
	    return size;	
	}

	public void setFlowControlMode(int flowcontrol) throws SerialException {
		// TODO Auto-generated method stub

	}

	public void setSerialPortParams(int baud, int data, int stop, int parity)
			throws SerialException {
		// TODO Auto-generated method stub

	}

	public void write(int value) throws SerialException 
	{
		tmpBuffer[0] = (byte)value;
		write(tmpBuffer, 0, 1);
	}

	public void write(byte[] buffer) throws SerialException 
	{
		write(buffer, 0, buffer.length);
	}

	public void write(byte[] buffer, int start, int length)
			throws SerialException 
	{
		byte [] bufToWrite = buffer;
		if(start != 0){
			System.arraycopy(buffer, start, tmpBuffer, 0, length);
			bufToWrite = tmpBuffer;
		}
		
		short numWritten = LabProUSB.writeBytes((short)length, bufToWrite);
		if(numWritten < length){			
			throw new SerialException("Didn't write all bytes. Wrote: " + numWritten +
					" out of: " + length);
		}		
	}

	/**
	 * This port can close and open quickly.
	 * 
	 */
	public boolean isOpenFast() 
	{
		return true;
	}
}
