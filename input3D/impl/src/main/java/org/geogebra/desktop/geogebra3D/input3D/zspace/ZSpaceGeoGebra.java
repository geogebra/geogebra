package org.geogebra.desktop.geogebra3D.input3D.zspace;
import java.text.DecimalFormat;

import org.geogebra.desktop.geogebra3D.input3D.Input3DFactory.Input3DException;
import org.geogebra.desktop.geogebra3D.input3D.Input3DFactory.Input3DExceptionType;

import com.zspace.Sdk4;
import com.zspace.Sdk4.ZCCoordinateSpace;
import com.zspace.Sdk4.ZCDisplayType;
import com.zspace.Sdk4.ZCError;
import com.zspace.Sdk4.ZCTargetType;
import com.zspace.ZCEventListener;
import com.zspace.ZCTrackerEventData;
import com.zspace.ZCTrackerPose;
import com.zspace.ZSMatrix4;
import com.zspace.ZSVector2;
import com.zspace.ZSVector3;




public class ZSpaceGeoGebra {
	
	public class ZSMatrix4Ggb extends ZSMatrix4 {

		public void set(float[] values) {
			for (int i = 0; i < 16; i++) {
				f[i] = values[i];
			}
		}

		public float getM(int i, int j) {
			return f[i + j * 4];
		}
	}

	public static double EYE_SEP_HALF = 0.035;
	public static float TRACKER_THRESHOLD_DISTANCE = 0.0001f;
	public static float TRACKER_THRESHOLD_ANGLE = 1f;
	public static float TRACKER_THRESHOLD_TIME = 0.02f;
	
	/**
	 * max delay (in ms) during which the tracker may be not detected but
	 * present
	 */
	private static int TRACKER_NOT_DETECTED_MAX_DELAY = 3000;

//	@BeforeClass
	public static void RunOnce() throws Input3DException {
		try {
			System.loadLibrary("ZSpaceSDK4");
		} catch (UnsatisfiedLinkError e) {
			throw new Input3DException(Input3DExceptionType.INSTALL,
					"zSpace: Failed to load library");
		}
	}
	
	/**
	 * try to initialize zSpace context
	 * 
	 * @throws Input3DException
	 *             exception
	 */
	public static void Initialize() throws Input3DException {
		// initialize context
		zContext = Sdk4.zcInitialize();
		if (Sdk4.zcGetError() != ZCError.ZC_ERROR_OK) {
			throw new Input3DException(Input3DExceptionType.RUN,
					"zSpace: Failed to init");
		}
	}
	
	
	private abstract class ZJEventListener extends ZCEventListener {
		
		public ZSMatrix4Ggb viewPortMatrix;
		
		protected ZSpaceGeoGebra zsggb;
		private ZCTrackerPose trackerPose;
		
		public ZJEventListener(ZSpaceGeoGebra zsggb) {
			this.zsggb = zsggb;
			viewPortMatrix = new ZSMatrix4Ggb();
			trackerPose = new ZCTrackerPose();
		}

		@Override
		public void runWithEventData(long targetHandle,
				ZCTrackerEventData eventData, Object userData) {
			
			zsggb.setEventOccured();
			setTrackerPose(trackerPose);
			updateViewPortMatrix();
			
		}
		
		abstract protected void setTrackerPose(ZCTrackerPose trackerPose);

		/**
		 * update matrix in viewport space
		 * 
		 */
		public void updateViewPortMatrix(){

			viewPortMatrix.set(trackerPose.matrix.f);
			Sdk4.zcTransformMatrix(
					zsggb.zViewport,
					ZCCoordinateSpace.ZC_COORDINATE_SPACE_TRACKER,
					ZCCoordinateSpace.ZC_COORDINATE_SPACE_VIEWPORT,
					viewPortMatrix);
			
			updateCoords();
		}
		
		/**
		 * update coords
		 */
		abstract protected void updateCoords();
		
		
	}
	
	private class ZJEventListenerStylus extends ZJEventListener{
		

		public double x, y, z, qx, qy, qz, qw, dx, dy, dz;

		public ZJEventListenerStylus(ZSpaceGeoGebra zsggb) {
			super(zsggb);
		}
		
		@Override
		public void runWithEventData(long targetHandle,
				ZCTrackerEventData eventData, Object userData) {

			zsggb.setStylusDetected();
			super.runWithEventData(targetHandle, eventData, userData);
		}

		@Override
		protected void updateCoords(){
			// update x, y, z
			x = viewPortMatrix.f[12] * zsggb.toPixelRatio;
			y = viewPortMatrix.f[13] * zsggb.toPixelRatio;
			z = viewPortMatrix.f[14] * zsggb.toPixelRatio;
			
			// update direction x, y, z
			dx = viewPortMatrix.f[8];
			dy = viewPortMatrix.f[9];
			dz = viewPortMatrix.f[10];
			
			// update quaternion
			// (from http://www.euclideanspace.com/maths/geometry/rotations/conversions/matrixToQuaternion)
			double m00 = viewPortMatrix.f[0];
			double m10 = viewPortMatrix.f[1];
			double m20 = viewPortMatrix.f[2];
			double m01 = viewPortMatrix.f[4];
			double m11 = viewPortMatrix.f[5];
			double m21 = viewPortMatrix.f[6];
			double m02 = dx;
			double m12 = dy;
			double m22 = dz;
			
			double tr = m00 + m11 + m22;

			if (tr > 0) { 
				double S = Math.sqrt(tr+1.0) * 2; // S=4*qw 
				qw = 0.25 * S;
				qx = (m21 - m12) / S;
				qy = (m02 - m20) / S; 
				qz = (m10 - m01) / S; 
			} else if ((m00 > m11)&(m00 > m22)) { 
				double S = Math.sqrt(1.0 + m00 - m11 - m22) * 2; // S=4*qx 
				qw = (m21 - m12) / S;
				qx = 0.25 * S;
				qy = (m01 + m10) / S; 
				qz = (m02 + m20) / S; 
			} else if (m11 > m22) { 
				double S = Math.sqrt(1.0 + m11 - m00 - m22) * 2; // S=4*qy
				qw = (m02 - m20) / S;
				qx = (m01 + m10) / S; 
				qy = 0.25 * S;
				qz = (m12 + m21) / S; 
			} else { 
				double S = Math.sqrt(1.0 + m22 - m00 - m11) * 2; // S=4*qz
				qw = (m10 - m01) / S;
				qx = (m02 + m20) / S;
				qy = (m12 + m21) / S;
				qz = 0.25 * S;
			}
		}

		@Override
		protected void setTrackerPose(ZCTrackerPose trackerPose) {
			Sdk4.zcGetTargetPose(targetHandle, trackerPose);
		}
		
	}
	
	private class ZJEventListenerHead extends ZJEventListener{
		
		private double leftX, leftY, leftZ, rightX, rightY, rightZ;
		
		

		public ZJEventListenerHead(ZSpaceGeoGebra zsggb) {
			super(zsggb);
		}

		@Override
		public void runWithEventData(long targetHandle,
				ZCTrackerEventData eventData, Object userData) {

			zsggb.setGlassesDetected();
			super.runWithEventData(targetHandle, eventData, userData);
		}
		
		@Override
		protected void updateCoords(){
			
			// update eyes
			double x = viewPortMatrix.f[12];
			double y = viewPortMatrix.f[13];
			double z = viewPortMatrix.f[14];
			double dx = ZSpaceGeoGebra.EYE_SEP_HALF * viewPortMatrix.f[0];
			double dy = ZSpaceGeoGebra.EYE_SEP_HALF * viewPortMatrix.f[1];
			double dz = ZSpaceGeoGebra.EYE_SEP_HALF * viewPortMatrix.f[2];
			
			leftX = (x - dx) * zsggb.toPixelRatio;
			leftY = (y - dy) * zsggb.toPixelRatio;
			leftZ = (z - dz) * zsggb.toPixelRatio;
			
			rightX = (x + dx) * zsggb.toPixelRatio;
			rightY = (y + dy) * zsggb.toPixelRatio;
			rightZ = (z + dz) * zsggb.toPixelRatio;

		}

		@Override
		protected void setTrackerPose(ZCTrackerPose trackerPose) {
			Sdk4.zcGetFrustumHeadPose(frustumHandle, trackerPose);
		}
		
	}
	
	
	private class ZJEventListenerPressButtons extends ZCEventListener {
		
		protected ZSpaceGeoGebra zsggb;
		
		public ZJEventListenerPressButtons(ZSpaceGeoGebra zsggb){
			this.zsggb = zsggb;
		}
		
		@Override
		public void runWithEventData(long targetHandle,
				ZCTrackerEventData eventData, Object userData) {
			
			zsggb.setEventOccured();
			zsggb.button[eventData.buttonId] = true;
		}
		
		
		
	}
	
	
	private class ZJEventListenerReleaseButtons extends ZCEventListener {
		
		protected ZSpaceGeoGebra zsggb;

		public ZJEventListenerReleaseButtons(ZSpaceGeoGebra zsggb){
			this.zsggb = zsggb;
		}
		
		@Override
		public void runWithEventData(long targetHandle,
				ZCTrackerEventData eventData, Object userData) {
			
			zsggb.setEventOccured();
			zsggb.button[eventData.buttonId] = false;
			
		}
		
		
		
	}
	
	static private DecimalFormat format = new DecimalFormat(" 0.00;-0.00");
	
	public static String matrixToString(ZSMatrix4Ggb m) {
		
		if (m == null){
			return "m == null";
		}
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(format.format(m.getM(0, 0)));
		sb.append(" ");
		sb.append(format.format(m.getM(0, 1)));
		sb.append(" ");
		sb.append(format.format(m.getM(0, 2)));
		sb.append(" ");
		sb.append(format.format(m.getM(0, 3)));
		sb.append("\n");
		
		sb.append(format.format(m.getM(1, 0)));
		sb.append(" ");
		sb.append(format.format(m.getM(1, 1)));
		sb.append(" ");
		sb.append(format.format(m.getM(1, 2)));
		sb.append(" ");
		sb.append(format.format(m.getM(1, 3)));
		sb.append("\n");
		
		sb.append(format.format(m.getM(2, 0)));
		sb.append(" ");
		sb.append(format.format(m.getM(2, 1)));
		sb.append(" ");
		sb.append(format.format(m.getM(2, 2)));
		sb.append(" ");
		sb.append(format.format(m.getM(2, 3)));
		sb.append("\n");
		
		sb.append(format.format(m.getM(3, 0)));
		sb.append(" ");
		sb.append(format.format(m.getM(3, 1)));
		sb.append(" ");
		sb.append(format.format(m.getM(3, 2)));
		sb.append(" ");
		sb.append(format.format(m.getM(3, 3)));
		
		
		return sb.toString();
	}
	
	
	
	static private long zContext;
	private long zViewport, zHead, zStylus;
	private long zBuffer;
	
	private boolean[] button;
	
	private ZSMatrix4Ggb origMatrix, viewPortMatrixFromOrigin;
	
    public ZSpaceGeoGebra() {

		init();
		
	    float[] unitFloat = {	1.0f,0.0f,0.0f,0.0f,
				0.0f,1.0f,0.0f,0.0f,
				0.0f,0.0f,1.0f,0.0f,
				0.0f,0.0f,0.0f,1.0f};
		origMatrix = new ZSMatrix4Ggb();
		origMatrix.set(unitFloat);
		
		viewPortMatrixFromOrigin = new ZSMatrix4Ggb();

    }
    
	double toPixelRatio = 3600;
    
	private long displayHandle;
	long frustumHandle;
    
    private void init(){
    	
    	// buttons
    	button = new boolean[3];
		
		// get display size
		int numDisplays = Sdk4.zcGetNumDisplays(zContext);
		// System.out.println("============= numDisplays = "+numDisplays);
		for (int i = 0 ; i < numDisplays ; i++){
			displayHandle = Sdk4.zcGetDisplayByIndex(zContext, i);
			ZCDisplayType type = Sdk4.zcGetDisplayType(displayHandle);
			
			if (type.equals(ZCDisplayType.ZC_DISPLAY_TYPE_ZSPACE)) {
				ZSVector2 displaySize = new ZSVector2();
				Sdk4.zcGetDisplaySize(displayHandle, displaySize);
				ZSVector2 resolution = new ZSVector2();
				Sdk4.zcGetDisplayNativeResolution(displayHandle, resolution);
				toPixelRatio = resolution.x / displaySize.x;
				System.out.println(
						"============= display width = " + displaySize.x);
				System.out.println("============= display resolution (x) = "
						+ resolution.x);
				System.out.println("============= ratio = " + toPixelRatio);
				System.out.println("============= monitor = "
						+ Sdk4.zcGetDisplayMonitorIndex(displayHandle));
			}			
			
		}
		
		zBuffer = Sdk4.zcCreateStereoBuffer(zContext,
				Sdk4.ZCRenderer.ZC_RENDERER_QUAD_BUFFER_GL, 0);
		zViewport = Sdk4.zcCreateViewport(zContext);
		
		frustumHandle = Sdk4.zcGetFrustum(zViewport);

		// initialize head tracking
		zHead = Sdk4.zcGetTargetByType(
				zContext,
				ZCTargetType.ZC_TARGET_TYPE_HEAD,
				0);
		Sdk4.zcSetTargetMoveEventThresholds(zHead, TRACKER_THRESHOLD_TIME,
				TRACKER_THRESHOLD_DISTANCE, TRACKER_THRESHOLD_ANGLE);

		// initialize stylus
		zStylus = Sdk4.zcGetTargetByType(
				zContext,
				ZCTargetType.ZC_TARGET_TYPE_PRIMARY,
				0);		
		Sdk4.zcSetTargetMoveEventThresholds(zStylus, TRACKER_THRESHOLD_TIME,
				TRACKER_THRESHOLD_DISTANCE, TRACKER_THRESHOLD_ANGLE);
		
		
		// ZSVector3 th = new ZSVector3();
		// Sdk4.zcGetTargetMoveEventThresholds(zHead, th);
		// System.out.println("========= head distance threshold: " + th.y);
		// System.out.println("========= head angle threshold: " + th.z);
		// System.out.println("========= head time threshold: " + th.x);
		// Sdk4.zcGetTargetMoveEventThresholds(zStylus, th);
		// System.out.println("========= stylus distance threshold: " + th.y);

		// create callbacks
		createCallbacks();
    }

    private ZJEventListenerHead headlistener;
    private ZJEventListenerStylus styluslistener;
    private ZJEventListenerPressButtons buttonspresslistener;
    private ZJEventListenerReleaseButtons buttonsreleaselistener;
       
    private void createCallbacks(){
    	
    	//add listener for head
    	headlistener = new ZJEventListenerHead(this);
		headlistener.targetHandle = zHead;
		headlistener.trackerEventType = Sdk4.ZCTrackerEventType.ZC_TRACKER_EVENT_MOVE;
		headlistener.userData = null;
		Sdk4.zcAddTrackerEventHandler(headlistener);
    	
    	//add listener for stylus
    	styluslistener = new ZJEventListenerStylus(this);
		styluslistener.targetHandle = zStylus;
		styluslistener.trackerEventType = Sdk4.ZCTrackerEventType.ZC_TRACKER_EVENT_MOVE;
		styluslistener.userData = null;
		Sdk4.zcAddTrackerEventHandler(styluslistener);
    	
		// add listeners for buttons
    	buttonspresslistener = new ZJEventListenerPressButtons(this);
		buttonspresslistener.targetHandle = zStylus;
		buttonspresslistener.trackerEventType = Sdk4.ZCTrackerEventType.ZC_TRACKER_EVENT_BUTTON_PRESS;
		buttonspresslistener.userData = null;
		Sdk4.zcAddTrackerEventHandler(buttonspresslistener);

    	buttonsreleaselistener = new ZJEventListenerReleaseButtons(this);
		buttonsreleaselistener.targetHandle = zStylus;
		buttonsreleaselistener.trackerEventType = Sdk4.ZCTrackerEventType.ZC_TRACKER_EVENT_BUTTON_RELEASE;
		buttonsreleaselistener.userData = null;
		Sdk4.zcAddTrackerEventHandler(buttonsreleaselistener);
    }
    
    
    
    
    private boolean eventOccured = false;
    
	public void setGlassesDetected() {
		lastGlassesDetection = System.currentTimeMillis();
	}

	public void setStylusDetected() {
		lastStylusDetection = System.currentTimeMillis();
	}

    public void setEventOccured(){
    	eventOccured = true;
    }
    
    public boolean eventOccured(){
    	return eventOccured;
    }
    
    public void getData(){
    	
    	eventOccured = false;
    	//return "\nhead:\n"+matrixToString(headlistener.matrix)+"\nin viewport:\n"+matrixToString(headlistener.viewPortMatrix);
    	//return button[0]+","+button[1]+","+button[2];
    }
    
    /**
     * 
     * @param i i-th button
     * @return state of the i-th button
     */
    public boolean getButton(int i){
    	return button[i];
    }

    public void setViewPort(int x, int y, int w, int h){
    	
		Sdk4.zcUpdate(zContext);
		Sdk4.zcSyncStereoBuffer(zBuffer);
    	
		Sdk4.zcSetViewportPosition(zViewport, x, y);
		Sdk4.zcSetViewportSize(zViewport, w, h);
		
		
		viewPortMatrixFromOrigin.set(origMatrix.f);

		Sdk4.zcTransformMatrix(
				zViewport,
				ZCCoordinateSpace.ZC_COORDINATE_SPACE_TRACKER,
				ZCCoordinateSpace.ZC_COORDINATE_SPACE_VIEWPORT,
				viewPortMatrixFromOrigin);
		
		headlistener.updateViewPortMatrix();
		styluslistener.updateViewPortMatrix();
    }
    
	public void update() {
		Sdk4.zcUpdate(zContext);
	}

    public String getViewPortMatrix(){
    	return matrixToString(viewPortMatrixFromOrigin);
    }
    
    
	/**
	 * @return last calculated x coord
	 */
	public double getStylusX(){
		return styluslistener.x;
	}
	
	/**
	 * @return last calculated y coord
	 */
	public double getStylusY(){
		return styluslistener.y;
	}
	
	/**
	 * @return last calculated z coord
	 */
	public double getStylusZ(){
		return styluslistener.z;
	}
	
	/**
	 * @return last calculated direction x coord
	 */
	public double getStylusDX(){
		return -styluslistener.dx;
	}
	
	/**
	 * @return last calculated direction y coord
	 */
	public double getStylusDY(){
		return -styluslistener.dy;
	}
	
	/**
	 * @return last calculated direction z coord
	 */
	public double getStylusDZ(){
		return -styluslistener.dz;
	}
	
	/**
	 * @return last calculated quaternion x coord
	 */
	public double getStylusQX(){
		return styluslistener.qx;
	}
	
	/**
	 * @return last calculated quaternion y coord
	 */
	public double getStylusQY(){
		return styluslistener.qy;
	}
	
	/**
	 * @return last calculated quaternion z coord
	 */
	public double getStylusQZ(){
		return styluslistener.qz;
	}
	
	/**
	 * @return last calculated quaternion w coord
	 */
	public double getStylusQW(){
		return styluslistener.qw;
	}
	
	
	
	public String getStylusMatrix(){
		return matrixToString(styluslistener.viewPortMatrix);
	}
	
	/**
	 * @return last calculated left eye x coord
	 */
	public double getLeftEyeX(){
		return headlistener.leftX;
	}
	
	/**
	 * @return last calculated left eye y coord
	 */
	public double getLeftEyeY(){
		return headlistener.leftY;
	}
	
	/**
	 * @return last calculated left eye z coord
	 */
	public double getLeftEyeZ(){
		return headlistener.leftZ;
	}

	/**
	 * @return last calculated right eye x coord
	 */
	public double getRightEyeX(){
		return headlistener.rightX;
	}
	
	/**
	 * @return last calculated right eye y coord
	 */
	public double getRightEyeY(){
		return headlistener.rightY;
	}
	
	/**
	 * @return last calculated right eye z coord
	 */
	public double getRightEyeZ(){
		return headlistener.rightZ;
	}

	/**
	 * 
	 * @return real world dim to pixel dim ratio
	 */
	public double toPixelRatio(){
		return toPixelRatio;
	}

	private long lastGlassesDetection = 0;

	private long lastStylusDetection = 0;

	/**
	 * 
	 * @return true if it wants stereo
	 */
	public boolean wantsStereo() {
		return lastGlassesDetection > 0;
		// return System.currentTimeMillis() < lastGlassesDetection
		// + TRACKER_NOT_DETECTED_MAX_DELAY;
	}

	/**
	 * 
	 * @return true if glasses are detected
	 */
	public boolean stylusDetected() {
		return System.currentTimeMillis() < lastStylusDetection
				+ TRACKER_NOT_DETECTED_MAX_DELAY;
	}

	/**
	 * 
	 * @return display angle with ground
	 */
	public double getDisplayAngle() {
		try{
			ZSVector3 angle = new ZSVector3();
			Sdk4.zcGetDisplayAngle(displayHandle, angle);
			float angleX = angle.x;
			if (angleX > 80f) {
				angleX = 45f;
			}
			return angleX;
		} catch (Throwable e) {
			System.out.println(
					"ZSpace, problem getting display angle: " + e.getMessage());
		}
		return 60; // default angle for zStation 100
	}

}
