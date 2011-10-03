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
 * Created on Feb 16, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.concord.sensor.serial;

import org.concord.sensor.device.DeviceService;

/**
 * @author scott
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ByteBufferStream
{
    byte [] inBuf;
    int offset;
    int endOffset;
    boolean lowNibble = false;
    DeviceService devService;
    
    public static final int readUShort(byte [] inBuf, int offset)
    {
        int value =((inBuf[offset] & 0xFF) << 8) | 
        	(inBuf[offset+1] & 0xFF);
        return value;        
    }

    public static final short readShort(byte [] inBuf, int offset)
    {
        int value =((inBuf[offset]) << 8) | 
        	(inBuf[offset+1] & 0xFF);
        return (short)value;
    }

    public static final long readULong(byte [] inBuf, int offset)
    {
        long value = ((long)((inBuf[offset++] & 0xFFL) << 24)) |
        	((inBuf[offset++] & 0xFF) << 16) |
        	((inBuf[offset++] & 0xFF) << 8) | 
        	(inBuf[offset] & 0xFF);
        return value;        
    }

    public static final void writeULong(long value, byte [] buf, int offset)
    {
        buf[offset]   = (byte)((value & 0xFF000000) >> 24);
        buf[offset+1] = (byte)((value & 0x00FF0000) >> 16);
        buf[offset+2] = (byte)((value & 0x0000FF00) >>  8);
        buf[offset+3] = (byte)(value & 0x000000FF);        
    }
    
    public static final void writeUShort(int value, byte [] buf, int offset)    
    {
        buf[offset] = (byte)((value & 0x0000FF00) >>  8);
        buf[offset+1] = (byte)(value & 0x000000FF);        
    }

    public static final void writeUByte(int value, byte [] buf, int offset)    
    {
        buf[offset] = (byte)(value & 0x000000FF);        
    }

    public ByteBufferStream(byte [] buffer, int offset, int length,
            DeviceService devService)
    {
        inBuf = buffer;
        this.offset = offset;
        this.endOffset = offset+length;
        this.devService = devService;
    }
    
    public void setOffset(int offset)
    {
    	this.offset = offset;
    }
    
    public float readFloat()
    {
        if(lowNibble) {
            throw new RuntimeException("unread low nibble");
        }
        int valueInt = ((0xFF & inBuf[offset]) << 24)
        | ((0xFF & inBuf[offset + 1]) << 16)
        | ((0xFF & inBuf[offset + 2]) << 8)
        | (0xFF & inBuf[offset + 3]);
        float value = devService.intBitsToFloat(valueInt);        

        offset += 4;
        
        return value;
    }
    
    public int readUShort()
    {
        if(lowNibble) {
            throw new RuntimeException("unread low nibble");
        }
        int value = readUShort(inBuf, offset);
        offset += 2;

        return value;        
    }
    
    public int readUByte()
    {
        if(lowNibble) {
            throw new RuntimeException("unread low nibble");
        }
        int value = (inBuf[offset] & 0xFF);
        offset++;
        return value;        
    }    

    public int readUNibble()
    {
        int value;
        
        if(lowNibble){
            value = (inBuf[offset] & 0xF);
            offset++;
            lowNibble = false;
        } else {
            value = ((inBuf[offset] & 0xF0) >> 4);
            // don't increase offset just set lowNibble
            lowNibble = true;           
        }
        
        return value;
    }

    public String readCRTermString()
    {
        if(lowNibble) {
            throw new RuntimeException("unread low nibble");
        }
        String returnStr = "";
        while(offset < endOffset){
            byte currChar = inBuf[offset++];
            if(currChar == 0x0D)  break;

            returnStr += (char)currChar;
        }
        
        return returnStr;
    }
    
    public void writeULong(long value)
    {
        writeULong(value, inBuf, offset);
        offset += 4;
    }
    
    public void writeUShort(int value)    
    {
        writeUShort(value, inBuf, offset);
        offset += 2;
    }

    public void writeUByte(int value)    
    {
        writeUByte(value, inBuf, offset);
        offset += 1;
    }

    /**
     * Print each byte as 2 digit hex with no 0x prefix and a space inbetween
     * @param numBytes
     * @param buf
     * @return
     */
	public static String printBytesHex(int numBytes, byte []buf)
	{
		String result = "";
		// FIXME this is going to break Waba
		for(int i=0; i<numBytes; i++) {
			result += Integer.toString(buf[i] & 0xFF, 16) + " ";
		}		
		return result;
	}		

	/**
	 * Print each byte a char.  They are padded so they can be lined
	 * up with printBytesHex.
	 * 
	 * @param numBytes
	 * @param buf
	 * @return
	 */
	public static String printBytesChar(int numBytes, byte [] buf)
	{
		String result = "";
		for(int i=0; i<numBytes; i++) {
			int charInt = buf[i] & 0xFF;
			if(charInt > 0x20 && charInt < 0x80){
				result += (char)buf[i] + "  ";
			} else {
				result += "   ";
			}
		}
		return result;
	}
}
