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

public class SkeletonCapability extends CapabilityBase
{
	public SkeletonCapability(ProductionNode node) throws StatusException
	{
		super(node);
		
		// Events
		jointConfigurationChangeEvent = new StateChangedObservable()
		{
			@Override
			protected int registerNative(String cb, OutArg<Long> phCallback)
			{
				return NativeMethods.xnRegisterToJointConfigurationChange(toNative(), this, cb, phCallback);
			}
			@Override
			protected void unregisterNative(long hCallback)
			{
				NativeMethods.xnUnregisterFromJointConfigurationChange(toNative(), hCallback);
			}
		};
		calibrationStartEvent = new Observable<CalibrationStartEventArgs>()
		{
			@Override
			protected int registerNative(OutArg<Long> phCallback) throws StatusException 
			{
				return NativeMethods.xnRegisterToCalibrationStart(toNative(), this, "callback", phCallback);
			}

			@Override
			protected void unregisterNative(long hCallback) 
			{
				NativeMethods.xnUnregisterFromCalibrationStart(toNative(), hCallback);
			}
			
			@SuppressWarnings("unused")
			public void callback(int user)
			{
				notify(new CalibrationStartEventArgs(user));
			}
		};
		calibrationInProgressEvent = new Observable<CalibrationProgressEventArgs>()
		{
			@Override
			protected int registerNative(OutArg<Long> phCallback) throws StatusException 
			{
				return NativeMethods.xnRegisterToCalibrationInProgress(toNative(), this, "callback", phCallback);
			}

			@Override
			protected void unregisterNative(long hCallback) 
			{
				NativeMethods.xnUnregisterFromCalibrationInProgress(toNative(), hCallback);
			}
			
			@SuppressWarnings("unused")
			public void callback(int user, int state)
			{
				notify(new CalibrationProgressEventArgs(user, CalibrationProgressStatus.fromNative(state)));
			}
		};
		calibrationCompleteEvent = new Observable<CalibrationProgressEventArgs>()
		{
			@Override
			protected int registerNative(OutArg<Long> phCallback) throws StatusException 
			{
				return NativeMethods.xnRegisterToCalibrationComplete(toNative(), this, "callback", phCallback);
			}

			@Override
			protected void unregisterNative(long hCallback) 
			{
				NativeMethods.xnUnregisterFromCalibrationComplete(toNative(), hCallback);
			}
			
			@SuppressWarnings("unused")
			public void callback(int user, int state)
			{
				notify(new CalibrationProgressEventArgs(user, CalibrationProgressStatus.fromNative(state)));
			}
		};
	}

	// Joint / Profile
	public boolean isJointAvailable(SkeletonJoint joint)
	{
		return NativeMethods.xnIsJointAvailable(toNative(), joint.toNative());
	}
	public boolean isProfileAvailable(SkeletonProfile profile)
	{
		return NativeMethods.xnIsProfileAvailable(toNative(), profile.toNative());
	}
	public void setSkeletonProfile(SkeletonProfile profile) throws StatusException
	{
		int status = NativeMethods.xnSetSkeletonProfile(toNative(), profile.toNative());
		WrapperUtils.throwOnError(status);
	}
	public void setJointActive(SkeletonJoint joint, boolean active) throws StatusException
	{
		int status = NativeMethods.xnSetJointActive(toNative(), joint.toNative(), active);
		WrapperUtils.throwOnError(status);
	}
	public boolean isJointActive(SkeletonJoint joint)
	{
		return NativeMethods.xnIsJointActive(toNative(), joint.toNative());
	}
	public SkeletonJoint[] enumerateActiveJoints() throws StatusException
	{
		OutArg<Integer[]> nativeJoints = new OutArg<Integer[]>();
		int status = NativeMethods.xnEnumerateActiveJoints(toNative(), nativeJoints);
		WrapperUtils.throwOnError(status);
		SkeletonJoint[] joints = new SkeletonJoint[nativeJoints.value.length];
		for (int i = 0; i < nativeJoints.value.length; ++i)
		{
			joints[i] = SkeletonJoint.fromNative(nativeJoints.value[i]);
		}
		return joints;
	}
	// Get joint information
	public SkeletonJointTransformation getSkeletonJoint(int user, SkeletonJoint joint) throws StatusException
	{
		OutArg<SkeletonJointTransformation> transformation = new OutArg<SkeletonJointTransformation>();
		int status = NativeMethods.xnGetSkeletonJoint(toNative(), user, joint.toNative(), transformation);
		WrapperUtils.throwOnError(status);
		return transformation.value;
	}
	public SkeletonJointPosition getSkeletonJointPosition(int user, SkeletonJoint joint) throws StatusException
	{
		OutArg<SkeletonJointPosition> position = new OutArg<SkeletonJointPosition>();
		int status = NativeMethods.xnGetSkeletonJointPosition(toNative(), user, joint.toNative(), position);
		WrapperUtils.throwOnError(status);
		return position.value;
	}
	public SkeletonJointOrientation getSkeletonJointOrientation(int user, SkeletonJoint joint) throws StatusException
	{
		OutArg<SkeletonJointOrientation> orientation = new OutArg<SkeletonJointOrientation>();
		int status = NativeMethods.xnGetSkeletonJointOrientation(toNative(), user, joint.toNative(), orientation);
		WrapperUtils.throwOnError(status);
		return orientation.value;
	}
	// Check state
	public boolean isSkeletonTracking(int user)
	{
		return NativeMethods.xnIsSkeletonTracking(toNative(), user);
	}
	public boolean isSkeletonCalibrated(int user)
	{
		return NativeMethods.xnIsSkeletonCalibrated(toNative(), user);
	}
	public boolean isSkeletonCalibrating(int user)
	{
		return NativeMethods.xnIsSkeletonCalibrating(toNative(), user);
	}
	// Control calibration
	public void requestSkeletonCalibration(int user, boolean force) throws StatusException
	{
		int status = NativeMethods.xnRequestSkeletonCalibration(toNative(), user, force);
		WrapperUtils.throwOnError(status);
	}
	public void requestSkeletonCalibration(int user) throws StatusException
	{
		requestSkeletonCalibration(user, false);
	}
	public void abortSkeletonCalibration(int user) throws StatusException
	{
		int status = NativeMethods.xnAbortSkeletonCalibration(toNative(), user);
		WrapperUtils.throwOnError(status);
	}
	// Calibration data files
	public void saveSkeletonCalibrationDataToFile(int user, String fileName) throws StatusException
	{
		int status = NativeMethods.xnSaveSkeletonCalibrationDataToFile(toNative(), user, fileName);
		WrapperUtils.throwOnError(status);
	}
	public void loadSkeletonCalibrationDatadFromFile(int user, String fileName) throws StatusException
	{
		int status = NativeMethods.xnLoadSkeletonCalibrationDataFromFile(toNative(), user, fileName);
		WrapperUtils.throwOnError(status);
	}
	// Calibration data slots
	public void saveSkeletonCalibrationData(int user,  int slot) throws StatusException
	{
		int status = NativeMethods.xnSaveSkeletonCalibrationData(toNative(), user, slot);
		WrapperUtils.throwOnError(status);
	}
	public void loadSkeletonCalibrationData(int user, int slot) throws StatusException
	{
		int status = NativeMethods.xnLoadSkeletonCalibrationData(toNative(), user, slot);
		WrapperUtils.throwOnError(status);		
	}
	public void clearSkeletonCalibrationData(int slot) throws StatusException
	{
		int status = NativeMethods.xnClearSkeletonCalibrationData(toNative(), slot);
		WrapperUtils.throwOnError(status);
	}
	public boolean isSkeletonCalibrationData(int slot)
	{
		return NativeMethods.xnIsSkeletonCalibrationData(toNative(), slot);
	}

	// Tracking
	public void startTracking(int user) throws StatusException
	{
		int status = NativeMethods.xnStartSkeletonTracking(toNative(), user);
		WrapperUtils.throwOnError(status);
	}
	public void stopTracking(int user) throws StatusException
	{
		int status = NativeMethods.xnStopSkeletonTracking(toNative(), user);
		WrapperUtils.throwOnError(status);		
	}
	public void reset(int user) throws StatusException
	{
		int status = NativeMethods.xnResetSkeleton(toNative(), user);
		WrapperUtils.throwOnError(status);
	}
	
	// Pose
	public boolean needPoseForCalibration()
	{
		return NativeMethods.xnNeedPoseForSkeletonCalibration(toNative());
	}
	public String getSkeletonCalibrationPose() throws StatusException
	{
		OutArg<String> pose = new OutArg<String>();
		int status = NativeMethods.xnGetSkeletonCalibrationPose(toNative(),	pose);
		WrapperUtils.throwOnError(status);
		return pose.value;
	}
	
	// Misc
	public void setSmoothing(float factor) throws StatusException
	{
		int status = NativeMethods.xnSetSkeletonSmoothing(toNative(), factor);
		WrapperUtils.throwOnError(status);
	}
	
	// Events
	public IStateChangedObservable getJointConfigurationChangeEvent()
	{
		return jointConfigurationChangeEvent;
	}
	public IObservable<CalibrationStartEventArgs> getCalibrationStartEvent()
	{
		return calibrationStartEvent;
	}
	public IObservable<CalibrationProgressEventArgs> getCalibrationInProgressEvent()
	{
		return calibrationInProgressEvent;
	}
	public IObservable<CalibrationProgressEventArgs> getCalibrationCompleteEvent()
	{
		return calibrationCompleteEvent;
	}
	
	private StateChangedObservable jointConfigurationChangeEvent;
	private Observable<CalibrationStartEventArgs> calibrationStartEvent;
	private Observable<CalibrationProgressEventArgs> calibrationInProgressEvent;
	private Observable<CalibrationProgressEventArgs> calibrationCompleteEvent;
}
