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

public class SceneAnalyzer extends MapGenerator
{

	SceneAnalyzer(Context context, long nodeHandle, boolean addRef)
			throws GeneralException
	{
		super(context, nodeHandle, addRef);
	}

	public static SceneAnalyzer create(Context context, Query query, EnumerationErrors errors) throws GeneralException
	{
		OutArg<Long> handle = new OutArg<Long>();
		int status = NativeMethods.xnCreateSceneAnalyzer(context.toNative(), handle,
			query == null ? 0 : query.toNative(),
			errors == null ? 0 : errors.toNative());
		WrapperUtils.throwOnError(status);
		SceneAnalyzer result = (SceneAnalyzer)context.createProductionNodeObject(handle.value, NodeType.SCENE);
		NativeMethods.xnProductionNodeRelease(handle.value);
		return result;
	}

	public static SceneAnalyzer create(Context context, Query query) throws GeneralException
	{
		return create(context, query, null);
	}

	public static SceneAnalyzer create(Context context) throws GeneralException
	{
		return create(context, null, null);
	}

	public SceneMap getSceneMap() throws GeneralException
	{
		int frameID = getFrameID();
		
		if ((this.currSceneMap == null) || (this.currSceneMapFrameID != frameID))
		{
			long ptr = NativeMethods.xnGetLabelMap(toNative());
			MapOutputMode mode = getMapOutputMode();
			this.currSceneMap = new SceneMap(ptr, mode.getXRes(), mode.getYRes());
			this.currSceneMapFrameID = frameID; 
		}

		return this.currSceneMap;
	}
	public void getMetaData(SceneMetaData sceneMD)
	{
		NativeMethods.xnGetSceneMetaData(this.toNative(), sceneMD);
	}

	public SceneMetaData getMetaData()
	{
		SceneMetaData sceneMD = new SceneMetaData();
		getMetaData(sceneMD);
		return sceneMD;
	}

	public Plane3D getFloor() throws StatusException
	{
		OutArg<Point3D> planeNormal = new OutArg<Point3D>();
		OutArg<Point3D> planePoint = new OutArg<Point3D>();
		
		int status = NativeMethods.xnGetFloor(toNative(), planeNormal, planePoint);
		WrapperUtils.throwOnError(status);
		return new Plane3D(planeNormal.value, planePoint.value);
	}

	private SceneMap currSceneMap;
	private int currSceneMapFrameID;
}
