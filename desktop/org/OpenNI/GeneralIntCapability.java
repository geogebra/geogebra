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

public class GeneralIntCapability extends CapabilityBase
{
	public GeneralIntCapability(ProductionNode node, Capability capName) throws StatusException
	{
		super(node);
		this.capName = capName.getName();
		
		OutArg<Integer> pMin = new OutArg<Integer>();
		OutArg<Integer> pMax = new OutArg<Integer>();
		OutArg<Integer> pStep = new OutArg<Integer>();
		OutArg<Integer> pDefault = new OutArg<Integer>();
		OutArg<Boolean> pAutoSupported = new OutArg<Boolean>();
		
		int status = NativeMethods.xnGetGeneralIntRange(toNative(), getCapName(), pMin, pMax, pStep, pDefault, pAutoSupported);
		WrapperUtils.throwOnError(status);
		
		this.min = pMin.value;
		this.max = pMax.value;
		this.step = pStep.value;
		this.defaultVal = pDefault.value;
		this.autoSupported = pAutoSupported.value;
		
		this.valueChanged = new StateChangedObservable() 
		{
			@Override
			protected int registerNative(String cb, OutArg<Long> phCallback) 
			{
				return NativeMethods.xnRegisterToGeneralIntValueChange(toNative(), getCapName(), this, cb, phCallback);
			}

			@Override
			protected void unregisterNative(long hCallback) 
			{
				NativeMethods.xnUnregisterFromNodeErrorStateChange(toNative(), hCallback);
			}
		};
	}
	
	public int getMin()
	{
		return this.min;
	}

	public int getMax()
	{
		return this.max;
	}

	public int getStep()
	{
		return this.step;
	}

	public int getDefault()
	{
		return this.defaultVal;
	}

	public boolean isAutoSupported()
	{
		return this.autoSupported;
	}
	
	public int getValue() throws StatusException
	{
		OutArg<Integer> val = new OutArg<Integer>();
		int status = NativeMethods.xnGetGeneralIntValue(toNative(), this.capName, val);
		WrapperUtils.throwOnError(status);
		return val.value;
	}
	
	public void setValue(int value) throws StatusException
	{
		int status = NativeMethods.xnSetGeneralIntValue(toNative(), this.capName, value);
		WrapperUtils.throwOnError(status);
	}

	public IStateChangedObservable getValueChangedEvent() { return this.valueChanged; }
	
	String getCapName() { return this.capName; }

	private StateChangedObservable valueChanged;
	private final String capName;
	private int min;
	private int max;
	private int step;
	private int defaultVal;
	private boolean autoSupported;
}
