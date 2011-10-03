/**
 * 
 */
package org.concord.sensor.device.impl;

import org.concord.sensor.serial.SensorSerialPort;
import org.concord.sensor.serial.SerialException;

/**
 * @author scott
 *
 */
public class SerialPortParams
{
	public int flowControl;
	public int baud;
	public int data;
	public int stop;
	public int parity;
	
	public SerialPortParams( int flowControl,
			int baud, int data, int stop, int parity)
	{
		this.flowControl = flowControl;
		this.baud = baud;
		this.data = data;
		this.stop = stop;
		this.parity = parity;
	}
	
	public void setupPort(SensorSerialPort port) 
		throws SerialException		
	{
		port.setFlowControlMode(flowControl);
		port.setSerialPortParams(baud, data, stop, parity);
	}
}
