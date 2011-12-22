/****************************************************************************
*                                                                           *
*  OpenNI 1.x Alpha                                                         *
*  Copyright (C) 2011 PrimeSense Ltd.                                       *
*                                                                           *
*  This file is part of OpenNI.                                             *
*                                                                           *
*  OpenNI is free software: you can redistribute it and/or modify           *
*  it under the terms of the GNU Lesser General Public License as published *
*  by the Free Software Foundation, either version 3 of the License, or     *
*  (at your option) any later version.                                      *
*                                                                           *
*  OpenNI is distributed in the hope that it will be useful,                *
*  but WITHOUT ANY WARRANTY; without even the implied warranty of           *
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the             *
*  GNU Lesser General Public License for more details.                      *
*                                                                           *
*  You should have received a copy of the GNU Lesser General Public License *
*  along with OpenNI. If not, see <http://www.gnu.org/licenses/>.           *
*                                                                           *
****************************************************************************/
package org.OpenNI;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Map 
{
	Map()
	{
	}
	
	Map(long ptr, int xRes, int yRes, int bytesPerPixel)
	{
		this.ptr = ptr;
		this.xRes = xRes;
		this.yRes = yRes;
		this.bytesPerPixel = bytesPerPixel;
	}
	
	public long getNativePtr()
	{
		return this.ptr;
	}
	
	public void setNativePtr(long ptr)
	{
		this.ptr = ptr;
	}
	
	protected ByteBuffer createByteBuffer()
	{
		int size = this.xRes * this.yRes * this.bytesPerPixel;
		ByteBuffer buffer = ByteBuffer.allocateDirect(size);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		NativeMethods.copyToBuffer(buffer, this.ptr, size);
		return buffer;
	}
	
	public void copyToBuffer(ByteBuffer buffer, int size)
	{
		NativeMethods.copyToBuffer(buffer, this.ptr, size);
	} 	
	
	protected long getPixelPtr(int x, int y) 
	{ 
		return this.ptr + (y * this.xRes + x) * this.bytesPerPixel; 
	} 

	public int getXRes() {
		return xRes;
	}

	public void setXRes(int xRes) {
		this.xRes = xRes;
	}

	public int getYRes() {
		return yRes;
	}

	public void setYRes(int yRes) {
		this.yRes = yRes;
	}

	public int getBytesPerPixel() {
		return bytesPerPixel;
	}

	protected void setBytesPerPixel(int bytesPerPixel) {
		this.bytesPerPixel = bytesPerPixel;
	}

	protected long ptr;
	protected int xRes;
	protected int yRes;
	protected int bytesPerPixel;
}
