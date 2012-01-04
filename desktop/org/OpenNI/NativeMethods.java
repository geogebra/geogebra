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

class NativeMethods
{
	static 
	{ 
		String arch = System.getenv("PROCESSOR_ARCHITECTURE");
		if ((arch != null) && ((arch.equals("AMD64")) || (arch.equals("IA64"))))
			System.loadLibrary("OpenNI.jni64"); 
		else
			System.loadLibrary("OpenNI.jni");
	}

	// Marshaling
	static native byte readByte(long ptr);
	static native short readShort(long ptr);
	static native int readInt(long ptr);
	static native long readLong(long ptr);
	static native void copyToBuffer(ByteBuffer buffer, long ptr, int size);
	static native long createProductionNodeDescription(int type, String vendor, String name, byte major, byte minor, short maintenance, int build);
	static native void freeProductionNodeDescription(long pDescription);

	// General
	static native String xnGetStatusString(int status);
	
	// Context
	static native int xnInit(OutArg<Long> ppContext);
	static native int xnContextRunXmlScriptFromFileEx(long pContext, String strFileName, long pErrors, OutArg<Long> phScriptNode);
	static native int xnContextRunXmlScriptEx(long pContext, String xmlScript, long pErrors, OutArg<Long> phScriptNode);
	static native int xnInitFromXmlFileEx(String fileName, OutArg<Long> ppContext, long pErrors, OutArg<Long> ppScriptNode);
	static native int xnContextOpenFileRecordingEx(long pContext, String strFileName, OutArg<Long> phPlayerNode);
	static native int xnContextAddRef(long pContext);
	static native void xnContextRelease(long pContext);
	static native int xnEnumerateProductionTrees(long pContext,int Type, long pQuery, OutArg<Long> ppTreesList, long pErrors);
	static native int xnCreateProductionTree(long pContext, long pTree, OutArg<Long> phNode);
	static native int xnCreateAnyProductionTree(long pContext, int type, long pQuery, OutArg<Long> phNode, long pErrors);
	//static native int xnCreateMockNode(long pContext, int type, String strName, OutArg<Long> phNode);
	//static native int xnCreateMockNodeBasedOn(long pContext, long hOriginalNode, String strName, OutArg<Long> phMockNode);
	static native int xnEnumerateExistingNodes(long pContext, OutArg<Long> ppList);
	static native int xnEnumerateExistingNodesByType(long pContext, int type, OutArg<Long> ppList);
	static native int xnFindExistingRefNodeByType(long pContext, int type, OutArg<Long> phNode);
	static native int xnGetRefNodeHandleByName(long pContext, String strInstanceName, OutArg<Long> phNode);
	static native int xnWaitAndUpdateAll(long pContext);
	static native int xnWaitOneUpdateAll(long pContext, long hNode);
	static native int xnWaitAnyUpdateAll(long pContext);
	static native int xnWaitNoneUpdateAll(long pContext);
	static native int xnStartGeneratingAll(long pContext);
	static native int xnStopGeneratingAll(long pContext);
	static native int xnSetGlobalMirror(long pContext, boolean bMirror);
	static native boolean xnGetGlobalMirror(long pContext);
	static native int xnGetGlobalErrorState(long pContext);
	static native int xnRegisterToGlobalErrorStateChange(long pContext, Object obj, String methodName, OutArg<Long> phCallback);
	static native void xnUnregisterFromGlobalErrorStateChange(long pContext, long hCallback);
	
	// Licenses
	static native int xnAddLicense(long pContext, String vendor, String key);
	static native int xnEnumerateLicenses(long pContext, OutArg<License[]> licenses);
	
	// Enumeration Errors
	static native int xnEnumerationErrorsAllocate(OutArg<Long> ppErrors);
	static native void xnEnumerationErrorsFree(long pErrors);
	//static native int xnEnumerationErrorsAdd(long pErrors, long pDesc, int nError);
	static native int xnEnumerationErrorsToString(long pErrors, OutArg<String> value);
	static native int xnEnumerationErrorsClear(long pErrors);
	static native long xnEnumerationErrorsGetFirst(long pErrors);
	//static native long xnEnumerationErrorsGetNext(long it);
	static native boolean xnEnumerationErrorsIteratorIsValid(long it);
	//static native long xnEnumerationErrorsGetCurrentDescription(long it);
	//static native int xnEnumerationErrorsGetCurrentError(long it);
	
	// NodeInfo
	//static native int xnNodeInfoAllocate(long pDescription, String strCreationInfo, long pNeededNodes, OutArg<Long> ppNodeInfo);
	//static native void xnNodeInfoFree(long pNodeInfo);
	static native int xnNodeInfoSetInstanceName(long pNodeInfo, String strInstanceName);
	static native ProductionNodeDescription xnNodeInfoGetDescription(long pNodeInfo);
	static native int xnNodeInfoGetTreeStringRepresentation(long pNodeInfo, OutArg<String> result);
	static native String xnNodeInfoGetInstanceName(long pNodeInfo);
	static native String xnNodeInfoGetCreationInfo(long pNodeInfo);
	static native long xnNodeInfoGetNeededNodes(long pNodeInfo);
	static native long xnNodeInfoGetRefHandle(long pNodeInfo);
	//static native Object xnNodeInfoGetAdditionalData(long pNodeInfo);
	
	// NodeInfoList
	static native int xnNodeInfoListAllocate(OutArg<Long> ppList);
	static native void xnNodeInfoListFree(long pList);
	static native int xnNodeInfoListAdd(long pList, long pDescription, String strCreationInfo, long pNeededNodes);
	//static native int xnNodeInfoListAddEx(long pList, long pDescription, String strCreationInfo, long pNeededNodes, Object additionalData);
	static native int xnNodeInfoListAddNode(long pList, long pNode);
	static native int xnNodeInfoListAddNodeFromList(long pList, long it);
	static native int xnNodeInfoListRemove(long pList, long it);
	static native int xnNodeInfoListClear(long pList);
	static native int xnNodeInfoListAppend(long pList, long other);
	static native boolean xnNodeInfoListIsEmpty(long pList);
	static native long xnNodeInfoListGetFirst(long pNodeInfoList);
	//static native long xnNodeInfoListGetLast(long pList);
	static native boolean xnNodeInfoListIteratorIsValid(long it);
	static native long xnNodeInfoListGetCurrent(long it);
	static native long xnNodeInfoListGetNext(long it);
	//static native long xnNodeInfoListGetPrevious(long it);

	// Queries
	static native int xnNodeQueryAllocate(OutArg<Long> ppQuery);
	static native void xnNodeQueryFree(long pQuery);
	static native int xnNodeQuerySetVendor(long pQuery, String strVendor);
	static native int xnNodeQuerySetName(long pQuery, String strName);
	static native int xnNodeQuerySetMinVersion(long pQuery, byte major, byte minor, short maintenance, int build);
	static native int xnNodeQuerySetMaxVersion(long pQuery, byte major, byte minor, short maintenance, int build);
	static native int xnNodeQueryAddSupportedCapability(long pQuery, String strNeededCapability);
	static native int xnNodeQueryAddSupportedMapOutputMode(long pQuery, int xRes, int yRes, int FPS);
	static native int xnNodeQuerySetSupportedMinUserPositions(long pQuery, int nCount);
	static native int xnNodeQuerySetExistingNodeOnly(long pQuery, boolean bExistingNode);
	static native int xnNodeQuerySetNonExistingNodeOnly(long pQuery, boolean bNonExistingNode);
	static native int xnNodeQueryAddNeededNode(long pQuery, String strInstanceName);
	static native int xnNodeQuerySetCreationInfo(long pQuery, String strCreationInfo);
	static native int xnNodeQueryFilterList(long pContext, long pQuery, long pList);
	
	// Production Node
	static native int xnProductionNodeAddRef(long hNode);
	static native void xnProductionNodeRelease(long hNode);
	
	static native long xnGetNodeInfo(long hNode);
	static native String xnGetNodeName(long hNode);
	static native long xnGetRefContextFromNodeHandle(long hNode);
	static native boolean xnIsCapabilitySupported(long hInstance, String strCapabilityName);
	static native int xnSetIntProperty(long hInstance, String strName, long nValue);
	static native int xnSetRealProperty(long hInstance, String strName, double dValue);
	static native int xnSetStringProperty(long hInstance, String strName, String strValue);
	static native int xnSetGeneralProperty(long hInstance, String strName, int nBufferSize, long pBuffer);
	static native int xnSetGeneralPropertyArray(long hInstance, String strName, byte[] buffer);
	static native int xnGetIntProperty(long hInstance, String strName, OutArg<Long> pnValue);
	static native int xnGetRealProperty(long hInstance, String strName, OutArg<Double> pdValue);
	static native int xnGetStringProperty(long hInstance, String strName, OutArg<String> csValue);
	static native int xnGetGeneralProperty(long hInstance, String strName, int nBufferSize, long pBuffer);
	static native int xnGetGeneralPropertyArray(long hInstance, String strName, byte[] buffer);
	static native int xnLockNodeForChanges(long hInstance, OutArg<Integer> phLock);
	static native int xnUnlockNodeForChanges(long hInstance, int hLock);
	static native int xnLockedNodeStartChanges(long hInstance, int hLock);
	static native int xnLockedNodeEndChanges(long hInstance, int hLock);
	static native int xnAddNeededNode(long hInstance, long hNeededNode);
	static native int xnRemoveNeededNode(long hInstance, long hNeededNode);
	
	// Device
	static native int xnCreateDevice(long pContext, OutArg<Long> phDevice, long pQuery, long pErrors);

	// Device Identification Capability
	static native int xnGetDeviceName(long hInstance, OutArg<String> result);
	static native int xnGetVendorSpecificData(long hInstance, OutArg<String> result);
	static native int xnGetSerialNumber(long hInstance, OutArg<String> result);

	// Error State Capability
	static native int xnGetNodeErrorState(long hInstance);
	static native int xnRegisterToNodeErrorStateChange(long hInstance, Object obj, String methodName, OutArg<Long> phCallback);
	static native void xnUnregisterFromNodeErrorStateChange(long hInstance, long hCallback);

	// General Int Capability
	static native int xnGetGeneralIntRange(long hNode, String strCap, OutArg<Integer> pnMin, OutArg<Integer> pnMax, OutArg<Integer> pnStep, OutArg<Integer> pnDefault, OutArg<Boolean> pbIsAutoSupported);
	static native int xnGetGeneralIntValue(long hNode, String strCap, OutArg<Integer> pnValue);
	static native int xnSetGeneralIntValue(long hNode, String strCap, int nValue);
	static native int xnRegisterToGeneralIntValueChange(long hInstance, String strCap, Object obj, String methodName, OutArg<Long> phCallback);
	static native void xnUnregisterFromGeneralIntValueChange(long hNode, String strCap, long hCallback);
	
	// Generator
	static native int xnStartGenerating(long hInstance);
	static native boolean xnIsGenerating(long hInstance);
	static native int xnStopGenerating(long hInstance);
	static native int xnRegisterToGenerationRunningChange(long hInstance, Object obj, String methodName, OutArg<Long> phCallback);
	static native void xnUnregisterFromGenerationRunningChange(long hInstance, long hCallback);
	static native int xnRegisterToNewDataAvailable(long hInstance, Object obj, String methodName, OutArg<Long> phCallback);
	static native void xnUnregisterFromNewDataAvailable(long hInstance, long hCallback);
	static native boolean xnIsNewDataAvailable(long hInstance, OutArg<Long> pnTimestamp);
	static native int xnWaitAndUpdateData(long hInstance);
	static native boolean xnIsDataNew(long hInstance);
	static native long xnGetData(long hInstance);
	static native int xnGetDataSize(long hInstance);
	static native long xnGetTimestamp(long hInstance);
	static native int xnGetFrameID(long hInstance);

	// Mirror Capability
	static native int xnSetMirror(long hInstance, boolean bMirror);
	static native boolean xnIsMirrored(long hInstance);
	static native int xnRegisterToMirrorChange(long hInstance, Object obj, String methodName, OutArg<Long> phCallback);
	static native void xnUnregisterFromMirrorChange(long hInstance, long hCallback);

	// Alternative View Point
	static native boolean xnIsViewPointSupported(long hInstance, long hOther);
	static native int xnSetViewPoint(long hInstance, long hOther);
	static native int xnResetViewPoint(long hInstance);
	static native boolean xnIsViewPointAs(long hInstance, long hOther);
	static native int xnRegisterToViewPointChange(long hInstance, Object obj, String methodName, OutArg<Long> phCallback);
	static native void xnUnregisterFromViewPointChange(long hInstance, long hCallback);

	// Frame Sync
	static native boolean xnCanFrameSyncWith(long hInstance, long hOther);
	static native int xnFrameSyncWith(long hInstance, long hOther);
	static native int xnStopFrameSyncWith(long hInstance, long hOther);
	static native boolean xnIsFrameSyncedWith(long hInstance, long hOther);
	static native int xnRegisterToFrameSyncChange(long hInstance, Object obj, String methodName, OutArg<Long> phCallback);
	static native void xnUnregisterFromFrameSyncChange(long hInstance, long hCallback);

	// Map Generators
	static native int xnGetSupportedMapOutputModesCount(long hInstance);
	static native int xnGetSupportedMapOutputModes(long hInstance, MapOutputMode[] aModes);
	static native int xnSetMapOutputMode(long hInstance, int xRes, int yRes, int FPS);
	static native int xnGetMapOutputMode(long hInstance, OutArg<Integer> pxRes, OutArg<Integer> pyRes, OutArg<Integer> pFPS);
	static native int xnRegisterToMapOutputModeChange(long hInstance, Object obj, String methodName, OutArg<Long> phCallback);
	static native void xnUnregisterFromMapOutputModeChange(long hInstance, long hCallback);
	static native int xnGetBytesPerPixel(long hInstance);
	
	// Cropping
	static native int xnSetCropping(long hInstance, int xOffset, int yOffset, int xSize, int ySize, boolean enabled);
	static native int xnGetCropping(long hInstance, OutArg<Integer> xOffset, OutArg<Integer> yOffset, OutArg<Integer> xSize, OutArg<Integer> ySize, OutArg<Boolean> enabled);
	static native int xnRegisterToCroppingChange(long hInstance, Object obj, String methodName, OutArg<Long> phCallback);
	static native void xnUnregisterFromCroppingChange(long hInstance, long hCallback);

	// Anti Flicker
	static native int xnSetPowerLineFrequency(long hGenerator, int nFrequency);
	static native int xnGetPowerLineFrequency(long hGenerator);
	static native int xnRegisterToPowerLineFrequencyChange(long hInstance, Object obj, String methodName, OutArg<Long> phCallback);
	static native void xnUnregisterFromPowerLineFrequencyChange(long hGenerator, long hCallback);

	// Depth
	static native int xnCreateDepthGenerator(long pContext, OutArg<Long> phDepthGenerator, long pQuery, long pErrors);
	static native short xnGetDeviceMaxDepth(long hInstance);
	static native int xnGetDepthFieldOfView(long hInstance, OutArg<Double> hFOV, OutArg<Double> vFOV);
	static native int xnRegisterToDepthFieldOfViewChange(long hInstance, Object obj, String methodName, OutArg<Long> phCallback);
	static native void xnUnregisterFromDepthFieldOfViewChange(long hInstance, long hCallback);
	static native int xnConvertProjectiveToRealWorld(long hInstance, Point3D[] aProjective, Point3D[] aRealWorld);
	static native int xnConvertRealWorldToProjective(long hInstance, Point3D[] aRealWorld, Point3D[] aProjective);
	static native long xnGetDepthMap(long hInstance);
	static native void xnGetDepthMetaData(long hInstance, DepthMetaData metaData);

	// User Position
	static native int xnGetSupportedUserPositionsCount(long hInstance);
	static native int xnSetUserPosition(long hInstance, int nIndex, float xMin, float yMin, float zMin, float xMax, float yMax, float zMax);
	static native int xnGetUserPosition(long hInstance, int nIndex, OutArg<BoundingBox3D> pPosition);
	static native int xnRegisterToUserPositionChange(long hInstance, Object obj, String methodName, OutArg<Long> phCallback);
	static native void xnUnregisterFromUserPositionChange(long hInstance, long hCallback);

	// Image
	static native int xnCreateImageGenerator(long pContext, OutArg<Long> phImageGenerator, long pQuery, long pErrors);
	//static native XnRGB24Pixel* xnGetRGB24ImageMap(long hInstance);
	//static native XnYUV422DoublePixel* xnGetYUV422ImageMap(long hInstance);
	//static native XnGrayscale8Pixel* xnGetGrayscale8ImageMap(long hInstance);
	//static native XnGrayscale16Pixel* xnGetGrayscale16ImageMap(long hInstance);
	static native long xnGetImageMap(long hInstance);
	static native boolean xnIsPixelFormatSupported(long hInstance, int Format);
	static native int xnSetPixelFormat(long hInstance, int Format);
	static native int xnGetPixelFormat(long hInstance);
	static native int xnRegisterToPixelFormatChange(long hInstance, Object obj, String methodName, OutArg<Long> phCallback);
	static native void xnUnregisterFromPixelFormatChange(long hInstance, long hCallback);
	static native void xnGetImageMetaData(long hInstance, ImageMetaData pMetaData);

	// IR
	static native int xnCreateIRGenerator(long pContext, OutArg<Long> phIRGenerator, long pQuery, long pErrors);
	static native long xnGetIRMap(long hInstance);
	static native void xnGetIRMetaData(long hInstance, IRMetaData pMetaData);

	// Gestures
	static native int xnCreateGestureGenerator(long pContext, OutArg<Long> phGestureGenerator, long pQuery, long pErrors);
	static native int xnAddGesture(long hInstance, String strGesture);
	static native int xnAddGesture(long hInstance, String strGesture, float minx, float miny, float minz, float maxx, float maxy, float maxz);
	static native int xnRemoveGesture(long hInstance, String strGesture);
//	static native int xnGetActiveGestures(long hInstance, XnChar** pstrGestures, XnUInt16* nGestures);
	static native int xnGetAllActiveGestures(long hInstance, OutArg<String[]> gestures);
//	static native int xnEnumerateGestures(long hInstance, XnChar** pstrGestures, XnUInt16* nGestures);
	static native int xnGetNumberOfAvailableGestures(long hInstance);
	static native int xnEnumerateAllGestures(long hInstance, OutArg<String[]> gestures);
	static native boolean xnIsGestureAvailable(long hInstance, String strGesture);
	static native boolean xnIsGestureProgressSupported(long hInstance, String strGesture);
	static native int xnRegisterGestureCallbacks(long hInstance, Object obj, String gestureRecognizedCB, String gestureProgressCB, OutArg<Long> phCallback);
	static native void xnUnregisterGestureCallbacks(long hInstance, long hCallback);
	static native int xnRegisterToGestureChange(long hInstance, Object obj, String cb, OutArg<Long> phCallback);
	static native void xnUnregisterFromGestureChange(long hInstance, long hCallback);
	static native int xnRegisterToGestureIntermediateStageCompleted(long hInstance, Object obj, String cb, OutArg<Long> phCallback);
	static native void xnUnregisterFromGestureIntermediateStageCompleted(long hInstance, long hCallback);
	static native int xnRegisterToGestureReadyForNextIntermediateStage(long hInstance, Object obj, String cb, OutArg<Long> phCallback);
	static native void xnUnregisterFromGestureReadyForNextIntermediateStage(long hInstance, long hCallback);

	// Scene
	static native int xnCreateSceneAnalyzer(
			long pContext,
			OutArg<Long> phSceneAnalyzer,
			long pQuery, 
			long pErrors
			);
	static native long xnGetLabelMap(long hInstance);
	static native int xnGetFloor(long hInstance, OutArg<Point3D> normal, OutArg<Point3D> point);
	static native void xnGetSceneMetaData(long hInstance, SceneMetaData pMetaData);

	// User
	static native int xnCreateUserGenerator(
			long pContext,
			OutArg<Long> phUserGenerator,
			long pQuery, 
			long pErrors
			);
	static native int xnGetNumberOfUsers(long hInstance);
	static native int xnGetUsers(long hInstance, OutArg<Integer[]> ids);
	static native int xnGetUserCoM(long hInstance, int user, OutArg<Point3D> pCoM);
	static native int xnGetUserPixels(long hInstance, int user, SceneMetaData pScene);
	static native int xnRegisterUserCallbacks(long hInstance, Object obj, String newUserCb, String lostUserCB, OutArg<Long> phCallback);
	static native void xnUnregisterUserCallbacks(long hInstance, long hCallback);
	static native int xnRegisterToUserExit(long hInstance, Object obj, String cb, OutArg<Long> phCallback);
	static native void xnUnregisterFromUserExit(long hInstance, long hCallback);
	static native int xnRegisterToUserReEnter(long hInstance, Object obj, String cb, OutArg<Long> phCallback);
	static native void xnUnregisterFromUserReEnter(long hInstance, long hCallback);

	// Skeleton Capability
	static native boolean xnIsJointAvailable(long hInstance, int eJoint);
	static native boolean xnIsProfileAvailable(long hInstance, int eProfile);
	static native int xnSetSkeletonProfile(long hInstance, int  eProfile);
	static native int xnSetJointActive(long hInstance, int eJoint, boolean bState);
	static native boolean xnIsJointActive(long hInstance, int eJoint);
	static native int xnRegisterToJointConfigurationChange(long hInstance, Object obj, String cb, OutArg<Long> phCallback);
	static native void xnUnregisterFromJointConfigurationChange(long hInstance, long hCallback);
	static native int xnEnumerateActiveJoints(long hInstance, OutArg<Integer[]> pJoints);
	static native int xnGetSkeletonJoint(long hInstance, int user, int eJoint, OutArg<SkeletonJointTransformation> pJoint);
	static native int xnGetSkeletonJointPosition(long hInstance, int user, int eJoint, OutArg<SkeletonJointPosition> pJoint);
	static native int xnGetSkeletonJointOrientation(long hInstance, int user, int eJoint, OutArg<SkeletonJointOrientation> pJoint);
	static native boolean xnIsSkeletonTracking(long hInstance, int user);
	static native boolean xnIsSkeletonCalibrated(long hInstance, int user);
	static native boolean xnIsSkeletonCalibrating(long hInstance, int user);
	static native int xnRequestSkeletonCalibration(long hInstance, int user, boolean bForce);
	static native int xnAbortSkeletonCalibration(long hInstance, int user);
	static native int xnSaveSkeletonCalibrationDataToFile(long hInstance, int user, String strFileName);
	static native int xnLoadSkeletonCalibrationDataFromFile(long hInstance, int user, String strFileName);
	static native int xnSaveSkeletonCalibrationData(long hInstance, int user, int nSlot);
	static native int xnLoadSkeletonCalibrationData(long hInstance, int user, int nSlot);
	static native int xnClearSkeletonCalibrationData(long hInstance, int nSlot);
	static native boolean xnIsSkeletonCalibrationData(long hInstance, int nSlot);
	static native int xnStartSkeletonTracking(long hInstance, int user);
	static native int xnStopSkeletonTracking(long hInstance, int user);
	static native int xnResetSkeleton(long hInstance, int user);
	static native boolean xnNeedPoseForSkeletonCalibration(long hInstance);
	static native int xnGetSkeletonCalibrationPose(long hInstance, OutArg<String> strPose);
	static native int xnSetSkeletonSmoothing(long hInstance, float fFactor);
	static native int xnRegisterToCalibrationStart(long hInstance, Object obj, String calibrationStartCB, OutArg<Long> phCallback);
	static native void xnUnregisterFromCalibrationStart(long hInstnace, long hCallback);
	static native int xnRegisterToCalibrationInProgress(long hInstance, Object obj, String calibrationInProgressCB, OutArg<Long> phCallback);
	static native void xnUnregisterFromCalibrationInProgress(long hInstance, long hCallback);
	static native int xnRegisterToCalibrationComplete(long hInstance, Object obj, String calibrationCompleteCB, OutArg<Long> phCallback);
	static native void xnUnregisterFromCalibrationComplete(long hInstance, long hCallback);
	// Pose Detection
	static native int xnGetNumberOfPoses(long hInstance);
	static native int xnGetAllAvailablePoses(long hInstance, OutArg<String[]> pstrPoses);
	static native int xnStartPoseDetection(long hInstance, String strPose, int user);
	static native int xnStopPoseDetection(long hInstance, int user);
	static native int xnStopSinglePoseDetection(long hInstance, int user, String strPose);
	static native int xnRegisterToPoseDetected(long hInstance, Object obj, String cb, OutArg<Long> phCallback);
	static native void xnUnregisterFromPoseDetected(long hInstance, long hCallback);
	static native int xnRegisterToOutOfPose(long hInstance, Object obj, String cb, OutArg<Long> phCallback);
	static native void xnUnregisterFromOutOfPose(long hInstance, long hCallback);
	static native int xnRegisterToPoseDetectionInProgress(long hInstance, Object obj, String poseDetectionInProgressCB, OutArg<Long> phCallback);
	static native void xnUnregisterFromPoseDetectionInProgress(long hInstance, long hCallback);
	static native boolean xnIsPoseSupported(long hInstance, String strPose);
	static native int xnGetPoseStatus(long hInstance, int user, String strPose, OutArg<Long> poseTime, OutArg<Integer> eStatus, OutArg<Integer> eState);
	// Hands
	static native int xnCreateHandsGenerator(
			long pContext,
			OutArg<Long> phHandsGenerator,
			long pQuery, 
			long pErrors
			);
	static native int xnRegisterHandCallbacks(long hInstance, Object obj, String createCB, String updateCB, String destroyCB, OutArg<Long> phCallback);
	static native void xnUnregisterHandCallbacks(long hInstance, long hCallback);
	static native int xnStopTracking(long hInstance, int user); // XnUserID
	static native int xnStopTrackingAll(long hInstance);
	static native int xnStartTracking(long hInstance, float x, float y, float z);
	static native int xnSetTrackingSmoothing(long hInstance, float fFactor);
	static native int xnRegisterToHandTouchingFOVEdge(long hInstance, Object obj, String cb, OutArg<Long> phCallback);
	static native void xnUnregisterFromHandTouchingFOVEdge(long hInstance, long hCallback);

	// Audio
	static native int xnCreateAudioGenerator(
			long pContext,
			OutArg<Long> phAudioGenerator,
			long pQuery, 
			long pErrors
			);

	static native long xnGetAudioBuffer(long hInstance);
	static native int xnGetSupportedWaveOutputModesCount(long hInstance);
	static native int xnGetSupportedWaveOutputModes(long hInstance, WaveOutputMode[] aSupportedModes);
	static native int xnSetWaveOutputMode(long hInstance, int sampleRate, short bitsPerSample, byte numberOfChannels);
	static native int xnGetWaveOutputMode(long hInstance, OutArg<Integer> sampleRate, OutArg<Short> bitsPerSample, OutArg<Byte> numberOfChannels);
	static native int xnRegisterToWaveOutputModeChanges(long hInstance, Object obj, String methodName, OutArg<Long> phCallback);
	static native void xnUnregisterFromWaveOutputModeChanges(long hInstance, long hCallback);
	static native void xnGetAudioMetaData(long hInstance, AudioMetaData pMetaData);
/*
	// Mocks
	static native int xnMockDepthSetData(long hInstance, int nFrameID, long nTimestamp, int nDataSize, const XnDepthPixel* pData);
	static native int xnMockImageSetData(long hInstance, int nFrameID, long nTimestamp, int nDataSize, const XnUInt8* pData);
	static native int xnMockIRSetData(long hInstance, int nFrameID, long nTimestamp, int nDataSize, const XnIRPixel* pData);
	static native int xnMockAudioSetData(long hInstance, int nFrameID, long nTimestamp, int nDataSize, const XnUInt8* pData);
	static native int xnMockRawSetData(long hInstance, int nFrameID, long nTimestamp, int nDataSize, const void* pData);
*/
	// Codecs
	static native int xnCreateCodec(long pContext, int codecID, long hInitializerNode, OutArg<Long> phCodec);
	static native int xnGetCodecID(long hCodec);
	static native int xnEncodeData(long hCodec, long pSrc, int nSrcSize, 
								   long pDst, int nDstSize, OutArg<Integer> pnBytesWritten);
	static native int xnDecodeData(long hCodec, long pSrc, int nSrcSize, 
								   long pDst, int nDstSize, OutArg<Integer> pnBytesWritten);

	// Recorder
	static native int xnCreateRecorder(long pContext, String strFormatName, OutArg<Long> phRecorder);
	static native int xnSetRecorderDestination(long hRecorder, int destType, String strDest);
	static native int xnGetRecorderDestination(long hRecorder, OutArg<Integer> pSourceType, OutArg<String> strSource);
	static native int xnAddNodeToRecording(long hRecorder, long hNode, int compression);
	static native int xnRemoveNodeFromRecording(long hRecorder, long hNode);
	static native int xnRecord(long hRecorder);
	static native String xnGetRecorderFormat(long hRecorder);

	// Player
	static native int xnCreatePlayer(long pContext, String strFormatName, OutArg<Long> phPlayer);
	static native int xnSetPlayerRepeat(long hPlayer, boolean bRepeat);
	static native int xnSetPlayerSource(long hPlayer, int sourceType, String strSource);
	static native int xnGetPlayerSource(long hPlayer, OutArg<Integer> pSourceType, OutArg<String> strSource);
	static native int xnPlayerReadNext(long hPlayer);
	static native int xnSeekPlayerToTimeStamp(long hPlayer, long nTimeOffset, int origin);
	static native int xnSeekPlayerToFrame(long hPlayer, String strNodeName, int nFrameOffset, int origin);
	static native int xnTellPlayerTimestamp(long hPlayer, OutArg<Long> pnTimestamp);
	static native int xnTellPlayerFrame(long hPlayer, String strNodeName, OutArg<Integer> pnFrame);
	static native int xnGetPlayerNumFrames(long hPlayer, String strNodeName, OutArg<Integer> pnFrames);
	static native String xnGetPlayerSupportedFormat(long hPlayer);
	static native int xnEnumeratePlayerNodes(long hPlayer, OutArg<Long> ppList);
	static native boolean xnIsPlayerAtEOF(long hPlayer);
	static native int xnRegisterToEndOfFileReached(long hInstance, Object obj, String methodName, OutArg<Long> phCallback);
	static native void xnUnregisterFromEndOfFileReached(long hInstance, long hCallback);
	static native int xnSetPlaybackSpeed(long hInstance, double dSpeed);
	static native double xnGetPlaybackSpeed(long hInstance);

	// Script
	static native int xnCreateScriptNode(long pContext, String strFormat, OutArg<Long> phScript);
	static native String xnScriptNodeGetSupportedFormat(long hScript);
	static native int xnLoadScriptFromFile(long hScript, String strFileName);
	static native int xnLoadScriptFromString(long hScript, String strScript);
	static native int xnScriptNodeRun(long hScript, long pErrors);
	
	// Utils
	static native String xnProductionNodeTypeToString(int Type);
	
	static native int xnResolutionGetXRes(int resolution);
	static native int xnResolutionGetYRes(int resolution);
	static native int xnResolutionGetFromXYRes(int xRes, int yRes);
	static native int xnResolutionGetFromName(String strName);
	static native String xnResolutionGetName(int resolution);

	static native int xnGetVersion(OutArg<Version> pVersion);
	static native boolean xnIsTypeGenerator(int type);
	static native boolean xnIsTypeDerivedFrom(int type, int base);

	/*
	static native int xnProductionNodeTypeFromString(const XnChar* strType, XnProductionNodeType* pType);
	
	static native const XnChar* xnPixelFormatToString(XnPixelFormat format);
	static native int xnPixelFormatFromString(const XnChar* strName, XnPixelFormat* pFormat);
	static native XnUInt32 xnGetBytesPerPixelForPixelFormat(XnPixelFormat format);
	
	static native XnInt32 xnVersionCompare(const XnVersion* pVersion1, const XnVersion* pVersion2);

	static native int xnProductionNodeDescriptionToString(const XnProductionNodeDescription* pDescription, XnChar* csResult, XnUInt32 nSize);
	static native int xnVersionToString(const XnVersion* pVersion, XnChar* csResult, XnUInt32 nSize);
	*/
	
	static native int xnGetBytesPerPixelForPixelFormat(int format);
}
