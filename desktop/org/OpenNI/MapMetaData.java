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

public abstract class MapMetaData extends OutputMetaData 
{
	MapMetaData(PixelFormat format, Map map)
	{
		this.pixelFormat = format;
		this.map = map;
	}
	
	public int getXRes() {
		return xRes;
	}
	public void setXRes(int xRes) {
		this.xRes = xRes;
		this.map.setXRes(xRes);
	}
	public int getYRes() {
		return yRes;
	}
	public void setYRes(int yRes) {
		this.yRes = yRes;
		this.map.setYRes(yRes);
	}
	public int getXOffset() {
		return xOffset;
	}
	public void setXOffset(int xOffset) {
		this.xOffset = xOffset;
	}
	public int getYOffset() {
		return yOffset;
	}
	public void setYOffset(int yOffset) {
		this.yOffset = yOffset;
	}
	public int getFullXRes() {
		return fullXRes;
	}
	public void setFullXRes(int fullXRes) {
		this.fullXRes = fullXRes;
	}
	public int getFullYRes() {
		return fullYRes;
	}
	public void setFullYRes(int fullYRes) {
		this.fullYRes = fullYRes;
	}
	public PixelFormat getPixelFormat() {
		return pixelFormat;
	}
	public int getFPS() {
		return FPS;
	}
	public void setFPS(int fPS) {
		FPS = fPS;
	}
	public Map getData() {
		return this.map;
	}
	
	@Override
	public void setDataPtr(long ptr) {
		super.setDataPtr(ptr);
		this.map.setNativePtr(ptr);
	}
	
	protected void setPixelFormat(PixelFormat format)
	{
		this.pixelFormat = format;
		this.map.setBytesPerPixel(format.getBytesPerPixel());
	}
	
	private int xRes;
	private int yRes;
	private int xOffset;
	private int yOffset;
	private int fullXRes;
	private int fullYRes;
	private PixelFormat pixelFormat;
	private int FPS;
	private Map map;
}
