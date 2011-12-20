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

public class EnumerationErrors extends ObjectWrapper
{
	public EnumerationErrors() throws StatusException
	{
		this(create());
	}
	
	public boolean isEmpty()
	{
		long first = NativeMethods.xnEnumerationErrorsGetFirst(toNative());
		return !NativeMethods.xnEnumerationErrorsIteratorIsValid(first);
	}
	
	@Override
	public String toString() 
	{
		OutArg<String> result = new OutArg<String>();
		NativeMethods.xnEnumerationErrorsToString(toNative(), result);
		return result.value;
	}
	
	@Override
	protected void freeObject(long ptr) 
	{
		NativeMethods.xnEnumerationErrorsFree(ptr);
	}

	private EnumerationErrors(long ptr) 
	{
		super(ptr);
	}
	
	private static long create() throws StatusException
	{
		OutArg<Long> pErrors = new OutArg<Long>();
		int status = NativeMethods.xnEnumerationErrorsAllocate(pErrors);
		WrapperUtils.throwOnError(status);
		return pErrors.value;
	}
}
