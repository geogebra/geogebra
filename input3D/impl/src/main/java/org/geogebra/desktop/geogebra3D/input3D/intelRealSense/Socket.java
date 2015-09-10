package org.geogebra.desktop.geogebra3D.input3D.intelRealSense;

import intel.rssdk.PXCMCapture;
import intel.rssdk.PXCMCaptureManager;
import intel.rssdk.PXCMHandConfiguration;
import intel.rssdk.PXCMHandConfiguration.AlertHandler;
import intel.rssdk.PXCMHandData;
import intel.rssdk.PXCMHandData.AlertType;
import intel.rssdk.PXCMHandData.BodySideType;
import intel.rssdk.PXCMHandData.IHand;
import intel.rssdk.PXCMHandModule;
import intel.rssdk.PXCMPoint3DF32;
import intel.rssdk.PXCMPoint4DF32;
import intel.rssdk.PXCMSenseManager;
import intel.rssdk.PXCMSession;
import intel.rssdk.pxcmStatus;

import java.util.Arrays;

import org.geogebra.common.euclidian3D.Input3D;
import org.geogebra.common.euclidian3D.Input3D.OutOfField;
import org.geogebra.common.util.debug.Log;


/**
 * socket of realsense
 * 
 * @author mathieu
 *
 */
public class Socket {


	private static double SCREEN_REAL_DIM_FACTOR = 1 / 0.1;
	private static double SIDE_OFFSET = 0.75;
	private static float DEPTH_ZERO = 0.4f;
	private static int SAMPLES = 7;
	

	public enum Gestures {PINCH, SPREAD, FIST};

	/** hand x position */
	public double handX;
	/** hand y position */
	public double handY;
	/** hand z position */
	public double handZ;

	public double handOrientationX, handOrientationY, handOrientationZ, handOrientationW;

	public double leftEyeX, leftEyeY, leftEyeZ;
	public double rightEyeX, rightEyeY, rightEyeZ;
	public double glassesCenterX, glassesCenterY, glassesCenterZ;
	public double glassesOrientationX, glassesOrientationY, glassesOrientationZ, glassesOrientationW;

	public boolean rightButton = false, leftButton = false;
	
	public float hand2Dx, hand2Dy, hand2Dfactor;

	/** says if it has got a message from realsense */
	public boolean gotMessage = false;      

	private PXCMSenseManager senseMgr;
	private pxcmStatus sts;
	private PXCMHandData handData;
	
	private DataSampler dataSampler;
	
	private abstract class DataSampler {
		
		protected int samples;
		protected int index;

		protected BodySideType side;
		
		protected int leftSideCount, rightSideCount;

		protected float[] worldX, worldY, worldZ;
		
		protected float[] handOrientationX, handOrientationY, handOrientationZ, handOrientationW;
		
		public DataSampler(int samples){
			this.samples = samples;
			index = 0;

			
			worldX = new float[samples];
			worldY = new float[samples];
			worldZ = new float[samples];
			
			
			handOrientationX = new float[samples];
			handOrientationY = new float[samples];
			handOrientationZ = new float[samples];
			handOrientationW = new float[samples];
			
			resetSide();

		}

		protected void resetSide() {
			leftSideCount = 0;
			rightSideCount = 0;
			side = BodySideType.BODY_SIDE_UNKNOWN;
		}

		protected void addSideDetected(BodySideType type) {
			if (type == BodySideType.BODY_SIDE_RIGHT) {
				rightSideCount++;
				if (rightSideCount > 10000) {
					rightSideCount /= 10;
					leftSideCount /= 10;
				}
				updateSide();
			} else if (type == BodySideType.BODY_SIDE_LEFT) {
				leftSideCount++;
				if (leftSideCount > 10000) {
					rightSideCount /= 10;
					leftSideCount /= 10;
				}
				updateSide();
			}
		}

		private void updateSide() {
			if (side == BodySideType.BODY_SIDE_UNKNOWN) {
				// check if we can decide side
				if (rightSideCount > leftSideCount) {
					side = BodySideType.BODY_SIDE_RIGHT;
				} else if (rightSideCount < leftSideCount) {
					side = BodySideType.BODY_SIDE_LEFT;
				}
			} else {
				// check if we should decide side
				if (rightSideCount > 2 * leftSideCount) {
					side = BodySideType.BODY_SIDE_RIGHT;
				} else if (2 * rightSideCount < leftSideCount) {
					side = BodySideType.BODY_SIDE_LEFT;
				}

			}

		}

		public abstract void addData(IHand hand,
				float wx, float wy, float wz, float ox, float oy, float oz, float ow);

		public abstract double getWorldX();

		public abstract double getWorldY();

		public abstract double getWorldZ();

		public abstract double getHandOrientationX();

		public abstract double getHandOrientationY();

		public abstract double getHandOrientationZ();

		public abstract double getHandOrientationW();

		public BodySideType getSide() {
			return side;
		}

	}
	
	private class DataAverage extends DataSampler{
		
		private float worldXSum, worldYSum, worldZSum;
		
		private float handOrientationXSum, handOrientationYSum, handOrientationZSum, handOrientationWSum;
		
		public DataAverage(int samples){
			
			super(samples);
			
			worldXSum = 0f;
			worldYSum = 0f;
			worldZSum = 0f;
			
			
			handOrientationXSum = 0f;
			handOrientationYSum = 0f;
			handOrientationZSum = 0f;
			handOrientationWSum = 0f;
			

		}
		
		@Override
		public void addData(IHand hand,
				float wx, float wy, float wz,
				float ox, float oy, float oz, float ow){
			
			
			if (resetAllValues){

				resetSide();
				addSideDetected(hand.QueryBodySide());

				for (int i = 0 ; i < samples ; i++){
					// reset all values
					worldX[i] = wx;
					worldY[i] = wy;
					worldZ[i] = wz;
					handOrientationX[i] = ox;
					handOrientationY[i] = oy;
					handOrientationZ[i] = oz;
					handOrientationW[i] = ow;
				}
				
				worldXSum = wx * samples;
				worldYSum = wy * samples;
				worldZSum = wz * samples;
				
				handOrientationXSum = ox * samples;
				handOrientationYSum = oy * samples;
				handOrientationZSum = oz * samples;
				handOrientationWSum = ow * samples;
				
				index = 0;
				resetAllValues = false;
				return;
			}
			
			addSideDetected(hand.QueryBodySide());

			
			worldXSum -= worldX[index];
			worldYSum -= worldY[index];
			worldZSum -= worldZ[index];
			
			worldX[index] = wx;
			worldY[index] = wy;
			worldZ[index] = wz;
			
			worldXSum += worldX[index];
			worldYSum += worldY[index];
			worldZSum += worldZ[index];
			
			
			handOrientationXSum -= handOrientationX[index];
			handOrientationYSum -= handOrientationY[index];
			handOrientationZSum -= handOrientationZ[index];
			handOrientationWSum -= handOrientationW[index];

			handOrientationX[index] = ox;
			handOrientationY[index] = oy;
			handOrientationZ[index] = oz;
			handOrientationW[index] = ow;

			handOrientationXSum += handOrientationX[index];
			handOrientationYSum += handOrientationY[index];
			handOrientationZSum += handOrientationZ[index];
			handOrientationWSum += handOrientationW[index];

			index++;
			if (index >= samples){
				index = 0;
			}
		}
		

		@Override
		public double getWorldX(){
			return -worldXSum * SCREEN_REAL_DIM_FACTOR / samples;
		}
		

		@Override
		public double getWorldY(){
			return worldYSum * SCREEN_REAL_DIM_FACTOR / samples;
		}
		

		@Override
		public double getWorldZ(){
			return (worldZSum / samples - DEPTH_ZERO) * SCREEN_REAL_DIM_FACTOR;
		}
		

		@Override
		public double getHandOrientationX(){
			return - handOrientationXSum / samples;
		}
		

		@Override
		public double getHandOrientationY(){
			return - handOrientationYSum / samples;
		}
		

		@Override
		public double getHandOrientationZ(){
			return handOrientationZSum / samples;
		}
		

		@Override
		public double getHandOrientationW(){
			return handOrientationWSum / samples;
		}
		
	
		

		
	}
	
	private class DataMedian extends DataAverage{

		public DataMedian(int samples){

			super(samples);
			
			sortedArray = new float[samples];
		}
		
		private float[] sortedArray;
		
		private float getMedian(float[] values){
			
			for (int i = 0 ; i < samples ; i++){
				sortedArray[i] = values[i];
			}
			
			Arrays.sort(sortedArray);
			
			return sortedArray[samples/2];
			
		}
		
		
		@Override
		public double getWorldX(){
			return -getMedian(worldX) * SCREEN_REAL_DIM_FACTOR;
		}
		

		@Override
		public double getWorldY(){
			return getMedian(worldY) * SCREEN_REAL_DIM_FACTOR;
		}
		

		@Override
		public double getWorldZ(){
			return (getMedian(worldZ) - 0.2f) * SCREEN_REAL_DIM_FACTOR;
		}
		

		@Override
		public double getHandOrientationX(){
			return - getMedian(handOrientationX);
		}
		

		@Override
		public double getHandOrientationY(){
			return - getMedian(handOrientationY);
		}
		

		@Override
		public double getHandOrientationZ(){
			return getMedian(handOrientationZ);
		}
		

		@Override
		public double getHandOrientationW(){
			return getMedian(handOrientationW);
		}
	}
	
	
	private Gestures gesture = Gestures.SPREAD;
	
	private int handId = -1;
	
	private Input3D.OutOfField handOut;

	public void setGesture(int id, String name){
		
		//App.debug(id+" : "+name);
		
		
		// check it's the current hand tracked
		if (handId != id){
			return;
		}
		
		switch(name.charAt(0)){
		case 'f':
			/*
			if (name.equals("full_pinch")){
				gesture = Gestures.PINCH;
			}
			*/

			/*
			if (name.equals("fist")){
				gesture = Gestures.FIST;
			}
			*/
			break;
		case 's':
			if (name.equals("spreadfingers")){
				gesture = Gestures.SPREAD;
			}
			break;
		case 't':
			if (name.equals("two_fingers_pinch_open")){
				gesture = Gestures.PINCH;
			}
			break;
		default:
			//gesture = Gestures.SPREAD;
			break;

		}
		
		//App.debug(""+gesture);
	}
	
	private boolean resetAllValues = false;
	
	private void setAlert(int id, AlertType type){
		
		// App.debug("alert hand #" + id + " : " + type);
		
		if (handOut != OutOfField.NO) { // no hand for now
			if (type == AlertType.ALERT_HAND_INSIDE_BORDERS){
				Log.debug("hand #" + id + " inside borders");
				handId = id;
				handOut = OutOfField.NO;
				resetAllValues = true;
			} else if (handId == id) {
				switch (type) {
				case ALERT_HAND_OUT_OF_BOTTOM_BORDER:
					handOut = OutOfField.BOTTOM;
					break;
				case ALERT_HAND_OUT_OF_TOP_BORDER:
					handOut = OutOfField.TOP;
					break;
				case ALERT_HAND_OUT_OF_LEFT_BORDER:
					handOut = OutOfField.LEFT;
					break;
				case ALERT_HAND_OUT_OF_RIGHT_BORDER:
					handOut = OutOfField.RIGHT;
					break;
				case ALERT_HAND_TOO_CLOSE:
					handOut = OutOfField.NEAR;
					break;
				case ALERT_HAND_TOO_FAR:
					handOut = OutOfField.FAR;
					break;
				}
			}
		}else if (handId == id){ // new alert from tracked hand
			if (type == AlertType.ALERT_HAND_OUT_OF_BORDERS){
				Log.debug("hand #" + id + " out of borders");
				handOut = OutOfField.YES;
			}
		}
		
		
	}
	

	public Socket() throws Exception {

		Log.debug("Try to connect realsense...");
		
		// Create session
		PXCMSession session = null;
		try {
			session = PXCMSession.CreateInstance();
		} catch (Throwable e) {
			throw new Exception(
					"RealSense: Failed to start session instance creation, maybe unsupported platform?");
		}
		if (session == null) {
			throw new Exception(
					"RealSense: Failed to create a session instance");
		}

		senseMgr = session.CreateSenseManager();
		if (senseMgr == null) {
			throw new Exception(
					"RealSense: Failed to create a SenseManager instance");
		}

		PXCMCaptureManager captureMgr = senseMgr.QueryCaptureManager();
		captureMgr.FilterByDeviceInfo("RealSense", null, 0);

		sts = senseMgr.EnableHand(null);
		if (sts.compareTo(pxcmStatus.PXCM_STATUS_NO_ERROR)<0) {
			throw new Exception("RealSense: Failed to enable HandAnalysis");
		}

		dataSampler = new DataAverage(SAMPLES);
		//dataSampler = new DataMedian(SAMPLES);

		sts = senseMgr.Init();
		if (sts.compareTo(pxcmStatus.PXCM_STATUS_NO_ERROR)>=0) {
			PXCMHandModule handModule = senseMgr.QueryHand(); 
			PXCMHandConfiguration handConfig = handModule.CreateActiveConfiguration(); 

			// handConfig.EnableAllGestures();

			handConfig.EnableAllAlerts();

			// enables stabilizer and smoothing
			// pxcmStatus status = handConfig.EnableStabilizer(true);
			// App.debug("EnableStabilizer: " + status.isSuccessful());
			// status = handConfig.SetSmoothingValue(1);
			// App.debug("SetSmoothingValue to 1: " + status.isSuccessful());
			
			// GestureHandler handler = new GestureHandler() {
			// @Override
			// public void OnFiredGesture(GestureData data) {
			// //App.debug(""+data.name+" -- "+data.handId);
			// setGesture(data.handId, data.name);
			// }
			// };
			// handConfig.SubscribeGesture(handler);
			
			AlertHandler alertHandler = new AlertHandler() {
				
				@Override
				public void OnFiredAlert(intel.rssdk.PXCMHandData.AlertData data) {
					// App.debug("alert : " + data.handId + ", "
					// + data.label.name());
					setAlert(data.handId, data.label);
				}
			};
			handConfig.SubscribeAlert(alertHandler);
			
			handConfig.ApplyChanges();
			handConfig.Update();
			
			handData = handModule.CreateOutput();
			
			handOut = OutOfField.YES;
			connected = true;
		}

		if (!connected) {
			throw new Exception("RealSense: not connected");
		}
		
		Log.debug("RealSense: connected");
		
	}





	private boolean connected = false;




	public boolean getData(){

		if (!connected)
			return false;

		sts = senseMgr.AcquireFrame(true);
		if (sts.compareTo(pxcmStatus.PXCM_STATUS_NO_ERROR)<0){
			gotMessage = false;
			senseMgr.ReleaseFrame();
			return false;
		};

		PXCMCapture.Sample sample = senseMgr.QueryHandSample();

		// Query and Display Joint of Hand or Palm
		handData.Update(); 

		PXCMHandData.IHand hand = new PXCMHandData.IHand(); 
		sts = handData.QueryHandData(PXCMHandData.AccessOrderType.ACCESS_ORDER_NEAR_TO_FAR, 0, hand);

		if (sts.compareTo(pxcmStatus.PXCM_STATUS_NO_ERROR) >= 0) {
			PXCMPoint3DF32 world = hand.QueryMassCenterWorld();
			PXCMPoint4DF32 palmOrientation = hand.QueryPalmOrientation();

			
			dataSampler.addData(hand,
					world.x, world.y, world.z,
					palmOrientation.x, palmOrientation.y, palmOrientation.z, palmOrientation.w);
			
			
			handX = dataSampler.getWorldX();
			handY = dataSampler.getWorldY();
			handZ = dataSampler.getWorldZ();
			

			switch (dataSampler.getSide()) {
			case BODY_SIDE_RIGHT:
				handX -= SIDE_OFFSET;
				break;
			case BODY_SIDE_LEFT:
				handX += SIDE_OFFSET;
				break;
			case BODY_SIDE_UNKNOWN:
			default:
				handX -= SIDE_OFFSET;
				break;
			}
			
			handOrientationX = dataSampler.getHandOrientationX();
			handOrientationY = dataSampler.getHandOrientationY();
			handOrientationZ = dataSampler.getHandOrientationZ();
			handOrientationW = dataSampler.getHandOrientationW();
			

			gotMessage = true;
			
		}else{
			gotMessage = false;
		}


		senseMgr.ReleaseFrame();

		


		return true;
	}



	/**
	 * 
	 * @return true if a hand is tracked
	 */
	public boolean hasTrackedHand() {
		return handOut == OutOfField.NO;
	}

	/**
	 * 
	 * @return out of field type
	 */
	public OutOfField getOutOfField() {
		return handOut;
	}



	public void setLeftButtonPressed(boolean flag) {
		leftButton = flag;	
	}




}

