package org.geogebra.desktop.geogebra3D.input3D.intelRealSense;

import intel.rssdk.PXCMCaptureManager;
import intel.rssdk.PXCMHandConfiguration;
import intel.rssdk.PXCMHandConfiguration.AlertHandler;
import intel.rssdk.PXCMHandData;
import intel.rssdk.PXCMHandData.AlertType;
import intel.rssdk.PXCMHandData.BodySideType;
import intel.rssdk.PXCMHandModule;
import intel.rssdk.PXCMPoint3DF32;
import intel.rssdk.PXCMPoint4DF32;
import intel.rssdk.PXCMSenseManager;
import intel.rssdk.PXCMSession;
import intel.rssdk.pxcmStatus;

import org.geogebra.common.euclidian3D.Input3D;
import org.geogebra.common.euclidian3D.Input3D.OutOfField;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.geogebra3D.input3D.Input3DFactory.Input3DException;
import org.geogebra.desktop.geogebra3D.input3D.Input3DFactory.Input3DExceptionType;


/**
 * socket of realsense
 * 
 * @author mathieu
 *
 */
public class Socket {


	/**
	 * factor screen / real world
	 */
	static double SCREEN_REAL_DIM_FACTOR = 1 / 0.1;
	/**
	 * right/left side offset
	 */
	static double SIDE_OFFSET = 0.75;
	/**
	 * origin point depth
	 */
	static float DEPTH_ZERO = 0.4f;
	/**
	 * samples used for average
	 */
	static int SAMPLES = 7;
	

	/** hand x position */
	public double handX;
	/** hand y position */
	public double handY;
	/** hand z position */
	public double handZ;

	/**
	 * hand orientation vector (x value)
	 */
	public double handOrientationX;
	/**
	 * hand orientation vector (y value)
	 */
	public double handOrientationY;
	/**
	 * hand orientation vector (z value)
	 */
	public double handOrientationZ;
	/**
	 * hand orientation vector (w value)
	 */
	public double handOrientationW;

	/**
	 * right button state
	 */
	public boolean rightButton = false;
	/**
	 * left button state
	 */
	public boolean leftButton = false;
	
	/** says if it has got a message from realsense */
	public boolean gotMessage = false;      

	static private PXCMSenseManager SENSE_MANAGER;
	static private PXCMCaptureManager CAPTURE_MANAGER;
	private pxcmStatus sts;
	private PXCMHandData handData;
	private PXCMHandData.IHand hand;
	
	private DataSampler dataSampler;
	
	private abstract class DataSampler {
		
		protected int samples;
		protected int index;

		protected BodySideType side;
		
		protected int leftSideCount, rightSideCount;

		protected float[] worldX, worldY, worldZ;
		
		protected float[] orientationX, orientationY, orientationZ, orientationW;
		
		public DataSampler(int samples){
			this.samples = samples;
			index = 0;

			
			worldX = new float[samples];
			worldY = new float[samples];
			worldZ = new float[samples];
			
			
			orientationX = new float[samples];
			orientationY = new float[samples];
			orientationZ = new float[samples];
			orientationW = new float[samples];
			
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

		public abstract void addData(BodySideType handSide,
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
		public void addData(BodySideType handSide,
				float wx, float wy, float wz,
				float ox, float oy, float oz, float ow){
			
			
			if (resetAllValues){

				resetSide();
				addSideDetected(handSide);

				for (int i = 0 ; i < samples ; i++){
					// reset all values
					worldX[i] = wx;
					worldY[i] = wy;
					worldZ[i] = wz;
					orientationX[i] = ox;
					orientationY[i] = oy;
					orientationZ[i] = oz;
					orientationW[i] = ow;
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
			
			addSideDetected(handSide);

			
			worldXSum -= worldX[index];
			worldYSum -= worldY[index];
			worldZSum -= worldZ[index];
			
			worldX[index] = wx;
			worldY[index] = wy;
			worldZ[index] = wz;
			
			worldXSum += worldX[index];
			worldYSum += worldY[index];
			worldZSum += worldZ[index];
			
			
			handOrientationXSum -= orientationX[index];
			handOrientationYSum -= orientationY[index];
			handOrientationZSum -= orientationZ[index];
			handOrientationWSum -= orientationW[index];

			orientationX[index] = ox;
			orientationY[index] = oy;
			orientationZ[index] = oz;
			orientationW[index] = ow;

			handOrientationXSum += orientationX[index];
			handOrientationYSum += orientationY[index];
			handOrientationZSum += orientationZ[index];
			handOrientationWSum += orientationW[index];

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
	
	
	
	private int handId = -1;
	
	private Input3D.OutOfField handOut;

	
	/**
	 * says if we need to reset all values
	 */
	boolean resetAllValues = false;
	
	/**
	 * cam set alert, this updates the hand in/out status
	 * 
	 * @param id
	 *            hand id
	 * @param type
	 *            alert type
	 */
	void setAlert(int id, AlertType type) {
		
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
	
	/**
	 * Create a session to use realsense camera
	 * 
	 * @throws Exception
	 *             if no camera installed
	 */
	public static void createSession() throws Input3DException {

		if (SESSION != null) {
			return;
		}

		// reset sense manager
		SENSE_MANAGER = null;

		try {
			// Create session
			SESSION = PXCMSession.CreateInstance();
		} catch (Throwable e) {
			throw new Input3DException(
					Input3DExceptionType.INSTALL,
					"RealSense: Failed to start session instance creation, maybe unsupported platform?");
		}
		if (SESSION == null) {
			throw new Input3DException(Input3DExceptionType.INSTALL,
					"RealSense: Failed to create a session instance");
		}

	}

	static private PXCMSession SESSION = null;

	/**
	 * Create a "Socket" for realsense camera
	 * 
	 * 
	 * @throws Exception
	 *             when fails
	 */
	public Socket() throws Input3DException {

		if (SESSION == null) {
			createSession();
		}

		if (SESSION == null) {
			throw new Input3DException(Input3DExceptionType.INSTALL,
					"RealSense: no session created");
		}

		if (SENSE_MANAGER != null) {
			throw new Input3DException(Input3DExceptionType.ALREADY_USED,
					"RealSense: already in use");
		}

		SENSE_MANAGER = SESSION.CreateSenseManager();
		if (SENSE_MANAGER == null) {
			throw new Input3DException(Input3DExceptionType.RUN,
					"RealSense: Failed to create a SenseManager instance");
		}

		CAPTURE_MANAGER = SENSE_MANAGER.QueryCaptureManager();
		CAPTURE_MANAGER.FilterByDeviceInfo("RealSense", null, 0);

		sts = SENSE_MANAGER.EnableHand(null);
		if (sts.compareTo(pxcmStatus.PXCM_STATUS_NO_ERROR)<0) {
			throw new Input3DException(Input3DExceptionType.RUN,
					"RealSense: Failed to enable HandAnalysis");
		}

		dataSampler = new DataAverage(SAMPLES);

		sts = SENSE_MANAGER.Init();
		if (sts.compareTo(pxcmStatus.PXCM_STATUS_NO_ERROR)>=0) {
			PXCMHandModule handModule = SENSE_MANAGER.QueryHand(); 
			PXCMHandConfiguration handConfig = handModule.CreateActiveConfiguration(); 


			handConfig.EnableAllAlerts();

			AlertHandler alertHandler = new AlertHandler() {
				
				@Override
				public void OnFiredAlert(intel.rssdk.PXCMHandData.AlertData data) {
					setAlert(data.handId, data.label);
				}
			};
			handConfig.SubscribeAlert(alertHandler);
			
			handConfig.ApplyChanges();
			handConfig.Update();
			
			handData = handModule.CreateOutput();
			hand = new PXCMHandData.IHand();
			
			handOut = OutOfField.YES;
			connected = true;
		}

		if (!connected) {
			throw new Input3DException(Input3DExceptionType.RUN,
					"RealSense: not connected");
		}
		
		Log.debug("RealSense: connected");
		
	}





	private boolean connected = false;




	/**
	 * get data from camera
	 * 
	 * @return true if data have been produced
	 */
	public boolean getData(){

		if (!connected)
			return false;

		sts = SENSE_MANAGER.AcquireFrame(true);
		if (sts.compareTo(pxcmStatus.PXCM_STATUS_NO_ERROR)<0){
			gotMessage = false;
			SENSE_MANAGER.ReleaseFrame();
			return false;
		}


		// Query and Display Joint of Hand or Palm
		handData.Update(); 

		sts = handData.QueryHandData(PXCMHandData.AccessOrderType.ACCESS_ORDER_NEAR_TO_FAR, 0, hand);

		if (sts.compareTo(pxcmStatus.PXCM_STATUS_NO_ERROR) >= 0) {
			PXCMPoint3DF32 world = hand.QueryMassCenterWorld();
			PXCMPoint4DF32 palmOrientation = hand.QueryPalmOrientation();
			BodySideType handSide = hand.QueryBodySide();

			
			dataSampler.addData(handSide,
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

		SENSE_MANAGER.ReleaseFrame();

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



	/**
	 * set left button status
	 * 
	 * @param flag
	 *            status
	 */
	public void setLeftButtonPressed(boolean flag) {
		leftButton = flag;	
	}

	/**
	 * exit and close manager
	 */
	public void exit() {
		CAPTURE_MANAGER.CloseStreams();
		SENSE_MANAGER.Close();
		SENSE_MANAGER = null;
		SESSION.close();
		SESSION = null;
	}



}

