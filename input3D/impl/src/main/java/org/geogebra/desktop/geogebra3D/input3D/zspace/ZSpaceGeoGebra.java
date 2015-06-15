package org.geogebra.desktop.geogebra3D.input3D.zspace;
import java.text.DecimalFormat;

import com.zspace.Sdk3;
import com.zspace.ZSCoordinateSpace;
import com.zspace.ZSDisplayType;
import com.zspace.ZSEventListener;
import com.zspace.ZSEventThresholds;
import com.zspace.ZSFloatWH;
import com.zspace.ZSIntWH;
import com.zspace.ZSIntXY;
import com.zspace.ZSMatrix4;
import com.zspace.ZSTargetType;
import com.zspace.ZSTrackerEventData;
import com.zspace.ZSTrackerEventType;


public class ZSpaceGeoGebra {
	
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
	public static void RunOnce() 
	{
		try {
			System.loadLibrary("Sdk3");
		} catch (UnsatisfiedLinkError e) {
		  System.err.println("zspace library failed to load" + e.getMessage());
		  System.exit(1);
		}
	}
	
	
	
	private abstract class ZJEventListener extends ZSEventListener{
		
		public ZSMatrix4 matrix, viewPortMatrix;
		
		protected ZSpaceGeoGebra zsggb;
		

		public ZJEventListener(ZSpaceGeoGebra zsggb)
		{
			this.zsggb = zsggb;
		}
		public void runWithEventData(final ZSTrackerEventData eventData){
			
			zsggb.setEventOccured();
			
			matrix = eventData.getPoseMatrix();
			updateViewPortMatrix();
			
		}
		
		/**
		 * update matrix in viewport space
		 * 
		 */
		public void updateViewPortMatrix(){
			
			if (matrix == null){
				return;
			}
			
			viewPortMatrix = Sdk3.ZSTransformMatrix(
					zsggb.zViewport,
					matrix,
					ZSCoordinateSpace.ZS_COORDINATE_SPACE_TRACKER,
					ZSCoordinateSpace.ZS_COORDINATE_SPACE_VIEWPORT);
			
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
		
		public void runWithEventData(final ZSTrackerEventData eventData) {

			zsggb.setStylusDetected();
			super.runWithEventData(eventData);
		}

		protected void updateCoords(){
			// update x, y, z
			x = viewPortMatrix.getM03() * zsggb.toPixelRatio;
			y = viewPortMatrix.getM13() * zsggb.toPixelRatio;
			z = viewPortMatrix.getM23() * zsggb.toPixelRatio;
			
			// update direction x, y, z
			dx = viewPortMatrix.getM02();
			dy = viewPortMatrix.getM12();
			dz = viewPortMatrix.getM22();
			
			// update quaternion
			// (from http://www.euclideanspace.com/maths/geometry/rotations/conversions/matrixToQuaternion)
			double m00 = viewPortMatrix.getM00();
			double m10 = viewPortMatrix.getM10();
			double m20 = viewPortMatrix.getM20();
			double m01 = viewPortMatrix.getM01();
			double m11 = viewPortMatrix.getM11();
			double m21 = viewPortMatrix.getM21();
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
		
	}
	
	private class ZJEventListenerHead extends ZJEventListener{
		
		private double leftX, leftY, leftZ, rightX, rightY, rightZ;
		
		

		public ZJEventListenerHead(ZSpaceGeoGebra zsggb) {
			super(zsggb);
		}

		public void runWithEventData(final ZSTrackerEventData eventData) {

			zsggb.setGlassesDetected();
			super.runWithEventData(eventData);
		}
		
		protected void updateCoords(){
			
			// update eyes
			double x = viewPortMatrix.getM03();
			double y = viewPortMatrix.getM13();
			double z = viewPortMatrix.getM23();
			double dx = ZSpaceGeoGebra.EYE_SEP_HALF * viewPortMatrix.getM00();
			double dy = ZSpaceGeoGebra.EYE_SEP_HALF * viewPortMatrix.getM10();
			double dz = ZSpaceGeoGebra.EYE_SEP_HALF * viewPortMatrix.getM20();
			
			leftX = (x - dx) * zsggb.toPixelRatio;
			leftY = (y - dy) * zsggb.toPixelRatio;
			leftZ = (z - dz) * zsggb.toPixelRatio;
			
			rightX = (x + dx) * zsggb.toPixelRatio;
			rightY = (y + dy) * zsggb.toPixelRatio;
			rightZ = (z + dz) * zsggb.toPixelRatio;

		}
		
	}
	
	
	private class ZJEventListenerPressButtons extends ZSEventListener{
		
		protected ZSpaceGeoGebra zsggb;
		
		public ZJEventListenerPressButtons(ZSpaceGeoGebra zsggb){
			this.zsggb = zsggb;
		}
		
		public void runWithEventData(final ZSTrackerEventData eventData){
			
			zsggb.setEventOccured();
			zsggb.button[eventData.getButtonId()] = true;
		}
		
		
		
	}
	
	
	private class ZJEventListenerReleaseButtons extends ZSEventListener{
		
		protected ZSpaceGeoGebra zsggb;

		public ZJEventListenerReleaseButtons(ZSpaceGeoGebra zsggb){
			this.zsggb = zsggb;
		}
		
		public void runWithEventData(final ZSTrackerEventData eventData){
			
			zsggb.setEventOccured();
			zsggb.button[eventData.getButtonId()] = false;
			
		}
		
		
		
	}
	
	static private DecimalFormat format = new DecimalFormat(" 0.00;-0.00");
	
	public static String matrixToString(ZSMatrix4 m){
		
		if (m == null){
			return "m == null";
		}
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(format.format(m.getM00()));
		sb.append(" ");
		sb.append(format.format(m.getM01()));
		sb.append(" ");
		sb.append(format.format(m.getM02()));
		sb.append(" ");
		sb.append(format.format(m.getM03()));
		sb.append("\n");
		
		sb.append(format.format(m.getM10()));
		sb.append(" ");
		sb.append(format.format(m.getM11()));
		sb.append(" ");
		sb.append(format.format(m.getM12()));
		sb.append(" ");
		sb.append(format.format(m.getM13()));
		sb.append("\n");
		
		sb.append(format.format(m.getM20()));
		sb.append(" ");
		sb.append(format.format(m.getM21()));
		sb.append(" ");
		sb.append(format.format(m.getM22()));
		sb.append(" ");
		sb.append(format.format(m.getM23()));
		sb.append("\n");
		
		sb.append(format.format(m.getM30()));
		sb.append(" ");
		sb.append(format.format(m.getM31()));
		sb.append(" ");
		sb.append(format.format(m.getM32()));
		sb.append(" ");
		sb.append(format.format(m.getM33()));
		
		
		return sb.toString();
	}
	
	
	
	
	private long zContext, zViewport, zHead, zStylus;
	private long zBuffer;
	
	private boolean[] button;
	
	private ZSMatrix4 origMatrix, viewPortMatrixFromOrigin ;
	
    public ZSpaceGeoGebra() {

		init();
		
	    float[] unitFloat = {	1.0f,0.0f,0.0f,0.0f,
				0.0f,1.0f,0.0f,0.0f,
				0.0f,0.0f,1.0f,0.0f,
				0.0f,0.0f,0.0f,1.0f};
		origMatrix = new ZSMatrix4();
		origMatrix.setF(unitFloat);
		

    }
    
    private double toPixelRatio = 3600;
    
    
    private void init(){
    	Sdk3.ZSPrintErrorsOn();
		
    	
    	// buttons
    	button = new boolean[3];
    	
		// initialize context 		
		zContext = Sdk3.ZSInitialize();
		
		// get display size
		int numDisplays = Sdk3.ZSGetNumDisplays(zContext);
		// System.out.println("============= numDisplays = "+numDisplays);
		for (int i = 0 ; i < numDisplays ; i++){
			long handle = Sdk3.ZSFindDisplayByIndex(zContext, i);
			ZSDisplayType type = Sdk3.ZSGetDisplayType(handle);
			
			if (type.equals(ZSDisplayType.ZS_DISPLAY_TYPE_ZSPACE)){
				ZSFloatWH displaySize = Sdk3.ZSGetDisplaySize(handle);
				ZSIntXY resolution = Sdk3.ZSGetDisplayNativeResolution(handle);
				toPixelRatio = resolution.getX() / displaySize.getW();
				//				System.out.println("============= display width = "+displaySize.getW());
				//				System.out.println("============= display resolution (x) = "+resolution.getX());
				//				System.out.println("============= ratio = "+toPixelRatio);
				System.out.println("============= monitor = "+Sdk3.ZSGetDisplayMonitorIndex(handle));
			}			
			
		}
		
		
		zBuffer = Sdk3.ZSCreateStereoBufferGL(zContext);
		zViewport = Sdk3.ZSCreateViewport(zContext);
//		long zFrustum = Sdk3.ZSFindFrustum(zViewport);
		
		// initialize head tracking
		zHead = Sdk3.ZSFindTargetByType(
				zContext,
				ZSTargetType.ZS_TARGET_TYPE_HEAD,
				0);
		ZSEventThresholds th = Sdk3.ZSGetTargetMoveEventThresholds(zHead);
		th.setDistance(TRACKER_THRESHOLD_DISTANCE);
		th.setAngle(TRACKER_THRESHOLD_ANGLE);
		th.setTime(TRACKER_THRESHOLD_TIME);
		Sdk3.ZSSetTargetMoveEventThresholds(zHead, th);

		// initialize stylus
		zStylus = Sdk3.ZSFindTargetByType(
				zContext,
				ZSTargetType.ZS_TARGET_TYPE_PRIMARY,
				0);		
		th = Sdk3.ZSGetTargetMoveEventThresholds(zStylus);
		th.setDistance(TRACKER_THRESHOLD_DISTANCE);
		th.setAngle(TRACKER_THRESHOLD_ANGLE);
		th.setTime(TRACKER_THRESHOLD_TIME);
		Sdk3.ZSSetTargetMoveEventThresholds(zStylus, th);
		
		
		
		System.out.println("========= head distance threshold: "+Sdk3.ZSGetTargetMoveEventThresholds(zHead).getDistance());
		System.out.println("========= head angle threshold: "+Sdk3.ZSGetTargetMoveEventThresholds(zHead).getAngle());
		System.out.println("========= head time threshold: "+Sdk3.ZSGetTargetMoveEventThresholds(zHead).getTime());
		System.out.println("========= stylus distance threshold: "+Sdk3.ZSGetTargetMoveEventThresholds(zStylus).getDistance());

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
    	Sdk3.ZSAddTrackerEventHandler(
				zHead, 
				ZSTrackerEventType.ZS_TRACKER_EVENT_MOVE, 
				headlistener);
    	
    	//add listener for stylus
    	styluslistener = new ZJEventListenerStylus(this);
    	Sdk3.ZSAddTrackerEventHandler(
				zStylus, 
				ZSTrackerEventType.ZS_TRACKER_EVENT_MOVE, 
				styluslistener);
    	
		// add listeners for buttons
    	buttonspresslistener = new ZJEventListenerPressButtons(this);
		Sdk3.ZSAddTrackerEventHandler(
				zStylus, 
				ZSTrackerEventType.ZS_TRACKER_EVENT_BUTTON_PRESS, 
				buttonspresslistener);
    	buttonsreleaselistener = new ZJEventListenerReleaseButtons(this);
		Sdk3.ZSAddTrackerEventHandler(
				zStylus, 
				ZSTrackerEventType.ZS_TRACKER_EVENT_BUTTON_RELEASE, 
				buttonsreleaselistener);

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
    
    private ZSIntXY viewPortPosition = new ZSIntXY();
    private ZSIntWH viewPortSize  = new ZSIntWH();
    

    
    public void setViewPort(int x, int y, int w, int h){
    	
    	Sdk3.ZSUpdate(zContext);
    	Sdk3.ZSSyncStereoBuffer(zBuffer);
    	
    	viewPortPosition.setX(x); viewPortPosition.setY(y);
		Sdk3.ZSSetViewportPosition(zViewport, viewPortPosition);

		viewPortSize.setW(w); viewPortSize.setH(h);
		Sdk3.ZSSetViewportSize(zViewport, viewPortSize);
		
		
		viewPortMatrixFromOrigin = Sdk3.ZSTransformMatrix(
				zViewport,
				origMatrix,
				ZSCoordinateSpace.ZS_COORDINATE_SPACE_TRACKER,
				ZSCoordinateSpace.ZS_COORDINATE_SPACE_VIEWPORT);
		
		headlistener.updateViewPortMatrix();
		styluslistener.updateViewPortMatrix();
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
		return System.currentTimeMillis() < lastGlassesDetection
				+ TRACKER_NOT_DETECTED_MAX_DELAY;
	}

	/**
	 * 
	 * @return true if glasses are detected
	 */
	public boolean stylusDetected() {
		return System.currentTimeMillis() < lastStylusDetection
				+ TRACKER_NOT_DETECTED_MAX_DELAY;
	}

}
