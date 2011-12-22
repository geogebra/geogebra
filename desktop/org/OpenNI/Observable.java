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

import java.util.ArrayList;

public abstract class Observable<Args> implements IObservable<Args> 
{
	public Observable()
	{
		this.observers = new ArrayList<IObserver<Args>>();
	}
	
	public void addObserver(IObserver<Args> observer) throws StatusException 
	{
		if (this.observers.size() == 0)
		{
			OutArg<Long> hCallback = new OutArg<Long>();
			int status = registerNative(hCallback);
			WrapperUtils.throwOnError(status);
			this.hCallback = hCallback.value;
		}
		
		this.observers.add(observer);
	}

	public void deleteObserver(IObserver<Args> observer) 
	{
		this.observers.remove(observer);

		if (this.observers.size() == 0)
		{
			unregisterNative(this.hCallback);
		}
	}
	
	public void notify(Args args)
	{
		for (IObserver<Args> observer : this.observers)
		{
			observer.update(this, args);
		}
	}
	
	protected abstract int registerNative(OutArg<Long> phCallback) throws StatusException;
	protected abstract void unregisterNative(long hCallback);

	private ArrayList<IObserver<Args>> observers; 
	private long hCallback;
}
