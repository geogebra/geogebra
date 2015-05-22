package org.geogebra.desktop.geogebra3D.input3D.intelRealSense;

import intel.rssdk.PXCMCapture;
import intel.rssdk.PXCMCaptureManager;
import intel.rssdk.PXCMHandConfiguration;
import intel.rssdk.PXCMHandConfiguration.AlertHandler;
import intel.rssdk.PXCMHandConfiguration.GestureHandler;
import intel.rssdk.PXCMHandData;
import intel.rssdk.PXCMHandData.AlertType;
import intel.rssdk.PXCMHandData.GestureData;
import intel.rssdk.PXCMHandModule;
import intel.rssdk.PXCMPoint3DF32;
import intel.rssdk.PXCMPoint4DF32;
import intel.rssdk.PXCMSenseManager;
import intel.rssdk.PXCMSession;
import intel.rssdk.pxcmStatus;

import java.util.Arrays;

import org.geogebra.common.main.App;



public class Socket {


	private static double SCREEN_REAL_DIM_FACTOR = 1 / 0.12;
	private static int SAMPLES = 7;

	public enum Gestures {PINCH, SPREAD, FIST};

	/** bird x position */
	public double birdX;
	/** bird y position */
	public double birdY;
	/** bird z position */
	public double birdZ;

	public double birdOrientationX, birdOrientationY, birdOrientationZ, birdOrientationW;

	public double leftEyeX, leftEyeY, leftEyeZ;
	public double rightEyeX, rightEyeY, rightEyeZ;
	public double glassesCenterX, glassesCenterY, glassesCenterZ;
	public double glassesOrientationX, glassesOrientationY, glassesOrientationZ, glassesOrientationW;

	public boolean rightButton = false, leftButton = false;
	
	public float hand2Dx, hand2Dy, hand2Dfactor;

	/** says if it has got a message from leo */
	public boolean gotMessage = false;      

	private PXCMSenseManager senseMgr;
	private pxcmStatus sts;
	private PXCMHandData handData;
	
	private DataSampler dataSampler;
	
	private abstract class DataSampler {
		
		protected int samples;
		protected int index;

		
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
			
			

		}

		public abstract void addData(//float imx, float imy, 
				float wx, float wy, float wz, float ox, float oy, float oz, float ow);

		public abstract double getWorldX();

		public abstract double getWorldY();

		public abstract double getWorldZ();

		public abstract double getHandOrientationX();

		public abstract double getHandOrientationY();

		public abstract double getHandOrientationZ();

		public abstract double getHandOrientationW();

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
		public void addData(//float imx, float imy, 
				float wx, float wy, float wz,
				float ox, float oy, float oz, float ow){
			
			
			if (resetAllValues){
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
			return (worldZSum / samples - 0.2f) * SCREEN_REAL_DIM_FACTOR;
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
		
		//App.debug("alert hand #"+id+" : "+type);
		
		if (handId == -1){ // no hand for now
			if (type == AlertType.ALERT_HAND_INSIDE_BORDERS){
				App.debug("hand #"+id+" inside borders");
				handId = id;
				resetAllValues = true;
			}
		}else if (handId == id){ // new alert from tracked hand
			if (type == AlertType.ALERT_HAND_OUT_OF_BORDERS){
				App.debug("hand #"+id+" out of borders");
				handId = -1;
				/*
				leftButton = false;
				rightButton = false;
				*/
			}
		}
		
		
	}
	

	public Socket() {

		App.debug("Try to connect realsense...");

		// Create session
		PXCMSession session = PXCMSession.CreateInstance();
		if (session == null) {
			App.error("Failed to create a session instance\n");
			return;
		}

		senseMgr = session.CreateSenseManager();
		if (senseMgr == null) {
			App.error("Failed to create a SenseManager instance\n");
			return;
		}

		PXCMCaptureManager captureMgr = senseMgr.QueryCaptureManager();
		captureMgr.FilterByDeviceInfo("RealSense", null, 0);

		sts = senseMgr.EnableHand(null);
		if (sts.compareTo(pxcmStatus.PXCM_STATUS_NO_ERROR)<0) {
			App.error("Failed to enable HandAnalysis\n");
			return;
		}

		dataSampler = new DataAverage(SAMPLES);
		//dataSampler = new DataMedian(SAMPLES);

		sts = senseMgr.Init();
		if (sts.compareTo(pxcmStatus.PXCM_STATUS_NO_ERROR)>=0) {
			PXCMHandModule handModule = senseMgr.QueryHand(); 
			PXCMHandConfiguration handConfig = handModule.CreateActiveConfiguration(); 
			handConfig.EnableAllGestures();
			handConfig.EnableAllAlerts();
			
			GestureHandler handler = new GestureHandler() {
				@Override
				public void OnFiredGesture(GestureData data) {
					//App.debug(""+data.name+" -- "+data.handId);
					setGesture(data.handId, data.name);
				}
			};
			handConfig.SubscribeGesture(handler);
			
			AlertHandler alertHandler = new AlertHandler() {
				
				@Override
				public void OnFiredAlert(intel.rssdk.PXCMHandData.AlertData data) {
					//App.debug("alert : "+data.handId+", "+data.label.name());
					setAlert(data.handId, data.label);
				}
			};
			handConfig.SubscribeAlert(alertHandler);
			
			handConfig.ApplyChanges();
			handConfig.Update();
			
			handData = handModule.CreateOutput();
			
			
			connected = true;
		}

		App.debug("connected to RealSense: "+connected);
		
		
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
//			PXCMPointF32 image = hand.QueryMassCenterImage();
			PXCMPoint3DF32 world = hand.QueryMassCenterWorld();
			PXCMPoint4DF32 palmOrientation = hand.QueryPalmOrientation();

			/*
			System.out.println("Palm Center : ");
			System.out.print("   Image Position: (" + image.x + "," +image.y + ")");
			System.out.println("   World Position: (" + world.x + "," + world.y + "," + world.z + ")");
			*/
		
			/*
			birdX = -world.x * SCREEN_REAL_DIM_FACTOR;
			birdY = world.y * SCREEN_REAL_DIM_FACTOR;
			birdZ = (world.z-0.3) * SCREEN_REAL_DIM_FACTOR;
			*/
			
			
			
			dataSampler.addData(
					//image.x, image.y, 
					world.x, world.y, world.z,
					palmOrientation.x, palmOrientation.y, palmOrientation.z, palmOrientation.w);
			
			
			/*
			hand2Dx = dataAverage.getImageX();
			hand2Dy = dataAverage.getImageY();
			hand2Dfactor = getScaleFactor(dataAverage.getImageScale());
			*/
			
			birdX = dataSampler.getWorldX();
			birdY = dataSampler.getWorldY();
			birdZ = dataSampler.getWorldZ();
			
			
			
			birdOrientationX = dataSampler.getHandOrientationX();
			birdOrientationY = dataSampler.getHandOrientationY();
			birdOrientationZ = dataSampler.getHandOrientationZ();
			birdOrientationW = dataSampler.getHandOrientationW();
			
			
			/*
			switch(gesture){
			case PINCH:
				smallButton = 1;
				bigButton = 0;
				break;
			case FIST:
				smallButton = 0;
				bigButton = 1;
				break;
			default:
				smallButton = 0;
				bigButton = 0;
				break;
			}
			*/

			gotMessage = true;
			
		}else{
			gotMessage = false;
		}

		/*
		// alerts
		int nalerts = handData.QueryFiredAlertsNumber();
		//System.out.println("# of alerts is " + nalerts);

		// gestures
		int ngestures = handData.QueryFiredGesturesNumber();
		//System.out.println("# of gestures at frame is " + ngestures);
		 
		 */

		senseMgr.ReleaseFrame();

		


		return true;
	}



	/**
	 * 
	 * @return true if a hand is tracked
	 */
	public boolean hasTrackedHand() {
		return handId >= 0;
	}



	public void setLeftButtonPressed(boolean flag) {
		leftButton = flag;	
		App.debug("\nleftButton = "+leftButton);
	}


	
	/*
	private float getScaleFactor(float z){
		App.debug(""+z);
		// z should be between 0.2 and 0.6
		// z = 0.5 >> far
		// z = 0.3 >> near
		
		float z1 = (z - 0.3f)/0.2f;
		
		return 0.5f + z1 * 2.5f;
	}
	*/


}

