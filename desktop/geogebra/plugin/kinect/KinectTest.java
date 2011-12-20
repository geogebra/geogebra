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

import geogebra.common.kernel.algos.AlgoElementInterface;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.main.Application;
import geogebra3D.kernel3D.GeoPoint3D;

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
	class NewUserObserver implements IObserver<UserEventArgs> {
		public void update(IObservable<UserEventArgs> observable,
				UserEventArgs args) {
			System.out.println("New user " + args.getId());
			try {
				poseDetectionCap.StartPoseDetection(calibPose, args.getId());
			} catch (StatusException e) {
				e.printStackTrace();
			}
		}
	}

	class LostUserObserver implements IObserver<UserEventArgs> {
		public void update(IObservable<UserEventArgs> observable,
				UserEventArgs args) {
			System.out.println("Lost use " + args.getId());
			joints.remove(args.getId());
		}
	}

	class CalibrationCompleteObserver implements
			IObserver<CalibrationProgressEventArgs> {
		public void update(
				IObservable<CalibrationProgressEventArgs> observable,
				CalibrationProgressEventArgs args) {
			System.out.println("Calibraion complete: " + args.getStatus());
			try {
				if (args.getStatus() == CalibrationProgressStatus.OK) {
					System.out.println("starting tracking " + args.getUser());
					skeletonCap.startTracking(args.getUser());
					joints.put(new Integer(args.getUser()),
							new HashMap<SkeletonJoint, SkeletonJointPosition>());
				} else {
					poseDetectionCap.StartPoseDetection(calibPose,
							args.getUser());
				}
			} catch (StatusException e) {
				e.printStackTrace();
			}
		}
	}

	class PoseDetectedObserver implements IObserver<PoseDetectionEventArgs> {
		public void update(IObservable<PoseDetectionEventArgs> observable,
				PoseDetectionEventArgs args) {
			System.out.println("Pose " + args.getPose() + " detected for "
					+ args.getUser());
			try {
				poseDetectionCap.StopPoseDetection(args.getUser());
				skeletonCap.requestSkeletonCalibration(args.getUser(), true);
			} catch (StatusException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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

	private final String SAMPLE_XML_FILE = "geogebra/plugin/kinect/SamplesConfig.xml";
	private final Kernel kernel;

	public KinectTest(Kernel kernel) {

		this.kernel = kernel;

		HandsGenerator handsGen;

		try {
			scriptNode = new OutArg<ScriptNode>();
			context = Context.createFromXmlFile(SAMPLE_XML_FILE, scriptNode);
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

	GeoPoint3D HEAD, NECK, TORSO, WAIST, LEFT_COLLAR, LEFT_SHOULDER,
			LEFT_ELBOW, LEFT_WRIST, LEFT_HAND, LEFT_FINGER_TIP, RIGHT_COLLAR,
			RIGHT_SHOULDER, RIGHT_ELBOW, RIGHT_WRIST, RIGHT_HAND,
			RIGHT_FINGER_TIP, LEFT_HIP, LEFT_KNEE, LEFT_ANKLE, LEFT_FOOT,
			RIGHT_HIP, RIGHT_KNEE, RIGHT_ANKLE, RIGHT_FOOT;

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
		if (HEAD == null) {
			HEAD = (GeoPoint3D) kernel.lookupLabel("HEAD");
		}
		if (NECK == null) {
			NECK = (GeoPoint3D) kernel.lookupLabel("NECK");
		}
		if (TORSO == null) {
			TORSO = (GeoPoint3D) kernel.lookupLabel("TORSO");
		}
		if (WAIST == null) {
			WAIST = (GeoPoint3D) kernel.lookupLabel("WAIST");
		}
		if (LEFT_COLLAR == null) {
			LEFT_COLLAR = (GeoPoint3D) kernel.lookupLabel("LEFT_COLLAR");
		}
		if (LEFT_SHOULDER == null) {
			LEFT_SHOULDER = (GeoPoint3D) kernel.lookupLabel("LEFT_SHOULDER");
		}
		if (LEFT_ELBOW == null) {
			LEFT_ELBOW = (GeoPoint3D) kernel.lookupLabel("LEFT_ELBOW");
		}
		if (LEFT_WRIST == null) {
			LEFT_WRIST = (GeoPoint3D) kernel.lookupLabel("LEFT_WRIST");
		}
		if (LEFT_HAND == null) {
			LEFT_HAND = (GeoPoint3D) kernel.lookupLabel("LEFT_HAND");
		}
		if (LEFT_FINGER_TIP == null) {
			LEFT_FINGER_TIP = (GeoPoint3D) kernel.lookupLabel("LEFT_FINGER");
		}
		if (RIGHT_COLLAR == null) {
			RIGHT_COLLAR = (GeoPoint3D) kernel.lookupLabel("RIGHT_COLLAR");
		}
		if (RIGHT_SHOULDER == null) {
			RIGHT_SHOULDER = (GeoPoint3D) kernel.lookupLabel("RIGHT_SHOULDER");
		}
		if (RIGHT_ELBOW == null) {
			RIGHT_ELBOW = (GeoPoint3D) kernel.lookupLabel("RIGHT_ELBOW");
		}
		if (RIGHT_WRIST == null) {
			RIGHT_WRIST = (GeoPoint3D) kernel.lookupLabel("RIGHT_WRIST");
		}
		if (RIGHT_HAND == null) {
			RIGHT_HAND = (GeoPoint3D) kernel.lookupLabel("RIGHT_HAND");
		}
		if (RIGHT_FINGER_TIP == null) {
			RIGHT_FINGER_TIP = (GeoPoint3D) kernel.lookupLabel("RIGHT_FINGER");
		}
		if (LEFT_HIP == null) {
			LEFT_HIP = (GeoPoint3D) kernel.lookupLabel("LEFT_HIP");
		}
		if (LEFT_KNEE == null) {
			LEFT_KNEE = (GeoPoint3D) kernel.lookupLabel("LEFT_KNEE");
		}
		if (LEFT_ANKLE == null) {
			LEFT_ANKLE = (GeoPoint3D) kernel.lookupLabel("LEFT_ANKLE");
		}
		if (LEFT_FOOT == null) {
			LEFT_FOOT = (GeoPoint3D) kernel.lookupLabel("LEFT_FOOT");
		}
		if (RIGHT_HIP == null) {
			RIGHT_HIP = (GeoPoint3D) kernel.lookupLabel("RIGHT_HIP");
		}
		if (RIGHT_KNEE == null) {
			RIGHT_KNEE = (GeoPoint3D) kernel.lookupLabel("RIGHT_KNEE");
		}
		if (RIGHT_ANKLE == null) {
			RIGHT_ANKLE = (GeoPoint3D) kernel.lookupLabel("RIGHT_ANKLE");
		}
		if (RIGHT_FOOT == null) {
			RIGHT_FOOT = (GeoPoint3D) kernel.lookupLabel("RIGHT_FOOT");
			pointList = new ArrayList<GeoElement>();
			pointList.add(HEAD);
			pointList.add(NECK);
			pointList.add(TORSO);
			// pointList.add(WAIST);
			// pointList.add(LEFT_COLLAR);
			pointList.add(LEFT_SHOULDER);
			pointList.add(LEFT_ELBOW);
			// pointList.add(LEFT_WRIST);
			pointList.add(LEFT_HAND);
			// pointList.add(LEFT_FINGER_TIP);
			// pointList.add(RIGHT_COLLAR);
			pointList.add(RIGHT_SHOULDER);
			pointList.add(RIGHT_ELBOW);
			// pointList.add(RIGHT_WRIST);
			pointList.add(RIGHT_HAND);
			// pointList.add(RIGHT_FINGER_TIP);
			pointList.add(LEFT_HIP);
			pointList.add(LEFT_KNEE);
			// pointList.add(LEFT_ANKLE);
			pointList.add(LEFT_FOOT);
			pointList.add(RIGHT_HIP);
			pointList.add(RIGHT_KNEE);
			// pointList.add(RIGHT_ANKLE);
			pointList.add(RIGHT_FOOT);

			// for (int i = 0; i < pointList.size(); i++) {
			// if (pointList.get(i) == null) {
			// Application.debug(i);
			// }

			// }

		}

		Point3D pos1 = dict.get(SkeletonJoint.RIGHT_HAND).getPosition();
		if (RIGHT_HAND != null) {
			RIGHT_HAND.setCoords(pos1.getX(), 1000 - pos1.getY(), pos1.getZ(),
					1.0);
		}

		pos1 = dict.get(SkeletonJoint.HEAD).getPosition();
		if (HEAD != null) {
			HEAD.setCoords(pos1.getX(), 1000 - pos1.getY(), pos1.getZ(), 1.0);
		}

		pos1 = dict.get(SkeletonJoint.NECK).getPosition();
		if (NECK != null) {
			NECK.setCoords(pos1.getX(), 1000 - pos1.getY(), pos1.getZ(), 1.0);
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
		if (LEFT_SHOULDER != null) {
			LEFT_SHOULDER.setCoords(pos1.getX(), 1000 - pos1.getY(), 1.0);
		}

		pos1 = dict.get(SkeletonJoint.LEFT_ELBOW).getPosition();
		if (LEFT_ELBOW != null) {
			LEFT_ELBOW.setCoords(pos1.getX(), 1000 - pos1.getY(), pos1.getZ(),
					1.0);
		}

		pos1 = dict.get(SkeletonJoint.TORSO).getPosition();
		if (TORSO != null) {
			TORSO.setCoords(pos1.getX(), 1000 - pos1.getY(), pos1.getZ(), 1.0);
		}

		// pos1 = dict.get(SkeletonJoint.LEFT_WRIST).getPosition();
		// if (LEFT_WRIST != null) {
		// LEFT_WRIST.setCoords(pos1.getX(), 1000 - pos1.getY(), 1.0);
		// }

		pos1 = dict.get(SkeletonJoint.LEFT_HAND).getPosition();
		if (LEFT_HAND != null) {
			LEFT_HAND.setCoords(pos1.getX(), 1000 - pos1.getY(), pos1.getZ(),
					1.0);
		}

		pos1 = dict.get(SkeletonJoint.LEFT_FOOT).getPosition();
		if (LEFT_FOOT != null) {
			LEFT_FOOT.setCoords(pos1.getX(), 1000 - pos1.getY(), pos1.getZ(),
					1.0);
		}

		pos1 = dict.get(SkeletonJoint.RIGHT_FOOT).getPosition();
		if (RIGHT_FOOT != null) {
			RIGHT_FOOT.setCoords(pos1.getX(), 1000 - pos1.getY(), pos1.getZ(),
					1.0);
		}

		pos1 = dict.get(SkeletonJoint.RIGHT_SHOULDER).getPosition();
		if (RIGHT_SHOULDER != null) {
			RIGHT_SHOULDER.setCoords(pos1.getX(), 1000 - pos1.getY(),
					pos1.getZ(), 1.0);
		}

		pos1 = dict.get(SkeletonJoint.RIGHT_ELBOW).getPosition();
		if (RIGHT_ELBOW != null) {
			RIGHT_ELBOW.setCoords(pos1.getX(), 1000 - pos1.getY(), pos1.getZ(),
					1.0);
		}

		pos1 = dict.get(SkeletonJoint.LEFT_HIP).getPosition();
		if (LEFT_HIP != null) {
			LEFT_HIP.setCoords(pos1.getX(), 1000 - pos1.getY(), pos1.getZ(),
					1.0);
		}

		pos1 = dict.get(SkeletonJoint.LEFT_KNEE).getPosition();
		if (LEFT_KNEE != null) {
			LEFT_KNEE.setCoords(pos1.getX(), 1000 - pos1.getY(), pos1.getZ(),
					1.0);
		}

		pos1 = dict.get(SkeletonJoint.RIGHT_HIP).getPosition();
		if (RIGHT_HIP != null) {
			RIGHT_HIP.setCoords(pos1.getX(), 1000 - pos1.getY(), pos1.getZ(),
					1.0);
		}

		pos1 = dict.get(SkeletonJoint.RIGHT_KNEE).getPosition();
		if (RIGHT_KNEE != null) {
			RIGHT_KNEE.setCoords(pos1.getX(), 1000 - pos1.getY(), pos1.getZ(),
					1.0);
		}

		GeoElement.updateCascade(pointList, getTempSet(), false);
		kernel.notifyRepaint();

	}

	private ArrayList<GeoElement> pointList;
	private TreeSet<AlgoElementInterface> tempSet;

	private TreeSet<AlgoElementInterface> getTempSet() {
		if (tempSet == null) {
			tempSet = new TreeSet<AlgoElementInterface>();
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
			Application.debug("");
			// ArrayList<Point3D> newList = new ArrayList<Point3D>();
			// newList.add(args.getPosition());
			// history.put(new Integer(args.getId()), newList);
		}
	}

	class MyHandUpdateEvent implements IObserver<ActiveHandEventArgs> {
		public void update(IObservable<ActiveHandEventArgs> observable,
				ActiveHandEventArgs args) {
			// ArrayList<Point3D> historyList = history.get(args.getId());
			Application.debug("");

			// historyList.add(args.getPosition());

			// while (historyList.size() > historySize) {
			// historyList.remove(0);
			// }

		}
	}

	class MyHandDestroyEvent implements IObserver<InactiveHandEventArgs> {
		public void update(IObservable<InactiveHandEventArgs> observable,
				InactiveHandEventArgs args) {
			Application.debug("");
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
