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
package geogebra.plugin.kinect;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.main.AppD;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import org.OpenNI.ActiveHandEventArgs;
import org.OpenNI.CalibrationProgressEventArgs;
import org.OpenNI.CalibrationProgressStatus;
import org.OpenNI.Context;
import org.OpenNI.DepthGenerator;
import org.OpenNI.DepthMetaData;
import org.OpenNI.GeneralException;
import org.OpenNI.HandsGenerator;
import org.OpenNI.IObservable;
import org.OpenNI.IObserver;
import org.OpenNI.InactiveHandEventArgs;
import org.OpenNI.OutArg;
import org.OpenNI.Point3D;
import org.OpenNI.PoseDetectionCapability;
import org.OpenNI.PoseDetectionEventArgs;
import org.OpenNI.SceneMetaData;
import org.OpenNI.ScriptNode;
import org.OpenNI.SkeletonCapability;
import org.OpenNI.SkeletonJoint;
import org.OpenNI.SkeletonJointPosition;
import org.OpenNI.SkeletonProfile;
import org.OpenNI.StatusException;
import org.OpenNI.UserEventArgs;
import org.OpenNI.UserGenerator;

public class KinectTest extends Component {
	class NewUserObserver implements IObserver<UserEventArgs>
	{
		public void update(IObservable<UserEventArgs> observable,
				UserEventArgs args)
		{
			System.out.println("New user " + args.getId());
			try
			{
				if (skeletonCap.needPoseForCalibration())
				{
					poseDetectionCap.startPoseDetection(calibPose, args.getId());
				}
				else
				{
					skeletonCap.requestSkeletonCalibration(args.getId(), true);
				}
			} catch (StatusException e)
			{
				e.printStackTrace();
			}
		}
	}
	class LostUserObserver implements IObserver<UserEventArgs>
	{
		public void update(IObservable<UserEventArgs> observable,
				UserEventArgs args)
		{
			System.out.println("Lost user " + args.getId());
			joints.remove(args.getId());
		}
	}
	
	class CalibrationCompleteObserver implements IObserver<CalibrationProgressEventArgs>
	{
		public void update(IObservable<CalibrationProgressEventArgs> observable,
				CalibrationProgressEventArgs args)
		{
			System.out.println("Calibraion complete: " + args.getStatus());
			try
			{
			if (args.getStatus() == CalibrationProgressStatus.OK)
			{
				System.out.println("starting tracking "  +args.getUser());
					skeletonCap.startTracking(args.getUser());
	                joints.put(new Integer(args.getUser()), new HashMap<SkeletonJoint, SkeletonJointPosition>());
			}
			else if (args.getStatus() != CalibrationProgressStatus.MANUAL_ABORT)
			{
				if (skeletonCap.needPoseForCalibration())
				{
					poseDetectionCap.startPoseDetection(calibPose, args.getUser());
				}
				else
				{
					skeletonCap.requestSkeletonCalibration(args.getUser(), true);
				}
			}
			} catch (StatusException e)
			{
				e.printStackTrace();
			}
		}
	}
	class PoseDetectedObserver implements IObserver<PoseDetectionEventArgs>
	{
		public void update(IObservable<PoseDetectionEventArgs> observable,
				PoseDetectionEventArgs args)
		{
			System.out.println("Pose " + args.getPose() + " detected for " + args.getUser());
			try
			{
				poseDetectionCap.stopPoseDetection(args.getUser());
				skeletonCap.requestSkeletonCalibration(args.getUser(), true);
			} catch (StatusException e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final float YMAX = 200;
	private OutArg<ScriptNode> scriptNode;
	private Context context;
	private DepthGenerator depthGen;
	private UserGenerator userGen;
	private SkeletonCapability skeletonCap;
	private PoseDetectionCapability poseDetectionCap;
	private byte[] imgbytes;
	private float histogram[];
	String calibPose = null;
	HashMap<Integer, HashMap<SkeletonJoint, SkeletonJointPosition>> joints;

	private final boolean drawBackground = true;
	private final boolean drawPixels = true;
	private final boolean drawSkeleton = true;
	private final boolean printID = true;
	private final boolean printState = true;

	private BufferedImage bimg;
	int width, height;

	//private final String SAMPLE_XML_FILE = "geogebra/plugin/kinect/SamplesConfig.xml";
	private final Kernel kernel;

	public KinectTest(Kernel kernel) {

		this.kernel = kernel;

		HandsGenerator handsGen;

		try {
			scriptNode = new OutArg<ScriptNode>();
			context = new Context(); //Context.createFromXmlFile(SAMPLE_XML_FILE, scriptNode);
			/*
			 * handsGen = HandsGenerator.create(context);
			 * handsGen.getHandCreateEvent().addObserver(new
			 * MyHandCreateEvent());
			 * handsGen.getHandUpdateEvent().addObserver(new
			 * MyHandUpdateEvent()); handsGen.getHandDestroyEvent()
			 * .addObserver(new MyHandDestroyEvent());
			 */

			depthGen = DepthGenerator.create(context);
			DepthMetaData depthMD = depthGen.getMetaData();

			histogram = new float[10000];
			width = depthMD.getFullXRes();
			height = depthMD.getFullYRes();

			imgbytes = new byte[width * height * 3];

			userGen = UserGenerator.create(context);
			skeletonCap = userGen.getSkeletonCapability();
			poseDetectionCap = userGen.getPoseDetectionCapability();

			userGen.getNewUserEvent().addObserver(new NewUserObserver());
			userGen.getLostUserEvent().addObserver(new LostUserObserver());
			skeletonCap.getCalibrationCompleteEvent().addObserver(
					new CalibrationCompleteObserver());
			poseDetectionCap.getPoseDetectedEvent().addObserver(
					new PoseDetectedObserver());

			calibPose = skeletonCap.getSkeletonCalibrationPose();
			joints = new HashMap<Integer, HashMap<SkeletonJoint, SkeletonJointPosition>>();

			skeletonCap.setSkeletonProfile(SkeletonProfile.ALL);

			context.startGeneratingAll();
		} catch (GeneralException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	private void calcHist(ShortBuffer depth) {
		// reset
		for (int i = 0; i < histogram.length; ++i) {
			histogram[i] = 0;
		}

		depth.rewind();

		int points = 0;
		while (depth.remaining() > 0) {
			short depthVal = depth.get();
			if (depthVal != 0) {
				histogram[depthVal]++;
				points++;
			}
		}

		for (int i = 1; i < histogram.length; i++) {
			histogram[i] += histogram[i - 1];
		}

		if (points > 0) {
			for (int i = 1; i < histogram.length; i++) {
				histogram[i] = 1.0f - (histogram[i] / points);
			}
		}
	}

	void updateDepth() {
		try {

			context.waitAnyUpdateAll();

			DepthMetaData depthMD = depthGen.getMetaData();
			SceneMetaData sceneMD = userGen.getUserPixels(0);

			ShortBuffer scene = sceneMD.getData().createShortBuffer();
			ShortBuffer depth = depthMD.getData().createShortBuffer();
			calcHist(depth);
			depth.rewind();

			while (depth.remaining() > 0) {
				int pos = depth.position();
				short pixel = depth.get();
				short user = scene.get();

				imgbytes[3 * pos] = 0;
				imgbytes[(3 * pos) + 1] = 0;
				imgbytes[(3 * pos) + 2] = 0;

				if (drawBackground || (pixel != 0)) {
					int colorID = user % (colors.length - 1);
					if (user == 0) {
						colorID = colors.length - 1;
					}
					if (pixel != 0) {
						float histValue = histogram[pixel];
						imgbytes[3 * pos] = (byte) (histValue * colors[colorID]
								.getRed());
						imgbytes[(3 * pos) + 1] = (byte) (histValue * colors[colorID]
								.getGreen());
						imgbytes[(3 * pos) + 2] = (byte) (histValue * colors[colorID]
								.getBlue());
					}
				}
			}
		} catch (GeneralException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(width, height);
	}

	Color colors[] = { Color.RED, Color.BLUE, Color.CYAN, Color.GREEN,
			Color.MAGENTA, Color.PINK, Color.YELLOW, Color.WHITE };

	public void getJoint(int user, SkeletonJoint joint) throws StatusException {
		SkeletonJointPosition pos = skeletonCap.getSkeletonJointPosition(user,
				joint);
		if (pos.getPosition().getZ() != 0) {
			joints.get(user).put(
					joint,
					new SkeletonJointPosition(depthGen
							.convertRealWorldToProjective(pos.getPosition()),
							pos.getConfidence()));
		} else {
			joints.get(user).put(joint,
					new SkeletonJointPosition(new Point3D(), 0));
		}
	}

	public void getJoints(int user) throws StatusException {
		getJoint(user, SkeletonJoint.HEAD);
		getJoint(user, SkeletonJoint.NECK);

		getJoint(user, SkeletonJoint.LEFT_SHOULDER);
		getJoint(user, SkeletonJoint.LEFT_ELBOW);
		getJoint(user, SkeletonJoint.LEFT_HAND);

		getJoint(user, SkeletonJoint.RIGHT_SHOULDER);
		getJoint(user, SkeletonJoint.RIGHT_ELBOW);
		getJoint(user, SkeletonJoint.RIGHT_HAND);

		getJoint(user, SkeletonJoint.TORSO);

		getJoint(user, SkeletonJoint.LEFT_HIP);
		getJoint(user, SkeletonJoint.LEFT_KNEE);
		getJoint(user, SkeletonJoint.LEFT_FOOT);

		getJoint(user, SkeletonJoint.RIGHT_HIP);
		getJoint(user, SkeletonJoint.RIGHT_KNEE);
		getJoint(user, SkeletonJoint.RIGHT_FOOT);

	}

	void drawLine(Graphics g,
			HashMap<SkeletonJoint, SkeletonJointPosition> jointHash,
			SkeletonJoint joint1, SkeletonJoint joint2) {
		Point3D pos1 = jointHash.get(joint1).getPosition();
		Point3D pos2 = jointHash.get(joint2).getPosition();

		if ((jointHash.get(joint1).getConfidence() == 0)
				|| (jointHash.get(joint1).getConfidence() == 0)) {
			return;
		}

		g.drawLine((int) pos1.getX(), (int) pos1.getY(), (int) pos2.getX(),
				(int) pos2.getY());
	}

	GeoNumeric xHEAD, xNECK, xTORSO, xWAIST, xLEFT_COLLAR, xLEFT_SHOULDER,
	xLEFT_ELBOW, xLEFT_WRIST, xLEFT_HAND, xLEFT_FINGER_TIP, xRIGHT_COLLAR,
	xRIGHT_SHOULDER, xRIGHT_ELBOW, xRIGHT_WRIST, xRIGHT_HAND,
	xRIGHT_FINGER_TIP, xLEFT_HIP, xLEFT_KNEE, xLEFT_ANKLE, xLEFT_FOOT,
	xRIGHT_HIP, xRIGHT_KNEE, xRIGHT_ANKLE, xRIGHT_FOOT;
	GeoNumeric yHEAD, yNECK, yTORSO, yWAIST, yLEFT_COLLAR, yLEFT_SHOULDER,
	yLEFT_ELBOW, yLEFT_WRIST, yLEFT_HAND, yLEFT_FINGER_TIP, yRIGHT_COLLAR,
	yRIGHT_SHOULDER, yRIGHT_ELBOW, yRIGHT_WRIST, yRIGHT_HAND,
	yRIGHT_FINGER_TIP, yLEFT_HIP, yLEFT_KNEE, yLEFT_ANKLE, yLEFT_FOOT,
	yRIGHT_HIP, yRIGHT_KNEE, yRIGHT_ANKLE, yRIGHT_FOOT;
	GeoNumeric zHEAD, zNECK, zTORSO, zWAIST, zLEFT_COLLAR, zLEFT_SHOULDER,
	zLEFT_ELBOW, zLEFT_WRIST, zLEFT_HAND, zLEFT_FINGER_TIP, zRIGHT_COLLAR,
	zRIGHT_SHOULDER, zRIGHT_ELBOW, zRIGHT_WRIST, zRIGHT_HAND,
	zRIGHT_FINGER_TIP, zLEFT_HIP, zLEFT_KNEE, zLEFT_ANKLE, zLEFT_FOOT,
	zRIGHT_HIP, zRIGHT_KNEE, zRIGHT_ANKLE, zRIGHT_FOOT;

	public void drawSkeleton(Graphics g, int user) throws StatusException {
		getJoints(user);
		HashMap<SkeletonJoint, SkeletonJointPosition> dict = joints
				.get(new Integer(user));

		drawLine(g, dict, SkeletonJoint.HEAD, SkeletonJoint.NECK);

		drawLine(g, dict, SkeletonJoint.LEFT_SHOULDER, SkeletonJoint.TORSO);
		drawLine(g, dict, SkeletonJoint.RIGHT_SHOULDER, SkeletonJoint.TORSO);

		drawLine(g, dict, SkeletonJoint.NECK, SkeletonJoint.LEFT_SHOULDER);
		drawLine(g, dict, SkeletonJoint.LEFT_SHOULDER, SkeletonJoint.LEFT_ELBOW);
		drawLine(g, dict, SkeletonJoint.LEFT_ELBOW, SkeletonJoint.LEFT_HAND);

		drawLine(g, dict, SkeletonJoint.NECK, SkeletonJoint.RIGHT_SHOULDER);
		drawLine(g, dict, SkeletonJoint.RIGHT_SHOULDER,
				SkeletonJoint.RIGHT_ELBOW);
		drawLine(g, dict, SkeletonJoint.RIGHT_ELBOW, SkeletonJoint.RIGHT_HAND);

		drawLine(g, dict, SkeletonJoint.LEFT_HIP, SkeletonJoint.TORSO);
		drawLine(g, dict, SkeletonJoint.RIGHT_HIP, SkeletonJoint.TORSO);
		drawLine(g, dict, SkeletonJoint.LEFT_HIP, SkeletonJoint.RIGHT_HIP);

		drawLine(g, dict, SkeletonJoint.LEFT_HIP, SkeletonJoint.LEFT_KNEE);
		drawLine(g, dict, SkeletonJoint.LEFT_KNEE, SkeletonJoint.LEFT_FOOT);

		drawLine(g, dict, SkeletonJoint.RIGHT_HIP, SkeletonJoint.RIGHT_KNEE);
		drawLine(g, dict, SkeletonJoint.RIGHT_KNEE, SkeletonJoint.RIGHT_FOOT);

		// GeoPoint3D p = (GeoPoint3D) kernel.lookupLabel("A");
		if (xHEAD == null) {
			xHEAD = getGeoPoint("xHEAD");
			xNECK = getGeoPoint("xNECK");
			xTORSO = getGeoPoint("xTORSO");
			xLEFT_SHOULDER = getGeoPoint("xLEFT_SHOULDER");
			xLEFT_ELBOW = getGeoPoint("xLEFT_ELBOW");
			xLEFT_HAND = getGeoPoint("xLEFT_HAND");
			xRIGHT_SHOULDER = getGeoPoint("xRIGHT_SHOULDER");
			xRIGHT_ELBOW = getGeoPoint("xRIGHT_ELBOW");
			xRIGHT_HAND = getGeoPoint("xRIGHT_HAND");
			xLEFT_HIP = getGeoPoint("xLEFT_HIP");
			xLEFT_KNEE = getGeoPoint("xLEFT_KNEE");
			xLEFT_FOOT = getGeoPoint("xLEFT_FOOT");
			xRIGHT_HIP = getGeoPoint("xRIGHT_HIP");
			xRIGHT_KNEE = getGeoPoint("xRIGHT_KNEE");
			xRIGHT_FOOT = getGeoPoint("xRIGHT_FOOT");

			yHEAD = getGeoPoint("yHEAD");
			yNECK = getGeoPoint("yNECK");
			yTORSO = getGeoPoint("yTORSO");
			yLEFT_SHOULDER = getGeoPoint("yLEFT_SHOULDER");
			yLEFT_ELBOW = getGeoPoint("yLEFT_ELBOW");
			yLEFT_HAND = getGeoPoint("yLEFT_HAND");
			yRIGHT_SHOULDER = getGeoPoint("yRIGHT_SHOULDER");
			yRIGHT_ELBOW = getGeoPoint("yRIGHT_ELBOW");
			yRIGHT_HAND = getGeoPoint("yRIGHT_HAND");
			yLEFT_HIP = getGeoPoint("yLEFT_HIP");
			yLEFT_KNEE = getGeoPoint("yLEFT_KNEE");
			yLEFT_FOOT = getGeoPoint("yLEFT_FOOT");
			yRIGHT_HIP = getGeoPoint("yRIGHT_HIP");
			yRIGHT_KNEE = getGeoPoint("yRIGHT_KNEE");
			yRIGHT_FOOT = getGeoPoint("yRIGHT_FOOT");

			zHEAD = getGeoPoint("zHEAD");
			zNECK = getGeoPoint("zNECK");
			zTORSO = getGeoPoint("zTORSO");
			zLEFT_SHOULDER = getGeoPoint("zLEFT_SHOULDER");
			zLEFT_ELBOW = getGeoPoint("zLEFT_ELBOW");
			zLEFT_HAND = getGeoPoint("zLEFT_HAND");
			zRIGHT_SHOULDER = getGeoPoint("zRIGHT_SHOULDER");
			zRIGHT_ELBOW = getGeoPoint("zRIGHT_ELBOW");
			zRIGHT_HAND = getGeoPoint("zRIGHT_HAND");
			zLEFT_HIP = getGeoPoint("zLEFT_HIP");
			zLEFT_KNEE = getGeoPoint("zLEFT_KNEE");
			zLEFT_FOOT = getGeoPoint("zLEFT_FOOT");
			zRIGHT_HIP = getGeoPoint("zRIGHT_HIP");
			zRIGHT_KNEE = getGeoPoint("zRIGHT_KNEE");
			zRIGHT_FOOT = getGeoPoint("zRIGHT_FOOT");

			numberList = new ArrayList<GeoNumeric>();
			numberList.add(xHEAD);
			numberList.add(xNECK);
			numberList.add(xTORSO);
			numberList.add(xLEFT_SHOULDER);
			numberList.add(xLEFT_ELBOW);
			numberList.add(xLEFT_HAND);
			numberList.add(xRIGHT_SHOULDER);
			numberList.add(xRIGHT_ELBOW);
			numberList.add(xRIGHT_HAND);
			numberList.add(xLEFT_HIP);
			numberList.add(xLEFT_KNEE);
			numberList.add(xLEFT_FOOT);
			numberList.add(xRIGHT_HIP);
			numberList.add(xRIGHT_KNEE);
			numberList.add(xRIGHT_FOOT);

			numberList.add(yHEAD);
			numberList.add(yNECK);
			numberList.add(yTORSO);
			numberList.add(yLEFT_SHOULDER);
			numberList.add(yLEFT_ELBOW);
			numberList.add(yLEFT_HAND);
			numberList.add(yRIGHT_SHOULDER);
			numberList.add(yRIGHT_ELBOW);
			numberList.add(yRIGHT_HAND);
			numberList.add(yLEFT_HIP);
			numberList.add(yLEFT_KNEE);
			numberList.add(yLEFT_FOOT);
			numberList.add(yRIGHT_HIP);
			numberList.add(yRIGHT_KNEE);
			numberList.add(yRIGHT_FOOT);
			
			numberList.add(zHEAD);
			numberList.add(zNECK);
			numberList.add(zTORSO);
			numberList.add(zLEFT_SHOULDER);
			numberList.add(zLEFT_ELBOW);
			numberList.add(zLEFT_HAND);
			numberList.add(zRIGHT_SHOULDER);
			numberList.add(zRIGHT_ELBOW);
			numberList.add(zRIGHT_HAND);
			numberList.add(zLEFT_HIP);
			numberList.add(zLEFT_KNEE);
			numberList.add(zLEFT_FOOT);
			numberList.add(zRIGHT_HIP);
			numberList.add(zRIGHT_KNEE);
			numberList.add(zRIGHT_FOOT);

			// for (int i = 0; i < pointList.size(); i++) {
			// if (pointList.get(i) == null) {
			// Application.debug(i);
			// }

			// }

		}

		Point3D pos1 = dict.get(SkeletonJoint.RIGHT_HAND).getPosition();
		if (xRIGHT_HAND != null) {
			xRIGHT_HAND.setValue(pos1.getX());
			yRIGHT_HAND.setValue(YMAX - pos1.getY());
			zRIGHT_HAND.setValue(pos1.getZ());
		}

		pos1 = dict.get(SkeletonJoint.HEAD).getPosition();
		if (xHEAD != null) {
			xHEAD.setValue(pos1.getX());
			yHEAD.setValue(YMAX - pos1.getY());
			zHEAD.setValue(pos1.getZ());
		}

		pos1 = dict.get(SkeletonJoint.NECK).getPosition();
		if (xNECK != null) {
			xNECK.setValue(pos1.getX());
			yNECK.setValue(YMAX - pos1.getY());
			zNECK.setValue(pos1.getZ());
		}

		// pos1 = dict.get(SkeletonJoint.WAIST).getPosition();
		// if (WAIST != null) {
		// WAIST.setCoords(pos1.getX(), 1000 - pos1.getY(), 1.0);
		// }

		// pos1 = dict.get(SkeletonJoint.LEFT_COLLAR).getPosition();
		// if (LEFT_COLLAR != null) {
		// LEFT_COLLAR.setCoords(pos1.getX(), 1000 - pos1.getY(), 1.0);
		// }

		pos1 = dict.get(SkeletonJoint.LEFT_SHOULDER).getPosition();
		if (xLEFT_SHOULDER != null) {
			xLEFT_SHOULDER.setValue(pos1.getX());
			yLEFT_SHOULDER.setValue(YMAX - pos1.getY());
			zLEFT_SHOULDER.setValue(pos1.getZ());
		}

		pos1 = dict.get(SkeletonJoint.LEFT_ELBOW).getPosition();
		if (xLEFT_ELBOW != null) {
			xLEFT_ELBOW.setValue(pos1.getX());
			yLEFT_ELBOW.setValue(YMAX - pos1.getY());
			zLEFT_ELBOW.setValue(pos1.getZ());
		}

		pos1 = dict.get(SkeletonJoint.TORSO).getPosition();
		if (xTORSO != null) {
			xTORSO.setValue(pos1.getX());
			yTORSO.setValue(YMAX - pos1.getY());
			zTORSO.setValue(pos1.getZ());
		}

		// pos1 = dict.get(SkeletonJoint.LEFT_WRIST).getPosition();
		// if (LEFT_WRIST != null) {
		// LEFT_WRIST.setCoords(pos1.getX(), 1000 - pos1.getY(), 1.0);
		// }

		pos1 = dict.get(SkeletonJoint.LEFT_HAND).getPosition();
		if (xLEFT_HAND != null) {
			xLEFT_HAND.setValue(pos1.getX());
			yLEFT_HAND.setValue(YMAX - pos1.getY());
			zLEFT_HAND.setValue(pos1.getZ());
		}

		pos1 = dict.get(SkeletonJoint.LEFT_FOOT).getPosition();
		if (xLEFT_FOOT != null) {
			xLEFT_FOOT.setValue(pos1.getX());
			yLEFT_FOOT.setValue(YMAX - pos1.getY());
			zLEFT_FOOT.setValue(pos1.getZ());
		}

		pos1 = dict.get(SkeletonJoint.RIGHT_FOOT).getPosition();
		if (xRIGHT_FOOT != null) {
			xRIGHT_FOOT.setValue(pos1.getX());
			yRIGHT_FOOT.setValue(YMAX - pos1.getY());
			zRIGHT_FOOT.setValue(pos1.getZ());
		}

		pos1 = dict.get(SkeletonJoint.RIGHT_SHOULDER).getPosition();
		if (xRIGHT_SHOULDER != null) {
			xRIGHT_SHOULDER.setValue(pos1.getX());
			yRIGHT_SHOULDER.setValue(YMAX - pos1.getY());
			zRIGHT_SHOULDER.setValue(pos1.getZ());
		}

		pos1 = dict.get(SkeletonJoint.RIGHT_ELBOW).getPosition();
		if (xRIGHT_ELBOW != null) {
			xRIGHT_ELBOW.setValue(pos1.getX());
			yRIGHT_ELBOW.setValue(YMAX - pos1.getY());
			zRIGHT_ELBOW.setValue(pos1.getZ());
		}

		pos1 = dict.get(SkeletonJoint.LEFT_HIP).getPosition();
		if (xLEFT_HIP != null) {
			xLEFT_HIP.setValue(pos1.getX());
			yLEFT_HIP.setValue(YMAX - pos1.getY());
			zLEFT_HIP.setValue(pos1.getZ());
		}

		pos1 = dict.get(SkeletonJoint.LEFT_KNEE).getPosition();
		if (xLEFT_KNEE != null) {
			xLEFT_KNEE.setValue(pos1.getX());
			yLEFT_KNEE.setValue(YMAX - pos1.getY());
			zLEFT_KNEE.setValue(pos1.getZ());
		}

		pos1 = dict.get(SkeletonJoint.RIGHT_HIP).getPosition();
		if (xRIGHT_HIP != null) {
			xRIGHT_HIP.setValue(pos1.getX());
			yRIGHT_HIP.setValue(YMAX - pos1.getY());
			zRIGHT_HIP.setValue(pos1.getZ());
		}

		pos1 = dict.get(SkeletonJoint.RIGHT_KNEE).getPosition();
		if (xRIGHT_KNEE != null) {
			xRIGHT_KNEE.setValue(pos1.getX());
			yRIGHT_KNEE.setValue(YMAX - pos1.getY());
			zRIGHT_KNEE.setValue(pos1.getZ());
		}

		GeoElement.updateCascade(numberList, getTempSet(), false);
		kernel.notifyRepaint();

	}

	private GeoNumeric getGeoPoint(String label) {
		GeoElement ret = kernel.lookupLabel(label);
		
		if (ret == null || !(ret.isGeoNumeric())) {
			ret = new GeoNumeric(kernel.getConstruction(), label, 0);
		}
		return (GeoNumeric) ret;
	}

	private ArrayList<GeoNumeric> numberList;
	private TreeSet<AlgoElement> tempSet;

	private TreeSet<AlgoElement> getTempSet() {
		if (tempSet == null) {
			tempSet = new TreeSet<AlgoElement>();
		}
		return tempSet;
	}

	@Override
	public void paint(Graphics g) {
		if (drawPixels) {
			DataBufferByte dataBuffer = new DataBufferByte(imgbytes, width
					* height * 3);

			WritableRaster raster = Raster.createInterleavedRaster(dataBuffer,
					width, height, width * 3, 3, new int[] { 0, 1, 2 }, null);

			ColorModel colorModel = new ComponentColorModel(
					ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[] { 8,
							8, 8 }, false, false, ComponentColorModel.OPAQUE,
					DataBuffer.TYPE_BYTE);

			bimg = new BufferedImage(colorModel, raster, false, null);

			g.drawImage(bimg, 0, 0, null);
		}
		try {
			int[] users = userGen.getUsers();
			for (int i = 0; i < users.length; ++i) {
				Color c = colors[users[i] % colors.length];
				c = new Color(255 - c.getRed(), 255 - c.getGreen(),
						255 - c.getBlue());

				g.setColor(c);
				if (drawSkeleton && skeletonCap.isSkeletonTracking(users[i])) {
					drawSkeleton(g, users[i]);
				}

				if (printID) {
					Point3D com = depthGen.convertRealWorldToProjective(userGen
							.getUserCoM(users[i]));
					String label = null;
					if (!printState) {
						label = new String("" + users[i]);
					} else if (skeletonCap.isSkeletonTracking(users[i])) {
						// Tracking
						label = new String(users[i] + " - Tracking");
					} else if (skeletonCap.isSkeletonCalibrating(users[i])) {
						// Calibrating
						label = new String(users[i] + " - Calibrating");
					} else {
						// Nothing
						label = new String(users[i] + " - Looking for pose ("
								+ calibPose + ")");
					}

					g.drawString(label, (int) com.getX(), (int) com.getY());
				}
			}
		} catch (StatusException e) {
			e.printStackTrace();
		}
	}

	class MyHandCreateEvent implements IObserver<ActiveHandEventArgs> {
		public void update(IObservable<ActiveHandEventArgs> observable,
				ActiveHandEventArgs args) {
			AppD.debug("");
			// ArrayList<Point3D> newList = new ArrayList<Point3D>();
			// newList.add(args.getPosition());
			// history.put(new Integer(args.getId()), newList);
		}
	}

	class MyHandUpdateEvent implements IObserver<ActiveHandEventArgs> {
		public void update(IObservable<ActiveHandEventArgs> observable,
				ActiveHandEventArgs args) {
			// ArrayList<Point3D> historyList = history.get(args.getId());
			AppD.debug("");

			// historyList.add(args.getPosition());

			// while (historyList.size() > historySize) {
			// historyList.remove(0);
			// }

		}
	}

	class MyHandDestroyEvent implements IObserver<InactiveHandEventArgs> {
		public void update(IObservable<InactiveHandEventArgs> observable,
				InactiveHandEventArgs args) {
			AppD.debug("");
			// history.remove(args.getId());
			// if (history.isEmpty()) {
			// try {
			// gestureGen.addGesture("Click");
			// } catch (StatusException e) {
			// e.printStackTrace();
			// }
			// }
		}
	}
}
