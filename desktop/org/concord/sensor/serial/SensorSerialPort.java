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
 * Created on Feb 25, 2005
 *
 */
package org.concord.sensor.serial;

import org.concord.sensor.impl.Vector;

/**
 * @author scytacki
 *
 */
public interface SensorSerialPort 
{	
	public static final int  FLOWCONTROL_NONE       =0;
	public static final int  FLOWCONTROL_RTSCTS_IN  =1;
	public static final int  FLOWCONTROL_RTSCTS_OUT =2;
	public static final int  FLOWCONTROL_XONXOFF_IN =4;
	public static final int  FLOWCONTROL_XONXOFF_OUT=8;

    /**
     * This method should return a vector of the names of ports
     * that can be passed to open.  This list should be as complete
     * as possible.  But in some cases implementations cannot know 
     * the names of their ports so the list could be empty.  
     * 
     * If the name of the port doesn't matter like in the current
     * implementation of the ftdi port, then this should return
     * vector of size one.  With a dummy portname in it.
     * 
     * @return
     */
    public Vector getAvailablePorts();        
    
	public abstract void open(String portName)
		throws SerialException;
	
	public abstract void close()
		throws SerialException;
	
	public boolean isOpen();
	
	public abstract void setSerialPortParams( int baud, int data, 
            int stop, int parity )
		throws SerialException;
    
	public abstract int getBaudRate();
	public abstract int getDataBits();
	public abstract int getStopBits();
	public abstract int getParity();
	
    public abstract void setFlowControlMode( int flowcontrol )
		throws SerialException;

	public abstract void disableReceiveTimeout();
	
    public abstract void enableReceiveTimeout( int time )
		throws SerialException;

	/**
	 * The read method on input stream does not handle the timeout 
	 * quite the way I want it.  It varies from implementation.  In 
	 * some cases the threshold is set to 1 byte in some cases it is
	 * set to len bytes.  In this method the threshold is always len
	 * bytes.
	 * 
	 * The thresold is the minimum number of bytes that need to be read
	 * before the method returns.  If this is minimum number is not read
	 * before the timeout then the method returns the number read
	 * at that point.   
	 * 
	 * @param buf
	 * @param off
	 * @param len
	 * @param timeout
	 * @return number bytes read
	 * @throws SerialException
	 */
	public int readBytes(byte [] buf, int off, int len, long timeout)
		throws SerialException;
    
    public void write(int value)
        throws SerialException;
    
    public void write(byte [] buffer)
        throws SerialException;
    
    public void write(byte [] buffer, int start, int length)
        throws SerialException;

    /**
     * This should return true the port can open itself quickly.  If that is true
     * then the code working with the port might close and open it often to reduce
     * the chance of conflict.
     * 
     * Exactly how fast "quickly" is, is not clear yet.  I would guess it is around less
     * than 100ms.
     * 
     * @return
     */
    public boolean isOpenFast();
}
