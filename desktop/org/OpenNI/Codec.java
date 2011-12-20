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

public class Codec extends ProductionNode
{
	Codec(Context context, long nodeHandle, boolean addRef) throws StatusException
	{
		super(context, nodeHandle, addRef);
	}

	public static Codec create(Context context, CodecID codecID, ProductionNode initializer) throws GeneralException
	{
		OutArg<Long> handle = new OutArg<Long>();
		int status = NativeMethods.xnCreateCodec(context.toNative(), codecID.toNative(), initializer.toNative(), handle);
		WrapperUtils.throwOnError(status);
		Codec result = (Codec)context.createProductionNodeObject(handle.value, NodeType.CODEC);
		NativeMethods.xnProductionNodeRelease(handle.value);
		return result;
	}

	public CodecID getCodecID()
	{
		int codecID = NativeMethods.xnGetCodecID(toNative());
		return new CodecID(codecID);
	}
	
	public int EncodeData(long pSrcPtr, int nSrcSize, long pDstPtr, int nDstSize) throws StatusException
	{
		OutArg<Integer> written = new OutArg<Integer>();
		int status = NativeMethods.xnEncodeData(toNative(), pSrcPtr, nSrcSize, pDstPtr, nDstSize, written);
		WrapperUtils.throwOnError(status);
		return written.value;
	}

	public int DecodeData(long pSrcPtr, int nSrcSize, long pDstPtr, int nDstSize) throws StatusException
	{
		OutArg<Integer> written = new OutArg<Integer>();
		int status = NativeMethods.xnDecodeData(toNative(), pSrcPtr, nSrcSize, pDstPtr, nDstSize, written);
		WrapperUtils.throwOnError(status);
		return written.value;
	}
}
