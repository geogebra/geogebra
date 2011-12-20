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

public class DepthGenerator extends MapGenerator 
{
	DepthGenerator(Context context, long nodeHandle, boolean addRef) throws GeneralException 
	{
		super(context, nodeHandle, addRef);
		
		this.fovChanged = new StateChangedObservable() 
		{
			@Override
			protected int registerNative(String cb, OutArg<Long> phCallback)
			{
				return NativeMethods.xnRegisterToDepthFieldOfViewChange(toNative(), this, cb, phCallback);
			}

			@Override
			protected void unregisterNative(long hCallback) 
			{
				NativeMethods.xnUnregisterFromDepthFieldOfViewChange(toNative(), hCallback);
			}
		};
	}
	
	public static DepthGenerator create(Context context, Query query, EnumerationErrors errors) throws GeneralException
	{
		OutArg<Long> handle = new OutArg<Long>();
		int status = NativeMethods.xnCreateDepthGenerator(context.toNative(), handle,
			query == null ? 0 : query.toNative(),
			errors == null ? 0 : errors.toNative());
		WrapperUtils.throwOnError(status);
		DepthGenerator result = (DepthGenerator)context.createProductionNodeObject(handle.value, NodeType.DEPTH);
		NativeMethods.xnProductionNodeRelease(handle.value);
		return result;
	}

	public static DepthGenerator create(Context context, Query query) throws GeneralException
	{
		return create(context, query, null);
	}

	public static DepthGenerator create(Context context) throws GeneralException
	{
		return create(context, null, null);
	}

	public DepthMap getDepthMap() throws GeneralException
	{
		int frameID = getFrameID();
		
		if ((this.currDepthMap == null) || (this.currDepthMapFrameID != frameID))
		{
			long ptr = NativeMethods.xnGetDepthMap(toNative());
			MapOutputMode mode = getMapOutputMode();
			this.currDepthMap = new DepthMap(ptr, mode.getXRes(), mode.getYRes());
			this.currDepthMapFrameID = frameID; 
		}

		return this.currDepthMap;
	}
	
	public int getDeviceMaxDepth()
	{
		return NativeMethods.xnGetDeviceMaxDepth(this.toNative());
	}

	public FieldOfView getFieldOfView() throws StatusException
	{
		OutArg<Double> hFOV = new OutArg<Double>();
		OutArg<Double> vFOV = new OutArg<Double>();
		int status = NativeMethods.xnGetDepthFieldOfView(this.toNative(), hFOV, vFOV);
		WrapperUtils.throwOnError(status);
		return new FieldOfView(hFOV.value, vFOV.value);
	}
	
	public IStateChangedObservable getFieldOfViewChangedEvent() { return this.fovChanged; }

	public Point3D[] convertProjectiveToRealWorld(Point3D[] projectivePoints) throws StatusException
	{
		Point3D[] realWorld = new Point3D[projectivePoints.length];
		int status = NativeMethods.xnConvertProjectiveToRealWorld(this.toNative(), projectivePoints, realWorld);
		WrapperUtils.throwOnError(status);
		return realWorld;
	}

    public Point3D convertProjectiveToRealWorld(Point3D projectivePoint) throws StatusException
    {
        Point3D[] projectivePoints = new Point3D[1];
        projectivePoints[0] = projectivePoint;

        return convertProjectiveToRealWorld(projectivePoints)[0];
    }

    public Point3D[] convertRealWorldToProjective(Point3D[] realWorldPoints) throws StatusException
    {
        Point3D[] projective = new Point3D[realWorldPoints.length];
        int status = NativeMethods.xnConvertRealWorldToProjective(this.toNative(), realWorldPoints, projective);
        WrapperUtils.throwOnError(status);
        return projective;
    }
    
    public Point3D convertRealWorldToProjective(Point3D realWorldPoint) throws StatusException
	{
        Point3D[] realWorldPoints = new Point3D[1];
        realWorldPoints[0] = realWorldPoint;

        return convertRealWorldToProjective(realWorldPoints)[0];
    }

	public UserPositionCapability getUserPositionCapability() throws StatusException
	{
		return new UserPositionCapability(this);
	}

	public void getMetaData(DepthMetaData depthMD)
	{
		NativeMethods.xnGetDepthMetaData(this.toNative(), depthMD);
	}

	public DepthMetaData getMetaData()
	{
		DepthMetaData depthMD = new DepthMetaData();
		getMetaData(depthMD);
		return depthMD;
	}

	private StateChangedObservable fovChanged;
	private DepthMap currDepthMap;
	private int currDepthMapFrameID;
}
